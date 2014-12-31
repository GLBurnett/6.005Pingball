package pingBall;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import physics.*;

/**
 * Mutable gadgets of pingball game
 * <p>Gadgets consists of square bumper, circle bumper, triangle bumper, flipper, absorber, outer walls and ball.
 * Bumpers are things that each have their own specific shape, location, and triggered behavior (when ball hit them).
 * Bumpers only rotated relative to the center of their bounding box but their origin never changes.
 * Outer walls can be either visible (ball will hit and reflect) or invisible (ball will pass through)
 * Balls can move and collide with each other.
 * Orientation, if applicable, can be 0, 90, 180, or 270 degrees
 * The origin of a standard (everything except Ball) Gadget is the top left corner of its bounding box
 * </p>
 *
 */

public interface Gadget {
    /**
     * Creates an immutable Square Bumper with the following properties:
     * Size and shape: a square shape with edge length 1L
     *Orientation: not applicable (symmetric to 90 degree rotations)
     *Coefficient of reflection: 1.0
     *Trigger: generated whenever the ball hits it
     * @param x x-coordinate of position of square bumper
     * @param y y-coordinate of position of square bumper
     * @return a Square bumper whose origin is at (x, y)
     */
    public static Gadget squareBumper(double x,double y){
        return new SquareBumper(x, y, new ArrayList<Gadget>());
    }
    /**
     * Creates an immutable Square Bumper with the following properties:
     * Size and shape: a square shape with edge length 1L
     *Orientation: not applicable (symmetric to 90 degree rotations)
     *Coefficient of reflection: 1.0
     *Trigger: generated whenever the ball hits it
     * @param x x-coordinate of position of square bumper
     * @param y y-coordinate of position of square bumper
     * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered
     * @return a Square bumper whose origin is at (x, y)
     */
    public static Gadget squareBumper(double x, double y, List<Gadget> triggeredGadgets){
        return new SquareBumper(x, y, triggeredGadgets);
    }
    /**
     * Constructs a Circle Bumper with the following properties:
    * Size and shape: a circular shape with diameter 1L
    * Coefficient of reflection: 1.0
    *Orientation: not applicable (symmetric to 90 degree rotations)
    *Trigger: generated whenever the ball hits it
    *  @param x x-coordinate of position of circle bumper
    * @param y y-coordinate of position of circle bumper
    * @return a Circle bumper whose origin is at (x, y) whose center is at (x + 0.5, y + 0.5)
    */
    public static Gadget circleBumper(double x, double y){
        return new CircleBumper(x,y);
    }
    /**
     * Constructs a Circle Bumper with the following properties:
    * Size and shape: a circular shape with diameter 1L
    * Coefficient of reflection: 1.0
    *Orientation: not applicable (symmetric to 90 degree rotations)
    *Trigger: generated whenever the ball hits it
    *  @param x x-coordinate of position of circle bumper
    * @param y y-coordinate of position of circle bumper
    * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered
    * @return a Circle bumper whose origin is at (x, y) whose center is at (x + 0.5, y + 0.5)
    */
    public static Gadget circleBumper(double x, double y, List<Gadget> triggeredGadgets){
        return new CircleBumper(x,y, triggeredGadgets);
    }

    /**
     * Creates a triangle bumper with the following properties:
     * Size and shape: a right-triangular shape with sides of length 1L and hypotenuse of length Sqrt(2)L
     * Orientation: the default orientation (0 degrees) places one corner in the northeast, one corner in the northwest, and the last corner in the southwest. The diagonal goes from the southwest corner to the northeast corner.
     * Coefficient of reflection: 1.0
     * Trigger: generated whenever the ball hits it
     * @param x x-coordinate of triangle bumper origin
     * @param y y-coordinate of triangle bumper origin
     * @param orientation rotation of the triangle bumper. default is 0
     * @return a triangle bumper whose origin is at (x,y) and whose orientation is orientation
     */
    public static Gadget triangleBumper(double x, double y, double orientation){
        return new TriangleBumper(x,y,orientation, new ArrayList<Gadget>());
    }

