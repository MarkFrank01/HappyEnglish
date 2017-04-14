package com.wjc.simpletranslate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.wjc.simpletranslate.service.ClipboardService;

public class StartClipboardServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences.getBoolean("tap_translate", false)){
                Intent serviceIntent = new Intent(context, ClipboardService.class);
                context.startService(serviceIntent);
            }

        }
    }
}
