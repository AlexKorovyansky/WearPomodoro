/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.model;

import com.alexkorovyansky.wearpomodoro.app.PomodoroConstants;

public enum ActivityType {

    NONE(0),
    POMODORO(1),
    SHORT_BREAK(2),
    LONG_BREAK(3);

    private int value;

    private ActivityType(int value) {
        this.value = value;
    }

    public static ActivityType fromValue(int value) {
        for (ActivityType activityType: ActivityType.values()) {
            if (activityType.value() == value) {
                return activityType;
            }
        }
        return ActivityType.NONE;
    }

    public int value() {
        return value;
    }

    public boolean isBreak() {
        return this == SHORT_BREAK || this == LONG_BREAK;
    }

    public boolean isPomodoro() {
        return this == POMODORO;
    }

    public int getLengthMs() {
        switch (this) {
            case POMODORO:
                return PomodoroConstants.POMODORO_LENGTH_MS;
            case LONG_BREAK:
                return PomodoroConstants.LONG_BREAK_LENGTH_MS;
            case SHORT_BREAK:
                return PomodoroConstants.SHORT_BREAK_LENGTH_MS;
            default:
                throw new IllegalStateException(this + " has no length");
        }

    }




}
