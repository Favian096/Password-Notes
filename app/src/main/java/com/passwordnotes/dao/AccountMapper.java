package com.passwordnotes.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 提供对于数据库的Accounts表CRUD
 */

public class AccountMapper {
    private Context context;
    public SQLiteDatabase accountsReader;
    public SQLiteDatabase accountsWriter;
    public SQLiteOpenHelper DB;

    //    在提供的构造器内初始化资源
    public AccountMapper(Context context) {
        this.context = context;
        DB = new SqlLiteDB(context, "DB", null, 1);
        accountsReader = DB.getReadableDatabase();
        accountsWriter = DB.getWritableDatabase();
    }

    //    获取全部账户数据
    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> allAccounts = new ArrayList<>();

        Cursor cursor = accountsReader.query("accounts",
                null, "isDelete <> ? and id <> ? ", new String[]{Integer.toString(1), Integer.toString(0)},
                null, null, "priority desc, id desc");

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Account account = new Account(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("id"))),
                        cursor.getString(cursor.getColumnIndex("tag")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("password")),
                        cursor.getString(cursor.getColumnIndex("remark")),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("weight"))),
                        Long.parseLong(cursor.getString(cursor.getColumnIndex("time"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("isDelete"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("priority")))
                );
                allAccounts.add(account);
            } while (cursor.moveToNext());
        }
        return allAccounts;
    }

    //    获取已经回收的账户数据
    public ArrayList<Account> getRecycleAccounts() {
        ArrayList<Account> allAccounts = new ArrayList<>();
        Cursor cursor = accountsReader.query("accounts",
                null, "isDelete = ? and id <> ? ", new String[]{Integer.toString(1), Integer.toString(0)},
                null, null, "priority desc, id desc");
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Account account = new Account(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("id"))),
                        cursor.getString(cursor.getColumnIndex("tag")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("password")),
                        cursor.getString(cursor.getColumnIndex("remark")),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("weight"))),
                        Long.parseLong(cursor.getString(cursor.getColumnIndex("time"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("isDelete"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("priority")))
                );
                allAccounts.add(account);
            } while (cursor.moveToNext());
        }
        return allAccounts;
    }

    //    根据id获取账户数据
    @SuppressLint("Range")
    public Account getAccount(int id) {
        System.out.println(String.valueOf(id));
        Cursor cursor = accountsReader.query("accounts",
                null, "id = ? ", new String[]{Integer.toString(id)},
                null, null, null);
        Account account = null;
        if (cursor.moveToFirst()) {
            account = new Account(
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex("id"))),
                    cursor.getString(cursor.getColumnIndex("tag")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("password")),
                    cursor.getString(cursor.getColumnIndex("remark")),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex("weight"))),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex("time"))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex("isDelete"))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex("priority")))
            );
        }
        return account;
    }

    //    获取Tag模糊查询账户数据
    public ArrayList<Account> getAccountsByTag(String tag, String remark) {
        ArrayList<Account> accountsList = new ArrayList<>();

        Cursor cursor = accountsReader.query("accounts",
                null, "tag like ? or remark like ? ", new String[]{"%" + tag + "%", "%" + remark + "%"},
                null, null, "id desc");

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Account account = new Account(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("id"))),
                        cursor.getString(cursor.getColumnIndex("tag")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("password")),
                        cursor.getString(cursor.getColumnIndex("remark")),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("weight"))),
                        Long.parseLong(cursor.getString(cursor.getColumnIndex("time"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("isDelete"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("priority")))
                );
                accountsList.add(account);
            } while (cursor.moveToNext());
        }
        return accountsList;
    }

    public boolean saveAccount(Account account) {
        if (account.getTag().isEmpty()) {
            Toast.makeText(context, "未填写账户标签!", Toast.LENGTH_SHORT).show();
            return false;
        }
        ContentValues accountValues = new ContentValues();
        accountValues.put("tag", account.getTag());
        accountValues.put("name", account.getName());
        accountValues.put("password", account.getPassword());
        accountValues.put("remark", account.getRemark());
        accountValues.put("weight", account.getWeight());
        accountValues.put("time", account.getTime());
        accountValues.put("isDelete", account.getIsDelete());
        accountValues.put("priority", account.getPriority());

        long flag = accountsWriter.insert("accounts", null, accountValues);
        if (-1 == flag) {
            Toast.makeText(context, account.getTag() + "账户添加失败!!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(context, account.getTag() + "账户添加成功", Toast.LENGTH_SHORT).show();
        return true;
    }

    public boolean updateAccount(Account account) {
        ContentValues accountUpdateValues = new ContentValues();
        accountUpdateValues.put("tag", account.getTag());
        accountUpdateValues.put("name", account.getName());
        accountUpdateValues.put("password", account.getPassword());
        accountUpdateValues.put("remark", account.getRemark());
        accountUpdateValues.put("weight", account.getWeight());
        accountUpdateValues.put("time", account.getTime());
        accountUpdateValues.put("isDelete", account.getIsDelete());
        accountUpdateValues.put("priority", account.getPriority());

        try {
            int flag = accountsWriter.update("accounts", accountUpdateValues,
                    "id = ?", new String[]{String.valueOf(account.getId())});
            if (1 == flag) {
                Toast.makeText(context, account.getTag() + "账户更新成功!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "数据异常!\n更新出错了!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean recyclerAccount(int id) {
        ContentValues accountUpdateValues = new ContentValues();
        accountUpdateValues.put("isDelete", 1);
        try {
            int flag = accountsWriter.update("accounts", accountUpdateValues,
                    "id = ?", new String[]{Integer.toString(id)});
            if (1 == flag) {
                Toast.makeText(context, "账户移除成功!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "数据异常!\n删错出错了!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean restoreAccount(int id) {
        ContentValues accountUpdateValues = new ContentValues();
        accountUpdateValues.put("isDelete", 0);
        try {
            int flag = accountsWriter.update("accounts", accountUpdateValues,
                    "id = ?", new String[]{Integer.toString(id)});
            if (1 == flag) {
                Toast.makeText(context, "账户恢复成功!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "数据异常!\n恢复出错了!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean deleteAccount(int id) {
        int flag = accountsWriter.delete("accounts", "id = ?", new String[]{String.valueOf(id)});
        if (1 == flag) {
            Toast.makeText(context, "账户已删除!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
