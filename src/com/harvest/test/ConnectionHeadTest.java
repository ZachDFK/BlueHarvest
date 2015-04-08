package com.harvest.test;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;

import com.harvest.model.district.EDistrictServer;
import com.harvest.model.head.EHeadServer;
import com.harvest.model.polling.EPollingStationClient;
import com.harvest.shared.Constant;

public class ConnectionHeadTest {

	
	private EHeadServer testHeadServer;
	
	
	@Before
	public void setUp() throws Exception {
		testHeadServer = new EHeadServer();
	}

	@Test
	public void districtConnectTest() {

		try {
			byte[] message = Constant.HEAD_SERVER_REGISTRATION_CODE.getBytes();
			DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), Integer.parseInt(Constant.HEAD_SERVER_REGISTRATION_CODE));
			DatagramSocket sock = new DatagramSocket();
			
			sock.send(packet);
			
			packet = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			sock.receive(packet);
			
			assert(new String(packet.getData(), 0, packet.getLength()).equals(Constant.SUCCESS_CONNECTION_ACK));
		} catch (Exception e) {
			System.out.println("Connection error");
		}
	}

}
