package com.example.longpong;

import static com.example.longpong.LongPongActivity.DEBUG_MODE;
import android.util.Log;

/**
 * The main ball that is used to score points.
 * 
 * @author John Qualls
 * @author Andrew Canastar
 * @version 1.0
 */
public class Ball extends GameObject {
    private Velocity velocity;
    private String direction; // Used for collision detection

    private float multiplier;

    /**
     * Creates a new ball object.
     * 
     * @param x x coordinate.
     * @param y y coordinate.
     * @param width Width of ball.
     * @param height Height of ball.
     */
    public Ball(float x, float y, float width, float height, String type, 
                Velocity v) {
        super(x, y, width, height, type);
        velocity = v;
    }

    /**
     * Changes the location of the ball's x and y coordinates to simulate
     * movement. Each time this method is called, the x variable is updated by
     * the speed * cos(angle) and the y variable is updated by the speed *
     * sin(angle).
     */
    public void updatePosition() {
        float angle = velocity.getAngle();
        x += velocity.calcXComponent();
        y += velocity.calcYComponent();
    }

    /**
     * Changes the speed of the ball by summing its current speed with the
     * amount parameter.
     * 
     * @param amountX The amount to accelerate or decelerate the ball's x component.
     */
    public void changeSpeed(float amountX, float amountY) {
        velocity.changeSpeed(amountX, amountY);
    }

    /**
     * Changes the angle of the ball by summing its current angle with the
     * amount parameter. Maintains the angle as a value from 0 to 360.
     * 
     * @param amount The amount to turn the ball.
     */
    public void turn(float amount) {
        velocity.changeAngle(Math.abs(amount));
        
        float angle = velocity.getAngle();
        // Keep values from 0 to 360 degrees
        if (angle + amount >= 360)
            velocity.setAngle(Math.abs(angle - 360));

        Log.d("Angle", "Angle: " + velocity.getAngle());
    }

    /**
     * Resets the ball's speed
     */
    public void resetSpeed() {
        velocity.setSpeed(multiplier, multiplier);
    }

    /**
     * Set method for the speed.
     * 
     * @param amount The new speed.
     */
    public void setSpeed(float amountX, float amountY) {
        velocity.setSpeed(amountX, amountY);
    }

    /**
     * Get method for the X speed component.
     * 
     * @return X speed component.
     */
    public float getSpeedX() {
        return velocity.getXSpeed();
    }
    
    /**
     * Get method for the Y speed component.
     * 
     * @return Y speed component.
     */
    public float getSpeedY() {
        return velocity.getYSpeed();
    }

    /**
     * NOT YET IMPLEMENTED Returns a String containing the ball's type and
     * speed.
     * 
     * @return The string representation of the ball.
     */
    /*@Override
    public String toString() {
        return super.toString() + " s: " + speed;
    }*/

    /**
     * Set method for angle.
     * 
     * @param amount The new angle.
     */
    public void setAngle(float amount) {
        velocity.setAngle(amount);

        if (DEBUG_MODE)
            Log.i("Angle", "Angle: " + velocity.getAngle());
    }

    /**
     * Get method for the direction.
     * 
     * @return The ball's direction.
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Set method for the direction.
     * 
     * @param direction The new direction.
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Sets the position, speed, and angle of a ball.
     * 
     * @param x The new x position.
     * @param y The new y position.
     * @param speed The new speed.
     * @param angle The new angle
     */
    public void setStartPosition(float x, float y, Velocity velocity) {
        this.x = x;
        this.y = y;
        this.velocity = velocity;
    }
    
    /**
     * Returns the ball's resultant speed vectorfrom adding the X and Y velocity components.
     * @return The resultant speed vector
     */
    public float getResultant() {
        return velocity.getResultant();
    }
    
    /**
     * Returns the ball's angle.
     * @return The ball's angle.
     */
    public float getAngle() {
        return velocity.getAngle();
    }

    /**
     * Returns the Ball's dpi multiplier.
     * @return The multiplier.
     */
    public float getMultiplier() {
        return multiplier;
    }
    /**
     * Sets the Ball's dpi multiplier.
     * @param multiplier The new multiplier.
     */
    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }
}
