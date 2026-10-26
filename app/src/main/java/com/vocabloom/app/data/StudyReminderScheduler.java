package com.vocabloom.app.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public final class StudyReminderScheduler {
    private static final int REQUEST_CODE = 7401;
    private StudyReminderScheduler() { }

    public static void schedule(Context context, int hour, int minute, String uid) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms == null) return;
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, hour); next.set(Calendar.MINUTE, minute); next.set(Calendar.SECOND, 0); next.set(Calendar.MILLISECOND, 0);
        if (next.getTimeInMillis() <= System.currentTimeMillis()) next.add(Calendar.DAY_OF_YEAR, 1);
        alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), reminderIntent(context, uid));
    }

    public static void cancel(Context context, String uid) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms != null) alarms.cancel(reminderIntent(context, uid));
    }

    static PendingIntent reminderIntent(Context context, String uid) {
        Intent intent = new Intent(context, StudyReminderReceiver.class).setAction("com.vocabloom.app.STUDY_REMINDER." + uid).putExtra("uid", uid);
        return PendingIntent.getBroadcast(context, REQUEST_CODE + uid.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
