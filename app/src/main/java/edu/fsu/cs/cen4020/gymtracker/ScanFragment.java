package edu.fsu.cs.cen4020.gymtracker;

/*
  3/3/2019 By Ben K. and Javier S.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;

public class ScanFragment extends Fragment {

    final private static String USED = "used";
    private final static String CURR_USER = "currentUser";
    // Arbitrary number
    public static final int REQ_CODE_SECOND_FRAGMENT = 342423;
    ToggleButton tbUsing;
    Button bScan;
    TextView tvEquipmentID;
    public static final String TAG = ScanFragment.class.getCanonicalName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String FirebaseUid;
    private boolean userFlag;
    private String QR_CODE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        tbUsing = view.findViewById(R.id.tb_Using);
        bScan = view.findViewById(R.id.b_Scan);
        tvEquipmentID = view.findViewById(R.id.tv_ScanID);
        FirebaseUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // If we opened the fragment form the CodeScanFragment
        Bundle argsFromCodeScanFragment = getArguments();
        if (argsFromCodeScanFragment != null) {
            QR_CODE = argsFromCodeScanFragment.getString(CodeScanFragment.INTENT_QR_CODE_KEY);
            if (validQRCODE(QR_CODE)) tvEquipmentID.setText(QR_CODE);
        }



        // Use Dexter to get permissions - As of API >=23 permissions should be dynamically requested as they are needed.
        // Since this fragment will be in charge of scanning, request camera permissions
        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getContext())
                        // Do not make these strings into resources, doesn't work for some reason
                        .withTitle("Camera permission")
                        .withMessage("Camera permission is needed for scanning QR codes")
                        .withButtonText(android.R.string.ok)
                        .build();


        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(dialogPermissionListener).check();


        // Create a reference to our DB and update the ui
        if (validQRCODE(QR_CODE)) {

            final DocumentReference docRef = db.collection("gyms").document("anchor").collection("equipment").document(QR_CODE);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        updateView(snapshot.getData());
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });

        }

        tbUsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton(tbUsing.isChecked());
            }
        });

        bScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.codeScanFragment);
            }
        });

        return view;
    }

    private boolean validQRCODE(String qr_code) {
        if (qr_code == null)
            return false;
        return qr_code.contains("|");
    }


    /**
     * The callback from the CodeScanFragment
     * @param intent Contains the QR code contents
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ScanFragment.REQ_CODE_SECOND_FRAGMENT) {
                String secondFragmentData = intent.getStringExtra(CodeScanFragment.INTENT_KEY_SECOND_FRAGMENT_DATA);
                tvEquipmentID.setText(secondFragmentData);
            }
        }

    }

    /***
     * This function is bound to tbUsing's onClick
     * @param checked  tbUsing.isChecked()
     */
    private void toggleButton(boolean checked) {
        // Check if in use
        // Send message to firebase
        final DocumentReference docRef = db.collection("gyms").document("anchor").collection("equipment").document(tvEquipmentID.getText().toString());
        // Make map for firebase
        Map<String, Object> data = new HashMap<>();
        data.put(USED, checked);
        if (!checked) {
            data.put(CURR_USER, "null");
        } else {
            data.put(CURR_USER, FirebaseUid);
        }
        // Put the data
        docRef.set(data, SetOptions.merge());
        // Update the UI
        updateView(data);
        Log.d(TAG, "sending to FB" + data.toString());
    }

    /***
     * Update UI
     * @param map Data from firebase in a map
     */
    private void updateView(Map<String, Object> map) {
        // Display correct information on toggle button
        // userFlag checks that the current user of the machine is not the signed in user and that CURR_USER is not null
        userFlag = !map.get(CURR_USER).equals(FirebaseUid) && !map.get(CURR_USER).equals("null");
        if (/*tbUsing.isChecked() && */userFlag) {
            tbUsing.setClickable(false);
            Log.d(TAG, "set clickable to false -- different user using machine");
        } else {
            tbUsing.setClickable(true);
            Log.d(TAG, "set clickable to true -- current or no user using machine");
        }
        tbUsing.setChecked((boolean) map.get(USED));

    }

    //Override to set Firebase to show that the machine they were previously scanning is no longer in use
    @Override
    public void onDetach() {
        if(!userFlag) toggleButton(false);
        super.onDetach();
    }
}
