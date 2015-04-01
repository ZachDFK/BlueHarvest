package com.harvest.model.head;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.harvest.sharedlibrary.SharedConstants;

public class newHeadServer {
	private final static int SOCKETNUMBER = 1111;
	private DatagramSocket headServer;
	private DatagramSocket headServerTalker;
	private DatagramPacket receivePacket;
	private DatagramPacket sendPacket;
	private byte[] receiveData;
	private byte[] sendData;
	private String superSecretMatchedCode;
	private ArrayList<InetAddress> clientList;

	public newHeadServer(String code) {
		receiveData = new byte[1024];
		this.superSecretMatchedCode = code;
		clientList = new ArrayList<InetAddress>();
		try {
			headServer = new DatagramSocket(SharedConstants.HEADSOCKETNUMBERLISTENER);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		try{
			headServerTalker = new DatagramSocket(SharedConstants.HEADSOCKETNUMBERTALKER);
		} catch(SocketException e){
			e.printStackTrace();
		}
		try {
			System.out.println(InetAddress.getLocalHost());

			Timer pollTimer = new Timer(InetAddress.getLocalHost());
			(new Thread(pollTimer)).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	

	public String receivePacketing() {

		while (true) {
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				headServer.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}

			InetAddress fromWho = receivePacket.getAddress();

			ServePacket p = new ServePacket(fromWho, new String(
					receivePacket.getData(), 0, receivePacket.getLength()));

			(new Thread(p)).start();
			
		}
	}

	public synchronized void addClient(InetAddress addr) {
		clientList.add(addr);
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
			// Test if the data is to register new district, send district data,
			// or is just trash
			if (this.senderData.equals(superSecretMatchedCode)) {
				addClient(senderAddr);
				System.out.println(clientList.get(clientList.size()-1) + " is added to the clients");
			} else if(this.senderData.equals(SharedConstants.POLLINGREQ)) {
			requestPoll();
			}else {
				System.out.println("Messsage recived: "+ senderData);
			}
		}

		
	}

	public void runHeadServer() {
		String[] parts = this.receivePacketing().split("$");
		String code = parts[0];
		String fromWho = parts[1];

		while (true) {
			if (code.equals(superSecretMatchedCode)) {
				// clientList.add(fromWho);

			}
		}
	}

	public void requestPoll() {
		int c= 1;
		for(InetAddress client:clientList){
			
			this.sendPacket(SharedConstants.POLLINGREQ,client,c++);
			
		}
	}


	public void sendPacket(String dataToSend,InetAddress address,int id) {
		sendData = new byte[1024];
		sendData = dataToSend.getBytes();
		 sendPacket = new DatagramPacket(sendData, sendData.length,address, SharedConstants.BASESOCKETNUMBER+id);
		try {
			headServerTalker.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
