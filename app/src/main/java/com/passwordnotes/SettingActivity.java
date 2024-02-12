package com.passwordnotes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.passwordnotes.config.Settings;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat hidden_item_list_password;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initData();
        initLayout();
        basicOnClickHandler();

    }

    /**
     * 监听点击事件
     */
    private void basicOnClickHandler() {
        // 给开关按钮设置监听状态改变事件
        hidden_item_list_password.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    Settings.showItemListPassword = isChecked;
                    Settings.notifySettingsChanged();
                    setResult(RESULT_OK, intent);
                }
        );

    }

    /**
     * 引导页面布局
     */
    private void initLayout() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_bar_setting);
            actionBar.setElevation(1);
        }

        hidden_item_list_password.setChecked(Settings.showItemListPassword);

    }

    /**
     * 初始化设置数据项
     */
    private void initData() {
        intent = new Intent();
        hidden_item_list_password = findViewById(R.id.setting_hidden_item_list_password);

    }
}