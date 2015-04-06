package com.harvest.model.district;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.harvest.model.candidate.Candidate;
import com.harvest.model.head.EHeadServer.DistrictChannel;
import com.harvest.model.voter.Voter;
import com.harvest.shared.Constant;
import com.harvest.shared.SharedConstants;

public class EDistrictServer {

	private InetAddress headServerAddress;
	private int headServerPort;
	
	private List<Candidate> candidates;
	private List<Voter> voters;
	
	public EDistrictServer() {
		
		candidates = new ArrayList<Candidate>();
		voters = new ArrayList<Voter>();
		
		Scanner input = new Scanner(System.in);
		boolean successInput = false;

		try {
			DatagramSocket districtToHeadSocket = new DatagramSocket();
			
			byte[] payload = Constant.HEAD_SERVER_REGISTRATION_CODE.getBytes();

			DatagramPacket registrationPacket;
			DatagramPacket registrationAcknowledgementPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			
			while(!successInput) {
				System.out.println("Enter the IP of the head server");
				
				// Register district to head server
				registrationPacket = new DatagramPacket(payload, payload.length,
						InetAddress.getByName(input.nextLine()), Constant.HEAD_SERVER_PORT);
				districtToHeadSocket.send(registrationPacket);
	
				// Receive acknowledgement from head server
				registrationAcknowledgementPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
				districtToHeadSocket.setSoTimeout(Constant.DATAGRAM_TIMEOUT);
				
				try {
					districtToHeadSocket.receive(registrationAcknowledgementPacket);
					successInput = true;
				} catch(SocketTimeoutException e) {
					System.out.println("Could not connect to head server.");
					successInput = false;
				}
			}
			
			headServerAddress = registrationAcknowledgementPacket.getAddress();
			headServerPort = registrationAcknowledgementPacket.getPort();

			String message = new String(registrationAcknowledgementPacket.getData(), 0, registrationAcknowledgementPacket.getLength());

			if (message.equals(Constant.SUCCESS_CONNECTION_ACK)) {
				System.out.println("District successfully connected to head server.");
				Thread t = new Thread(new DistrictHeadServerChannel(districtToHeadSocket));
				t.start();
			} else {
				System.out.println("District did not connect to head server.");
			}
		} catch (IOException e) {
			System.out.println("District cannot aquire port. Shutting down.");
			return;
		}
		
		successInput = false;
		
		while(!successInput) {
			System.out.println("Enter the name of the candidate file:");
			successInput = openCandidateFile(input.nextLine());
			if(!successInput) System.out.println("Could not open the candidate file...");
		}
		
		successInput = false;
		
		while(!successInput) {
			System.out.println("Enter the name of the voter file:");
			successInput = openVoterFile(input.nextLine());
			if(!successInput) System.out.println("Could not open the voter file...");
		}
		
//		setupPollingStationRegistration();
	}
	
//	public void setupPollingStationRegistration() {
//		DatagramPacket packet;
//		byte[] packetData;
//		DistrictChannel channel;
//		
//		while(true) {
//			packetData = new byte[Constant.DATAGRAM_BUFFER_SIZE];
//			packet = new DatagramPacket(packetData, Constant.DATAGRAM_BUFFER_SIZE);
//
//			try {
//				headSocket.receive(packet);
//				System.out.println("Head Server receives packet for registration");
//				channel = new DistrictChannel(packet.getAddress(), packet.getPort(), new String(packet.getData(), 0, packet.getLength()));
//				executor.execute(channel);
//				
//			} catch (IOException e) {
//				System.out.println("Head server cannot recieve any more packets");
//				break;
//			}
//		}
//	}
	
	public boolean openCandidateFile(String fileName) {
		try (BufferedReader br = new BufferedReader(new FileReader(Constant.DISTRICT_CANDIDATES_PATH + fileName))) {
			for (String line; (line = br.readLine()) != null;) {
				String[] vars = line.split(Constant.FILE_DELIMITER);
				if (vars.length == 3)
					candidates.add(new Candidate(vars[0], vars[1], vars[2]));
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean openVoterFile(String fileName) {
		try (BufferedReader br = new BufferedReader(new FileReader(Constant.DISTRICT_VOTERS_PATH + fileName))) {
			for (String line; (line = br.readLine()) != null;) {
				String[] vars = line.split(Constant.FILE_DELIMITER);
				if (vars.length == 4)
					voters.add(new Voter(vars[0], vars[1], vars[2], vars[3]));
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public class DistrictHeadServerChannel implements Runnable {

		private DatagramSocket districtToHeadSocket;
		
		public DistrictHeadServerChannel(DatagramSocket districtToHeadSocket) {
			this.districtToHeadSocket = districtToHeadSocket;
		}
		
		@Override
		public void run() {
			try {
				districtToHeadSocket.setSoTimeout(0);
	
				DatagramPacket receivedPacketFromHead;
				
				while(true) {
					receivedPacketFromHead = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
					districtToHeadSocket.receive(receivedPacketFromHead);
					
					System.out.println("Packet recieved from head server.");
				}
			} catch (IOException e) {
				System.out.println("District is breaking connection with head server.");
			}
		}		
	}
}
