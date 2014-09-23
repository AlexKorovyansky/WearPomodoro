/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.alexkorovyansky.wearpomodoro.model.ActivityType;

public class PersistentStorage {

    private final SharedPreferences sharedPreferences;

    public PersistentStorage(Context context) {
        this.sharedPreferences = context.getSharedPreferences("persistent", Context.MODE_PRIVATE);
    }

    public void writeActivityType(ActivityType activityType) {
        sharedPreferences.edit().putInt("activity_type", activityType.value()).apply();;
    }

    public ActivityType readActivityType() {
        return ActivityType.fromValue(sharedPreferences.getInt("activity_type", -1));
    }

    public void writeWhenMs(long whenMs) {
        sharedPreferences.edit().putLong("when", whenMs).apply();;
    }

    public long readWhenMs() {
        return sharedPreferences.getLong("when", -1);
    }

    public void writeLastEatenPomodoroTimestampMs(long timestampMs) {
        sharedPreferences.edit().putLong("last_eaten_pomodoro_timestamp", timestampMs).apply();
    }

    public long readLastEatenPomodoroTimestampMs() {
        return sharedPreferences.getLong("last_eaten_pomodoro_timestamp", 0);
    }

    public void writeEatenPomodoros(int number) {
        sharedPreferences.edit().putInt("eaten_pomodoros", number).apply();;
    }

    public int readEatenPomodoros() {
        return sharedPreferences.getInt("eaten_pomodoros", 0);
    }
}
