package com.harvest.launcher;

import com.harvest.controller.AutomaticPollingStationClient;

/**
 * Launches an automatic voting system (with no GUI)
 * 
 */
public class AutomaticPollingStationLauncher {
	public static void main (String args[]) {
		new AutomaticPollingStationClient();
	}
}
