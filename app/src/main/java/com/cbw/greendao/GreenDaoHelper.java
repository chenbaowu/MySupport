package com.cbw.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by cbw on 2019/1/9.
 */
public class GreenDaoHelper {

    /**
     * 数据库名
     */
    public final static String DB_NAME = "CamHomePay.db";

    private static GreenDaoHelper mDaoHelper;
    private DaoSession mDaoSession;

    private UserGoodsDaoManage mUserGoodsDaoManage;

    public static GreenDaoHelper getInstance() {

        if (mDaoHelper == null) {
            synchronized (GreenDaoHelper.class) {
                if (mDaoHelper == null) {
                    mDaoHelper = new GreenDaoHelper();
                }
            }
        }
        return mDaoHelper;
    }

    public void init(Context context) {
        if (mDaoSession != null) {
            return;
        }
        DbOpenHelper dbOpenHelper = new DbOpenHelper(context, DB_NAME);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        mUserGoodsDaoManage = new UserGoodsDaoManage(mDaoSession.getUserGoodsDao());
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public UserGoodsDaoManage getUserGoodsDaoManage() {
        return mUserGoodsDaoManage;
    }

    public void clearAllData(boolean reBuildTables) {
        if (reBuildTables) {
            DaoMaster.dropAllTables(mDaoSession.getDatabase(), false);
            DaoMaster.createAllTables(mDaoSession.getDatabase(), true);
        } else {
            mUserGoodsDaoManage.deleteData(null, null, null);
        }
    }
}
