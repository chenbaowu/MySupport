package com.cbw.recyclerView;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.cbw.utils.OnAnimatorTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbw on 2018/10/25.
 * <p>
 * 作为一个基础Adapter,可以继承后重写自己的需求
 */
public class BaseAdapter extends RecyclerView.Adapter {

    public final static int ItemType_Base = 0;
    public Context mContext;

    protected RecyclerView mParent;
    protected ArrayList<BaseItemInfo> mItemInfoList = new ArrayList<>();
    protected UiConfig mUiConfig;
    protected int mCurrentPosition = 0; // 当前选中

    public BaseAdapter(@NonNull Context context, @NonNull UiConfig uiConfig) {
        mContext = context;
        mUiConfig = uiConfig;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mParent = recyclerView;
    }

    public void setData(List<BaseItemInfo> infoList) {
        mItemInfoList.clear();
        if (infoList != null) {
            mItemInfoList.addAll(infoList);
        }
        notifyDataSetChanged();
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == ItemType_Base) {
            BaseItem baseItem = new BaseItem(mContext, mUiConfig);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(mUiConfig.item_w, mUiConfig.item_h);
            baseItem.setLayoutParams(params);
            holder = new BaseViewHolder(baseItem);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ItemType_Base) {
            BaseItem baseItem = (BaseItem) viewHolder.itemView;
            baseItem.setData(mItemInfoList.get(position), position);
            baseItem.setTag(position);

            if (mItemInfoList.get(position).getIsClickable()) {
                if (mCurrentPosition == position) {
                    baseItem.onSelected();
                } else {
                    baseItem.onUnSelected();
                }
                baseItem.setOnTouchListener(mAnimatorTouchListener);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseItemInfo itemInfo = mItemInfoList.get(position);
        return itemInfo.getItemType();
    }

    @Override
    public int getItemCount() {
        return mItemInfoList.size();
    }

    protected OnAnimatorTouchListener mAnimatorTouchListener = new OnAnimatorTouchListener() {
        @Override
        public void onActionClick(View v) {
            if (v.getTag() != null) {
                int position = (int) v.getTag();
                if (v instanceof BaseItem) {
                    ((BaseItem) v).onClick();
                }
                scrollByCenter(v);
                notifyItemChanged(mCurrentPosition);
                notifyItemChanged(position);
                mCurrentPosition = position;
                if (mOnItemListener != null) {
                    mOnItemListener.OnItemClick(mItemInfoList.get(position), position);
                }
            }
        }
    };

    /**
     * 只适用于屏幕可视的item点击
     *
     * @param view
     */
    protected void scrollByCenter(View view) {
        if (view != null && mParent != null) {
            float viewCenter = view.getLeft() + view.getWidth() / 2f;
            float parentCenter = mUiConfig.m_w / 2f;
            float offX = viewCenter - parentCenter;
            mParent.smoothScrollBy((int) offX, 0);
        }
    }

    /**
     * 用于设置选中
     *
     * @param position   下标
     * @param isCallBack 是否回调
     */
    public void setSelectByIndex(int position, boolean isCallBack) {

        if (position >= mItemInfoList.size()) return;

        notifyItemChanged(mCurrentPosition);
        mCurrentPosition = position;
        if (mParent != null && mParent.getLayoutManager() instanceof LinearLayoutManager) {
            float viewCenter = mUiConfig.item_w / 2f;
            float parentCenter = mUiConfig.m_w / 2f;
            int offX = (int) (parentCenter - viewCenter - getPaddingLeft());
            ((LinearLayoutManager) mParent.getLayoutManager()).scrollToPositionWithOffset(position, offX);
        }
        notifyItemChanged(position);

        if (isCallBack && mOnItemListener != null) {
            mOnItemListener.OnItemClick(mItemInfoList.get(position), position);
        }
    }

    /**
     * 计算左边距(RecyclerView的左边距 + ItemOffsets的左边距)
     *
     * @return
     */
    protected float getPaddingLeft() {
        float mPaddingLeft = mUiConfig.itemOffset_left;
        mPaddingLeft += mUiConfig.padding_left;
        return mPaddingLeft;
    }

    /**
     * 设置 itemView touch动画缩放比
     *
     * @param scale
     */
    public void setAnimatorScale(float scale) {
        mAnimatorTouchListener.setAnimatorScale(scale);
    }

    /**
     * 设置 itemView touch动画时间
     *
     * @param duration
     */
    public void setAnimatorDuration(int duration) {
        mAnimatorTouchListener.setAnimatorDuration(duration);
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(AbsItem itemView) {
            super(itemView);
        }
    }

    public interface OnItemListener {

        void OnItemClick(BaseItemInfo info, int position);
    }
}
