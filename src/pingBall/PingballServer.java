package pingBall;

import java.io.*;
import java.net.*;
import java.util.*;


public class PingballServer {
    /**
     * Pingball Server, where boards are ping ball clients.
     * Clients send messages to the server which consist of wall ball passing messages
     * or portal ball passing messages. The server returns messages to the client 
     * consisting of ball passing messages, portal passing messages, connect 
     * or disconnect messages
     */
    
    /**
     * Thread safety argument:
     * 
     * Only two fields are shared between arguments: boardSockets and boardMappings
     * Both of these have synchronized wrappers thrown around them.
     * Additionally, every time an observer is used before using the shared field
     * (ie. checking to see if a socket exists before writing to it), the block of
     * code using that field is synchronized on that field.
     */
    
    /** Default server port. */
    private static final int DEFAULT_PORT = 10987;
    /** Maximum port number as defined by ServerSocket. */
    private static final int MAXIMUM_PORT = 65535;
    
    /**
     * Represents the sockets of the currently connected boards
     * RI: Any board name can appear in the keys only once, and
     * each socket can only be present in the values once
     * AF: The keys are the strings corresponding to the Board Name of the board,
     * and the value is a socket that connects the server to a pingball 
     * client of that board
     */
    private Map<String,Socket> boardSockets = Collections.synchronizedMap(new HashMap<String,Socket>());
    
    /** Socket for receiving incoming connections. */
    private final ServerSocket serverSocket;
    
    /**
     * AF: Represents present boards and which boards are connected to 
     * each side of the board. The keys of this map are the active boards
     * on this server, and the values are maps where they keys are 
     * directions, and the values are boards connected to the first board 
     * in that direction.
     * RI: All keys must exist in boardSockets, and the maps of the values 
     * must contain four or less values (one for each direction). 
     * ie: <board1, <left, board2>> is connected as:
     * board2 <-> board1
     */
    private Map<String,Map<Direction,String>> boardMappings = Collections.synchronizedMap(new HashMap<String,Map<Direction,String>>());
    
    /** Enum describing the four possible OuterWall positions */
    private enum Direction{
        top,
        bottom, 
        left, 
        right;
        @Override
        public String toString(){
            if(this.equals(Direction.top)){
                return "top";
            }
            else if(this.equals(Direction.bottom)){
                return "bottom";
            }
            else if(this.equals(Direction.left)){
                return "left";
            }
            else {
                return "right";
            }
        }
        /**
         * Gives opposite direction
         * @return the opposite Direction of this
         */
        public Direction opposite(){
            if(this.equals(Direction.top)){
                return Direction.bottom;
            }
            else if(this.equals(Direction.bottom)){
                return Direction.top;
            }
            else if(this.equals(Direction.left)){
                return Direction.right;
            }
            else {
                return Direction.left;
            }
        }
    };
    
    /**
     *enum describing the two orientations a joining can take:
     *horizontal: left right
     *vertical: top bottom
     */
    private enum Orientation{horizontal, vertical};
    
