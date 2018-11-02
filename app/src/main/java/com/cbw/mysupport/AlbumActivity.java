package com.cbw.mysupport;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.cbw.Adapter.MyPagerAdapter;
import com.cbw.recyclerView.BaseAdapter;
import com.cbw.recyclerView.BaseItemInfo;
import com.cbw.recyclerView.UiConfig;
import com.cbw.utils.AlbumUtils;
import com.cbw.utils.PercentUtil;

import java.util.ArrayList;


/**
 * Created by cbw on 2017/11/23.
 */

public class AlbumActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        AlbumUtils.InitAlbumData(this);
        initView();
    }

    private void initView() {

        setContentView(R.layout.activity_album);

        initRcv();

        initViewPager();
    }

    private RecyclerView rcv_album;
    public BaseAdapter adapter_album;
    private RecyclerView.LayoutManager mLayoutManager;

    private void initRcv() {
        rcv_album = this.findViewById(R.id.rcv_album);
        rcv_album.setPadding(0, 0, 0, 0);
        rcv_album.setClipToPadding(false);

        UiConfig uiConfig = new UiConfig();
        uiConfig.item_w = PercentUtil.WidthPxxToPercent(350);
        uiConfig.item_h = PercentUtil.WidthPxxToPercent(350);

        adapter_album = new BaseAdapter(this, uiConfig);
        rcv_album.setAdapter(adapter_album);

        mLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rcv_album.setLayoutManager(mLayoutManager);

        rcv_album.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;
                int childCount = parent.getAdapter().getItemCount();
                int itemPosition = parent.getChildAdapterPosition(view);

                if (mLayoutManager instanceof GridLayoutManager) {
                    int spanCount = ((GridLayoutManager) mLayoutManager).getSpanCount();

                    if (itemPosition % spanCount == 0) { //第一列
                        left = PercentUtil.WidthPxxToPercent(15);
                    } else if ((itemPosition + 1) % spanCount == 0) { // 最后一列
                        right = PercentUtil.WidthPxxToPercent(15);
                    }
                }
                outRect.set(left, top, right, bottom);
            }
        });

        ArrayList<BaseItemInfo> baseItemInfoList = new ArrayList<>();
        for (
                int i = 0; i < AlbumUtils.mAlbumBeans.size(); i++)

        {
            BaseItemInfo baseItemInfo = new BaseItemInfo();
            baseItemInfo.mItemType = BaseAdapter.ItemType_Base;
//            baseItemInfo.mRes = AlbumUtils.mAlbumBeans.get(i).mImagePath;
            baseItemInfo.mRes = R.drawable.test;
            baseItemInfoList.add(baseItemInfo);
        }
        adapter_album.setData(baseItemInfoList);

        adapter_album.setOnItemListener(mItemListener);
        rcv_album.addOnItemTouchListener(mOnItemTouchListener);
    }

    private BaseAdapter.OnItemListener mItemListener = new BaseAdapter.OnItemListener() {
        @Override
        public void OnItemClick(BaseItemInfo info, int position) {

            m_viewPage.setCurrentItem(position, false);
            m_viewPage.setVisibility(View.VISIBLE);
        }
    };

    private RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };

    private ViewPager m_viewPage;
    private MyPagerAdapter m_pagerAdapter;

    private void initViewPager() {
        m_viewPage = (ViewPager) this.findViewById(R.id.viewPager);
        m_pagerAdapter = new MyPagerAdapter(this);
        m_viewPage.setAdapter(m_pagerAdapter);
        m_viewPage.addOnPageChangeListener(mOnPageChangeListener);
        m_viewPage.setVisibility(View.INVISIBLE);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override   //方法的第一个参数是页数位置，默认0开始，第二个参数是偏移的百分比，左滑的百分比是从0~1，右滑的参数是1~0，第三个个参数是滑动的像素点
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override // state==1表示手指滑动  state==2表示手指离开屏幕 state==0表示viewPager停止滑动稳定下来
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                if (m_viewPage.getVisibility() == View.VISIBLE) {
                    m_viewPage.setVisibility(View.GONE);
                } else {
                    break;
                }

                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
