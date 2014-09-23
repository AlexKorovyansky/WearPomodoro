/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.app.receivers;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import com.alexkorovyansky.wearpomodoro.BuildConfig;
import com.alexkorovyansky.wearpomodoro.app.PomodoroConstants;
import com.alexkorovyansky.wearpomodoro.app.ui.PomodoroTransitionActivity;
import com.alexkorovyansky.wearpomodoro.helpers.PomodoroMaster;
import com.alexkorovyansky.wearpomodoro.helpers.ServiceProvider;
import com.alexkorovyansky.wearpomodoro.helpers.WakefulBroadcastReceiver;
import com.alexkorovyansky.wearpomodoro.model.ActivityType;

import hugo.weaving.DebugLog;

public class PomodoroAlarmReceiver extends WakefulBroadcastReceiver {

    public static final String ACTION = BuildConfig.APPLICATION_ID + ".action.ALARM";

    @DebugLog
    public PomodoroAlarmReceiver() {
    }

    @DebugLog
    @Override
    public void onReceive(Context context, Intent intent) {
        PomodoroMaster pomodoroMaster = ServiceProvider.getInstance().getPomodoroMaster(context);
        ActivityType justStoppedActivityType = pomodoroMaster.stop(); // order may be important, else we can get race conditions
        Intent transitionIntent = intentForAlarm(context, justStoppedActivityType, pomodoroMaster.getEatenPomodoros());
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, 0, 0);
        startWakefullActity(context, transitionIntent, activityOptions);
    }

    private static Intent intentForAlarm(Context context, ActivityType justStoppedActivityType, int eatenPomdoros) {
        Intent result = new Intent(context, PomodoroTransitionActivity.class);
        result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (justStoppedActivityType.isPomodoro()) {
            if ((eatenPomdoros + 1) % PomodoroConstants.POMODORO_NUMBER_FOR_LONG_BREAK == 0) {
                result.putExtra(PomodoroTransitionActivity.EXTRA_NEXT_ACTIVITY_TYPE, ActivityType.LONG_BREAK.value());
            } else {
                result.putExtra(PomodoroTransitionActivity.EXTRA_NEXT_ACTIVITY_TYPE, ActivityType.SHORT_BREAK.value());
            }
        } else if (justStoppedActivityType.isBreak()) {
            result.putExtra(PomodoroTransitionActivity.EXTRA_NEXT_ACTIVITY_TYPE, ActivityType.POMODORO.value());
        }
        return result;
    }
}
