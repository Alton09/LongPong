package com.example.longpong;

/**
 * Contains basic information about every object in the game.
 * float values are used for precise collision detection.
 * precision.
 * @author John Qualls & Andrew Canastar
 *
 */
public class GameObject {
	
	// Object's position
	protected float x;
	protected float y;
	
	// Object's size
	protected float width;
	protected float height;
	
	// Type of object
	protected String type;
	
	/**
	 *  Creates a basic object with coordinates, size, and type.
	 * @param x x coordinate.
	 * @param y y coordinate.
	 * @param width Width of object.
	 * @param height Height of object.
	 */
	public GameObject(float x, float y, float width, float height,
						String type) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
	}
	
	/**
	 * Getter method for width.
	 * @return width
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * Getter method for height.
	 * @return height
	 */
	public float getHeight() {
		return height ;
	}
	
	/**
	 * Getter method for x.
	 * @return x
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Getter method for y.
	 * @return y
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Setter method for x.
	 * @param x
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Setter method for y.
	 * @param y
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Getter method for type.
	 * @return type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Returns a string of the object's type.
	 * @return type
	 */
	@Override
	public String toString() {
		return type;
	}
	
	/**
	 * Checks to see which of two GameObjects has a larger height.
	 * @param object1
	 * @param object2
	 * @return either object1 or object2
	 */
	public String compareHeight(GameObject object1, GameObject object2) {
		// Return Object with greater height
		if(object1.getHeight() > object2.getHeight())
			return "object1";
		else
			return "object2";
	}

	/**
	 * Checks to see which of two GameObjects has a larger width.
	 * @param object1
	 * @param object2
	 * @return either object1 or object2
	 */
	public String compareWidth(GameObject object1, GameObject object2) {
		// Return Object with greater height
		if(object1.getWidth() > object2.getWidth())
			return "object1";
		else
			return "object2";
	}
}
