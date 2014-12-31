
package pingBall;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ClientGUI.ClientFrame;

// import javafx.scene.shape.Rectangle;
// import javafx.scene.shape.Shape;
import physics.*;
/**
 * Represents a mutable Absorber Gadget on pingball board.
 * Size and shape: A rectangle kL × mL where k and m are positive integers <= 20
 * Orientation: not applicable (only one orientation is allowed)
 * Coefficient of reflection: not applicable; the ball is captured
 * Trigger: generated whenever the ball hits it
 * Action: shoots out a stored ball at default 50L/sec
 * If it is already storing a ball when another ball collides with it, and it is not self-triggering, then the ball reflects
 * When ejecting a ball when it is doing its action, the ball does not collide with the inner sides of the absorber walls.
 *
 */
public class Absorber implements Gadget{
    //AF:
    //  Represents an absorber of specified size
    //RI:
    //  x >= 0, y>=0
    //  x+width<=20, y+height <=20
    
    private final double x;
    private final double y;
    private final int height;
    private final int width;
    private final List<Gadget> triggeredGadgets;
    private final double margin;
    private boolean contain;
    private List<Ball> store;
    private final List<LineSegment> lines;
    private final List<Circle> circles;
    private List<Circle> collideCircle;
    private List<LineSegment> collideLine;
    private static double ballrad;
    private static double speed;
    /**
     * Creates a new Absorber.
    * @param x x-coordinate of top left corner of absorber
    * @param y y-coordinate of top left corner of absorber
    * @param height - height of absorber (in L)
    * @param width - width of absorber (in L)
    * @param triggeredGadgets, a list of gadgets that it will trigger when its triggered default value is none
    */
    public Absorber(double x, double y, int width, int height, List<Gadget> triggeredGadgets){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        speed = -50.0;
        this.triggeredGadgets = new ArrayList<>();
        for (Gadget gadget : triggeredGadgets){
            this.triggeredGadgets.add(gadget);
        }
        ballrad = 0.25;
        this.contain = false; //true if Absorber is storing a ball
        this.store = new ArrayList<Ball>();
        margin = 0.25;
        this.collideCircle = new ArrayList<Circle>();
        this.collideLine = new ArrayList<LineSegment>();
        //list of line segments representing the 4 sides of absorber
        this.lines = Arrays.asList(new LineSegment(x, y, x+width, y),new LineSegment(x+width, y, x+width, y+height),
                new LineSegment(x+width, y+height, x, y+height), new LineSegment(x, y+height, x, y));
        //list of circles representing the corners of the absorber
        this.circles = Arrays.asList(new Circle(x,y,0.0), new Circle(x+width, y, 0.0), new Circle(x,y+height, 0.0), new Circle(x+width, y+height, 0.0));
        checkRep();

    }

    @Override
    public void collision(Ball ball) {
        //if the ball collides with the absorber, store it
        if (!(ball.getPosition().x() + ballrad > this.x && ball.getPosition().x() - ballrad < this.x + this.width && ball.getPosition().y() + ballrad > this.y && ball.getPosition().y() - ballrad < this.y + this.height)){
            ball.setVelocity(new Vect(0.0,0.0));
            this.contain = true;
            ball.setPosition(this.x + this.width - margin, this.y + this.height - margin);

            this.store.add(ball);
            ball.setInsideAbsorber(true);
        }
        this.trigger();
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        double minTime = Double.POSITIVE_INFINITY;
        Circle circle = new Circle(ball.getPosition().x(), ball.getPosition().y(),ballrad);
        //get minimum time until ball collides with each side of absorber
        for(LineSegment line: this.lines){
            double time = Geometry.timeUntilWallCollision(line, circle, ball.getVelocity());
            if(time < minTime){
                minTime = time;
                this.collideLine.clear();
                this.collideLine.add(line);
            }
        }
        //get minimum time until ball collides with each corner of absorber, resetting previous minimum time as necessary
        for(Circle circle1: this.circles){
            double time1 = Geometry.timeUntilCircleCollision(circle1, circle, ball.getVelocity());
            if(time1 < minTime){
                minTime = time1;
                this.collideLine.clear();
                this.collideCircle.clear();
                this.collideCircle.add(circle1);
            }
        }return minTime;
    }

    @Override
    public Vect getPosition() {
        return new Vect(this.x, this.y);
    }


    @Override
    public void doAction() {
        
        if(this.store.size() == 0)
        {
            this.contain = false;
        }
        //if absorber is storing something, shoot it out
        if (this.contain == true){
            this.store.get(0).setInsideAbsorber(false);
            this.store.get(0).setVelocity(new Vect(0.0, speed));
            this.store.remove(0);//remove first item
        }
    }

    @Override
    public void trigger() {
        for (Gadget gadget: this.triggeredGadgets){
            gadget.doAction();
        }
    }

    @Override
    public String[][] toASCIIRep() {
        String[][] ASCIIRep = new String[this.height][this.width];
        //first clear
        for (int i = 0; i < this.height; i++){
            for(int j = 0; j<this.width; j++){
                ASCIIRep[i][j] = "=";
            }
        }
        return ASCIIRep;
    }

    @Override
    public void updatePosition(double time) {
        //do nothing
    }

    @Override
    public List<Ball> getBallList() {
        return this.store;
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
    //  x >= 0, y>=0
    //  x+width<=20, y+height <=20
     * Checks whether or not RI is maintained
     */
    public void checkRep()
    {
        assert(x>= 0 && y >= 0 && x+width<=20 && y+height<=20);
    }
    
    public int getWidth(){
        return this.width;
    }
    
    public int getHeight(){
        return this.height;
    }

    public void drawShape(Graphics2D g){
        
        int c = ClientFrame.L; // multiplier for pixels

        int width = (int) (c*this.width);
        int height = (int) (c*this.height);
        int upperLeftCornerX = (int) (c*this.x);
        int upperLeftCornerY = (int) (c*this.y);
        
        g.draw(new Rectangle(upperLeftCornerX, upperLeftCornerY, width, height));
    }    
}
