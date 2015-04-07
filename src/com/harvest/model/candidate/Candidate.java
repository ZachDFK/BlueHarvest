package com.harvest.model.candidate;

public class Candidate {

	private String id;
	private String name;
	private String partyName;

	public Candidate(String name, String id, String partyName) {
		this.name = name;
		this.id = id;
		this.partyName = partyName;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public String toString() {
		return name + ":" + id + ":" + partyName;

	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Candidate))
			return false;
		
		if(this.id.equals(((Candidate)obj).getId()))
			return true;
		
		return false;
	}
}
