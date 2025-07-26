package com.hdytyldrm.batterylevel.database;

import androidx.lifecycle.LiveData;
import com.hdytyldrm.batterylevel.model.History;
import java.util.List;

public interface HistoryDao {
    void deleteAllData();

    LiveData<List<History>> getAllData();

    void insert(History history);
}
