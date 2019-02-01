package com.cbw.App;

import android.app.Application;
import android.util.Log;

import com.cbw.greendao.GreenDaoHelper;
import com.cbw.utils.ShareData;
import com.cbw.utils.Utils;

/**
 * Created by cbw on 2017/11/23.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("bbb", "Application onCreate: " + android.os.Process.myPid());

        String mCurrentProcessName = Utils.GetProcessName(this);
        if (mCurrentProcessName != null && mCurrentProcessName.equals(getPackageName())) {
            Log.i("bbb", "这是主进程");
            GreenDaoHelper.getInstance().init(this);
        }
        ShareData.InitData(this);
    }
}
