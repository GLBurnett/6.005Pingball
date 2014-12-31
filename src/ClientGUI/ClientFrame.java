package ClientGUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import pingBall.Board;
import pingBall.Pingball;
/**
 * 
 * the client frame is the main content fram for a client side of a game of pingball
 *
 */
public class ClientFrame extends JFrame{
	public static final int L = 20; //the unit of distance in pixels (the multiplier)
	private static final long serialVersionUID = 1L;
	private Pingball pingball;
	
	private JPanel mainPanel= new JPanel();	
	
	private JButton loadBoardButton;
	private JButton pauseButton;
	private JButton connectButton;
	private JButton restartButton;
	private JTextField hostField;
	private JTextField portField;
	private File file;
	private GamePanel gamePanel;
	private Board board= new Board();
	
	private PauseGame pause;
	private Restart restart;
	private KeyAction keyAct;
	private LoadListen listenLoad;
	/**
	 * Makes the main frame for the pingball client.
	 * The frame contaisn a grouplayout in which
	 * there is a panel responsible for displaying the game.
	 * a button to connect/ disconnect
	 * a button to pause/play
	 * a buton to restart the game
	 * a keyListener to control the game
	 * two textFields to enter the host and the port.
	 * 
	 */
	public ClientFrame(Pingball pingball){
		this.pingball=pingball;
		this.setPreferredSize(new Dimension(550,550));//Magic Numbers are set to make board look best. they are there for aesthetic purposes.
		this.setFocusable( true );
		keyAct= new KeyAction(board);
		addKeyListener(keyAct);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		gamePanel= new GamePanel();		
		connectButton= new JButton();
		if (pingball.isConnected()){
			connectButton.setText("Disconnect");
		}
		else{
			connectButton.setText("Connect");
		}
		pauseButton= new JButton("Pause");
		restartButton= new JButton("Restart");		
		restart= new Restart(this,file);
		pause= new PauseGame(board,pauseButton,this);
		restartButton.addActionListener(restart);
		pauseButton.addActionListener(pause);
		hostField= new JTextField("localhost");
		portField= new JTextField("10987");
		connectButton.addActionListener(new Connect(pingball,hostField,portField,connectButton,this));
		listenLoad= new LoadListen(this);
		loadBoardButton= new JButton("Load Board");
		loadBoardButton.addActionListener(listenLoad);		
		GroupLayout layout= new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup().addComponent(loadBoardButton).addComponent(connectButton).addComponent(hostField).addComponent(portField))
				.addGroup(layout.createSequentialGroup().addComponent(gamePanel)
						.addGroup(layout.createParallelGroup().addComponent(pauseButton).addComponent(restartButton))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(loadBoardButton).addComponent(connectButton).addComponent(hostField).addComponent(portField))
				.addGroup(layout.createParallelGroup().addComponent(gamePanel)
						.addGroup(layout.createSequentialGroup().addComponent(pauseButton).addComponent(restartButton))));
		this.add(mainPanel);
	}

    /**
     * 
     * @return the gamePanel that this frame contains
     */
	public GamePanel getGamePanel(){
		return this.gamePanel;
	}
	/**
	 * 
	 * @return the board that is shown in the frame
	 */
	public Board getBoard(){
		return board;
	}
	/**
	 * 
	 * @param board the board that the frame should display
	 * @param file the file that the board was created from.
	 */
	public void addBoard(Board board,File file){
		this.board=board;
		this.file=file;
		this.setTitle(board.getBoardName());
		this.gamePanel.addBoard(board);
		pause.replaceBoard(board);
		restart.replaceFile(file);
		keyAct.replaceBoard(board);
		pingball.replaceBoard(board);
	}
}
