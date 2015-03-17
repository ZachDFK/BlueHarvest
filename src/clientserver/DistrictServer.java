package clientserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class DistrictServer implements Runnable {
	
	
	private int id;
	private ArrayList<Candidate> candidates = new ArrayList<Candidate>();
	
	private static final int MAX_CLIENT = 1;
	private ArrayList<PollingStationClient> stationClients = new ArrayList<PollingStationClient>();
	private final Semaphore available = new Semaphore(MAX_CLIENT,true);
	
	private DatagramSocket serverSocket;
	private InetAddress headServerAddress;
	private DatagramSocket headServerSocket;
	private byte[] sendData;
	private DatagramPacket sendPacket;	
	
	private DatagramPacket receivePacket;
	private byte[] receiveData;
	
	private String receivedTaliString;
	
	
	public  DistrictServer(DatagramSocket server,int id){
		this.id = id;
		this.headServerSocket = server;
		try {
			instiateSockets();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(int c =0;c<10;c++){
			stationClients.add(new PollingStationClient(this,this.serverSocket));
		}
		
		
		
		loadCanditates();
		
	}
	public void loadCanditates(){
		
	}
	
	
	public void sendPacket(String dataToSend){
		sendData = new byte[1024];
		sendData = dataToSend.getBytes();
		sendPacket = new DatagramPacket(sendData,sendData.length,headServerAddress,headServerSocket.getPort());
		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void instiateSockets() throws Exception {
		serverSocket = new DatagramSocket();
		headServerAddress = InetAddress.getByName("localhost");
		
	}
	
	public void run(){
		boolean on = true;
		receiveData = new byte[1024];
		while(on){
			receivePacketing();
		}
	}
	public void receivePacketing(){
		
		try {
			available.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String stringP = new String(receivePacket.getData(),0,receivePacket.getLength());
		taliAddition(stringP);
		
		available.release();
	}
	
	public void taliAddition(String passedS){
		
		for(Candidate c:candidates){
			if(c.getName().equals(passedS)){
				c.setVoteTali(c.getVoteTali() + 1);
			}
		}
	}
	
	
	/**
	 * get the tali of all clients listening
	 * @throws InterruptedException 
	 * 
	 */
	public void requestTali() throws InterruptedException{
		
		 System.out.println("tali oh!---ID:" + id);
		
	}
	
	public PollingStationClient getStation(int index){
		
		return stationClients.get(index);
	}
	public String[] getCandidates() {
		String[] names = null;
		int inc = 0;
		for(Candidate c: candidates){
			names[inc] = c.getName();
			inc++;
		}
		
		return names;
	}
	
}
