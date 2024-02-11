package com.passwordnotes.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import com.passwordnotes.dao.Account;
import com.passwordnotes.dao.AccountMapper;
import com.passwordnotes.utils.toaster.Toaster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class DataProcess {
    private Context context;
    public SQLiteDatabase reader;
    public SQLiteDatabase writer;
    public SQLiteOpenHelper DB;

    private AccountMapper accountMapper;

    // 初始化资源
    public DataProcess(Context context) {
        this.context = context;
        accountMapper = new AccountMapper(context);
        DB = accountMapper.DB;
        reader = accountMapper.accountsReader;
        writer = accountMapper.accountsWriter;
    }

    public static String getTime() {
        Long time = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HH_mm_ss");
        return format.format(time);
    }

    /**
     * 在指定路径进行数据备份
     *
     * @param uri 用户选择的路径
     */
    public void dataBaseBackup(Uri uri) {
        File dbFile = new File(reader.getPath());
        try {
            FileInputStream dbInputStream = new FileInputStream(dbFile);

            DocumentFile pickedDir = DocumentFile.fromTreeUri(context, uri);
            assert pickedDir != null;
            DocumentFile file = pickedDir.createFile(
                    null,
                    getTime() + "-" + accountMapper.getAllAccounts().size() + ".db");

            OutputStream dbOutputStream = context.getContentResolver().openOutputStream(file.getUri());

            byte[] buffer = new byte[32];
            int temp;
            while ((temp = dbInputStream.read(buffer)) != -1) {  // 读
                dbOutputStream.write(buffer, 0, temp);   // 写
            }
            Toaster.success("数据文件备份完成! 请检查文件夹内数据文件!");
            dbOutputStream.flush();
            dbInputStream.close();
            dbOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.error("出错了!" + e.getMessage());
        }
    }


    /**
     * 选择数据文件来恢复数据库
     *
     * @param uri 数据文件地址
     */
    public void dataBaseRestore(Uri uri) {
        File dbFile = new File(reader.getPath());
        try {
            InputStream dbInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream dbOutputStream = new FileOutputStream(dbFile);

            if (dbInputStream != null) {
                byte[] buffer = new byte[32];
                int temp;
                while ((temp = dbInputStream.read(buffer)) != -1) {  // 读
                    dbOutputStream.write(buffer, 0, temp);   // 写
                }
                dbInputStream.close();
            }
            dbOutputStream.flush();
            dbOutputStream.close();
            Toaster.success("数据文件恢复完成, 请重新刷新数据!");
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.error("数据恢复失败!" + e.getMessage());
        }
    }

    /**
     * 将列表的账户信息导出为txt文档
     *
     * @param uri 导出文件夹
     */
    public void exportAccountsData(Uri uri) {
        List<Account> accounts = accountMapper.getAllAccounts();
        try {
            DocumentFile pickedDir = DocumentFile.fromTreeUri(context, uri);
            assert pickedDir != null;
            DocumentFile file = pickedDir.createFile(
                    "text/plain",
                    getTime() + "-" + accountMapper.getAllAccounts().size() + ".txt");

            OutputStream outputStream = context.getContentResolver().openOutputStream(file.getUri(), "wa");

            String info = "!注: 第一行为账户标签\n第二行为账号(可能为空)\n第三行为密码(可能为空)\n";
            byte[] buffer = info.getBytes();
            outputStream.write(buffer);
            outputStream.flush();

            try {
                for (Account account : accounts) {
                    outputStream.write(
                            ("\n" + account.getTag() + "\n" +
                                    account.getName() + "\n" +
                                    account.getPassword() + "\n").getBytes()
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toaster.success("账户数据导出完成! 请检查文件夹内文本文件!");
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.error("出错了!" + e.getMessage());
        }

    }
}
