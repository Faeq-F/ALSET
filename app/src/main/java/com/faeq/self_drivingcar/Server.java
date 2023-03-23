package com.faeq.self_drivingcar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final static int SERVER_PORT = 1234;
    private final Handler mHandler;

    private int mState;

    private ServerThread serverThread;
    private ConnectionThread connectionThread;

    // Constants that indicate the current connection state
    static final int STATE_NONE = 0;       // we're doing nothing
    static final int STATE_LISTEN = 1;     // now listening for incoming connections
    static final int STATE_CONNECTED = 2;  // now connected to a remote device

    private static final String TAG = "Server";

    Server(Handler handler) {
        mHandler = handler;
        mState = STATE_NONE;
    }

    //stop all threads (stopping server) onPause
    synchronized void stop() {
        Log.d(TAG, "stop");
        // Cancel any thread currently running a connection
        if (connectionThread != null) {
            connectionThread.cancel();
            connectionThread = null;
        }
        if (serverThread != null) {
            serverThread.cancel();
            serverThread = null;
        }
    }

    //start ServerThread - begin a session in listening mode onResume()
    synchronized void start() {
        Log.d(TAG, "start");
        // Cancel any thread currently running a connection
        if (connectionThread != null) {
            connectionThread.cancel();
            connectionThread = null;
        }
        if (serverThread == null) {
            serverThread = new ServerThread();
            serverThread.start();
        }
    }

    //Start the ConnectedThread to begin managing the EV3 connection
    private synchronized void connected(Socket socket, String EV3name) {
        Log.d(TAG, "connected");
        // Cancel any thread currently running a connection
        if (connectionThread != null) {
            connectionThread.cancel();
            connectionThread = null;
        }
        if (serverThread != null) {
            serverThread.cancel();
            serverThread = null;
        }
        connectionThread = new ConnectionThread(socket);
        connectionThread.start();
        // Send the name of the EV3 back to the Main Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, EV3name);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    synchronized int getState() {
        return mState;
    }

    private class ServerThread extends Thread {
        private final ServerSocket serverSocket;

        ServerThread() {
            ServerSocket tmp = null;
            // Create a new listening server socket
            try {
                tmp = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                Log.e(TAG, "Server Socket listen() failed", e);
            }
            serverSocket = tmp;
            mState = STATE_LISTEN;
        }

        @Override
        public void run() {
            Log.d(TAG, "BEGIN  Listening (server) Thread");
            Socket socket;
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a successful connection or an exception
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Server Socket accept() failed", e);
                    break;
                } catch (NullPointerException e) {
                    Log.e(TAG, "NullPointer exception to stop crash ONLY first boot", e);
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    synchronized (Server.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                                String name = getEV3name();
                                if (name == null) {
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        Log.e(TAG, "Could not close unwanted socket", e);
                                    }// EV3 is connected - Start sending messages
                                } else connected(socket, name);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END Server Accept Thread");
        }
        void cancel() {
            Log.d(TAG, " Server Thread cancelled");
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "closing Server Socket failed", e);
            }
        }
    }

    private String getEV3name() {
        final String name_code = "R18";
        // Send the name of the connected device back to the Main Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, name_code);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        return name_code;
    }

    //handles all messages to EV3
    private class ConnectionThread extends Thread {
        private final Socket mmSocket;
        private final OutputStream mmOutStream;

        ConnectionThread(Socket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            OutputStream tmpOut = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
        }

        void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
                System.exit(0);//program ended on EV3
            }
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "closing of socket failed", e);
            }
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectionThread#write(byte[])
     */
    void write(byte[] out) {
        ConnectionThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = connectionThread;
        }
        // Perform the write unsynchronized (writing to socket should be atomic)
        new Thread(() -> r.write(out)).start();
    }
}
