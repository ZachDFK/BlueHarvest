package clientserver;

public class runner {

	public static void main(String[] args) {
		
		HeadServer hServer;
		hServer = new HeadServer();
		
		System.out.println("Head server Running!\n");
		int c = 0;
		while(c<10){
			hServer.requestTali();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			c++;
		}
		
		

	}

}
