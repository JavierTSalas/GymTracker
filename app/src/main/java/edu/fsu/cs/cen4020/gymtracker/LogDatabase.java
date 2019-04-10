package edu.fsu.cs.cen4020.gymtracker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LogEntry.class}, version = 1, exportSchema = false)
public abstract class LogDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();
}