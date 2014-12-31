package pingBall;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList; 

import ClientGUI.ClientFrame;
import physics.*;

/**
 * Mutable 20L by 20L Board where the game of pingball is played
 * The board contains various gadgets used in pingball, including 
 * balls and an outer boundary.
 * The board can be printed to see an ASCII representation of the game.
 * The board can also have gravity and friction
 */
public class Board {

    //AF:
    //  This class represents the entirety of a 20L by 20L play area and all elements (Balls and gadgets) inside of it
    //RI:
    //  Each gadget's origin must be between (0,0) and (19,19)
    //  mu and mu2 must be non-negative values
    //  update frequency must be a positive value
    private String boardName; //Name of the board
    private List<Gadget> standardGadgets = new ArrayList<Gadget>();
    private List<Ball> ballGadgets = Collections.synchronizedList(new CopyOnWriteArrayList<Ball>(new ArrayList<Ball>())); 
    private final int updateFrequency = 50; //Measured in milliseconds
    private double boardGravity; //Measures in L/sec^2
    private double mu; //per second
    private double mu2; //per L
    private List<Ball> noGravityBalls;
    private String[][] boardRepresentation = new String[22][22]; //Used to store the ASCII representation of the board
    private final double epsilon = 1e-6; // the smallest minimum collision time
    private final double precisionCorrectionValue = 1e-7; //Used to correct for precision error of double
    private OuterWall outer = new OuterWall(false, false, false, false);
    private final int boardWitdth=20;// the dimension of the square board
    public enum direction {top, bottom, left, right}
    //A map where the key is a direction, and the corresponding String value is the name of the Board connected
    //on that side of this board
    private Map<direction, String> directionBoard = new HashMap<direction, String>();
    //A map where each key is a ball leaving this board to another board through a portal
    //Each corresponding value is a portal the ball is entering. Each portal should be linked 
    //to a portal on a different board
    private Map<Ball, Portal> ballPortal = new HashMap<Ball, Portal>(); 
    //A list of the portals on this board
    private List<Portal> portalsOnBoard = new ArrayList<Portal>();
    //A map where each key is a ball leaving this board through a boundary to another board
    //Each corresponding value is the direction the ball is leaving through
    private Map<Ball, direction> ballsLeavingBoard = Collections.synchronizedMap(new HashMap<Ball,direction>());
    
    //Map where key is the keyboard key, and the values are the lists of gadgets triggered by this key
    private Map<String, List<Gadget>> keyUpTriggeredGadgets = new HashMap<String, List<Gadget>>();
    private Map<String, List<Gadget>> keyDownTriggeredGadgets = new HashMap<String, List<Gadget>>();
    
    private boolean isPaused = false;
    
   

    /**
     * This constructor initializes a board using the default values of gravity and friction
     * @param gadgets the list of gadgets contained in the board. Should not contain
     * any OuterWalls, because there is one by default on every board.
     * @param balls the list of balls contained in the board
     */
    public Board(List<Gadget> gadgets, List<Ball> balls) {
        standardGadgets = gadgets;

        ballGadgets = balls;
        boardGravity = 25;
        mu = 0.025;
        mu2 = 0.025;
        noGravityBalls = new ArrayList<Ball>();
        for (int i = 0; i<22; i++) //initialize board representation array
            for (int j = 0; j<22; j++)
                boardRepresentation[i][j] = " ";
        for(Gadget g : standardGadgets)
        {
            if(g.isPortal())
            {
                Portal p = (Portal) g;
                portalsOnBoard.add(p);
            }
        }
        checkRep();
    }
    
