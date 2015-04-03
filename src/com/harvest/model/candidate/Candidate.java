package com.harvest.model.candidate;

public class Candidate {

	private int id;
	private String name;
	private String partyName;
	private int voteCount;

	public Candidate(String name, String id, String partyName) {
		this.name = name;
		this.id = Integer.parseInt(id);
		this.partyName = partyName;
		this.voteCount = 0;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public String getPartyName() {
		return partyName;
	}

	public int getCandidateVoteCount() {
		return voteCount;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public String toString() {
		return name + ":" + id + ":" + partyName + ":" + voteCount;

	}

}
