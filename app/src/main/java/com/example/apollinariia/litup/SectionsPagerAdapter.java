package com.example.apollinariia.litup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.apollinariia.litup.setup.DeviceSetupActivityFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            final AlarmList alarmFragment = new AlarmList();
            alarmFragment.setPosition(position);
            return alarmFragment;
        } else {
            final SettingsFragment settingsFragment = new SettingsFragment();
            settingsFragment.setPosition(position);
            return settingsFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // null == No title
        if (position == 0) {
            return "Alarms";
        } else  {
            return "Metawear";
        }
    }
}
