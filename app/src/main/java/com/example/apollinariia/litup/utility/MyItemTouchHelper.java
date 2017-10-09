package com.example.apollinariia.litup.utility;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.apollinariia.litup.Alarm;
import com.example.apollinariia.litup.AlarmList;
import com.example.apollinariia.litup.data.AlarmDbHelper;

public class MyItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private AlarmList alarmFragment;

    public MyItemTouchHelper(int dragDirs, int swipeDirs, AlarmList alarmFragment) {
        super(dragDirs, swipeDirs);
        this.alarmFragment = alarmFragment;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final Alarm alarm = (Alarm) alarmFragment.getAdapter().getItem(viewHolder.getAdapterPosition());

        if (alarm != null) {
            alarm.cancelAlarm(alarmFragment.getContext());
            AlarmDbHelper.deleteEntry(alarm);
        }

        alarmFragment.updateAlarmList();
    }

}