    /**
     * Starts a PingballServer with the given command line arguments.
     * The only allowed arguments are --port PORTNUMBER, where 
     * PORTNUMBER is the desired portnumber. Must be less than
     * 65535. If no port number is specified, the the default
     * port number is 10987 
     * 
     * @param args
     */
    public static void main(String[] args) {
        PingballServer pingballServer;
        try {
            Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
            
            if (arguments.size() > 0){
                String flag = arguments.remove();
                
                if (flag.equals("--port")){
                    int portNum =Integer.parseInt(arguments.remove());
                    if (portNum<MAXIMUM_PORT){
                        pingballServer = new PingballServer(portNum);
                    }
                    else{
                        String errorMsg = "Port number too high, must be less than "+ MAXIMUM_PORT+", was "+portNum;
                        System.out.println(errorMsg);
                        throw new UnsupportedOperationException(errorMsg);
                    }
                } else{
                    String errorMsg = "invalid arguments. Only --port allowed. Was: " + flag;
                    System.out.println(errorMsg);
                    throw new UnsupportedOperationException(errorMsg);
                }
                
            } else{
                pingballServer = new PingballServer(DEFAULT_PORT);
            }
            pingballServer.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Make a PingballServer that listens for connections on port.
     * 
     * @param port port number, requires 0 <= port <= 65535
     * @throws IOException if an error occurs opening the server socket
     */
    public PingballServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        
    }
    
    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * Creates necessary threads for setup and as clients clients connect
     * 
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException{
        
        Thread userInputThread = new Thread(new Runnable() {
            public void run() {
                userInput();
            }
        });
        userInputThread.start();
        
        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();
            Thread clientThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        handleConnection(socket);
                    } catch (IOException ioe) {
                        ioe.printStackTrace(); // but don't terminate serve()
                    } finally {
                        try {
                            socket.close();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            clientThread.start();
        }
        
    }
    
    /**
     * Handle a single client connection. Returns when client disconnects.
     * When a PingballClient first connects to the server, it must send
     * "board BOARDNAME" where BOARDNAME is the board's name
     * Subsequent messages must be in the form described in userInput
     * 
     * @param socket socket where the client is connected
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     * 
     * @param socket
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String boardName = "";
        try {
            String firstLine = in.readLine();
            if((firstLine.trim().split(" ")[0].equals("board")) && (firstLine.trim().split(" ").length == 2)){
                boardName = firstLine.trim().split(" ")[1];
                synchronized (boardSockets) {
                    if (boardSockets.containsKey(boardName)) {
                        System.out
                                .println("Sorry, client with same board name already "
                                        + "connected, please use another board name: "
                                        + boardName);
                        return;
                    } else {
                        boardSockets.put(boardName, socket);
                        System.out.println(boardName + " connected");
                    }
                }
                checkRep();
                
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    System.out.println("Ball passing through");
                    handleRequestFromBoardSocket(line);
                    checkRep();
                }
            } else{
                System.out.println("Sorry, first message does not match grammar of \"board BOARDNAME\", was: " + firstLine);
                return;
            }
        } finally {
            if (!boardName.isEmpty()){
                synchronized (boardMappings) {
                    if (boardMappings.containsKey(boardName)) {
                        for (Direction currentDirection : boardMappings.get(
                                boardName).keySet()) {
                            passMessageToBoardSocket(
                                    boardMappings.get(boardName).get(
                                            currentDirection), "disconnect "
                                            + currentDirection.opposite()
                                                    .toString());
                        }
                        boardMappings.remove(boardName);
                    }
                    for (Map<Direction, String> currentMap : boardMappings
                            .values()) {
                        for (Direction currentDirection : Direction.values()) {
                            if (currentMap.containsKey(currentDirection)) {
                                if (currentMap.get(currentDirection).equals(
                                        boardName)) {
                                    currentMap.remove(currentDirection);
                                }
                            }
                        }
                    }
                }
            }
            
            synchronized (boardSockets) {
                if (boardSockets.containsKey(boardName)) {
                    boardSockets.remove(boardName);
                }
            }
            
            in.close();
            checkRep();
        }
        
    }
    
    /**
     * Handles exiting ball messages. The only supported message is of the form:
     * "ball x y xVel yVel Board Direction"
     * Where x,y are the coordinates of the ball when it exited the map,
     * xVel,yVel is the velocity vector of the ball, Board is the name 
     * of the board to deliver to, and Direction is the direction that
     * the ball exited the board
     * 
     * "portal THISBOARD THISPORTAL OTHERBOARD OTHERPORTAL XVEL YVEL"
     * 
     * @param request string that is requested
     */
    private void handleRequestFromBoardSocket(String request){
        String[] splitMessage = request.split(" ");
        if (splitMessage[0].equals("ball")){
            if(splitMessage[6].equals("top")){
                splitMessage[2] = "19.74";
            } else if(splitMessage[6].equals("bottom")){
                splitMessage[2] = "0.26";
            } else if(splitMessage[6].equals("left")){
                splitMessage[1] = "19.74";
            } else if(splitMessage[6].equals("right")){
                splitMessage[1] = "0.26";
            } else{
                System.out.println("Invalid request: " + request);
                return;
            }
            
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 0; i<5;i++){
                messageBuilder.append(splitMessage[i]);
                messageBuilder.append(" ");
            }
            passMessageToBoardSocket(splitMessage[5], messageBuilder.toString().trim());
            
        } else if (splitMessage[0].equals("portal")){
            synchronized (boardSockets) {
                if (boardSockets.containsKey(splitMessage[3])) {
                    passMessageToBoardSocket(splitMessage[3], request);
                } else {
                    StringBuilder messageToPass = new StringBuilder();

                    messageToPass.append("portal ");
                    messageToPass.append(splitMessage[3] + " "
                            + splitMessage[4] + " ");
                    messageToPass.append(splitMessage[1] + " "
                            + splitMessage[2] + " ");
                    messageToPass.append(splitMessage[5] + " "
                            + splitMessage[6] + " ");

                    passMessageToBoardSocket(splitMessage[1],
                            messageToPass.toString());

                }
            }
            
        }
        
    }
    
