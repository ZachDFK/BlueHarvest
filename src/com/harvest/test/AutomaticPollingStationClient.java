package com.harvest.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import com.harvest.shared.Constant;

/**
 * This polling station just uses a file to automatically vote
 * No GUI is provided for the user.
 * 
 * Launch this class using AutomaticPollingStationLauncher
 *
 */
public class AutomaticPollingStationClient {
	
	private InetAddress districtServerAddress;
	private int districtServerPort;
	private DatagramSocket pollingStationToDistrictSocket;
	
	public AutomaticPollingStationClient() {
		
		boolean successInput = false;
		Scanner input = new Scanner(System.in);
		
		try {
			pollingStationToDistrictSocket = new DatagramSocket();
			
			byte[] payload = Constant.DISTRICT_SERVER_REGISTRATION_CODE.getBytes();

			DatagramPacket registrationPacket;
			DatagramPacket registrationAcknowledgementPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		
			String ip;
			String port;
			while(!successInput) {
				System.out.println("Enter the IP of the district server");
				ip = input.nextLine();
				System.out.println("Enter the port of the district server");				
				port = input.nextLine();
				
				// Register polling station to district server
				registrationPacket = new DatagramPacket(payload, payload.length,
						InetAddress.getByName(ip), Integer.parseInt(port));
				pollingStationToDistrictSocket.send(registrationPacket);
	
				// Receive acknowledgment from district server
				registrationAcknowledgementPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
				pollingStationToDistrictSocket.setSoTimeout(Constant.DATAGRAM_TIMEOUT);
				
				try {
					pollingStationToDistrictSocket.receive(registrationAcknowledgementPacket);
					successInput = true;
				} catch(SocketTimeoutException e) {
					System.out.println("Could not connect to district server.");
					successInput = false;
				}
			}

			districtServerAddress = registrationAcknowledgementPacket.getAddress();
			districtServerPort = registrationAcknowledgementPacket.getPort();
			
			String message = new String(registrationAcknowledgementPacket.getData(), 0, registrationAcknowledgementPacket.getLength());

			if (message.equals(Constant.SUCCESS_CONNECTION_ACK)) {
				System.out.println("Polling station successfully connected to district server.");
			} else {
				System.out.println("Polling station did not connect to district server.");
				pollingStationToDistrictSocket.close();
				return;
			}
			
			readAutomaticVotingFile();
			
		} catch (IOException e) {
			System.out.println("Polling station cannot aquire port. Shutting down.");
			return;
		}
	}
	
	public void readAutomaticVotingFile() {
		
		// After you connect to a district, you must get the list of candidates
		// Since this is an automatic test process, where you provide a file to 
		// vote for candidates, this is somewhat unnecessary and only here to test
		// if the district is responding correctly
		
		try {
			pollingStationToDistrictSocket.setSoTimeout(0);
			
			byte[] message = Constant.REQUEST_CANDIDATE_INFO.getBytes();
			DatagramPacket candidatesPacket = new DatagramPacket(message, message.length, districtServerAddress, districtServerPort);

			// send request to district for candidates information
			pollingStationToDistrictSocket.send(candidatesPacket);

			candidatesPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			pollingStationToDistrictSocket.receive(candidatesPacket);
			
			String candidatesString = new String(candidatesPacket.getData(), 0, candidatesPacket.getLength());
			String[] candidates = candidatesString.split(Constant.CANDIDATES_STRING_DELIMITER);
				
		} catch (IOException e) {
			System.out.println("District is not returning with candidates");
		}
		
		// Read the file of all the voters registering and sending votes
		// See the magic in the media server GUI
		try (BufferedReader br = new BufferedReader(new FileReader(Constant.TEST_FOLDER_PATH + "TestVoters"))) {
			for (String line; (line = br.readLine()) != null;) {
				DatagramPacket votePacket;
				byte[] msg = line.getBytes();
				votePacket = new DatagramPacket(msg, msg.length, districtServerAddress, districtServerPort);
				pollingStationToDistrictSocket.send(votePacket);
				System.out.println("Automatic client has sent a packet to the district");
				
				votePacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
				pollingStationToDistrictSocket.receive(votePacket);
				System.out.println("Client has recieved a packet from the district");
				
				System.out.println("District Reply: " + new String(votePacket.getData(), 0, votePacket.getLength()));
			}
		} catch (IOException e) {
			System.out.println("Automatic test file has been corrupted.");
		}
	}
}
