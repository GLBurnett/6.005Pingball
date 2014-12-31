package ClientGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import pingBall.Board;
/**
 * Action that loads a board onto the client frame 
 * @author mashk_000
 *
 */
public class LoadListen implements ActionListener{
	private ClientFrame cf;
	private BoardLoad fc;
	private File file;
	private Board board;
	/**
	 * 
	 * @param cf the client frame that the file should be loaded to.
	 */
	public LoadListen(ClientFrame cf){
		this.cf=cf;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		fc= new BoardLoad(cf);				
		file=fc.getFile();
	
			if (file!= null){
			try {
				board= new Board(file);
						
				cf.addBoard(board,file);
			cf.setFocusable(true);
			}
			 catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}}		
	}
	
}
