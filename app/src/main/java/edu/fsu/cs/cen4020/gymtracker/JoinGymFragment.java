package edu.fsu.cs.cen4020.gymtracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.fsu.cs.cen4020.gymtracker.recycler.GymAdapter;
import edu.fsu.cs.cen4020.gymtracker.recycler.Gym_POJO;


public class JoinGymFragment extends Fragment {
    TextView tvGymTitle;

    // For recycler view
    private RecyclerView recyclerView;
    private GymAdapter adapter;
    private ArrayList<Gym_POJO> gymArrayList;


    // Choose an arbitrary request code value
    public static final int RC_SIGN_IN = 123;
    public static final String GYM_TAG = "gymID";


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = JoinGymFragment.class.getCanonicalName();
    private View root;

    public JoinGymFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_join_gym, container, false);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // If there is no user
        if (currentUser == null) {
            Log.d(TAG, "currentUser is null, prompting for login");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.gym_tracker_logo)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }


        tvGymTitle =root.findViewById(R.id.tv_gymTitle);

        recyclerView = root.findViewById(R.id.recyclerView);

        gymArrayList = new ArrayList<>();
        adapter = new GymAdapter(getContext(), gymArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);


        // See https://stackoverflow.com/a/31671289/3843432 for why we need to do this
        adapter.setOnItemClickListener(new GymAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG, "onItemClick position: " + position + " content=" + gymArrayList.get(position));

                // Update to firebase
                String docId = gymArrayList.get(position).getDocument_id();
                setGymInFirebase(docId);
                // Go back to home
                processGYMID(docId);
            }


        });
        updateRecyclerFromFirestore();


        return root;
    }

    /***
     * Opens the gym fragment if there the user has a gym. Open a fragment to pick a gym if the user does not have a gym
     * @param gymString gym doc name
     */
    private void processGYMID(String gymString) {
        if (gymString != null) {
            if (!gymString.isEmpty() && !gymString.equals("null")) {

                // Add the GYM_TAG to a bundle
                Bundle bundle = new Bundle();
                bundle.putString(GYM_TAG, gymString);


                Navigation.findNavController(root).navigate(R.id.action_joinGymFragment_to_gymFragment, bundle);

                Log.d(TAG, "User has already selected a gym:" + gymString);
            }
        }
    }



    private void updateRecyclerFromFirestore() {
        db.collection("gyms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                gymArrayList.add(new Gym_POJO(data,document.getId()));
                                adapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void setGymInFirebase(String text) {
        // Send message to firebase
        final DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        // Make map for firebase
        Map<String, Object> data = new HashMap<>();
        data.put(GYM_TAG, text);
        // Put the data
        docRef.set(data, SetOptions.merge());
        Log.d(TAG, "sending to FB" + data.toString());

    }


}
