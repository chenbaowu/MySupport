package com.cbw.App;

import android.app.Application;

import com.cbw.utils.ShareData;

/**
 * Created by cbw on 2017/11/23.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ShareData.InitData(this);
    }
}
