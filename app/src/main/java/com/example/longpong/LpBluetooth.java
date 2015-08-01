package com.example.longpong;

import static com.example.longpong.LongPongActivity.DEBUG_MODE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.State;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class LpBluetooth {
    private BluetoothAdapter mBluetoothAdapter;
    private HandlerThread    mHandler;

    /** Need to have states in order to manage all the threads */
    // A state for NOT_CONNECTED
    public static final int  NOT_CONNECTED       = 1;
    // A state for CONNECTED_AS_SERVER
    public static final int  CONNECTED_AS_SERVER = 2;
    // A state for CONNECTED_AS_CLIENT
    public static final int  CONNECTED_AS_CLIENT = 3;
    // A state for CONNECTED, data ready to transfer
    public static final int  CONNECTED           = 4;

    private ServerThread     mServerThread;
    private ClientThread     mClientThread;
    private ConnectedThread  mConnectedThread;
    private BluetoothSocket  mSocket;

    /**
     * 
     * @param mAdapter
     * @param mHandler
     */
    public LpBluetooth(BluetoothAdapter mAdapter, HandlerThread mHandler) {
        this.mBluetoothAdapter = mAdapter;
        this.mHandler = mHandler;
    }

    /**
     * 
     * @param socket
     */
    public void setSocket(BluetoothSocket socket) {
        this.mSocket = socket;
    }

    /**
     * Performs game setup based on the state the device is in.<br>
     * <p>States:<br>
     * CONNECTED_AS_SERVER
     * CONNECTED_AS_CLIENT
     * </p>
     * @param connectedAsServer
     */
    public void setState(int state) {
        switch (state) {
            case NOT_CONNECTED:
                break;
            case CONNECTED_AS_SERVER:
                if (DEBUG_MODE)
                    Log.i("SUCCESS LpBlueTooth", "Connected as server");
                mConnectedThread = new ConnectedThread(mSocket);
                mConnectedThread.start();
                mHandler.obtainMessage(LongPongActivity.SERVER_CONN)
                        .sendToTarget();
                break;
            case CONNECTED_AS_CLIENT:
                mServerThread.cancel();
                mConnectedThread = new ConnectedThread(mSocket);
                mConnectedThread.start();
                mHandler.obtainMessage(LongPongActivity.CLIENT_CONN)
                        .sendToTarget();
                if (DEBUG_MODE)
                    Log.i("SUCCESS LpBlueTooth",
                            "ServerThread cancelled, connected as client");
                break;
            case CONNECTED:
                break;
        }
    }

    /**
     * Set up and start the ServerThread.
     */
    public void startServerThread() {
        mServerThread = new ServerThread();
        mServerThread.start();
    }

    /**
     * Set up and start the ClientThread.
     * 
     * @param device, the bluetooth device to connect to
     */
    public void startClientThread(BluetoothDevice device) {
        mClientThread = new ClientThread(device);
        mClientThread.start();
    }

    /**
	 * 
	 */
    public void write(String message) {
        mConnectedThread.write(message.getBytes());
        if (DEBUG_MODE)
            Log.i("SUCCESS LpBlueTooth.write()", " "
                    + message.getBytes().toString() + " from: " + message);
    }

    /**
     * Stops a running Server Thread. If there is no Server Thread running,
     * nothing happens.
     */
    public void stopServerThread() {
        Thread.State stState = null;
        
        if(mServerThread != null) {
            stState = mServerThread.getState();
            
            // Close ongoing server thread
            if(stState == State.RUNNABLE) {
                mServerThread.cancel();
            }
        }
    }

    /**
     * Stops a running Connected Thread. If there is no Connected Thread running,
     * nothing happens.
     */
    public void stopConnectedThread() {
        Thread.State stState = null;
        
        if(mConnectedThread != null) {
            stState = mConnectedThread.getState();
            
            // Close ongoing connected thread
            if(stState == State.RUNNABLE) {
                mConnectedThread.cancel();
            } 
        }
    }
    
    /**
     * Gets a reference to the Server Thread.
     * 
     * @return The Server Thread Reference. null if thread is not initialized.
     */
    public Thread getServerThread() {
        return mServerThread;
    }
    
    /**
     * Gets a reference to the Client Thread.
     * 
     * @return The Client Thread Reference. null if thread is not initialized.
     */
    public Thread getClientThread() {
        return mClientThread;
    }

    /**
     * Gets a reference to the Connected Thread.
     * 
     * @return The Connected Thread Reference. null if thread is not
     *         initialized.
     */
    public Thread getConnectedThread() {
        return mConnectedThread;
    }

    /**
     * Terminates the serverThread.
     */
    public void closeServerThread(Thread serverThread) {
        mServerThread.cancel();
    }

    /*
     * Listens for a Bluetooth connection to initiate.
     * 
     * @author andrew.canastar
     * 
     * @author john.qualls
     * 
     * @version 1.0
     */
    private class ServerThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;

        /*
         * Initializes the Server Socket that listens for incoming connections.
         */
        public ServerThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client
                // code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
                        LongPongActivity.APP_NAME, LongPongActivity.MY_UUID);

            }
            catch (IOException e) {
                if (DEBUG_MODE)
                    Log.d("IOException LpBlueTooth",
                            "ServerThread Constructor exception assigning"
                                    + " BluetoothServerSocket");
            }
            mmServerSocket = tmp;
        }

        /*
         * Starts a separate thread, which uses a server socket to listen for
         * incoming connections.
         */
        @Override
        public void run() {
            if(DEBUG_MODE)
                Log.i("SERVERTHREAD", "Starting server thread.");
            
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                Log.i("TEST", "Test");
                try {
                    // accept() is the blocking call, it will only return once a
                    // connection
                    // is accepted or an exception has occurred
                    if (DEBUG_MODE)
                        Log.i("SERVERSOCKET",
                                "Serverthread waiting on connection");
                    socket = mmServerSocket.accept();
                }
                catch (IOException e) {
                    if (DEBUG_MODE)
                        Log.d("IOException LpBlueTooth",
                                "ServerThread "
                                        + "BluetoothServerSocket.socket() exception in run()");
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    if (DEBUG_MODE)
                        Log.i("SERVERSOCKET", "Socket instantiated and set");
                    setSocket(socket);
                    cancel();
                    if (DEBUG_MODE) {
                        Log.i("SUCCESS LpBlueTooth",
                                "ServerThread socket not null in run()");
                        Log.i("SERVERSOCKET",
                                "Setting state to connected as server");
                    }
                    setState(CONNECTED_AS_SERVER);
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish.
         * Must call close() before moving on with sending data across the
         * socket. This is the place where you may want to leave it open for
         * multiple connections.
         */
        public void cancel() {
            try {
                mmServerSocket.close();
                if (DEBUG_MODE)
                    Log.i("SERVERTHREAD",
                            "BluetoothServerSocket successfully closed");
            }
            catch (IOException e) {
                if (DEBUG_MODE)
                    Log.d("IOException LpBlueTooth",
                            "Error closing server thread");
            }
        }
    }

    /*
     * Initiates a connection with this device and the chosen remote device from the spinner
     * UI in the BTFragment.
     * 
     * @author andrew.canastar
     * @author john.qualls
     * @version 1.0
     */
    private class ClientThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        /*
         * Initializes the remote device, and the socket to connect to it.
         * 
         * @param device The BluetoothDevice reference.
         */
        public ClientThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server
                // code
                tmp = mmDevice
                        .createRfcommSocketToServiceRecord(LongPongActivity.MY_UUID);
            }
            catch (IOException e) {
                if (DEBUG_MODE)
                    Log.d("IOException LpBlueTooth",
                            "ClientThread Constructor "
                                    + "exception assigning BluetoothSocket");
            }
            mmSocket = tmp;
        }

        /*
         * Initiates the BluetoothSocket connection.
         */
        @Override
        public void run() {
            // Cancel discovery because it will slow down the connection
            // (currently not enabled)
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            }
            catch (IOException e) {
                // Unable to connect; close the socket and get out
                if (DEBUG_MODE)
                    Log.d("IOException LpBlueTooth", "ClientThread exception "
                            + "Bluetoothsocket.connect() in run()");
                try {
                    mmSocket.close();
                }
                catch (IOException closeException) {
                    if (DEBUG_MODE)
                        Log.d("IOException LpBlueTooth",
                                "ClientThread exception "
                                        + "Bluetoothsocket.close() in run()");
                }
                return;
            }
            if (DEBUG_MODE)
                Log.i("SUCCESS LpBlueTooth",
                        "ClientThread socket not null in run()");
            // Do work to manage the connection (in a separate thread)
            setSocket(mmSocket);
            setState(CONNECTED_AS_CLIENT);
        }

        /**
         * Will cancel an in-progress connection, close the socket, and cause
         * the thread to finish. Must call close() before moving on with sending
         * data across the socket. This is the place where you may want to leave
         * it open for multiple connections.
         */
        public void cancel() {
            try {
                if (DEBUG_MODE)
                    Log.i("CLIENTTHREAD", "ClientThread closing");
                mmSocket.close();
            }
            catch (IOException e) {
                if (DEBUG_MODE)
                    Log.d("IOException LpBlueTooth", "ClientThread exception "
                            + "Bluetoothsocket.close() in cancel()");
            }
        }

    }

    /*
     * Manages the connection between the client and server.
     * 
     * @author andrew.canastar
     * 
     * @author john.qualls
     * 
     * @version 1.0
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream     mmInStream;
        private final OutputStream    mmOutStream;

        /*
         * Initializes the input and output stream objects using the socket
         * connection.
         * 
         * @param socket The socket connection.
         */
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                if (DEBUG_MODE)
                    Log.d("IOException LpBlueTooth",
                            "ConnectedThread exception "
                                    + "on socket.getInputStream or socket.getOutputStream");
            }
            if (DEBUG_MODE)
                Log.i("SUCCESS LpBluetooth",
                        "Assigned mmInStream and mmOutStream");
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /*
         * Starts continuously listening for incoming bytes from remote device
         * until the socket is closed.
         */
        public void run() {
            if(DEBUG_MODE)
                Log.i("CONNECTEDTHREAD", "Connected Thread Started");
            byte[] buffer = new byte[1024]; // buffer store for the stream

            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    if (DEBUG_MODE) {
                        Log.w("BUFFER", "length of content: "
                                + new String(buffer).trim().length());
                        Log.i("BUFFER", "contents: " + buffer.toString());
                    }

                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    if (DEBUG_MODE)
                        Log.w("BUFFER", "num bytes: " + bytes);

                    // Send the obtained bytes to the UI thread
                    mHandler.obtainMessage(LongPongActivity.DATA_READ, bytes,
                            -1, buffer).sendToTarget();
                }
                catch (IOException e) {
                    if (DEBUG_MODE)
                        Log.d("IOException LpBlueTooth",
                                "ConnectedThread exception "
                                        + "in run() while reading from inputstream");
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                if (DEBUG_MODE)
                    Log.i("SUCCESS ConnectedThread.write(bytes)",
                            " " + bytes.toString());
                mmOutStream.write(bytes);
            }
            catch (IOException e) {
                if (DEBUG_MODE)
                    Log.d("IOException LpBlueTooth",
                            "ConnectedThread exception "
                                    + "in write() mmOutStream.write(bytes[])");
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
                if (DEBUG_MODE)
                    Log.i("CONNECTEDTHREAD",
                            "Connected Thread successfully closed");
            }
            catch (IOException e) {
                if (DEBUG_MODE)
                    Log.d("IOException LpBlueTooth",
                            "ConnectedThread exception "
                                    + "in close() mmSocket.close()");
            }
        }
    }
}