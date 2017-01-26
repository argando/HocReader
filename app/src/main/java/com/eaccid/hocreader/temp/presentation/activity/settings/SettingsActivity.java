package com.eaccid.hocreader.temp.presentation.activity.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.eaccid.hocreader.temp.presentation.fragment.settings.SettingsFragment;
import com.eaccid.hocreader.temp.presentation.service.SchedulingMemorizingAlarmManager;
import com.eaccid.hocreader.temp.presentation.service.OnAlarmManagerScheduleListener;

public class SettingsActivity extends AppCompatActivity implements OnAlarmManagerScheduleListener {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onSchedule(long interval) {
        SchedulingMemorizingAlarmManager alarm = new SchedulingMemorizingAlarmManager();
        alarm.scheduleAlarm(interval);
        Log.i("MemorizingAlarmManager", "Scheduling repeating memorizing alarms: every " + interval / 60 / 1000 + " minutes");
    }
}