package pingBall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.Polygon;

import ClientGUI.ClientFrame;

import physics.*;
/**
 * Size and shape: an immutable right-triangular bumper with sides of length 1L and hypotenuse of length Sqrt(2)L
 * Orientation: the default orientation (0 degrees) places one corner in the northeast, one corner in the northwest, and the last corner in the southwest.
 * The diagonal goes from the southwest corner to the northeast corner.
 * Coefficient of reflection: 1.0
 * Trigger: generated whenever the ball hits it
 * Action: none
 */
public class TriangleBumper implements Gadget{
    //AF:
    //  Represents a right triangular bumper where smallest sides are 1 and long side is sqrt(2)
    //RI:
    //  x and y must be >=0 and <=19
    //  orientation can be 0, 90, 180 or 270
    private final Double x;
    private final Double y;
    private final Double orientation;
    private final List<Gadget> triggeredGadgets;
    private final List<LineSegment> lines;
    private final List<Circle> circles;
    private List<Circle> collideCirc;
    private List<LineSegment> collideLine;
    /**
     * Creates an immutable Triangle Bumper on the pingball board in a 1Lx1L bounding box.
     * @param x - x coordinate of top left corner of triangle bumper's bounding box.
     * @param y - y coordinate of top left corner of triangle bumper's bounding box.
     * @param orientation - orientation of triangle bumper within its bounding box
     * @param triggeredGadgets - list of gadgets triggered by triangle bumper
     */
    public TriangleBumper(Double x,Double y,Double orientation,List<Gadget>triggeredGadgets){
        this.x = x;
        this.y = y;
        this.collideCirc = new ArrayList<Circle>(); //corner that ball will collide with
        this.collideLine = new ArrayList<LineSegment>(); //side that ball will collide with
        this.orientation = orientation;
        this.triggeredGadgets = new ArrayList<>();
        for (Gadget gadget : triggeredGadgets){
            this.triggeredGadgets.add(gadget);
        }
        //circle at each corner
        //line segment at each side
        if(this.orientation == 0.0){
            this.circles = Arrays.asList(new Circle(x,y,0.0), new Circle(x, y+1, 0.0), new Circle(x+1, y, 0.0));
            this.lines = Arrays.asList(new LineSegment(x+1, y, x, y+1), new LineSegment(x, y, x, y+1), new LineSegment(x,y, x+1, y));
        }else if (this.orientation == 90.0){
            this.circles = Arrays.asList(new Circle(x,y,0.0), new Circle(x+1, y, 0.0), new Circle(x+1, y+1,0.0));
            this.lines = Arrays.asList(new LineSegment(x, y, x+1, y), new LineSegment(x+1, y, x+1, y+1), new LineSegment(x,y, x+1, y+1));
        }else if (this.orientation == 180.0){
            this.circles = Arrays.asList(new Circle(x,y+1, 0.0), new Circle(x+1, y, 0.0), new Circle(x+1, y+1, 0.0));
            this.lines = Arrays.asList(new LineSegment(x, y+1, x+1, y), new LineSegment(x+1, y, x+1, y+1), new LineSegment(x+1, y+1, x, y+1));
        }else{
            this.circles = Arrays.asList(new Circle(x,y,0.0), new Circle(x, y+1, 0.0), new Circle(x+1, y+1, 0.0));
            this.lines = Arrays.asList(new LineSegment(x,y,x, y+1), new LineSegment(x,y+1, x+1, y+1), new LineSegment(x+1, y+1, x, y));
        }
        checkRep();

    }
    @Override
    public double timeUntilCollision(Ball ball) {
        Double minTime = Double.POSITIVE_INFINITY;
        Circle circle = new Circle(ball.getPosition().x(), ball.getPosition().y(),0.25);

        for(LineSegment line: this.lines){
            Double time = Geometry.timeUntilWallCollision(line, circle, ball.getVelocity());
            if(time < minTime){
                minTime = time;
                this.collideLine.clear();
                this.collideLine.add(line); //reset line to be collided with
            }

        }
        for(Circle circle1: this.circles){
            Double time1 = Geometry.timeUntilCircleCollision(circle1, circle, ball.getVelocity());
            if(time1 < minTime){
                minTime = time1;
                this.collideLine.clear();
                this.collideCirc.clear();
                this.collideCirc.add(circle1); //reset line to be collided with
            }

        }
        return minTime;
    }

    @Override
    public Vect getPosition() {
        return new Vect(this.x, this.y);
    }

    @Override
    public void doAction() {
        // do nothing

    }

    @Override
    public void collision(Ball ball) {
        //if ball collides with side, reflect off line
        if (!(this.collideLine.size() == 0)){
            ball.setVelocity(Geometry.reflectWall(this.collideLine.get(0), ball.getVelocity()));
        //if ball collides with corner, reflect off circle
        }else if (!(this.collideCirc.size() == 0)){
            ball.setVelocity(Geometry.reflectCircle(this.collideCirc.get(0).getCenter(), ball.getPosition(), ball.getVelocity()));
        }

        this.trigger();

    }

    @Override
    public void trigger() {
        for (Gadget gadget: this.triggeredGadgets){
            gadget.doAction();
        }
    }

    @Override
    public String[][] toASCIIRep() {
        String[][] ASCIIrep;
        if(this.orientation == 0.0 || this.orientation == 180.0){
            ASCIIrep = new String[][]{
                    {"/"}
                };
        }else{
            ASCIIrep = new String[][]{
                    {"\\"}
                };
        }
        return ASCIIrep;
    }

    @Override
    public void updatePosition(double time) {
        // do nothing

    }
    @Override
    public List<Ball> getBallList() {
        return new ArrayList<Ball>();
    }
    @Override
    public void addToTriggeredList(Gadget gadget) {
        triggeredGadgets.add(gadget);

    }
    @Override
    /**
     * @return false. Not a portal
     */
    public boolean isPortal() {
        return false;
    }

    /**
     * //RI:
    //  x and y must be >=0 and <=19
    //  orientation can be 0, 90, 180 or 270
     * Checks whether or not checkRep is maintained
     */
    public void checkRep()
    {
        assert (x>=0 && x<=19 && y >=0 && y<=19 && (orientation == 0 || orientation == 90 || orientation == 180 || orientation == 270));
    }
    
    public void drawShape(Graphics2D g){
        
        int c = ClientFrame.L; // multiplier for pixels
        
        int[] xCoords = new int[]{(int) (c*this.lines.get(0).p1().x()), (int) (c*this.lines.get(0).p2().x()), (int) (c*this.lines.get(1).p2().x())};
        int[] yCoords = new int[]{(int) (c*this.lines.get(0).p1().y()), (int) (c*this.lines.get(0).p2().y()), (int) (c*this.lines.get(1).p2().y())};
        int numPoints = 3;
        
        g.draw(new Polygon(xCoords, yCoords, numPoints));
    }
}