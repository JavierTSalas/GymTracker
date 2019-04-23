package edu.fsu.cs.cen4020.gymtracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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
import edu.fsu.cs.cen4020.gymtracker.recycler.MealAdapter;
import edu.fsu.cs.cen4020.gymtracker.recycler.Meal_POJO;


public class NutritionFragment extends Fragment {
    TextView tvGymTitle;

    // For recycler view
    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private ArrayList<Meal_POJO> mealArrayList;


    // Choose an arbitrary request code value
    public static final int RC_SIGN_IN = 123;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = NutritionFragment.class.getCanonicalName();
    private View root;

    public NutritionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_nutrition, container, false);

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

        recyclerView = root.findViewById(R.id.recycler_meal_plans);

        mealArrayList = new ArrayList<>();
        adapter = new MealAdapter(getContext(), mealArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);


        // See https://stackoverflow.com/a/31671289/3843432 for why we need to do this
        adapter.setOnItemClickListener(new MealAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG, "onItemClick position: " + position + " content=" + mealArrayList.get(position));

                // Update to firebase
                Meal_POJO meal = mealArrayList.get(position);
                //TODO:: show meal
                StringBuilder stringBuilder = new StringBuilder();

                for (String line : meal.getContent().split("\\\\n")){
                    stringBuilder.append("\n");
                    stringBuilder.append(line.trim());
                }


                new AlertDialog.Builder(getContext())
                        .setTitle(meal.getTitle())
                        .setMessage(stringBuilder.toString())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();


            }


        });
        updateRecyclerFromFirestore();


        return root;
    }


    private void updateRecyclerFromFirestore() {
        db.collection("gyms").document("anchor").collection("nutrition")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mealArrayList.add(new Meal_POJO(data,document.getId()));
                                adapter.notifyDataSetChanged();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



}
