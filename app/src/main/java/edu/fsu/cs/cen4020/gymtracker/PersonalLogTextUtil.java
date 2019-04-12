package edu.fsu.cs.cen4020.gymtracker;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class PersonalLogTextUtil {

    private static boolean updated;
    private static String oldText;

    public static void addExerciseToLog(Context context, LifecycleOwner owner, String currDate, String machine, int sets, int reps){
        final LogRepository db = new LogRepository(context);
        final String addition = machine + "\n\t" + sets + " Sets for " + reps + " Repetitions\n";
        final String date = currDate;
        updated = false;
        oldText = "";
        db.getLog(date).observe(owner, new Observer<LogEntry>() {
            @Override
            public void onChanged(LogEntry entry) {
                if(entry != null){
                    if(!updated) {
                        oldText = entry.getText();
                        db.updateLog(date, oldText + addition);
                        updated = true;
                    }
                }
                else{
                    db.insertEntry(date, addition);
                }
            }
        });
    }

    public static String removeTags(String qrString){
        String[] splits = qrString.split("\\|");
        String[] splits2 = splits[0].split("#");
        char[] formatted = splits2[0].toCharArray();
        for(int i = 0; i < splits2[0].length(); i++){
            if(formatted[i] == '_'){
                formatted[i] = ' ';
            }
        }
        return new String(formatted);
    }

}
