package pt.utl.ist.thesis77077.display;

import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import pt.utl.ist.thesis77077.api.API;
import pt.utl.ist.thesis77077.api.APIException;
import pt.utl.ist.thesis77077.api.PRCommit;
import pt.utl.ist.thesis77077.core.Core;

@Controller
public class CommitController {
	
	@GetMapping("/api/{repoToScanOwner}/{repoToScanName}/pulls/{prNumber}/commit/{sha}")
	public String printPRCommits(Model model, @PathVariable String repoToScanOwner, @PathVariable String repoToScanName, @PathVariable int prNumber, @PathVariable String sha) {
		
		APIData apiData = APIInterface.getSingleApiData(repoToScanOwner, repoToScanName); 
		API api = apiData.getApi();
		Core core = apiData.getCore();
		try {
			PRCommit prCommit = api.getRepositoryPRCommit(prNumber, sha);
			core.processCommit(api, prCommit);
			core.sortCommitByDescendingImpact(prCommit);
			model.addAttribute("apiData", apiData);
			model.addAttribute("api", api);
			model.addAttribute("prNumber", prNumber);
			model.addAttribute("commit", prCommit);
			return "commitlib";
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/api/{repoToScanOwner}/{repoToScanName}/pulls/{prNumber}/commits";
	}
}
