package com.dailyreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditReminderFragment extends Fragment {

    Toolbar toolbar;
    View v;
    String titleString;
    EditText name, notes;
    TimePicker frequency;
    Button delete;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int count;
    boolean hasNotes = false;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_info, container, false);
        titleString = getArguments().getString("title");
        ((MainActivity)getActivity()).changeTitle(titleString, true, 1, 1);

        toolbar = (Toolbar)getActivity().findViewById(R.id.mToolbar);
        name = (EditText)v.findViewById(R.id.reminderText);
        notes = (EditText)v.findViewById(R.id.noteseditText);
        frequency = (TimePicker)v.findViewById(R.id.frequencyPicker);
        delete = (Button)v.findViewById(R.id.deleteReminder);
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = prefs.edit();
        count = getArguments().getInt("position");


        frequency.setIs24HourView(true);
        name.setText(titleString);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            frequency.setHour(getArguments().getInt("hora"));
            frequency.setMinute(getArguments().getInt("minuto"));
        } else {
            frequency.setCurrentHour(getArguments().getInt("hora"));
            frequency.setCurrentMinute(getArguments().getInt("minuto"));
        }
        if (!getArguments().getString("notes").equals("Unnoted")) {
            notes.setText(getArguments().getString("notes"));
            hasNotes = true;
        }


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {    }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!name.getText().toString().equals(titleString) &&
                        ((MainActivity)getActivity()).getCrossInt() == 1) {

                    ((MainActivity)getActivity()).addCross(0);
                }
                else if (mayAddArrow()){
                    ((MainActivity)getActivity()).addCross(1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {        }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!notes.getText().toString().equals(getArguments().getString("notes")) &&
                        ((MainActivity)getActivity()).getCrossInt() == 1) {

                    ((MainActivity)getActivity()).addCross(0);
                }
                else if (mayAddArrow()){
                    ((MainActivity)getActivity()).addCross(1);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {        }
        });

        frequency.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                if (horinha() != getArguments().getInt("hora") ||
                        minutinho() != getArguments().getInt("minuto") &&
                        ((MainActivity)getActivity()).getCrossInt() == 1) {

                    ((MainActivity)getActivity()).addCross(0);
                }
                else if (mayAddArrow()){
                    ((MainActivity)getActivity()).addCross(1);
                }
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save_toolbar:
                        if (((MainActivity)getActivity()).getCrossInt() == 0)
                            saveStuff();
                        getActivity().onBackPressed();
                        break;
                }
             return true;
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                EventBus.getDefault().post(new RemoveEvent(count, getArguments().getInt("listPosition")));
            }
        });

        return v;
    }

    public void saveStuff() {
        if (!name.getText().toString().equals(titleString))
            editor.putString("name"+count, name.getText().toString());

        if (!notes.getText().toString().equals(getArguments().getString("notes")) && notes.getText().toString().length() > 0)
            editor.putString("notes"+count, notes.getText().toString());

        if (horinha() != getArguments().getInt("hora") || minutinho() != getArguments().getInt("minuto")) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            Date date = null;
            try {
                date = sdf.parse(horinha() + ":" + minutinho());
            } catch (ParseException ignored) {}
            SimpleDateFormat format = new SimpleDateFormat("'Every' HH:mm 'hours'");

            editor.putString("frequency"+count, String.valueOf(format.format(date)));
            editor.putInt("hora"+count, horinha());
            editor.putInt("minuto"+count, minutinho());
        }

        editor.apply();
    }


    public boolean mayAddArrow() {
        boolean withNotes;
        boolean withoutNotes;

        withNotes = ((MainActivity)getActivity()).getCrossInt() == 0 &&
                horinha() == getArguments().getInt("hora") &&
                minutinho() == getArguments().getInt("minuto") &&
                name.getText().toString().equals(titleString) &&
                !hasNotes && notes.getText().toString().length() == 0;

        withoutNotes = ((MainActivity)getActivity()).getCrossInt() == 0 &&
                horinha() == getArguments().getInt("hora") &&
                minutinho() == getArguments().getInt("minuto") &&
                name.getText().toString().equals(titleString) &&
                hasNotes && notes.getText().toString().equals(getArguments().getString("notes"));

        return withNotes || withoutNotes;
    }


    public int horinha () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return frequency.getHour();
        else
            return frequency.getCurrentHour();
    }

    public int minutinho() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return frequency.getMinute();
        else
            return frequency.getCurrentMinute();
    }

}
