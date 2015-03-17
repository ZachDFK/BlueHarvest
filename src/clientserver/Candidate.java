package clientserver;

public class Candidate {
	private String name;
	private int id;
	private String partyName;
	private int voteTali;
	
	public Candidate(String name, int id, String partyName){
		this.name = name;
		this.id = id;
		this.partyName = partyName;
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
	public int getVoteTali() {
		return voteTali;
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
	public void setVoteTali(int voteTali) {
		this.voteTali = voteTali;
	}
	
	
	
	
}
