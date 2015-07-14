package com.example.longpong;

import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

/**
 * This is the main menu for Long Pong. Bluetooth is setup in this Activity.
 * 
 * @author John Qualls
 * @author Andrew Canastar
 * @version 1.0
 */
public class LongPongActivity extends FragmentActivity {
    /**
     * Toggle variable for displaying debug messages
     */
    private final boolean   DEBUG_MODE    = false;
    public final static String APP_NAME      = "LongPong";
    public final static UUID   MY_UUID       = UUID.fromString("903765b0-4ca0-11e3-8e77-ce3f5508acd9");
    public static final int    DATA_READ     = 1;
    // protected static final int DATA_SENT = 2;
    public static final int    SERVER_CONN   = 3;
    public static final int    CLIENT_CONN   = 4;
    public static final int    BALL_STATE    = 5;
    public static final int    SCORE         = 6;
    public static final int    YOU_LOST      = 7;
    public static final int    NEW_GAME      = 8;
    public static final int    SINGLE_PLAYER = 9;

    private BTFragment            btFragment;
    private LPFragment            lpFragment;

    /**
     * Bluetooth fragment tag name for the fragment manager.
     */
    public final static String    TAG_BT        = "FRAGMENT_BT";

    /**
     * LongPong fragment tag name for the fragment manager.
     */
    public final static String    TAG_LP        = "FRAGMENT_LP";

    /**
     * SinglePlayer fragment tag name for the fragment manager.
     */
    public final static String    TAG_SP        = "FRAGMENT_SP";

    private int                   score;
    private TextView              scoreBoard;

    private boolean               clientMode;
    private boolean               gameFinished;

    /**
     * NOT YET IMPLEMENTED Toggle for if game ending conditions have been met
     */
    public boolean                gameOver      = false;
  
    private HandlerThread         mHandler;
    
    /**
     * Sets the layout for this Activity. Also sets up the Bluetooth<br>
     * fragment for Bluetooth initialization.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_pong);

        // Used for message handling between threads
        mHandler = new HandlerThread(this);
        mHandler.start();
        
        // Create fragments
        btFragment = new BTFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(R.id.maincontainer, btFragment, TAG_BT);
        fragmentTransaction.commit();

        scoreBoard = (TextView) this.findViewById(R.id.msgBox);
    }

    protected void setBallStart(float x, float percY, Velocity v) {
        lpFragment.setBallStart(x, percY, v);
    }

    protected void sendBallNotice(Ball b, float height) {
        float y = b.getY() / height,
              speedX = b.getSpeedX(),
              speedY = b.getSpeedY(),
              angle = b.getAngle();
        StringBuilder sb = new StringBuilder("ball " + b.getX() + " " + y + " "
                + speedX + " " + speedY + " " + angle);
        Log.i("BALL", "ball message " + sb.toString());
        btFragment.callWrite(sb.toString());
    }

    protected void setBallStart() {
        lpFragment.setBallStart();

    }

    /**
     * Used for ActionBar options.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_long_pong, menu);
        return true;
    }

    /**
     * Enable/Disable this device to Client Mode.
     * 
     * @param clientMode Whether or not this device is a client.
     */
    public void setClientMode(boolean clientMode) {
        this.clientMode = clientMode;
        if (DEBUG_MODE)
            Log.i("CLIENTMODE", "Client mode set to: " + this.clientMode);
    }

    /**
     * Checks to see if this device is in Client Mode.
     * 
     * @return Whether or not this device is a client.
     */
    public boolean getClientMode() {
        return this.clientMode;
    }

    /**
     * Updates the score when the player using this device scores a point.
     */
    public void updateScore() {
        score += 1;
        Log.i("SCORE", "score is " + String.valueOf(score));
        scoreBoard.setText(String.valueOf(score));
    }

    /**
     * NOT YET IMPLEMENTED Notifies the remote device to update the score.
     */
    public void sendScoreNotice() {
        Log.i("SCORE / LONGPONGACTIVITY", "sending notice to update score");
        btFragment.callWrite("score ");
    }

    /**
     * Safely Stops the game for later play.
     */
    @Override
    public void onStop() {
        super.onStop();

        finish();
    }

    /**
     * NOT YET IMPLEMENTED
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Called right before the activity is killed. All ongoing threads are<br>
     * stopped and all data is saved here.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop on going threads
        gameFinished = true; // Stops the HandlerThread
        LpBluetooth lpBluetooth = btFragment.getLpBluetooth();
        stopServerThread(lpBluetooth);
        stopConnectedThread(lpBluetooth);

        if (DEBUG_MODE)
            Log.i("onDestroy()", "LongPongActivity killed");
    }

    /**
     * Returns reference to the HandlerThread instance.
     * 
     * @return The reference to the HalderThread instance.
     */
    public HandlerThread getHandler() {
        return this.mHandler;
    }

    /**
     * Stops the Server Thread. Does nothing if the Sever Thread has not been
     * started yet.
     * 
     * @param lpBluetoothObj The reference to the lpBluetooth object that
     *            contains the Server Thread.
     */
    public void stopServerThread(LpBluetooth lpBluetoothObj) {
        // Handle Server Thread
        if (lpBluetoothObj != null) {
            lpBluetoothObj.stopServerThread();
        }
    }
    
    /**
     * Stops the Connected Thread. Does nothing if the Connected Thread has not been
     * started yet.
     * 
     * @param lpBluetoothObj The reference to the lpBluetooth object that
     *            contains the Connected Thread.
     */
    public void stopConnectedThread(LpBluetooth lpBluetoothObj) {
        // Handle Server Thread
        if (lpBluetoothObj != null) {
            lpBluetoothObj.stopConnectedThread();
        }
    }

    /**
     * Returns whether or not the game is finished.
     * 
     * @return whether or not the game is finished.
     */
    public boolean isGameFinished() {
        return this.gameFinished;
    }
    
    /**
     * Accessor method for the reference to the LPFragment object.
     * @return the reference to the LPFragment object.
     */
    public LPFragment getLPFragment() {
        return this.lpFragment;
    }

    /**
     * Gives this object a reference to an LPFragment object.
     */
    public void setLPFragment(LPFragment lpFragment) {
        this.lpFragment = lpFragment;
    }
}
