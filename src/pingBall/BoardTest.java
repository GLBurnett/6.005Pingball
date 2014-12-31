package pingBall;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import physics.Vect;

public class BoardTest {
    // Testing strategy
    //  There are did it runnable test cases for each method (except for void methods) of Board class.
    //  Testing strategy for each method is written on top of each test cases
    //  Test File Constructor for valid and invalid files
    // For test cases that is impossible to run with Didit. We performed visual test 


    //Test cases for minTimeUntilCollision method
    /**
     * Partitioning test cases into following subspaces:
     * 1. When ball collides with nothing (implying there was not anything except only one ball)
     * 2. Ball can possibly collide with only one non-ball gadget on board
     * 3. Ball can possibly collide with only one Ball gadget on board
     * 4. Ball can possibly collide with more than one mix of non-ball and ball gadgets on board 
     *       (but it should return min time)
     */
    @Test // #1
    public void testminTimeUntilCollisionNothing() {
        List<Gadget> gadgets = new ArrayList<Gadget>();
        gadgets.add(Gadget.outerwall(false, false, false, false));
        Ball ball = new Ball(2.0, 2.0, new Vect(0.0, 0.0));
        Board testBoard = new Board(Arrays.asList(), Arrays.asList());

        assertTrue(Double.MAX_VALUE == testBoard.minTimeUntilCollision(ball));
    }

    @Test // #2
    public void testminTimeUntilCollisionStandardGadget() {
        List<Gadget> gadgets = new ArrayList<Gadget>();
        gadgets.add(Gadget.squareBumper(1.0, 2.0));
        Ball ball = new Ball(2.0, 2.0, new Vect(-1.0, 0.0));
        Board testBoard = new Board(gadgets, Arrays.asList());
        assertTrue(0.75 == testBoard.minTimeUntilCollision(ball));
    }
    @Test // #3
    public void testminTimeUntilCollisionBall() {
        List<Gadget> gadgets = new ArrayList<Gadget>();
        Ball ball = new Ball(2.0, 2.0, new Vect(-1.0, 0.0));
        Ball ball1 = new Ball(0.0, 2.0, new Vect(1.0, 0.0));
        Board testBoard = new Board(gadgets, Arrays.asList(ball1));
        assertTrue(0.75 == testBoard.minTimeUntilCollision(ball));
    }
    @Test // #4
    public void testminTimeUntilCollisionMix() {
        List<Gadget> gadgets = new ArrayList<Gadget>();
        gadgets.add(Gadget.squareBumper(1.0, 2.0));
        gadgets.add(Gadget.squareBumper(5.0, 2.0));
        gadgets.add(Gadget.circleBumper(7.0, 2.0));
        Ball ball = new Ball(2.0, 2.0, new Vect(1.0, 0.0));
        Ball ball1 = new Ball(0.0, 2.0, new Vect(1.0, 0.0));
        Board testBoard = new Board(gadgets, Arrays.asList(ball1));
        assertTrue(2.75 == testBoard.minTimeUntilCollision(ball));
    }
    @Test // #5
    public void testminTimeUntilCollisionCloseWall() {
        List<Gadget> gadgets = new ArrayList<Gadget>();
        gadgets.add(Gadget.squareBumper(1.0, 2.0));
        gadgets.add(Gadget.squareBumper(5.0, 2.0));
        gadgets.add(Gadget.circleBumper(7.0, 2.0));
        Ball ball1 = new Ball(0.0, 2.0, new Vect(1.0, 0.0));
        Board testBoard = new Board(gadgets, Arrays.asList(ball1));
        assertTrue(0.75 == testBoard.minTimeUntilCollision(ball1));
    }

    // Test cases for  firstGadgetCollidedWith
    /**
     * Partitioning test cases into following subspaces:
     * 1. Testing whether returning first collided object for standard gadgets (gadgets except ball)
     * 2. Testing whether returning first collided object for ball gadgets.
     * 3. Testing whether it is returning correct answer for mix of standard and non standard gadgets
     */
    @Test // #1
    public void testfirstGadgetCollidedWithStandardGadget() {
        List<Gadget> gadgets = new ArrayList<Gadget>();
        Gadget sqBumper = Gadget.squareBumper(2.0, 4.0);
        gadgets.add(sqBumper);
        gadgets.add(Gadget.squareBumper(2.0, 6.0));
        Ball ball = new Ball(2.0, 2.0, new Vect(0.0, 1.0));
        Board testBoard = new Board(gadgets, Arrays.asList());
        assertTrue(sqBumper == testBoard.firstGadgetCollidedWith(ball));
    }
    @Test // #2
    public void testfirstBallCollidedWithBallGadget() {
        List<Gadget> gadgets = new ArrayList<Gadget>();
        Ball ball = new Ball(2.0, 2.0, new Vect(1.0, 0.0));
        Ball ball1 = new Ball(3.0, 3.0, new Vect(0.0, -1.0));
        Ball ball2 = new Ball(6.0, 2.0, new Vect(1.0, 0.0));
        Ball ball3 = new Ball(10.0, 10.0, new Vect(1.0, 1.0));
        Board testBoard = new Board(gadgets, Arrays.asList(ball1, ball2, ball3));
        assertTrue( ball1 == testBoard.firstBallCollidedWith(ball));
    }
    @Test // #3 change the sequence that it is computing 
    public void testfirstGadgetCollidedMix() {
        List<Gadget> gadgets = new ArrayList<Gadget>();
        Gadget sqrBumper = Gadget.squareBumper(3.0, 2.0);
        gadgets.add(sqrBumper);
        Ball ball = new Ball(2.0, 2.0, new Vect(1.0, 0.0));
        Ball ball1 = new Ball(3.0, 4.0, new Vect(0.0, -1.0));
        Ball ball2 = new Ball(6.0, 2.0, new Vect(1.0, 0.0));
        Ball ball3 = new Ball(10.0, 10.0, new Vect(1.0, 1.0));
        Board testBoard = new Board(gadgets, Arrays.asList(ball1, ball2, ball3));
        assertTrue( sqrBumper == testBoard.firstGadgetCollidedWith(ball));
    }

