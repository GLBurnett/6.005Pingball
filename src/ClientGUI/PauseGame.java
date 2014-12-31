package ClientGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import pingBall.Board;

/**
 * 
 * @author mashk_000
 *
 */
public class PauseGame implements ActionListener {
	Board board;
	JButton pause;
	ClientFrame cf;
	/**
	 * 
	 * @param board the board that the pause button is responsible for.
	 * @param pauseB the button that was clicked.
	 * @param cf the clientFrame that is responsible for displaying the board
	 */
	public PauseGame(Board board,JButton pauseB,ClientFrame cf){
		this.board= board;
		pause=pauseB;
		this.cf=cf;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (!board.isPaused()){
			this.board.pause();
			this.pause.setText("Play");}
		else{
			this.board.unPause();
			this.pause.setText("Pause");
		}
		cf.requestFocusInWindow();
		
	}
	/**
	 * If a new board was loaded in, then the board is replaced here.
	 * @param board2
	 */
	public void replaceBoard(Board board2) {
		board=board2;
		if (!board.isPaused()){
			this.pause.setText("Pause");
		
		}
		else{
			this.pause.setText("Play");
		}
		
	}

}
