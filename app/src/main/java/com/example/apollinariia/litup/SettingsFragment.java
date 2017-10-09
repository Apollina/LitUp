package com.example.apollinariia.litup;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

    private TabLayout tabLayout;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        if (getActivity() != null) {
            tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        }

        tabLayout.getTabAt(position);

        return rootView;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}