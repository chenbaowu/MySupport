package com.cbw.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cbw.bean.AlbumBean;
import com.cbw.utils.AlbumUtils;
import com.cbw.view.fingerMove.ImageShowView;

import java.util.ArrayList;
import java.util.LinkedList;



/**
 * Created by cbw on 2017/5/24.
 */

public class MyPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LinkedList<View> mViewCache = null;

    public ArrayList<AlbumBean> m_itemInfors;

    public MyPagerAdapter(Context context) {
        this.mContext = context;
        mViewCache = new LinkedList<>();

        m_itemInfors = AlbumUtils.mAlbumBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (m_itemInfors != null) {
            return m_itemInfors.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;//官方推荐写法
    }

    @Override
    public int getItemPosition(Object object) {

//        return POSITION_NONE;  // 不缓存模式

        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageShowView imageShowView = null;
        if (mViewCache.size() == 0) {

            imageShowView = new ImageShowView(mContext);
        } else {
            imageShowView = (ImageShowView) mViewCache.removeFirst();
        }
        AlbumBean albumBean = m_itemInfors.get(position);
        imageShowView.setImage(albumBean.mImagePath);

        container.addView(imageShowView);
        return imageShowView;
    }

    public static class ItemView extends FrameLayout {

        public ItemView(@NonNull Context context) {
            super(context);
        }

        public ImageShowView imageShowView;
    }

    private void setItemData(ViewHolder viewHolder, int position) {

        AlbumBean albumBean = m_itemInfors.get(position);

        viewHolder.imageShowView.setImage();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        if (object != null) {

            View contentView = (View) object;
            container.removeView(contentView);
            container.clearDisappearingChildren();
            mViewCache.add(contentView);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return null;
    }

    public final class ViewHolder {
        public ImageShowView imageShowView;
    }
}
