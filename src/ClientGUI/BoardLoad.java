package ClientGUI;

import java.io.File;

import javax.swing.*;
/**
 * 
 * 
 *
 */
public class BoardLoad extends JFileChooser {
	private static final long serialVersionUID = 1L;
	private File file;
	/**
	 * 
	 * @param cf the clientframe that the boardload file chooser, chooses a file for.
	 * 
	 */
	public BoardLoad(ClientFrame cf) {
		super.setCurrentDirectory(new File(".\\src\\resources"));
		System.out.println(this.getCurrentDirectory());
		int returnVal = this.showOpenDialog(this);
		if (returnVal == JFileChooser.OPEN_DIALOG) {
			file = this.getSelectedFile();
			cf.setFocusable(true);
			cf.setFocusableWindowState(true);
			this.setVisible(false);
			cf.pack();
			System.out.println("REPAINT");
			cf.setFocusable(true);
			cf.requestFocusInWindow();
		} else if (returnVal == JFileChooser.CANCEL_OPTION) {
			this.setVisible(false);
			cf.setFocusableWindowState(true);
			cf.setFocusable(true);
		}
	}
	/**
	 * 
	 * @return the file selected by user in the dialog
	 * 
	 */
	public File getFile() {
		return file;
	}
}
