package com.lrptest.daemon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class LrpService extends Service {
    private static final int ONGOING_NOTIFICATION_ID = 1000;
    final static String TAG = "LRP_LOG_DAEMON_SRV";
    private LrpUDP udpServer = null;
    private Handler eventHandler = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        String channelId = createNotificationChannel("LrpService", "LTE Accelerator");
        Notification notification =
                new Notification.Builder(this, channelId)
                        .setContentTitle("LRP Service")
                        .setContentText("Keeps your LTE responsive")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,  channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Register intent receivers
        LrpService.ActionReceiver receiver = new LrpService.ActionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.lrptest.daemon.toast");
        filter.addAction("com.lrptest.daemon.measure");
        registerReceiver(receiver, filter);

        // Start UDP server
        eventHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                final String action = message.getData().getString("action");

                switch (action.toLowerCase()) {
                    case "toast":
                        final String value = message.getData().getString("text");
                        Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        udpServer = new LrpUDP(eventHandler);

        Toast.makeText(this, "LRP service started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    public class ActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceiveService: " + intent.getAction());
            LrpHandler.handleIntent(intent, getApplicationContext());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "LRP service terminated", Toast.LENGTH_SHORT).show();
    }

    private final ILrpBoundService.Stub binder = new ILrpBoundService.Stub() {
        public void measure(long nanoTime) {
            final long sendTime = nanoTime / 1000;
            final String message = "measure(): " + LrpHandler.measureFromPast(sendTime) + " us";
            Log.d(TAG, message);
            LrpHandler.toastWithHandler(eventHandler, message);
        }

        public void sendInMs(long nanoTime, long ms) {
            ms = ms + 2; // Mitigate calling latency
            Log.d(TAG, "sendInMs: " + ((ms * 1000000 - (System.nanoTime() - nanoTime)) / 1000000));
            LrpHandler.sendPacket((int) ((ms * 1000000 - (System.nanoTime() - nanoTime)) / 1000000));
        }
    };
}
