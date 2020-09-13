package pt.utl.ist.thesis77077.api;

import java.util.ArrayList;
import java.util.List;

public class PRCommit {
	
	private String sha;
	private String message;
	private List<ChangedFile> changedFiles;
	private boolean processed;
	
	
	public PRCommit(String sha, String message) {
		this.sha = sha;
		this.message = message;
		this.changedFiles = new ArrayList<ChangedFile>();
		this.processed = false;
	}
	
	public String getSha() {
		return sha;
	}
	
	
	public void setSha(String sha) {
		this.sha = sha;
	}
	
	
	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}
	
	
	public void setChangedFiles(List<ChangedFile> changedFiles) {
		this.changedFiles = changedFiles;
	}
	
	public void addChangedFile(ChangedFile changedFile) {
		this.changedFiles.add(changedFile);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isProcessed() {
		return this.processed;
	}
	
	public void setProcessStatus(boolean status) {
		this.processed = status;
	}
}
