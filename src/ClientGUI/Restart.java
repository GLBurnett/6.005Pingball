package ClientGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import pingBall.Board;
/**
 * Is the action listener responsible when the board should be restarted
 * @author mashk_000
 *
 */
public class Restart implements ActionListener {
	ClientFrame cf;
	File file;
	/**
	 * 
	 * @param cf is the client frame that contains the game that needs to be restarted
	 * @param file the file that the current board was started from.
	 */
	public Restart(ClientFrame cf, File file){
		this.cf=cf;
		this.file=file;
	}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				Board board= new Board(file);
				System.out.println("file");
				board.print();
				cf.addBoard(board,file);
				cf.requestFocusInWindow();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
	}
		/**
		 * 
		 * @param file2 is a file that should replace the old file that the board should be loaded from
		 */
		public void replaceFile(File file2) {
			this.file=file2;
			
		}
		
	
}
