package com.harvest.model.district;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import com.harvest.model.candidate.Candidate;
import com.harvest.model.polling.AbstractClient;
import com.harvest.model.polling.PollingStationClient;
import com.harvest.model.voter.AutoVoteRun;
import com.harvest.model.voter.ManualVoter;

public class DistrictServer implements Runnable {

	private int id;
	private ArrayList<Candidate> candidates = new ArrayList<Candidate>();

	private static final int MAX_CLIENT = 2;
	private ArrayList<AbstractClient> stationClients = new ArrayList<AbstractClient>();
	private final Semaphore available = new Semaphore(MAX_CLIENT, true);

	private DatagramSocket serverSocket;
	private InetAddress headServerAddress;
	private DatagramSocket headServerSocket;
	private byte[] sendData;
	private DatagramPacket sendPacket;

	private DatagramPacket receivePacket;
	private byte[] receiveData;

	private String receivedTaliString;

	public DistrictServer(DatagramSocket server, int id, int mode) {
		this.id = id;
		this.headServerSocket = server;
		try {
			instiateSockets();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int c = 0; c < 10; c++) {
			stationClients
					.add(new PollingStationClient(this, this.serverSocket));
		}

		loadCanditates();
		if (mode == 1) {
			ManualVoter manu = new ManualVoter(this);
			Thread t = new Thread(manu);
			t.start();
		} else {
			AutoVoteRun auto = new AutoVoteRun(this);
			Thread t = new Thread(auto);
			t.start();
		}
	}

	public void loadCanditates() {
		try {
			FileInputStream candidateFile = new FileInputStream(
					"./inputFiles/CandidateFile.txt");

			Scanner reader = new Scanner(candidateFile);

			int tempID;
			do {
				tempID = Integer.parseInt(reader.nextLine().toString()
						.split("-")[0]);
			} while (!(tempID == this.id) || !(reader.hasNext()));

			boolean valid = true;
			while (valid) {

				String candidateInfo = reader.nextLine().toString();

				String splitInfo[] = new String[10];
				splitInfo = candidateInfo.split(":");
				if (splitInfo.length < 3) {
					valid = false;
				} else {
					System.out.println(candidateInfo);
					candidates.add(new Candidate(splitInfo[0], Integer
							.parseInt(splitInfo[1]), splitInfo[2]));
				}
			}
			reader.close();
			candidateFile.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendPacket(String dataToSend) {
		sendData = new byte[1024];
		sendData = dataToSend.getBytes();
		sendPacket = new DatagramPacket(sendData, sendData.length,
				headServerAddress, 1111);
		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void instiateSockets() throws Exception {
		serverSocket = new DatagramSocket(1112);
		headServerAddress = InetAddress.getByName("localhost");

	}

	public void run() {
		boolean on = true;
		receiveData = new byte[1024];
		while (on) {

			receivePacketing();
		}
	}

	public void receivePacketing() {

		try {
			available.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			serverSocket.receive(receivePacket);

		} catch (IOException e) {
			e.printStackTrace();
		}

		String stringP = new String(receivePacket.getData(), 0,
				receivePacket.getLength());

		taliAddition(stringP);

		available.release();

	}

	public void taliAddition(String passedS) {

		for (Candidate c : candidates) {
			if (c.getName().equals(passedS)) {
				c.setVoteTali(c.getVoteTali() + 1);
			}
		}
	}

	public void end() throws FileNotFoundException {
		String msg = "";
		PrintWriter out = new PrintWriter("./outputFiles/results.txt");
		for (Candidate cand : candidates) {
			msg += cand.getPartyName() + ":" + cand.getVoteTali() + "- \n";
		}

		out.print(msg);
		out.close();

	}

	/**
	 * get the tali of all clients listening
	 * 
	 * @throws InterruptedException
	 * 
	 */
	public void requestTali() throws InterruptedException {
		String msg = "";
		for (Candidate cand : candidates) {
			msg += cand.getPartyName() + ":" + cand.getVoteTali() + "\n";
		}

		this.sendPacket(msg);
	}

	public PollingStationClient getStation(int index) {

		return (PollingStationClient) stationClients.get(index);
	}

	public String[] getCandidates() {
		String[] names = new String[candidates.size()];
		int inc = 0;
		for (Candidate c : candidates) {

			names[inc] = c.getName();
			inc++;
		}

		return names;
	}

}
