package edu.fsu.cs.cen4020.gymtracker;

/*
  3/3/2019 By Ben K. and Javier S.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
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
    private String[] urlSuffixes = {
            "neck",
            "traps",
            "shoulders",
            "chest",
            "biceps",
            "forearms",
            "abs",
            "quadriceps",
            "calves",
            "triceps",
            "lats",
            "middle-back",
            "lower-back",
            "glutes",
            "hamstrings"
    };
    private View root;
    private HorizontalScrollView tagContainer;
    private Button logButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_scan, container, false);
        tbUsing = root.findViewById(R.id.tb_Using);
        bScan = root.findViewById(R.id.b_Scan);
        tvEquipmentID = root.findViewById(R.id.tv_ScanID);
        FirebaseUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //TODO: Verify that Uid exists
        tagContainer = root.findViewById(R.id.tag_container);
        logButton = root.findViewById(R.id.add_machine_to_log_button);

        // If we opened the fragment form the CodeScanFragment
        Bundle argsFromCodeScanFragment = getArguments();
        if (argsFromCodeScanFragment.getString(CodeScanFragment.INTENT_QR_CODE_KEY) != null) {
            QR_CODE = argsFromCodeScanFragment.getString(CodeScanFragment.INTENT_QR_CODE_KEY);
            if (validQRCODE(QR_CODE))
            {
                // Do something with the QR code,
                tvEquipmentID.setText(PersonalLogTextUtil.removeTags(QR_CODE));

            } else {
                Log.d(TAG, QR_CODE);
                Toast.makeText(getContext(), "Invalid QR Code, scan only GymTracker QR Codes", Toast.LENGTH_LONG).show();
            }
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
                if (validQRCODE(QR_CODE)) {
                    toggleButton(tbUsing.isChecked());
                } else {
                    // TODO: Use a better feedback
                    tbUsing.setChecked(false);
                    displayError();
                }
            }
        });

        bScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.codeScanFragment);
            }
        });

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validQRCODE(QR_CODE)){
                    displayError();
                }
                else{
                    logWorkout();
                }
            }
        });


        // For lottie - animations
        LottieAnimationView animationView = (LottieAnimationView) root.findViewById(R.id.animation_view);
        animationView.setAnimation("scan-qr.json");
        animationView.playAnimation();


        return root;
    }

    private void displayError() {
        Toast toast = Toast.makeText(getContext(), "No Valid Machine Scanned", Toast.LENGTH_SHORT);
        toast.setMargin(50, 50);
        toast.show();
    }

    private boolean validQRCODE(String qr_code) {
        if (qr_code ==null){
            return false;
        }
        return qr_code.matches("(.+)\\|[01]{16}");
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
                tvEquipmentID.setText(PersonalLogTextUtil.removeTags(secondFragmentData));
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

        showExerciseTags();
        tbUsing.setChecked((boolean) map.get(USED));

    }

    //Override to set Firebase to show that the machine they were previously scanning is no longer in use
    @Override
    public void onDetach() {
        if(!userFlag) toggleButton(false);
        super.onDetach();
    }

    private void showExerciseTags(){

        if(!validQRCODE(QR_CODE))
            return;
        String[] split = QR_CODE.split("\\|");
        String bitmask = split[1];
        Log.d(TAG, "[0]:"+split[0]+"[1]:"+ bitmask);

        tagContainer.removeAllViews();
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        int i;
        for(i = 0; i < bitmask.length() && i < 15; i++){
            Log.d(TAG, "  "+ bitmask.charAt(i));
            if(bitmask.charAt(i) == '1'){
                Log.d(TAG, "adding button for " + urlSuffixes[i]);

                int buttonStyle = R.style.AppTheme_RoundedCornerMaterialButton;
                Button b = new Button(new ContextThemeWrapper(getContext(), buttonStyle), null, buttonStyle);

                b.setText(urlSuffixes[i]);
                //String url = getResources().getString(R.string.bodybuilding_url).concat(urlSuffixes[i]);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openBrowser = new Intent(Intent.ACTION_VIEW);
                        Button button = (Button)v;
                        String url = getResources().getString(R.string.bodybuilding_url).concat(button.getText().toString());
                        openBrowser.setData(Uri.parse(url));
                        startActivity(openBrowser);
                    }
                });
                linearLayout.addView(b);
            }
        }

        tagContainer.addView(linearLayout);

    }

    private void logWorkout() {
        final NumberPicker repPicker = new NumberPicker(getActivity());
        final NumberPicker setPicker = new NumberPicker(getActivity());
        repPicker.setMinValue(1);
        repPicker.setMaxValue(50);
        setPicker.setMinValue(1);
        setPicker.setMaxValue(15);
        TextView chooseReps = new TextView(getContext());
        TextView chooseSets = new TextView(getContext());
        chooseReps.setText("# of Repetitions:");
        chooseSets.setText("# of Sets");
        chooseReps.setTextSize(20);
        chooseSets.setTextSize(20);
        chooseReps.setTextColor(Color.BLACK);
        chooseSets.setTextColor(Color.BLACK);


        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(chooseSets);
        linearLayout.addView(setPicker);
        linearLayout.addView(chooseReps);
        linearLayout.addView(repPicker);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Log Machine");
        builder.setView(linearLayout);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PersonalLogTextUtil.addExerciseToLog(getContext(),
                        getViewLifecycleOwner(),
                        buildDate(),
                        PersonalLogTextUtil.removeTags(QR_CODE),
                        setPicker.getValue(),
                        repPicker.getValue());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private String buildDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
        Date dateObj = new Date();
        return sdf.format(dateObj);
    }
}