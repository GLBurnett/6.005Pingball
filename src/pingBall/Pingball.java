package pingBall;

import physics.*;
import pingBall.Board.direction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import ClientGUI.ClientFrame;

public class Pingball {
    //AF: 
    //  Represents a PingBall board game that can operate in either Single-Machine or
    //  CLient-Server mode. 
    //RI:
    //  Once a board is initialized, it should not be changed to a different board.
    //  portalSendBacks should be cleared at the end of every generateRequest() call
    //Thread Safety Argument:
    //  There are three separate threads running in Pingball.java.
    //  There are two mutable fields in Pingball.java
    //  Any method/place where the threads mutate board or portalSendBacks
    //  are synchronized on this Pingball object. In the few places where
    //  other methods are using observer methods of board, they are only
    //  observing immutable fields of board (updateFrequency and boardName).
    private Board board;
    private static final int DEFAULT_PORT = 10987;
    private static final int MAXIMUM_PORT = 65535;
    private static List<String> portalSendBacks = new ArrayList<String>();
    private Socket serverSocket;
    private boolean isConnected = false;

    /**
     * Starts a pingball client with the given arguments.
     * Usage: Pingball [--host HOST] [--port PORT] --file FILE
     * 
     * HOST is an optional hostname or IP address of the server to connect to. 
     * If no HOST is provided, then the client starts in single-machine play mode, as described above.
     * 
     * PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port where the 
     * server is listening for incoming connections. The default port is 10987.
     * 
     * if HOST is provided, FILE is a required argument specifying a file pathname of the Pingball board that this client should run.
     * if HOST is not provided, FILE is an optional argument specifying a file pathname of the Pingball board 
     * that this client will run in Single-Machine mode. If file is not provided, a default board is used.
     * 
     * @param args arguments as described
     * @throws IOException if FILE is not found
     */
    public static void main(String[] args) throws IOException {
        Pingball pingBall = new Pingball();
        int port = DEFAULT_PORT;
        Optional<File> file = Optional.empty();
        Optional<String> host = Optional.empty();
      
       
        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--host")) {
                        host = Optional.of(arguments.remove());
                    } else if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > MAXIMUM_PORT) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else if (flag.equals("--file")) {
                        file = Optional.of(new File(arguments.remove()));
                        if ( ! file.get().isFile()) {
                            throw new IllegalArgumentException("file not found: \"" + file + "\"");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("Usage: Pingball [--host HOST] [--port PORT] FILE");
            return;
        }
        try {
            if (host.isPresent() && file.isPresent())
                pingBall.startPingballClient(file.get(), host.get(), port);
            else if (!host.isPresent() && file.isPresent())
                pingBall.startPingballClient(file.get());
            else
                pingBall.startPingballClient();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Starts a client-server Pingball game with a board
     * generated from the given file. It is connected to the server
     * specified at hostName, at the port given by port.
     * Sends a message to the server of the form:
     * "Ball X Y XVEL YVEL BOARNAME DIRECTION", where X and Y are the positions on the board where a ball goes
     * out of bounds, XVEL and YVEL are its velocities, and BOARDNAME is the name of the board the ball
     * should go into, and DIRECTION is the side of the board that the ball exits
     * @param file
     * @throws IOException if file cannot be found
     * @throws RuntimeException if File is not a valid .pb file
     */
    public void startPingballClient(File file, String hostName, int port) throws IOException {
    	this.isConnected=true;
        board = new Board(file);
        ClientFrame cf= new ClientFrame(this);
        cf.addBoard(board,file);
        cf.pack();
        cf.setVisible(true);
        startConnectionHandlerThreads(hostName, port);
        while(true) {
            try{
                synchronized (this) {
                	cf.getBoard().update();
                    cf.repaint();
                    
                }
                Thread.sleep(cf.getBoard().getUpdateFrequency());
            }
            catch (InterruptedException e) {
                break;
            }
        }

    }
    
    /**
     * begins the threads for handling connections to other boards
     * @param hostName of server to connect to
     * @param port of the server to connect to
     * @throws UnknownHostException if host doesn't exist
     * @throws IOException if socket is closed unexpectedly
     */
    public void startConnectionHandlerThreads(String hostName, int port) throws UnknownHostException, IOException{
        System.out.println(hostName);
        System.out.println(port);
        serverSocket= new Socket(hostName, port);
        
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        out.println("board "+board.getBoardName()); //Initial Message
        
        isConnected = true;
        
        Thread senderThread = new Thread(new Runnable() {
            public void run(){
                try {
                    while (isConnected){
                        List<String> myRequests = generateRequest();
                        for(String request: myRequests) {
                            System.out.println(request);
                            out.println(request);
                        }
                    }
                } finally {
                    out.close();
                }

            }
        });
        senderThread.start();
        
        Thread recieverThread = new Thread(new Runnable() {
            public void run(){
                try {
                    for(String line = in.readLine(); line != null; line = in.readLine()) {
                        handleRequest(line);
                    }

                } catch (IOException e) {
                    isConnected = false;
                    // If we close the socket unexpectedly
                    e.printStackTrace();
                } finally {
                    try {
                        isConnected = false;
                        in.close();
                    } catch (IOException e) {
                        //if unable to close socket (ie. we closed it externally)
                        e.printStackTrace();
                    }
                }
            }
        });
        recieverThread.start();
    }
    
    /**
     * disconnects from server by closing the socket and sending disconnect messages
     * to the board
     */
    public void stopConnectionHandlerThreads() {
        try {
            serverSocket.close();
            board.removeConnection(direction.bottom);
            board.removeConnection(direction.top);
            board.removeConnection(direction.left);
            board.removeConnection(direction.right);
            isConnected = false;
            board.clearBalls();
        } catch (IOException e) {
            //if socket is already closed
            e.printStackTrace();
        }
    }

    /**
     * Starts a single-machine Pingball game with a board
     * generated from the given file
     * @param file
     * @throws IOException if file cannot be found
     * @throws RuntimeException if File is not a valid .pb file
     */
    public void startPingballClient(File file) throws IOException {
        board = new Board(file);
        ClientFrame cf= new ClientFrame(this);
        cf.addBoard(board,file);
        cf.pack();
        cf.setVisible(true); 
        while(true) {
            try{
                board.update();
                cf.getBoard().update();
                cf.repaint();
                Thread.sleep(cf.getBoard().getUpdateFrequency());
            }
            catch (InterruptedException e) {
            }
        }
    }

    /**
     * Starts a single-machine Pingball game with default board
     */
    public void startPingballClient() {

    	File file= new File("src/resources/default.pb");
    	System.out.println(file);
    	
        try {
			board = new Board(file);
			board.print();
        ClientFrame cf= new ClientFrame(this);
        cf.addBoard(board,file);
        cf.pack();
        cf.setVisible(true);
        while(true) {
            try{
                cf.getBoard().update();
                cf.repaint();
                Thread.sleep(cf.getBoard().getUpdateFrequency());
            }
            catch (InterruptedException e) {
            }
        }
        } catch (IOException e1) {
			e1.printStackTrace();
		}
    }

    /**
     * 
     * Parses and handles messages sent from the server.
     * Possible messages:
     * "connect DIRECTION BOARDNAME"
     *      Means the board with BOARDNAME is connected to this.board's DIRECTION side
     * "disconnect DIRECTION"
     *      Means that the board on this.board's DIRECTION side has disconnected
     * "ball X' Y' XVEL YVEL"
     *      Means that this.board should create a new ball with position (X', Y') and velocity (XVEL, YVEL)
     * "portal OTHERBOARD OTHERPORTAL THISBOARD THISPORTAL XVEL YVEL"
     *      Means a ball was sent from portal OTHERPORTAL on board OTHERBOARD to portal THISPORTAL on this board
     *      with velocity (XVEL, YVEL)
     * DIRECTION should be top, bottom, left, or right
     * THISBOARD should be board.getBoardName()
     * @param request the message sent by the server to handle
     */
    public synchronized void handleRequest(String request) {
        
        String[] splitLine = request.split(" ");
        if (splitLine[0].equals("connect")) {
            board.addConnection(Board.direction.valueOf(splitLine[1]), splitLine[2]);
        }
        else if (splitLine[0].equals("disconnect")) {
            board.removeConnection(Board.direction.valueOf(splitLine[1]));
        }
        else if (splitLine[0].equals("ball")) {
            double xPos = Double.parseDouble(splitLine[1]);
            double yPos = Double.parseDouble(splitLine[2]);
            double xVel = Double.parseDouble(splitLine[3]);
            double yVel = Double.parseDouble(splitLine[4]);
            board.addBall(new Ball(xPos, yPos, new Vect(xVel,yVel)));
        }
        else if (splitLine[0].equals("portal")) {
            String otherBoard = splitLine[1];
            String otherPortal = splitLine[2];
            String thisBoard = splitLine[3];
            String thisPortal = splitLine[4];
            double xVel = Double.parseDouble(splitLine[5]);
            double yVel = Double.parseDouble(splitLine[6]);
            boolean portalExists = board.ballExitingPortal(thisPortal, new Vect(xVel,yVel));
            if (!portalExists) {
                portalSendBacks.add("portal "+thisBoard+" "+thisPortal+" "+otherBoard+" "+
                        otherPortal+" "+xVel+" "+yVel);
            }
        }
        else
            throw new IllegalArgumentException("unable to parse request");
    }

    /**
     * Generates a list of messages of the form 
     * "ball X Y XVEL YVEL BOARDNAME DIRECTION"
     *  where X and Y are the position of a ball traveling to a different board
     *  XVEL and YVEL specify its velocity, BOARDNAME specified which board it's
     *  traveling into, and DIRECTION specifies the direction of the outerwall it is
     *  traveling out of.
     *  DIRECTION will be top, bottom, left, or right
     *  or of the form
     *  "portal THISBOARD THISPORTAL OTHERBOARD OTHERPORTAL XVEL YVEL"
     *  where a ball is traveling through THISPORTAL in THISBOARD to OTHERPORTAL in OTHERBOARD
     *  with velocity (XVEL,YVEL)
     * @return A list of messages of the form specified above
     */
    public synchronized List<String> generateRequest() {
        Map<Ball, direction> ballToDifferentBoardMap = board.ballsTravelingToDifferentBoardsThroughWalls();
        Map<Ball, Portal> ballThroughPortalMap = board.ballsLeavingThroughPortals();
        List<String> messagesToSend = new ArrayList<String>();
        Set<Ball> ballsToDifferentBoard = ballToDifferentBoardMap.keySet();
        for (Ball ball:ballsToDifferentBoard) {
            double x = ball.getPosition().x();
            double y = ball.getPosition().y();
            double xVel = ball.getVelocity().x();
            double yVel = ball.getVelocity().y();
            String boardName = board.getConnection(ballToDifferentBoardMap.get(ball));
            String direction = ballToDifferentBoardMap.get(ball).toString();
            messagesToSend.add("ball "+x+" "+y+" "+xVel+" "+yVel+" "+boardName+" "+direction);
        }
        Set<Ball> ballsThroughPortals = ballThroughPortalMap.keySet();
        for (Ball ball: ballsThroughPortals) {
            String thisBoard = board.getBoardName();
            Portal thisPortal = ballThroughPortalMap.get(ball);
            String otherPortal = thisPortal.getOtherPortalName();
            String otherBoard = thisPortal.getOtherBoardName();
            double xVel = ball.getVelocity().x();
            double yVel = ball.getVelocity().y();
            messagesToSend.add("portal "+thisBoard+" "+thisPortal.getName()+" "+otherBoard+" "+
                    otherPortal+" "+xVel+" "+yVel);
        }
        for (String message: portalSendBacks) {
            messagesToSend.add(message);
        }
        portalSendBacks.clear();
        checkRep();
        return messagesToSend;
    }
    
    /**
     * whether this pingball client is connected to a sever
     * @return boolean representing whether this pingball client is connected to a sever 
     */
    public boolean isConnected(){
        return isConnected;
    }
    
    /**
     * ensures rep invariants hold true
     */
    private void checkRep() {
        assert(portalSendBacks.isEmpty());
    }
    
    /**
     * replaces the board used by this pingBall client
     * @param board to use as replacement
     */
    public void replaceBoard(Board board){
        this.board = board;
    }

}
