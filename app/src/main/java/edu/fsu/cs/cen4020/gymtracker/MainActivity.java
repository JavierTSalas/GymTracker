package edu.fsu.cs.cen4020.gymtracker;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Arrays;
import java.util.Map;

import static edu.fsu.cs.cen4020.gymtracker.JoinGymFragment.GYM_TAG;
import static edu.fsu.cs.cen4020.gymtracker.JoinGymFragment.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String FirebaseUid;

    public Toolbar toolbar;

    public DrawerLayout drawerLayout;

    public NavController navController;

    public NavigationView navigationView;

    // Setting Up One Time Navigation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        setupNavigation();

        // If first time launch
        // Open slider
        // Checking for first time launch - before calling setContentView()
        PrefManager prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false);
            navController.navigate(R.id.action_gymFragment_to_sliderPage1);
            return;
        }


        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // If there is no user
        if (currentUser==null)
        {
            Log.d(TAG,"currentUser is null, prompting for login");
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

        }else {
            // For the event that the user is already signed in
            FirebaseUid = currentUser.getUid();
            Log.d(TAG,"currentUser is "+FirebaseUid);


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


    }

    private void setupNavigation() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigationView);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);


        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(navigationView, navController);

        getSupportActionBar().hide();

        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(drawerLayout, Navigation.findNavController(this, R.id.nav_host_fragment));
    }



    /***
     * Opens the gym fragment if there the user has a gym. Open a fragment to pick a gym if the user does not have a gym
     * @param map Data from firebase in a map
     */
    public void processGYMID(Map<String, Object> map) {
        String gymString = (String) map.get(GYM_TAG);
        if (gymString != null) {
            if (!gymString.isEmpty() && !gymString.equals("null")) {

                // Add the GYM_TAG to a bundle
                Bundle bundle = new Bundle();
                bundle.putString(GYM_TAG, gymString);

                // Open the gym fragment
                navController.navigate(R.id.gymFragment,bundle);

                Log.d(TAG, "User has already selected a gym:"+gymString);
            } else {
                // Open the gym fragment
                navController.navigate(R.id.joinGymFragment);
                Log.d(TAG, "Prompting user to select their gym");

            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                navController.navigate(R.id.gymFragment);
                break;
            case R.id.nav_scan:
                navController.navigate(R.id.scanFragment);
                break;
            case R.id.nav_nutrition:
                navController.navigate(R.id.nutritionFragment);
                break;
            case R.id.nav_log:
                navController.navigate(R.id.logFragment);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

}