    /**
     * Creates a triangle bumper with the following properties:
     * Size and shape: a right-triangular shape with sides of length 1L and hypotenuse of length Sqrt(2)L
     * Orientation: the default orientation (0 degrees) places one corner in the northeast, one corner in the northwest, and the last corner in the southwest. The diagonal goes from the southwest corner to the northeast corner.
     * Coefficient of reflection: 1.0
     * Trigger: generated whenever the ball hits it
     * @param x x-coordinate of triangle bumper origin
     * @param y y-coordinate of triangle bumper origin
     * @return a triangle bumper whose origin is at (x,y)
     */
    public static Gadget triangleBumper(double x, double y){
        return new TriangleBumper(x,y,0.0, new ArrayList<Gadget>());
    }

    /**
     * Creates a triangle bumper with the following properties:
     * Size and shape: a right-triangular shape with sides of length 1L and hypotenuse of length Sqrt(2)L
     * Orientation: the default orientation (0 degrees) places one corner in the northeast, one corner in the northwest, and the last corner in the southwest. The diagonal goes from the southwest corner to the northeast corner.
     * Coefficient of reflection: 1.0
     * Trigger: generated whenever the ball hits it
     * @param x x-coordinate of triangle bumper origin
     * @param y y-coordinate of triangle bumper origin
     * @param orientation rotation of the triangle bumper. default is 0
     * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered default value is none
     * @return a triangle bumper whose origin is at (x,y) and whose orientation is orientation
     */

    public static Gadget triangleBumper(double x, double y, double orientation, List<Gadget> triggeredGadgets){
        return new TriangleBumper(x,y,orientation, triggeredGadgets);
    }

    /**
     * Creates a triangle bumper with the following properties:
     * Size and shape: a right-triangular shape with sides of length 1L and hypotenuse of length Sqrt(2)L
     * Orientation: the default orientation (0 degrees) places one corner in the northeast, one corner in the northwest, and the last corner in the southwest. The diagonal goes from the southwest corner to the northeast corner.
     * Coefficient of reflection: 1.0
     * Trigger: generated whenever the ball hits it
     * @param x x-coordinate of triangle bumper origin
     * @param y y-coordinate of triangle bumper origin
     * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered default value is none
     * @return a triangle bumper whose origin is at (x,y)
     */
    public static Gadget triangleBumper(double x, double y, List<Gadget> triggeredGadgets){
        return new TriangleBumper(x,y,0.0, triggeredGadgets);
    }

