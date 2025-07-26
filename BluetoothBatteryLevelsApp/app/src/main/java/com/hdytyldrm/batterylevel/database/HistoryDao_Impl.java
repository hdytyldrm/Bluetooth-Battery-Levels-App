package com.hdytyldrm.batterylevel.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.hdytyldrm.batterylevel.model.History;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public final class HistoryDao_Impl implements HistoryDao {
    
    public final RoomDatabase __db;
    private final EntityInsertionAdapter<History> __insertionAdapterOfHistory;
    private final SharedSQLiteStatement __preparedStmtOfDeleteAllData;

    public HistoryDao_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
        this.__insertionAdapterOfHistory = new EntityInsertionAdapter<History>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR ABORT INTO `history_table` (`id`,`name`,`date`,`time`,`status`,`battery`,`type`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, History history) {
                supportSQLiteStatement.bindLong(1, (long) history.id);
                if (history.name == null) {
                    supportSQLiteStatement.bindNull(2);
                } else {
                    supportSQLiteStatement.bindString(2, history.name);
                }
                if (history.date == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindString(3, history.date);
                }
                if (history.time == null) {
                    supportSQLiteStatement.bindNull(4);
                } else {
                    supportSQLiteStatement.bindString(4, history.time);
                }
                if (history.status == null) {
                    supportSQLiteStatement.bindNull(5);
                } else {
                    supportSQLiteStatement.bindString(5, history.status);
                }
                supportSQLiteStatement.bindLong(6, (long) history.battery);
                supportSQLiteStatement.bindLong(7, (long) history.type);
            }
        };
        this.__preparedStmtOfDeleteAllData = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM history_table";
            }
        };
    }

    public void insert(History history) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfHistory.insert(history);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public void deleteAllData() {
        this.__db.assertNotSuspendingTransaction();
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeleteAllData.acquire();
        this.__db.beginTransaction();
        try {
            acquire.executeUpdateDelete();
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
            this.__preparedStmtOfDeleteAllData.release(acquire);
        }
    }

    public LiveData<List<History>> getAllData() {
        final RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * FROM history_table", 0);
        return this.__db.getInvalidationTracker().createLiveData(new String[]{"history_table"}, false, new Callable<List<History>>() {
            public List<History> call() throws Exception {
                Cursor query = DBUtil.query(HistoryDao_Impl.this.__db, acquire, false, (CancellationSignal) null);
                try {
                    int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "id");
                    int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "name");
                    int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "date");
                    int columnIndexOrThrow4 = CursorUtil.getColumnIndexOrThrow(query, "time");
                    int columnIndexOrThrow5 = CursorUtil.getColumnIndexOrThrow(query, NotificationCompat.CATEGORY_STATUS);
                    int columnIndexOrThrow6 = CursorUtil.getColumnIndexOrThrow(query, "battery");
                    int columnIndexOrThrow7 = CursorUtil.getColumnIndexOrThrow(query, "type");
                    ArrayList arrayList = new ArrayList(query.getCount());
                    while (query.moveToNext()) {
                        History history = new History(query.getString(columnIndexOrThrow2), query.getString(columnIndexOrThrow3), query.getString(columnIndexOrThrow4), query.getString(columnIndexOrThrow5), query.getInt(columnIndexOrThrow6), query.getInt(columnIndexOrThrow7));
                        history.id = query.getInt(columnIndexOrThrow);
                        arrayList.add(history);
                    }
                    return arrayList;
                } finally {
                    query.close();
                }
            }

            
            public void finalize() {
                acquire.release();
            }
        });
    }
}
