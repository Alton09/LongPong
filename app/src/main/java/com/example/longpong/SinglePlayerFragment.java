package com.example.longpong;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Used to create a single player test in order to test specific game
 * functionality i.e. the paddle, collision.
 * 
 * @author John Qualls
 * @version 1.0
 * 
 */
public class SinglePlayerFragment extends Fragment {
    private LongPongActivity activity;
    GameFramework game;

    /**
     * Provides a reference to this fragments activity.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (LongPongActivity) activity;
    }

    /**
     * Creates the view for this single player test
     */
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        game = new GameFramework(activity);
        game.setClientMode(true);
        game.singlePlayerMode(true);
        return game;
    }
    
    /**
     * Stops the Game 
     */
    @Override
    public void onStop() {
        game.stopGame();
        super.onStop();
    }
}
