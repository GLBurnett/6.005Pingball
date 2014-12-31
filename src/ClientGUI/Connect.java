package ClientGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JTextField;

import pingBall.Pingball;
/**
 * Connect is an action listener responsible for connecting the board and client to the server.
 * @author mashk_000
 *
 */
public class Connect implements ActionListener{
	private Pingball pingball;
	private JTextField hostField;
	private JTextField portField;
	private String host;
	private String port;
	private JButton cnct;
	private ClientFrame cf;
	/**
	 * 
	 * @param pb the pingball client instance that is connecting to the server. 
	 * @param host the host field that contains the host to connect to
	 * @param port the port field that contains the port number to connect to
	 * @param cnct the connection button that was pressed
	 * @param cf the client frame that displays all the boards
	 */
	public Connect(Pingball pb, JTextField host, JTextField port,JButton cnct, ClientFrame cf){
		this.pingball=pb;
		this.hostField=host;
		this.portField=port;
		this.cnct=cnct;
		this.cf=cf;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		host= hostField.getText();
		port= portField.getText();
		if (!pingball.isConnected()){
			if (port.matches("[0-9]+")){//Regex matches to any integer.
				int portInt= Integer.parseInt(port);
				if (portInt>=0){
					try {
						pingball.startConnectionHandlerThreads(host, portInt);
						this.cnct.setText("Disconnect");
					} catch (UnknownHostException e1) {

						e1.printStackTrace();
					} catch (IOException e1) {

						e1.printStackTrace();
					}}
			}
		}
		else{
			pingball.stopConnectionHandlerThreads();
			this.cnct.setText("Connect");
		}
		cf.requestFocusInWindow();
	}
}
