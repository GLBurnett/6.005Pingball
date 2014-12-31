package ClientGUI;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import pingBall.Board;

public class GamePanel extends JPanel {

	/**
	 * The Panel paints the board.
	 */
	private static final long serialVersionUID = 1L;
	private Board board = new Board();
	/**
	 * empty constructor. Draws an empty board with a 20 by 20 rectangle.
	 */
	public GamePanel() {
	}

	@Override
	public void paintComponent(Graphics g) {
		board.drawBoard((Graphics2D) g);
	}

	/**
	 * 
	 * @param board
	 *            adds the board that the Game Panel should display. If a board
	 *            already exists this board replaces the old board.
	 */
	public void addBoard(Board board) {
		this.board = board;

	}

	/**
	 * 
	 * @return the board that the game panel is painting
	 */
	public Board getBoard() {
		return board;
	}
}
