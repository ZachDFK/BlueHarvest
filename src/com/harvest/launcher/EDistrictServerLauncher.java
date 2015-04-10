package com.harvest.launcher;

import com.harvest.controller.EDistrictServer;

/**
 * Launches a district server
 * 
 *
 */
public class EDistrictServerLauncher {
	public static void main (String args[]) {
		EDistrictServer d = new EDistrictServer(false);
		// Run the district server synchronously to keep main thread alive
		d.run();
	}
}