    /**
     * Generates a board from the input file.
     * Each line in the file describes a board element, like a ball or bumper. 
     * Whitespace at the start or end of lines is irrelevant. Extra whitespace between 
     * tokens of a line (words or =) are not important. Lines that are blank, or lines 
     * that start with a #, are ignored.
     * The first non-comment line should define the board, and there should be only
     * one such line in the file.
     * @param file The file to generate the board from
     * @throws IOException If file is not found.
     * @throws RuntimeException if file is not formatted correctly
     */
    public Board(File file) throws IOException {
        Map<String, Gadget> gadgetMap = new HashMap<String, Gadget>();
        Map<String, Ball> ballMap = new HashMap<String, Ball>();
        Boolean boardLineRead = false;
        FileReader reader = new FileReader(file);
        BufferedReader buffReader = new BufferedReader(reader);
        noGravityBalls = new ArrayList<Ball>();
        for (int i = 0; i<22; i++) //initialize board representation array
            for (int j = 0; j<22; j++)
                boardRepresentation[i][j] = " ";
        while(buffReader.ready()) {
            String line = buffReader.readLine();
            line = line.trim(); //Remove whitespace at start and end
            if (line.startsWith("#") || line.startsWith("\n") || line.startsWith("\r\n") || line.matches("[\\s]+") || line.equals("")) {
                //Ignore line, it's a comment or empty line
            }
            else if (line.startsWith("board ")) {
                if (boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Multiple boards cannot be declared in a file!");
                }
                boardLineRead = true;
                Map <String, String> boardInfo = boardFromString(line);
                boardName = boardInfo.get("name");
                boardGravity = Double.parseDouble(boardInfo.get("boardGravity"));
                mu = Double.parseDouble(boardInfo.get("mu"));
                mu2 = Double.parseDouble(boardInfo.get("mu2"));
            }
            else if (line.startsWith("ball")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> ballInfo = ballFromString(line);
                String name = ballInfo.get("name");
                double x = Double.parseDouble(ballInfo.get("x"));
                double y = Double.parseDouble(ballInfo.get("y"));
                double xVel = Double.parseDouble(ballInfo.get("xVelocity"));
                double yVel = Double.parseDouble(ballInfo.get("yVelocity"));
                Ball b = new Ball(x, y, new Vect(xVel, yVel));
                if (!name.equals(""))
                    ballMap.put(name, b);
                ballGadgets.add(b);
            }
            else if (line.startsWith("squareBumper")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> gadgetInfo = gadgetFromString(line);
                String name = gadgetInfo.get("name");
                double x = Double.parseDouble(gadgetInfo.get("x"));
                double y = Double.parseDouble(gadgetInfo.get("y"));
                Gadget square = Gadget.squareBumper(x, y);
                if (!name.equals(""))
                    gadgetMap.put(name, square);
                standardGadgets.add(square);
            }
            else if (line.startsWith("circleBumper")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> gadgetInfo = gadgetFromString(line);
                String name = gadgetInfo.get("name");
                double x = Double.parseDouble(gadgetInfo.get("x"));
                double y = Double.parseDouble(gadgetInfo.get("y"));
                Gadget circ = Gadget.circleBumper(x, y);
                if (!name.equals(""))
                    gadgetMap.put(name, circ);
                standardGadgets.add(circ);
            }
            else if (line.startsWith("triangleBumper")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> gadgetInfo = gadgetFromString(line);
                String name = gadgetInfo.get("name");
                double x = Double.parseDouble(gadgetInfo.get("x"));
                double y = Double.parseDouble(gadgetInfo.get("y"));
                double orientation = Double.parseDouble(gadgetInfo.get("orientation"));
                Gadget tri = Gadget.triangleBumper(x, y, orientation);
                if (!name.equals(""))
                    gadgetMap.put(name, tri);
                standardGadgets.add(tri);
            }
            else if (line.startsWith("leftFlipper")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> gadgetInfo = gadgetFromString(line);
                String name = gadgetInfo.get("name");
                double x = Double.parseDouble(gadgetInfo.get("x"));
                double y = Double.parseDouble(gadgetInfo.get("y"));
                double orientation = Double.parseDouble(gadgetInfo.get("orientation"));
                Gadget lflip = Gadget.leftFlipper(x, y, orientation);
                if (!name.equals(""))
                    gadgetMap.put(name, lflip);
                standardGadgets.add(lflip);

            }
            else if (line.startsWith("rightFlipper")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> gadgetInfo = gadgetFromString(line);
                String name = gadgetInfo.get("name");
                double x = Double.parseDouble(gadgetInfo.get("x"));
                double y = Double.parseDouble(gadgetInfo.get("y"));
                double orientation = Double.parseDouble(gadgetInfo.get("orientation"));
                Gadget rflip = Gadget.rightFlipper(x, y, orientation);
                if (!name.equals(""))
                    gadgetMap.put(name, rflip);
                standardGadgets.add(rflip);
            }
            else if (line.startsWith("absorber")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> gadgetInfo = absorberFromString(line);
                String name = gadgetInfo.get("name");
                double x = Double.parseDouble(gadgetInfo.get("x"));
                double y = Double.parseDouble(gadgetInfo.get("y"));
                int width = Integer.parseInt(gadgetInfo.get("width"));
                int height = Integer.parseInt(gadgetInfo.get("height"));
                Gadget abs = Gadget.absorber(x, y, width, height);
                if (!name.equals(""))
                    gadgetMap.put(name, abs);
                standardGadgets.add(abs);
            }
            else if (line.startsWith("portal")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> gadgetInfo = portalFromString(line);
                String name = gadgetInfo.get("name");
                double x = Double.parseDouble(gadgetInfo.get("x"));
                double y = Double.parseDouble(gadgetInfo.get("y"));
                String otherBoardName = gadgetInfo.get("otherBoard");
                String otherPortal = gadgetInfo.get("otherPortal");
                Portal portal;
                if (otherBoardName.equals(""))
                    portal = new Portal(name, x, y, otherPortal, this);
                else
                    portal = new Portal(name, x, y, otherPortal, otherBoardName, this);
                standardGadgets.add(portal);
                portalsOnBoard.add(portal);
                gadgetMap.put(portal.getName(), portal);
            }
            else if (line.startsWith("fire")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> fireInfo = fireFromString(line);
                if (gadgetMap.containsKey(fireInfo.get("trigger")) && gadgetMap.containsKey(fireInfo.get("action"))) {
                    gadgetMap.get(fireInfo.get("trigger")).addToTriggeredList(gadgetMap.get(fireInfo.get("action")));
                } else {
                    buffReader.close();
                    throw new RuntimeException("fire format in file incorrect");
                }
            } else if (line.startsWith("keyup")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> keyActionInfo = keyActionFromString(line);
                keyUpTriggeredGadgets.putIfAbsent(keyActionInfo.get("key"), new ArrayList<Gadget>());
                keyUpTriggeredGadgets.get(keyActionInfo.get("key")).add(gadgetMap.get(keyActionInfo.get("action")));
            } else if (line.startsWith("keydown")) {
                if (!boardLineRead) {
                    buffReader.close();
                    throw new RuntimeException("Board must be first noncomment line in file");
                }
                Map <String, String> keyActionInfo = keyActionFromString(line);
                keyDownTriggeredGadgets.putIfAbsent(keyActionInfo.get("key"), new ArrayList<Gadget>());
                keyDownTriggeredGadgets.get(keyActionInfo.get("key")).add(gadgetMap.get(keyActionInfo.get("action")));
            } else {
                buffReader.close();
                throw new RuntimeException("Couldn't read file correctly");
            }   
        }
        buffReader.close();
        checkRep();
    }

    /**
     * This constructor initializes a board with configurable values of gravity and friction
     * @param gadgets the list of gadgets contained in the board. Should not contain any Outer
     * Walls, as these are in any Boards by default.
     * @param balls the list of balls contained in the board
     * @param gravity the value of gravity to use, in units of L/sec^2
     * @param mu the value of mu to use, in units of 1/sec
     * @param mu2 the value of mu2 to use, in units of 1/L
     */
    public Board(List<Gadget> gadgets, List<Ball> balls, double gravity, double mu, double mu2) {
        standardGadgets = gadgets;
        ballGadgets = balls;
        boardGravity = gravity;
        this.mu = mu;
        this.mu2 = mu2;
        noGravityBalls = new ArrayList<Ball>();
        for (int i = 0; i<22; i++) //initialize board representation array
            for (int j = 0; j<22; j++)
                boardRepresentation[i][j] = " ";

        for(Gadget g : standardGadgets)
        {
            if(g.isPortal())
            {
                Portal p = (Portal) g;
                portalsOnBoard.add(p);
            }
        }
        checkRep();
    }
    
    public Board(String name){
    	this.boardName=name;
    }
    public void setMu(double mu){
    	this.mu=mu;
    }
    public void setMu2(double mu2){
    	this.mu2=mu2;
    }
    public void setG(double g){
    	this.boardGravity=g;
    }
    public double getMu(){
    	return mu;
    }
    public double getG(){
    	return boardGravity;
    }
    public  double getMu2(){
    	return mu2;
    }
    
