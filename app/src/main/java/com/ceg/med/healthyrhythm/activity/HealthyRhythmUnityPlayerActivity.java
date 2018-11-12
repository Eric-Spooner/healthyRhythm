package com.ceg.med.healthyrhythm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ceg.med.healthyrhythm.UnityPlayerActivity;
import com.ceg.med.healthyrhythm.data.CallbackAble;
import com.ceg.med.healthyrhythm.data.NiniGattCallback;
import com.unity3d.player.UnityPlayer;

import static com.ceg.med.healthyrhythm.activity.MainActivity.HEALTHY_RHYTHM_LOG_TAG;

public class HealthyRhythmUnityPlayerActivity extends UnityPlayerActivity implements CallbackAble<Integer> {

    private boolean active;

    public static HealthyRhythmUnityPlayerActivity currentActivity;

    public static HealthyRhythmUnityPlayerActivity instance() {
        return currentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = false;
        currentActivity = this;
        Intent intent = getIntent();
        try {
            NiniGattCallback.set(this);
        } catch (Exception ex) {
            Log.d(HEALTHY_RHYTHM_LOG_TAG, ex.toString());
        }
    }

    @Override
    public void callback(Integer value) {
        if(active && value < 5){
            active = false;
            press(0);
        }else if(value > 5){
            active = true;
            press(value);
        }
    }

    private void press(int value) {
        UnityPlayer.UnitySendMessage("Control", "androidReceiveSignal", String.valueOf(value));
    }

}
