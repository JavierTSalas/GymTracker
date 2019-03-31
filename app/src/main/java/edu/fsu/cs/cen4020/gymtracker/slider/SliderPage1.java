package edu.fsu.cs.cen4020.gymtracker.slider;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.fsu.cs.cen4020.gymtracker.R;


public class SliderPage1 extends Fragment {
     Button bSlider1;

    public SliderPage1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_slider_page1, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        bSlider1= root.findViewById(R.id.bSlider1);
        bSlider1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_sliderPage1_to_sliderPage2);
            }
        });
        return root;
    }

}
