package com.harvest.model.district;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.harvest.model.candidate.Candidate;
import com.harvest.model.head.EHeadServer.DistrictChannel;
import com.harvest.model.voter.Voter;
import com.harvest.shared.Constant;
import com.harvest.shared.DaemonThreadFactory;
import com.harvest.shared.SharedConstants;

public class EDistrictServer {

	private static final int THREAD_COUNT = 3;
	private static final int POOL_QUEUE_SIZE = 3;
	
	private InetAddress headServerAddress;
	private int headServerPort;
	
	private List<Candidate> candidates;
	private Map<Voter, Candidate> candidateVoters;
	
	private String candidatesString;	// String format of the candidates
	
	ExecutorService executor;
	
	public EDistrictServer() {
		executor = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						POOL_QUEUE_SIZE), new DaemonThreadFactory());
		
		candidates = new ArrayList<Candidate>();
		candidateVoters = new HashMap<Voter, Candidate>();
		candidatesString = "";
		
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
				districtToHeadSocket.close();
				return;
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
		
		setupPollingStationRegistration();
	}
	
	public void setupPollingStationRegistration() {
		DatagramSocket pollingStationRegistrationSocket;
		DatagramPacket packet;
		DistrictPollingStationChannel channel;
		byte[] packetData;
		
		try {
			pollingStationRegistrationSocket = new DatagramSocket();
			System.out.println("Connect to " + InetAddress.getLocalHost().getHostAddress() + ":" + pollingStationRegistrationSocket.getLocalPort() + " to register to this district.");

			while(true) {
				packetData = new byte[Constant.DATAGRAM_BUFFER_SIZE];
				packet = new DatagramPacket(packetData, Constant.DATAGRAM_BUFFER_SIZE);
	
				pollingStationRegistrationSocket.receive(packet);
				System.out.println("District receives packet for registration");
				channel = new DistrictPollingStationChannel(packet.getAddress(), packet.getPort(), new String(packet.getData(), 0, packet.getLength()));
				executor.execute(channel);				
			}
		} catch (IOException e) {
			System.out.println("Socket cannot be aquired to setup district registration");
		}

	}
	
	public boolean openCandidateFile(String fileName) {
		try (BufferedReader br = new BufferedReader(new FileReader(Constant.DISTRICT_CANDIDATES_PATH + fileName))) {
			for (String line; (line = br.readLine()) != null;) {
				candidatesString += line + Constant.CANDIDATES_STRING_DELIMITER;
				String[] vars = line.split(Constant.DATA_DELIMITER);
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
				String[] vars = line.split(Constant.DATA_DELIMITER);
				if (vars.length == 4)
					candidateVoters.put(new Voter(vars[0], vars[1], vars[2], vars[3]), null);
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public Candidate getCandidateById(String id) {
		for (Candidate c : candidates) {
			if(c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}
	
	public synchronized String handlePollingStationPacket(DatagramPacket packet) {
		String psPacketMessage = new String(packet.getData(), 0, packet.getLength());
		String[] psSplitPacketMessage = psPacketMessage.split(Constant.DATA_DELIMITER);
		
		if(psSplitPacketMessage.length == Constant.POLLING_STATION_PACKET_DATA_SIZE) {
			if(psSplitPacketMessage[0].equals(Constant.REGISTER_VOTER_PACKET_ID)) {
				return registerNewVoter(psSplitPacketMessage[1], psSplitPacketMessage[2], psSplitPacketMessage[3], psSplitPacketMessage[4]);
			} else if (psSplitPacketMessage[0].equals(Constant.VOTE_CANDIDATE_PACKET_ID)) {
				return voteForCandidate(psSplitPacketMessage[1], psSplitPacketMessage[2], psSplitPacketMessage[3], psSplitPacketMessage[4]);				
			} else {
				System.out.println("Invalid Packet from polling station.");
			}
		} else {
			System.out.println("Invalid Packet from polling station.");
		}
		return null;
	}
	
	public String registerNewVoter(String fName, String lName, String sin, String addr) {
		for (Map.Entry<Voter, Candidate> v : candidateVoters.entrySet()) {
			Voter vot = v.getKey();
			
			if(vot.getSin().equals(sin)) {
				return Constant.VOTE_REGISTRATION_FAILURE;
			}
		}
		
		candidateVoters.put(new Voter(fName, lName, sin, addr), null);
		return Constant.VOTE_REGISTRATION_SUCCESS;
//		
//		
//		if (candidateVoters.containsKey(new Voter(fName, lName, sin, addr))) {
//			return Constant.VOTE_REGISTRATION_FAILURE;
//		} else {
//			candidateVoters.put(new Voter(fName, lName, sin, addr), null);
//			return Constant.VOTE_REGISTRATION_SUCCESS;
//		}
	}
	
	public String voteForCandidate(String fName, String lName, String sin, String candidateId) {
		for (Map.Entry<Voter, Candidate> v : candidateVoters.entrySet()) {
			Voter vot = v.getKey();
			if(vot.getSin().equals(sin) && vot.getFirstName().equals(fName) && vot.getLastName().equals(lName)) {
				if(v.getValue() == null) {
					v.setValue(getCandidateById(candidateId));
					return Constant.VOTE_SUCCESS;
				} else {
					return Constant.VOTE_FAILURE_MULTIPLE;
				}
			}
		}
		
		return Constant.VOTE_FAILURE_INVALID;
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
	
	public class DistrictPollingStationChannel implements Runnable {

		private DatagramSocket channelSocket;
		private InetAddress pollingStationAddress;
		private int pollingStationPort;
		private String pollingStationData;
		
		public DistrictPollingStationChannel(InetAddress addr, int port, String data) {
			pollingStationAddress = addr;
			pollingStationPort = port;
			pollingStationData = data;
			try {
				channelSocket = new DatagramSocket();
			} catch (SocketException e) {
				System.out.println("Channel to connect district and polling station could not be completed.");
			}
		}
		
		@Override
		public void run() {
			if(this.pollingStationData.equals(Constant.DISTRICT_SERVER_REGISTRATION_CODE)) {
				
				try {
					channelSocket = new DatagramSocket();

					System.out.println("Registration to district server is successful. Sending connection success acknowledgement.");
					
					// Send acknowledgment to polling station for successful connection
					byte[] message = Constant.SUCCESS_CONNECTION_ACK.getBytes();
					channelSocket.send(new DatagramPacket(message, message.length, pollingStationAddress, pollingStationPort));
					
					// Receive candidate information request from polling station	
					DatagramPacket packet;
					byte[] packetBuffer;
					
					packetBuffer = new byte[Constant.DATAGRAM_BUFFER_SIZE];
					packet = new DatagramPacket(packetBuffer, packetBuffer.length);
					channelSocket.receive(packet);
						
					String pollingStationMessage = new String(packet.getData(), 0, packet.getLength());
					
					// When polling station requests for candidate information, send it
					if(pollingStationMessage.equals(Constant.REQUEST_CANDIDATE_INFO)){
						byte[] candidatesMessage = candidatesString.getBytes();
						packet = new DatagramPacket(candidatesMessage, candidatesMessage.length, pollingStationAddress, pollingStationPort);
						channelSocket.send(packet);
						System.out.println("Sent candidate info back to client");
						
						while(true) {
							packetBuffer = new byte[Constant.DATAGRAM_BUFFER_SIZE];
							packet = new DatagramPacket(packetBuffer, packetBuffer.length);
							channelSocket.receive(packet);
							System.out.println("Received a vote packet from a polling station");
							
							String pollingStationVoteReplyMessage = handlePollingStationPacket(packet);
							byte[] pollingStationVoteReply = pollingStationVoteReplyMessage == null ? Constant.INVALID_PACKET.getBytes() : pollingStationVoteReplyMessage.getBytes();
							
							packet = new DatagramPacket(pollingStationVoteReply, pollingStationVoteReply.length, pollingStationAddress, pollingStationPort);
							channelSocket.send(packet);
							System.out.println("Sending a reply to the vote packet to the polling station");
						}
					} else {
						System.out.println("Polling station sending invalid requests. Closing socket(");
						channelSocket.close();
						return;
					}
					
				} catch (IOException e) {
					System.out.println("Could not open socket to reply to polling station");
					channelSocket.close();
					return;
				}
			} else {
				System.out.println("Invalid registration to district server");
				channelSocket.close();
			}
		}
	}
}
