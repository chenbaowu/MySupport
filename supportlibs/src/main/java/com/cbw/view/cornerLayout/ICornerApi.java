package com.cbw.view.cornerLayout;

/**
 * Created by cbw on 2019/7/11
 */
public interface ICornerApi {
    void setRadius(float radius);

    void setTopLeftRadius(float radius);

    void setTopRightRadius(float radius);

    void setBottomLeftRadius(float radius);

    void setBottomRightRadius(float radius);

    void setStrokeWidth(float strokeWidth);

    void setStrokeColor(int strokeColor);

    void setAsCircle(boolean asCircle);
}
