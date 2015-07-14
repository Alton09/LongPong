package com.example.longpong;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author andrew.canastar
 * @author john.qualls
 *
 */
public class LPFragment extends Fragment {
	
	// Reference to GameFramework object
	private GameFramework game;
	// Reference to the LongPongActivity
	private LongPongActivity activity;
	
	private HandlerThread mHandler;
	
	/**
	 * Used to to get the MainHandler reference.
	 */
	public LPFragment(HandlerThread handler) {
	    this.mHandler = handler;
	}
	
	/**
	 * Provides a reference to this fragments activity.
	 */
	@Override
	public void onAttach( Activity activity ) {
		super.onAttach(activity);
		this.activity = (LongPongActivity)activity;
	}
	
	/**
	 * Creates the game view for this component.
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState ) {
		
		super.onCreate(savedInstanceState);
		game = new GameFramework(activity);
		game.setHandler( mHandler );
		game.setClientMode( activity.getClientMode());
		mHandler.setGame(game);
		return game;
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	
	@Override
	public void onStop() {
		game.stopGame();
		super.onStop();
	}
	
	public void toggleGameHasBall() {
		game.toggleHasBall();
	}

	public void setBallStart() {
		Log.i( "BALLSTART", "ball start with no args");
		game.setBallStart();			
	}

	public void setBallStart(float x, float percY, Velocity velocity) {
		Log.i( "BALLSTART", "ball start with args- x: " + x
				+ ", percY: " + percY + ", v: " + velocity);
		game.setBallStart( x, percY, velocity);			
	}
}

