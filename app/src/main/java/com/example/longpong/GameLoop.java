package com.example.longpong;

import static com.example.longpong.LongPongActivity.DEBUG_MODE;
import android.util.Log;

/**
 * Worker thread that updates the game graphics.
 * @author John Qualls
 * @version 1.0
 *
 */
public class GameLoop extends Thread{
    private GameFramework game;
    
    public GameLoop(GameFramework game) {
        this.game = game;
    }
    
    /**
     * starts the game loop
     */
    @Override
    public void run() {
        // Start game loop
        if (DEBUG_MODE)
            Log.i("Game loop thread", "Game Loop Started");

        while (!game.hasGameStopped()) {
            try {
                Thread.sleep(5);
            }
            catch (Exception e) {
                Log.d("GAME LOOP ERROR", "Error with thread sleep.");
            }
            if (game.getHasBall()) {
                game.update();
            }
        }
        
        if (DEBUG_MODE)
            Log.i("Game loop thread", "Game Loop Stopped");
    }
}
