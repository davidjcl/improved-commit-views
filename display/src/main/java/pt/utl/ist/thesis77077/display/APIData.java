package pt.utl.ist.thesis77077.display;

import java.util.SortedMap;

import pt.utl.ist.thesis77077.api.API;
import pt.utl.ist.thesis77077.api.APIException;
import pt.utl.ist.thesis77077.core.Core;

public class APIData {
		
	private String repoToScanOwner;
	private String repoToScanName;
	private API api;
	private Core core;
	
	public APIData() {
		
	}
	
	public APIData(String repoToScanOwner, String repoToScanName) throws APIException{
		this.repoToScanOwner = repoToScanOwner;
		this.repoToScanName = repoToScanName;
		this.api = new API(repoToScanOwner, repoToScanName);
		this.core = new Core();
	}

	public APIData(String repoToScanOwner, String repoToScanName, String authToken) throws APIException{
		this.repoToScanOwner = repoToScanOwner;
		this.repoToScanName = repoToScanName;
		this.api = new API(repoToScanOwner, repoToScanName, authToken);
		this.core = new Core();
	}

	public String getRepoToScanOwner() {
		return repoToScanOwner;
	}

	public void setRepoToScanOwner(String repoToScanOwner) {
		this.repoToScanOwner = repoToScanOwner;
	}

	public String getRepoToScanName() {
		return repoToScanName;
	}

	public void setRepoToScanName(String repoToScanName) {
		this.repoToScanName = repoToScanName;
	}

	public API getApi() {
		return api;
	}

	public void setApi(API api) {
		this.api = api;
	}

	public Core getCore() {
		return core;
	}

	public void setCore(Core core) {
		this.core = core;
	}
	
	
	
}
