package com.harvest.model.polling;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import com.harvest.shared.Constant;

public class EPollingStationClient {
	
	private InetAddress districtServerAddress;
	private int districtServerPort;
	
	private DatagramSocket pollingStationToDistrictSocket;
	
	public EPollingStationClient() {

		Scanner input = new Scanner(System.in);
		boolean successInput = false;
	
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
			
			setupVotingSystem();
			
			// might not need to close
			pollingStationToDistrictSocket.close();
		} catch (IOException e) {
			System.out.println("Polling station cannot aquire port. Shutting down.");
			return;
		}
	}	
	
	public void setupVotingSystem() {
		try {
			byte[] message = Constant.REQUEST_CANDIDATE_INFO.getBytes();
			DatagramPacket candidatesPacket = new DatagramPacket(message, message.length, districtServerAddress, districtServerPort);

			// send request to district for candidates information
			pollingStationToDistrictSocket.send(candidatesPacket);

			candidatesPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			pollingStationToDistrictSocket.receive(candidatesPacket);
			
			System.out.println(new String(candidatesPacket.getData(), 0, candidatesPacket.getLength()));
			
		
		} catch (IOException e) {
			System.out.println("Count not setup polling station to request for candidates.");
		}
	}
}
