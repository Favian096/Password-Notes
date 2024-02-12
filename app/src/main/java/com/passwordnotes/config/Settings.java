package com.passwordnotes.config;

import android.content.Context;

import com.passwordnotes.dao.AccountMapper;
import com.passwordnotes.utils.toaster.Toaster;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings {
    Context context;
    public static JSONObject settings;
    private AccountMapper accountMapper;

    /**
     * 首页列表是否直接显示出密码
     */
    public static boolean showItemListPassword = true;

    public Settings(Context context) {
        this.context = context;
    }

    /**
     * 初始化设置数据
     */
    public void init() {
        accountMapper = new AccountMapper(this.context);
        try {
            settings = new JSONObject(accountMapper.getAccount(0).getRemark());
            //*******************************************************************************
            if (settings.isNull("showItemListPassword")) {
                settings.put("showItemListPassword", showItemListPassword);
            }

            // 初始化全部设置数据
            showItemListPassword = settings.optBoolean("showItemListPassword");

            //*******************************************************************************
        } catch (JSONException ignore) {
        }

    }

    /**
     * 应用数据改变
     */
    public static void notifySettingsChanged() {
        try {
            settings.put("showItemListPassword", showItemListPassword);

            Toaster.success("设置已更新!\n回到首页, 写入数据以生效!");
        } catch (JSONException e) {
            Toaster.error("出错了! error = " + e.getMessage());
        }
    }

}
