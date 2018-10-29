package com.cbw.recyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by cbw on 2018/10/25.
 */
public class BaseItem extends AbsItem {

    protected ImageView mCover;
    protected TextView mInfo;

    public BaseItem(@NonNull Context context, @NonNull UiConfig uiConfig) {
        super(context, uiConfig);
    }

    @Override
    public void setData(BaseItemInfo itemInfo, int position) {
        if (itemInfo.getRes() instanceof Integer) {
            mCover.setImageResource((Integer) itemInfo.getRes());
        } else if (itemInfo.getRes() instanceof Bitmap) {
            mCover.setImageBitmap((Bitmap) itemInfo.getRes());
        } else {
            Glide.with(getContext()).load(itemInfo.getRes()).into(mCover);
        }
        mInfo.setText(String.valueOf(position));
    }

    @Override
    public void initView() {
        LayoutParams params;

        mCover = new ImageView(getContext());
        mCover.setScaleType(ImageView.ScaleType.FIT_XY);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        this.addView(mCover, params);

        mInfo = new TextView(getContext());
        mInfo.setTextColor(Color.BLUE);
        mInfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        this.addView(mInfo, params);
        mInfo.setVisibility(GONE);
    }

    @Override
    public void onSelected() {
        mInfo.setVisibility(VISIBLE);
    }

    @Override
    public void onUnSelected() {
        mInfo.setVisibility(GONE);
    }

    @Override
    public void onClick() {

    }
}
