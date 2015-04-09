package com.harvest.controller;

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

/**
 * This class should be created once a head server and at least one district server exists
 * The polling station should be instantiated using the EPollingStationLauncher
 * 
 * When you create a polling station, you are prompted for the IP and port of the district
 * server. The IP and port of the district is given in the console when you create a district.
 * 
 * Once a polling station is created, and the inputs are provided, the GUI will launch for the
 * end user to vote and register new voters.
 * 
 * @author alok
 *
 */
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
			//pollingStationToDistrictSocket.close();
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
				clientView.loadCandidatesToRadioButtons();
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
			loadCandidatesToMap(candidates);
			addCandidatesToView();

		} catch (IOException e) {
			System.out.println("Polling Station Timeout error.");
		}
	}
	
//	public String registerPacket
	public Map<String, String> getCandidateIdMap() {
		return candidateIdMap;
	}

	public void setCandidateIdMap(Map<String, String> candidateIdMap) {
		this.candidateIdMap = candidateIdMap;
	}

	
	public void loadCandidatesToMap(String[] candidates){
		for(String c: candidates){
			if (c.length() > 0) {
				String[] candidateInfo = c.split(Constant.DATA_DELIMITER);
				candidateIdMap.put(candidateInfo[0] + " - " + candidateInfo[2], candidateInfo[1]);
			}
		}
	
	}
	
	public void addCandidatesToView(){

		//GUI INIT
		showGUI();
		
	}
	
	public void sendPollingStationData(String data) {
		try {
			byte[] input_data = data.getBytes();
			
			DatagramPacket districtDataPacket = new DatagramPacket(input_data, input_data.length, districtServerAddress, districtServerPort);
			pollingStationToDistrictSocket.send(districtDataPacket);
			System.out.println("Client has sent a vote/voter registration packet to the district");
			
			districtDataPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			pollingStationToDistrictSocket.receive(districtDataPacket);
			System.out.println("Client has recieved an acknowledgement packet from the district");
			
			handleDistrictAcknowledgements(new String(districtDataPacket.getData(), 0, districtDataPacket.getLength()));
		} catch (IOException e) {
			System.out.println("Could not send data to district");
		}
	}
	
	private void handleDistrictAcknowledgements(String ack) {
		if(ack.equals(Constant.VOTE_FAILURE_INVALID))
			clientView.showAlertBox("Vote failed due to invalid credentials.");
		else if(ack.equals(Constant.VOTE_FAILURE_MULTIPLE))
			clientView.showAlertBox("Vote failed. You cannot vote multiple times.");
		else if(ack.equals(Constant.VOTE_SUCCESS))
			clientView.showAlertBox("Vote success.");
		else if(ack.equals(Constant.VOTE_REGISTRATION_FAILURE))
			clientView.showAlertBox("Voter with same SIN has already registered");
		else if(ack.equals(Constant.VOTE_REGISTRATION_SUCCESS))
			clientView.showAlertBox("Voter has successfully registered");

		clientView.clearInputBoxes();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		System.out.println((e.getActionCommand()));
		if(e.getActionCommand().equals("Submit")){
			if(clientView.getInfoRegister()==null)
			{
				System.out.println("Invalid action performed");
			}else{
				sendPollingStationData(clientView.getInfoRegister());
			}
		} else if(e.getActionCommand().equals("Vote")){
			if(clientView.getInfoRegister()==null)
			{
				System.out.println("Invalid action performed");
			}else{
				sendPollingStationData(clientView.getInfoVote());
			}
		}
	}
}
