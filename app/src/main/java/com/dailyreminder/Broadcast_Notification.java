package com.dailyreminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.NotificationCompat;


public class Broadcast_Notification extends BroadcastReceiver {

    String name;
    int count;
    int hours, minutes;
    int minutesBefore;
    NotificationCompat.Builder notification;
    SharedPreferences prefs;
    Context context;
    NotificationManager nm;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (!intent.getExtras().getBoolean("cancel", false)) {

            this.context = context;
            prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification = new NotificationCompat.Builder(context);
            count = intent.getExtras().getInt("count");
            name = prefs.getString("name"+count, "");
            hours = prefs.getInt("hora"+count, 8);
            minutes = prefs.getInt("minuto"+count, 0);
            minutesBefore = prefs.getInt("minutesBefore", 30);


            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH)
                isScreenOn = pm.isInteractive();
            else
                isScreenOn = pm.isScreenOn();

            if (!isScreenOn) {

                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                wl.acquire(10000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
                wl_cpu.acquire(10000);
            }


            /**
             * notification
             */

            final Intent notificationIntent = new Intent(context, Broadcast_TakenAction.class);
            //Broadcast_TakenAction gets called when notification is clicked
            notificationIntent.putExtra("count", count);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, count, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setAutoCancel(true);
            notification.setLargeIcon(drawableToBitmap(ResourcesCompat.getDrawable(context.getResources(), R.drawable.notification_large_icon, null)));
            notification.setSmallIcon(R.drawable.small_icon);
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle(name);
            notification.setContentIntent(pIntent);
            notification.setPriority(0);
            notification.addAction(R.drawable.check, "Taken", pIntent);


            if (intent.getExtras().getBoolean("shownBefore", false)) {

                Runnable delayedThreadStartTask2 = new Runnable() {
                    @Override
                    public void run() {
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {

                                        for (int incr = 0; incr < minutesBefore; incr++) {

                                            if (!intent.getExtras().getBoolean("cancel", false)) {

                                                int time_left = minutesBefore - incr;

                                                notification.setContentText(time_left + "m left.");
                                                nm.notify(count, notification.build());
                                                try {
                                                    Thread.sleep(60 * 1000);
                                                } catch (InterruptedException ignored) {
                                                }
                                            }
                                        }

                                        realNotification();
                                        if (prefs.getBoolean("vibrates", true)) {
                                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                            v.vibrate(500);
                                        }
                                    }
                                }

                        ).start();
                    }
                };
                delayedThreadStartTask2.run();

            } else {
                realNotification();
            }

        }

    }

    public void realNotification() {

        notification.setTicker("Take the " + name);
        notification.setContentText("Click this when you've taken it");
        nm.notify(count, notification.build());


    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
