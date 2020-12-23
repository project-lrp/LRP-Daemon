package com.lrptest.daemon;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LrpUDP {
    final static String TAG = "LRP_LOG_DAEMON_UDP";

    private DatagramSocket datagramSocket;
    private Thread serverThread = null;
    private Handler eventHandler = null;

    public static final int SERVERPORT = 15113;

    LrpUDP() {
        this(null);
    }

    LrpUDP(Handler a_eventHandler) {
        eventHandler = a_eventHandler;
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        datagramSocket.close();
    }

    class ServerThread implements Runnable {
        public void run() {
            try {
                datagramSocket = new DatagramSocket(SERVERPORT, InetAddress.getLoopbackAddress());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            boolean looping = true;
            byte[] buffer = new byte[1500];

            while (looping) {
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                try {
                    datagramSocket.receive(dp);
                    String recString = new String(dp.getData(), 0, dp.getLength());
                    long past = Long.parseLong(recString) / 1000;
                    long res = LrpHandler.measureFromPast(past);

                    final String message = "measureUdp(): " + res + " us";
                    Log.i(TAG, message);
                    LrpHandler.toastWithHandler(eventHandler, message);
                } catch (IOException e) {
                    Log.e(TAG, "Shutting down LRP UDP");
                    looping = false;
                }
            }
        }
    }
}
