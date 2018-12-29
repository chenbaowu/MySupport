package com.cbw.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.cbw.utils.ShareData;

/**
 * Created by cbw on 2018/12/10.
 */
public class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected String[] mPermission;
    protected Handler mUiHandler;

    public static final String MyAuthority = "com.cbw.support";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        ShareData.InitData(this);
        mUiHandler = new Handler(getMainLooper());
        checkPermission();
    }

    protected void checkPermission() {
        if (mPermission == null) {
            mPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        String[] mNeedRequestPermission = null;

        for (int i = 0; i < mPermission.length; i++) {
            if (ContextCompat.checkSelfPermission(this, mPermission[i]) != PackageManager.PERMISSION_GRANTED) {
                if (mNeedRequestPermission == null) {
                    mNeedRequestPermission = new String[mPermission.length];
                }
                mNeedRequestPermission[i] = mPermission[i];
            }
        }
        if (mNeedRequestPermission != null) {
            ActivityCompat.requestPermissions(this, mNeedRequestPermission, 0);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ShareData.m_HasNotch) {
            ShareData.setStatusBarColor(this, Color.TRANSPARENT);
        } else {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                System.exit(0);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
