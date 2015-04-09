package com.harvest.test;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.harvest.controller.EDistrictServer;
import com.harvest.controller.EHeadServer;
import com.harvest.controller.EPollingStationClient;
import com.harvest.shared.Constant;

public class ConnectionHeadTest {

	@Test
	public void districtConnectTest() {

		new EHeadServer();
		try {

			byte[] message = Constant.HEAD_SERVER_REGISTRATION_CODE.getBytes();
			DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName("127.0.0.1"), Constant.HEAD_SERVER_PORT);
			DatagramSocket sock = new DatagramSocket();
			sock.send(packet);
			System.out.println("Sending registration packet");
			
			packet = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			sock.setSoTimeout(Constant.DATAGRAM_TIMEOUT);
			sock.receive(packet);
			System.out.println("Received acknowledgement");
			
			assert(new String(packet.getData(), 0, packet.getLength()).equals(Constant.SUCCESS_CONNECTION_ACK));
		} catch (Exception e) {
			System.out.println("Connection error");
		}
	}

	@After
	public void tearDown() throws Exception {
	}
}