    /**
     * gadgets triggered when a key is released
     * @return a map where key is the keyboard key, and the values are the lists of gadgets triggered by this key
     */
    public Map<String, List<Gadget>> getKeyUpGadgets(){
        return keyUpTriggeredGadgets;
    }
    
    /**
     * gadgets triggered when a key is pressed
     * @return a map where key is the keyboard key, and the values are the lists of gadgets triggered by this key
     */
    public Map<String, List<Gadget>> getKeyDownGadgets(){
        return keyDownTriggeredGadgets;
    }
    
    /**
     * 
     * Connect another board with this board.
     * Also sets the wall corresponding to direction to d as invisible.
     * @param d the direction of this board to connect the other board to.
     * Should be top, bottom, left, or right
     * @param name the name of the other board
     */
    public void addConnection(direction d, String name)
    {
        directionBoard.put(d, name);
        if (d.toString().equals("left"))
        {
            outer.setInvisLeft(true);
        }
        if (d.toString().equals("right"))
        {
            outer.setInvisRight(true);
        }
        if (d.toString().equals("top"))
        {
            outer.setInvisTop(true);
        }
        if (d.toString().equals("bottom"))
        {
            outer.setInvisBottom(true);
        }
    }

    /**
     * Removes the connection on the side specified by d
     * from the board, and updates its outer walls accordingly
     * @param d the direction of the connection to remove. Should be top, bottom, left, or right
     */
    public void removeConnection(direction d) {
        directionBoard.remove(d);
        if (d.toString().equals("left"))
        {
            outer.setInvisLeft(false);
        }
        if (d.toString().equals("right"))
        {
            outer.setInvisRight(false);
        }
        if (d.toString().equals("top"))
        {
            outer.setInvisTop(false);
        }
        if (d.toString().equals("bottom"))
        {
            outer.setInvisBottom(false);
        }
    }

    /**
     * Returns the name of the board connected on the side specified by d
     * @param d the direction of the connection. Should be top, bottom, left, or right
     * @return the name of the board connected the side d of this board
     */
    public String getConnection(direction d) {
        return directionBoard.get(d);
    }

    /**
     * Updates the velocity of every ball in BallGadgets to account for
     * changes in velocity due to gravity and friction. Friction is taken into
     * account before gravity is.
     */
    public void updateBallGravityFriction() {
        noGravityBalls.clear();
        for (Gadget gadget : standardGadgets){
            for (Ball ball: gadget.getBallList()){ //Gets list of balls stored in Gadgets (so not affected by gravity)
                noGravityBalls.add(ball);
            }
        }
        for (Ball ball:ballGadgets) {
            if (!(noGravityBalls.contains(ball))){
                //First we apply friction
                double frictionCoefficient = 1-mu*(updateFrequency/1000.0)-mu2*ball.getVelocity().length()*(updateFrequency/1000.0);
                ball.setVelocity(ball.getVelocity().times(frictionCoefficient));
                //Then gravity
                Vect gravity = new Vect(0, boardGravity);
                ball.setVelocity(ball.getVelocity().plus(gravity.times(updateFrequency/1000.0)));
            }
        }
    }

    /**
     * Used to determine the minimum time until ball collides with another ball or gadget
     * This assumes the balls' velocity is not changing
     * @param ball the ball to check minimum collision time for
     * @return the time in seconds when the ball first collides with something else
     */
    public double minTimeUntilCollision(Ball ball) {
        double minTime = Double.MAX_VALUE;
        // First check collision with gadgets
        for (Gadget gadget:standardGadgets) {
            if (gadget.timeUntilCollision(ball)<minTime)
                minTime = gadget.timeUntilCollision(ball);
        }
        // Then collisions with other balls
        for (Ball otherBall:ballGadgets) {
            if (!(otherBall==ball)) // Can't collide with itself
                if (otherBall.timeUntilCollision(ball)<minTime)
                    minTime = otherBall.timeUntilCollision(ball);
        }
        //Then collisions with outer wall
        if (outer.timeUntilCollision(ball)<minTime){
            minTime = outer.timeUntilCollision(ball);
        }
        return minTime;
    }


    /**
     * Creates a new ball with velocity ballVelocity at the portal on this board
     * specified by portalName
     * @param portalName name of the portal on this board
     * @param ballVelocity exit velocity of a ball from the portal
     * @return true if the ball was successfully created, false otherwise (portal wasn't
     * found on the board.)
     */
    public boolean ballExitingPortal(String portalName, Vect ballVelocity)
    {
        boolean containsPortal = false;
       
        for(Portal p : portalsOnBoard)
        {
            if(p.getName().equals(portalName))
            {
                containsPortal = true;
                //new position of the ball after it collides
                Vect newPos = ballVelocity.unitSize().times(.8).plus(p.getPosition().plus(new Vect(0.5,0.5)));
       
                Ball b = new Ball(newPos.x(), newPos.y(), ballVelocity);
                ballGadgets.add(b);
                break;
            }
        }
        checkRep();
        return containsPortal;
    }

    /**
     * Adds a ball to this board. If the ball is specified in a position it isn't allowed to be,
     * no ball is added.
     * @param b
     */
    public void addBall(Ball b)
    {
        Double criticalTime = 1.0;
        Ball testBall; 
        if ((b.getPosition().x() == .26) || (b.getPosition().x() == 19.74) || (b.getPosition().y() == .26) || (b.getPosition().y() == 19.74)) {
            if (b.getPosition().x() == .26){
                testBall = new Ball(-0.75, b.getPosition().y(), new Vect(1, 0));
            } else if (b.getPosition().x() == 19.74){
                testBall = new Ball(20.75, b.getPosition().y(), new Vect(-1, 0));
            } else if (b.getPosition().y() == .26){
                testBall = new Ball(b.getPosition().x(), -0.75, new Vect(0, 1));
            } else {
                testBall = new Ball(b.getPosition().x(), 20.75, new Vect(0, -1));
            }

            if(minTimeUntilCollision(testBall)<=criticalTime){
                updateBallVelocityIfCollision(testBall);
                setVelocityIfGoingOutOfBounds(testBall.getPosition().x(), testBall.getPosition().y(), testBall);
                return;
            }

        }

        ballGadgets.add(b);
        checkRep();
    }

