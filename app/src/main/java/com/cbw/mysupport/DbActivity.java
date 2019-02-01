package com.cbw.mysupport;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.cbw.base.BaseActivity;
import com.cbw.bean.UserGoods;
import com.cbw.greendao.DaoMaster;
import com.cbw.greendao.DaoSession;
import com.cbw.greendao.GreenDaoHelper;
import com.cbw.utils.OnAnimatorTouchListener;

/**
 * Created by cbw on 2018/11/15.
 */
public class DbActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        init();
    }

    private TextView tv_show;

    private void init() {
        initGreenDao();

        this.findViewById(R.id.btn_add).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_delete).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_update).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_find).setOnTouchListener(animatorTouchListener);
        tv_show = this.findViewById(R.id.tv_show);
    }

    /**
     * 初始化GreenDao
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "bao.db");
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
//                        User user = new User();
//                        user.setId(i++);
//                        user.setStudentNo((int) i++);
//                        user.setName("abc");
//                        daoSession.insert(user);

                        UserGoods userGoods = new UserGoods();
                        userGoods.setUserID("cbw");
                        userGoods.setGoodsID("g1");
                        GreenDaoHelper.getInstance().getUserGoodsDaoManage().insert(userGoods);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_delete:
//                    daoSession.getUserDao().deleteAll();

//                    new RxJava2Test().init();

                    UserGoods userGoods = new UserGoods();
                    userGoods.setUserID("null");
                    try {
                        GreenDaoHelper.getInstance().getUserGoodsDaoManage().delete(userGoods);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_update:
                    try {
                        GreenDaoHelper.getInstance().getUserGoodsDaoManage().findUniqueData("cbw", "g1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_find:
                    GreenDaoHelper.getInstance().getUserGoodsDaoManage().findData(null, null);

//                    new RetrofitTest();
                    break;
            }
        }
    };
}
