package com.passwordnotes.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.passwordnotes.utils.toaster.Toaster;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供对于数据库的Accounts表CRUD
 */

public class AccountMapper {
    private Context context;
    public SQLiteDatabase accountsReader;
    public SQLiteDatabase accountsWriter;
    public SQLiteOpenHelper DB;

    /**
     * 初始化资源
     *
     * @param context Activity会话
     */
    public AccountMapper(Context context) {
        this.context = context;
        DB = new SqlLiteDB(context, "DB", null, 1);
        accountsReader = DB.getReadableDatabase();
        accountsWriter = DB.getWritableDatabase();
    }

    /**
     * 获取全部账户数据
     *
     * @return 账户数据列表(不包含回收的和管理员数据)
     */
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

    /**
     * 获取已经回收的账户数据
     *
     * @return 已回收的数据
     */
    public ArrayList<Account> getRecycleAccounts() {
        ArrayList<Account> allAccounts = new ArrayList<>();
        Cursor cursor = accountsReader.query("accounts",
                null, "isDelete = ? ", new String[]{Integer.toString(1)},
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

    /**
     * 根据id获取账户数据
     *
     * @param id id值
     * @return account对象
     */
    @SuppressLint("Range")
    public Account getAccount(int id) {
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

    /**
     * 获取Tag模糊查询账户数据
     *
     * @param tag    标签字符串
     * @param remark 备注字符串
     * @return 数据列表
     */
    public ArrayList<Account> getAccountsByTag(String tag, String remark) {
        ArrayList<Account> accountsList = new ArrayList<>();

        Cursor cursor = accountsReader.query("accounts",
                null, "(tag like ? or remark like ?) and (isDelete <> ?) ",
                new String[]{"%" + tag + "%", "%" + remark + "%", Integer.toString(1)},
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

    /**
     * 保存账户对象
     *
     * @param account 对象
     * @return 执行状态
     */
    public boolean saveAccount(Account account) {
        if (account.getTag().isEmpty()) {
            Toaster.warm("未填写账户标签!");
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
            Toaster.error("账户添加失败!!!");
            return false;
        }
        Toaster.success("账户添加成功");
        return true;
    }

    /**
     * 批量保存账户对象, 适配文本解析
     *
     * @param accounts 对象列表
     * @return 执行状态
     */
    public int saveAccountBatch(List<Account> accounts) {
        int batchNum = accounts.size();
        for (Account account : accounts) {
            ContentValues accountValues = new ContentValues();
            accountValues.put("tag", account.getTag());
            accountValues.put("name", account.getName());
            accountValues.put("password", account.getPassword());
            accountValues.put("remark", "");
            accountValues.put("weight", 2);
            accountValues.put("time", System.currentTimeMillis());
            accountValues.put("isDelete", 0);
            accountValues.put("priority", 1);
            long flag = accountsWriter.insert("accounts", null, accountValues);
            if (-1 == flag) {
                batchNum--;
            }
        }
        if (batchNum >= 0) {
            Toaster.success("数据添加成功" + batchNum + "条, "
                    + "失败" + (accounts.size() - batchNum) + "条!");
        } else {
            Toaster.error("数据添加失败! status = " + batchNum);
        }
        return batchNum;
    }

    /**
     * 根据id更新账户
     *
     * @param account 账户对象
     * @return 执行状态
     */
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
                Toaster.success("账户更新成功!");
            }
        } catch (Exception e) {
            Toaster.error("数据异常!\n更新出错了! account = " + account);
            return false;
        }
        return true;
    }

    /**
     * 根据id回收账户
     *
     * @param id 账户id
     * @return 执行状态
     */
    public boolean recyclerAccount(int id) {
        ContentValues accountUpdateValues = new ContentValues();
        accountUpdateValues.put("isDelete", 1);
        try {
            int flag = accountsWriter.update("accounts", accountUpdateValues,
                    "id = ?", new String[]{Integer.toString(id)});
            if (1 == flag) {
                Toaster.success("账户移除成功!");
            }
        } catch (Exception e) {
            Toaster.warm("数据异常!\n删错出错了! id = " + id);
            return false;
        }
        return true;
    }

    /**
     * 根据id恢复已回收的账户
     *
     * @param id id值
     * @return 执行状态
     */
    public boolean restoreAccount(int id) {
        ContentValues accountUpdateValues = new ContentValues();
        accountUpdateValues.put("isDelete", 0);
        try {
            int flag = accountsWriter.update("accounts", accountUpdateValues,
                    "id = ?", new String[]{Integer.toString(id)});
            if (1 == flag) {
                Toaster.success("账户恢复成功!");
            }
        } catch (Exception e) {
            Toaster.warm("数据异常!\n恢复出错了! id = " + id);
            return false;
        }
        return true;
    }

    /**
     * 根据id彻底删除账户
     *
     * @param id id值
     * @return 执行状态
     */
    public boolean deleteAccount(int id) {
        int flag = accountsWriter.delete("accounts", "id = ?", new String[]{String.valueOf(id)});
        if (1 == flag) {
            Toaster.success("账户已删除!");
        }
        return true;
    }
}