    /**
     * removes a specific ball from the board
     * @param ball
     * 
     */
    public void removeBall(Ball ball)
    {
        for(Ball b: ballGadgets)
        {
            if(b.equals(ball))
            {
                ballGadgets.remove(b);
                return;
            }
        }
    }
    
    /**
     *  
     * Creates a map where key is balls that are traveling through walls to different boards, 
     * and direction reflects which wall they are traveling out of.
     * This method also clears these balls from the current board.
     * @return a map as described above.
     */
    public Map<Ball, direction> ballsTravelingToDifferentBoardsThroughWalls()
    {
        Map<Ball, direction> returnMap = new HashMap<Ball, direction>();
        returnMap.putAll(ballsLeavingBoard);
        for (Ball ball: ballsLeavingBoard.keySet()) {
            if (ballGadgets.contains(ball))
                ballGadgets.remove(ball);
        }
        ballsLeavingBoard.clear();
        return returnMap;
    }

    /**
     * Add a gadget to the board. Gadget should not be an Outer Wall.
     * @param g
     */
    public void addGadgetToBoard(Gadget g) {
        standardGadgets.add(g);
        if(g.isPortal())
        {
            Portal p = (Portal) g;
            portalsOnBoard.add(p);
        }
        checkRep();
    }
    /**
     * Returns a Map where each key value is a ball leaving the board through a portal,
     * and each ball's value is the portal it's exiting through.
     * This method also clears these balls from the current board.
     * @return the map described above
     */
    public Map<Ball, Portal> ballsLeavingThroughPortals() {
        Map<Ball,Portal> returnMap = new HashMap<Ball,Portal>();
        returnMap.putAll(ballPortal);
        for (Ball ball: ballPortal.keySet()) {
            if (ballGadgets.contains(ball))
                ballGadgets.remove(ball);
        }
        ballPortal.clear();
        checkRep();
        return returnMap;
    }

    /**
     * Used to determine the first gadget ball will collide with
     * This assumes the ball's velocity is not changing
     * @param ball the ball we are checking collisions for
     * @return the gadget that ball will first collide with
     */
    public Gadget firstGadgetCollidedWith(Ball ball) {
        Gadget firstGadget = outer; // By default we assume the bal will collide with an outerwall first
        double minTime = Double.MAX_VALUE;
        // First check collision with gadgets
        for (Gadget gadget:standardGadgets) {
            if (gadget.timeUntilCollision(ball)<minTime) {
                minTime = gadget.timeUntilCollision(ball);
                firstGadget = gadget;
            }
        }
        if (outer.timeUntilCollision(ball)<minTime){
            firstGadget = outer;
        }
        checkRep();
        return firstGadget;
    }

    /**
     * Used to determine the first ball that this ball will collide with
     * This assumes the ball's velocity is not changing
     * @param ball the ball we are checking collisions for
     * @return the ball that this ball will first collide with
     */
    public Ball firstBallCollidedWith(Ball ball) {
        Ball firstBall = ballGadgets.get(0);
        double minTime = Double.MAX_VALUE;
        // Then collisions with other balls
        for (Ball otherBall:ballGadgets) {
            if (!(otherBall==ball)) // Can't collide with itself
                if (otherBall.timeUntilCollision(ball)<minTime) {
                    minTime = otherBall.timeUntilCollision(ball);
                    firstBall = otherBall;
                }
        }
        return firstBall;
    }

    /**
     * Prints an ASCII representation of the board to the console
     */
    public void print() {
        //First clear the board
        for (int i = 0; i<22; i++)
            for (int j = 0; j<22; j++)
                boardRepresentation[i][j] = " ";
        // Add outer walls

        for (int i = 0; i<boardRepresentation.length; i++){
            String letterAtPosition = ".";
            if(directionBoard.containsKey(direction.top))
            {
                if(i-1 < directionBoard.get(direction.top).length() && i-1 >= 0) //if the position is less than the length of the name, extract the character
                {    
                    letterAtPosition = Character.toString(directionBoard.get(direction.top).charAt(i-1));
                }
                boardRepresentation[0][i] = letterAtPosition;//top wall
            }
            else
            {
                boardRepresentation[0][i] = ".";//top wall    
            }
            letterAtPosition = ".";
            if(directionBoard.containsKey(direction.bottom))
            {
                if(i-1 < directionBoard.get(direction.bottom).length() && i-1 >= 0) //if the position is less than the length of the name, extract the character
                {    
                    letterAtPosition = Character.toString(directionBoard.get(direction.bottom).charAt(i-1));
                }

                boardRepresentation[21][i] = letterAtPosition;//bottom wall
            }
            else
            {
                boardRepresentation[21][i] = ".";//bottom wall
            }
        }
        for (int i = 0; i<boardRepresentation[0].length; i++){
            String letterAtPosition = ".";
            if(directionBoard.containsKey(direction.left))
            {
                if(i-1 < directionBoard.get(direction.left).length() && i-1 >= 0) //if the position is less than the length of the name, extract the character
                {    
                    letterAtPosition = Character.toString(directionBoard.get(direction.left).charAt(i-1));
                }
                boardRepresentation[i][0] = letterAtPosition;//left wall

            }
            else
            {
                boardRepresentation[i][0] = ".";//left wall
            }
            letterAtPosition = ".";
            boardRepresentation[i][21] = ".";//right wall

            if(directionBoard.containsKey(direction.right))
            {
                if(i-1 < directionBoard.get(direction.right).length() && i-1 >= 0) //if the position is less than the length of the name, extract the character
                {    
                    letterAtPosition = Character.toString(directionBoard.get(direction.right).charAt(i-1));
                }
                boardRepresentation[i][21] = letterAtPosition;//right wall
            }
            else
            {
                boardRepresentation[i][21] = ".";//right wall
            }
        }

        // Add in gadgets
        for (Gadget gadget: standardGadgets){
            String[][] gadgetStringArray = gadget.toASCIIRep();
            int rows = 0;
            int cols = 0;
            if (gadgetStringArray.length==0){
                //do nothing since gadget has no ascii representation
            }
            else {
                rows = gadgetStringArray.length;
                cols = gadgetStringArray[0].length;
            }
            int origin_x = (int)(gadget.getPosition().x()+1); //Add 1 to account for offset due to outer walls
            int origin_y = (int)(gadget.getPosition().y()+1); //Add 1 to account for offset due to outer walls
            for (int y = 0; y<rows; y++) {
                for (int x = 0; x<cols; x++) {
                    boardRepresentation[y+origin_y][x+origin_x] = gadgetStringArray[y][x];
                }
            }
        }
        //Add in balls the same way as we did gadgets
        for (Ball ball: ballGadgets){
            String[][] gadgetStringArray = ball.toASCIIRep();
            int rows = 0;
            int cols = 0;
            if (gadgetStringArray.length==0){
                //do nothing
            }
            else {
                rows = gadgetStringArray.length;
                cols = gadgetStringArray[0].length;
            }
            int origin_x = (int)(ball.getPosition().x()+1); //Add 1 to account for offset due to outer walls
            int origin_y = (int)(ball.getPosition().y()+1); //Add 1 to account for offset due to outer walls
            for (int x = 0; x<rows; x++) {
                for (int y = 0; y<cols; y++) {
                    boardRepresentation[y+origin_y][x+origin_x] = gadgetStringArray[y][x];
                }
            }
        }
        //Actual printing
        for (int i = 0; i<boardRepresentation.length; i++){
            for (int j = 0; j<boardRepresentation[0].length; j++)
                System.out.print(boardRepresentation[i][j]);
            System.out.print("\n");
        }

    }

