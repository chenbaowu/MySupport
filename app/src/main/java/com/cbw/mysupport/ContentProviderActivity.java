package com.cbw.mysupport;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.cbw.base.BaseActivity;
import com.cbw.utils.OnAnimatorTouchListener;

/**
 * Created by cbw on 2018/12/29.
 */
public class ContentProviderActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
    }

    private ContentResolver mContentResolver;
    private ContentObserver mContentObserver;
    private Uri uri_user;

    private void init() {
        this.findViewById(R.id.btn_add).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_delete).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_update).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_find).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.tv_show).setOnTouchListener(animatorTouchListener);

        mContentResolver = getContentResolver();
        uri_user = Uri.parse("content://com.cbw.support/user");

        // withAppendedId（）作用：向URI追加一个id
        Uri resultUri = ContentUris.withAppendedId(uri_user, 7); // 最终生成后的Uri为：content://com.cbw.support/user/7
        // parseId（）作用：从URL中获取ID
        long parseId = ContentUris.parseId(resultUri); //获取的结果为:7

        // 增、删 & 改
        mContentObserver = new ContentObserver(mUiHandler) {
            @Override
            public boolean deliverSelfNotifications() {
                Log.i("bbb", "deliverSelfNotifications: ");
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.i("bbb", "onChange: " + uri);
            }
        };
        mContentResolver.registerContentObserver(uri_user, false, mContentObserver);
    }

    OnAnimatorTouchListener animatorTouchListener = new OnAnimatorTouchListener() {
        @Override
        public void onActionClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add:

                    ContentValues values = new ContentValues();
                    values.put("name", "cbw");
                    mContentResolver.insert(uri_user, values);

                    Cursor cursor = mContentResolver.query(uri_user, null, null, null, null);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            Log.i("bbb", "query: " + cursor.getInt(0) + " " + cursor.getString(1));
                            // 将表中数据全部输出
                        }
                        cursor.close();
                    }
                    break;
                case R.id.btn_delete:
                    Intent intent = new Intent(mContext, DbActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    public static class DBHelper extends SQLiteOpenHelper {

        // 数据库名
        private static final String DATABASE_NAME = "cbw.db";

        // 表名
        public static final String USER_TABLE_NAME = "user";
        public static final String JOB_TABLE_NAME = "job";

        private static final int DATABASE_VERSION = 1;
        //数据库版本号

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // 创建两个表格:用户表 和职业表
            db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + " name TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + JOB_TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + " job TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class MyContentProvider extends ContentProvider {

        private Context mContext;
        DBHelper mDbHelper = null;
        SQLiteDatabase db = null;

        public static final int User_Code = 1;
        public static final int Job_Code = 2;

        // UriMatcher类使用:在ContentProvider 中注册URI
        private static final UriMatcher mMatcher;

        static {
            mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            mMatcher.addURI(MyAuthority, "user", User_Code); // 注册 uri ，设置 uriMatcher.match(uri) 的返回码
            mMatcher.addURI(MyAuthority, "job", Job_Code);
            Log.i("bbb", "add uri");
        }

        @Override
        public boolean onCreate() {
            mContext = getContext();
            // 在ContentProvider创建时对数据库进行初始化
            // 运行在主线程，故不能做耗时操作,此处仅作展示
            mDbHelper = new DBHelper(getContext());
            db = mDbHelper.getWritableDatabase();

            // 初始化两个表的数据(先清空两个表,再各加入一个记录)
            db.execSQL("delete from user");
            db.execSQL("insert into user values(1,'Carson');");
            db.execSQL("insert into user values(2,'Kobe');");

            db.execSQL("delete from job");
            db.execSQL("insert into job values(1,'Android');");
            db.execSQL("insert into job values(2,'iOS');");

            return false;
        }

        @Nullable
        @Override
        public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

            String table = getType(uri);

            return db.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
        }

        @Nullable
        @Override
        public String getType(@NonNull Uri uri) {
            String tableName = null;
            switch (mMatcher.match(uri)) {
                case User_Code:
                    tableName = DBHelper.USER_TABLE_NAME;
                    break;
                case Job_Code:
                    tableName = DBHelper.JOB_TABLE_NAME;
                    break;
            }
            return tableName;
        }

        @Nullable
        @Override
        public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

            String table = getType(uri);

            // 向该表添加数据
            db.insert(table, null, values);

            // 当该URI的ContentProvider数据发生变化时，通知外界（即访问该ContentProvider数据的访问者）
            mContext.getContentResolver().notifyChange(uri, null);

            return uri;
        }

        @Override
        public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
            return 0;
        }

        @Override
        public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
            return 0;
        }
    }

    @Override
    protected void onDestroy() {
        mContentResolver.unregisterContentObserver(mContentObserver);
        super.onDestroy();
    }
}
