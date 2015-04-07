package com.harvest.model.district;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	
	ExecutorService executor;
	
	public EDistrictServer() {
		executor = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						POOL_QUEUE_SIZE), new DaemonThreadFactory());
		
		candidates = new ArrayList<Candidate>();
		candidateVoters = new HashMap<Voter, Candidate>();
		
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
		
		while(true) {
			packetData = new byte[Constant.DATAGRAM_BUFFER_SIZE];
			packet = new DatagramPacket(packetData, Constant.DATAGRAM_BUFFER_SIZE);

			try {
				pollingStationRegistrationSocket = new DatagramSocket();
				System.out.println("Connect to " + InetAddress.getLocalHost().getHostAddress() + ":" + pollingStationRegistrationSocket.getLocalPort() + " to register to this district.");
				pollingStationRegistrationSocket.receive(packet);
				System.out.println("District receives packet for registration");
				channel = new DistrictPollingStationChannel(packet.getAddress(), packet.getPort(), new String(packet.getData(), 0, packet.getLength()));
				executor.execute(channel);
				
			} catch (IOException e) {
				System.out.println("Head server cannot recieve any more packets");
				break;
			}
		}
	}
	
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
					candidateVoters.put(new Voter(vars[0], vars[1], vars[2], vars[3]), null);
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
					
					
					

					while(true) {
						packetBuffer = new byte[Constant.DATAGRAM_BUFFER_SIZE];
						packet = new DatagramPacket(packetBuffer, packetBuffer.length);
						channelSocket.receive(packet);
						
						System.out.println(new String(packet.getData(), 0, packet.getLength()));
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