    /**
     * Assumes the ball is going to collide within the next time step, so updates it's velocity accordingly
     * @param ball the ball to update velocities for
     */
    public void updateBallVelocityIfCollision(Ball ball) {
        Gadget firstGadget = firstGadgetCollidedWith(ball);
        double ballTime;
        if (ballGadgets.size() == 0){
            ballTime = Double.MAX_VALUE;
        } else {
            Ball firstBall = firstBallCollidedWith(ball);
            ballTime = firstBall.timeUntilCollision(ball);
        }

        double gadgetTime = firstGadget.timeUntilCollision(ball);

        if (gadgetTime<=ballTime) {
            if (firstGadget.isPortal()) {
                Portal p = (Portal)firstGadget;
                if (p.isLinkedToAnotherBoard()) {
                    p.trigger();
                    ballPortal.put(ball, p);
                    ballGadgets.remove(ball);
                }
                else {
                    for (Portal portal: portalsOnBoard) {
                        if (portal.getName().equals(p.getOtherPortalName())) {
                            p.collision(ball);
                            break;
                        }
                    }
                }
            } else
                firstGadget.collision(ball);
        }
        else if (ballGadgets.size()>0) {
            Ball firstBall = firstBallCollidedWith(ball);
            firstBall.collision(ball);
        }
        checkRep();
    }


    /**
     * This will be called at every time step and
     * update the board accordingly
     */
    public void update() {
        if(isPaused){
        	return;
        }
        
        for (Gadget gadget: standardGadgets){
            gadget.updatePosition(updateFrequency/1000.0); //Updates any moving gadgets in the board (like flippers)
        }

        updateBallGravityFriction(); //Apply friction and gravity
        double minBallCollisionTime = Double.MAX_VALUE;
        double timeUntilTimestepEnds = updateFrequency/1000.0;
        //First find minimum collision time of all balls
        for (Ball ball: ballGadgets) {
            if (minTimeUntilCollision(ball)<minBallCollisionTime)
                minBallCollisionTime = minTimeUntilCollision(ball);
        }
        //No collision in the next time step
        if (minBallCollisionTime> updateFrequency/1000.0) {
            for (Ball ball: ballGadgets) {
                //Check if ball will go out of bounds just in case
                Ball testBall = new Ball(ball.getPosition().x(), ball.getPosition().y(), ball.getVelocity());
                testBall.updatePosition(updateFrequency/1000.0, outer);

                Double testBallX = testBall.getPosition().x();
                Double testBallY = testBall.getPosition().y();
                setVelocityIfGoingOutOfBounds(testBallX, testBallY, ball);
                ball.updatePosition(updateFrequency/1000.0, outer);
            }
        } else { //There will be a collision in the next time step
            while (timeUntilTimestepEnds>minBallCollisionTime && minBallCollisionTime>epsilon) { //Loop while there will be a collision in the time remaining in the time step
                List<Ball> collidingBalls = new ArrayList<Ball>();
                //Find the ball that collides in minBallCollisionTime
                for (Ball ball: ballGadgets) {
                    if (minTimeUntilCollision(ball) <= minBallCollisionTime){
                        collidingBalls.add(ball);
                    }
                }
                for (Ball ball: ballGadgets) {
                    //Check if ball will go out of bounds just in case
                    Ball testBall = new Ball(ball.getPosition().x(), ball.getPosition().y(), ball.getVelocity());
                    testBall.updatePosition(minBallCollisionTime-precisionCorrectionValue, outer);
                    Double testBallX = testBall.getPosition().x();
                    Double testBallY = testBall.getPosition().y();
                    setVelocityIfGoingOutOfBounds(testBallX, testBallY, ball);
                    ball.updatePosition((minBallCollisionTime-precisionCorrectionValue), outer);
                }
                for (Ball ball: collidingBalls) {
                    updateBallVelocityIfCollision(ball);
                }
                timeUntilTimestepEnds -= (minBallCollisionTime-precisionCorrectionValue); //Subtract elapsed time
                minBallCollisionTime = Double.POSITIVE_INFINITY;
                //Find new minTimeUntilCollision of all balls
                for (Ball ball: ballGadgets) {
                    if (minTimeUntilCollision(ball)<minBallCollisionTime)
                        minBallCollisionTime = minTimeUntilCollision(ball);
                }
            }
            //And finally, update everything to account for time left in time step
            for (Ball ball: ballGadgets) {
                //And just in case, test if ball will somehow get outside of the board
                Ball testBall = new Ball(ball.getPosition().x(), ball.getPosition().y(), ball.getVelocity());
                testBall.updatePosition(timeUntilTimestepEnds, outer);
                Double testBallX = testBall.getPosition().x();
                Double testBallY = testBall.getPosition().y();
                setVelocityIfGoingOutOfBounds(testBallX, testBallY, ball);
                ball.updatePosition(timeUntilTimestepEnds, outer);
            }
        }
        checkRep();
       // print();
    }

