package com.dailyreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsFragment extends Fragment {

    View v;
    Toolbar toolbar;
    TextView minutesBefore, decrement, minutesBeforeButton, increment;
    RelativeLayout enableVibration;
    SwitchCompat vibrationSwitcher;
    TextView legal;
    int minutesInt;
    SharedPreferences prefs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity)getActivity()).changeTitle("Settings", true, 1, 1);

        toolbar = (Toolbar)getActivity().findViewById(R.id.mToolbar);
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        minutesBefore = (TextView)v.findViewById(R.id.minutesBefore);
        decrement = (TextView)v.findViewById(R.id.decrement);
        minutesBeforeButton = (TextView)v.findViewById(R.id.minutesBeforeButton);
        increment = (TextView)v.findViewById(R.id.increment);
        enableVibration = (RelativeLayout)v.findViewById(R.id.enabelVibration);
        vibrationSwitcher = (SwitchCompat)v.findViewById(R.id.enableVibrationSwitcher);
        legal = (TextView)v.findViewById(R.id.legal);
        minutesInt = prefs.getInt("minutesBefore", 30);


        //time before
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minutesInt < 120) {

                    minutesInt = minutesInt + 1;
                    minutesBefore.setText(String.valueOf(minutesInt));
                    minutesBeforeButton.setText(String.valueOf(minutesInt));
                }
            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minutesInt > 0) {

                    minutesInt = minutesInt - 1;
                    minutesBefore.setText(String.valueOf(minutesInt));
                    minutesBeforeButton.setText(String.valueOf(minutesInt));
                }
            }
        });


        //vibration
        vibrationSwitcher.setHighlightColor(Color.parseColor("#d23d23"));
        enableVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrationSwitcher.toggle();
            }
        });


        //save
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.save_toolbar) {
                    prefs.edit().putInt("minutesBefore", minutesInt).apply();
                    prefs.edit().putBoolean("vibrates", vibrationSwitcher.isChecked()).apply();
                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                return true;
            }
        });


        legal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).toLegal();
            }
        });

        return v;
    }
}
