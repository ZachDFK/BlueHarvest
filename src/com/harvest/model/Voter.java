package com.harvest.model;

/**
 * Voter model
 * SIN is used as a identifier and also as a password when voting
 *
 */
public class Voter {
	private String firstName;
	private String lastName;
	private String sin; // ID
	private String address;

	public Voter(String fname, String lname, String sin, String address) {
		this.firstName = fname;
		this.lastName = lname;
		this.sin = sin;
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

	public String getSin() {
		return sin;
	}

	public void setSin(String sin) {
		this.sin = sin;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Voter))
			return false;
		
		if(this.sin.equals(((Voter)obj).getSin()))
			return true;
		
		return false;
	}
}
