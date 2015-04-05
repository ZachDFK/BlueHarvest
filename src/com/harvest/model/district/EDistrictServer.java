package com.harvest.model.district;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.harvest.shared.Constant;

public class EDistrictServer {

	public EDistrictServer(String headServerIp) {
		try {
			DatagramSocket s = new DatagramSocket();

			byte[] payload = Constant.HEAD_SERVER_REGISTRATION_CODE.getBytes();

			// Register district to head server
			DatagramPacket p = new DatagramPacket(payload, payload.length,
					InetAddress.getByName(headServerIp), 2222);
			s.send(p);

			// Receive acknowledgement from head server
			DatagramPacket r = new DatagramPacket(new byte[1024], 1024);
			s.receive(r);

			String message = new String(r.getData(), 0, r.getLength());

			if (message.equals(Constant.SUCCESS_CONNECTION_ACK))
				System.out.println("District successfully connected to head server.");
			
			s.close();
		} catch (IOException e) {
			System.out.println("Distrct cannot aquire port. Shutting down.");
			return;
		}
	}
}
