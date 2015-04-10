package com.harvest.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.harvest.controller.EDistrictServer;
import com.harvest.shared.Constant;

/**
 * This test suite simulates basic user inputs in polling stations and how the district will respond
 * Possible scenarios:
 * 	-Registrating successfully
 * 	-Registrating failure due to duplicate Id
 *  -Voting successfully
 *  -Voting failure due to invalid credentials
 *  -Voting failure due to voting multiple times
 *
 */
public class PollingStationDataTest {
	private static EDistrictServer dist;
	
	@Before
	public void setUp() throws Exception {
		if(dist == null) {
			dist = new EDistrictServer(true);
			(new Thread(dist)).start();
		}
	}
	
	@Test
	public void testPollingStationInputData() throws IOException {
		
		/* Do all the basic set up to pretend to be a real polling station */
		
		DatagramPacket registrationPacket;
		DatagramSocket pollingStationToDistrictSocket = new DatagramSocket();
		int districtPort = 0;
		
		byte[] payload = Constant.DISTRICT_SERVER_REGISTRATION_CODE.getBytes();
		
		// Register polling station to district server
		registrationPacket = new DatagramPacket(payload, payload.length,
				InetAddress.getByName("localhost"), dist.getDistrictServerPort());
		pollingStationToDistrictSocket.send(registrationPacket);

		// Receive acknowledgment from district server
		registrationPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		pollingStationToDistrictSocket.setSoTimeout(Constant.DATAGRAM_TIMEOUT);
		
		try {
			pollingStationToDistrictSocket.receive(registrationPacket);
			districtPort = registrationPacket.getPort();
		} catch(SocketTimeoutException e) {
			fail("Could not connect to district");
		}

		// Send candidate information request (no really used in test suite)
		byte[] message = Constant.REQUEST_CANDIDATE_INFO.getBytes();
		DatagramPacket candidatesPacket = new DatagramPacket(message, message.length, InetAddress.getByName("localhost"), districtPort);

		// send request to district for candidates information
		pollingStationToDistrictSocket.send(candidatesPacket);

		candidatesPacket = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		pollingStationToDistrictSocket.receive(candidatesPacket);
		
		
		
		
		// --------------------------------------------------
		// THIS IS WHERE THE REAL TESTS BEGIN FOR USER INPUTS
		// --------------------------------------------------
		
		DatagramPacket clientData;
		DatagramPacket districtResponse;
		
		// Test 1: Register user (Should successfully register)
		payload = "0:Foo:Bar:888444333:123 Fun Street".getBytes();
		
		clientData = new DatagramPacket(payload, payload.length,
				InetAddress.getByName("localhost"), districtPort);
		pollingStationToDistrictSocket.send(clientData);

		districtResponse = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		pollingStationToDistrictSocket.receive(districtResponse);
		assertTrue((new String(districtResponse.getData(), 0, districtResponse.getLength())).equals(Constant.VOTE_REGISTRATION_SUCCESS));

		// Test 2: Register duplicate user (Should fail registration)
		payload = "0:Foo:Bar:888444333:123 Fun Street".getBytes();
		
		clientData = new DatagramPacket(payload, payload.length,
				InetAddress.getByName("localhost"), districtPort);
		pollingStationToDistrictSocket.send(clientData);

		districtResponse = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		pollingStationToDistrictSocket.receive(districtResponse);
		assertTrue((new String(districtResponse.getData(), 0, districtResponse.getLength())).equals(Constant.VOTE_REGISTRATION_FAILURE));

		// Test 3: Vote with user (Should successfully vote)
		payload = "1:Foo:Bar:888444333:1".getBytes();
		
		clientData = new DatagramPacket(payload, payload.length,
				InetAddress.getByName("localhost"), districtPort);
		pollingStationToDistrictSocket.send(clientData);

		districtResponse = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		pollingStationToDistrictSocket.receive(districtResponse);
		assertTrue((new String(districtResponse.getData(), 0, districtResponse.getLength())).equals(Constant.VOTE_SUCCESS));

		// Test 4: Vote with a user multiple times (Should fail for voting multiple times)
		payload = "1:Foo:Bar:888444333:1".getBytes();
		
		clientData = new DatagramPacket(payload, payload.length,
				InetAddress.getByName("localhost"), districtPort);
		pollingStationToDistrictSocket.send(clientData);

		districtResponse = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		pollingStationToDistrictSocket.receive(districtResponse);
		assertTrue((new String(districtResponse.getData(), 0, districtResponse.getLength())).equals(Constant.VOTE_FAILURE_MULTIPLE));
		
		// Test 4: Vote with an unregistered user (Should fail)
		payload = "1:Tim:Brown:00023544:1".getBytes();
		
		clientData = new DatagramPacket(payload, payload.length,
				InetAddress.getByName("localhost"), districtPort);
		pollingStationToDistrictSocket.send(clientData);

		districtResponse = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
		pollingStationToDistrictSocket.receive(districtResponse);
		assertTrue((new String(districtResponse.getData(), 0, districtResponse.getLength())).equals(Constant.VOTE_FAILURE_INVALID));
	}
}
