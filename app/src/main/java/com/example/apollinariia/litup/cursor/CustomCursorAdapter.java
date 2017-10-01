package com.example.apollinariia.litup.cursor;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import com.example.apollinariia.litup.Alarm;
import com.example.apollinariia.litup.AlarmList;
import com.example.apollinariia.litup.R;
import com.example.apollinariia.litup.data.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;



public class CustomCursorAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private AlarmList alarmFragment;
    private List<Alarm> alarmList = new ArrayList<>();

    public CustomCursorAdapter(AlarmList alarmFragment) {
        this.alarmFragment = alarmFragment;
    }

    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    public Object getItem(int position) {
        return alarmList.get(position);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final Alarm alarm = (Alarm) getItem(position);

        final Switch switchBtn = holder.getSwitch();
        switchBtn.setChecked(alarm.isActive());
        switchBtn.setTag(position);
        switchBtn.setOnClickListener(alarmFragment);

        Log.d("adapter", "alarm is " + alarm.isActive() + ", " + alarm.getAlarmTimeStringParcelable());

        final TextView alarmTimeView = holder.getAlarmTimeView();
        alarmTimeView.setText(alarm.getAlarmTimeString());
        alarmTimeView.setOnClickListener(alarmFragment);
        alarmTimeView.setTag(position);

        //final TextView alarmDayView = holder.getAlarmTimeView();
        //alarmTimeView.setText(alarm.getAlarmDayString());
        //alarmTimeView.setOnClickListener(alarmFragment);
        //alarmTimeView.setTag(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }
}
