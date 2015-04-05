package com.harvest.model.head;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.harvest.shared.SharedConstants;

public class Timer implements Runnable {
	
	private DatagramSocket timerSocket;
	private byte[] sendData;
	private DatagramPacket sendPacket;
	private InetAddress headServer;
	
	public Timer(InetAddress headserver){
		this.headServer = headserver;
		try {
			timerSocket = new DatagramSocket(1109);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		System.out.println("Timer Started");
		while(true){

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("PULL!");
			this.sendPollRequest();
			
		}
	
	}
	public void sendPollRequest(){
		sendData = new byte[1024];
		sendData = SharedConstants.POLLINGREQ.getBytes();
		sendPacket = new DatagramPacket(sendData, sendData.length,
				headServer, SharedConstants.HEADSOCKETNUMBERLISTENER);
		try {
			timerSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
