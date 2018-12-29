package com.cbw.mysupport;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cbw.base.BaseActivity;
import com.cbw.utils.OnAnimatorTouchListener;

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
        this.findViewById(R.id.tv_show).setOnTouchListener(animatorTouchListener);
    }

    private static final int intervalMillis = 60 * 1000;
    private long triggerAtMillis;
    private NotificationManager mNotificationManager;

    OnAnimatorTouchListener animatorTouchListener = new OnAnimatorTouchListener() {
        @Override
        public void onActionClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add:

//                    Context context = null;
//                    try {
//                        context = createPackageContext(getPackageName(), Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                        context = mContext;
//                    }

                    triggerAtMillis = System.currentTimeMillis();
                    AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(mContext, AlarmReceiver.class);
//                    intent.setPackage(getPackageName());
//                    intent.setFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);

                    PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, sender);

                    break;
                case R.id.btn_delete:
                    if (mNotificationManager == null) {
                        /*创建一个NotificationManager的引用*/
                        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    }

                    /*处理点击Notification的逻辑*/
                    Intent resultIntent = new Intent(mContext, FingerMoveViewActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resultIntent.putExtra("what", 5);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 5, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    /*定义Notification的各种属性*/
                    Notification.Builder mBuilder = new Notification.Builder(mContext.getApplicationContext())
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)        //声音uri
                            .setSmallIcon(R.mipmap.ic_launcher)                                         //设置通知的图标
                            .setTicker("有福利")                                                        //设置状态栏的标题
                            .setContentTitle("帅哥")                                                    //设置标题
                            .setContentText("你寂寞么")                                               //消息内容
                            .setDefaults(Notification.DEFAULT_ALL)                                      //设置默认的提示音
                            .setPriority(Notification.PRIORITY_HIGH)                                 //设置该通知的优先级
                            // 无效 .setOngoing(false)                                                 //让通知左右滑的时候不能取消通知
                            .setWhen(System.currentTimeMillis())                                      //设置通知时间，默认为系统发出通知的时间，通常不用设置
                            .setAutoCancel(true);                                                       //打开程序后图标消失
//                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setFullScreenIntent(resultPendingIntent,false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
                    }

                    final Notification notification = mBuilder.build();
                    notification.flags |= Notification.FLAG_NO_CLEAR; // LAG_NO_CLEAR 表示该通知不能被状态栏的清除按钮给清除掉,也不能被手动清除,但能通过 cancel() 方法清除
                    notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//                    notification.ledARGB = Color.BLUE;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
//                    notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
//                    notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位

                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNotificationManager.notify(1, notification); // 发送 , 结束广播 mNotificationManager.cancel(1);
                        }
                    }, 1500);
                    break;
                case R.id.btn_update:
                    Intent intent3 = new Intent();
                    intent3.setAction("my.alarm");
                    Bundle bundle = new Bundle();
                    bundle.putString("name", "cbw");
                    intent3.putExtra("data", bundle);
                    sendBroadcast(intent3);
                    break;
                case R.id.btn_find:
                    /*协议唤醒*/
                    Uri uri = Uri.parse("cbw://support.goto/album/go"); // camhomme://goto?type=inner_app&pid=1340126&is_struct=1&struct_theme_id=38933&struct_id=54580
                    Intent intent4 = new Intent();
                    intent4.setData(uri);

                    /*包名唤醒*/
//                    PackageManager packageManager = getPackageManager();
//                    Intent intent4 = packageManager.getLaunchIntentForPackage("com.cbw.mygl");

                    /* 指定打开的activity*/
//                    ComponentName componentName = new ComponentName("com.cbw.mygl", "com.cbw.mygl.MainActivity");
//                    Intent intent4 = new Intent();
//                    intent4.setComponent(componentName);

                    Bundle bundle4 = new Bundle();
                    bundle4.putString("cbw", "cbw");
                    intent4.putExtra("cbw", bundle4);
                    intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent4);

                    break;
                case R.id.tv_show:
                    startService(new Intent(mContext, GrayService.class));
                    break;
            }
        }
    };

    private static int count;

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "i am alarm", Toast.LENGTH_LONG).show();
            Log.i("bbb", "onReceive: " + count);

            if (++count > 100) {
                cancelAlarm(context);
                Log.i("bbb", "AlarmManager Cancel: ");
            }
        }
    }

    private static void cancelAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
    }

    public static class GrayService extends Service {

        private final static int GRAY_SERVICE_ID = 1001;

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            if (Build.VERSION.SDK_INT < 18) {
                startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
            } else {
                Intent innerIntent = new Intent(this, GrayInnerService.class);
                startService(innerIntent);
                startForeground(GRAY_SERVICE_ID, new Notification());
            }

            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * 给 API >= 18 的平台上用的灰色保活手段
         */
        public static class GrayInnerService extends Service {

            @Override
            public int onStartCommand(Intent intent, int flags, int startId) {
                startForeground(GRAY_SERVICE_ID, new Notification());
                stopForeground(true);
                stopSelf();
                return super.onStartCommand(intent, flags, startId);
            }

            @Override
            public IBinder onBind(Intent intent) {
                return null;
            }

        }
    }

}

