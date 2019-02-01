package com.cbw.mysupport;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
                    final Notification.Builder mBuilder = new Notification.Builder(mContext.getApplicationContext())
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
                    mBuilder.setFullScreenIntent(resultPendingIntent, false);
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

                    getTask();


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
                case R.id.btn_find:
//                    Intent intent3 = new Intent();
//                    intent3.setAction("my.alarm");
//                    Bundle bundle = new Bundle();
//                    bundle.putString("name", "cbw");
//                    intent3.putExtra("data", bundle);
//                    sendBroadcast(intent3);

                    startService(new Intent(mContext, ServiceTest.class));
                    break;
                case R.id.tv_show:
//                    startService(new Intent(mContext, GrayService.class));

                    bindService(new Intent(mContext, ServiceTest.class), new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
//                            myBinder = (ServiceTest.MyBinder) service;
//                            myBinder.startBinder();

//                            if (mReceiverReplyMsg == null) {
//                                mReceiverReplyMsg = new Messenger(new MyHandler());
//                            }
//                            try {
//                                Messenger messenger = new Messenger(service);
//                                Message msg = Message.obtain(null, 0);
//                                //把接收服务器端的回复的Messenger通过Message的replyTo参数传递给服务端
//                                msg.replyTo = mReceiverReplyMsg;
//                                messenger.send(msg);
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }

                            try {
                                myAIDLService = (IMyAidlInterface) IMyAidlInterface.Stub.asInterface(service);
                                myAIDLService.sayWhat();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {

                        }
                    }, Service.BIND_AUTO_CREATE);
                    break;
            }
        }
    };

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.i("bbb", "receive message from service: " + msg.getData().getString("cbw"));
                    break;
            }
        }
    }

    /**
     * 用于接收服务器返回的信息
     */
    private Messenger mReceiverReplyMsg;

    private ServiceTest.MyBinder myBinder;

    private IMyAidlInterface myAIDLService;

    /**
     * service aidl
     */
    public static class ServiceTest extends Service {

        MyBinder myBinder = new MyBinder(); // 同一进程使用

        Messenger messenger; // 不同一进程使用

        /**
         * 用于接收从客户端传递过来的数据
         */
        public static class IpcHandle extends Handler {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Log.i("bbb", "receive message from client: ");

                        // 回复客户端消息
                        Messenger client = msg.replyTo;
                        Message message = Message.obtain(null, 0);
                        Bundle bundle = new Bundle();
                        bundle.putString("cbw", "008");
                        message.setData(bundle);
                        try {
                            client.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }

        IMyAidlInterface.Stub myAidlInterface = new IMyAidlInterface.Stub() {
            @Override
            public void sayWhat() throws RemoteException {
                Log.i("bbb", "i am Aidl");
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();
            Log.i("bbb", "Service onCreate: ");
            messenger = new Messenger(new IpcHandle());

            Intent intent = new Intent(this, AlbumActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public IBinder onBind(Intent intent) {
//            return myBinder;
//            return messenger.getBinder();
            Log.i("bbb", "Service onBind: ");
            return myAidlInterface;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i("bbb", "Service onStartCommand: ");
            return START_REDELIVER_INTENT;
        }

        @Override
        public void onDestroy() {
            Log.i("bbb", "Service onDestroy: ");
            super.onDestroy();
        }

        public class MyBinder extends Binder {

            // 返回当前对象ServiceTest,这样我们就可在客户端端调用Service的公共方法了
            public ServiceTest startBinder() {
                Log.i("bbb", "startBinder: ");
                return ServiceTest.this;
            }
        }
    }

    /**
     * 保活方式，前台进程
     */
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

}

