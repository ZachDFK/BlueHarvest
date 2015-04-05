package com.harvest.model.district;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.harvest.model.candidate.Candidate;
import com.harvest.shared.SharedConstants;

public class newDistrictServer {

	private int id;
	private DatagramSocket headServer;
	private DatagramSocket districtServer;

	private DatagramPacket receivePacket;

	private DatagramPacket sendPacket;

	private byte[] receiveData;
	private byte[] sendData;
	private String superSecretMatchedCode;
	private InetAddress headAddress;

	private ArrayList<Candidate> candidates;

	public newDistrictServer(String code, int id) {
		Scanner scan = new Scanner(System.in);

		System.out.print("Enter head server adress:");
		String listener = scan.nextLine();
		try {
			this.headAddress = InetAddress.getByName(listener);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		scan.close();

		this.id = id;
		receiveData = new byte[1024];
		this.superSecretMatchedCode = code;
		try {
			districtServer = new DatagramSocket(
					SharedConstants.BASESOCKETNUMBER + id);
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}

	public void connectToHead() {

		System.out.println("Connecting...");

		sendData = new byte[1024];
		sendData = superSecretMatchedCode.getBytes();
		sendPacket = new DatagramPacket(sendData, sendData.length, headAddress,
				SharedConstants.HEADSOCKETNUMBERLISTENER);
		try {
			districtServer.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Connected");

	}

	public void sendData(String message) {
		sendData = new byte[1024];
		sendData = message.getBytes();
		sendPacket = new DatagramPacket(sendData, sendData.length, headAddress,
				SharedConstants.HEADSOCKETNUMBERLISTENER);
		try {
			districtServer.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String receivePacketing() {

		while (true) {
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				districtServer.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}

			InetAddress fromWho = receivePacket.getAddress();
			ServePacket p = new ServePacket(fromWho, new String(
					receivePacket.getData(), 0, receivePacket.getLength()));

			(new Thread(p)).start();

		}
	}

	private class ServePacket implements Runnable {

		private InetAddress senderAddr;
		private String senderData;

		ServePacket(InetAddress addr, String data) {
			this.senderAddr = addr;
			this.senderData = data;
		}

		@Override
		public void run() {
			System.out.println("Poll request!");

		}
	}

	public void loadCanditates(String fileName) {
		// try {
		// FileInputStream candidateFile = new FileInputStream(
		// FileName);
		//
		// Scanner reader = new Scanner(candidateFile);
		//
		// while(reader.hasNext()){
		// Candidate()
		//
		// }
		System.out.println("load candidate call, start.");
		fileName =SharedConstants.DISTRICTCANDIDATE + fileName;
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			for (String line; (line = br.readLine()) != null;) {

				String[] vars = line.split(SharedConstants.REGEX);
				if (vars.length == 3)
					candidates.add(new Candidate(vars[0], vars[1], vars[2]));
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("load candidate call, stoped.");
	}
}
