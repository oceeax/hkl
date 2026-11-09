package com.example.screenwake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                Intent service = new Intent(context, ScreenWakeService.class);
                service.setAction(ScreenWakeService.ACTION_START);
                if (Build.VERSION.SDK_INT >= 26) {
                    context.startForegroundService(service);
                } else {
                    context.startService(service);
                }
                Log.i("ScreenWake5", "Boot completed; wake service requested.");
            } catch (Exception e) {
                Log.e("ScreenWake5", "Unable to start wake service", e);
            }
        }
    }
}
