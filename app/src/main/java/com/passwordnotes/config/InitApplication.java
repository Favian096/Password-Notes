package com.passwordnotes.config;

import android.app.Application;

import com.passwordnotes.utils.toaster.Toaster;

public class InitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*初始化Toaster*/
        Toaster.init(this);

        /*初始化设置项*/
        Settings settings = new Settings(this);
        settings.init();
    }

}
