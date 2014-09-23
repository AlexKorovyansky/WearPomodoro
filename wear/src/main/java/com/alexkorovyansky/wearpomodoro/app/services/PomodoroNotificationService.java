/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.app.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.alexkorovyansky.wearpomodoro.helpers.PomodoroMaster;
import com.alexkorovyansky.wearpomodoro.helpers.ServiceProvider;

import hugo.weaving.DebugLog;

public class PomodoroNotificationService extends Service {

    private PomodoroMaster pomodoroMaster;
    private BroadcastReceiver broadcastReceiverScreenOff;
    private BroadcastReceiver broadcastReceiverScreenOn;

    public PomodoroNotificationService() {
    }

    @DebugLog
    @Override
    public void onCreate() {
        super.onCreate();
        this.pomodoroMaster = ServiceProvider.getInstance().getPomodoroMaster(this);
        this.broadcastReceiverScreenOn = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleScreenOnOff(true);
            }
        };
        this.broadcastReceiverScreenOff = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleScreenOnOff(false);
            }
        };

        registerReceiver(broadcastReceiverScreenOn, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(broadcastReceiverScreenOff, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @DebugLog
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @DebugLog
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @DebugLog
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverScreenOn);
        unregisterReceiver(broadcastReceiverScreenOff);
    }

    @DebugLog
    private void handleScreenOnOff(boolean isOn) {
        if (!pomodoroMaster.isActive()) {
            stopSelf();
            return;
        }
        pomodoroMaster.syncNotification(isOn);
    }
}
