package com.harvest.model.head;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
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
	
	Map<String, String> districtData;
	Map<DatagramSocket, String> mediaServerMap;
	
	ExecutorService executor;
	
	public EHeadServer() {

		executor = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
						POOL_QUEUE_SIZE), new DaemonThreadFactory());

		districtData = new HashMap<String, String>();
		mediaServerMap = new HashMap<DatagramSocket, String>();
		
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
	
	// Call when new district is registered to head server
	public synchronized void addDistrictData(String address, int port) {
		districtData.put(address + Constant.DATA_DELIMITER + port, "");
	}
	
	// Call when district updates its info
	public synchronized void updateDistrictData(String address, int port, String data) {
		districtData.put(address + Constant.DATA_DELIMITER + port, data);
		updateMediaServers();
	}
	
	// Call when new media server is registered to head server
	public synchronized void addMediaServer(DatagramSocket sock, String addr, int port) {
		mediaServerMap.put(sock, addr + Constant.DATA_DELIMITER + port);
	}
	
	// Call to parse each district server's data into a string format
	private String getDistrictDataString() {
		String dataStr = "";
		Map<String, Integer> partyVoteMap = new HashMap<String, Integer>();
		
		for (Map.Entry<String, String> entry : districtData.entrySet()) {
			String[] districtData = entry.getValue().split(Constant.CANDIDATES_STRING_DELIMITER);
			for (String d : districtData) {
				String[] districtDataContent = d.split(Constant.DATA_DELIMITER);
				if (districtDataContent.length == 3) {
					Integer partyVotes = partyVoteMap.get(districtDataContent[1]);
					if (partyVotes == null)
						partyVoteMap.put(districtDataContent[1], Integer.parseInt(districtDataContent[2]));
					else
						partyVoteMap.put(districtDataContent[1], partyVotes+Integer.parseInt(districtDataContent[2]));
				}
			}
		}
		
		for (Map.Entry<String, Integer> entry : partyVoteMap.entrySet()) {
			dataStr += entry.getKey() + Constant.DATA_DELIMITER + entry.getValue() + Constant.CANDIDATES_STRING_DELIMITER;
		}
		
		System.out.println("Data sent to media servers: " + dataStr);
		
		return dataStr;
	}
	
	// Call when media server needs to be updated (when one of the district server updates)
	private void updateMediaServers() {
		try {
			String[] mediaServerIp;
			byte[] mediaServerData = getDistrictDataString().getBytes();
			
			for (Map.Entry<DatagramSocket, String> entry : mediaServerMap.entrySet()) {
				mediaServerIp = entry.getValue().split(Constant.DATA_DELIMITER);
				
				entry.getKey().send(new DatagramPacket(mediaServerData, mediaServerData.length, InetAddress.getByName(mediaServerIp[0]), Integer.parseInt(mediaServerIp[1])));

				System.out.println("Sent information update to media server " + mediaServerIp[0] + Constant.DATA_DELIMITER + mediaServerIp[1]);
			}
		} catch (IOException e) {
			System.out.println("Cannot send data to media server");
		}
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
					addDistrictData(districtAddress.getHostAddress(), districtPort);
					System.out.println("Registration to head server is successful");
					
					// Send acknowledgment to district for successful connection
					byte[] message = Constant.SUCCESS_CONNECTION_ACK.getBytes();
					channelSocket.send(new DatagramPacket(message, message.length, districtAddress, districtPort));

					DatagramPacket packet;

					while(true) {
						Thread.sleep(Constant.HEAD_SERVER_REQUEST_PERIOD);
						
						message = Constant.VOTE_TALLY_REQUEST.getBytes();
						packet = new DatagramPacket(message, message.length, districtAddress, districtPort);
						channelSocket.send(packet);
						
						packet = new DatagramPacket(new byte[Constant.DATAGRAM_BUFFER_SIZE], Constant.DATAGRAM_BUFFER_SIZE);
						channelSocket.receive(packet);
						
						String dat = new String(packet.getData(), 0, packet.getLength());

						System.out.println("Data received from district: " + dat);
						updateDistrictData(this.districtAddress.getHostAddress(), this.districtPort, dat);
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
			} else if(this.districtData.equals(Constant.HEAD_SERVER_MEDIA_REGISTRATION_CODE)) {
				try {
					channelSocket = new DatagramSocket();

					System.out.println("Registration to head server is successful");
					
					// Send acknowledgment to media server for successful connection
					byte[] message = Constant.SUCCESS_CONNECTION_ACK.getBytes();
					channelSocket.send(new DatagramPacket(message, message.length, districtAddress, districtPort));

					addMediaServer(channelSocket, this.districtAddress.getHostAddress(), this.districtPort);
					
					while(true) {
						// hold thread alive
					}
				} catch (IOException e) {
					System.out.println("Could not open socket to reply to media server");
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