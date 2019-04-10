package edu.fsu.cs.cen4020.gymtracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

@Entity
public class LogEntry implements Serializable {

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String logDate;

    @ColumnInfo(name = "text")
    private String text;

    public String getText(){
        return text;
    }

    public void setText(String dongle){
        text = dongle;
    }

}