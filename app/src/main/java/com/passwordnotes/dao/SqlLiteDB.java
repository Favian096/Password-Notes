package com.passwordnotes.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.passwordnotes.utils.toaster.Toaster;

public class SqlLiteDB extends SQLiteOpenHelper {

    /**
     * 建表语句
     */
    private Context activityContext;
    private final String createAccountsTable = "CREATE TABLE accounts (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "tag TEXT NOT NULL, " +
            "name TEXT, " +
            "password TEXT, " +
            "remark TEXT," +
            "weight INTEGER," +
            "time INTEGER," +
            "isDelete INTEGER," +
            "priority INTEGER)";

    public SqlLiteDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        activityContext = context;
    }

    public SqlLiteDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        activityContext = context;
    }

    public SqlLiteDB(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
        activityContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        创建账户的数据表
        db.execSQL(createAccountsTable);
        db.execSQL("INSERT INTO accounts (id, tag, name, password, remark, weight, time, isDelete, priority) " +
                "VALUES (0, 'dudu', 'dudu_account', 'dudu_password', '{\"settings\":\"data set\"}', 0, 1706889600000, 2, 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toaster.info("数据库暂时不支持升级操作!");
    }
}
