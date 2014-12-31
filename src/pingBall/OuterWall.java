package pingBall;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * An immutable gadget that represents the outer walls surrounding the play area.
 * The corners of the walls are (0L,0L), (0L,20L), (20L,0L), (20L,20L).
 * Each part of the outer wall (top, left, right, bottom) can be solid or invisible.
 * The reflection coefficient of the outer walls is 1.0
 * Trigger: None
 * Action: None
 */
public class OuterWall implements Gadget {
    //AF:
    //  Represents the outer boundary of the play area
    //RI:
    //  Outer walls are at (0L,0L), (0L,20L), (20L,0L), (20L,20L)
    //  A wall is only in walls if it is visible
    //  A corner is only in Corners if it is visible

    private LineSegment leftWall = new LineSegment(0, 0, 0, 20);
    private LineSegment rightWall = new LineSegment(20, 0, 20, 20);
    private LineSegment topWall = new LineSegment(0, 0, 20, 0);
    private LineSegment bottomWall = new LineSegment(0, 20, 20, 20);
    private List<LineSegment> wallsList = Arrays.asList(leftWall,rightWall,topWall,bottomWall); // visible walls
    private List<LineSegment> walls = new ArrayList<LineSegment>();
    private Circle topLeftCorner = new Circle(0, 0, 0);
    private Circle topRightCorner = new Circle(20, 0, 0);
    private Circle bottomLeftCorner = new Circle(0, 20, 0);
    private Circle bottomRightCorner = new Circle(20, 20, 0);
    private List<Circle> corners = new ArrayList<Circle>();
    private boolean invisTop;
    private boolean invisBottom;
    private boolean invisLeft;
    private boolean invisRight;
    private double reflectionCoeff = 1.0;

    /**
     * Constructs an outer wall, with each side being optionally invisible
     * @param invisTop true if the top wall is invisible, false otherwise
     * @param invisRight true if the right wall is invisible, false otherwise
     * @param invisBottom true if the bottom wall is invisible, false otherwise
     * @param invisLeft true if the left wall is invisible, false otherwise
     */
    public OuterWall(boolean invisTop, boolean invisRight, boolean invisBottom, boolean invisLeft) {
        this.invisTop = invisTop;
        this.invisRight = invisRight;
        this.invisBottom = invisBottom;
        this.invisLeft = invisLeft;
        List<Boolean> invisList = Arrays.asList(invisLeft, invisRight, invisTop, invisBottom);
        for (int i=0; i < 4; i++){
            if (!invisList.get(i)){//if not invisible, add it to walls
                walls.add(wallsList.get(i));  // removing all invisible walls from visible walls list
            }
        }
        //Check for invisible corners
        if (!invisTop && !invisLeft)
            corners.add(topLeftCorner);
        if (!invisTop && !invisRight)
            corners.add(topRightCorner);
        if (!invisBottom && !invisLeft)
            corners.add(bottomLeftCorner);
        if (!invisBottom && !invisRight)
            corners.add(bottomRightCorner);
        checkRep();
    }

    /**
     * 
     * @return true if top wall is invisible
     */
    public boolean isInvisTop() {
        return invisTop;
    }

    /**
     * 
     * @param invisTop
     * set the value of invisTop
     */
    public void setInvisTop(boolean invisTop) {
        this.invisTop = invisTop;
        updateWallsAndCorners();
    }

    private void updateWallsAndCorners() {
        List<Boolean> invisList = Arrays.asList(invisLeft, invisRight, invisTop, invisBottom);
        walls.clear();
        corners.clear();
        for (int i=0; i < 4; i++){
            if (!invisList.get(i)){//if not invisible, add it to walls
                walls.add(wallsList.get(i));  // removing all invisible walls from visible walls list
            }
        }
        //Check for invisible corners
        if (!invisTop && !invisLeft)
            corners.add(topLeftCorner);
        if (!invisTop && !invisRight)
            corners.add(topRightCorner);
        if (!invisBottom && !invisLeft)
            corners.add(bottomLeftCorner);
        if (!invisBottom && !invisRight)
            corners.add(bottomRightCorner);
        checkRep();
    }

    /**
     * 
     * @return true if bottom wall is invisible
     */
    public boolean isInvisBottom() {
        return invisBottom;
    }

