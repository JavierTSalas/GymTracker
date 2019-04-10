package edu.fsu.cs.cen4020.gymtracker;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    Long insertTask(LogEntry logEntry);


    @Query("SELECT * FROM LogEntry ORDER BY logDate desc")
    LiveData<List<LogEntry>> fetchAllLogs();


    @Query("SELECT * FROM LogEntry WHERE logDate =:date")
    LiveData<LogEntry> getLog(String date);

    @Update
    void updateLog(LogEntry entry);

    @Delete
    void deleteLog(LogEntry entry);
}