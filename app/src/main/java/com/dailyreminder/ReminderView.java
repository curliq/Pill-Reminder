package com.dailyreminder;

import android.content.Context;
import android.widget.RelativeLayout;


public class ReminderView extends RelativeLayout{

    String title, time, notes;
    int hour, minute;
    int count;

    public ReminderView(Context context, String title, String time, String notes, int hour, int minute, int count) {
        super(context);
        this.title = title;
        this.time = time;
        this.notes = notes;
        this.hour = hour;
        this.minute = minute;
        this.count = count;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getCount() {
        return count;
    }
}