    /**
     * 
     * @param invisBottom
     * set value of invisBottom
     */
    public void setInvisBottom(boolean invisBottom) {
        this.invisBottom = invisBottom;
        updateWallsAndCorners();
    }

    /**
     * 
     * @return is left wall is invisible
     */
    public boolean isInvisLeft() {
        return invisLeft;
    }

    /**
     * 
     * @param invisLeft
     * set value of invisLeft
     */
    public void setInvisLeft(boolean invisLeft) {
        this.invisLeft = invisLeft;
        updateWallsAndCorners();
    }

    /**
     * 
     * @return true if right wall is invisible
     */
    public boolean isInvisRight() {
        return invisRight;
    }

    /**
     * 
     * @param invisRight
     * set value of invisRight
     */
    public void setInvisRight(boolean invisRight) {
        this.invisRight = invisRight;
        updateWallsAndCorners();
    }


    /**
     * Updates velocity of ball to reflect off wall if ball collided with wall.
     * @param ball Ball object that collides with wall
     */
    @Override
    public void collision(Ball ball) {
        double minTime = timeUntilCollision(ball);
        List<LineSegment> collidedWalls = new ArrayList<>();
        //Checks which walls ball is colliding with, and reflect off of those walls
        for (LineSegment l: walls) {
            if ((Geometry.timeUntilWallCollision(l, new Circle(ball.getPosition(), 0.25), ball.getVelocity())==minTime) && !Double.isInfinite(minTime)){
                collidedWalls.add(l);
            }
        }
        for (LineSegment wall: collidedWalls) {
            ball.setVelocity(Geometry.reflectWall(wall, ball.getVelocity(), reflectionCoeff));
        }
        checkRep();
    }
    /**
     * Gets collision time for ball object to collide with wall
     * @param ball Ball object for which collision time will be calculated
     * @return collision time
     */
    @Override
    public double timeUntilCollision(Ball ball) {
        double minTime = Double.POSITIVE_INFINITY;
        for (LineSegment l: walls) {
            if (Geometry.timeUntilWallCollision(l, new Circle(ball.getPosition(), 0.25), ball.getVelocity())<minTime)
                minTime = Geometry.timeUntilWallCollision(l, new Circle(ball.getPosition(), 0.25), ball.getVelocity());
        }
        return minTime;
    }
    /**
     * Gets coordinates of top-left corner of OuterWall
     * @return a Vector representation of the coordinates of top-left corner of Wall
     */
    @Override
    public Vect getPosition() {
        return topLeftCorner.getCenter();
    }
    /**
     * Carries out action of Outer Wall
     */
    @Override
    public void doAction() {
        //Does nothing

    }
    /**
     * Carries out actions of all triggered Gadgets of OuterWall
     */
    @Override
    public void trigger() {
        //Outer walls can not trigger anything

    }
    /**
     * Gets ASCII Representation of OuterWall
     * @return ASCII Representation of OuterWall
     */
    @Override
    public String[][] toASCIIRep() {
        //Board takes care of outer wall representation, so this returns empty 2d array
        return new String[0][0];
    }
    /**
     * Updates position of OuterWall
     */
    @Override
    public void updatePosition(double time) {
        // Do nothing

    }
    @Override
    public List<Ball> getBallList() {
        return new ArrayList<Ball>();
    }
    @Override
    public void addToTriggeredList(Gadget gadget) {
        // Do nothing

    }

    @Override
    /**
     * @return false. Not a portal
     */
    public boolean isPortal() {
        return false;
    }


    private void checkRep() {
        List<Boolean> invisList = Arrays.asList(invisLeft, invisRight, invisTop, invisBottom);
        for (int i=0; i < 4; i++){
            if (!invisList.get(i)){
                assert(walls.contains(wallsList.get(i)));
            }
        }
        //Check for invisible corners
        if (!invisTop && !invisLeft)
            assert(corners.contains(topLeftCorner));
        if (!invisTop && !invisRight)
            assert(corners.contains(topRightCorner));
        if (!invisBottom && !invisLeft)
            assert(corners.contains(bottomLeftCorner));
        if (!invisBottom && !invisRight)
            assert(corners.contains(bottomRightCorner));
    }

	@Override
	public void drawShape(Graphics2D g) {
		return;
		
	}
}
