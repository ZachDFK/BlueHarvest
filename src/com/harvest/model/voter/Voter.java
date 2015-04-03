package com.harvest.model.voter;

public class Voter {
	private String firstName;
	private String lastName;
	private int sin; // ID
	private int districtNum;
	private String address;

	public Voter(String fname, String lname, int sin, int districtNum, String address) {
		this.firstName = fname;
		this.lastName = lname;
		this.sin = sin;
		this.districtNum = districtNum;
		this.address = address;

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String fname) {
		this.firstName = fname;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lname) {
		this.lastName = lname;
	}

	public int getSin() {
		return sin;
	}

	public void setSin(int sin) {
		this.sin = sin;
	}

	public int getDistrictNum() {
		return districtNum;
	}

	public void setDistrictNum(int districtNum) {
		this.districtNum = districtNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
