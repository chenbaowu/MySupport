package com.cbw.mysupport;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cbw.view.fingerMove.FingerView;
import com.cbw.view.fingerMove.ImageShowView;
import com.cbw.view.fingerMove.MoveViewGroup;


/**
 * Created by cbw on 2017/6/6.
 */

public class FingerMoveViewActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myview);
        initView();
    }

    private RelativeLayout rl_contentView;
    private FingerView fingerView;
    private MoveViewGroup moveViewGroup;
    private ImageShowView imageShowView;
    private Button btn_save, btn_add;
    private TextView tvMode;

    private void initView() {

        rl_contentView = (RelativeLayout) this.findViewById(R.id.rl_contentview);

        fingerView = (FingerView) this.findViewById(R.id.FingerView);

        imageShowView = (ImageShowView) this.findViewById(R.id.ImageShowView);
        imageShowView.setVisibility(View.INVISIBLE);
        imageShowView.setImage();

        moveViewGroup = (MoveViewGroup) this.findViewById(R.id.MoveViewGroup);
        moveViewGroup.setVisibility(View.INVISIBLE);

        tvMode = (TextView) this.findViewById(R.id.tv_mode);
        tvMode.setOnClickListener(this);

        btn_save = (Button) this.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

        btn_add = (Button) this.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mode:
                if(tvMode.getText().equals("装饰")){
                    fingerView.setVisibility(View.GONE);
                    imageShowView.setVisibility(View.VISIBLE);
                    moveViewGroup.setVisibility(View.GONE);
                    tvMode.setText("图片");
                }else if(tvMode.getText().equals("图片")){
                    fingerView.setVisibility(View.GONE);
                    imageShowView.setVisibility(View.GONE);
                    moveViewGroup.setVisibility(View.VISIBLE);
                    tvMode.setText("惯性");
                }else {
                    fingerView.setVisibility(View.VISIBLE);
                    imageShowView.setVisibility(View.GONE);
                    moveViewGroup.setVisibility(View.GONE);
                    tvMode.setText("装饰");
                }
                break;
            case R.id.btn_save:
                fingerView.addItem();
                break;
            case R.id.btn_add:
                fingerView.setBmp();
                break;
        }
    }

}
