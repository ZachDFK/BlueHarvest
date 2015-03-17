package clientserver;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;



public class HeadServer {
	
	
	private ArrayList<DistrictServer> districts = new ArrayList<DistrictServer>();
	
	private DatagramSocket thisServer;
	
	public HeadServer() {
		
		
		try {
			thisServer = new DatagramSocket(8001);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		for(int c = 0;c<3;c++){
			districts.add(new DistrictServer(thisServer,c+1));
			
		}
		
		Thread titing;
		for(DistrictServer d:districts){
			titing = new Thread(d);
			titing.start();
		}
		
		
	}
	
	public  void requestTali(){
		Thread titing;
		for(DistrictServer d:districts){
			titing = new Thread(d);
			titing.interrupt();
			try {
				d.requestTali();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			titing.start();
		}
		
	}
	
	
}
