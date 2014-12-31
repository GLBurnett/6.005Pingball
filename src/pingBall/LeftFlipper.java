package pingBall;

import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import ClientGUI.ClientFrame;

import physics.*;
/**
 * An mutable gadget that represents the left flipper on playing area.
 * Pivot is centered at specified position (at northwest corner of 2L by 2L grid by default).
 * The reflection coefficient of the left flipper is 0.95
 * Trigger: generated whenever ball hits it
 * Orientation: default value is 0 (parallel to y axis)
 * Action: Rotates 90 degrees within the 2L by 2L bounding box
 */
public class LeftFlipper implements Gadget{
    private LineSegment lineSegment;
    private double angularVelocity = 0.0;
    private double orientation = 0.0;
    private final List<Gadget> triggeredGadgets = new ArrayList<>();
    private static double circleRad;
    private static double straightAngle;
    private static double refCoeff;
    private static double flipperLength;
    private static double ballRad;
    private static double rightAngle;
    private static double threeQuartAngle;
    private static double flipSpeed;

  //AF:
    //  Represents the left flipper gadget on the board
    //RI:
    //  length of flipper is 2L
    //  Reflection coefficient is .95


    /**
     * Constructs an left flipper in specified location
     * @param x - x coordinate of left flipper
     * @param y - y coordinate of left flipper
     */
    LeftFlipper(double x, double y){
        circleRad = 6.28;
        straightAngle = 180.0;
        refCoeff = 0.95;
        flipperLength = 2.0;
        ballRad = 0.25;
        rightAngle = 90.0;
        threeQuartAngle = 270.0;
        flipSpeed = 1080.0;
        lineSegment = new LineSegment(x, y, x, y+flipperLength);

    }
    /**
     * Constructs a left flipper with specified location and list of triggered gadgets.
     * @param x - x coordinate of left flipper
     * @param y - y coordinate of left flipper
     * @param triggerGadgets - list of gadgets triggered by left flipper
     */
    LeftFlipper(double x, double y, List<Gadget> triggerGadgets){
        circleRad = 6.28;
        straightAngle = 180.0;
        refCoeff = 0.95;
        flipperLength = 2.0;
        ballRad = 0.25;
        rightAngle = 90.0;
        threeQuartAngle = 270.0;
        flipSpeed = 1080.0;
        lineSegment = new LineSegment(x, y, x, y+flipperLength);

        for(Gadget gadget : triggerGadgets){
            triggeredGadgets.add(gadget);
        }
    }
    /**
     * Constructs a left flipper with specified location and orientation.
     * @param x - x coordinate of left flipper
     * @param y - y coordinate of left flipper
     * @param orientation - clockwise rotation of left flipper
     */
    LeftFlipper(double x, double y, double orientation){
        circleRad = 6.28;
        straightAngle = 180.0;
        refCoeff = 0.95;
        flipperLength = 2.0;
        ballRad = 0.25;
        rightAngle = 90.0;
        threeQuartAngle = 270.0;
        flipSpeed = 1080.0;
        if (orientation == 0.0){
            lineSegment = new LineSegment(x, y, x, y+flipperLength);
        }
        else if (orientation == rightAngle){
            this.orientation = orientation;
            this.lineSegment = new LineSegment(x+flipperLength, y, x, y); // changed pivot point p1() to be in northeast corner
        }
        else if (orientation == straightAngle){
            this.orientation = orientation;
            lineSegment = new LineSegment(x+flipperLength, y+flipperLength, x+flipperLength, y); // changed pivot point p1() to be in bottom right corner
        }
        else if (orientation == threeQuartAngle){
            this.orientation = orientation;
            lineSegment = new LineSegment(x, y+flipperLength, x+flipperLength, y+flipperLength); // changed pivot point p1() to be in northwest corner
        }
        else{
            throw new RuntimeException("Not possible orientation configuration");
        }

    }
    /**
     * Constructs a left flipper with specified location, orientation, and list of triggered gadgets.
     * @param x - x coordinate of left flipper
     * @param y - y coordinate of left flipper
     * @param orientation - clockwise rotation of left flipper
     * @param triggerGadgets - list of gadgets triggered by left flipper
     */
    LeftFlipper(double x, double y, double orientation, List<Gadget> triggerGadgets){
        circleRad = 6.28;
        straightAngle = 180.0;
        refCoeff = 0.95;
        flipperLength = 2.0;
        ballRad = 0.25;
        rightAngle = 90.0;
        threeQuartAngle = 270.0;
        flipSpeed = 1080.0;
        for(Gadget gadget : triggerGadgets){
            triggeredGadgets.add(gadget);
        }
        if (orientation == 0.0){
            lineSegment = new LineSegment(x, y, x, y+flipperLength);
        }
        else if (orientation == rightAngle){
            this.orientation = orientation;
            lineSegment = new LineSegment(x+flipperLength, y, x, y); // changed pivot point p1() to be in northeast corner
        }
        else if (orientation == straightAngle){
            this.orientation = orientation;
            lineSegment = new LineSegment(x+flipperLength, y+flipperLength, x+flipperLength, y); // changed pivot point p1() to be in bottom right corner
        }
        else if (orientation == threeQuartAngle){
            this.orientation = orientation;
            lineSegment = new LineSegment(x, y+flipperLength, x+flipperLength, y+flipperLength); // changed pivot point p1() to be in northwest corner
        }
        else{
            throw new RuntimeException("Not possible orientation configuration");
        }
    }

