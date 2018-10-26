package com.cbw.recyclerView;

/**
 * Created by cbw on 2018/10/25.
 */
public class BaseItemInfo implements IItemInfo {

    public int mItemType;
    public int mResId;
    public Object mRes;
    public boolean mIsClickable = true;

    @Override
    public int getItemType() {
        return mItemType;
    }

    @Override
    public int getResId() {
        return mResId;
    }

    @Override
    public Object getRes() {
        return mRes;
    }

    @Override
    public boolean getIsClickable() {
        return mIsClickable;
    }
}
