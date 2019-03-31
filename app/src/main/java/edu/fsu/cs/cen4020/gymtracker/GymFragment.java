package edu.fsu.cs.cen4020.gymtracker;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class GymFragment extends Fragment {
    private String GYM_ID;
    TextView tvTitle;
    ImageView ivGym;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = GymFragment.class.getCanonicalName();

    public GymFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            GYM_ID = getArguments().getString(MainActivity.GYM_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        View root = inflater.inflate(R.layout.fragment_gym, container, false);
        tvTitle = root.findViewById(R.id.tv_gym_name);
        tvTitle.setText(GYM_ID);
        ivGym = root.findViewById(R.id.iv_gym);


        // If there is no gym
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
                } else {
                    Log.d(TAG, "data error: null");
                }
            }
        });

        return root;
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
