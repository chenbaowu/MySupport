package com.cbw.bean;

import android.support.annotation.NonNull;

import com.cbw.greendao.UserGoodsDaoManage;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by cbw on 2019/1/9.
 * 用户商品关系表
 * 手机的IMEI作为一个特殊的userID
 * <p>
 * {@link UserGoods#copy(UserGoods)} 一定要注意这个方法如果你修改属性
 */
@Entity
public class UserGoods {

    @Id(autoincrement = true)
    private Long id;

    private String orderCode;

    @NotNull
    @Unique
    private String userID;

    @NotNull
    @Unique
    private String goodsID;

    private String articleID;

    /**
     * user和goods的关系
     */
    private @UserGoodsDaoManage.PayStatus
    String payStatus = UserGoodsDaoManage.PayStatus.UnPaid;

    /**
     * user解锁goods的方式
     */
    private @UserGoodsDaoManage.PayWay
    String payWay;

    /**
     * 限免过期时间
     */
    private long shareExpireTime;

    /**
     * 用于保存goods所关联的userID（对于手机这里的id是IMEI）
     */
    private String userLikeID;

    private String resType = "filter";

    /**
     * 仅用于提示user去打开由限免分享解锁的goods，点过就不再提示,具体根据状态的解释
     */
    @UserGoodsDaoManage.LookTipStatus
    private String lookTipStatus = UserGoodsDaoManage.LookTipStatus.NoneShow;


    @Generated(hash = 878145019)
    public UserGoods(Long id, String orderCode, @NotNull String userID,
            @NotNull String goodsID, String articleID, String payStatus, String payWay,
            long shareExpireTime, String userLikeID, String resType, String lookTipStatus) {
        this.id = id;
        this.orderCode = orderCode;
        this.userID = userID;
        this.goodsID = goodsID;
        this.articleID = articleID;
        this.payStatus = payStatus;
        this.payWay = payWay;
        this.shareExpireTime = shareExpireTime;
        this.userLikeID = userLikeID;
        this.resType = resType;
        this.lookTipStatus = lookTipStatus;
    }

    @Generated(hash = 367369332)
    public UserGoods() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return this.orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGoodsID() {
        return this.goodsID;
    }

    public void setGoodsID(String goodsID) {
        this.goodsID = goodsID;
    }

    public String getArticleID() {
        return this.articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getPayStatus() {
        return this.payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayWay() {
        return this.payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public long getShareExpireTime() {
        return this.shareExpireTime;
    }

    public void setShareExpireTime(long shareExpireTime) {
        this.shareExpireTime = shareExpireTime;
    }

    public String getUserLikeID() {
        return this.userLikeID;
    }

    public void setUserLikeID(String userLikeID) {
        this.userLikeID = userLikeID;
    }

    public String getResType() {
        return this.resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public String getLookTipStatus() {
        return this.lookTipStatus;
    }

    public void setLookTipStatus(String lookTipStatus) {
        this.lookTipStatus = lookTipStatus;
    }

    public void copy(UserGoods userGoods) {
        if (userGoods == null) return;

        if (userGoods.getUserID() != null) {
            this.userID = userGoods.getUserID();
        }

        if (userGoods.getGoodsID() != null) {
            this.goodsID = userGoods.getGoodsID();
        }

        if (userGoods.getArticleID() != null) {
            this.articleID = userGoods.getArticleID();
        }

        if (userGoods.getPayStatus() != null) {
            this.payStatus = userGoods.getPayStatus();
        }

        if (userGoods.getPayWay() != null) {
            this.payWay = userGoods.getPayWay();
        }

        if (userGoods.getShareExpireTime() != 0) {
            this.shareExpireTime = userGoods.getShareExpireTime();
        }

        if (userGoods.getUserLikeID() != null) {
            this.userLikeID = userGoods.getUserLikeID();
        }

        if (userGoods.getResType() != null) {
            this.resType = userGoods.getResType();
        }

        if (!userGoods.getLookTipStatus().equals(UserGoodsDaoManage.LookTipStatus.NoneShow)) {
            this.lookTipStatus = userGoods.getLookTipStatus();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "UserGoods{" +
                "id=" + id +
                ", orderCode='" + orderCode + '\'' +
                ", userID='" + userID + '\'' +
                ", goodsID='" + goodsID + '\'' +
                ", articleID='" + articleID + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", payWay='" + payWay + '\'' +
                ", shareExpireTime=" + shareExpireTime +
                ", userLikeID='" + userLikeID + '\'' +
                ", resType='" + resType + '\'' +
                ", lookTipStatus='" + lookTipStatus + '\'' +
                '}';
    }

}
