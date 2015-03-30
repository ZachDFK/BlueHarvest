package com.harvest.model.voter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.harvest.model.candidate.Candidate;
import com.harvest.model.district.DistrictServer;
import com.harvest.model.polling.PollingStationClient;

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
		try {
			district.end();
		} catch (FileNotFoundException e1) {
		
			e1.printStackTrace();
		}
		reader.close();
		try {
			voters.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	
	}
	public void voteInPoll(PollingStationClient polled){
		int canNum = reader.nextInt();
		String canN = polled.printCandidates()[canNum -1];
		polled.voteFor(canN);
		
	}
}
