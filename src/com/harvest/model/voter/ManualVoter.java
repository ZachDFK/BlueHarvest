//package com.harvest.model.voter;
//import java.io.FileNotFoundException;
//import java.util.Scanner;
//
//import com.harvest.controller.*;
//import com.harvest.model.district.DistrictServer;
//import com.harvest.model.polling.PollingStationClient;
//public class ManualVoter implements Runnable {
//	
//	private DistrictServerLauncher district;
//	private Scanner scan;
//	private boolean on;
//	public ManualVoter(DistrictServerLauncher district){
//		this.district = district;
//		this.scan = new Scanner(System.in);
//		System.out.println("Weclome to manual mode.\n");
//		on = true;
//	}
//	
//	
//	public void consoleRun(){
//		int pollingNumber;
//		System.out.println("Enter the polling station you belong to( Q to quit and close district):");
//		
//		if(scan.hasNextInt() != true){
//			try {
//				district.end();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			on = false;
//		}
//		else{
//			pollingNumber = scan.nextInt();
//			voteInPoll(district.getStation(pollingNumber-1));
//		}		
//	}
//	
//	public void voteInPoll(PollingStationClient polled){
//		System.out.println("Vote for the canditate:");
//		int inc =0;
//		for(String name: polled.printCandidates()){
//			System.out.println( ++inc + "-" + name);
//		}
//		int canNum = scan.nextInt();
//		String canN = polled.printCandidates()[canNum -1];
//		System.out.println("Voted for" + " " + canN);
//		polled.voteFor(canN);
//		
//	}
//
//
//	@Override
//	public void run() {
//		while(on){
//			this.consoleRun();
//		}
//		
//	}
//}
