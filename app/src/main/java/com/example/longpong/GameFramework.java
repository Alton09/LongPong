package com.example.longpong;

import static com.example.longpong.LongPongActivity.DEBUG_MODE;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * GameFramework defines how the game will appear to the user. It contains all
 * the GameObjects to be drawn during the progress of the game. It updates the
 * location of the objects based upon their state and their interactions with
 * other objects.
 *
 * @author john.qualls
 * @author andrew.canastar
 * @version 1.0
 */
public class GameFramework extends View {
    // Events that will lead the Ball GameObject to cease being visible
    public static final int EXIT = 1;
    public static final int GOAL = 2;

    // Used to draw onto the canvas
    private Paint paint;
    private Canvas canvas;
    private boolean startLine;
    private boolean midLine;
    private boolean midLineLock;
    private boolean finishLine;
    private boolean lockPaddle;      // prevents from drawing
    // another paddle
    private boolean drawPaddle;      // smoothly draws paddle if
    // there's a long press
    private boolean createPaddle;    // Used for fade thread to
    // notify when paddle is
    // created
    private float drawStartLine;
    private float drawLine;
    private float prevDrawLine;
    private Path path;
    private int ballColor;
    private int paddleColor;
    private int currAlpha;       // Used for fading color
    private float drawMax;

    // The screen's width and height
    private int width, height;

    // Acceptable paddle drawing range
    private float clientRange;
    private float serverRange;
    private float paddleLocation;

    // initializes fields
    private boolean initialize;

    // Game Objects
    private Ball ball;
    private GameObject paddle;

    // Draw Bounding boxes
    private boolean boundingBoxes;

    // Paddle variables
    private float paddleWidth;
    // ArrayList
    private int maxLength;

    // Handler to the main activity
    private HandlerThread mHandler;

    // Ball variables
    private int ballX,
            ballY;
    private float ballWidth,
            ballHeight,
            ballSpeedX,
            ballSpeedY,
            multiplier;


    private boolean hasBall,
            winner;

    // Gameloop thread
    private GameLoop gameLoop;
    private boolean stopGame;        // Stops game loop

    // Convenient reference to StartMenuActivity
    private LongPongActivity activity;

    // Client and server mode variables
    private boolean clientMode;

    // Arraylist of all the GameObjects for collision
    private ArrayList<GameObject> objects;
    private ArrayList<Ball> balls;

    private Collision collision;

    // For testing in single player mode
    private boolean singlePlayerMode;

    /**
     * Starts up the game.
     */
    public GameFramework(LongPongActivity activity) {
        super((Context) activity);
        initialize = true;
        this.activity = activity;
        collision = new Collision(this);
        this.setBackgroundColor(Color.BLACK);
    }

    /**
     * Initializes instance fields
     */
    private void init() {
        // Set listeners
        setOnTouchListener(new TouchListener());

        // Used for smooth drawing
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Get the screen size
        width = canvas.getWidth();
        height = getBottom();

        if (DEBUG_MODE)
            Log.i("GameFramework", "Canvas dimensions: Width = " + width +
                    " Height = " + height);

        // Set ball and paddle variables
        setDimensions();

        // Set hasBall based on clientMode
        if (clientMode)
            hasBall = true;
        else
            hasBall = false;
        // clientMode = false;

        // Get acceptable paddle drawing range
        float paddleRange = width / 6;
        clientRange = width - paddleRange;
        serverRange = paddleRange;

        // Draw paddle based on mode
        if (clientMode)
            paddleLocation = (clientRange + width) / 2;
        else
            paddleLocation = serverRange / 2;

        // Draw Bounding boxes for testing
        if (singlePlayerMode) boundingBoxes = true;
        else boundingBoxes = false;

        // initialize objects and balls
        objects = new ArrayList<GameObject>();
        balls = new ArrayList<Ball>();

        // Create the ball
        newBall();

        // Create walls
        addWalls();

        // Set the colors
        ballColor = Color.argb(255, 255, 255, 255);
        paddleColor = Color.argb(255, 255, 255, 255);

        // Don't call this method again
        initialize = false;

        // Start game loop
        gameLoop = new GameLoop(this);
        gameLoop.start();
    }

