package pt.utl.ist.thesis77077.display;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import pt.utl.ist.thesis77077.api.API;
import pt.utl.ist.thesis77077.api.APIException;

@Controller
public class APIController {
	
	@Autowired
	private YAMLConfig config;

	@GetMapping("/api")
	public String gitRepoForm(Model model) {
		model.addAttribute("apiData", new APIData());
	    return "apiform";
	 }
	
	@GetMapping("/api/error")
	public String apiErrorPage(Model model) {
		model.addAttribute("apiData", new APIData());
	    return "apierror";
	 }
	
	@PostMapping("/api/{repoToScanOwner}/{repoToScanName}")
	public String gitRepoSubmit(Model model, @ModelAttribute APIData apiData, @PathVariable String repoToScanOwner, @PathVariable String repoToScanName) {
		try {
			APIData newAPIData = APIInterface.getSingleApiData(apiData.getRepoToScanOwner(), apiData.getRepoToScanName());
			if(newAPIData == null){
				if(config.getAuth_token().isBlank()) {
					newAPIData = new APIData(apiData.getRepoToScanOwner(), apiData.getRepoToScanName());
				}else {
					newAPIData = new APIData(apiData.getRepoToScanOwner(), apiData.getRepoToScanName(), config.getAuth_token());
				}
				APIInterface.addApiData(newAPIData);
			}
			API api = newAPIData.getApi();
			model.addAttribute("apiData", newAPIData);
			model.addAttribute("api", api);
						System.out.println(api.isAuthenticated());
			System.out.println(config.getAuth_token());
		    return "redirect:/api/" + apiData.getRepoToScanOwner() + "/" + apiData.getRepoToScanName() + "/pulls";
		}catch(APIException e) {
			model.addAttribute("error", e.getMessage());
		}
		return "redirect:/api/error";
	 }

}
