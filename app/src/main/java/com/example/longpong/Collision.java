/*
 * NOTES:
 * Padding: The padding is defined as how much to increment the 
 * 			coordinates by to get better collision visually.
 */

package com.example.longpong;

import static com.example.longpong.LongPongActivity.DEBUG_MODE;
import java.util.ArrayList;
import android.util.Log;

/**
 * Handles collision for all of the GameObjects for the game.
 * 
 * @author John Qualls & Andrew Canastar
 * 
 */
public class Collision {
	private GameFramework game;

	/**
	 * Constructs a Collision object.
	 * 
	 * @param game
	 */
	public Collision(GameFramework game) {
		this.game = game;
	}

	/**
	 * Checks for possible collisions from now, until the next frame increment.
	 * 
	 * @param objects
	 *            The list of objects to check for collision.
	 * @param balls
	 *            The list of balls to check for collision.
	 * @Param game Used to get collision padding.
	 */
	public void collision(ArrayList<GameObject> objects, ArrayList<Ball> balls,
			GameFramework game) {
		// Check balls against each object in game
		for (int i = 0; i < balls.size(); i++) { // all the balls
			Ball ball = balls.get(i);
			for (int j = 0; j < objects.size(); j++) { // all the objects
														// against each ball
				GameObject object = objects.get(j); // The object to check
													// against the ball
				// Check for collision on each of the ball's sides
				Float speed = ball.getResultant();
				collideBottom(ball, object, speed);
				collideLeft(ball, object, speed);
				collideTop(ball, object, speed);
				collideRight(ball, object, speed);
			}
		}
	}

	/**
	 * Checks to see if ball's bottom side collides with another object's top
	 * side.
	 * 
	 * @param ball
	 *            The ball.
	 * @param object
	 *            The object to check against the ball
	 * @param speed
	 *            The speed used for collision detection.
	 */
	private void collideBottom(Ball ball, GameObject object, float speed) {
		// Variables for points to check
		float p1 = 0; // ball's point
		float p2 = 0; // object's point
		float xMax1 = 0; // The ball's max x value
		float xMin1 = 0; // The ball's min x value
		float xMax2 = 0; // The object's max x value
		float xMin2 = 0; // The object's min x value
		float lamda = 0; // when collision happens
		String bigger = ""; // for comparison of sizes

		// Check top to bottom face collision
		p1 = ball.getY() + ball.getHeight() / 2; // ball's bottom side
		p2 = object.getY() - object.getHeight() / 2; // object's top side
		if (p1 > p2) { // Only check if ball is above the object
			bigger = object.compareWidth(ball, object); // Check to see which is
														// bigger, ball or
														// object
			lamda = Math.abs((p2 - p1) / speed); // Calculate at what time they
												// collide
			xMax1 = ball.getX() + ball.getWidth() / 2; // Get the Width range to
														// check
			xMin1 = ball.getX() - ball.getWidth() / 2;
			xMax2 = object.getX() + object.getWidth() / 2;
			xMin2 = object.getX() - object.getWidth() / 2;

			if (lamda <= 1) { // Collision occurs if lamda is less than 1, the
								// time at next iteration
				if (bigger.equals("object1")) { // If ball is bigger see if
												// object is within ball's width
					if (xMax2 <= xMax1 && xMax2 >= xMin1 || xMin2 <= xMax1
							&& xMin2 >= xMin1) {
						ball.setDirection("bottom");
						action(ball, object); // perform an action based on
												// collision
					}
				}
				if (bigger.equals("object2")) { // object is bigger, check if
												// ball is within object's width
					if (xMax1 <= xMax2 && xMax1 >= xMin2 || xMin1 <= xMax2
							&& xMin1 >= xMin2) {
						// display(ball, object, lamda);
						ball.setDirection("bottom");
						action(ball, object); // perform an action based on
												// collision
					}
				}
			}
		}
	}

