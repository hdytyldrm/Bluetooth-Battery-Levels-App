package com.demo.bluetoothbatterylevel.database;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.demo.bluetoothbatterylevel.model.History;
import java.util.List;

public class HistoryViewModal extends AndroidViewModel {
    private LiveData<List<History>> allWebData;
    private HistoryRepository repository;

    public HistoryViewModal(Application application) {
        super(application);
        HistoryRepository historyRepository = new HistoryRepository(application);
        this.repository = historyRepository;
        this.allWebData = historyRepository.getAllData();
    }

    public void insert(History history) {
        this.repository.insert(history);
    }

    public LiveData<List<History>> getAllData() {
        return this.allWebData;
    }

    public void deleteAllData() {
        this.repository.deleteAllData();
    }
}
