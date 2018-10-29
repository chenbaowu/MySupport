package com.cbw.bean;

/**
 * Created by cbw on 2017/12/20.
 */

public class AlbumBean {

    public int mImageID; // id
    public String mImagePath; // 地址
    public String mImageName; // 名字
    public Long mImageDate; // 日期
    public String mImagedDesc ; // 详细信息
    public int mImagedRotation ; // 旋转角度
    public Long mImagedSize ; // 大小

    @Override
    public String toString() {
        return "AlbumBean{" +
                "mImageID=" + mImageID +
                ", mImagePath='" + mImagePath + '\'' +
                ", mImageName='" + mImageName + '\'' +
                ", mImageDate=" + mImageDate +
                ", mImagedDesc='" + mImagedDesc + '\'' +
                ", mImagedRotation=" + mImagedRotation +
                ", mImagedSize=" + mImagedSize +
                '}';
    }
}
