package com.demo.bluetoothbatterylevel.database;

import androidx.core.app.NotificationCompat;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomMasterTable;
import androidx.room.RoomOpenHelper;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.util.HashMap;
import java.util.HashSet;

public final class HistoryDatabase_Impl extends HistoryDatabase {
    private volatile HistoryDao _historyDao;

    
    public SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration databaseConfiguration) {
        return databaseConfiguration.sqliteOpenHelperFactory.create(SupportSQLiteOpenHelper.Configuration.builder(databaseConfiguration.context).name(databaseConfiguration.name).callback(new RoomOpenHelper(databaseConfiguration, new RoomOpenHelper.Delegate(1) {
            public void onPostMigrate(SupportSQLiteDatabase supportSQLiteDatabase) {
            }

            public void createAllTables(SupportSQLiteDatabase supportSQLiteDatabase) {
                supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `history_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `date` TEXT, `time` TEXT, `status` TEXT, `battery` INTEGER NOT NULL, `type` INTEGER NOT NULL)");
                supportSQLiteDatabase.execSQL(RoomMasterTable.CREATE_QUERY);
                supportSQLiteDatabase.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5c9fa46140100053c225b0b28ba658ac')");
            }

            public void dropAllTables(SupportSQLiteDatabase supportSQLiteDatabase) {
                supportSQLiteDatabase.execSQL("DROP TABLE IF EXISTS `history_table`");
                if (HistoryDatabase_Impl.this.mCallbacks != null) {
                    int size = HistoryDatabase_Impl.this.mCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        ((RoomDatabase.Callback) HistoryDatabase_Impl.this.mCallbacks.get(i)).onDestructiveMigration(supportSQLiteDatabase);
                    }
                }
            }

            
            public void onCreate(SupportSQLiteDatabase supportSQLiteDatabase) {
                if (HistoryDatabase_Impl.this.mCallbacks != null) {
                    int size = HistoryDatabase_Impl.this.mCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        ((RoomDatabase.Callback) HistoryDatabase_Impl.this.mCallbacks.get(i)).onCreate(supportSQLiteDatabase);
                    }
                }
            }

            public void onOpen(SupportSQLiteDatabase supportSQLiteDatabase) {
                SupportSQLiteDatabase unused = HistoryDatabase_Impl.this.mDatabase = supportSQLiteDatabase;
                HistoryDatabase_Impl.this.internalInitInvalidationTracker(supportSQLiteDatabase);
                if (HistoryDatabase_Impl.this.mCallbacks != null) {
                    int size = HistoryDatabase_Impl.this.mCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        ((RoomDatabase.Callback) HistoryDatabase_Impl.this.mCallbacks.get(i)).onOpen(supportSQLiteDatabase);
                    }
                }
            }

            public void onPreMigrate(SupportSQLiteDatabase supportSQLiteDatabase) {
                DBUtil.dropFtsSyncTriggers(supportSQLiteDatabase);
            }

            
            public RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase supportSQLiteDatabase) {
                HashMap hashMap = new HashMap(7);
                hashMap.put("id", new TableInfo.Column("id", "INTEGER", true, 1, (String) null, 1));
                hashMap.put("name", new TableInfo.Column("name", "TEXT", false, 0, (String) null, 1));
                hashMap.put("date", new TableInfo.Column("date", "TEXT", false, 0, (String) null, 1));
                hashMap.put("time", new TableInfo.Column("time", "TEXT", false, 0, (String) null, 1));
                hashMap.put(NotificationCompat.CATEGORY_STATUS, new TableInfo.Column(NotificationCompat.CATEGORY_STATUS, "TEXT", false, 0, (String) null, 1));
                hashMap.put("battery", new TableInfo.Column("battery", "INTEGER", true, 0, (String) null, 1));
                hashMap.put("type", new TableInfo.Column("type", "INTEGER", true, 0, (String) null, 1));
                TableInfo tableInfo = new TableInfo("history_table", hashMap, new HashSet(0), new HashSet(0));
                TableInfo read = TableInfo.read(supportSQLiteDatabase, "history_table");
                if (!tableInfo.equals(read)) {
                    return new RoomOpenHelper.ValidationResult(false, "history_table(com.demo.bluetoothbatterylevel.model.History).\n Expected:\n" + tableInfo + "\n Found:\n" + read);
                }
                return new RoomOpenHelper.ValidationResult(true, (String) null);
            }
        }, "5c9fa46140100053c225b0b28ba658ac", "4254608fb4b53d0c82bef82a997b4bdb")).build());
    }

    
    public InvalidationTracker createInvalidationTracker() {
        return new InvalidationTracker(this, new HashMap(0), new HashMap(0), "history_table");
    }

    public void clearAllTables() {
        super.assertNotMainThread();
        SupportSQLiteDatabase writableDatabase = super.getOpenHelper().getWritableDatabase();
        try {
            super.beginTransaction();
            writableDatabase.execSQL("DELETE FROM `history_table`");
            super.setTransactionSuccessful();
        } finally {
            super.endTransaction();
            writableDatabase.query("PRAGMA wal_checkpoint(FULL)").close();
            if (!writableDatabase.inTransaction()) {
                writableDatabase.execSQL("VACUUM");
            }
        }
    }

    public HistoryDao Dao() {
        HistoryDao historyDao;
        if (this._historyDao != null) {
            return this._historyDao;
        }
        synchronized (this) {
            if (this._historyDao == null) {
                this._historyDao = new HistoryDao_Impl(this);
            }
            historyDao = this._historyDao;
        }
        return historyDao;
    }
}
