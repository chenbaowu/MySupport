package com.cbw.greendao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * Created by cbw on 2019/1/10.
 */
public class DbOpenHelper extends DaoMaster.OpenHelper {

    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
//        MigrationHelper.getInstance().migrate(db, UserPaidGoodsDao.class);
    }
}
