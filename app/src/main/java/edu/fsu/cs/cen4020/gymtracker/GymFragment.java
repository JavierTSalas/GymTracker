package edu.fsu.cs.cen4020.gymtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static edu.fsu.cs.cen4020.gymtracker.JoinGymFragment.GYM_TAG;


public class GymFragment extends Fragment {
    private String GYM_ID;
    TextView tvTitle, tv_announcements;
    ImageView ivGym;
    Button feedbackButton,joinGymFragment;
    final private static String USED = "used";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = GymFragment.class.getCanonicalName();

    public GymFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            GYM_ID = getArguments().getString(GYM_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        final View root = inflater.inflate(R.layout.fragment_gym, container, false);
        tvTitle = root.findViewById(R.id.tv_gym_name);
        ivGym = root.findViewById(R.id.iv_gym);
        feedbackButton = root.findViewById(R.id.feedback_button);
        joinGymFragment = root.findViewById(R.id.start_workout_button);
        tv_announcements = root.findViewById(R.id.tv_announcement_content);
        tv_announcements.setMovementMethod(new ScrollingMovementMethod());


        // If there is no gym
        Log.d(TAG, "Fetching gym_id=" + GYM_ID);
        if (GYM_ID != null) {
            updateUI(GYM_ID);
        } else {
            // For the event that the user is already signed in
            mAuth = FirebaseAuth.getInstance();
            String FirebaseUid = mAuth.getUid();
            Log.d(TAG, "currentUser is " + FirebaseUid);


            if(FirebaseUid == null){
                Log.d(TAG, "no uid!!");
            }
            // If there is no gym
            final DocumentReference docRef = db.collection("users").document(FirebaseUid);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "snapshot data: " + snapshot.getData());
                        // Attempt to process the data just fetched from firebase
                        processGYMID(snapshot.getData());
                    } else {
                        Log.d(TAG, "data error: null");
                    }
                }
            });
        }

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFeedbackMessage();

            }
        });
        joinGymFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDailyFlag(true);
                setDailyFlag(false);
                Navigation.findNavController(root).navigate(R.id.scanFragment);

            }
        });

        return root;
    }

    private void fillAnnouncements() {
        ///gyms/anchor/notifications/3VzigG2UUH0tsbWDQeke
        if(GYM_ID == null){
            tv_announcements.setText("Can't Find Any Announcements Right Now.");
            return;

        }
        db.collection("gyms").document(GYM_ID).collection("notifications")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            tv_announcements.setText("---------------------------------------\n");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Date start, end, now;
                                String startStr, endStr;
                                startStr = (String) document.getData().get("start_date");
                                endStr = (String) document.getData().get("end_date");

                                try {
                                    start = sdf.parse(startStr);
                                    end = sdf.parse(endStr);
                                    now = new Date();
                                    if(now.compareTo(start) > 0 && now.compareTo(end) < 0){
                                        tv_announcements.setText(tv_announcements.getText().toString()+
                                                document.getData().get("message") + "\n---------------------------------------\n");

                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    private void setDailyFlag(boolean b) {
        final DocumentReference docRef = db.collection("gyms").document("anchor").collection("equipment").document("Daily");
        // Make map for firebase
        Map<String, Object> data = new HashMap<>();
        data.put(USED,  b);
        // Put the data
        docRef.set(data, SetOptions.merge());
    }

    private void getFeedbackMessage() {
        final Date now = new Date();
        final String feedbackReference = mAuth.getUid() + now.toString();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Feedback");

        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("date", now);
                        data.put("fixed", false);
                        data.put("message", input.getText().toString());
                        db.collection("gyms").document(GYM_ID).collection("reports").document(feedbackReference).set(data);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void updateUI(String GYM_ID) {
        final DocumentReference docRef = db.collection("gyms").document(GYM_ID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "snapshot data: " + snapshot.getData());
                    // Attempt to process the data just fetched from firebase
                    processGymMap(snapshot.getData());
                    fillAnnouncements();
                } else {
                    Log.d(TAG, "data error: null");
                }
            }
        });

    }

    /***
     * Opens the gym fragment if there the user has a gym. Open a fragment to pick a gym if the user does not have a gym
     * @param map Data from firebase in a map
     */
    public void processGYMID(Map<String, Object> map) {
        String gymString = (String) map.get(GYM_TAG);
        if (gymString != null) {
            if (!gymString.isEmpty() && !gymString.equals("null")) {
                Log.d(TAG, "User has already selected a gym:" + gymString);
                updateUI(gymString);
            }
        }

    }


    /***
     * Opens the gym fragment if there the user has a gym. Open a fragment to pick a gym if the user does not have a gym
     * @param map Data from firebase in a map
     */
    private void processGymMap(Map<String, Object> map) {
        String gymName = (String) map.get("name");
        if (gymName != null) {
            if (!gymName.isEmpty() && !gymName.equals("null")) {
                Log.d(TAG, "User is being shown gym=:" + gymName);
                String icon_url = (String) map.get("icon_url");
                tvTitle.setText(gymName);
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round);



                Glide.with(this).load(icon_url).apply(options).into(ivGym);

//                Picasso.Builder builder = new Picasso.Builder(getContext());
//                builder.listener(new Picasso.Listener()
//                {
//                    @Override
//                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                        Log.d(TAG, "onImageLoadFailed: "+exception);
//                    }
//
//
//                });
//                builder.build().load(icon_url).into(ivGym);
            } else {

            }
        }

    }


    @Override
    public void onDestroy() {
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        super.onDestroy();
    }
}
