package com.harvest.controller;

import com.harvest.model.head.newHeadServer;
import com.harvest.shared.SharedConstants;

public class HeadServerLauncher {

public static void main(String[] args) {
		
		newHeadServer hServer = new newHeadServer(SharedConstants.SUPERSECRET);
		hServer.runHeadServer();
	}

}
