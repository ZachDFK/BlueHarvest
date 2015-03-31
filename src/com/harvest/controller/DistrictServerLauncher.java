package com.harvest.controller;

import java.util.Scanner;

import com.harvest.model.district.newDistrictServer;
import com.harvest.sharedlibrary.SharedConstants;

public class DistrictServerLauncher {

	public static void main(String[] args) {
		
		String listener;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter head server adress given");
		listener = scanner.nextLine();
		newDistrictServer dServer;
		for(int id = 1; id<=10;id++){
			dServer = new newDistrictServer(SharedConstants.SUPERSECRET,id,listener);
		
			dServer.connectToHead();
			
		
		}
		scanner.close();
	}
}
