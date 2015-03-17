package MVC;
import java.util.Scanner;

import clientserver.*;
public class ManualVoter {
	
	private DistrictServer district;
	private Scanner scan;
	public ManualVoter(DistrictServer district){
		this.district = district;
		this.scan = new Scanner(System.in);
		
	}
	
	
	public void consoleRun(){
		int pollingNumber;
		System.out.println("Weclome to manual mode."
				+ "/nEnter the polling station you belong to:");
		
		pollingNumber = scan.nextInt();
		voteInPoll(district.getStation(pollingNumber-1));
	}
	
	public void voteInPoll(PollingStationClient polled){
		System.out.println("Vote for the canditate:");
		int inc =0;
		for(String name: polled.printCandidates()){
			System.out.println( ++inc + "-" + name);
		}
		int canNum = scan.nextInt();
		String canN = polled.printCandidates()[canNum -1];
		
		polled.voteFor(canN);
		
	}
}
