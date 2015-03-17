package clientserver;

import java.net.DatagramSocket;

public class PollingStationClient extends Client{


	
	
	public PollingStationClient(DatagramSocket server) {
		super(server);
	}


	public void voteFor(String canditate)
	{
		this.sendPacket(canditate);
	}

}
