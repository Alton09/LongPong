package com.example.longpong;

import static com.example.longpong.LongPongActivity.DEBUG_MODE;
import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Sets up Bluetooth before the game begins.<br>
 * <p>
 * Preconditions:<br>
 * 1) Devices must be paired before LongPong is started.<br>
 * </p>
 * <p>
 * The following procedures take place before the game begins.<br>
 * 1) Fragment layout is initialized<br>
 * 2) Bluetooth is setup<br>
 * &nbsp;&nbsp;&nbsp;- Checks to see if bluetooth exists<br>
 * &nbsp;&nbsp;&nbsp;- Turned on if not on already<br>
 * &nbsp;&nbsp;&nbsp;- Lists available paired devices to select
 * </p>
 * 
 * @author andrew.canastar
 * @author john.qualls
 * @version 1.0
 * 
 */
public class BTFragment extends Fragment {
    private static final int     REQUEST_ENABLE_BT = 1;
    protected BluetoothAdapter   mAdapter;
    private Button               connectButton, singlePlayerButton;
    private LpBluetooth          mLpBluetooth;
    private Spinner              mSpinner;
    private ArrayAdapter<String> mArrayAdapter;
    private String               deviceMAC;
    private View                 view;
    private LongPongActivity     activity;
    private HandlerThread        mHandler;

    /**
     * Gets a reference to this fragment's attached Activity.
     * 
     * @param activity The reference to this fragment's Activity.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (LongPongActivity) activity;
    }

    /**
     * Inflates this fragment's UI.
     * 
     * @param inflater
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bt_fragment_layout, container, false);
        return view;
    }

    /**
     * Begins the bluetooth setup only after the onCreate() method in the parent
     * Activity has finished execution.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    /**
     * NOT YET IMPLEMENTED
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Private utility method that performs initialization for view components
     * and bluetooth.
     */
    private void init() {
        mHandler = activity.getHandler();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // Setup buttons
        connectButton = (Button) view.findViewById(R.id.connect);
        connectButton.setOnClickListener(new ConnectButtonListener());
        singlePlayerButton = (Button) view.findViewById(R.id.single_player_button);
        singlePlayerButton.setOnClickListener(new SinglePlayerButton());
        
        if (mAdapter == null) {
            // this device does not support bluetooth. End the program.
            // Change this later to be a single player version of Pong
            // Send a message back to the activity to close ... System.exit(0);
            if (DEBUG_MODE)
                Log.e("BLUETOOTH ADAPTER PRESENT", "There is no bluetooth "
                        + "radio on this device.");
            activity.finish();
        }
        else {
            if (DEBUG_MODE)
                Log.i("BLUETOOTH ADAPTER PRESENT", "There is bluetooth!!");
            if (!mAdapter.isEnabled()) {
                if (DEBUG_MODE)
                    Log.i("BT ENABLED",
                            "Bluetooth is not enabled; maybe you should enable it.");
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                startServerPopulateSpinner();
            }
        }
    }

    /*
     * Starts the server thread and populates the spinner with a list of paired
     * devices.
     */
    private void startServerPopulateSpinner() {
        if (DEBUG_MODE)
            Log.i("BT ENABLED", "Populating Spinner!");
        mLpBluetooth = new LpBluetooth(mAdapter, mHandler);
        mLpBluetooth.startServerThread();
        this.populateSpinner();
        Toast.makeText(getActivity(), "You chose " + deviceMAC,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the return from the Bluetooth enable Intent.
     * 
     * @param requestCode The integer request code originally supplied to
     *            startActivityForResult(), allowing you to identify who this
     *            result came from.
     * @param resultCode The integer result code returned by the child activity
     *            through its setResult().
     * @param data An Intent, which can return result data to the caller
     *            (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LongPongActivity.RESULT_OK) {
            if (DEBUG_MODE)
                Log.i("BT ENABLED", "Bluetooth is enabled!");
            startServerPopulateSpinner();
        }
        if (resultCode == LongPongActivity.RESULT_CANCELED) {
            if (DEBUG_MODE)
                Log.e("BT ENABLED", "Bluetooth failed to enable.");
            activity.finish();
        }
    }

    /**
     * Populates the Spinner view component with devices that are currently
     * paired with this device. Also provides listener for the Spinner view
     * component in an anonymous inner class.
     */
    private void populateSpinner() {
        mSpinner = (Spinner) view.findViewById(R.id.device_spinner);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            /*
             * Selects
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                String selection = (String) parent.getItemAtPosition(pos);
                if (DEBUG_MODE)
                    Log.i("DATA_VALIDATION", "The value of selection in "
                            + "setOnItemSelectedListener is: " + selection);
                String[] info = selection.split("\\r?\\n");
                deviceMAC = info[1];
                Toast.makeText(getActivity(), "You chose " + deviceMAC,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing to see here!
            }
        });
        ArrayAdapter<String> adapter = this.getPairedDevices();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    /*
     * Populates the ArrayAdapter<String> object with a String representation of
     * each paired device's name and MAC address.
     * 
     * @return The ArrayAdapter<String> object.
     */
    private ArrayAdapter<String> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
        mArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item);
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a
                // ListView
                if (DEBUG_MODE)
                    Log.i("DEVICE_NAME", device.getName());
                mArrayAdapter
                        .add(device.getName() + "\n" + device.getAddress());
            }
        }
        return mArrayAdapter;
    }

    /**
     * 
     * @return
     */
    public String setDeviceSpinnerListener() {
        return deviceMAC;
    }

    public void callWrite(String message) {
        if (DEBUG_MODE)
            Log.i("SCORE or BALL / BTFRAGMENT",
                    "calling write with this message:" + " " + message);
        mLpBluetooth.write(message);
    }

    /*
     * Handles the event of the connectButton being touched by the user. The
     * Device selected is determined by the selected item within the Spinner
     * component.
     */
    private class ConnectButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (BluetoothAdapter.checkBluetoothAddress(deviceMAC)) {
                BluetoothDevice device = mAdapter.getRemoteDevice(deviceMAC);
                Log.i("CONNECT BUTTON", "Connecting to: " + deviceMAC);
                mLpBluetooth.startClientThread(device);
            }
        }
    }
    
    /*
     * Handles the event of the single player test button being clicked by the user.
     */
    private class SinglePlayerButton implements OnClickListener {

        @Override
        public void onClick(View v) {
         // Enqueue this task to the Handler in the main UI thread
         mHandler.obtainMessage(LongPongActivity.SINGLE_PLAYER).sendToTarget();
        }
    }
    
    /**
     * Returns reference to LpBluetooth object.
     * @return LpBluetooth reference. null if LpBluetooth it is not initialized.
     */
    public LpBluetooth getLpBluetooth() {
        return mLpBluetooth;
    }
}