    /**
     * Restarts a game of longpong. Sets the ball position to the generic start
     * position on both client and server. disappears the ball for the loser and
     * shows the ball for the winner. Sets hasBall to true for the winner and
     * false for the loser.
     */
    public void restart() {
        if (winner == true) {
            appearBall();
            setHasBall(true);
            setBallStart();
        } else {
            disappearBall();
            setHasBall(false);
            setBallStart();
        }
    }

    /**
     * Sets the winner variable for the player.
     *
     * @param winner
     */
    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /*
     * Sets ball and paddle variables based on the dpi units of the device.
     */
    private void setDimensions() {
        // Get device dpi unit to get multiplier
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int dpi = metrics.densityDpi;
        multiplier = dpi / 160f;
        if (DEBUG_MODE) Log.i("GameFramework", "Multiplier: " + multiplier + " DPI: " + dpi);

        // Ball Variables
        ballX = width / 2;
        ballY = height / 2;
        ballWidth = Math.round(25 * multiplier);
        ballHeight = Math.round(30 * multiplier);
        ballSpeedX = multiplier;
        ballSpeedY = multiplier;

            /*
             * Paddle variables
             */
        maxLength = (int) ballHeight * 2;
        paddleWidth = ballWidth;

        if (DEBUG_MODE) {
            Log.i("GameFramework", "BallX: " + ballX);
            Log.i("GameFramework", "BallY: " + ballY);
            Log.i("GameFramework", "BallWidth: " + ballWidth);
            Log.i("GameFramework", "BallHeight: " + ballHeight);
            Log.i("GameFramework", "BallSpeedX: " + ballSpeedX);
            Log.i("GameFramework", "BallSpeedY: " + ballSpeedY);
        }
    }

