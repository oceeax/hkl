package com.example.screenwake;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

public class ScreenWakeService extends Service {
    public static final String ACTION_START = "com.example.screenwake.START";
    private static final String CHANNEL_ID = "screen_wake_channel";
    private static final int NOTIFICATION_ID = 5105;
    private static final long DURATION_MS = 5 * 60 * 1000L;

    private PowerManager.WakeLock wakeLock;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable stopRunnable = this::stopWakeService;

    @Override public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, buildNotification());

        // 旧式屏幕唤醒锁仅作兼容性尝试；现代系统可能忽略它。
        try {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null) {
                int level = PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE;
                wakeLock = pm.newWakeLock(level, "ScreenWake5:BootScreenWake");
                wakeLock.setReferenceCounted(false);
                wakeLock.acquire(DURATION_MS);
            }
        } catch (Exception e) {
            Log.e("ScreenWake5", "Wake lock failed", e);
        }

        // 尝试显示带图案的全屏页面。Android 可能因后台启动限制而拒绝。
        try {
            Intent page = new Intent(this, MainActivity.class);
            page.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(page);
            Log.i("ScreenWake5", "Requested patterned screen activity.");
        } catch (Exception e) {
            Log.e("ScreenWake5", "Background activity launch blocked", e);
        }

        handler.removeCallbacks(stopRunnable);
        handler.postDelayed(stopRunnable, DURATION_MS);
        return START_NOT_STICKY;
    }

    private void stopWakeService() {
        try {
            if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        } catch (Exception ignored) {}
        wakeLock = null;
        stopForeground(true);
        stopSelf();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "屏幕唤醒", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("开机后暂时保持屏幕唤醒");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        return builder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("开机亮屏 5 分钟")
                .setContentText("正在显示亮屏图案，结束后自动恢复")
                .setOngoing(true).build();
    }

    @Override public void onDestroy() {
        handler.removeCallbacks(stopRunnable);
        try {
            if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        } catch (Exception ignored) {}
        wakeLock = null;
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