    /**
     *Passes a message to the socket corresponding to BoardName of the form: "ball X Y XVEL YVEL", 
     *where this represents a new ball entering the board at X,Y with velocity vector XVEL,YVEL
     *Or a disconnect message;
     *Thread safe: Uses a synchronizedMap object
     * @param BoardName board to pass message to
     * @param message to pass to BoardName
     * @throws IOException 
     */
    private void passMessageToBoardSocket(String BoardName, String message){
        Socket currentSocket = boardSockets.get(BoardName);
        try {
            PrintWriter out = new PrintWriter(currentSocket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Blocks waiting for an user to type input on the console, and sends that input to handle request
     * Two boards can be joined side by side by the command: h NAME_left NAME_right
     * Two boards can be joined top to bottom by the command: v NAME_top NAME_bottom
     * 
     * If an existing connection exists and you wish to overwrite, the existing connection is severed.
     * 
     * The messages sent to the board socket are in the form of:
     * -"connect DIRECTION BOARDNAME", to connect BOARDNAME to the first board's DIRECTION wall
     * -"disconnect DIRECTION", to disconnect a connected board on the DIRECTION wall
     * 
     * @return
     */
    private void userInput(){
        while(true){
            String input = "";
            
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
            try {
                input = inputStream.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            };
            
            String[] splitInput = input.split(" ");
            if (!(splitInput.length == 3)){
                System.out.println("Sorry, input not in correct format, must be: h NAME_left NAME_right "+
                        "or v NAME_top NAME_bottom");
                continue;
            }
            
            String BoardA = splitInput[1];
            String BoardB = splitInput[2];
            
            synchronized (boardSockets) {
                if (!(boardSockets.containsKey(BoardA))) {
                    System.out.println("Sorry, board not connected: " + BoardA);
                    continue;
                }
                if (!(boardSockets.containsKey(BoardB))) {
                    System.out.println("Sorry, board not connected: " + BoardB);
                    continue;
                }
            }
            
            if (splitInput[0].equals("h")){
                joinTwoBoards(Orientation.horizontal, BoardA, BoardB);
            } else if (splitInput[0].equals("v")){
                joinTwoBoards(Orientation.vertical, BoardA, BoardB);
            }

            
        }
    }
    
    /**
     * attempts to add two boards to board mappings; if a connection already exists on a side that we are 
     * attempting to connect to, we sever that connection and send a disconnect message to required boards
     * @param orientation either horizontal or vertical, corresponding to the orientation of the joining
     * @param BoardA first board to add
     * @param BoardB second board to add
     */
    private void joinTwoBoards(Orientation orientation, String BoardA, String BoardB){
        synchronized (boardSockets) {
            if (boardSockets.containsKey(BoardA)
                    && boardSockets.containsKey(BoardB)) {
                if (orientation.equals(Orientation.horizontal)) {
                    passMessageToBoardSocket(BoardA, "connect right " + BoardB);
                    addToBoardMappings(Direction.right, BoardA, BoardB);
                    passMessageToBoardSocket(BoardB, "connect left " + BoardA);
                    addToBoardMappings(Direction.left, BoardB, BoardA);
                } else {
                    passMessageToBoardSocket(BoardA, "connect bottom " + BoardB);
                    addToBoardMappings(Direction.bottom, BoardA, BoardB);
                    passMessageToBoardSocket(BoardB, "connect top " + BoardA);
                    addToBoardMappings(Direction.top, BoardB, BoardA);
                }
            }
        }
    }
    
    /**
     * Joins BoardA to BoardB along direction ie: 
     * addToBoardMappings(left, BoardA, BoardB):
     * BoardB <-> BoardA
     * @param direction in which the first is connected to the second
     * @param BoardA first board to add
     * @param BoardB second board to add
     */
    private void addToBoardMappings(Direction direction, String BoardA, String BoardB){
        synchronized (boardMappings) {
            boardMappings
                    .putIfAbsent(
                            BoardA,
                            Collections
                                    .synchronizedMap(new HashMap<PingballServer.Direction, String>()));
            Map<PingballServer.Direction, String> BoardAMap = boardMappings
                    .get(BoardA);
            if (BoardAMap.containsKey(direction)) {
                passMessageToBoardSocket(BoardAMap.get(direction),
                        "disconnect " + direction.toString());
            }
            BoardAMap.put(direction, BoardB);
        }
    }
    
    /**
     * checks to make sure rep invariants hold true
     */
    private void checkRep(){
        int numOfDuplicates = 0;
        
        synchronized (boardSockets) {
            for (Socket currentSocket : boardSockets.values()) {
                for (Socket socket : boardSockets.values()) {
                    if (currentSocket.equals(socket)) {
                        numOfDuplicates++;
                    }
                }
                assert (numOfDuplicates == 1);
                numOfDuplicates = 0;
            }
        }
        
        synchronized (boardMappings) {
            for (String currentBoard : boardMappings.keySet()) {
                assert (boardSockets.containsKey(currentBoard));
            }
            for (Map<Direction, String> currentMap : boardMappings.values()) {
                assert (currentMap.values().size() <= 4);
            }
        }
    }
}