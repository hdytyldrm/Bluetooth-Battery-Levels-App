package com.demo.bluetoothbatterylevel.database;

import androidx.lifecycle.LiveData;
import com.demo.bluetoothbatterylevel.model.History;
import java.util.List;

public interface HistoryDao {
    void deleteAllData();

    LiveData<List<History>> getAllData();

    void insert(History history);
}
