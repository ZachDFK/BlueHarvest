package com.harvest.model.head;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.harvest.shared.Constant;
import com.harvest.shared.DaemonThreadFactory;

public class EHeadServer {

	private static final int THREAD_COUNT = 5;
	private static final int POOL_QUEUE_SIZE = 5;
	
	DatagramSocket headSocket;
	List<InetAddress> districts;
	ExecutorService executor;
	
	public EHeadServer() {

		executor = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						POOL_QUEUE_SIZE), new DaemonThreadFactory());
		districts = new ArrayList<InetAddress>();
		
		try {
			headSocket = new DatagramSocket(Constant.HEAD_SERVER_PORT);

			System.out.println("Head Server started on " + InetAddress.getLocalHost().getHostAddress() + ":" + headSocket.getLocalPort());	
			receivePackets();
			
		} catch (IOException e) {
			System.out.println("Head server could not aquire port. Shutting down.");
		}
	}
	
	public void receivePackets() {
		DatagramPacket packet;
		byte[] packetData;
		DistrictChannel channel;
		
		while(true) {
			
			packetData = new byte[Constant.DATAGRAM_BUFFER_SIZE];
			packet = new DatagramPacket(packetData, Constant.DATAGRAM_BUFFER_SIZE);

			try {
				headSocket.receive(packet);
				System.out.println("Head Server receives packet for registration");
				channel = new DistrictChannel(packet.getAddress(), packet.getPort(), new String(packet.getData(), 0, packet.getLength()));
				executor.execute(channel);
				
			} catch (IOException e) {
				System.out.println("Head server cannot recieve any more packets");
				break;
			}
		}
	}
	
	public synchronized void addDistrict(InetAddress addr) {
		districts.add(addr);
	}
	
	public class DistrictChannel implements Runnable {
		
		private DatagramSocket channelSocket;
		private InetAddress districtAddress;
		private int districtPort;
		private String districtData;
		
		public DistrictChannel(InetAddress addr, int port, String data) {
			districtAddress = addr;
			districtPort = port;
			districtData = data;
			try {
				channelSocket = new DatagramSocket();
			} catch (SocketException e) {
				System.out.println("Channel to connect district and server could not be completed.");
			}
		}

		@Override
		public void run() {
			
			if(this.districtData.equals(Constant.HEAD_SERVER_REGISTRATION_CODE)) {
				
				try {
					channelSocket = new DatagramSocket();

					// Add district to list of districts
					addDistrict(districtAddress);
					System.out.println("Registration to head server is successful");
					
					// Send acknowledgment to district for successful connection
					byte[] message = Constant.SUCCESS_CONNECTION_ACK.getBytes();
					channelSocket.send(new DatagramPacket(message, message.length, districtAddress, districtPort));

					DatagramPacket packet;
					byte[] packetBuffer;

					while(true) {
						Thread.sleep(Constant.HEAD_SERVER_REQUEST_PERIOD);
						
						message = Constant.VOTE_TALLY_REQUEST.getBytes();
						packet = new DatagramPacket(message, message.length, districtAddress, districtPort);
						channelSocket.send(packet);
						
						packet = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
						channelSocket.receive(packet);
						
						System.out.println(new String(packet.getData(), 0, packet.getLength()));
					}
				} catch (IOException e) {
					System.out.println("Could not open socket to reply to district");
					channelSocket.close();
					return;
				} catch (InterruptedException i) {
					System.out.println("Could not send periodic requests to districts. Shutting down...");
					channelSocket.close();
					return;
				}
			} else {
				System.out.println("Invalid registration to head server");
				channelSocket.close();
			}
		}
	}
}