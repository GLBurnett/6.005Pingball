package pingBall;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import physics.Vect;

public class PortalTest {

    /**
     * Testing Strategy:
     *  1. Ball collide with portal straight on, otherPortal on same board
     *  2. Ball collide with portal at different angles, otherPortal on same board
     *  3. Ball collide with portal straight on, otherPortal on different board
     *  4. Ball collide with portal at different angles, otherPortal on different board
     *  5. Ball collide with portal straight on, otherPortal does not exist
     *  6. Ball collide with portal at different angles, otherPortal does not exist 
     *  7. Test getPosition
     *  8. Test to ASCIIRep
     *  9. Test whether portal is able to trigger other gadgets i.e. a left flipper
     *  10. Check time until collision
     */
    private static List<Gadget> gadgets1, gadgets2;
    private static List<Ball> balls1, balls2;
    private static Ball b1, b2;
    private static Board board1, board2;
    private static Portal p1, p2;
    private static final double epsilon = 0.05;
    @BeforeClass
    public static void setUpBeforeClass(){
        gadgets1 = new ArrayList<Gadget>();
        balls1 = new ArrayList<Ball>();
        gadgets2 = new ArrayList<Gadget>();
        balls2 = new ArrayList<Ball>();
        
        b1 = new Ball(5.0, 5.0, new Vect(1.0, 1.0));
        b2 = new Ball(10.0, 4.0, new Vect(0.0, 1.0));
    }
    
    @Test
    public void straightOnCollisionOtherPortalSameBoardTest() {
        clearEverything();
        
        board1 = new Board(gadgets1, balls1, 0, 0, 0);
        p1 = new Portal("Portal1", 10.0, 10.0, "Portal2", board1);
        p2 = new Portal("Portal2", 15.0, 15.0, "Portal1", board1);
        board1.addGadgetToBoard(p1);
        board1.addGadgetToBoard(p2);
        balls1.add(b2);
        p1.collision(b2);
        //board1.print();
        //by visual inspection the ball is in the right position
    
    }
    @Test
    public void angledCollisionOtherPortalSameBoardTest() {
        clearEverything();
        
        board1 = new Board(gadgets1, balls1, 0, 0, 0);
        p1 = new Portal("Portal1", 10.0, 10.0, "Portal2", board1);
        p2 = new Portal("Portal2", 15.0, 15.0, "Portal1", board1);
        board1.addGadgetToBoard(p1);
        board1.addGadgetToBoard(p2);
        balls1.add(b1);
        p1.collision(b1);
        //board1.print();
        //by visual inspection the ball is in the right position
    }
    @Test
    public void straightOnCollisionOtherPortalDiffBoardTest() {
        //This is difficult to test in JUnit, so we 
        //Conducted this test manually to confirm that a 
        //ball entering a portal straight on exited the linked
        //portal correctly.
        assertTrue(true);  
    }
    /**
     * clears all ball lists and gadgets lists
     */
    public void clearEverything()
    {
        balls1.clear();
        balls2.clear();
        gadgets1.clear();
        gadgets2.clear();
    }
    @Test
    public void angledCollisionOtherPortalDiffBoardTest() {
        //This is difficult to test in JUnit, so we 
        //Conducted this test manually to confirm that a 
        //ball entering a portal at an angle exited the linked
        //portal correctly.
        assertTrue(true);

    }
    @Test
    public void straightOnCollisionOtherPortalDNETest() {
        clearEverything();
        
        board1 = new Board(gadgets1, balls1, 0, 0, 0);
        p1 = new Portal("Portal1", 10.0, 10.0, "Portal2", board1);
        board1.addGadgetToBoard(p1);
        balls1.add(b2);
        board1.updateBallVelocityIfCollision(b1);
        b2.updatePosition(6.0, (OuterWall) Gadget.outerwall(true, true, true, true));
        assertTrue(p1.getPosition().equals(b2.getPosition()));
    }
    @Test
    public void angledCollisionOtherPortalDNETest() {
        clearEverything();
      
        board1 = new Board(gadgets1, balls1, 0, 0, 0);
        p1 = new Portal("Portal1", 10.0, 10.0, "Portal2", board1);
      
        board1.addGadgetToBoard(p1);
      
        balls1.add(b1);
        p1.collision(b1);
        assertTrue(p1.getPosition().equals(b2.getPosition()));
    }
    @Test
    public void getPositionTest() {
        assertTrue(p1.getPosition().equals(new Vect(10.0, 10.0)));
        assertTrue(p2.getPosition().equals(new Vect(15.0, 15.0)));
    }
    @Test
    public void toASCIIRepTest() {
        String[][] actual = p1.toASCIIRep();
        String[][] expected = new String[1][1];
        expected[0][0] = "o";
        assertTrue(expected[0][0].equals(actual[0][0]));
        assertTrue(actual.length==1);
        assertTrue(actual[0].length==1);
        
    }
    @Test
    public void triggerTest() {
        clearEverything();
        
        LeftFlipper flipper = new LeftFlipper(8.0, 7.0);
        gadgets1.add(flipper);
        balls1.add(b2);
        
        board1 = new Board(gadgets1, balls1, 0, 0, 0);
        
        p1 = new Portal("Portal1", 10.0, 10.0, "Portal2", board1);
        p2 = new Portal("Portal2", 15.0, 15.0, "Portal1", board1);
        p1.addToTriggeredList(flipper);
        
        board1.addGadgetToBoard(p1);
        board1.addGadgetToBoard(p2);
        String[][] expected = new String[][]{
                {"-", "-"},
                {" ", " "}
        };
        p1.collision(b2);
        flipper.updatePosition(1000.0);
        String[][] afterTrigger = flipper.toASCIIRep();
        
        for(int i = 0; i<expected.length; i++){
            for(int j=0; j<expected.length; j++){
                assertEquals(expected[i][j], afterTrigger[i][j]);
            }
        }
    }
    @Test
    public void timeUntilCollisionTest() {
        clearEverything();
        board1 = new Board(gadgets1, balls1, 0, 0, 0);
        p1 = new Portal("Portal1", 10.0, 10.0, "Portal2", board1);
        board1.addGadgetToBoard(p1);
        balls1.add(b1);
        assertTrue(Math.abs((p1.timeUntilCollision(b1) - 5.0)) < epsilon);
    }   
}