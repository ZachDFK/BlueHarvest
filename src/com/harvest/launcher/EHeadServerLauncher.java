package com.harvest.launcher;

import com.harvest.controller.EHeadServer;

/**
 * Launches a head server
 *
 */
public class EHeadServerLauncher {
	public static void main (String args[]) {
		EHeadServer h = new EHeadServer();
		// Run this server using a synchronous function to keep main thread alive
		h.run();
	}
}
