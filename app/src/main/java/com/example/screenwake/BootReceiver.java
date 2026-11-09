package com.example.screenwake;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                Intent s = new Intent(context, ScreenWakeService.class);
                s.setAction(ScreenWakeService.ACTION_START);
                if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(s);
                else context.startService(s);
            } catch (Exception e) { Log.e("ScreenWake5", "Boot start failed", e); }
        }
    }
}
