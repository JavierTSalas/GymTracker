package edu.fsu.cs.cen4020.gymtracker;

/*
  3/3/2019 By Ben K. and Javier S.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ScanFragment extends Fragment {

    final private static String USED = "used";
    private final static String CURR_USER = "currentUser";
    ToggleButton tbUsing;
    TextView etScanID;
    boolean usedByCurr = false;
    public static final String TAG = ScanFragment.class.getCanonicalName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String FirebaseUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private boolean userFlag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        tbUsing = view.findViewById(R.id.tb_Using);
        etScanID = view.findViewById(R.id.et_ScanID);


        final DocumentReference docRef = db.collection("gyms").document("anchor").collection("equipment").document(etScanID.getText().toString());
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


        tbUsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton(tbUsing.isChecked());
            }
        });

        return view;
    }

    /***
     * This function is bound to tbUsing's onClick
     * @param checked  tbUsing.isChecked()
     */
    private void toggleButton(boolean checked) {
        // Check if in use
        // Send message to firebase
        final DocumentReference docRef = db.collection("gyms").document("anchor").collection("equipment").document(etScanID.getText().toString());
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
