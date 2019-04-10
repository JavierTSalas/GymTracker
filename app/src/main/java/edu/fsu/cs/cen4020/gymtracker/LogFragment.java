package edu.fsu.cs.cen4020.gymtracker;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LogFragment extends Fragment {

    private String TAG = LogFragment.class.getCanonicalName();
    private View view;
    private CalendarView calendar;
    private Button editButton;
    private String currDate;
    private LogDatabase db;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log,container,false);

        calendar = view.findViewById(R.id.logCalendar);
        editButton = view.findViewById(R.id.editLogButton);

        Date date = new Date(calendar.getDate());
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); //TODO:: requires higher API than 26 -- discuss implications and solutions later w team
        currDate = ""+localDate.getMonthValue()+localDate.getDayOfMonth()+localDate.getYear();
        //Log.d(TAG, currDate);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLog();
            }
        });
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                currDate = ""+(month+1)+dayOfMonth+year;
                //TODO::implement textview under to view log and update here ; edit log button should only be used to edit
                //Log.d(TAG, currDate);
            }
        });

        return view;
    }

    private void editLog() {
        //TODO:: database


        //TODO:: show toast with contents and allow for edit / save
    }
}
