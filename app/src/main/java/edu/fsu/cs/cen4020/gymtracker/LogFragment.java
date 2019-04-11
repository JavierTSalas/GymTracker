package edu.fsu.cs.cen4020.gymtracker;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LogFragment extends Fragment {

    private String TAG = LogFragment.class.getCanonicalName();
    private View view;
    private CalendarView calendar;
    private Button editButton;
    private TextView logTextView;
    private String currDate;
    private LogRepository db;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_log,container,false);

        db = new LogRepository(getContext());
        //db.insertEntry("4112019", "bench press 32 reps");
        if(db.getLog("4112019") == null) Log.d(TAG, "\n\nshit aint working\n\n");

        calendar = view.findViewById(R.id.logCalendar);
        editButton = view.findViewById(R.id.editLogButton);
        logTextView = view.findViewById(R.id.logEntryTextView);

        final Date date = new Date(calendar.getDate());
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
                //MMDDYYYY
                String sMonth = String.valueOf(month+1);
                String sDay = String.valueOf(dayOfMonth);
                if(month < 10){
                    sMonth = "0"+sMonth;
                }
                if(dayOfMonth < 10){
                    sDay = "0"+sDay;
                }

                currDate = sMonth+sDay+year;
                displayLog(currDate);
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
        Date dateObj = new Date();
        displayLog(sdf.format(dateObj));


        return view;
    }

    private void displayLog(String date){
        Log.d(TAG, "display log for : "+date);
        db.getLog(date).observe(getViewLifecycleOwner(), new Observer<LogEntry>() {
            @Override
            public void onChanged(LogEntry entry) {
                if(entry != null)
                    logTextView.setText(entry.getText());
                else logTextView.setText("No log found");
            }
        });
    }

    private void editLog() {
        //TODO:: database


        //TODO:: show dialog with contents and allow for edit / save
    }
}
