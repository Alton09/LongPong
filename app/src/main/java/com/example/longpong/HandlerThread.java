package com.example.longpong;

import java.util.Arrays;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class HandlerThread extends Thread {
    private final boolean DEBUG_MODE = false;
    private LongPongActivity activity;
    private MainHandler      handler;
    private LPFragment lpFragment;

    /**
     * Gets a reference to the LongPongActivity.
     * 
     * @param activity The reference to the LongPongActivity.
     */
    public HandlerThread(LongPongActivity activity) {
        handler = new MainHandler();
        this.activity = activity;
        handler.activity = (LongPongActivity) activity;
        handler.myFragmentManager = activity.getSupportFragmentManager();
    }

    @Override
    public void run() {
        if (DEBUG_MODE)
            Log.i("Handler Thread", "Thread has started");

        while (!activity.isGameFinished()) {
            // Runs until onDestroy() is called in LongPongActivity
        }

        if (DEBUG_MODE)
            Log.i("Handler Thread", "Thread has stopped");
    }

    /**
     * Sets a reference to a GameFramework object.
     */
    public void setGame(GameFramework game) {
        handler.game = game;
    }
    
    /**
     * Creates a LPFragment to display the game in.
     */
    public void createLPFragment() {
        lpFragment = new LPFragment(this);
    }
    
    /**
     * Calls the Handler class method obtainMessage(). See Google API for more
     * information.
     */
    public Message obtainMessage() {
        return handler.obtainMessage();
    }

    /**
     * Calls the Handler class method obtainMessage(int what). See Google API
     * for more information.
     */
    public Message obtainMessage(int what) {
        return handler.obtainMessage(what);
    }

    /**
     * Calls the Handler class method obtainMessage(int what, int arg1, int
     * arg2). See Google API for more information.
     */
    public Message obtainMessage(int what, int arg1, int arg2) {
        return handler.obtainMessage(what, arg1, arg2);
    }

    /**
     * Calls the Handler class method obtainMessage(int what, int arg1, int
     * arg2, Object obj). See Google API for more information.
     */
    public Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return handler.obtainMessage(what, arg1, arg2, obj);
    }

    /**
     * Calls the Handler class method obtainMessage(int what, Object obj). See Google API for more information.
     */
    public Message obtainMessage(int what, Object obj) {
        return handler.obtainMessage(what, obj);
    }
    
    /*
     * Handles Message passing communication between threads.
     */
    @SuppressLint("HandlerLeak")
    private class MainHandler extends Handler {
        private LongPongActivity activity;
        private FragmentManager  myFragmentManager;
        private GameFramework    game;
        
        @Override
        public void handleMessage(Message msg) {
            final int WHAT = msg.what, ARG1 = msg.arg1;
            final Object OBJ = msg.obj;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentTransaction fragmentTransaction = null;
                    switch (WHAT) {
                        case LongPongActivity.SERVER_CONN:
                            activity.setClientMode(false);
                            createLPFragment();
                            fragmentTransaction = myFragmentManager
                                    .beginTransaction();
                            fragmentTransaction.add(R.id.maincontainer, lpFragment, LongPongActivity.TAG_LP);
                            fragmentTransaction.commit();
                            activity.setLPFragment(lpFragment);
                            if (DEBUG_MODE)
                                Log.i("SERVERCONN", "Connected as server");
                            break;
                        case LongPongActivity.CLIENT_CONN:
                            activity.setClientMode(true);
                            createLPFragment();
                            fragmentTransaction = myFragmentManager
                                    .beginTransaction();
                            fragmentTransaction.add(R.id.maincontainer, lpFragment, LongPongActivity.TAG_LP);
                            fragmentTransaction.commit();
                            activity.setLPFragment(lpFragment);
                            if (DEBUG_MODE)
                                Log.i("CLIENTCONN", "Connected as client");
                            break;
                        case LongPongActivity.SINGLE_PLAYER:
                            BTFragment btFragment = (BTFragment) myFragmentManager
                                    .findFragmentByTag(LongPongActivity.TAG_BT);
                            activity.stopServerThread(btFragment
                                    .getLpBluetooth());

                            /*
                             * Swap fragments for single player testing
                             */
                            SinglePlayerFragment spFragment = new SinglePlayerFragment();
                            fragmentTransaction = myFragmentManager
                                    .beginTransaction();
                            fragmentTransaction.replace(R.id.maincontainer,
                                    spFragment, LongPongActivity.TAG_SP);
                            fragmentTransaction.commit();
                            break;
                        case LongPongActivity.DATA_READ:
                            /*
                             * If this event happened, then that means the ball
                             * must have left the opponents screen
                             */
                            LPFragment lpFragment = (LPFragment) myFragmentManager
                                    .findFragmentByTag(LongPongActivity.TAG_LP);

                            byte[] buffer = Arrays.copyOf((byte[]) OBJ, ARG1);
                            String[] message = new String(buffer)
                                    .toLowerCase(Locale.ENGLISH).trim()
                                    .split("\\s");
                            
                            Log.i("DATA_READ", "byte to string = " + message[0]);
                            if (message[0].equals("score")) {
                                Log.i("SCORE /LONGPONGACTIVITY",
                                        "got message[0]");
                                activity.updateScore();
                                /*
                                 * //if( score >= 10 ) //send that the player
                                 * has won to the opponent //open a fragment to
                                 * ask the player if they'd like to play another
                                 * game
                                 */
                                // else {
                                lpFragment.toggleGameHasBall();
                                activity.setBallStart();
                                // }
                            }
                            else if (message[0].equals("ball")) {
                                float x = Float.valueOf(message[1]);
                                float percY = Float.valueOf(message[2]);
                                float speedX = Float.valueOf(message[3]);
                                float speedY = Float.valueOf(message[4]);
                                float angle = Float.valueOf(message[5]);
                                lpFragment.toggleGameHasBall();
                                Velocity v = new Velocity(speedX, speedY, angle);
                                activity.setBallStart(x, percY, v);
                            }
                            else {
                                Log.w("DATA_READ",
                                        "some gobbity-gook came through");
                            }

                            /*
                             * Set the ball to the start position of the person
                             * who scored the point
                             */
                            break;/*
                                   * case DATA_SENT : // With this message it
                                   * instructs the GameFramework to hide the
                                   * ball // Downstream the ball's variables
                                   * will be updated to set its waiting position
                                   * . Toast . makeText ( BTFragment . activity
                                   * , "Message sent" , Toast. LENGTH_SHORT )
                                   * .show() ; break;
                                   */
                        case LongPongActivity.BALL_STATE:
                            Log.i("BALL / LPFRAGMENT",
                                    "About to send ball notice to LongPongActivity.");
                            activity.sendBallNotice((Ball) OBJ,
                                    game.getHeight());
                            break;
                        case LongPongActivity.SCORE:
                            Log.i("SCORE / LPFRAGMENT",
                                    "About to send score notice to LongPongActivity.");
                            activity.sendScoreNotice();
                            break;
                    /*
                     * case LongPongActivity. YOU_LOST : // open a fragment
                     * asking the player if they'd like to play another game
                     * break;
                     */
                    /*
                     * case LongPongActivity. NEW_GAME : // set the
                     * opponentWantsNewGame variable to true break;
                     */
                    }
                }
            });
        }
    }
}
