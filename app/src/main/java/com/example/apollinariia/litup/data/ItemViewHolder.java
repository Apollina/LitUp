package com.example.apollinariia.litup.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.apollinariia.litup.Alarm;
import com.example.apollinariia.litup.R;



public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final Switch switchBtn;
    private final TextView alarmTimeView;
    private Alarm alarm;

    public ItemViewHolder(View itemView) {
        super(itemView);

        switchBtn = (Switch) itemView.findViewById(R.id.switch_btn);
        alarmTimeView = (TextView) itemView.findViewById(R.id.textView_alarm_time);
    }

    public Switch getSwitch() {
        return switchBtn;
    }

    public TextView getAlarmTimeView() {
        return alarmTimeView;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}

