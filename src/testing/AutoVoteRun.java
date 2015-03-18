package testing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import clientserver.Candidate;
import clientserver.DistrictServer;
import clientserver.PollingStationClient;

public class AutoVoteRun implements Runnable{
	
	
	private FileInputStream voters;
	private DistrictServer district;
	private Scanner reader;
	public AutoVoteRun(DistrictServer district){
		this.district = district;
		try {
			
			voters = new FileInputStream("./inputFiles/VoterFile.txt");
			
			reader = new Scanner(voters);
			
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void run(){
		while(reader.hasNext()){
			int pollingNumber;
			pollingNumber = reader.nextInt();
			voteInPoll(district.getStation(pollingNumber-1));
		
		}
	}
	public void voteInPoll(PollingStationClient polled){
		int canNum = reader.nextInt();
		String canN = polled.printCandidates()[canNum -1];
		polled.voteFor(canN);
		
	}
}
