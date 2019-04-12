package edu.fsu.cs.cen4020.gymtracker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class LogRepository {

    private String DB_NAME = "db";

    private LogDatabase LogDatabase;
    public LogRepository(Context context) {
        LogDatabase = Room.databaseBuilder(context, LogDatabase.class, DB_NAME).build();
    }

    public void insertEntry(String date,
                           String description) {

        insertLog(date, description);
    }

    public void insertLog(String date,
                           String description) {

        LogEntry log = new LogEntry();
        log.setLogDate(date);
        log.setText(description);

        insertLog(log);
    }

    public void insertLog(final LogEntry log) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                LogDatabase.daoAccess().insertTask(log);
                return null;
            }
        }.execute();
    }

    public void updateLog(final LogEntry log) {
        //log.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                LogDatabase.daoAccess().updateLog(log);
                return null;
            }
        }.execute();
    }

    public void updateLog(String date, String description){
        LogEntry entry = new LogEntry();
        entry.setLogDate(date);
        entry.setText(description);
        updateLog(entry);
    }

    public void deleteTask(final String id) {
        final LiveData<LogEntry> log = getLog(id);
        if(log != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    LogDatabase.daoAccess().deleteLog(log.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final LogEntry entry) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                LogDatabase.daoAccess().deleteLog(entry);
                return null;
            }
        }.execute();
    }

    public LiveData<LogEntry> getLog(String id) {
        return LogDatabase.daoAccess().getLog(id);
    }

    public LiveData<List<LogEntry>> getLogs() {
        return LogDatabase.daoAccess().fetchAllLogs();
    }
}