/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.app.receivers;

import android.content.Context;
import android.content.Intent;

import com.alexkorovyansky.wearpomodoro.BuildConfig;
import com.alexkorovyansky.wearpomodoro.helpers.PomodoroMaster;
import com.alexkorovyansky.wearpomodoro.helpers.ServiceProvider;
import com.alexkorovyansky.wearpomodoro.helpers.WakefulBroadcastReceiver;

import hugo.weaving.DebugLog;

public class PomodoroAlarmTickReceiver extends WakefulBroadcastReceiver {

    public static final String ACTION = BuildConfig.APPLICATION_ID + ".action.ALARM_TICK";

    @DebugLog
    public PomodoroAlarmTickReceiver() {
    }

    @DebugLog
    @Override
    public void onReceive(Context context, Intent intent) {
        PomodoroMaster pomodoroMaster = ServiceProvider.getInstance().getPomodoroMaster(context);
        pomodoroMaster.syncNotification();
    }

}
