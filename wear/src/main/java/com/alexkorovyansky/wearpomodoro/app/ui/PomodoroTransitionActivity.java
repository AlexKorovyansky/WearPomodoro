/*
 * Copyright (C) 2014 Alex Korovyansky.
 */
package com.alexkorovyansky.wearpomodoro.app.ui;

import android.animation.ObjectAnimator;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.alexkorovyansky.wearpomodoro.BuildConfig;
import com.alexkorovyansky.wearpomodoro.R;
import com.alexkorovyansky.wearpomodoro.app.base.BasePomodoroActivity;
import com.alexkorovyansky.wearpomodoro.app.receivers.PomodoroAlarmReceiver;
import com.alexkorovyansky.wearpomodoro.helpers.PomodoroMaster;
import com.alexkorovyansky.wearpomodoro.helpers.PomodoroUtils;
import com.alexkorovyansky.wearpomodoro.helpers.ServiceProvider;
import com.alexkorovyansky.wearpomodoro.helpers.UITimer;
import com.alexkorovyansky.wearpomodoro.model.ActivityType;
import com.felipecsl.gifimageview.library.GifImageView;

import hugo.weaving.DebugLog;

public class PomodoroTransitionActivity extends BasePomodoroActivity implements SensorEventListener {

    public static final String EXTRA_NEXT_ACTIVITY_TYPE = BuildConfig.APPLICATION_ID + ".extra.NEXT_ACTIVITY_TYPE";

    private PomodoroMaster pomodoroMaster;
    private UITimer uiTimer;
    private SensorManager sensorManager;
    private Vibrator vibrator;

    private GifImageView awesomeGif;

    private ActivityType nextActivityType;

    private int stepSensorTicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViews(R.layout.activity_transite_rect, R.layout.activity_transite_round);
        this.pomodoroMaster = ServiceProvider.getInstance().getPomodoroMaster(this);
        this.uiTimer = ServiceProvider.getInstance().getUITimer();
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        this.nextActivityType = ActivityType.fromValue(getIntent().getIntExtra(EXTRA_NEXT_ACTIVITY_TYPE, -1));
    }

    @Override
    public void onLayoutInflated(WatchViewStub stub) {
        super.onLayoutInflated(stub);
        PomodoroAlarmReceiver.completeWakefulIntent(getIntent());
        pomodoroMaster.cancelNotification();
        vibrator.vibrate(1000);

        awesomeGif = (GifImageView) stub.findViewById(R.id.transition_awesome_gif);
        awesomeGif.setBytes(PomodoroUtils.readRawResourceBytes(getResources(), R.raw.pomodoro));
        awesomeGif.startAnimation();

        if (nextActivityType.isBreak()) {
            float dp = PomodoroUtils.dipToPixels(this, 1);
            ObjectAnimator anim = ObjectAnimator.ofFloat(awesomeGif, View.TRANSLATION_X, -8*dp, 8*dp);
            anim.setDuration(1200);
            anim.setRepeatMode(ObjectAnimator.REVERSE);
            anim.setRepeatCount(ObjectAnimator.INFINITE);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }

        final TextView messageText = (TextView) stub.findViewById(R.id.transition_text);
        final int eatenPomodoros = pomodoroMaster.getEatenPomodoros();

        if (nextActivityType.isBreak()) {
            int templateId = nextActivityType == ActivityType.LONG_BREAK ?
                    R.string.transition_text_before_long_break_message_template :
                    R.string.transition_text_before_short_break_message_template;
            messageText.setText(String.format(
                    getString(templateId),
                    eatenPomodoros + 1));
            activateStepsCounter();
        } else if (nextActivityType.isPomodoro()) {
            messageText.setText(String.format(
                    getString(R.string.transition_text_before_pomodoro_message_template),
                    eatenPomodoros + 1));
            uiTimer.schedule(new UITimer.Task() {
                @Override
                public void run() {
                    cancelTask();
                    finish();
                    pomodoroMaster.start(ActivityType.POMODORO);
                }
            }, 3000, "PomodoroTransitionActivity.DelayTimer");
        }
    }

    @DebugLog
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            ++stepSensorTicks;
            if (stepSensorTicks > 5) {
                sensorManager.unregisterListener(this);
                pomodoroMaster.start(nextActivityType);
                finish();
            }
        }
    }

    @DebugLog
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        awesomeGif.stopAnimation();
        sensorManager.unregisterListener(this);
        uiTimer.cancel("PomodoroTransitionActivity.DelayTimer");
    }

    private void activateStepsCounter() {
        stepSensorTicks = 0;
        Sensor stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

}
