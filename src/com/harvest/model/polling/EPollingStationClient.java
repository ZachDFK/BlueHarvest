package com.harvest.model.polling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import com.harvest.shared.Constant;
import com.harvest.view.PollStaionClientView;

public class EPollingStationClient implements ActionListener {
	
	private InetAddress districtServerAddress;
	private int districtServerPort;
	private ActionListener controller;
	
	private DatagramSocket pollingStationToDistrictSocket;
	
	private Map<String, String> candidateIdMap;
	
	private PollStaionClientView clientView;
	
	
	public EPollingStationClient() {

		candidateIdMap = new HashMap<String, String>();
		
		controller = this;
		
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
	
	private void showGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				clientView = new PollStaionClientView(controller);
				// UIManager.put("swing.boldmetal", Boolean.FALSE);
				PollStaionClientView.createAndShowGUI(clientView);
			}

		});
	}

	public void setupVotingSystem() {
		try {
			byte[] message = Constant.REQUEST_CANDIDATE_INFO.getBytes();
			DatagramPacket candidatesPacket = new DatagramPacket(message, message.length, districtServerAddress, districtServerPort);

			// send request to district for candidates information
			pollingStationToDistrictSocket.send(candidatesPacket);

			candidatesPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			pollingStationToDistrictSocket.receive(candidatesPacket);
			
			String candidatesString = new String(candidatesPacket.getData(), 0, candidatesPacket.getLength());
			String[] candidates = candidatesString.split(Constant.CANDIDATES_STRING_DELIMITER);
			updateView(candidates);
			
			Scanner in = new Scanner(System.in);
			while(true) {
				System.out.println("Here are the candidates: ");
				for (String name : candidateIdMap.keySet())
					System.out.println(name);
				System.out.println("Enter your command");

				String input = in.nextLine();
				byte[] input_data = input.getBytes();
				
				candidatesPacket = new DatagramPacket(input_data, input_data.length, districtServerAddress, districtServerPort);
				pollingStationToDistrictSocket.send(candidatesPacket);
				System.out.println("Client has sent a packet to the district");
				
				candidatesPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
				pollingStationToDistrictSocket.receive(candidatesPacket);
				System.out.println("Client has recieved a packet from the district");
				
				System.out.println("District Reply: " + new String(candidatesPacket.getData(), 0, candidatesPacket.getLength()));
			}
		} catch (IOException e) {
			System.out.println("Polling Station Timeout error.");
		}
	}
	
	public void updateView(String[] candidates){
		for(String c: candidates){
			if (c.length() > 0) {
				String[] candidateInfo = c.split(Constant.DATA_DELIMITER);
				candidateIdMap.put(candidateInfo[0] + " - " + candidateInfo[2], candidateInfo[1]);
			}
		}

		//GUI INIT
		showGUI();
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		System.out.println("BOOOOMOMOMOBMBOMBBOBOOBOOBOBOOOBOBO ");
	}
}
