package edu.fsu.cs.cen4020.gymtracker.slider;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.fsu.cs.cen4020.gymtracker.R;


public class SliderPage4 extends Fragment {

    private Button bSlider4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_slider_page4, container, false);
        bSlider4= root.findViewById(R.id.bSlider4);
        bSlider4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = getActivity().getIntent();
                getActivity().finish();
                startActivity(mIntent);
            }
        });
        return root;
    }

}
