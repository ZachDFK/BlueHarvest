package com.harvest.model.polling;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import com.harvest.model.candidate.Candidate;
import com.harvest.model.voter.Voter;
import com.harvest.sharedlibrary.SharedConstants;

public class newPollingStation {
	
	private InetAddress districtAddr;
	private List<Candidate> districtCandidates;
	private List<Voter> districtVoters;
	
	
	public newPollingStation() {
		
//		
//		Scanner scan = new Scanner(System.in);
//
//		System.out.print("Enter head server adress:");
//		String listener = scan.nextLine();
//		try {
//			this.headAddress = InetAddress.getByName(listener);
//		} catch (UnknownHostException e1) {
//			e1.printStackTrace();
//		}
//		scan.close();
//
//		this.id = id;
//		receiveData = new byte[1024];
//		this.superSecretMatchedCode = code;
//		try {
//			districtServer = new DatagramSocket(
//					SharedConstants.BASESOCKETNUMBER + id);
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}

		
	}

}
