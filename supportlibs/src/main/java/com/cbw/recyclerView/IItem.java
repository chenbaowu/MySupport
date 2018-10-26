package com.cbw.recyclerView;

/**
 * Created by cbw on 2018/10/25.
 *
 * UI接口
 */
public interface IItem {

    /**
     * 设置数据
     */
    void setData(BaseItemInfo itemInfo,int position);

    /**
     *  初始化ui
     */
    void initView();

    /**
     * 选中
     */
    void onSelected();

    /**
     * 取消选中
     */
    void onUnSelected();

    /**
     * 点击
     */
    void onClick();

}
