package clientserver;

public class runner {

	public static void main(String[] args) {
		
		HeadServer hServer;
		System.out.println("Setting up!");
		hServer = new HeadServer();
		
		System.out.println("Running!");
		int c = 0;
		while(c<10){
			hServer.requestTali();
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			c++;
		}
		
		

	}

}
