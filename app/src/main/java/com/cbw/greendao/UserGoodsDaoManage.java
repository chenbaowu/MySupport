package com.cbw.greendao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cbw.bean.UserGoods;

import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;


/**
 * Created by cbw on 2019/1/11.
 * 用户商品关系表管理类
 */
public class UserGoodsDaoManage {

    private UserGoodsDao mUserGoodsDao;

    public UserGoodsDaoManage(UserGoodsDao userGoodsDao) {
        mUserGoodsDao = userGoodsDao;
    }

    public ArrayList<UserGoods> findData(@Nullable String userId, @Nullable String goodsId) {

        return findData(userId, goodsId, null, null, null, null);
    }

    public ArrayList<UserGoods> findDataPayStatus(@Nullable String userId, @Nullable String payStatus) {

        return findData(userId, null, null, payStatus, null, null);
    }

    public ArrayList<UserGoods> findDataPayWay(@Nullable String userId, @Nullable String payWay) {

        return findData(userId, null, null, null, payWay, null);
    }

    public UserGoods findUniqueData(@NonNull String userId, @NonNull String goodsId) {

//        if (userId == null || goodsId == null) return null;

        QueryBuilder<UserGoods> queryBuilder = mUserGoodsDao.queryBuilder();
        queryBuilder.where(UserGoodsDao.Properties.UserID.eq(userId));
        queryBuilder.where(UserGoodsDao.Properties.GoodsID.eq(goodsId));

        return queryBuilder.build().forCurrentThread().unique();
    }

    public UserGoods findUniqueArticle(@NonNull String userId, @NonNull String articleId) {

//        if (userId == null || goodsId == null) return null;

        QueryBuilder<UserGoods> queryBuilder = mUserGoodsDao.queryBuilder();
        queryBuilder.where(UserGoodsDao.Properties.UserID.eq(userId));
        queryBuilder.where(UserGoodsDao.Properties.ArticleID.eq(articleId));

        return queryBuilder.build().forCurrentThread().unique();
    }

    public ArrayList<UserGoods> findData(@Nullable String userId,
                                         @Nullable String goodsId,
                                         @Nullable String articleId,
                                         @Nullable String payStatus,
                                         @Nullable String payWay,
                                         @Nullable String lookTipStatus) {

        QueryBuilder<UserGoods> queryBuilder = mUserGoodsDao.queryBuilder();

        if (userId != null) {
            queryBuilder.where(UserGoodsDao.Properties.UserID.eq(userId));
        }
        if (goodsId != null) {
            queryBuilder.where(UserGoodsDao.Properties.GoodsID.eq(goodsId));
        }
        if (articleId != null) {
            queryBuilder.where(UserGoodsDao.Properties.ArticleID.eq(articleId));
        }
        if (payStatus != null) {
            queryBuilder.where(UserGoodsDao.Properties.PayStatus.eq(payStatus));
        }
        if (payWay != null) {
            queryBuilder.where(UserGoodsDao.Properties.PayWay.eq(payWay));
        }
        if (lookTipStatus != null) {
            queryBuilder.where(UserGoodsDao.Properties.LookTipStatus.eq(lookTipStatus));
        }

        return (ArrayList<UserGoods>) queryBuilder.build().forCurrentThread().list();
    }

    public void insertOrUpdateData(UserGoods userGoods) {
        if (checkData(userGoods)) {
            UserGoods mDbData = findUniqueData(userGoods.getUserID(), userGoods.getGoodsID());

            if (mDbData != null && mDbData.getUserID().equals(userGoods.getUserID()) && mDbData.getGoodsID().equals(userGoods.getGoodsID())) {
                mDbData.copy(userGoods);
                update(mDbData);
            } else {
                insert(userGoods);
            }
        }
    }

    public void deleteData(@Nullable String userId, @Nullable String goodsId, @Nullable String articleId) {
        if (userId == null && goodsId == null && articleId == null) {
            mUserGoodsDao.deleteAll();
            return;
        }

        ArrayList<UserGoods> userGoodsArrayList = findData(userId, goodsId, articleId, null, null, null);
        for (UserGoods userGoods : userGoodsArrayList) {
            delete(userGoods);
        }
    }

    public void insert(UserGoods userGoods) {
        if (checkData(userGoods)) {
            mUserGoodsDao.insert(userGoods);
        }
    }

    public void update(UserGoods userGoods) {
        if (checkData(userGoods)) {
            mUserGoodsDao.update(userGoods);
        }
    }

    public void delete(UserGoods userGoods) {
        if (checkData(userGoods)) {
            mUserGoodsDao.delete(userGoods);
        }
    }

    private boolean checkData(UserGoods userGoods) {
        return userGoods != null && userGoods.getUserID() != null && userGoods.getGoodsID() != null;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PayWay {
        // 支付方式

        int AliPay = 1;
        int WeiChat = 2;
        int Huawei = 5;
        int Google = 6;
        int Oppo = 7;
        int Share = 9;      // 限免分享
        int HandSel = 10;   // 赠送
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PayStatus {
        String UnPaid = "0";           //  未支付
        String NeedConfirm = "1";      // 已支付待确认
        String Ok = "2";               // 支付完成（或者限免分享解锁成功）
        String Failure = "3";          // 支付失败
        String Refund = "4";           // 退款状态
        String Shareable = "5";        // 可分享解锁，限免时间内 ，还没开始倒计时
        String ShareTimeIng = "6";     // 可分享解锁，限免时间内 ,已经开始倒计时
        String ShareExpire = "7";      // 限免过期
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PayProcess {
        // 支付流程

        int Normal = 1 << 2;
        int CreteOrderIng = 1 << 3;       // 创建订单中
        int CreteOrderFailure = 1 << 4;   // 创建订单失败 （未安装微信）
        int PayIng = 1 << 5;              //  支付中
        int PayOK = 1 << 6;               //  支付成功
        int PayFailure = 1 << 7;          //  支付失败
        int PayCancel = 1 << 8;           //  支付取消
        int PayNetworkInvalid = 1 << 9;   //  断网
        int TokenExpired = 1 << 10;       //  token失效
        int RePay = 1 << 11;              // 重复购买
        int NotInstallWeiChat = 1 << 12;  // 未安装微信
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface LookTipStatus {
        // 分享解锁素材的展示状态（也就是说是否点开过这个goods）

        String NoneShow = "1";    // 需要显示提示，且没有展示过
        String NeedTip = "2";    // 需要显示提示，且曾经展示过（或者已经记录到SP），但是没有点开
        String UnTip = "3";      // 不需要显示提示（已经点开过）
    }
}
