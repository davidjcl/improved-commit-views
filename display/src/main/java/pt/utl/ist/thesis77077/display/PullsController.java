package pt.utl.ist.thesis77077.display;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import pt.utl.ist.thesis77077.api.API;
import pt.utl.ist.thesis77077.api.APIException;
import pt.utl.ist.thesis77077.api.PR;

@Controller
public class PullsController {
	
	@GetMapping("/api/{repoToScanOwner}/{repoToScanName}/pulls")
	public String printPRs(Model model, @PathVariable String repoToScanOwner, @PathVariable String repoToScanName) {
		APIData apiData = APIInterface.getSingleApiData(repoToScanOwner, repoToScanName);
		API api = apiData.getApi();
		try {
			model.addAttribute("apiData", apiData);
			model.addAttribute("api", api);
			model.addAttribute("interfaces", APIInterface.getApiData());
			model.addAttribute("prs", api.getRepositoryPRsInfo());
		    return "apires";
		}catch(APIException e) {
			model.addAttribute("error", e.getMessage());
			return "redirect:/api/error";
		}
		
	  }
	
	@GetMapping("/api/{repoToScanOwner}/{repoToScanName}/pulls/{prNumber}/commits")
	public String printPRCommits(Model model, @PathVariable String repoToScanOwner, @PathVariable String repoToScanName, @PathVariable int prNumber) {
		
		APIData apiData = APIInterface.getSingleApiData(repoToScanOwner, repoToScanName); 
		API api = apiData.getApi();
		try {
			model.addAttribute("apiData", apiData);
			model.addAttribute("api", api);
			model.addAttribute("prNumber", prNumber);
			model.addAttribute("commits", api.printRepositoryPRCommits(prNumber));
			return "prCommits";
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/api/{repoToScanOwner}/{repoToScanName}/pulls";
	}
	
}
