package clientserver;
import java.io.IOException;
import java.net.*;
public abstract class Client {

	private DatagramSocket clientSocket;
	private InetAddress serverAddress;
	private DatagramSocket serverSocket;
	private byte[] sendData;
	private DatagramPacket sendPacket;
	
	public Client(DatagramSocket server){
		this.serverSocket = server;
		try {
			instiateSockets();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendPacket(String dataToSend){
		sendData = new byte[1024];
		sendData = dataToSend.getBytes();
		sendPacket = new DatagramPacket(sendData,sendData.length,serverAddress,1112);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void instiateSockets() throws Exception {
		clientSocket = new DatagramSocket();
		serverAddress = InetAddress.getByName("localhost");
		
	}
	
	public void showResults(){
		
	}
}
