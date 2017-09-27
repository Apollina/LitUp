package com.example.apollinariia.litup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Apollinariia on 9/27/2017.
 */

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
        return null;
    }
}
