package com.example.screenwake;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.util.Log;

public class ScreenWakeService extends Service {
    public static final String ACTION_START = "com.example.screenwake.START";
    private static final String CHANNEL = "screen_wake";
    private PowerManager.WakeLock lock;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable stop = this::stopServiceNow;

    @Override public void onCreate() { super.onCreate(); createChannel(); }

    @Override public int onStartCommand(Intent intent, int flags, int id) {
        startForeground(5105, notification());
        try {
            PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
            if (pm != null) {
                int level = PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE;
                lock = pm.newWakeLock(level, "ScreenWake5:Wake");
                lock.setReferenceCounted(false);
                lock.acquire(300000L);
            }
        } catch (Exception e) { Log.e("ScreenWake5", "Wake lock failed", e); }

        try {
            Intent page = new Intent(this, PatternActivity.class);
            page.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(page);
        } catch (Exception e) { Log.e("ScreenWake5", "Background activity blocked", e); }

        handler.removeCallbacks(stop);
        handler.postDelayed(stop, 300000L);
        return START_NOT_STICKY;
    }

    private void stopServiceNow() {
        try { if (lock != null && lock.isHeld()) lock.release(); } catch (Exception ignored) {}
        lock = null;
        stopForeground(true);
        stopSelf();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "屏幕唤醒", NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(c);
        }
    }

    private Notification notification() {
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        return b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("开机亮屏 5 分钟")
            .setContentText("正在尝试显示亮屏图案")
            .setOngoing(true).build();
    }

    @Override public void onDestroy() {
        handler.removeCallbacks(stop);
        try { if (lock != null && lock.isHeld()) lock.release(); } catch (Exception ignored) {}
        lock = null;
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent i) { return null; }
}
