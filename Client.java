import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame{
	private JTextField enterField; //imputs message from user
	private JTextArea displayArea;
	private ObjectOutputStream output; //output stream to client
	private ObjectInputStream input;
	private String message = "";
	private String chatServer;
	private Socket client;


	public Client(String host){
		super("Client");

		chatServer = host; //Sets the server the client is connecting to.

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

	public void runClient(){
		try{
			connectToServer();
			getStreams();
			processConnection();
		}catch(EOFException eofException){
			displayMessage("\nClient terminated connection");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			closeConnection();
		}
	}

	private void connectToServer() throws IOException{
		displayMessage("Attempting to connect to Server\n");

		client = new Socket(InetAddress.getByName(chatServer), 1900);
		displayMessage("Sucessfully connected to: "+client.getInetAddress().getHostName());
	}

	private void getStreams() throws IOException{
		output = new ObjectOutputStream(client.getOutputStream());
		output.flush();

		input = new ObjectInputStream(client.getInputStream());
		displayMessage("\nGot I/O streams\n");
	}

	private void processConnection() throws IOException{
		setTextFieldEditable(true);

		do{
			try{
				message = (String) input.readObject();
				displayMessage("\n "+message);
			}catch(ClassNotFoundException classNotFoundException){
				displayMessage("\n Unknown message format. Please use basic String text\n");
			}
		}while(!message.equals("SERVER>>> TERMINATE"));
	}

	private void closeConnection(){
		displayMessage("\nTerminating connection\n");
		setTextFieldEditable(false);

		try{
			output.close();
			input.close();
			client.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	//Sending a message to the client.
	private void sendData(String message){
		try{
			output.writeObject("CLIENT>>> "+message);
			output.flush();
			displayMessage("\nCLIENT>>> "+message);
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
			});
	}

	//manipulates enterfield.
	public void setTextFieldEditable(final boolean editable){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					enterField.setEditable(editable);
				}
			});
	}

}