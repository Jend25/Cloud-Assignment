import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame{
	private JTextField enterField; //imputs message from user
	private JTextArea displayArea;
	private ObjectOutputStream output; //output stream to client
	private ObjectInputStream input;
	private ServerSocket server; //server socket
	private Socket connection; //connection to client
	private int counter = 1; //number of connections

	//Setting up simple GUI
	public Server(){
		super("Server");

		enterField = new JTextField();
		enterField.setEditable(false);
		enterField.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendData(event.getActionCommand());
					enterField.setText("");
				}
			}
		);

		add(enterField, BorderLayout.SOUTH);
		displayArea = new JTextArea();
		add(new JScrollPane(displayArea), BorderLayout.CENTER);

		setSize(400,200);
		setVisible(true);
	}

	//set up and run server
	public void runServer(){
		try{
			server = new ServerSocket(1900, 100); //create ServerSocket

			while(true){
				try{
					waitForConnection();
					getStreams();
					processConnection();
				}
				catch(EOFException eofException){ displayMessage("\nServer terminated connection"); }
				
				finally{
					closeConnection();
					++counter;
				}
			}
		}catch(IOException ioException){ ioException.printStackTrace(); }
	}

	private void waitForConnection() throws IOException{
		displayMessage("Waiting for connection\n");
		connection = server.accept();
		displayMessage("Connection "+counter+" recieved from: "+connection.getInetAddress().getHostName());
	}

	//streams to end and recieve data
	private void getStreams() throws IOException{
		output =  new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());

		displayMessage("\nGot I/O streams\n");
	}

	//Process connection
	private void processConnection() throws IOException{
		String message = "Connection Sucessful";
		sendData(message);
		setTextFieldEditable(true);
		//Check message is in string format
		do{
			try{
				message = (String) input.readObject();
				displayMessage("\n"+message);
			}catch(ClassNotFoundException classNotFoundException){
				displayMessage("\n Unknown message format. Please use basic String text\n");
			}
		}while(!message.equals("CLIENT>>> TERMINATE"));
	}

	//close streams and sockets.
	private void closeConnection(){
		displayMessage("\nTerminating connection\n");
		setTextFieldEditable(false);

		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	//Sending a message to the client.
	private void sendData(String message){
		try{
			output.writeObject("SERVER>>> "+message);
			output.flush();
			displayMessage("\nSERVER>>> "+message);
		}catch(IOException ioException){
			displayArea.append("\nError writing object");
		}
	}

	private void displayMessage(final String messageToDisplay){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){ //updates the display area
					displayArea.append(messageToDisplay);
				}
			}
		);
	}

	//manipulates enterfield.
	public void setTextFieldEditable(final boolean editable){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					enterField.setEditable(editable);
				}
			}
		);
	}
}





































