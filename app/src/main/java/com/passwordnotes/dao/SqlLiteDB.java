package com.passwordnotes.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
                "VALUES (0, 'dudu', 'dudu_name_string', 'password_string', 'dudu_account', 0, 1643683200000, 2, 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(activityContext, "数据库暂时不支持升级操作!", Toast.LENGTH_SHORT).show();
    }
}