    // Test cases for update() method
    /**
     * Partitioning test cases into following subspaces:
     * 1. Testing whether balls position as well as velocity updates correctly  
     * 2. Testing whether ball is updating correctly if there were two collision with close 
     * 3. Since rest of ball friction and gravity is difficult to test we used inspection to test it
     * (See additional test boards on main for other )
     */
    @Test // #1 
    public void testUpdateWithoutFrictionWall(){
        Ball ball = new Ball(2.0, 2.0, new Vect(1.0, 0.0));
        Gadget outerWall = Gadget.outerwall(false, false, false, false);
        Board testBoard = new Board(Arrays.asList(outerWall), Arrays.asList(ball), 0.0, 0.0, 0.0);
        testBoard.update();
        // Seeing whether velocity is same
        assertTrue(ball.getVelocity().equals(new Vect(1.0, 0.0)));
        // Seeing whether location added by 0.05L
        assertTrue(ball.getPosition().equals(new Vect(2.05, 2.0)));
        // Seeing whether location is still correct in 200 iterations
        for (int i = 0; i<500; i++){
            testBoard.update();
        }
        // checking whether ball bounced off correctly
        assertTrue((ball.getPosition().minus(new Vect(12.45, 2.0))).length() < 3e-7);
    }
    @Test // #2
    public void testUpdateWithoutFrictionInterval(){
        Ball ball = new Ball(2.0, 2.0, new Vect(1.0, 0.0));
        Gadget recBump = Gadget.squareBumper(3.0, 2.0);
        Gadget outerWall = Gadget.outerwall(false, false, false, false);
        Board testBoard = new Board(Arrays.asList(outerWall), Arrays.asList(ball), 0.0, 0.0, 0.0);
        testBoard.update();
        // Seeing whether velocity is same
        assertTrue(ball.getVelocity().equals(new Vect(1.0, 0.0)));
        // Seeing whether location added by 0.05L
        assertTrue(ball.getPosition().equals(new Vect(2.05, 2.0)));
        // Seeing whether location is still correct in 200 iterations
        for (int i = 0; i<500; i++){
            testBoard.update();
        }
        // checking whether ball bounced off correctly
        assertTrue((ball.getPosition().minus(new Vect(12.45, 2.0))).length() < 3e-7);
    }

    // Test cases for Board(File file) constructor
    /**
     * Partitioning test cases into following subspaces:
     * Valid cases:
     *      1. Test all valid gadgets, options, comments, whitespace, etc.
     *         This includes Portals connected to each other on the same board and portals connected on different boards.
     * Invalid Cases
     *      2. Board not declared in first non comment line
     *      3. Mid line comments
     *      4. Trying to fire objects not named in file
     *      5. Options out of order    
     *      6. Accepts only =, and not == when assigning
     *      7. Additional Parameters
     * @throws IOException 
     */

    @Test //Test 1
    public void testValidBoardConstructorAllGadgets() throws IOException
    {
        File file = new File("src/phase2/boardConstructorValidContainsAllGadgets.pb");
        Board b = new Board(file);
        //also, by visual inspection we saw that all elements are there in the right place
    }

    @Test (expected=RuntimeException.class)//Test 2
    public void testBoardConstructorInvalidNotDeclaredFirstLine() throws IOException
    { 
        File file = new File("src/phase2/boardConstructorInvalidNotDeclaredInFirstLine.pb");
        Board b = new Board(file);
    }

    @Test (expected=RuntimeException.class)//Test 3
    public void testBoardConstructorInvalidMidLineComment() throws IOException
    {
        File file = new File("src/phase2/boardConstructorInvalidMidLineComment.pb");
        Board b = new Board(file);
    }
    @Test (expected=RuntimeException.class)//Test 4
    public void testboardConstructorInvalidFire() throws IOException
    {
        File file = new File("src/phase2/boardConstructorInvalidFire.pb");
        Board b = new Board(file);

    }

    @Test (expected=RuntimeException.class)//Test 5
    public void testboardConstructorInvalidOptionsOutOfOrder() throws IOException
    {
        File file = new File("src/phase2/boardConstructorOptionsOutOfOrder.pb");
        Board b = new Board(file);

    }
    
    @Test (expected=RuntimeException.class)//Test 6
    public void testboardConstructorInvalidEqualSign() throws IOException
    {
        File file = new File("src/phase2/boardConstructorInvalidEqualSign.pb");
        Board b = new Board(file);
    }
    
    @Test (expected=RuntimeException.class)//Test 7
    public void testboardConstructorAdditionalParameters() throws IOException
    {
        File file = new File("src/phase2/boardConstructorInvalidAdditionalParameters.pb");
        Board b = new Board(file);
    }
}