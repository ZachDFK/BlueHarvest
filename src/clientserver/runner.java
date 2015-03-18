package clientserver;

import java.util.Scanner;

public class runner {
	private static Scanner scan;
	private static int choice;
	public static void main(String[] args) {
		scan = new Scanner(System.in);
		System.out.println("Enter 1 for Manual Test or 2 for Automatic Test");
		HeadServer hServer;
		
		
		choice = scan.nextInt();
		if(choice == 1){
			System.out.println("Manual Test");
		}
		else {
			System.out.println("Automatic Test");
		}
		hServer = new HeadServer(choice);
		int c = 0;
		while(c<10){
			hServer.requestTali();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			c++;
		}
	}

}
