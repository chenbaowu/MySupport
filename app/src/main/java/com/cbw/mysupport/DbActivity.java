package com.cbw.mysupport;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.cbw.bean.User;
import com.cbw.greendao.DaoMaster;
import com.cbw.greendao.DaoSession;
import com.cbw.utils.OnAnimatorTouchListener;

import java.util.ArrayList;

/**
 * Created by cbw on 2018/11/15.
 */
public class DbActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        init();
    }

    private void init() {
        initGreenDao();

        this.findViewById(R.id.btn_add).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_delete).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_update).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_find).setOnTouchListener(animatorTouchListener);
    }

    /**
     * 初始化GreenDao
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "aserbao.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    private long i = 0;

    private OnAnimatorTouchListener animatorTouchListener = new OnAnimatorTouchListener() {
        @Override
        public void onActionClick(View v) {

            switch (v.getId()) {
                case R.id.btn_add:
                    try {
                        User user = new User();
                        user.setId(i++);
                        user.setStudentNo((int) i++);
                        user.setName("abc");
                        daoSession.insert(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.btn_delete:
                    daoSession.getUserDao().deleteAll();
                    break;
                case R.id.btn_update:
                    break;
                case R.id.btn_find:
                    ArrayList<User> users = (ArrayList<User>) daoSession.getUserDao().loadAll();
                    for (int j = 0; j < users.size(); j++) {
                        Log.i("bbb", "user: " + users.get(j).toString());
                    }
                    break;
            }
        }
    };
}
