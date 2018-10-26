package com.cbw.recyclerView;

/**
 * Created by cbw on 2018/10/25.
 *
 * 数据接口
 */
public interface IItemInfo {

    /**
     * 获取item类型
     *
     * @return
     */
    int getItemType();

    /**
     * 获取资源id
     *
     * @return
     */
    int getResId();

    /**
     * 资源对象
     *
     * @return
     */
    Object getRes();

    /**
     * 是否能点击
     *
     * @return
     */
    boolean getIsClickable();

}
