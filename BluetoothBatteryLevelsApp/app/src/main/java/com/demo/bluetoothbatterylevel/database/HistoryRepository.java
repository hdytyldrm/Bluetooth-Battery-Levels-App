package com.demo.bluetoothbatterylevel.database;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.demo.bluetoothbatterylevel.model.History;
import java.util.List;

public class HistoryRepository {
    private LiveData<List<History>> allWebData;
    private HistoryDao dao;

    public HistoryRepository(Application application) {
        HistoryDao Dao = HistoryDatabase.getInstance(application).Dao();
        this.dao = Dao;
        this.allWebData = Dao.getAllData();
    }

    public void insert(History history) {
        new InsertAsyncTask(this.dao).execute(new History[]{history});
    }

    public LiveData<List<History>> getAllData() {
        return this.allWebData;
    }

    public void deleteAllData() {
        new DeleteAllAsyncTask(this.dao).execute(new Void[0]);
    }

    private static class InsertAsyncTask extends AsyncTask<History, Void, Void> {
        private HistoryDao dao;

        private InsertAsyncTask(HistoryDao historyDao) {
            this.dao = historyDao;
        }

        @Override
        public Void doInBackground(History... historyArr) {
            this.dao.insert(historyArr[0]);
            return null;
        }
    }

    private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private HistoryDao dao;

        private DeleteAllAsyncTask(HistoryDao historyDao) {
            this.dao = historyDao;
        }

        @Override
        public Void doInBackground(Void... voidArr) {
            this.dao.deleteAllData();
            return null;
        }
    }
}
