package com.alexkorovyansky.wearpomodoro.helpers;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

public class UITimer {

    public static abstract class Task {
        UITimer uiTimer;
        String tag;

        public abstract void run();

        public UITimer uiTimer() {
            return uiTimer;
        }

        public String tag() {
            return tag;
        }

        public void cancelTask() {
            uiTimer.cancel(tag);
        }


    }

    private Handler handler;
    private Map<String, Runnable> runnablesMap;

    public UITimer() {
        this.handler = new Handler(Looper.myLooper());
        this.runnablesMap = new HashMap<String, Runnable>();
    }

    public void schedule(final Task task, int delayMs, String tag) {
        schedule(task, delayMs, -1, tag);
    }

    public void schedule(final Task task, int delayMs, final int periodMs, String tag) {
        cancel(tag);
        task.uiTimer = this;
        task.tag = tag;
        Runnable periodRunnable = new Runnable() {
            @Override
            public void run() {
                if (periodMs > 0) {
                    handler.postDelayed(this, periodMs);
                }
                task.run();
            }
        };
        runnablesMap.put(tag, periodRunnable);
        handler.postDelayed(periodRunnable, delayMs);
    }

    public boolean cancel(String tag) {
        Runnable runnable = runnablesMap.remove(tag);
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            return true;
        }
        return false;
    }

    public void cancelAll() {
        for (Runnable runnable: runnablesMap.values()) {
            handler.removeCallbacks(runnable);
        }
        runnablesMap.clear();
    }
}