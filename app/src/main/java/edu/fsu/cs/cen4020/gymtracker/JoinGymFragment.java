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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
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


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String FirebaseUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static final String TAG = JoinGymFragment.class.getCanonicalName();

    public JoinGymFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_join_gym, container, false);
        tvGymTitle =root.findViewById(R.id.tv_gymTitle);

        recyclerView = (RecyclerView) root.findViewById(R.id.rv_gym_annoucements);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gymArrayList = new ArrayList<>();
        adapter = new GymAdapter(getContext(), gymArrayList);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);


        // See https://stackoverflow.com/a/31671289/3843432 for why we need to do this
        adapter.setOnItemClickListener(new GymAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG, "onItemClick position: " + position + " content=" + gymArrayList.get(position));

                // Update to firebase
                setGymInFirebase(gymArrayList.get(position).getDocument_id());
                // Go back to home
                Navigation.findNavController(root).navigate(R.id.homeFragment);
            }


        });
        updateRecyclerFromFirestore();
        return root;
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
        final DocumentReference docRef = db.collection("users").document(FirebaseUid);
        // Make map for firebase
        Map<String, Object> data = new HashMap<>();
        data.put(MainActivity.GYM_TAG, text);
        // Put the data
        docRef.set(data, SetOptions.merge());
        Log.d(TAG, "sending to FB" + data.toString());

    }


}