    /**
     * Used for drawing graphics on the screen
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Used to draw objects
        float x = 0;
        float y = 0;
        float w = 0;
        float h = 0;
        float left = 0;
        float top = 0;

        // Update canvas
        this.canvas = canvas;

        // Initialize fields if first time called
        if (initialize)
            init();

        // Draw ball
        x = (float) ball.getX();
        y = (float) ball.getY();
        w = (float) ball.getWidth();
        h = (float) ball.getHeight();
        left = x - (w / 2);
        top = y - (h / 2);

        paint.setColor(ballColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left, top, left + w, top + h, paint);
        paint.setStyle(Paint.Style.STROKE);

        // Draw top boundary line
        //canvas.drawLine(0, 0, width, 0, paint);

        // Draw bounding boxes if enabled
        if (boundingBoxes) {
            // set up paint for drawing
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(5);

            // Draw bounding boxes for each object in the game
            for (int i = 0; i < objects.size(); i++) {
                GameObject object = objects.get(i);

                // Vars. for objects
                x = (float) object.getX();
                y = (float) object.getY();
                w = (float) object.getWidth();
                h = (float) object.getHeight();

                // Canvas draws point at top left corner of box so subtract
                left = x - (w / 2);
                top = y - (h / 2);
                canvas.drawRect(left, top, left + w, top + h, paint);
                canvas.drawPoint(x, y, paint);
            }

            // Draw Bounding boxes for the balls
            for (int i = 0; i < balls.size(); i++) {
                Ball ball = balls.get(i);

                // Vars. for balls
                x = (float) ball.getX();
                y = (float) ball.getY();
                w = (float) ball.getWidth();
                h = (float) ball.getHeight();

                // Canvas draws point at center of box so subtract
                left = x - (w / 2);
                top = y - (h / 2);
                canvas.drawRect(left, top, left + w, top + h, paint);
                canvas.drawPoint(x, y, paint);
            }
        }

        // Draw paddle if not locked
        if (!lockPaddle) {
            if (startLine) {
                path = new Path();
                path.moveTo(paddleLocation, drawStartLine);

                // calculate draw interval
                drawMax = drawStartLine + maxLength;

                startLine = false;
                midLineLock = false;
            }

            if (midLine) {
                // Fixes bounding box resizing for collision
                if (drawLine < prevDrawLine && prevDrawLine != 0) {
                    startFade();
                }

                // Only draw paddle if it is within correct interval
                else if (drawLine < drawMax && drawLine >= drawStartLine) {
                    drawPaddle = true;
                    prevDrawLine = drawLine; // Keep this for later drawing use
                } else if (drawLine <= drawStartLine) { // prevents bug when user
                    // draws wrong way
                    drawPaddle = false;
                } else
                    // start fading animation
                    startFade();

                midLine = false;
            }

            // Fade paddle
            if (finishLine) {
                if (drawLine > drawStartLine) { // Fixes bug when user taps
                    // screen
                    startFade();
                } else {// Don't draw paddle
                    drawPaddle = false;
                    prevDrawLine = 0;
                }

                finishLine = false;
            }

            /*
             * draw paddle if move motion event isn't picked up by game loop
             * preventing flashing when holding down press while drawing
             */
            if (drawPaddle) {
                // Set paddle color
                paint.setColor(Color.LTGRAY);
                paint.setStrokeWidth(paddleWidth);
                path.lineTo(paddleLocation, drawLine);
                canvas.drawPath(path, paint);
            }
        }

        // Draw fading paddle if locked
        if (lockPaddle) {
            paint.setColor(Color.argb(currAlpha, Color.red(paddleColor),
                    Color.green(paddleColor), Color.blue(paddleColor)));
            path.lineTo(paddleLocation, prevDrawLine);
            paint.setStrokeWidth(paddleWidth);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * Thread that executes the fade animation
     */
    private void startFade() {
        // Prevents a long press from executing the thread continuously
        midLineLock = true;
        lockPaddle = true; // prevent from drawing another paddle
        drawPaddle = false;
        createPaddle = true;

        createPaddle();

        setClickable(false);
        // Set up alpha variable
        currAlpha = 255;

        // Use thread to fade paddle
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Leave line for a half a second
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    if (DEBUG_MODE)
                        Log.i("GameFramework: ERROR", "Error with thread sleep for fade thread");
                }

                while (currAlpha > 0) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        if (DEBUG_MODE)
                            Log.i("GameFramework: ERROR", "Error with thread sleep for fade thread");
                    }
                    currAlpha -= 1;

                    // Run in UI thread
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });

                }
                lockPaddle = false;
                createPaddle = false;
                prevDrawLine = 0;
                // remove paddle
                objects.remove(objects.size() - 1);
            }
        }).start();
    }

    /**
     * Updates the user's inputs.
     */
    public void updateInputs() {

    }

    /**
     * Updates the game's events.
     */
    public void update() {

        // Check for collision
        collision.collision(objects, balls, this);

        // Update ball's Position
        ball.updatePosition();

        //debug();

        // Redraw
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });

    }

    /**
     * Used for displaying debug messages
     */
    private void debug() {
        // Display game objects
        for (int i = 0; i < objects.size(); i++) {
            GameObject obj =
                    objects.get(i);
            Log.d("Objects", "" + obj);
        }

    }

    /**
     * Adds the paddle to the objects ArrayList for collision.
     */
    private void createPaddle() {
        // paddle variables needed for collision
        float x = paddleLocation; // Move x pos to left by half of the paddle
        // width
        float h = Math.abs(prevDrawLine - drawStartLine);
        float y = drawStartLine + h / 2;
        float w = paddleWidth; // based off of screen width

        // Add paddle for collision
        paddle = new GameObject(x, y, w, h, "paddle");
        objects.add(paddle);
    }

    /**
     * Add all the walls, goal, and exit GameObjects
     */
    private void addWalls() {
        GameObject wall = null; // reference for the wall objects

        // wall dimensions
        int wallWidth = (int) ballWidth;

        int wallHeight = (int) ballHeight;

        // Add walls different depending on mode
        int wallX = wallWidth / 2;
        int wallY = height / 2;
        if (clientMode) {
            // Add exit wall on left side
            wall = new GameObject(-wallX, wallY, wallWidth, height, "exit");

            // Add regular wall if in singlePlayerMode
            if (singlePlayerMode)
                wall = new GameObject(-wallX, wallY, wallWidth, height, "test wall");

            objects.add(wall); // Add to objects for collision

            // Add goal wall on right side
            wall = new GameObject(width + wallWidth, wallY, wallWidth, height,
                    "goal");
            objects.add(wall); // Add to objects for collision
        } else {
            // Add exit wall on right side
            wall = new GameObject(width + wallWidth, wallY, wallWidth, height,
                    "exit");
            objects.add(wall);

            // Add goal wall on left side
            wall = new GameObject(-wallX, wallY, wallWidth, height, "goal");
            objects.add(wall);

        }

        // Add goal wall

        /*
         * Add top and bottom walls
         */
        // Top wall
        wallX = width / 2;
        wall = new GameObject(wallX, -wallHeight / 2, width, wallHeight, "wall");
        objects.add(wall);

        // Bottom wall
        wallY = (height + wallHeight / 2);
        wall = new GameObject(wallX, wallY, width, wallHeight, "wall");
        objects.add(wall);
    }

    /**
     * Creates a new ball in the game
     */
    private void newBall() {
        Velocity v = new Velocity(ballSpeedX, ballSpeedY, 0);
        if (clientMode)
            ball = new Ball(ballX, ballY, ballWidth, ballHeight, "ball", v);
        else // Position off screen cause client receives ball first
            ball = new Ball(ballX + width, ballY, ballWidth, ballHeight, "ball", v);
        ball.setMultiplier(multiplier);
        ball.setSpeed(ballSpeedX, ballSpeedY);

        // Set ball angle based on mode
        if (!clientMode)
            ball.turn(180);

        // Store ball for collision
        balls.add(ball);
    }

    /**
     * Stops the game loop.
     */
    public void stopGame() {
        stopGame = true;
    }

    /*
     * INNER CLASSES FOR LISTENERS
     */

    /**
     * Touch listener
     */
    private class TouchListener implements OnTouchListener {
        /*
         * Draws paddle in onDraw() in game loop
         */
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // X coordinate of touch event
            float x = event.getX();
            float y = event.getY();
            //
            if (clientMode) {
                // Draw paddle if touch is in valid range
                if (x < width && x > clientRange)
                    drawPaddle(y, event);

                return true;
            } else {
                // Draw paddle if touch is in valid range
                if (x < serverRange && x > 0)
                    drawPaddle(y, event);

                return true;
            }
        }

        /*
         * Sets specific boolean values to draw paddle in onDraw() in game loop
         * 
         * @param y The y coordinate of the paddle.
         */
        private void drawPaddle(float y, MotionEvent event) {
            // Check if action is press or release
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!lockPaddle) {
                    startLine = true;
                    drawStartLine = y;
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (!lockPaddle && !midLineLock) {
                    midLine = true;
                    drawLine = y;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!lockPaddle && !midLineLock) {
                    finishLine = true;
                    drawLine = y;
                }
            }
        }
    }

    /**
     * Set the ball start position to the location used for the start of the
     * game or the start of a new point.
     */
    public void setBallStart() {
        if (DEBUG_MODE)
            Log.i("GameFramework", "setBallStart() no args called.");
        ball.setX(ballX);
        ball.setY(ballY);
        ball.setSpeed(ballSpeedX, ballSpeedY);
        ball.setAngle(20);
        if (!clientMode) {
            ball.turn(180);
        }
    }

    /**
     * Set the ball start position to the location, and velocity.
     *
     * @param x        The x coordinate of the ball's start position.
     * @param percY    The y coordinate of the ball's start position.
     * @param velocity The velocity vector of the ball's start position.
     */
    public void setBallStart(float x, float percY, Velocity velocity) {
        if (DEBUG_MODE)
            Log.i("GameFramework", "ball start with args- x: " + x + ", percY: "
                    + percY);
        if (clientMode)
            ball.setStartPosition((width - (width - (ball.getWidth() + 10))),
                    (percY * height), velocity);
        else
            ball.setStartPosition((width - (ball.getWidth() + 10)),
                    (percY * height), velocity);
    }

    /**
     * Set the message handler to a handler passed to the method.
     *
     * @param handler
     */
    public void setHandler(HandlerThread handler) {
        this.mHandler = handler;
    }

    /**
     * Toggles the boolean instance variable hasBall which controls whether the
     * ball position is updated. If hasBall is set to false then disappearBall()
     * is called to match the ball color to the background. If hasBall is set to
     * true then appearBall() is called and sets the ball color to white.
     */
    public synchronized void toggleHasBall() {
        if (this.hasBall) {
            this.hasBall = false;
            disappearBall();
        } else {
            this.hasBall = true;
            appearBall();
        }

        if (DEBUG_MODE)
            Log.i("GameFramework", "Ball toggled to: " + hasBall);
    }

    /**
     * Setter method for hasBall.
     *
     * @param hasBall
     */
    public synchronized void setHasBall(boolean hasBall) {
        this.hasBall = hasBall;
    }

    /**
     * Getter method for hasBall.
     *
     * @return boolean hasBall.
     */
    public synchronized boolean getHasBall() {
        return hasBall;
    }

    /**
     * Setter method for client mode.
     *
     * @param mode = true if in clientMode.
     */
    public void setClientMode(boolean mode) {
        this.clientMode = mode;
    }

    /**
     * Getter method for client mode.
     *
     * @return boolean clientMode.
     */
    public boolean getClientMode() {
        return clientMode;
    }

    /**
     * There are two events where a player will lose the ball - hits exit or
     * goal. Dependent on the type of the event (final variables EXIT or GOAL) a
     * different message is sent to the LongPongActivity via the message
     * handler: mHandler.
     *
     * @param type
     */
    public void loseBall(int type) {
        switch (type) {
            case EXIT:
                if (DEBUG_MODE)
                    Log.i("GameFramework", "hit the exit");
                mHandler.obtainMessage(LongPongActivity.BALL_STATE, ball)
                        .sendToTarget();
                break;
            case GOAL:
                if (DEBUG_MODE)
                    Log.i("GameFramework", "hit the goal");
                mHandler.obtainMessage(LongPongActivity.SCORE).sendToTarget();
                break;
        }
    }

    /**
     * Sets the ball to invisible against the black background.
     */
    public void disappearBall() {
        ballColor = Color.argb(0, 0, 0, 0);
    }

    /**
     * Sets the ball to all white.
     */
    private void appearBall() {
        ballColor = Color.argb(255, 255, 255, 255);
    }

    /**
     * Sets the single player mode. Needs to be called before constructor is
     * executed.
     *
     * @param mode Whether or not the game is being played in single player mode.
     */
    public void singlePlayerMode(boolean mode) {
        singlePlayerMode = mode;
    }

    /**
     * Getter for whether or not the game has been stopped.
     *
     * @return Whether or not the game has been stopped.
     */
    public boolean hasGameStopped() {
        return this.stopGame;
    }

    /**
     * Getter for the ball.
     *
     * @return A reference to the ball.
     */
    public Ball getBall() {
        return ball;
    }

    /**
     * Setter for the ball.
     *
     * @param ball The new ball.
     */
    public void setBall(Ball ball) {
        this.ball = ball;
    }
}