package com.harvest.model.voter;

public class Voters {
	private String name;
	private int sinNumber; // ID
	private int districtNum;
	private String address;

	public Voters(String name, int sinNumber, int districtNum, String address) {
		this.name = name;
		this.sinNumber = sinNumber;
		this.districtNum = districtNum;
		this.address = address;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSinNumber() {
		return sinNumber;
	}

	public void setSinNumber(int sinNumber) {
		this.sinNumber = sinNumber;
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
