package com.ishujaa.scraper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class RestartBroadcast extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            new DBC(context).putLog("RestartBroadcast", "service restarted.");
        } catch (Exception e) {
            Log.i("Er", e.getMessage());
        }

        context.startForegroundService(new Intent(context, ScraperService.class));
    }
}