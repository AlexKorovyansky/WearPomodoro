/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alexkorovyansky.wearpomodoro.helpers.PomodoroMaster;
import com.alexkorovyansky.wearpomodoro.helpers.ServiceProvider;
import com.alexkorovyansky.wearpomodoro.model.ActivityType;

public class PomodoroEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PomodoroMaster pomodoroMaster = ServiceProvider.getInstance().getPomodoroMaster(this);
        pomodoroMaster.stop();
        pomodoroMaster.check();
        Intent intent = new Intent(this, PomodoroTransitionActivity.class);
        intent.putExtra(PomodoroTransitionActivity.EXTRA_NEXT_ACTIVITY_TYPE, ActivityType.POMODORO.value());
        startActivity(intent);
        finish();
    }

}
