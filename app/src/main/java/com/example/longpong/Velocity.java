package com.example.longpong;

/**
 * The velocity of a GameObject. Velocity is a vector, thus this object contains
 * an X component, Y component, and angle for direction.
 * 
 * @author John Qualls
 * @version 1.0
 * 
 */
public class Velocity {
    private final boolean DEBUG_MODE = false;
    private float speedX, speedY, angle;

    /**
     * Sets this Velocity Object's X component, Y component, and angle.
     * @param speedX The Velocity's X magnitude
     * @param speedY The Velocity's Y magnitude
     * @param angle The Velocity's angle
     */
    public Velocity(float speedX, float speedY, float angle) {
        this.speedX = speedX;
        this.speedY = speedY;
        this.angle = angle;
    }
    
    /**
     * Gets the X speed of the velocity.
     * @return The X speed of the velocity.
     */
    public float getXSpeed() {
        return this.speedX;
    }
    
    /**
     * Gets the Y speed of the velocity.
     * @return The Y speed of the velocity.
     */
    public float getYSpeed() {
        return this.speedY;
    }
    
    /**
     * Sets the angle of the velocity.
     * @return The angle of the velocity.
     */
    public float getAngle() {
        return this.angle;
    }
    
    /**
     * Sets the X speed of the velocity.
     * @param speedX The X speed of the velocity.
     */
    public void setXspeed(float speedX) {
        this.speedX = speedX;
    }
    
    /**
     * Sets the Y speed of the velocity.
     * @param speedY The Y speed of the velocity.
     */
    public void setYspeed(float speedY) {
        this.speedY = speedY;
    }
    
    /**
     * Sets both the X and Y speeds of the velocity.
     * @param speedX The X speed
     * @param speedY The Y speed
     */
    public void setSpeed(float speedX, float speedY) {
        setXspeed(speedX);
        setYspeed(speedY);
    }
    
    
    /**
     * Sets the angle of the velocity.
     * @param angle The angle of the velocity.
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }
    
    /**
     * Sums the current speed.
     * @param amountX Amount added to X component.
     * @param amountY Amount added to Y component.
     */
    public void changeSpeed(float amountX,float amountY) {
        this.speedX += amountX;
        this.speedY += amountY;
    }
    
    /**
     * Sums the current angle.
     * @param angle Amount added to angle.
     */
    public void changeAngle(float angle) {
        this.angle += angle;
    }

    /**
     * Returns the ball's resultant speed vector from adding the X and Y velocity components.
     * @return The resultant speed vector
     */
    public float getResultant() {
        double xComponent = (double) this.calcXComponent(),
               yComponent = (double) this.calcYComponent();
        if(DEBUG_MODE)
            System.out.println("xComponent" + xComponent + "\nyComponent " + yComponent);
        double a = Math.pow(xComponent, 2.0),
               b = Math.pow(yComponent, 2.0);
        if(DEBUG_MODE)
            System.out.println("a: " + a + "\nb: " + b);
     return (float) Math.sqrt(a + b);
    }
    
    /**
     * Calculates the X vector component.
     * @return The X vector component
     */
    public float calcXComponent() {
        return (float) (speedX * Math.cos(Math.toRadians(angle)));
    }
    
    /**
     * Calculates the Y vector component.
     * @return The Y vector component
     */
    public float calcYComponent() {
        return (float) (speedY * Math.sin(Math.toRadians(angle)));
    }
    
    /**
     * String representation of the velocity's resultant and angle.
     */
    @Override
    public String toString() {
        return "Velocity:\n" + "Speed = " + getResultant() + "\n" +
                "Angle = " + getAngle();
    }
}
