
package pingBall;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;

import ClientGUI.ClientFrame;

import physics.Circle;
import physics.Geometry;
import physics.Geometry.VectPair;
import physics.Vect;
/**
 * An mutable class that represents the ball on playing area.
 * Centered at specified location which is center of ball
 */
public class Ball {

    //AF:
    //  Represents the ball on the board
    //RI:
    //  radius of ball is 0.25
    //  Velocity of ball can be 0, >.01, and <200 L/sec
    //  Position of ball must be between (0.25,0.25) and (19.75,19.75)

    private Circle circle;
    private Vect velocity;
    private static double radius = 0.25;
    private boolean insideAbsorber = false;

    /**
     * Constructs an ball, with specified location and velocity
     * If velocity is too fast or slow, it will be rounded to closest valid velocity
     * @param x - x coordinate of ball's center
     * @param y - ycoordinate of ball's center
     * @param velocity - velocity of ball in L/sec
     */
    public Ball(double cx, double cy, Vect velocity) {
        if (velocity.length()>200)
            this.velocity = velocity.times(200/velocity.length());
        else if (velocity.length()<.01)
            this.velocity = velocity.times(0);
        else
            this.velocity = velocity;
        this.circle = new Circle(cx, cy, radius);
        checkRep();
    }

    public void updatePosition(double time, OuterWall outerWall){
        if (!this.insideAbsorber) {
            Vect updatedPosition = this.getPosition().plus(this.velocity.times(time));
            //Gadget outerWall = Gadget.outerwall(false, false, false, false);
            if(updatedPosition.y()>19.75|| updatedPosition.y()<0.25||updatedPosition.x()>19.75||updatedPosition.x()<0.25){
                outerWall.collision(this); 
            }
            if (updatedPosition.y()>19.75 && !outerWall.isInvisBottom()){

                updatedPosition =  new Vect(updatedPosition.x(), 19.75);
            }
            if(updatedPosition.y()<0.25 && !outerWall.isInvisTop()){

                updatedPosition =  new Vect(updatedPosition.x(), 0.25);
            }
            if (updatedPosition.x()>19.75 && !outerWall.isInvisRight()){

                updatedPosition =  new Vect(19.75, updatedPosition.y());
            }
            if(updatedPosition.x()<0.25 && !outerWall.isInvisLeft()){

                updatedPosition =  new Vect(0.25, updatedPosition.y());
            }


            this.circle = new Circle(updatedPosition, radius);
            if(updatedPosition.y()<.25){
            }
        } else {
            //do nothing since ball is stored inside absorber and should not move
        }
        checkRep();
    }
    
    /**
     * Sets position of the ball to the specified location
     * @param x the new x location of the center of the ball
     * @param y the new y location of the center of the ball
     */
    public void setPosition(double x,double y){
        this.circle = new Circle(x,y,radius);
        checkRep();
    }

    /**
     *
     * @param velocity
     * modifies ball's velocity to have new velocity
     * Maximum magnitude of ball's velocity is 200L per sec
     */
    public void setVelocity(Vect velocity){
        if (!this.insideAbsorber) {
            if (velocity.length()>200)
                this.velocity = velocity.times(200/velocity.length());
            else if (velocity.length()<.01)
                this.velocity = velocity.times(0);
            else
                this.velocity = velocity;
        } else {
            //do nothing since ball is inside an absorber
        }
        checkRep();
    }
    /**
     *
     * @return velocity of current ball
     */
    public Vect getVelocity(){
        return this.velocity;
    }

    /**
     * @return time until collision happens between two balls in seconds
     */
    public double timeUntilCollision(Ball ball) {
        return Geometry.timeUntilBallBallCollision(this.circle, this.getVelocity(), new Circle(ball.getPosition(), radius), ball.getVelocity());
    }

    /**
     * @return current position of center of Ball
     */
    public Vect getPosition() {
        return this.circle.getCenter();
    }
    /**
     * changes velocity of that ball and this ball when collision happens between this ball and that ball
     *
     */
    public void collision(Ball ball) {
        VectPair newVel = Geometry.reflectBalls(this.getPosition(), 1.0, this.velocity, ball.getPosition(), 1.0, ball.getVelocity());
        ball.setVelocity(newVel.v2);
        this.setVelocity(newVel.v1);
        checkRep();
    }
    /*
     * @see phase1.Gadget#doAction()
     */
    public void doAction() {
        // ball will do nothing when it gets triggered
    }
    public void trigger() {
     // ball will not do anything when it triggered
    }
    /**
     * @return ASCIII representation of Ball as a 2d array
     */
    public String[][] toASCIIRep() {
        String [][] ASCIIrep = new String[][]{
            {"*"}
        };
        return ASCIIrep;
    }


    public List<Ball> getBallList() {
        return new ArrayList<Ball>();
    }

    /**
     * @return true if ball is inside Absorber, false otherwise
     */
    public boolean isInsideAbsorber() {
        return insideAbsorber;
    }


    /**
     * @param insideAbsorber set this.insideAbsorber to insideAbsorber
     */
    public void setInsideAbsorber(boolean insideAbsorber) {
        this.insideAbsorber = insideAbsorber;
        checkRep();
    }
    
    /**
     * checks to see if rep invariants hold true
     */
    private void checkRep(){
        assert(((Double)radius).equals(0.25));
        assert((velocity.x()>=-200.0)&&(velocity.x()<=200.0));
        assert((velocity.y()>=-200.0)&&(velocity.y()<=200.0));
        assert((circle.getCenter().x()>=-0.75)&&(circle.getCenter().x()<=20.75));
        assert((circle.getCenter().y()>=-0.75)&&(circle.getCenter().y()<=20.75));
        
    }
    
    public void drawShape(Graphics2D g){
        
        int c = ClientFrame.L; // multiplier for pixels
        
        double xCoord = c*this.circle.getCenter().x();
        double yCoord = c*this.circle.getCenter().y();
        double radius = c*this.circle.getRadius();
        
        g.draw(new Ellipse2D.Double(xCoord, yCoord, radius, radius));
    }
}
