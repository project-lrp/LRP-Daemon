package com.lrptest.daemon;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LrpClient extends Thread {
    public final String TAG = "LRP_LOG_CLIENT";
    private DatagramPacket datagram;
    private boolean active = false;

    LrpClient() {
        start();
    }

    @Override
    public void run() {
        // UDP Client
        DatagramSocket udpClientSocket = null;
        try {
            udpClientSocket = new DatagramSocket();
        } catch (IOException e) {
            Log.e(TAG, "Failed to create DatagramSocket: " + e.getMessage());
        }

        active = true;

        while (true) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    return;
                }

                if (datagram != null) {
                    Log.d(TAG, "Sending LRP packet");
                    try {
                        if (udpClientSocket != null)
                            udpClientSocket.send(datagram);
                        datagram = null;
                    } catch (IOException e) {
                        Log.e(TAG, "Sending LRP packet failed: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean measure() {
        if (!active) return false;

        byte[] sendData = Long.toString(System.nanoTime()).getBytes();
        InetAddress IPAddress = InetAddress.getLoopbackAddress();
        datagram = new DatagramPacket(sendData, sendData.length, IPAddress, 15113);
        datagram.setLength(sendData.length);

        synchronized (this) {
            notify();
        }

        return true;
    }
}