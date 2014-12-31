package pingBall;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;

import ClientGUI.ClientFrame;

import physics.*;


/**
 * An immutable gadget that represents the circle bumper on playing area.
 * Centered at specified position.
 * The reflection coefficient of the circle bumper is 1.0
 * Trigger: generated whenever ball hits it
 * Action: None
 */

class CircleBumper implements Gadget{
  //AF:
    //  Represents the circle bumper gadget on the board
    //RI:
    //  Radius of circle bumper is 0.5L
    //  Reflection coefficient is 1.0
    private final Circle circle;
    private final double radius = 0.5;
    private final double reflectCoeff = 1;
    private final List<Gadget> triggeredGadgets = new ArrayList<>();


    /**
     * Constructs an circle bumper at specified location
     * @param x - x coordinate of circle bumper
     * @param y - y coordinate of circle bumper
     * @param triggeredGadgets - list of gadgets that should be triggered when circle bumper triggered by action such as ball collision
     *          (default value of list is none)
     */
    CircleBumper(double x, double y, List<Gadget> triggerGadgets){
        circle = new Circle(x+radius, y+radius, radius);
        for(Gadget gadget: triggerGadgets){
            triggeredGadgets.add(gadget);
        }
        checkRep();
    }
    /**
     * Constructs a circle bumper at specified location
     * @param x - x coordinate of circle bumper
     * @param y - y coordinate of circle bumper
     */
    public CircleBumper(double x, double y){
        circle = new Circle(x+radius, y+radius, radius);
        checkRep();
    }


    @Override
    public double timeUntilCollision(Ball ball) {
        return Geometry.timeUntilCircleCollision(this.circle, new Circle(ball.getPosition(), 0.25), ball.getVelocity());
    }
    @Override
    public Vect getPosition() {
        return this.circle.getCenter().minus(new Vect(radius, radius));
    }

    @Override
    public void collision(Ball ball) {
        this.trigger();
        Vect newVelocity = Geometry.reflectCircle(this.circle.getCenter(), ball.getPosition(), ball.getVelocity(), reflectCoeff);
        ball.setVelocity(newVelocity);
        checkRep();
    }
    @Override
    public void doAction() {
        // Circle bumper will do nothing
        }

    /*
     * @see phase1.Gadget#trigger()
     */
    @Override
    public void trigger() {
        for (Gadget gadget: this.triggeredGadgets){
            gadget.doAction();
        }

    }
    /*
     * @see phase1.Gadget#toASCIIRep()
     */
    @Override
    public String[][] toASCIIRep() {
        String [][] ASCIIrep = new String[][]{
                {"O"}
            };
            return ASCIIrep;
    }

    /*
     * @see phase1.Gadget#updatePosition(java.lang.double)
     */
    @Override
    public void updatePosition(double time) {
        // circle bumper is static gadget

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
    
    //RI:
    //  Radius of circle bumper is 0.5L
    //  Reflection coefficient is 1.0
    private void checkRep() {
        assert(circle.getRadius()==radius);
        // assert(reflectCoeff==1);
    }

    @Override
    public void drawShape(Graphics2D g) {
        
        int c = ClientFrame.L; // multiplier for pixels
        
        double xCoord = c*this.circle.getCenter().x();
        double yCoord = c*this.circle.getCenter().y();
        double radius = c*this.circle.getRadius();
        
        g.draw(new Ellipse2D.Double(xCoord-radius, yCoord-radius, 2*radius, 2*radius));
        
    }
}
