package com.cbw.recyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.cbw.supportlibs.R;

import java.util.ArrayList;

/**
 * Created by cbw on 2018/10/25.
 * <p>
 * 提供一个基础使用的demo
 */
public class BaseUseRecycleView extends FrameLayout {

    public BaseUseRecycleView(@NonNull Context context) {
        super(context);
    }

    private RecyclerView mRecyclerView;
    private BaseAdapter mBaseAdapter;
    private LinearLayoutManager mLayoutManager;
    private UiConfig mUiConfig;

    public void init() {

        /*配置ui*/
        mUiConfig = new UiConfig();
        mUiConfig.item_w = 250;
        mUiConfig.item_h = 250;
        mUiConfig.m_w = 1080;
        mUiConfig.m_h = 300;
        mUiConfig.padding_left = 60;
//        mUiConfig.padding_right = 60;
        mUiConfig.itemOffset_left = 40;

        /*初始化Adapter*/
        mBaseAdapter = new BaseAdapter(getContext(), mUiConfig);
        mBaseAdapter.setAnimatorDuration(80);
        mBaseAdapter.setAnimatorScale(0.95f);
        mBaseAdapter.setOnItemListener(mItemListener);

        /*初始化RecyclerView*/
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int left = mUiConfig.itemOffset_left;
                int top = mUiConfig.itemOffset_top;
                int right = mUiConfig.itemOffset_right;
                int bottom = mUiConfig.itemOffset_bottom;
                int itemCount = parent.getAdapter().getItemCount();
                int itemPosition = parent.getChildAdapterPosition(view);

                if (itemPosition == 0 || itemPosition == 1) {
                    right = 0;
                } else if (itemPosition == itemCount - 1) {
                    right = 0;
                }
                outRect.set(left, top, right, bottom);
            }
        });
        mRecyclerView.setPadding(mUiConfig.padding_left, mUiConfig.padding_top, mUiConfig.padding_right, mUiConfig.padding_bottom);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setAdapter(mBaseAdapter);
        mBaseAdapter.setRecyclerView(mRecyclerView);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mUiConfig.m_w, mUiConfig.m_h);
        params.gravity = Gravity.BOTTOM;
        params.bottomMargin = 50;
//        params.leftMargin = 200;
        mRecyclerView.setBackgroundColor(Color.BLUE);
        this.addView(mRecyclerView, params);

        /*设置数据*/
        ArrayList<BaseItemInfo> baseItemInfoList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            BaseItemInfo baseItemInfo = new BaseItemInfo();
            baseItemInfo.mItemType = BaseAdapter.ItemType_Base;
            baseItemInfo.mRes = R.mipmap.ic_mk;
            if (i == 2) {
                baseItemInfo.mIsClickable = false;
            }
            baseItemInfoList.add(baseItemInfo);
        }
        mBaseAdapter.setData(baseItemInfoList);

        /*设置默认选中*/
        mBaseAdapter.setSelectByIndex(3, false);
    }

    public BaseAdapter getAdapter() {
        return mBaseAdapter;
    }

    private BaseAdapter.OnItemListener mItemListener = new BaseAdapter.OnItemListener() {
        @Override
        public void OnItemClick(BaseItemInfo info, int position) {
            Log.i("bbb", "OnItemClick: " + position);
        }
    };
}