    /**
     * @param testBallX
     * @param testBallY
     * @param ball
     * If the ball will go out of bounds and wall is not invisible, change velocity first to account for collision
     * If wall is invisible, do not change the velocity
     * If the wall is invisible, send the ball to connected board
     */
    public void setVelocityIfGoingOutOfBounds(double testBallX, double testBallY, Ball ball)
    {
        if(testBallY > 19.75)
        {
            if (!outer.isInvisBottom())
                updateBallVelocityIfCollision(ball);
            else if (outer.isInvisBottom()) {
                ballsLeavingBoard.put(ball, direction.bottom);
                ballGadgets.remove(ball);
            }
        }
        if(testBallX > 19.75)
        {
            if (!outer.isInvisRight())
                updateBallVelocityIfCollision(ball);
            else if (outer.isInvisRight()) {
//                System.out.println("here");
                ballsLeavingBoard.put(ball, direction.right);
                ballGadgets.remove(ball);
            }
        }
        if(testBallX < 0.25)
        {
            if (!outer.isInvisLeft())
                updateBallVelocityIfCollision(ball);
            else if (outer.isInvisLeft()) {
                ballsLeavingBoard.put(ball, direction.left);
                ballGadgets.remove(ball);
            }
        }
        if(testBallY < 0.25)
        {
            if (!outer.isInvisTop())
                updateBallVelocityIfCollision(ball);
            else if (outer.isInvisTop()) {
                ballsLeavingBoard.put(ball, direction.top);
                ballGadgets.remove(ball);
            }
        }
    }
    /**
     *
     * @return the update frequency of the board in milliseconds
     */
    public int getUpdateFrequency() {
        return updateFrequency;
    }
    /**
     * Takes an input string of the form:
     * board name=NAME gravity=FLOAT friction1=FLOAT friction2=FLOAT
     * Where gravity, friction1, and friction 2 are optional, and generates
     * a map where each key is the parameter name and each value is a String 
     * that represents the corresponding value. if parameters are not provided, 
     * default values are used
     * @param line String of the form board name=NAME gravity=FLOAT friction1=FLOAT friction2=FLOAT
     * @return A Map containing keys "name", "boardGravity", "mu", "mu2" and their corresponding values as Strings
     */
    Map<String, String> boardFromString(String line) {
        String[] splitLine = line.split("[ ]*=[ ]*|[ ]+");
        List<Integer> validLengths = Arrays.asList(3, 5, 7, 9);
        String name;
        Map<String, String> returnMap = new HashMap<String, String>();
        String gravity = "25";
        String fric1 = ".025";
        String fric2 = ".025";
        if (!validLengths.contains(splitLine.length))
            throw new RuntimeException("Incorrect File Formatting");
        if (!splitLine[0].equals("board"))
            throw new RuntimeException("Incorrect File Formatting");
        if (!splitLine[1].equals("name"))
            throw new RuntimeException("Incorrect File Formatting");
        name = splitLine[2];
        if (splitLine.length == 5) {
            if (splitLine[3].equals("gravity"))
                gravity = splitLine[4];
            else
                throw new RuntimeException("Incorrect File Formatting");
        }
        else if (splitLine.length == 7) {
            if (!splitLine[3].equals("friction1") || !splitLine[5].equals("friction2"))
                throw new RuntimeException("Incorrect File Formatting");
            fric1 = splitLine[4];
            fric2 = splitLine[6];
        }
        else if (splitLine.length == 9) {
            if (!splitLine[3].equals("gravity") || !splitLine[5].equals("friction1") || !splitLine[7].equals("friction2"))
                throw new RuntimeException("Incorrect File Formatting");
            gravity = splitLine[4];
            fric1 = splitLine[6];
            fric2 = splitLine[8];
        }
        returnMap.put("name", name);
        returnMap.put("boardGravity", gravity);
        returnMap.put("mu", fric1);
        returnMap.put("mu2", fric2);
        return returnMap;
    }

    /**
     * Takes an input string of the form:
     * ball name=NAME x=FLOAT y=FLOAT xVelocity=FLOAT yVelocity=FLOAT
     * and generates a map where each key is the parameter name and each value is a String 
     * that represents the corresponding value.
     * @param line String of the form: ball name=NAME x=FLOAT y=FLOAT xVelocity=FLOAT yVelocity=FLOAT
     * @return A Map containing keys "name", "x", "y", "xVelocity", "yVelocity" and their corresponding values as Strings
     */
    Map<String, String> ballFromString(String line) {
        String[] splitLine = line.split("[ ]*=[ ]*|[ ]+");
        List<Integer> validLengths = Arrays.asList(9,11);
        String name = "";
        Map<String, String> returnMap = new HashMap<String, String>();
        String x;
        String y;
        String xVelocity;
        String yVelocity;
        if (!validLengths.contains(splitLine.length))
            throw new RuntimeException("Incorrect File Formatting");
        if (!splitLine[0].equals("ball"))
            throw new RuntimeException("Incorrect File Formatting");
        if (splitLine.length==11) {
            if (!splitLine[1].equals("name"))
                throw new RuntimeException("Incorrect File Formatting");
            name = splitLine[2];
            if (!splitLine[3].equals("x") || !splitLine[5].equals("y")
                    || !splitLine[7].equals("xVelocity")
                    || !splitLine[9].equals("yVelocity"))
                throw new RuntimeException("Incorrect File Formatting");
            x = splitLine[4];
            y = splitLine[6];
            xVelocity = splitLine[8];
            yVelocity = splitLine[10];
        }
        else {
            if (!splitLine[1].equals("x") || !splitLine[3].equals("y")
                    || !splitLine[5].equals("xVelocity")
                    || !splitLine[7].equals("yVelocity"))
                throw new RuntimeException("Incorrect File Formatting");
            x = splitLine[2];
            y = splitLine[4];
            xVelocity = splitLine[6];
            yVelocity = splitLine[8];
        }
        returnMap.put("name", name);
        returnMap.put("x", x);
        returnMap.put("y", y);
        returnMap.put("xVelocity", xVelocity);
        returnMap.put("yVelocity", yVelocity);
        return returnMap;
    }

