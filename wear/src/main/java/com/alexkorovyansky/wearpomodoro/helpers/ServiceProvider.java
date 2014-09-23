/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.helpers;

import android.content.Context;

public class ServiceProvider {

    private static ServiceProvider instance = new ServiceProvider();
    private PersistentStorage persistentStorage;
    private UITimer uiTimer;
    private PomodoroMaster pomodoroMaster;

    public static ServiceProvider getInstance() {
        return instance;
    }

    private PersistentStorage getPersistentStorage(Context context) {
        if (persistentStorage == null) {
            persistentStorage = new PersistentStorage(context.getApplicationContext());
        }
        return persistentStorage;
    }

    public PomodoroMaster getPomodoroMaster(Context context) {
        if (pomodoroMaster == null) {
            pomodoroMaster = new PomodoroMaster(context.getApplicationContext(), getPersistentStorage(context));
        }
        return pomodoroMaster;
    }

    public UITimer getUITimer() {
        if (uiTimer == null) {
            uiTimer = new UITimer();
        }
        return uiTimer;
    }

    private ServiceProvider() {
    }
}
