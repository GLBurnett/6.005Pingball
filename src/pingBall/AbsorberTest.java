
package pingBall;



import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import physics.Vect;

public class AbsorberTest {
    
    //Testing Strategy:
    //  collision
    //      Check ball hitting all sides of the absorber
    //      Check ball hitting corner of absorber
    //      check that Ball is moved to appropriate location inside absorber and velocity is 0
    //      If a ball is shot, it should not be re-absorbed by the Absorber
    //      Check if multiple balls collide absorber holds them all
    //  timeUntilCollision
    //      Check ball hitting all sides of the absorber
    //  getPostion
    //      Check that origin (top left corner) is returned)
    //  doAction
    //      If the absorber is holding a ball
    //      If the absorber is not holding a ball
    //      If the previously ejected ball has not left the absorber
    //      If the absorber is self triggering, doAction should not be called by ball being shot 
    //      ( this is same as ball not colliding with absorber as it is being shot)
    //  trigger
    //      This is called by collision, so is already tested by collision
    //  toASCIIRep
    //      1 by 1 absorber
    //      Big absorber (20 by 3)
    //  toString
    //      Check absorber is displayed correctly
    //  getWidth and getHeight
    //      Checks that the absorber has the correct width and height

    @Test
    public void absorberToStringTest(){
        List<Gadget> triggeredGadgetsList = new ArrayList<>();
        Absorber myAbsorber = new Absorber(12.0, 15.0, 5, 2, triggeredGadgetsList);
        assertEquals(myAbsorber.toString().length(), 24);
    }
    
    @Test
    public void absorberGetLocationTest(){
        List<Gadget> triggeredGadgetsList = new ArrayList<>();
        assertEquals(new Absorber(12.0, 15.0, 5, 2, triggeredGadgetsList).getPosition(), new Vect(12.0, 15.0));
    }
       
    @Test
    public void absorberGetWidthAndGetHeightTest(){
        List<Gadget> triggeredGadgetsList = new ArrayList<>();
        Absorber abs1 = new Absorber(0.0, 20.0, 10, 2, triggeredGadgetsList);
        Absorber abs2 = new Absorber(0.0, 1.0, 15, 1, triggeredGadgetsList);
        Absorber abs3 = new Absorber(1.0, 1.0, 1, 1, triggeredGadgetsList);
        
        assertTrue(abs1.getWidth() == 10);
        assertTrue(abs2.getWidth() == 15);
        assertTrue(abs3.getWidth() == 1);
        
        assertTrue(abs1.getHeight() == 2);
        assertTrue(abs2.getHeight() == 1);
        assertTrue(abs3.getHeight() == 1);
    }    
    
    @Test
    public void testCollisionBallTop() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        
        assertTrue(absorber.timeUntilCollision(ball)==8.25);
    }
    
    @Test
    public void testCollisionBallBottom() {
        Ball ball = new Ball(10.0, 18.5, new Vect(0.0,-1.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        
        assertTrue(absorber.timeUntilCollision(ball)==7.25);
    }
    
    @Test
    public void testCollisionBallLeft() {
        Ball ball = new Ball(1.5, 10.5, new Vect(1.0, 0.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        
        assertTrue(absorber.timeUntilCollision(ball)==6.25);
    }
    
    @Test
    public void testCollisionBallRight() {
        Ball ball = new Ball(18.5, 10.5, new Vect(-1.0, 0.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        
        assertTrue(absorber.timeUntilCollision(ball)==6.25);
    }
    
    @Test
    public void testCollisionBallCorner() {
        Ball ball = new Ball(1.5, 1.5, new Vect(1.0, 1.0));
        Gadget absorber = Gadget.absorber(10.0, 10.0, 1, 1);
        
        assertTrue(absorber.timeUntilCollision(ball)>=8);
        assertTrue(absorber.timeUntilCollision(ball)<=8.5);
    }
    
    @Test
    public void testCollisionBallMovedToCorrectLocationAndZeroVelocity() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        
        absorber.collision(ball);
        assertTrue(ball.getPosition().equals(new Vect(11.75, 10.75)));
        assertTrue(ball.getVelocity().length() == 0);
        
    }
    
    @Test
    public void testCollisionAbsorberContainsBallAlready() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Ball ball2 = new Ball(10.0, 5.5, new Vect(0.0,0.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        absorber.collision(ball2);
        absorber.collision(ball);
        assertTrue(ball.getVelocity().equals(new Vect(0.0, 0.0)));
        assertTrue(ball2.getVelocity().equals(new Vect(0.0, 0.0)));
    }
    @Test
    public void testCollisionAbsorberHoldsMultipleBalls()
    {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Ball ball2 = new Ball(10.0, 5.5, new Vect(0.0,0.0));
        Ball ball3 = new Ball(8.0, 4.5, new Vect(3.0,0.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        absorber.collision(ball2);
        absorber.collision(ball);
        absorber.collision(ball3);
        assertTrue(absorber.getBallList().size()==3);
    }
    @Test
    public void testCollisionBallBeingShot() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        absorber.collision(ball);
        absorber.doAction();
        //Try to emulate how board would check and call collision:
        if (absorber.timeUntilCollision(ball)<5)
            absorber.collision(ball);
        assertTrue(ball.getVelocity().equals(new Vect(0.0, -50.0)));
    }

    @Test
    public void testGetPosition() {
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        assertTrue(absorber.getPosition().equals(new Vect(8.0,10.0)));
    }
    
    @Test
    public void testDoActionAbsorberContainsBall() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        absorber.collision(ball);
        absorber.doAction();
        assertTrue(ball.getVelocity().equals(new Vect(0.0,-50.0)));
        
    }
    
    @Test
    public void testDoActionAbsorberContainsNoBall() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        absorber.doAction();
        assertTrue(ball.getVelocity().equals(new Vect(0.0,1.0)));
        
    }
    
    @Test
    public void testDoActionPreviouslyShotBallHasNotLeftAbsorberYet() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget absorber = Gadget.absorber(8.0, 10.0, 4, 1);
        absorber.collision(ball);
        absorber.doAction();
        absorber.doAction();
        assertTrue(ball.getVelocity().equals(new Vect(0.0,-50.0)));
        
    }

    @Test
    public void testToASCIIRepOneByOne() {
        Gadget absorber = Gadget.absorber(8.0, 10.0, 1, 1);
        String[][] actual = absorber.toASCIIRep();
        String[][] expected = new String[1][1];
        expected[0][0] = "=";
        assertTrue(expected[0][0].equals(actual[0][0]));
        assertTrue(actual.length==1);
        assertTrue(actual[0].length==1);
    }
    
    @Test
    public void testToASCIIRepLarge() {
        Gadget absorber = Gadget.absorber(0.0, 10.0, 20, 3);
        String[][] actual = absorber.toASCIIRep();
        String[][] expected = new String[3][20];
        for (int i = 0; i<expected.length; i++)
            for (int j = 0; j<expected[0].length; j++)
                expected[i][j] = "=";
        for (int i = 0; i<expected.length; i++)
            for (int j = 0; j<expected[0].length; j++)
                assertTrue(expected[i][j].equals(actual[i][j]));
        assertTrue(actual.length==expected.length);
        assertTrue(actual[0].length==expected[0].length);
    }

}
