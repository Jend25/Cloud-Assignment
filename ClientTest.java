import javax.swing.JFrame;

public class ClientTest{
	public static void main(String[] args){
		Client application;

		if(args.length == 0){
			application = new Client("127.0.0.1"); //Connect to local host
		}else{
			application = new Client(args[0]); //use ip address given in args
		}

		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		application.runClient();
	}
}