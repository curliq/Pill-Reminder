package com.dailyreminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;


public class Broadcast_TakenAction extends BroadcastReceiver {

    int count;
    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {

        prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        count = intent.getExtras().getInt("count", -1);
        int minutes = prefs.getInt("minuto"+count, 0);
        int hours = prefs.getInt("hora"+count, 8);

        if (count >= 0) {
            final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(count);
        }
        Toast.makeText(context, "Taken", Toast.LENGTH_SHORT).show();


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar killCal = Calendar.getInstance();
        killCal.isLenient();
        killCal.add(Calendar.MINUTE, minutes - prefs.getInt("minutesBefore", 30));
        killCal.add(Calendar.HOUR_OF_DAY, hours);
        //Creates a calendar and adds the time that the user selected for this reminder
        //And calls the Broadcast_Notification again to build a new notification on that calendar
        Intent newIntentKill = new Intent(context, Broadcast_Notification.class);
        Bundle saco = new Bundle();
        saco.putInt("count", count);
        saco.putBoolean("shownBefore", true);
        newIntentKill.putExtras(saco);
        PendingIntent pendingIntentKill = PendingIntent.getBroadcast(context, count, newIntentKill, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC, killCal.getTimeInMillis(), pendingIntentKill);
        } else {
            alarmManager.set(AlarmManager.RTC, killCal.getTimeInMillis(), pendingIntentKill);
        }


    }


}
