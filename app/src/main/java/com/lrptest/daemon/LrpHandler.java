package com.lrptest.daemon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LrpHandler {
    final static String TAG = "LRP_LOG_DAEMON";

    public static void handleIntent(Intent intent) {
        handleIntent(intent, null);
    }

    public static void handleIntent(Intent intent, Context context) {
        final Bundle bundle = intent.getExtras();
        final boolean toast = context != null && bundle.getBoolean("toast", false);

        switch (intent.getAction().toLowerCase()) {
            case "com.lrptest.daemon.toast":
                if (toast)
                    Toast.makeText(context, "Toast from daemon", Toast.LENGTH_SHORT).show();
                break;

            case "com.lrptest.daemon.measure":
                final long sendTime = bundle.getLong("nanoTime") / 1000;
                final String message = "measure(): " + measureFromPast(sendTime) + " us";
                Log.i(TAG, message);

                if (toast)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static long measureFromPast(long pastMicro) {
        long nTime = doNativeWork();
        long t2 = System.nanoTime() / 1000;
        return t2 - nTime - pastMicro;
    }

    public static void toastWithHandler(Handler eventHandler, String text) {
        if (eventHandler == null) return;
        Message message = eventHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("action", "toast");
        bundle.putString("text", text);
        message.setData(bundle);
        message.sendToTarget();
    }

    public static native long getNativeTime();
    public static native long doNativeWork();
    public static native long sendPacket(int ms, int drx, int sr);
    public static native long getConfigDrx();
    public static native long getConfigSch();
}
