package com.harvest.launcher;

import com.harvest.view.BarChart;

/**
 * Launches a new media server
 *
 */
public class EMediaServerLauncher {
	public static void main(String[] args) {
		BarChart nChart = new BarChart();
		nChart.setUp();
		nChart.establichConnectionWithHeadServer();
	}
}