    /**
     * Creates a Left Flipper with the following properties:
     * Size and shape: A generally-rectangular rotating shape with bounding box of size 2L × 2L
     * Orientation: For a left flipper, the default orientation (0 degrees) places the flipper’s pivot point in the northwest corner.
     * Coefficient of reflection: 0.95 (but see below)
     * Trigger: generated whenever the ball hits it
     * Action: rotates 90 degrees, as described below
     * @param x x-coordinate of Flipper origin
     * @param y y-coordinate of Flipper origin
     * @return a Flipper with origin (x,y)
     */
     public static Gadget leftFlipper(double x, double y){
         return new LeftFlipper(x,y);
     }
     /**
      * Creates a Left Flipper with the following properties:
      * Size and shape: A generally-rectangular rotating shape with bounding box of size 2L × 2L
      * Orientation: For a left flipper, the default orientation (0 degrees) places the flipper’s pivot point in the northwest corner.
      * Coefficient of reflection: 0.95 (but see below)
      * Trigger: generated whenever the ball hits it
      * Action: rotates 90 degrees, as described below
      * @param x x-coordinate of Flipper origin
      * @param y y-coordinate of Flipper origin
      * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered default value is none
      * @return a Flipper with origin (x,y)
      */
     public static Gadget leftFlipper(double x, double y, List<Gadget> triggeredGadgets){
         return new LeftFlipper(x,y, triggeredGadgets);
     }
     /**
      * Creates a Left Flipper with the following properties:
      * Size and shape: A generally-rectangular rotating shape with bounding box of size 2L × 2L
      * Orientation: For a left flipper, the default orientation (0 degrees) places the flipper’s pivot point in the northwest corner.
      * Coefficient of reflection: 0.95 (but see below)
      * Trigger: generated whenever the ball hits it
      * Action: rotates 90 degrees, as described below
      * @param x x-coordinate of Flipper origin
      * @param y y-coordinate of Flipper origin
      * @param orientation rotation of the left flipper.
      * @return a Flipper with origin (x,y) with orientation.
      */
     public static Gadget leftFlipper(double x, double y, double orientation){
         return new LeftFlipper(x,y, orientation);
     }
     /**
      * Creates a Left Flipper with the following properties:
      * Size and shape: A generally-rectangular rotating shape with bounding box of size 2L × 2L
      * Orientation: For a left flipper, the default orientation (0 degrees) places the flipper’s pivot point in the northwest corner.
      * Coefficient of reflection: 0.95 (but see below)
      * Trigger: generated whenever the ball hits it
      * Action: rotates 90 degrees, as described below
      * @param x x-coordinate of Flipper origin
      * @param y y-coordinate of Flipper origin
      * @param orientation rotation of the left flipper.
      * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered default value is none
      * @return a Flipper with origin (x,y) with orientation.
      */
     public static Gadget leftFlipper(double x, double y, double orientation, List<Gadget> triggeredGadgets){
         return new LeftFlipper(x,y, orientation, triggeredGadgets);
     }
    /**
     * Creates a Right Flipper with the following properties:
     * Size and shape: A generally-rectangular rotating shape with bounding box of size 2L × 2L
     * Orientation: For a right flipper, the default orientation puts the pivot point in the northeast corner (0 degrees).
     * Coefficient of reflection: 0.95 (but see below)
     * Trigger: generated whenever the ball hits it
     * Action: rotates 90 degrees, as described below
     * @param x x-coordinate of Flipper origin
     * @param y y-coordinate of Flipper origin
     * @return a Flipper with origin (x,y)
     */
    public static Gadget rightFlipper(double x, double y){
        return new RightFlipper(x,y);
    }
    /**
     * Creates a Right Flipper with the following properties:
     * Size and shape: A generally-rectangular rotating shape with bounding box of size 2L × 2L
     * Orientation: For a right flipper, the default orientation puts the pivot point in the northeast corner (0 degrees).
     * Coefficient of reflection: 0.95 (but see below)
     * Trigger: generated whenever the ball hits it
     * Action: rotates 90 degrees, as described below
     * @param x x-coordinate of Flipper origin
     * @param y y-coordinate of Flipper origin
     * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered default value is none
     * @return a Flipper with origin (x,y)
     */

    public static Gadget rightFlipper(double x, double y, List<Gadget> triggeredGadgets){
        return new RightFlipper(x,y, triggeredGadgets);
   }
    /**
     * Creates a Right Flipper with the following properties:
     * Size and shape: A generally-rectangular rotating shape with bounding box of size 2L × 2L
     * Orientation: For a right flipper, the default orientation puts the pivot point in the northeast corner (0 degrees).
     * Coefficient of reflection: 0.95 (but see below)
     * Trigger: generated whenever the ball hits it
     * Action: rotates 90 degrees, as described below
     * @param x x-coordinate of Flipper origin
     * @param y y-coordinate of Flipper origin
     * @param orientation rotation of the  right flipper.
     * @return a Flipper with origin (x,y) with orientation.
     */
    public static Gadget rightFlipper(double x, double y, double orientation){
        return new RightFlipper(x,y, orientation);
    }
    /**
     * Creates a Right Flipper with the following properties:
     * Size and shape: A generally-rectangular rotating shape with bounding box of size 2L × 2L
     * Orientation: For a right flipper, the default orientation puts the pivot point in the northeast corner (0 degrees).
     * Coefficient of reflection: 0.95 (but see below)
     * Trigger: generated whenever the ball hits it
     * Action: rotates 90 degrees, as described below
     * @param x x-coordinate of Flipper origin
     * @param y y-coordinate of Flipper origin
     * @param orientation rotation of the  right flipper.
     * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered default value is none
     * @return a Flipper with origin (x,y) at orientation
     */
    public static Gadget rightFlipper(double x, double y, double orientation, List<Gadget> triggeredGadgets){
        return new RightFlipper(x,y, orientation, triggeredGadgets);
   }

