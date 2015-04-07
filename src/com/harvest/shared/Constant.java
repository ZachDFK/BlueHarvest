package com.harvest.shared;

public class Constant {
	
	public static int HEAD_SERVER_PORT = 2222;

	public static int DATAGRAM_BUFFER_SIZE = 1024;
	public static int DATAGRAM_TIMEOUT = 5000;
	
	public static String HEAD_SERVER_REGISTRATION_CODE = "12345";
	public static String DISTRICT_SERVER_REGISTRATION_CODE = "67890";
	
	public static String FILE_DELIMITER = ":";
	public static String DISTRICT_CANDIDATES_PATH = "./inputFiles/district_candidates/";
	public static String DISTRICT_VOTERS_PATH = "./inputFiles/district_voters/";
	
	public static String SUCCESS_CONNECTION_ACK = "SUCCESS";
	public static String FAILURE_CONNECTION_ACK = "FAILURE";
	
	public static String REQUEST_CANDIDATE_INFO = "CANDIDATES";
	
	public static String VOTE_SUCCESS = "VOTE_SUCCESS";
	public static String VOTE_FAILURE_BUSY = "VOTE_BUSY";
	public static String VOTE_FAILURE_MULTIPLE = "VOTE_MULTIPLE";
	public static String VOTE_FAILURE_INVALID = "VOTE_INVALID";
	
	public static String VOTE_REGISTRATION_SUCCESS = "VOTE_REG_SUCCESS";
	public static String VOTE_REGISTRATION_FAILURE = "VOTE_REG_FAILURE";
}
