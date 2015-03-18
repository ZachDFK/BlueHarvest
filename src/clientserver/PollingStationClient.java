package clientserver;

import java.net.DatagramSocket;

public class PollingStationClient extends Client{


	private DistrictServer district;
	
	public PollingStationClient(DistrictServer district,DatagramSocket server) {
		super(server);
		this.district = district;
		
	}
	
	public String[] printCandidates(){
		
		return district.getCandidates();
	}
	

	public void voteFor(String candidate)
	{
		System.out.println("Confirm sending vote for " + candidate );
		this.sendPacket(candidate);
	}

}
