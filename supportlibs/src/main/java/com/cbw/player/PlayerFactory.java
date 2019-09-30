package com.cbw.player;

import android.content.Context;

/**
 * Created by cbw on 2018/10/25.
 */
public interface PlayerFactory {

    IPlayer CreatePlayer(Context context);
}
