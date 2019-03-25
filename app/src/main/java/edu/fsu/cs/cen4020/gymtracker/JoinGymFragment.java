package edu.fsu.cs.cen4020.gymtracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class JoinGymFragment extends Fragment {
    EditText etGymName;
    TextView tvGymTitle;

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
        etGymName =root.findViewById(R.id.et_gymName);
        tvGymTitle =root.findViewById(R.id.tv_gymTitle);
        tvGymTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update to firebase
                setGymInFirebase(etGymName.getText().toString());
                // Go back to home
                Navigation.findNavController(root).navigate(R.id.homeFragment);
            }
        });
        return root;
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
