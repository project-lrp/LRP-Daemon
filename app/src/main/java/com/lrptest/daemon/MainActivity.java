package com.lrptest.daemon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "LRP_LOG_DAEMON_ACT";

    static {
        System.loadLibrary("native-lib");
    }

    LrpClient client = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> results = benchmark();
        results.forEach((s) -> {
            Log.i(TAG, s);
        });
        ((TextView)findViewById(R.id.result_text)).setText(String.join("\n", results));

        Intent intent = new Intent(this, LrpService.class);
        startService(intent);
    }

    public ArrayList<String> benchmark() {
        ArrayList<String> results = new ArrayList<String>();

        results.add("Start time (native): " + LrpHandler.getNativeTime() / 1000 + " ms");
        results.add("Start time (system): " + getTime() + " ms");

        results.add("5ms sleep (native): " + LrpHandler.doNativeWork() + " us");

        long jniOverhead = getJNIOverhead();
        results.add("JNI overhead: " + jniOverhead + " us per call");

        long clockOffset = Math.round(Math.ceil(Math.abs(getTime() - LrpHandler.getNativeTime() / 1000.0)));
        results.add("Clock offset: " + clockOffset + " ms");

        return results;
    }

    public long getTime() {
        return System.currentTimeMillis();
    }

    public int getJNIOverhead() {
        final int iterations = 10;
        int totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            long t1 = System.nanoTime();
            long nTime = LrpHandler.doNativeWork();
            long t2 = System.nanoTime();

            totalTime += ((t2 - t1) / 1000 - nTime);
        }

        return totalTime / iterations;
    }

    ILrpBoundService mBoundService = null;
    private final ServiceConnection boundServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "LRP service connected");
            mBoundService =  ILrpBoundService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "LRP service disconnected");
            mBoundService = null;
        }
    };
}
