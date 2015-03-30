package com.harvest.model.polling;

import java.net.DatagramSocket;

import com.harvest.model.district.DistrictServer;

public class PollingStationClient extends AbstractClient{


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
