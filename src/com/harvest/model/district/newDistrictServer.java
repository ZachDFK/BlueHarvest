package com.harvest.model.district;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import com.harvest.sharedlibrary.SharedConstants;

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

	private Scanner scaning = new Scanner(System.in);
	public newDistrictServer(String code,int id,String listener){
		
		try {
			this.headAddress = InetAddress.getByName(listener);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		this.id = id;
		receiveData = new byte[1024];
		this.superSecretMatchedCode = code;
		try {
			districtServer = new DatagramSocket(SharedConstants.BASESOCKETNUMBER+id);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}
	public void connectToHead(){
		
		System.out.println("Connecting...");
		
		sendData = new byte[1024];
		sendData = superSecretMatchedCode.getBytes();
		sendPacket = new DatagramPacket(sendData, sendData.length,
				headAddress, SharedConstants.HEADSOCKETNUMBERLISTENER);
		try {
			districtServer.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Connected");
		
	
	}
	
	public void sendData(String message){
		sendData = new byte[1024];
		sendData = message.getBytes();
		sendPacket = new DatagramPacket(sendData, sendData.length,
				headAddress, SharedConstants.HEADSOCKETNUMBERLISTENER);
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
}
