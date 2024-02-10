package com.passwordnotes.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import com.passwordnotes.dao.AccountMapper;
import com.passwordnotes.dao.SqlLiteDB;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
            Toast.makeText(context, "数据文件备份完成!", Toast.LENGTH_SHORT).show();
            dbOutputStream.flush();
            dbInputStream.close();
            dbOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "出错了!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, "数据文件恢复完成, 请刷新数据!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "数据恢复失败!", Toast.LENGTH_SHORT).show();
        }
    }

}
