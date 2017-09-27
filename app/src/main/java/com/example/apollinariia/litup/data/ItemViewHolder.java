package com.example.apollinariia.litup.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.apollinariia.litup.Alarm;
import com.example.apollinariia.litup.R;

/**
 * Created by Apollinariia on 9/27/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final CheckBox checkBox;
    private final TextView alarmTimeView;
    private Alarm alarm;

    public ItemViewHolder(View itemView) {
        super(itemView);

        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_alarm_active);
        alarmTimeView = (TextView) itemView.findViewById(R.id.textView_alarm_time);
    }

    public CheckBox getCheckBox() {
        return checkBox;
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

