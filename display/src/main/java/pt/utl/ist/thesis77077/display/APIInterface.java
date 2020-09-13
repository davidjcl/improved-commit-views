package pt.utl.ist.thesis77077.display;

import java.util.ArrayList;
import java.util.List;

public class APIInterface {
	
	private static List<APIData> apiData = new ArrayList<APIData>();
	
	public APIInterface() {
		
	}

	public static List<APIData> getApiData() {
		return apiData;
	}

	public void setApiData(List<APIData> apiData) {
		this.apiData = apiData;
	}
	
	public static void addApiData(APIData api) {
		apiData.add(api);
	}
	
	public static APIData getSingleApiData(String repositoryOwner, String repositoryName) {
		for(APIData apid : apiData) {
			if(apid.getRepoToScanOwner().equals(repositoryOwner) && apid.getRepoToScanName().equals(repositoryName)) {
				return apid;
			}
		}
		return null;
	}
}
