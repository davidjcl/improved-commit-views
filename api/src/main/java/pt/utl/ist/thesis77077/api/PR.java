package pt.utl.ist.thesis77077.api;

import java.util.ArrayList;
import java.util.List;

public class PR {

	private String prTitle;
	private String state;
	private long id;
	private int number;
	private List<PRCommit> prCommits;
	
	
	public PR(String title, String state, long id, int number) {
		this.prTitle = title;
		this.state = state;
		this.id = id;
		this.number = number;
		this.prCommits = new ArrayList<PRCommit>();
	}
	
	public String getPrTitle() {
		return prTitle;
	}
	
	
	public void setPrTitle(String prTitle) {
		this.prTitle = prTitle;
	}
	
	
	public String getState() {
		return state;
	}
	
	
	public void setState(String state) {
		this.state = state;
	}
	
	
	public long getId() {
		return this.id;
	}
	
	
	public void setId(long id) {
		this.id = id;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public List<PRCommit> getPrCommits() {
		return prCommits;
	}
	
	
	public void setPrCommits(List<PRCommit> prCommits) {
		this.prCommits = prCommits;
	}
	
	public void addPrCommit(PRCommit prCommit) {
		this.prCommits.add(prCommit);
	}
	
}