    /**
     * Takes an input string of the form:
     * gadgetName name=NAME x=INTEGER y=INTEGER orientation=0|90|180|270
     * where gadgetName is squareBumper, triangleBumper, circleBumper, rightFlipper, or leftFlipper
     * and orientation is optional for triangleBumper, rightFlipper, and leftFlipper,
     * and generates a map where each key is the parameter name and each value is a String 
     * that represents the corresponding value.
     * @param line String of the form: gadgetName name=NAME x=INTEGER y=INTEGER orientation=0|90|180|270
     * @return A Map containing keys "name", "x", "y", "orientation" (if applicable),  and their corresponding values as Strings
     */
    Map<String, String> gadgetFromString(String line) {
        String[] splitLine = line.split("[ ]*=[ ]*|[ ]+");
        List<Integer> validLengths = new ArrayList<Integer>();
        validLengths.add(5);
        validLengths.add(7);
        if (splitLine[0].equals("triangleBumper") || splitLine[0].equals("leftFlipper") || splitLine[0].equals("rightFlipper"))
            validLengths.add(9);
        String name = "";
        Map<String, String> returnMap = new HashMap<String, String>();
        String x="";
        String y="";
        String orientation = "0";
        if (!validLengths.contains(splitLine.length))
            throw new RuntimeException("Incorrect File Formatting");
        if (splitLine[0].equals("squareBumper") || splitLine[0].equals("circleBumper")) {
            if (splitLine.length==7) { //Specifies x y and name
                if (!splitLine[1].equals("name") || !splitLine[3].equals("x") || !splitLine[5].equals("y"))
                    throw new RuntimeException("Incorrect File Formatting");
                name = splitLine[2];
                x = splitLine[4];
                y = splitLine[6];
            }
            else { //specifies only x y
                if (!splitLine[1].equals("x") || !splitLine[3].equals("y"))
                    throw new RuntimeException("Incorrect File Formatting");
                x = splitLine[2];
                y = splitLine[4];
            }
        }
        else if (splitLine[0].equals("triangleBumper") || splitLine[0].equals("leftFlipper") || splitLine[0].equals("rightFlipper")) {
            if (splitLine.length==5) {
                if (!splitLine[1].equals("x") || !splitLine[3].equals("y"))
                    throw new RuntimeException("Incorrect File Formatting");
                x = splitLine[2];
                y = splitLine[4];
            }
            if (splitLine.length==7) {
                if (splitLine[1].equals("name")) { //So doesn't include orientation
                    if (!splitLine[3].equals("x") || !splitLine[5].equals("y"))
                        throw new RuntimeException("Incorrect File Formatting");
                    name = splitLine[2];
                    x = splitLine[4];
                    y = splitLine[6];
                }
                else if (splitLine[1].equals("x")) { //So doesn't include name
                    if (!splitLine[3].equals("y") || !splitLine[5].equals("orientation"))
                        throw new RuntimeException("Incorrect File Formatting");
                    orientation = splitLine[6];
                    x = splitLine[2];
                    y = splitLine[4];
                }
                else
                    throw new RuntimeException("Incorrect File Formatting");
            }
            if (splitLine.length==9) { //specifies name x y orientation
                if (!splitLine[1].equals("name") || !splitLine[3].equals("x")|| !splitLine[5].equals("y") || 
                        !splitLine[7].equals("orientation"))
                    throw new RuntimeException("Incorrect File Formatting");
                name = splitLine[2];
                x = splitLine[4];
                y = splitLine[6];
                orientation = splitLine[8];
            }
        } else {
            throw new RuntimeException("Incorrect File Formatting");
        }
        returnMap.put("name", name);
        returnMap.put("x", x);
        returnMap.put("y", y);
        returnMap.put("orientation", orientation);
        return returnMap;
    }

    /**
     * Takes an input string of the form:
     * absorber name=NAME x=INTEGER y=INTEGER width=INTEGER height=INTEGER
     * and generates a map where each key is the parameter name and each value is a String 
     * that represents the corresponding value.
     * @param line String of the form: absorber name=NAME x=INTEGER y=INTEGER width=INTEGER height=INTEGER
     * @return A Map containing keys "name", "x", "y", "width", "height" and their corresponding values as Strings
     */
    Map<String, String> absorberFromString(String line) {
        String[] splitLine = line.split("[ ]*=[ ]*|[ ]+");
        List<Integer> validLengths = Arrays.asList(9, 11);
        String name="";
        Map<String, String> returnMap = new HashMap<String, String>();
        String x;
        String y;
        String width;
        String height;
        if (!validLengths.contains(splitLine.length))
            throw new RuntimeException("Incorrect File Formatting");
        if (!splitLine[0].equals("absorber"))
            throw new RuntimeException("Incorrect File Formatting");
        if (splitLine.length==11) {
            if (!splitLine[1].equals("name") || !splitLine[3].equals("x")
                    || !splitLine[5].equals("y")
                    || !splitLine[7].equals("width")
                    || !splitLine[9].equals("height"))
                throw new RuntimeException("Incorrect File Formatting");
            name = splitLine[2];
            x = splitLine[4];
            y = splitLine[6];
            width = splitLine[8];
            height = splitLine[10];
        }
        else {
            if (!splitLine[1].equals("x")
                    || !splitLine[3].equals("y")
                    || !splitLine[5].equals("width")
                    || !splitLine[7].equals("height"))
                throw new RuntimeException("Incorrect File Formatting");
            x = splitLine[2];
            y = splitLine[4];
            width = splitLine[6];
            height = splitLine[8];
        }
        returnMap.put("name", name);
        returnMap.put("x", x);
        returnMap.put("y", y);
        returnMap.put("width", width);
        returnMap.put("height", height);
        return returnMap;
    }

    /**
     * Takes an input string of the form:
     * fire trigger=NAME action=NAME
     * and generates a map where each key is the parameter name and each value is a String 
     * that represents the corresponding value.
     * @param line String of the form: fire trigger=NAME action=NAME
     * @return A Map containing keys "trigger", "action" and their corresponding values as Strings
     */
    Map<String, String> fireFromString(String line) {
        String[] splitLine = line.split("[ ]*=[ ]*|[ ]+");
        List<Integer> validLengths = Arrays.asList(5);
        Map<String, String> returnMap = new HashMap<String, String>();
        String trigger;
        String action;
        if (!validLengths.contains(splitLine.length))
            throw new RuntimeException("Incorrect File Formatting");
        if (!splitLine[0].equals("fire"))
            throw new RuntimeException("Incorrect File Formatting");
        if (!splitLine[1].equals("trigger") || !splitLine[3].equals("action"))
            throw new RuntimeException("Incorrect File Formatting");
        trigger = splitLine[2];
        action = splitLine[4];
        returnMap.put("trigger", trigger);
        returnMap.put("action", action);
        return returnMap;
    }

