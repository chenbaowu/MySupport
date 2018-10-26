package com.cbw.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

/**
 * Created by cbw on 2018/10/25.
 */
public abstract class AbsItem extends FrameLayout implements IItem {

    protected UiConfig mUiConfig;

    public AbsItem(@NonNull Context context, @NonNull UiConfig uiConfig) {
        super(context);
        mUiConfig = uiConfig;
        initView();
    }
}
