package com.harvest.shared;

/**
 * This file contains ALL the constants used through out this project
 * Each constant is self explanatory due to their names
 *
 */
public class Constant {
	
	public static int HEAD_SERVER_PORT = 2222;

	public static int DATAGRAM_BUFFER_SIZE = 1024;
	public static int DATAGRAM_TIMEOUT = 5000;
	public static int HEAD_SERVER_REQUEST_PERIOD = 10000;
	
	// These are secret codes in other to register to the head server, or district servers
	// This is used to avoid malicious connections to head server or district servers
	public static String HEAD_SERVER_REGISTRATION_CODE = "12345";
	public static String DISTRICT_SERVER_REGISTRATION_CODE = "67890";
	public static String HEAD_SERVER_MEDIA_REGISTRATION_CODE = "00000";
	
	public static String DATA_DELIMITER = ":";
	public static String CANDIDATES_STRING_DELIMITER = "%";
	public static String DISTRICT_CANDIDATES_PATH = "./inputFiles/district_candidates/";
	public static String DISTRICT_VOTERS_PATH = "./inputFiles/district_voters/";
	public static String TEST_FOLDER_PATH = "./inputFiles/test_input_files/";
	
	public static String TEST_CANDIDATES_FILE = "TestDistrictCandidates";
	public static String TEST_VOTERS_FILE = "TestDistrictVoters";
	
	// These two strings represent what the data of the datagram packet contains
	// to represent failed or successful connection
	public static String SUCCESS_CONNECTION_ACK = "SUCCESS";
	public static String FAILURE_CONNECTION_ACK = "FAILURE";
	
	public static String REQUEST_CANDIDATE_INFO = "CANDIDATES";
	
	// When a person at the polling station is voting send "1" with packet
	// When a person at the polling station is registering send "0" with the packet
	public static String REGISTER_VOTER_PACKET_ID = "0";
	public static String VOTE_CANDIDATE_PACKET_ID = "1";
	
	public static int POLLING_STATION_PACKET_DATA_SIZE = 5;
	
	// The rest of the strings just represent what to send in the datagram packet
	// to say success, failure, etc.
	public static String VOTE_SUCCESS = "VOTE_SUCCESS";
	public static String VOTE_FAILURE_BUSY = "VOTE_BUSY";
	public static String VOTE_FAILURE_MULTIPLE = "VOTE_MULTIPLE";
	public static String VOTE_FAILURE_INVALID = "VOTE_INVALID";
	
	public static String VOTE_REGISTRATION_SUCCESS = "VOTE_REG_SUCCESS";
	public static String VOTE_REGISTRATION_FAILURE = "VOTE_REG_FAILURE";
	
	public static String VOTE_TALLY_REQUEST = "VOTE_TALLY";
	
	public static String INVALID_PACKET = "INVALID_PACKET";
}
