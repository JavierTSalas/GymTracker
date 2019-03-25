package edu.fsu.cs.cen4020.gymtracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.MessageFormat;


public class GymFragment extends Fragment {
    private String GYM_ID;
    TextView tvTitle;


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
        View root = inflater.inflate(R.layout.fragment_gym, container, false);
        tvTitle=root.findViewById(R.id.tv_gymTitle);
        tvTitle.setText(MessageFormat.format("You are in the gym:{0}", GYM_ID));

        return root;
    }

}
