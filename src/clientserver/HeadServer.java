package clientserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;



public class HeadServer {
	
	
	private ArrayList<DistrictServer> districts = new ArrayList<DistrictServer>();
	private DatagramSocket thisServer;
	private Semaphore available = new Semaphore(1);
	private DatagramPacket receivePacket;
	private byte[] receiveData = new byte[1024];
	private ArrayList<Thread> deepThreads = new ArrayList<Thread>();
	public HeadServer() {
		
		
		try {
			thisServer = new DatagramSocket(1111);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		for(int c = 0;c<1;c++){
			districts.add(new DistrictServer(thisServer,c+1));
			
		}
		
		Thread titing;
		for(DistrictServer d:districts){
			titing = new Thread(d);
			deepThreads.add(titing);
			titing.start();
		}
		
		
	}
	
	public  void requestTali(){
		
		Thread titing;
		
		for(Thread t:deepThreads){
			
		
			//t.interrupt();
			try {
				
				districts.get(deepThreads.indexOf(t)).requestTali();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//t.run();
			receivePacketing();
		}
		
	}
	public void receivePacketing(){
  for(int i =0; i<districts.size(); i++){
	  
		try {
			available.acquire();
		} catch (InterruptedException e1) {
		//	 TODO Auto-generated catch block
			e1.printStackTrace();
		}
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			thisServer.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String stringP = new String(receivePacket.getData(),0,receivePacket.getLength());
		System.out.println(stringP);
		
		available.release();
	}
	}
	
	
	
}
