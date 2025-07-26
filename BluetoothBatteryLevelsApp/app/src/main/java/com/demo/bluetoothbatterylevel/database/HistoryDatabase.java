package com.demo.bluetoothbatterylevel.database;

import android.content.Context;
import android.os.AsyncTask;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public abstract class HistoryDatabase extends RoomDatabase {
    
    public static HistoryDatabase instance;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(SupportSQLiteDatabase supportSQLiteDatabase) {
            super.onCreate(supportSQLiteDatabase);
            new PopulateDbAsyncTask(HistoryDatabase.instance).execute(new Void[0]);
        }
    };


    public abstract HistoryDao Dao();

    public static synchronized HistoryDatabase getInstance(Context context) {
        HistoryDatabase historyDatabase;
        synchronized (HistoryDatabase.class) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(), HistoryDatabase.class, "history_list_database").fallbackToDestructiveMigration().addCallback(roomCallback).build();
            }
            historyDatabase = instance;
        }
        return historyDatabase;
    }

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void... voidArr) {
            return null;
        }

        PopulateDbAsyncTask(HistoryDatabase historyDatabase) {
            historyDatabase.Dao();
        }
    }
}
