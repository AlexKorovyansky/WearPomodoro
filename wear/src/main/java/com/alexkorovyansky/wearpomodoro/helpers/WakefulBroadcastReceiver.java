/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.helpers;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.util.SparseArray;

// based on https://github.com/android/platform_frameworks_support/blob/master/v4/java/android/support/v4/content/WakefulBroadcastReceiver.java
public abstract class WakefulBroadcastReceiver extends BroadcastReceiver {
    private static final String EXTRA_WAKE_LOCK_ID = "com.alexkorovyansky.pomodorowear.wakelockid";

    private static final SparseArray<PowerManager.WakeLock> mActiveWakeLocks
            = new SparseArray<PowerManager.WakeLock>();
    private static int mNextId = 1;


    public static void startWakefullActity(Context context, Intent intent, ActivityOptions activityOptions) {
        synchronized (mActiveWakeLocks) {
            int id = mNextId;
            mNextId++;
            if (mNextId <= 0) {
                mNextId = 1;
            }

            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                            | PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wake:activity");
            wl.setReferenceCounted(false);
            wl.acquire(60*1000);
            mActiveWakeLocks.put(id, wl);

            intent.putExtra(EXTRA_WAKE_LOCK_ID, id);
            context.startActivity(intent, activityOptions.toBundle());
        }
    }

    public static boolean completeWakefulIntent(Intent intent) {
        final int id = intent.getIntExtra(EXTRA_WAKE_LOCK_ID, 0);
        if (id == 0) {
            return false;
        }
        synchronized (mActiveWakeLocks) {
            PowerManager.WakeLock wl = mActiveWakeLocks.get(id);
            if (wl != null) {
                wl.release();
                mActiveWakeLocks.remove(id);
                return true;
            }
            // We return true whether or not we actually found the wake lock
            // the return code is defined to indicate whether the Intent contained
            // an identifier for a wake lock that it was supposed to match.
            // We just log a warning here if there is no wake lock found, which could
            // happen for example if this function is called twice on the same
            // intent or the process is killed and restarted before processing the intent.
            Log.w("WakefulBroadcastReceiver", "No active wake lock id #" + id);
            return true;
        }
    }
}