    /**
     * Takes an input string of the form:
     * portal name=NAME x=INTEGER y=INTEGER otherBoard=NAME otherPortal=NAME
     * and generates a map where each key is the parameter name and each value is a String 
     * that represents the corresponding value. otherBoard is optional
     * @param line String of the form: portal name=NAME x=INTEGER y=INTEGER otherBoard=NAME otherPortal=NAME
     * @return A Map containing keys "name", "x", "y", "otherBoard", and "otherPortal" and their 
     * corresponding values as Strings
     */
    Map<String, String> portalFromString(String line) {
        String[] splitLine = line.split("[ ]*=[ ]*|[ ]+");
        List<Integer> validLengths = Arrays.asList(9, 11);
        Map<String, String> returnMap = new HashMap<String, String>();
        String name;
        String x;
        String y;
        String otherBoard = "";
        String otherPortal;
        if (!validLengths.contains(splitLine.length))
            throw new RuntimeException("Incorrect File Formatting");
        if (splitLine.length == 9) {
            if (!splitLine[1].equals("name") || !splitLine[3].equals("x") || !splitLine[5].equals("y") ||
                    !splitLine[7].equals("otherPortal"))
                throw new RuntimeException("Incorrect File Formatting");
            name = splitLine[2];
            x = splitLine[4];
            y = splitLine[6];
            otherPortal = splitLine[8];
        }
        else {
            if (!splitLine[1].equals("name") || !splitLine[3].equals("x") || !splitLine[5].equals("y") ||
                    !splitLine[7].equals("otherBoard") || !splitLine[9].equals("otherPortal"))
                throw new RuntimeException("Incorrect File Formatting");
            name = splitLine[2];
            x = splitLine[4];
            y = splitLine[6];
            otherBoard = splitLine[8];
            otherPortal = splitLine[10];
        }
        returnMap.put("name", name);
        returnMap.put("x", x);
        returnMap.put("y", y);
        returnMap.put("otherBoard", otherBoard);
        returnMap.put("otherPortal", otherPortal);
        return returnMap;
    }

    public Board(){
    	
    }

    
    /**
     * Takes an input string of the form:
     * keyup key=KEY action=NAME
     * and generates a map where each key is the parameter name and each value is a String 
     * that represents the corresponding value. otherBoard is optional
     * @param line String of the form: portal name=NAME x=INTEGER y=INTEGER otherBoard=NAME otherPortal=NAME
     * @return A Map containing keys "name", "x", "y", "otherBoard", and "otherPortal" and their 
     * corresponding values as Strings
     */
    Map<String, String> keyActionFromString(String line) {
        String[] splitLine = line.split("[ ]*=[ ]*|[ ]+");
        List<Integer> validLengths = Arrays.asList(5);
        Map<String, String> returnMap = new HashMap<String, String>();
        String key;
        String action;
        if (!validLengths.contains(splitLine.length))
            throw new RuntimeException("Incorrect File Formatting");
    
        if (!(splitLine[0].equals("keyup") || splitLine[0].equals("keydown")) || !splitLine[1].equals("key")
                || !splitLine[3].equals("action"))
            throw new RuntimeException("Incorrect File Formatting");
        key = splitLine[2];
        action = splitLine[4];
    
        returnMap.put("key", key);
        returnMap.put("action", action);
        return returnMap;
    }    
    


    /**
     * @return the board's name
     */
    public String getBoardName() {
        return boardName;
    }
    public void setBoardName(String name){
    	this.boardName=name;
    }

    private void checkRep() {
        for (Gadget g: standardGadgets) {
            double x = g.getPosition().x();
            double y = g.getPosition().y();
            assert(x>=0 && x<=19 && y>=0 && y<=19);
        }
        assert(mu>=0 && mu2>=0);
        assert(updateFrequency>0);
    }
    /**
     * 
     * @param g2 draws the board on g2.
     */
	public void drawBoard(Graphics2D g2) {
		final int textSize=12;
		final int edgeBuffer=25;
		//Rectangle boardEdges= new Rectangle(0,0,400,400);
		g2.setColor(Color.BLACK);
		
		g2.fillRect(0,0,this.boardWitdth*ClientFrame.L,this.boardWitdth*ClientFrame.L);
		
		g2.setColor(Color.RED);
		g2.draw(new Rectangle(0, 0 ,this.boardWitdth*ClientFrame.L, this.boardWitdth*ClientFrame.L));
		g2.setColor(Color.WHITE);
		String topWall=directionBoard.get(direction.top);
		String bottomWall= directionBoard.get(direction.bottom);
		String leftWall= directionBoard.get(direction.left);
		String rightWall= directionBoard.get(direction.right);
		if (topWall!=null){
		g2.drawString(topWall,edgeBuffer , textSize);}
		if (bottomWall!=null){
		g2.drawString(directionBoard.get(direction.bottom),edgeBuffer, this.boardWitdth*ClientFrame.L);}
		if(leftWall!=null){
			for (int i=0; i<leftWall.length();i++){
				g2.drawString(Character.toString(leftWall.charAt(i)),0,edgeBuffer+textSize*i);
			}
		}
		if(rightWall!=null){
			for (int i=0; i<rightWall.length();i++){
				g2.drawString(Character.toString(rightWall.charAt(i)),this.boardWitdth*ClientFrame.L-textSize,edgeBuffer+textSize*i);
			}
		}
		
		for (Gadget g: standardGadgets){
			
			g2.setColor(Color.WHITE);
			g.drawShape(g2);
		}
		for (Ball ball: ballGadgets){
			g2.setColor(Color.RED);
			
			ball.drawShape(g2);
		}
	}
	
	/**
	 * pauses the board so that the gameboard does not update
	 */
	public void pause(){
	    isPaused = true;
	}
	
	/**
     * unpauses the board so that the gameboard does update
     */
	public void unPause(){
        isPaused = false;
    }

	/**
	 * determine whether the board is in its paused state or not
	 * @return boolean representing the state of the board
	 */
	public boolean isPaused(){
	    return isPaused;
	}
	/**
	 * removes balls from board
	 */
	public void clearBalls(){
	    this.ballGadgets.clear();
	}
}
