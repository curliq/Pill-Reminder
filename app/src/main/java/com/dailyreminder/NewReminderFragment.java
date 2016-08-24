package com.dailyreminder;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NewReminderFragment extends Fragment {

    View v;
    TimePicker frequencyPicker;
    EditText eventName, notes;
    Button add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_new_reminder, container, false);
        ((MainActivity)getActivity()).changeTitle("New reminder", true, 1, 2);

        frequencyPicker = (TimePicker)v.findViewById(R.id.frequencyPicker);
        eventName = (EditText)v.findViewById(R.id.nrereminderText);
        notes = (EditText)v.findViewById(R.id.noteseditText);
        add = (Button)v.findViewById(R.id.addReminder);

        frequencyPicker.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            frequencyPicker.setHour(8);
            frequencyPicker.setMinute(0);
        }
        else {
            frequencyPicker.setCurrentHour(8);
            frequencyPicker.setCurrentMinute(0);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventName.getText().toString().length() == 0) {
                    if (Build.VERSION.SDK_INT < 16) {
                        eventName.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.edittext_border_red));
                    } else {
                        eventName.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edittext_border_red));
                    }
                    eventName.requestFocus();
                }
                else {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                    Date date = null;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            date = sdf.parse(frequencyPicker.getHour() + ":" + frequencyPicker.getMinute());
                        else
                            date = sdf.parse(frequencyPicker.getCurrentHour() + ":" + frequencyPicker.getCurrentMinute());
                    } catch (ParseException ignored) {
                    }
                    SimpleDateFormat format = new SimpleDateFormat("'Every' HH:mm 'hours'");

                    String nameString = eventName.getText().toString();
                    String timeString = format.format(date);
                    String notesString = (notes.getText().toString().length() == 0) ? "Unnoted" : notes.getText().toString();
                    int horita = getHora(frequencyPicker);
                    int minuto = getMinuto(frequencyPicker);
                    getActivity().onBackPressed();
                    EventBus.getDefault().post(new AddEvent(nameString, timeString, notesString, horita, minuto));
                }
            }
        });


        return v;
    }

    public int getHora(TimePicker picker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return picker.getHour();
        else
            return picker.getCurrentHour();
    }

    public int getMinuto(TimePicker picker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return picker.getMinute();
        else
            return picker.getCurrentMinute();
    }

}
