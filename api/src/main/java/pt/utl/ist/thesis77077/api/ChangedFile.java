package pt.utl.ist.thesis77077.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangedFile implements Comparable {
	
	private String name;
	private String patch;
	private String content;
	private String fileMasterUrl;
	private String fileCommitUrl;
	private double impactValue;
	private Map<String, String> relatedJavaFiles;
	private List<String> patchSmells;
	private List<String> patchSmellsPmd;
	
	
	public ChangedFile (String name, String patch, String content) {
		this.name = name;
		this.patch = patch;
		this.content = content;
		this.impactValue = 0;
		this.relatedJavaFiles = new HashMap<String, String>();
		this.patchSmells = new ArrayList<String>();
		this.patchSmellsPmd = new ArrayList<String>();
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getPatch() {
		return patch;
	}
	
	
	public void setPatch(String patch) {
		this.patch = patch;
	}
	
	
	public String getContent() {
		return this.content;
	}
	
	
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public double getImpactValue() {
		return impactValue;
	}
	
	
	public void setImpactValue(int impactValue) {
		this.impactValue = impactValue;
	}
	
	
	
	public Map<String, String> getRelatedJavaFiles() {
		return relatedJavaFiles;
	}


	public void setRelatedJavaFiles(Map<String, String> relatedJavaFiles) {
		this.relatedJavaFiles = relatedJavaFiles;
	}
	
	


	public String getFileMasterUrl() {
		return fileMasterUrl;
	}


	public void setFileMasterUrl(String fileMasterUrl) {
		this.fileMasterUrl = fileMasterUrl;
	}


	public String getFileCommitUrl() {
		return fileCommitUrl;
	}


	public void setFileCommitUrl(String fileCommitUrl) {
		this.fileCommitUrl = fileCommitUrl;
	}
	
	


	public List<String> getPatchSmells() {
		return patchSmells;
	}


	public void setPatchSmells(List<String> patchSmells) {
		this.patchSmells = patchSmells;
	}
	
	public List<String> getPatchSmellsPmd() {
		return patchSmellsPmd;
	}


	public void setPatchSmellsPmd(List<String> patchSmellsPmd) {
		this.patchSmellsPmd = patchSmellsPmd;
	}


	public void incrementImpactValue(double value) {
		this.impactValue = this.impactValue + value;
	}
	
	@Override
	public int compareTo(Object compareFile) {
        double compareImpact= ((ChangedFile)compareFile).getImpactValue();
        
        /* orders file by descending order o impact value*/
        return Double.compare(compareImpact, this.getImpactValue());
    }

	
}
