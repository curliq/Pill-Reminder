package com.dailyreminder;

public class AddEvent {

    public String name, hour, notes;
    public int hourita, minute;

    public AddEvent(String name, String hour, String notes, int horita, int minute) {
        this.name = name;
        this.hour = hour;
        this.notes = notes;
        this.hourita = horita;
        this.minute = minute;
    }
}