package com.harvest.controller;

import java.util.Scanner;

import com.harvest.model.district.newDistrictServer;
import com.harvest.shared.SharedConstants;

public class DistrictServerLauncher {

	public static void main(String[] args) {
		
		newDistrictServer dServer;
		int id = 1;
//		for(int id = 1; id<=10;id++){
			dServer = new newDistrictServer(SharedConstants.SUPERSECRET,id);
			
		
//		}
	}
}
