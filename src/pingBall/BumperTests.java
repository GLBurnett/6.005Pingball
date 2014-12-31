package pingBall;

import static org.junit.Assert.*;


import org.junit.Test;

import physics.Vect;


public class BumperTests {

    //Testing Strategy:
    //  All Bumpers:
    //      test timeUntilCollision with ball colliding on different sides
    //      getPosition returns origin of bumper
    //      ASCIIrepresentation of bumper is accurate at all orientations
    //      trigger method is called by collision, so testing collision is enough to see that trigger is working
    //      
    //  Square Bumper:
    //      Ball bounces on left/right/top/bottom
    //      Ball bounces on a corner
    //  Triangle Bumper:
    //      Ball bounces on sides parallel to outer walls
    //      Ball bounces on diagonal
    //      repeat tests on Rotated triangle
    //  Circle Bumper:
    //      Ball bounces on left/right/top/bottom
    //      Ball bounces at some weird angle (not left/right/top/bottom)
    
    //Square Bumper Tests
    @Test
    public void testSquareBumperBallBounceTop() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget sqrBump = Gadget.squareBumper(10.0, 10.0);
        assertTrue(sqrBump.timeUntilCollision(ball) == 8.25);
        sqrBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,-1.0));
    }
    
    @Test
    public void testSquareBumperBallBounceBottom() {
        Ball ball = new Ball(10.0, 17.5, new Vect(0.0,-1.0));
        Gadget sqrBump = Gadget.squareBumper(10.0, 10.0);
        assertTrue(sqrBump.timeUntilCollision(ball) == 6.25);
        sqrBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,1.0));
    }
    
    @Test
    public void testSquareBumperBallBounceLeft() {
        Ball ball = new Ball(1.5, 10.5, new Vect(1.0,0.0));
        Gadget sqrBump = Gadget.squareBumper(10.0, 10.0);
        assertTrue(sqrBump.timeUntilCollision(ball) == 8.25);
        sqrBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(-1.0,0.0));
    }
    
    @Test
    public void testSquareBumperBallBounceRight() {
        Ball ball = new Ball(18.5, 10.5, new Vect(-1.0,0.0));
        Gadget sqrBump = Gadget.squareBumper(10.0, 10.0);
        assertTrue(sqrBump.timeUntilCollision(ball) == 7.25);
        sqrBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(1.0,0.0));
    }
    
    @Test
    public void testSquareBumperBallBounceAngle() {
        Ball ball = new Ball(3.5, 2.5, new Vect(1.0,1.0));
        Gadget sqrBump = Gadget.squareBumper(10.0, 10.0);
        assertTrue(sqrBump.timeUntilCollision(ball) == 7.25);
        sqrBump.collision(ball);
        double dx = ball.getVelocity().x()-1.0;
        double dy = ball.getVelocity().y()+1.0;
        assertTrue(dx<0.002 && dx>-0.002);
        assertTrue(dy<0.002 && dy>-0.002);
    }
    
    @Test
    public void testSquareBumperBallBounceCorner() {
        Ball ball = new Ball(2.5, 2.5, new Vect(1.0,-1.0));
        Gadget sqrBump = Gadget.squareBumper(10.0, 10.0);
        assertTrue(sqrBump.timeUntilCollision(ball) >= 7);
        sqrBump.collision(ball);
        double dx = ball.getVelocity().x()-1.0;
        double dy = ball.getVelocity().y()+1.0;
        assertTrue(dx<0.002 && dx>-0.002);
        assertTrue(dy<0.002 && dy>-0.002);
    }
    
    @Test
    public void testSquareBumperGetPosition() {
        Gadget sqrBump = Gadget.squareBumper(10.0, 10.0);
        assertTrue(sqrBump.getPosition().equals(new Vect(10.0,10.0)));
    }
    
    @Test
    public void testSquareBumperASCII() {
        Gadget sqrBump = Gadget.squareBumper(10.0, 10.0);
        String[][] actual = sqrBump.toASCIIRep();
        String[][] expected = new String[1][1];
        expected[0][0] = "#";
        assertTrue(expected[0][0].equals(actual[0][0]));
        assertTrue(actual.length==1);
        assertTrue(actual[0].length==1);
    }
    
    //Circle Bumper Tests
    @Test
    public void testCircleBumperBallBounceTop() {
        Ball ball = new Ball(10.5, 1.5, new Vect(0.0,1.0));
        Gadget circBump = Gadget.circleBumper(10.0, 10.0);
        assertTrue(circBump.timeUntilCollision(ball) == 8.25);
        circBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,-1.0));
    }
    
    @Test
    public void testCircleBumperBallBounceBottom() {
        Ball ball = new Ball(10.5, 18.5, new Vect(0.0,-1.0));
        Gadget circBump = Gadget.circleBumper(10.0, 10.0);
        assertTrue(circBump.timeUntilCollision(ball) == 7.25);
        circBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,1.0));
    }
    
    @Test
    public void testCircleBumperBallBounceLeft() {
        Ball ball = new Ball(1.5, 10.5, new Vect(1.0,0.0));
        Gadget circBump = Gadget.circleBumper(10.0, 10.0);
        assertTrue(circBump.timeUntilCollision(ball) == 8.25);
        circBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(-1.0,0.0));
    }

    @Test
    public void testCircleBumperBallBounceRight() {
        Ball ball = new Ball(18.5, 10.5, new Vect(-1.0,0.0));
        Gadget circBump = Gadget.circleBumper(10.0, 10.0);
        assertTrue(circBump.timeUntilCollision(ball) == 7.25);
        circBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(1.0,0.0));
    }
    
    @Test
    public void testCircleBumperBallBounceAngle() {
        Ball ball = new Ball(1.5, 1.5, new Vect(1.0,-1.0));
        Gadget circBump = Gadget.circleBumper(10.0, 10.0);
        assertTrue(circBump.timeUntilCollision(ball) >= 8);
        circBump.collision(ball);
        double dx = ball.getVelocity().x()-1.0;
        double dy = ball.getVelocity().y()+1.0;
        assertTrue(dx<0.002 && dx>-0.002);
        assertTrue(dy<0.002 && dy>-0.002);
    }
    
    @Test
    public void testCircleBumperGetPosition() {
        Gadget circBump = Gadget.circleBumper(10.0, 10.0);
        assertTrue(circBump.getPosition().equals(new Vect(10.0,10.0)));
    }
    
    @Test
    public void testCircleBumperASCII() {
        Gadget circBump = Gadget.circleBumper(10.0, 10.0);
        String[][] actual = circBump.toASCIIRep();
        String[][] expected = new String[1][1];
        expected[0][0] = "O";
        assertTrue(expected[0][0].equals(actual[0][0]));
        assertTrue(actual.length==1);
        assertTrue(actual[0].length==1);
    }

    
    //Triangle Bumper 0 degree rotation tests
    @Test
    public void testTriangleBumperBallBounceParallelSide1() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0);
        assertTrue(triBump.timeUntilCollision(ball) == 8.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,-1.0));
    }
    
    @Test
    public void testTriangleBumperBallBounceParallelSide2() {
        Ball ball = new Ball(1.5, 10.5, new Vect(1.0,0.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0);
        assertTrue(triBump.timeUntilCollision(ball) == 8.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(-1.0,0.0));
    }
    
    @Test
    public void testTriangleBumperBallBounceDiagonalSide() {
        Ball ball = new Ball(18.5, 10.5, new Vect(-1.0,0.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0);
        assertTrue(triBump.timeUntilCollision(ball) >= 7);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,1.0));
    }
    
    @Test
    public void testTriangleBumperBallBounceCorner() {
        Ball ball = new Ball(2.5, 2.5, new Vect(1.0,-1.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0);
        assertTrue(triBump.timeUntilCollision(ball) >= 7);
        triBump.collision(ball);
        double dx = ball.getVelocity().x()-1.0;
        double dy = ball.getVelocity().y()+1.0;
        assertTrue(dx<0.002 && dx>-0.002);
        assertTrue(dy<0.002 && dy>-0.002);
    }
    
    @Test
    public void testTriangleBumperGetPosition() {
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0);
        assertTrue(triBump.getPosition().equals(new Vect(10.0,10.0)));
    }
    
    @Test
    public void testTriangleBumperASCII() {
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0);
        String[][] actual = triBump.toASCIIRep();
        String[][] expected = new String[1][1];
        expected[0][0] = "/";
        assertTrue(expected[0][0].equals(actual[0][0]));
        assertTrue(actual.length==1);
        assertTrue(actual[0].length==1);
    }
    
  //Triangle Bumper 90 degree tests
    @Test
    public void testTriangleBumper90BallBounceParallelSide1() {
        Ball ball = new Ball(10.0, 1.5, new Vect(0.0,1.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 90.0);
        assertTrue(triBump.timeUntilCollision(ball) == 8.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,-1.0));
    }
    
    @Test
    public void testTriangleBumper90BallBounceParallelSide2() {
        Ball ball = new Ball(18.5, 10.5, new Vect(-1.0,0.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 90.0);
        assertTrue(triBump.timeUntilCollision(ball) == 7.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(1.0,0.0));
    }
    
    @Test
    public void testTriangleBumper90BallBounceDiagonalSide() {
        Ball ball = new Ball(1.5, 10.5, new Vect(1.0,0.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 90.0);
        assertTrue(triBump.timeUntilCollision(ball) >= 8.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,1.0));
    }
    
    @Test
    public void testTriangleBumper90BallBounceCorner() {
        Ball ball = new Ball(2.5, 2.5, new Vect(1.0,-1.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 90.0);
        assertTrue(triBump.timeUntilCollision(ball) >= 7.25);
        triBump.collision(ball);
        double dx = ball.getVelocity().x()-1.0;
        double dy = ball.getVelocity().y()+1.0;
        assertTrue(dx<0.002 && dx>-0.002);
        assertTrue(dy<0.002 && dy>-0.002);
    }
    
    @Test
    public void testTriangleBumper90GetPosition() {
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 90.0);
        assertTrue(triBump.getPosition().equals(new Vect(10.0,10.0)));
    }
    
    @Test
    public void testTriangleBumper90ASCII() {
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 90.0);
        String[][] actual = triBump.toASCIIRep();
        String[][] expected = new String[1][1];
        expected[0][0] = "\\";
        assertTrue(expected[0][0].equals(actual[0][0]));
        assertTrue(actual.length==1);
        assertTrue(actual[0].length==1);
    }
    
    
    //Triangle Bumper 180 degree tests
    @Test
    public void testTriangleBumper180BallBounceParallelSide1() {
        Ball ball = new Ball(10.5, 18.5, new Vect(0.0,-1.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 180.0);
        assertTrue(triBump.timeUntilCollision(ball) == 7.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,1.0));
    }
    
    @Test
    public void testTriangleBumper180BallBounceParallelSide2() {
        Ball ball = new Ball(18.5, 10.5, new Vect(-1.0,0.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 180.0);
        assertTrue(triBump.timeUntilCollision(ball) == 7.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(1.0,0.0));
    }
    
    @Test
    public void testTriangleBumper180BallBounceDiagonalSide() {
        Ball ball = new Ball(1.5, 10.5, new Vect(1.0,0.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 180.0);
        assertTrue(triBump.timeUntilCollision(ball) >= 8);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,-1.0));
    }
    
    @Test
    public void testTriangleBumper180BallBounceCorner() {
        Ball ball = new Ball(18.5, 18.5, new Vect(-1.0,-1.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 180.0);
        assertTrue(triBump.timeUntilCollision(ball) >= 7);
        assertTrue(triBump.timeUntilCollision(ball) <= 8);
        triBump.collision(ball);
        double dx = ball.getVelocity().x()-1.0;
        double dy = ball.getVelocity().y()-1.0;
        assertTrue(dx<0.002 && dx>-0.002);
        assertTrue(dy<0.002 && dy>-0.002);
    }
    
    @Test
    public void testTriangleBumper180GetPosition() {
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 180.0);
        assertTrue(triBump.getPosition().equals(new Vect(10.0,10.0)));
    }
    
    @Test
    public void testTriangleBumper180ASCII() {
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 180.0);
        String[][] actual = triBump.toASCIIRep();
        String[][] expected = new String[1][1];
        expected[0][0] = "/";
        assertTrue(expected[0][0].equals(actual[0][0]));
        assertTrue(actual.length==1);
        assertTrue(actual[0].length==1);
    }

    
  //Triangle Bumper 270 degree rotation tests
    @Test
    public void testTriangleBumper270BallBounceParallelSide1() {
        Ball ball = new Ball(10.0, 18.5, new Vect(0.0,-1.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0);
        assertTrue(triBump.timeUntilCollision(ball) == 7.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,1.0));
    }
    
    @Test
    public void testTriangleBumper270BallBounceParallelSide2() {
        Ball ball = new Ball(1.5, 10.5, new Vect(1.0,0.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 270.0);
        assertTrue(triBump.timeUntilCollision(ball) == 8.25);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(-1.0,0.0));
    }
    
    @Test
    public void testTriangleBumper270BallBounceDiagonalSide() {
        Ball ball = new Ball(18.5, 10.5, new Vect(-1.0,0.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 270.0);
        assertTrue(triBump.timeUntilCollision(ball) >= 7);
        triBump.collision(ball);
        assertEquals(ball.getVelocity(),new Vect(0.0,-1.0));
    }
    
    @Test
    public void testTriangleBumper270BallBounceCorner() {
        Ball ball = new Ball(2.5, 2.5, new Vect(1.0,-1.0));
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 270.0);
        assertTrue(triBump.timeUntilCollision(ball) >= 7);
        triBump.collision(ball);
        double dx = ball.getVelocity().x()-1.0;
        double dy = ball.getVelocity().y()+1.0;
        assertTrue(dx<0.002 && dx>-0.002);
        assertTrue(dy<0.002 && dy>-0.002);
    }
    
    @Test
    public void testTriangleBumper270GetPosition() {
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 270.0);
        assertTrue(triBump.getPosition().equals(new Vect(10.0,10.0)));
    }
    
    @Test
    public void testTriangleBumper270ASCII() {
        Gadget triBump = Gadget.triangleBumper(10.0, 10.0, 270.0);
        String[][] actual = triBump.toASCIIRep();
        String[][] expected = new String[1][1];
        expected[0][0] = "\\";
        assertTrue(expected[0][0].equals(actual[0][0]));
        assertTrue(actual.length==1);
        assertTrue(actual[0].length==1);
    }
    
}