	/**
	 * Checks to see if ball's left side collides with another object's right
	 * side.
	 * 
	 * @param ball
	 *            The ball.
	 * @param object
	 *            The object to check against the ball
	 * @param speed
     *            The speed used for collision detection.
	 */
	private void collideLeft(Ball ball, GameObject object, float speed) {
		// Log.d("COLLIDELEFT", "started collide left");

		// Variables for points to check
		float p1 = 0; // ball's point
		float p2 = 0; // object's point
		float yMax1 = 0; // The ball's max height value
		float yMin1 = 0; // The ball's min height value
		float yMax2 = 0; // The object's max height value
		float yMin2 = 0; // The object's min height value
		float lamda = 0; // when collision happens
		String bigger = ""; // for comparison of sizes

		// Check right to left face collision
		p1 = ball.getX() - ball.getWidth() / 2; // ball's left side
		p2 = object.getX() + object.getWidth() / 2; // object's right side

		if (p1 > p2) { // Only check if ball is to the right of the object
			bigger = object.compareHeight(ball, object); // Check to see which
															// is bigger, ball
															// or object
			lamda = Math.abs((p2 - p1) / speed); // Calculate at what time they
												// collide
			// display(ball, object, lamda);// DEBUG
			yMax1 = ball.getY() + ball.getHeight() / 2; // Get the height range
														// to check
			yMin1 = ball.getY() - ball.getHeight() / 2;
			yMax2 = object.getY() + object.getHeight() / 2;
			yMin2 = object.getY() - object.getHeight() / 2;

			if (lamda <= 1) { // Collision occurs if lamda is less than 1, the
								// time at next iteration
				if (bigger.equals("object1")) { // If ball is bigger see if
												// object is within ball's
												// height
					if (yMax2 <= yMax1 && yMax2 >= yMin1 || yMin2 <= yMax1
							&& yMin2 >= yMin1)
						ball.setDirection("straightRight");
					action(ball, object); // perform an action based on
											// collision
				}
				if (bigger.equals("object2")) { // object is bigger, check if
												// ball is within object's
												// height
					// Bounce ball straight back
					if (yMax1 <= yMax2 && yMax1 >= yMin2 && yMin1 <= yMax2
							&& yMin1 >= yMin2) {
						ball.setDirection("straightRight");
						action(ball, object); // perform an action based on
												// collision
					}

					// Bounce ball at an angle between 300 to 330 degrees
					else if (yMax1 <= yMax2 && yMax1 >= yMin2) {
						ball.setDirection("topRight");
						action(ball, object);
					}

					// Bounce ball at an angle between 30 and 60
					else if (yMin1 <= yMax2 && yMin1 >= yMin2) {
						ball.setDirection("bottomRight");
						action(ball, object);
					}
				}
			}
		}
	}

	/**
	 * Checks to see if ball's top side collides with another object's bottom
	 * side.
	 * 
	 * @param ball
	 *            The ball.
	 * @param object
	 *            The object to check against the ball
	 * @param speed
     *            The speed used for collision detection.
	 */
	private void collideTop(Ball ball, GameObject object, float speed) {
		// Variables for points to check
		float p1 = 0; // ball's point
		float p2 = 0; // object's point
		float xMax1 = 0; // The ball's max height value
		float xMin1 = 0; // The ball's min height value
		float xMax2 = 0; // The object's max height value
		float xMin2 = 0; // The object's min height value
		float lamda = 0; // when collision happens
		String bigger = ""; // for comparison of sizes

		// Check top to bottom face collision
		p1 = ball.getY() - ball.getHeight() / 2; // ball's top side
		p2 = object.getY() + object.getHeight() / 2; // object's bottom side
		if (p1 < p2) { // Only check if ball is under the object
			bigger = object.compareWidth(ball, object); // Check to see which is
														// bigger, ball or
														// object
			lamda = Math.abs((p2 - p1) / speed); // Calculate at what time they
												// collide
			xMax1 = ball.getX() + ball.getWidth() / 2; // Get the Width range to
														// check
			xMin1 = ball.getX() - ball.getWidth() / 2;
			xMax2 = object.getX() + object.getWidth() / 2;
			xMin2 = object.getX() - object.getWidth() / 2;

			if (lamda <= 1) { // Collision occurs if lamda is less than 1, the
								// time at next iteration
				if (bigger.equals("object1")) { // If ball is bigger see if
												// object is within ball's width
					if (xMax2 <= xMax1 && xMax2 >= xMin1 || xMin2 <= xMax1
							&& xMin2 >= xMin1) {
						ball.setDirection("top");
						action(ball, object); // perform an action based on
												// collision
					}
				}
				if (bigger.equals("object2")) { // object is bigger, check if
												// ball is within object's width
					if (xMax1 <= xMax2 && xMax1 >= xMin2 || xMin1 <= xMax2
							&& xMin1 >= xMin2) {
						ball.setDirection("top");
						action(ball, object); // perform an action based on
												// collision
					}
				}
			}
		}
	}

