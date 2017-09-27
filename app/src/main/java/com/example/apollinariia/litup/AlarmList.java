package com.example.apollinariia.litup;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.example.apollinariia.litup.Alarm;
import com.example.apollinariia.litup.R;
import com.example.apollinariia.litup.cursor.CustomCursorAdapter;
import com.example.apollinariia.litup.data.AlarmDbHelper;

import java.util.List;


public class AlarmList extends Fragment implements View.OnClickListener {

    private TabLayout tabLayout;
    private AnimatedVectorDrawable animation;
    private Drawable activated;
    private Drawable deactivated;
    private int position;
    private CustomCursorAdapter adapter;
    private RecyclerView recyclerView;
    private AnimatedVectorDrawable checkedToUnchecked;
    private AnimatedVectorDrawable uncheckedToChecked;

    private Animation showFab;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        AlarmDbHelper.init(getContext());

        adapter = new CustomCursorAdapter(this);

        if (getActivity() != null) {
            tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) {
            return;
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //new ItemTouchHelper(new MyItemTouchHelper(0, ItemTouchHelper.RIGHT, this)).attachToRecyclerView(recyclerView);

        updateAlarmList();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //StartTimePickerFragment fragment = new StartTimePickerFragment();
                //fragment.setAlarmFragment(AlarmFragment.this);
                //fragment.show(getFragmentManager(), "timePicker");
            }
        });

        showFab();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (tabLayout == null || animation == null) {
            return;
        }

        if (tabLayout.getTabAt(position) == null) {
            return;
        }

        if (isVisibleToUser) {
            // start animation
            updateAlarmList();
            activateIcon();
            showFab();
        } else {
            // deactivated icon
            deactivateIcon();
        }
    }

    private void showFab() {
        if (fab == null) {
            return;
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.rightMargin += (int) (fab.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab.getHeight() * 0.25);
        fab.setLayoutParams(layoutParams);
        //fab.startAnimation(showFab);
        fab.setClickable(true);
    }

    private void activateIcon() {
        tabLayout.getTabAt(position).setIcon(animation);
        animation.start();
    }


    private void deactivateIcon() {
        animation.stop();
        tabLayout.getTabAt(position).setIcon(deactivated);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CustomCursorAdapter getAdapter() {
        return adapter;
    }

    public void updateAlarmList() {
        final List<Alarm> alarms = AlarmDbHelper.getAll();
        adapter.setAlarmList(alarms);

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    // reload content
                    adapter.notifyDataSetChanged();
                    if (alarms.size() > 0) {
                        getActivity().findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
                    } else {
                        getActivity().findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.checkBox_alarm_active:
                CheckBox checkBox = (CheckBox) view;

                Alarm alarm = (Alarm) adapter.getItem((Integer) checkBox.getTag());
                alarm.setActive(checkBox.isChecked());
                AlarmDbHelper.update(alarm);
                //callMathAlarmScheduleService();
                if (checkBox.isChecked()) {
                    checkedToUnchecked.stop();
                    checkBox.setButtonDrawable(uncheckedToChecked);
                    uncheckedToChecked.start();
                    alarm.reset();
                    alarm.schedule(getContext());
//                    Toast.makeText(getActivity(), alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
                } else {
                    uncheckedToChecked.stop();
                    checkBox.setButtonDrawable(checkedToUnchecked);
                    checkedToUnchecked.start();
                }
                break;
            case R.id.textView_alarm_time:
            case R.id.textView_alarm_days:
                final Bundle bundle = new Bundle();
                //  bundle.putParcelable(Alarm.TAG, (Alarm) adapter.getItem((Integer) view.getTag()));

                // final StartTimePickerFragment fragment = new StartTimePickerFragment();
                //  fragment.setAlarmFragment(AlarmFragment.this);
                //  fragment.setArguments(bundle);
                //   fragment.show(getFragmentManager(), "timePicker");
                break;
        }
    }
}
