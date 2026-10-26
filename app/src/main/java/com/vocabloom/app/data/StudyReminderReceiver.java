package com.vocabloom.app.data;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class StudyReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "study_reminders_vibration_v3";
    private static final int NOTIFICATION_ID = 7401;
    private static final String ACTION_STOP = "com.vocabloom.app.STOP_STUDY_REMINDER";
    private static final String PREFS = "study_reminder";
    private static final String PREF_ACTIVE = "study_reminder_vibrating";
    private static final long[] VIBRATION_PATTERN = {0, 700, 350, 700, 350, 700};

    @Override public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().startsWith(ACTION_STOP)) {
            stopReminder(context, intent);
            return;
        }

        String uid = intent == null ? null : intent.getStringExtra("uid");
        if (uid == null || uid.isEmpty()) return;
        android.content.SharedPreferences preferences = context.getSharedPreferences(PREFS + "_" + uid, Context.MODE_PRIVATE);
        if (!preferences.getBoolean("study_reminder_enabled", false)) return;
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;
        createSilentVibrationChannel(context, manager);
        preferences.edit().putBoolean(PREF_ACTIVE, true).apply();

        Uri notificationUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;
        Intent tapped = new Intent(context, StudyReminderReceiver.class).setAction(ACTION_STOP + "." + uid).putExtra("uid", uid);
        PendingIntent stopIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, tapped, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("\u0110\u1EBFn gi\u1EDD h\u1ECDc t\u1EEB v\u1EF1ng r\u1ED3i!")
                .setContentText("Ch\u1EA1m v\u00E0o \u0111\u00E2y ho\u1EB7c nh\u1EA5n T\u1EAFt \u0111\u1EC3 d\u1EEBng rung.")
                .setContentIntent(stopIntent)
                .setDeleteIntent(stopIntent)
                .addAction(android.R.drawable.ic_media_pause, "T\u1EAFt", stopIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSilent(true)
                .setAutoCancel(true);
        manager.notify(NOTIFICATION_ID, notification.build());
        startRepeatingVibration(context);

        int hour = preferences.getInt("study_reminder_hour", 20);
        int minute = preferences.getInt("study_reminder_minute", 30);
        StudyReminderScheduler.schedule(context, hour, minute, uid);
    }

    private static void createSilentVibrationChannel(Context context, NotificationManager manager) {
        if (Build.VERSION.SDK_INT < 26 || manager.getNotificationChannel(CHANNEL_ID) != null) return;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Nh\u1EAFc h\u1ECDc (ch\u1EC9 rung)", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(false); // App controls repeating vibration and stops it on tap.
        channel.setSound(null, (AudioAttributes) null);
        manager.createNotificationChannel(channel);
    }

    private static void startRepeatingVibration(Context context) {
        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= 31) {
            VibratorManager manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = manager == null ? null : manager.getDefaultVibrator();
        } else {
            //noinspection deprecation
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= 26) vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, 0));
        else {
            //noinspection deprecation
            vibrator.vibrate(VIBRATION_PATTERN, 0);
        }
    }

    private static void stopReminder(Context context, Intent intent) {
        String uid = intent == null ? null : intent.getStringExtra("uid");
        if (uid == null || uid.isEmpty()) return;
        android.content.SharedPreferences preferences = context.getSharedPreferences(PREFS + "_" + uid, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(PREF_ACTIVE, false).apply();
        if (Build.VERSION.SDK_INT >= 31) {
            VibratorManager manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            if (manager != null) manager.getDefaultVibrator().cancel();
        } else {
            //noinspection deprecation
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) vibrator.cancel();
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.cancel(NOTIFICATION_ID);
    }
}