	/**
	 * Checks to see if ball's right side collides with another object's left
	 * side.
	 * 
	 * @param ball
	 *            The ball.
	 * @param object
	 *            The object to check against the ball
	 * @param speed
     *            The speed used for collision detection.
	 */
	private void collideRight(Ball ball, GameObject object, float speed) {
		// Variables for points to check
		float p1 = 0; // ball's point
		float p2 = 0; // object's point
		float yMax1 = 0; // The ball's max height value
		float yMin1 = 0; // The ball's min height value
		float yMax2 = 0; // The object's max height value
		float yMin2 = 0; // The object's min height value
		float lamda = 0; // when collision happens
		String bigger = ""; // for comparison of sizes

		// Check right to left face collision
		p1 = ball.getX() + ball.getWidth() / 2; // ball's right side
		p2 = object.getX() - object.getWidth() / 2; // object's left side

		if (p1 < p2) { // Only check if ball is to the left of the object
			bigger = object.compareHeight(ball, object); // Check to see which
															// is bigger, ball
															// or object
			lamda = Math.abs((p2 - p1) / speed); // Calculate at what time they
												// collide
			// display(ball, object, lamda);// DEBUG
			yMax1 = ball.getY() + ball.getHeight() / 2; // Get the height range
														// to check
			yMin1 = ball.getY() - ball.getHeight() / 2;
			yMax2 = object.getY() + object.getHeight() / 2;
			yMin2 = object.getY() - object.getHeight() / 2;

			if (lamda <= 1) {// Collision occurs if lambda is less than 1, the
								// time at next iteration
				if (bigger.equals("object1")) { // If ball is bigger see if
												// object is within ball's
												// height
					if (yMax2 <= yMax1 && yMax2 >= yMin1 || yMin2 <= yMax1
							&& yMin2 >= yMin1)
						display(ball, object, lamda);
					ball.setDirection("straightLeft");
					action(ball, object); // perform an action based on
											// collision
				}
				if (bigger.equals("object2")) { // object is bigger, check if
												// ball is within object's
												// height
					// Bounce ball straight back
					if (yMax1 <= yMax2 && yMax1 >= yMin2 && yMin1 <= yMax2
							&& yMin1 >= yMin2) {
						// display(ball,object,lamda);
						ball.setDirection("straightLeft");
						action(ball, object); // perform an action based on
												// collision
					}

					// Bounce ball at an angle between 130 to 150 degrees
					else if (yMax1 <= yMax2 && yMax1 >= yMin2) {
						ball.setDirection("topLeft");
						action(ball, object);
					}

					// Bounce ball at an angle between 210 and 240
					else if (yMin1 <= yMax2 && yMin1 >= yMin2) {
						ball.setDirection("bottomLeft");
						action(ball, object);
					}
				}
			}
		}
	}