    @Override
    public void collision(Ball ball) {
        Vect newVel = ball.getVelocity();
        List<Double> collisionTimeOfComponents = new ArrayList<>(); // list of all time until collision with ball
        Circle startPoint = new Circle(lineSegment.p1(), 0.0);
        Circle endPoint = new Circle(lineSegment.p2(), 0.0);

     // time until collision on boundaries of line segment

        if (angularVelocity == 0.0){ // if flipper was not moving
            collisionTimeOfComponents.add(Geometry.timeUntilCircleCollision(startPoint, new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
            collisionTimeOfComponents.add(Geometry.timeUntilCircleCollision(endPoint, new Circle(ball.getPosition(), ballRad), ball.getVelocity()));

            for (int i =0; i<2; i++){
                // checking whether collision happened with end points
                if (!collisionTimeOfComponents.get(i).equals(Double.POSITIVE_INFINITY)){
                    if (i == 0){
                        newVel = Geometry.reflectCircle(startPoint.getCenter(), ball.getPosition(), ball.getVelocity(), refCoeff);
                    }
                    else{
                        newVel = Geometry.reflectCircle(endPoint.getCenter(), ball.getPosition(), ball.getVelocity(), refCoeff);
                    }
                }
            }

            collisionTimeOfComponents.add(Geometry.timeUntilWallCollision(lineSegment, new Circle(ball.getPosition(), ballRad),
                    ball.getVelocity()));
         // checking whether collision happened with line segment
            if (!collisionTimeOfComponents.get(2).equals(Double.POSITIVE_INFINITY)){
                // if so, set velocity
                newVel = Geometry.reflectWall(lineSegment, ball.getVelocity(), refCoeff);
            }

        }
        else{   // if flipper was moving
            collisionTimeOfComponents.add(Geometry.timeUntilRotatingCircleCollision(startPoint, lineSegment.p1(), angularVelocity/(circleRad), new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
            collisionTimeOfComponents.add(Geometry.timeUntilRotatingCircleCollision(endPoint, lineSegment.p1(), angularVelocity/(circleRad), new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
            for (int i =0; i<2; i++){
                // checking whether collision happened with end points
                if (!collisionTimeOfComponents.get(i).equals(Double.POSITIVE_INFINITY)){
                    if (i == 0){
                        newVel = Geometry.reflectRotatingCircle(startPoint, lineSegment.p1(), angularVelocity/(circleRad), new Circle(ball.getPosition(), ballRad), ball.getVelocity(), refCoeff);
                    }
                    else{
                        newVel = Geometry.reflectRotatingCircle(endPoint, lineSegment.p1(), angularVelocity/(circleRad), new Circle(ball.getPosition(), ballRad), ball.getVelocity(), refCoeff);
                    }
                }
            }
            collisionTimeOfComponents.add(Geometry.timeUntilRotatingWallCollision(lineSegment, startPoint.getCenter(),
                    angularVelocity/(circleRad), new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
         // checking whether collision happened with line segment
            if (!collisionTimeOfComponents.get(2).equals(Double.POSITIVE_INFINITY)){
                // if so, set velocity
                newVel = Geometry.reflectRotatingWall(lineSegment, lineSegment.p1(), -angularVelocity/(circleRad),
                        new Circle(ball.getPosition(), ballRad), ball.getVelocity(), refCoeff); // dividing by 2Pi to make it radians
            }

       }
       ball.setVelocity(newVel);
       this.trigger();     // start triggering event

    }
    /*
     * @see phase1.Gadget#timeUntilCollision(phase1.Ball)
     */
    @Override
    public double timeUntilCollision(Ball ball) {
        List<Double> collisionTimeOfComponents = new ArrayList<>(); // list of all time until collision with ball
        Circle startPoint = new Circle(lineSegment.p1(), 0.0);
        Circle endPoint = new Circle(lineSegment.p2(), 0.0);
        if(angularVelocity == 0.0){
         // time until collision on boundaries of line segment
            collisionTimeOfComponents.add(Geometry.timeUntilCircleCollision(startPoint, new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
            collisionTimeOfComponents.add(Geometry.timeUntilCircleCollision(endPoint, new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
            // time until collision in linesegment's main body
            collisionTimeOfComponents.add(Geometry.timeUntilWallCollision(lineSegment, new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
        }
        else{
            collisionTimeOfComponents.add(Geometry.timeUntilRotatingCircleCollision(startPoint, lineSegment.p1(), angularVelocity/(circleRad), new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
            collisionTimeOfComponents.add(Geometry.timeUntilRotatingCircleCollision(endPoint, lineSegment.p1(), angularVelocity/(circleRad), new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
         // time until collision in linesegment's main body
            collisionTimeOfComponents.add(Geometry.timeUntilRotatingWallCollision(lineSegment, startPoint.getCenter(),
                    angularVelocity/(circleRad), new Circle(ball.getPosition(), ballRad), ball.getVelocity()));
        }
        double minTime = Double.POSITIVE_INFINITY;
        for (double time : collisionTimeOfComponents){  // finding min time of collision
            if (minTime > time){
                minTime = time;
            }
        }
        return minTime;

    }

    @Override
    public Vect getPosition() {
        Vect position = this.lineSegment.p1(); // pivot position
        if (this.orientation == rightAngle){
            position = new Vect(position.x()-flipperLength, position.y());
        }
        else if(this.orientation == straightAngle){
            position = new Vect(position.x()-flipperLength, position.y()-flipperLength);
        }
        else if(this.orientation == threeQuartAngle){
            position = new Vect(position.x(), position.y()-flipperLength);
        }
        return position;
    }

    /**
     * Sets angular velocity of flipper, to - 1080 degrees per second (from positive y axis to positive x axis) if it was vertical.
     * 1080 degrees per second if it was horizontal (positive x axis to positive y axis)
     *
     */
    @Override
    public void doAction() {
        if((lineSegment.angle().equals(new Angle(0.0)) || lineSegment.angle().equals(Angle.RAD_PI)) && angularVelocity == 0.0){ // if flipper was horizontal
            if(this.orientation == 0.0){
                angularVelocity = flipSpeed;
            }
            else if(this.orientation == rightAngle){
                angularVelocity = -flipSpeed;
            }
            else if(this.orientation == straightAngle){
                angularVelocity = flipSpeed;
            }
            else if(this.orientation == threeQuartAngle){
                angularVelocity = -flipSpeed;
            }

        }
        else if (angularVelocity == 0.0){  // if it was vertical
            if(this.orientation == 0.0){
                angularVelocity = -flipSpeed;
            }
            else if(this.orientation == rightAngle){
                angularVelocity = flipSpeed;
            }
            else if(this.orientation == straightAngle){
                angularVelocity = -flipSpeed;
            }
            else if(this.orientation == threeQuartAngle){
                angularVelocity = flipSpeed;
            }
        }
        else{ // if angular velocity was not 0 when trigger happened. Then, reverse angular velocity.
            angularVelocity = -angularVelocity;
        }
    }
    /*
     *
     * @see phase1.Gadget#trigger()
     */
    @Override
    public void trigger() {
        for (Gadget gadget: this.triggeredGadgets){
            gadget.doAction();
        }
    }

    /**
     * @return 2 by 2 ASCII representation array of flipper.
     */

    @Override
    public String[][] toASCIIRep() {
        String[][] ASCIIRep = new String[][] {
            {" ", " "},
            {" ", " "}
        };
        double angle = lineSegment.angle().cos();
        double thresholdAngle = Math.sqrt(2)/2.0;
        if (Math.abs(angle)>thresholdAngle) {
            if(this.orientation == 0.0){
                ASCIIRep = new String[][]{
                        {"-", "-"},
                        {" ", " "}
                };
            }
            else if(this.orientation == rightAngle){
                ASCIIRep = new String[][]{
                        {"-", "-"},
                        {" ", " "}
                };
            }
            else if(this.orientation == straightAngle){
                ASCIIRep = new String[][]{
                        {" ", " "},
                        {"-", "-"}
                };
            }
            else if(this.orientation == threeQuartAngle){
                ASCIIRep = new String[][]{
                        {" ", " "},
                        {"-", "-"}
                };
            }

        }
        else {
            if(this.orientation == 0.0){
                ASCIIRep = new String[][]{
                        {"|", " "},
                        {"|", " "}
                };
            }
            else if(this.orientation == rightAngle){
                ASCIIRep = new String[][]{
                        {" ", "|"},
                        {" ", "|"}
                };
            }
            else if(this.orientation == straightAngle){
                ASCIIRep = new String[][]{
                        {" ", "|"},
                        {" ", "|"}
                };
            }
            else if(this.orientation == threeQuartAngle){
                ASCIIRep = new String[][]{
                        {"|", " "},
                        {"|", " "}
                };
            }
        }
        return ASCIIRep;
    }
    /**
     * rotates the left flipper in specified "time" interval
     * restricts left flipper movement in 2L by 2L rectangle
     */
    @Override
    public void updatePosition(double time) {
        // updating position of line segment
        lineSegment = Geometry.rotateAround(lineSegment, lineSegment.p1(), new Angle(angularVelocity*time/circleRad));
        Vect topLeftCorner = this.getPosition();
        boolean isXcoorInBox = (topLeftCorner.x() <= lineSegment.p2().x() && topLeftCorner.x()+flipperLength >= lineSegment.p2().x());
        boolean isYcoorInBox = (topLeftCorner.y() <= lineSegment.p2().y() && topLeftCorner.y()+flipperLength >= lineSegment.p2().y());
        if (!(isXcoorInBox && isYcoorInBox)){ // if it was not on bounding box
            if(this.orientation == 0.0 || this.orientation == straightAngle){
                if (angularVelocity < 0){ // if it was going from positive y axis to positive x axis, we will make it horizontal line
                    lineSegment = Geometry.rotateAround(lineSegment, lineSegment.p1(),
                            Angle.RAD_PI.plus(Angle.RAD_PI).minus(lineSegment.angle()));
                }
                else{  // if it was going from positive x axis to positive y axis, we will make it vertical line
                    lineSegment = Geometry.rotateAround(lineSegment, lineSegment.p1(),
                            Angle.RAD_PI.plus(Angle.RAD_PI).minus(lineSegment.angle()).plus(Angle.RAD_PI_OVER_TWO));
                }
                angularVelocity = 0.0; // stop motion and set line segment to horizontal line
            }
            else{
                if (angularVelocity > 0){ // if it was going from positive y axis to positive x axis, we will make it horizontal line
                    lineSegment = Geometry.rotateAround(lineSegment, lineSegment.p1(),
                            Angle.RAD_PI.plus(Angle.RAD_PI).plus(Angle.RAD_PI).minus(lineSegment.angle()));
                }
                else{  // if it was going from positive x axis to positive y axis, we will make it vertical line
                    lineSegment = Geometry.rotateAround(lineSegment, lineSegment.p1(),
                            Angle.RAD_PI.plus(Angle.RAD_PI).plus(Angle.RAD_PI).minus(lineSegment.angle()).plus(Angle.RAD_PI_OVER_TWO));
                }
                angularVelocity = 0.0; // stop motion and set line segment to horizontal line
            }
        }
        checkRep();

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
     * Checks to make sure rep invariant holds true
     */
    private void checkRep(){
        assert(flipperLength == 2.0);
        assert(refCoeff == 0.95);
    }
    
    @Override
    public void drawShape(Graphics2D g) {
        
        int c = ClientFrame.L; // multiplier for pixels
        
        g.draw(new Line2D.Double(c*this.lineSegment.p1().x(), c*this.lineSegment.p1().y(), c*this.lineSegment.p2().x(), c*this.lineSegment.p2().y()));
        
    }

}
