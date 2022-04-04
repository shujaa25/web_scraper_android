package com.ishujaa.scraper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ScraperService extends Service {

    String CHANNEL_ID = "com.ishujaa.Scraper";
    DBC dbc;

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createNotificationChannel();

        dbc = new DBC(this);
        dbc.clearLogs();

        sendNotification("Scraper", "Service started running.", null);

        new BackScraperAsync(this).execute();



        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartBroadcast.class);
        this.sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int notification_id = new Random().nextInt();
    public void sendNotification(String title, String text, String url){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);

        Intent intent;
        if(url != null){
            intent = new Intent(this, WebV.class);
            intent.putExtra("url", url);
        }else{
            intent = new Intent(this, MainActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notification_id,
                intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        builder.setAutoCancel(true);
        notificationManager.notify(notification_id++, builder.build());
    }

    private boolean scrape(Target target){
        try{
            Document document = Jsoup.connect(target.getUrl()).get();
            Elements primaryContainer = document.select(target.getPrimarySelector());
            Elements targetElements = primaryContainer.select(target.getSecondarySelector());
            String newData = targetElements.html();
            if(!newData.equals(target.getCurrentData())){
                dbc.updateTargetData(target.getId(), newData);
                target.setCurrentData(newData);
                return true;
            }
        }catch (Exception e){
            //dbc.putLog("AsyncTask", e.getMessage());
            sendNotification("Error", e.getMessage(), null);
        }
        return false;
    }

    private class BackScraperAsync extends AsyncTask<Void, Void, Void> {

        private Context context;
        BackScraperAsync(Context context){
            this.context = context;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            do{

                try{
                    dbc.putLog("AsyncTask", "AsyncTask started.");
                    List<Target> targets = dbc.retrieveAllTargets();
                    dbc.putLog("AsyncTask", "read the latest data.");
                    for(Target target: targets){
                        dbc.putLog("AsyncTask", "Checking "+target.getName());
                        if(scrape(target)){
                            dbc.putLog("AsyncTask", "Update found for "+target.getName());
                            sendNotification(target.getName(), target.getUrl(), target.getUrl());
                        }else{
                            dbc.putLog("AsyncTask", "No update for "+target.getName());
                        }
                    }
                    dbc.putLog("AsyncTask", "Sleeping for 10 minutes");
                    Thread.sleep(600000);//10 minutes delay for all.
                    dbc.putLog("AsyncTask", "Sleep completed.");

                }catch (Exception e){
                    //dbc.putLog("AsyncTask", e.getMessage());
                    sendNotification("Error", e.getMessage(), null);
                }

            }while (true);
        }
    }
}