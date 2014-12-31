package pingBall;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import ClientGUI.ClientFrame;

import physics.Circle;
import physics.Geometry;
import physics.Vect;

public class Portal implements Gadget{

    private final String name;
    private final double x, y;
    private final String otherPortalName;
    private final String otherBoardName;
    private final List<Gadget> triggeredGadgets = new ArrayList<>();
    private static final double ballRad = 0.25;
    private static final double portalRad = 0.5;
    private final Board board;
    private final boolean isLinkedToAnotherBoard;
    /*
     * AF: Represents a portal with diameter 1L
     * RI: 0<= x <=19 and 0<= y <= 19. otherPortalName, name should not be empty string.
     */
    public Portal(String name, double x, double y, String otherPortalName, Board board)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.otherPortalName = otherPortalName; 
        isLinkedToAnotherBoard = false;
        //if otherBoard does not exist, then it is present on the same board as source portal
        this.board = board;
        this.otherBoardName = board.getBoardName();

        checkRep();
    }

    public Portal(String name, double x, double y, String otherPortalName, String otherBoardName, Board board)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        isLinkedToAnotherBoard = true;
        this.otherPortalName = otherPortalName;
        this.otherBoardName = otherBoardName;
        this.board = board;
        checkRep();
    }

    /**
     * 
     * @return name of the portal
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * 
     * @return true if this portal is linked to a portal on 
     * another board, false if it's not.
     */
    public boolean isLinkedToAnotherBoard() {
        return isLinkedToAnotherBoard;
    }
    /**
     * 
     * @return otherPortal
     */
    public String getOtherPortalName()
    {
        return this.otherPortalName;
    }
    /**
     * @return the name of the other board that this portal links too
     */
    public String getOtherBoardName()
    {
        return this.otherBoardName;
    }

    @Override
    /**
     * This method is only called by Board if this portal is not linked to another board
     */
    public void collision(Ball ball) {
        this.trigger();
        boolean success = board.ballExitingPortal(otherPortalName, ball.getVelocity());
        if (success)
            board.removeBall(ball);

    }

    /**
     * returns the time until the ball collides with the portal
     */
    @Override
    public double timeUntilCollision(Ball ball) {
        return Geometry.timeUntilCircleCollision(new Circle(this.x + portalRad, this.y + portalRad, portalRad),
                new Circle(ball.getPosition().x(), ball.getPosition().y(), ballRad),
                ball.getVelocity());
    }

    /**
     * @return position vector of the portal
     */
    @Override
    public Vect getPosition() {
        return new Vect(this.x, this.y);
    }

    /**
     * does nothing
     */
    @Override
    public void doAction() {
        //Does nothing, portal has no action
    }

    /**
     * triggers additional gadgets
     */
    @Override
    public void trigger() {
        for (Gadget gadget: triggeredGadgets){
            gadget.doAction();
        }
    }

    /**
     * add triggered gadgets to the triggered list
     */
    @Override
    public void addToTriggeredList(Gadget gadget) {
        triggeredGadgets.add(gadget);

    }

    /**
     * @return String representation of a portal
     */
    @Override
    public String[][] toASCIIRep() {
        String [][] ASCIIrep = new String[][]{
                {"o"}
        };
        return ASCIIrep;
    }

    /**
     * portal is a static gadget so do nothing
     */
    @Override
    public void updatePosition(double time) {

    }

    /**
     * @return empty arraylist because it stores no balls
     */
    @Override
    public List<Ball> getBallList() {
        return new ArrayList<Ball>();
    }

    @Override
    public boolean isPortal() {
        return true;
    }
    /**
     * RI: 0<= x <=19 and 0<= y <= 19. otherPortalName and name should not be empty string.
     * Checks whether or not rep invariant is maintained
     */
    public void checkRep()
    {
        assert (x >=0 && x <= 19 && y >= 0 && y <=19 && !otherPortalName.equals("") && !name.equals(""));
    }

    @Override
    public void drawShape(Graphics2D g) {
        
        int c = ClientFrame.L; // multiplier for pixels
        
        double xCoord = c*this.x;
        double yCoord = c*this.y;
        double radius = 5;
        
        g.setColor(Color.ORANGE);
        g.draw(new Ellipse2D.Double(xCoord, yCoord, radius, radius));
    }
}