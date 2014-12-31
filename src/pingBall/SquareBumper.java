package pingBall;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ClientGUI.ClientFrame;

import physics.*;

/**
 *      Represents a 1L x 1L immutable square bumper on the pingball board.
 *      Size and shape: a square shape with edge length 1L
 *      Orientation: not applicable (symmetric to 90 degree rotations)
 *      Coefficient of reflection: 1.0
 *      Trigger: generated whenever the ball hits it
 *      Action: none
 *      Origin (x,y) must have 0 <= x <= 19, 0 <= y <= 19
 */
public class SquareBumper implements Gadget{
    private final Double x;
    private final Double y;
    private final LineSegment top;
    private final LineSegment right;
    private final LineSegment bottom;
    private final LineSegment left;
    private final List<LineSegment> lines;
    private List<LineSegment> collideLine;
    private final List<Gadget> triggeredGadgets;
    private final List<Circle> circles;
    private List<Circle> collideCirc;

    /**
     * Creates a SquareBumper with top-left corner at (x,y)
     * @param x x-coordinate of SquareBumper top left corner
     * @param y y-coordinate of SquareBumper top left corner
     * @param triggeredGadgets - list of gadgets that are triggered by SquareBumper
     */
    public SquareBumper(Double x, Double y, List<Gadget> triggeredGadgets){
        //AF:
        //  Represents a square triangular bumper with side length 1L
        //RI:
        //  x and y must be >=0 and <=19
        this.x = x;
        this.y = y;
        this.top = new LineSegment(x, y, x+1, y);
        this.right = new LineSegment(x+1, y, x+1, y+1);
        this.bottom = new LineSegment(x+1, y+1, x, y+1);
        this.left = new LineSegment(x, y+1, x, y);
        //line segment list representing each side of the square
        this.lines = Arrays.asList(this.top, this.right, this.bottom, this.left);
        //circles representing each corner
        this.circles = Arrays.asList(new Circle(x,y,0.0), new Circle(x+1, y, 0.0), new Circle(x, y+1, 0.0), new Circle(x+1, y+1, 0.0));
        this.collideLine = new ArrayList<LineSegment>();
        this.collideCirc = new ArrayList<Circle>();
        this.triggeredGadgets = new ArrayList<>();
        for (Gadget gadget : triggeredGadgets){
            this.triggeredGadgets.add(gadget);
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
        //no action
    }

    @Override
    public void collision(Ball ball) {
        //if ball collides with side, reflect with line
        if (!(this.collideLine.size() ==  0)){
            ball.setVelocity(Geometry.reflectWall(this.collideLine.get(0), ball.getVelocity()));
        //if ball collides with circle, reflect with circle
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
        String [][] ASCIIrep = new String[][]{
                {"#"}
            };
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
     *RI:
     *x and y must be >=0 and <=19
     *Checks whether or not RI is maintained
     */
    public void checkRep()
    {
        assert (x>= 0 && y>= 0 && x <=19 && y<=19);
    }

    @Override
    public void drawShape(Graphics2D g) {
        
        int c = ClientFrame.L; // multiplier for pixels
        
        int sideLength = (int) (c*this.top.length());
        int upperLeftCornerX = (int) (c*this.x);
        int upperLeftCornerY = (int) (c*this.y);
        
        g.draw(new Rectangle(upperLeftCornerX, upperLeftCornerY, sideLength, sideLength));
        
    }
}
