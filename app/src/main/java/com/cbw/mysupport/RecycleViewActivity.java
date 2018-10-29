package com.cbw.mysupport;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cbw.recyclerView.BaseUseRecycleView;

/**
 * Created by cbw on 2018/10/25.
 */
public class RecycleViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseUseRecycleView baseUseRecycleView = new BaseUseRecycleView(this);
        baseUseRecycleView.init();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(baseUseRecycleView, params);
    }
}
