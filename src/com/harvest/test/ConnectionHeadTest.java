package com.harvest.test;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.harvest.controller.EHeadServer;
import com.harvest.shared.Constant;

/**
 * This test suit checks to see if the district, head server, and media server respond appropriately
 * when connections between them are being established.
 *
 */
public class ConnectionHeadTest {

	private static EHeadServer head;
	
	@Before
	public void setUp() throws Exception {
		if(head == null) {
			head = new EHeadServer();
			(new Thread(head)).start();
		}
	}
	
	// Test connection between district and head server
	// This test sees if the correct packet data
	// is being sent from both the head and the district
	@Test
	public void districtConnectTest() {
		try {
			byte[] message = Constant.HEAD_SERVER_REGISTRATION_CODE.getBytes();
			DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), Constant.HEAD_SERVER_PORT);
			DatagramSocket sock = new DatagramSocket();
			sock.send(packet);
			System.out.println("Sending registration packet from district");
			
			packet = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			sock.setSoTimeout(Constant.DATAGRAM_TIMEOUT);
			sock.receive(packet);
			System.out.println("Received acknowledgement from head");
			
			assertTrue(new String(packet.getData(), 0, packet.getLength()).equals(Constant.SUCCESS_CONNECTION_ACK));
		} catch (Exception e) {
			System.out.println("Connection error");
		}
	}
	
	// Test connection between media server and head server
	// This test sees if the correct packet data
	// is being sent from both the head and the media server
	@Test
	public void mediaConnectTest() {
		try {
			byte[] message = Constant.HEAD_SERVER_MEDIA_REGISTRATION_CODE.getBytes();
			DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), Constant.HEAD_SERVER_PORT);
			DatagramSocket sock = new DatagramSocket();
			sock.send(packet);
			System.out.println("Sending registration packet from media server");
			
			packet = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
			sock.setSoTimeout(Constant.DATAGRAM_TIMEOUT);
			sock.receive(packet);
			System.out.println("Received acknowledgement from head");
			
			assertTrue(new String(packet.getData(), 0, packet.getLength()).equals(Constant.SUCCESS_CONNECTION_ACK));
		} catch (Exception e) {
			System.out.println("Connection error");
		}
	}

	@After
	public void tearDown() throws Exception {
	}
}