	/**
	 * Perform in game events based on what the ball collides with.
	 * 
	 * @param ball
	 *            The in game ball.
	 * @param object
	 *            The object that collided with the ball.
	 */
	private void action(Ball ball, GameObject object) {
		// Perform specific action based on object type
		if (object.getType().equals("paddle") ||
		    object.getType().equals("test wall")) {
			// Speed up ball slightly
			ball.changeSpeed(.2f, .2f);
			if(DEBUG_MODE)
			    Log.i("Collision", "paddle");
			// Turn angle based on location of collision
			String dir = ball.getDirection();

			/*
			 * Right to left collision
			 */
			if (dir.equals("straightLeft")) {
			    if(DEBUG_MODE)
	                Log.i("Collision", "Right to Left Collision");
				ball.setAngle(180);
			}

			else if (dir.equals("topLeft")) {
			    if(DEBUG_MODE)
                    Log.i("Collision", "Top Left Collision");
				// get random number between 210 to 240
				int num = 210 + (int) (Math.random() * ((240 - 210) + 1));
				ball.setAngle(num);
			}

			else if (dir.equals("bottomLeft")) {
			    if(DEBUG_MODE)
                    Log.i("Collision", "Bottom Left Collision");
				// get random number between 130 to 150
				int num = 130 + (int) (Math.random() * ((150 - 130) + 1));
				ball.setAngle(num);
			}

			/*
			 * Left to right collision
			 */
			else if (dir.equals("straightRight")) {
			    if(DEBUG_MODE)
                    Log.i("Collision", "Left To Right Collision");
				ball.setAngle(0);
			}

			else if (dir.equals("topRight")) {
			    if(DEBUG_MODE)
                    Log.i("Collision", "Top Right Collision");
				// get random number between 300 to 330
				int num = 300 + (int) (Math.random() * ((330 - 300) + 1));
				ball.setAngle(num);
			}

			else if (dir.equals("bottomRight")) {
			    if(DEBUG_MODE)
                    Log.i("Collision", "Bottom Right Collision");
				// get random number between 30 to 60
				int num = 30 + (int) (Math.random() * ((60 - 30) + 1));
				ball.setAngle(num);
			}

			/*
			 * Top or bottom collision
			 */
			else if (dir.equals("top") || dir.equals("bottom")) {
			    if(DEBUG_MODE)
                    Log.i("Collision", "Top or Bottom Collision");
				ball.turn(180);
			}
		}

		else if (object.getType().equals("exit")) {
			Log.i("Collision", "EXIT!!");
			game.toggleHasBall();
			game.loseBall(GameFramework.EXIT);
		}

		else if (object.getType().equals("goal")) {
			Log.i("Collision", "GOAL!!");
			game.toggleHasBall();
			game.loseBall(GameFramework.GOAL);
		}

		else if (object.getType().equals("wall")) {
			// Emulate a realistic bounce when colliding with wall
			reflectionAngle(ball, object);
		}
	}

	/**
	 * Calculates the reflection angle of the ball's vector to emulate a
	 * realistic bounce.
	 * 
	 * @param ball
	 *            The ball.
	 * @param object
	 *            The object the ball is bouncing from.
	 */
	private void reflectionAngle(Ball ball, GameObject object) {
		// Reflect angle based on current angle
		float angle = ball.getAngle();
		if (angle < 360 && angle > 270) {
			ball.setAngle(45);
		}

		else if (angle < 270 && angle > 180) {
			ball.setAngle(130);
		}

		else if (angle < 90 && angle > 0) {
			ball.setAngle(315);
		}

		else if (angle < 180 && angle > 90) {
			ball.setAngle(225);
		}

		/*
		 * // Create the required vector's Triple bVector = new
		 * Triple(ball.getX(), ball.getY(), 0); // Ball vector Triple oVector =
		 * new Triple(object.getX(), object.getY(), 0);// Object vector Triple n
		 * = new Triple(oVector.x, oVector.y, 0);
		 * 
		 * // Find reflection angle based on above vectors Triple reflectionVect
		 * = bVector.vectorTo(n.scalarProduct(2*(n.dotProduct(bVector) /
		 * n.dotProduct(n))));
		 * 
		 * Log.i("Angle", "orig: " + bVector + " reflect: " + reflectionVect);
		 * // Find reflection angle float reflectAngle =
		 * (float)Math.toRadians(Math.atan(reflectionVect.y /
		 * reflectionVect.x)); float x = (float)Math.cos(reflectAngle); float y
		 * = (float)Math.sin(reflectAngle); Log.i("Angle", "reflect pos: " + x +
		 * " , " + y);
		 * 
		 * // Change ball's angle to reflection angle
		 * ball.setAngle(reflectAngle); //Log.d("Angle", "Angle: " + angle);
		 */
	}

	/**
	 * Logs the ball's instance variables using Android logging.
	 * @param ball
	 * @param object
	 * @param lamda
	 */
	private void display(Ball ball, GameObject object, float lamda) {
		Log.d("COORDS",
				"Ball: " + ball.toString() + "\nObject: " + object.toString()
						+ "\nlambda:" + lamda);

	}
}
