package com.cbw.mysupport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.cbw.utils.OnAnimatorTouchListener;

import java.util.Calendar;

/**
 * Created by cbw on 2018/12/11.
 */
public class AlarmManagerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        init();
    }

    private void init() {
        this.findViewById(R.id.btn_add).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_delete).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_update).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_find).setOnTouchListener(animatorTouchListener);
    }

    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h

    OnAnimatorTouchListener animatorTouchListener = new OnAnimatorTouchListener() {
        @Override
        public void onActionClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add:
                    // 设置一个每晚21:30唤醒的重复闹钟
                    AlarmManager am = (AlarmManager) mContext
                            .getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(mContext, RequestAlarmReceiver.class);
                    PendingIntent sender = PendingIntent.getBroadcast(mContext,
                            0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 18);
                    calendar.set(Calendar.MINUTE, 30);
                    calendar.set(Calendar.SECOND, 10);
                    calendar.set(Calendar.MILLISECOND, 0);

                    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            INTERVAL, sender);


                    break;
                case R.id.btn_delete:
                    break;
                case R.id.btn_update:
                    break;
                case R.id.btn_find:
                    break;
                case R.id.tv_show:
                    break;
            }
        }
    };

    public static class RequestAlarmReceiver {

    }
}