    /**
     * Creates an absorber with the following properties:
     * Size and shape: A rectangle kL × mL where k and m are positive integers <= 20
     * Orientation: not applicable (only one orientation is allowed)
     * Coefficient of reflection: not applicable; the ball is captured
     * Trigger: generated whenever the ball hits it
     * Action: shoots out a stored ball
     * if x balls hit it at exactly the same time (x > 1), spit out (x-1) balls and keep 1 ball
     * @param x - x coordinate of origin of absorber
     * @param y - y coordinate of origin of absorber
     * @param k - height of absorber (in L)
     * @param m - width of absorber (in L)
     * @return an kL x mL absorber at (x,y)
     */
    public static Gadget absorber(double x, double y, int k, int m){
        return new Absorber(x, y, k, m, new ArrayList<Gadget>());
    }
    /**
     * Creates an absorber with the following properties:
     * Size and shape: A rectangle kL × mL where k and m are positive integers <= 20
     * Orientation: not applicable (only one orientation is allowed)
     * Coefficient of reflection: not applicable; the ball is captured
     * Trigger: generated whenever the ball hits it
     * Action: shoots out a stored ball
     * if x balls hit it at exactly the same time (x > 1), spit out (x-1) balls and keep 1 ball
     * @param x - x coordinate of origin of absorber
     * @param y - y coordinate of origin of absorber
     * @param k - height of absorber (in L)
     * @param m - width of absorber (in L)
     * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered default value is none
     * @return an kL x mL absorber at (x,y)
     */
    public static Gadget absorber(double x, double y, int k, int m, List<Gadget> triggeredGadgets){
        return new Absorber(x, y, k, m, triggeredGadgets);
    }

    /**
     * Creates the border walls surrounding the playfield.
     * Trigger: none
     * Action: none
     * Coefficient of reflection: 1.0 if solid, otherwise passes through.
     * @param invisTop True if top outerwall is invisible
     * @param invisRight True if right outerwall is invisible
     * @param invisBottom True if bottom outerwall is invisible
     * @param invisLeft True if left outerwall is invisible
     * @return an OuterWall object representing the four outerwalls of the board
     */
    public static Gadget outerwall(boolean invisTop, boolean invisRight, boolean invisBottom, boolean invisLeft){
        return new OuterWall(invisTop, invisRight, invisBottom, invisLeft);
    }
    
    /**
     * Carries out events when the ball collides with this Gadget.
     * Changes the velocity of the Ball if it collides with a reflective Gadget
     * @param Ball object that collides with Gadget
     */
    public void collision(Ball ball);


    /**
     * Gets time until ball collides with the Gadget object
     * @param ball Ball that may collide with the Gadget object
     * @return time (in sec) that it will take for Ball to collide with the Gadget object
     */
    public double timeUntilCollision(Ball ball);

    /**
     *
     * @return a vector representation of position of gadget on the board (top left corner of bounding box)
     */
    public Vect getPosition();
    /**
     * Carries out the action of the Gadget.
     */
    public void doAction();
    /**
     * Carries out actions of Gadgets that are triggered by this Gadget.
     */
    public void trigger();

    /**
     * Adds a gadget to this gadget's list of triggered objects
     * @param gadget the gadget to add to this gadget's list of triggered objects
     */
    /**
     * 
     * @return whether or not the gadget is a portal.
     * Portals behave differently than other gadgets
     */
    public boolean isPortal();
    
    public void addToTriggeredList(Gadget gadget);

    /**
     * @return 2D array ASCII representation of gadget
     */
    public String[][] toASCIIRep();
    /**
     * @param time in seconds
     * updates position of gadget on the board after time is passed.
     */
    public void updatePosition(double time);
    /**
     *
     * @return a list of balls that the Gadget is currently storing, if applicable. If not storing anything, return the empty list.
     */
    public List<Ball> getBallList();
    
    /**
     * Draws the shape of the gadget
     */
    public void drawShape(Graphics2D g);
}

