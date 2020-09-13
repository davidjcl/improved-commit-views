package pt.utl.ist.thesis77077.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;



public class API {
	
	private List<PR> prs;
	private List<PRCommit> commits;//this is just for core module development purposes
	private String repoToScanOwner;
	private String repoToScanName;
	private GitHubClient client;
	private RepositoryService repoService;
	private CommitService commitService;
	private PullRequestService prService;
	private Repository repo;
	private Map<String, String> repositoryJavaFiles;
	private boolean authenticated = false;
	
	public API() {
		
	}
	
	public API(String repoToScanOwner, String repoToScanName) {
		
		try {
			this.prs = new ArrayList<PR>();
			this.commits = new ArrayList<PRCommit>();
			this.repoToScanOwner = repoToScanOwner;
			this.repoToScanName = repoToScanName;
			
			this.client = new GitHubClient();
			this.repoService = new RepositoryService(client);
			this.commitService = new CommitService(client);
			this.prService = new PullRequestService(client);
			this.repo = this.repoService.getRepository(repoToScanOwner, repoToScanName);
			this.repositoryJavaFiles = new HashMap<String, String>();
			retrieveRepositoryFilesNames(null);
		}catch (IOException e) {
			System.out.println("Something went wrong!");
			throw new APIException("Something went wrong while retrieving the repository info!");
		}
	}

	public API(String repoToScanOwner, String repoToScanName, String authToken) {
		
		try {
			this.prs = new ArrayList<PR>();
			this.commits = new ArrayList<PRCommit>();
			this.repoToScanOwner = repoToScanOwner;
			this.repoToScanName = repoToScanName;
			
			this.client = new GitHubClient();
			this.client.setOAuth2Token(authToken);
			this.authenticated = true;
			this.repoService = new RepositoryService(client);
			this.commitService = new CommitService(client);
			this.prService = new PullRequestService(client);
			this.repo = this.repoService.getRepository(repoToScanOwner, repoToScanName);
			this.repositoryJavaFiles = new HashMap<String, String>();
			retrieveRepositoryFilesNames(null);
		}catch (IOException e) {
			System.out.println("Something went wrong!");
			throw new APIException("Something went wrong while retrieving the repository info!");
		}
	}
	
	public List<PRCommit> getCommits(){
		return this.commits;
	}
	
	public void addCommit(PRCommit commit) {
		this.commits.add(commit);
	}
	
	//this is just for core module development purposes
	public void getCommitsOnly(GitHubClient client, RepositoryService repService, PullRequestService prs, CommitService commitService, String repositoryOwner, String repositoryName) {

		try {
			Repository pubRepo = repService.getRepository(repositoryOwner, repositoryName);
			List<RepositoryCommit> prCommits = new ArrayList<RepositoryCommit>();
			prCommits = commitService.getCommits(pubRepo);
			DataService dataService = new DataService(client);
			
			for(RepositoryCommit rc : prCommits) {
				PRCommit newPRCommit = new PRCommit(rc.getSha(), rc.getCommit().getMessage());
				RepositoryCommit anotherCommit = commitService.getCommit(pubRepo, rc.getSha());
				for(CommitFile f : anotherCommit.getFiles()) {
					Blob blob = dataService.getBlob(pubRepo, f.getSha());
					String fileContent = "";
					byte[] decoded = Base64.getMimeDecoder().decode(blob.getContent());
					fileContent = new String(decoded, "UTF-8") + "\n";

					ChangedFile changedFile = new ChangedFile(f.getFilename(), f.getPatch(), fileContent);
					newPRCommit.addChangedFile(changedFile);
				}
				addCommit(newPRCommit);
			}
		} catch (IOException e) {
			System.out.println("Error: Make sure the requested repository is public.");
		}

//		for(PRCommit prc: this.getCommits()) {
//			System.out.println(prc.getSha());
//			for(ChangedFile cf : prc.getChangedFiles()) {
//				System.out.println(cf.getName());
//				System.out.println(cf.getPatch());
//			}
//			System.out.println("########BREAK BETWEEN COMMITS########");
//		}
	}
	
	
	
	/**
	* retrieves and saves all the existing java files of the repository in an array
	*
	* @param path
	*/
	public void retrieveRepositoryFilesNames(String path) {
		
		try {
			//Repository pubRepo = repService.getRepository(repositoryOwner, repositoryName);
			ContentsService content = new ContentsService(this.client);
			DataService dataService = new DataService(this.client);
			//System.out.println(pubRepo.getName());
			boolean hasDirs = false;
			List<RepositoryContents> rootContent = null;
			if(path == null) {
				rootContent = content.getContents(this.repo);
			}else {
				rootContent = content.getContents(this.repo, path);
			}
			
			for(RepositoryContents repContent : rootContent) {
				//System.out.println(repContent.getPath());
				//System.out.println("content is: " + repContent.getType());
				if(repContent.getType().equals("dir")) {
					hasDirs = true;
					while(hasDirs) {
						//System.out.println("Dir: " + repContent.getPath());
						hasDirs = false;
						retrieveRepositoryFilesNames(repContent.getPath());
					}
				}
				else {
					if(repContent.getPath().endsWith(".java")) {
						String filePath = repContent.getPath();
						this.repositoryJavaFiles.put(filePath, createFilePathURL(filePath));
						
					}
					//System.out.println("File: " + repContent.getPath());
			
					
				}
			}
		} catch (IOException e) {
			System.out.println("Error: Make sure the requested repository is public.");
		}
		
	}
	
	public String createFilePathURL(String filePath) {
		String filePathURL = "https://github.com/" + this.getRepoToScanOwner() + "/" + this.repoToScanName + "/blob/master/" + filePath;
		return filePathURL;
	}
	
	public String createFilePathURLCommit(String filePath, String commitSha) {
		String filePathURL = "https://github.com/" + this.getRepoToScanOwner() + "/" + this.repoToScanName + "/blob/" + commitSha + "/" + filePath;
		return filePathURL;
	}
	
	public void getPubRepositorySinglePR(int prNumber) {

		try {
			Repository pubRepo = this.repoService.getRepository(this.repoToScanOwner, this.repoToScanName);
			List<RepositoryCommit> prCommits = new ArrayList<RepositoryCommit>();
			DataService dataService = new DataService(client);
			
			for(PullRequest pr : this.prService.getPullRequests(pubRepo, "open")) {
				if(pr.getNumber() == prNumber) {
					PR newPR = new PR(pr.getTitle(), pr.getState(), pr.getId(), pr.getNumber());
					prCommits = this.prService.getCommits(pubRepo, pr.getNumber());
					
					for(RepositoryCommit rc : prCommits) {
						PRCommit newPRCommit = new PRCommit(rc.getSha(), rc.getCommit().getMessage());
						RepositoryCommit anotherCommit = this.commitService.getCommit(pubRepo, rc.getSha());
						for(CommitFile f : anotherCommit.getFiles()) {
							Blob blob = dataService.getBlob(pubRepo, f.getSha());
							String fileContent = "";
							byte[] decoded = Base64.getMimeDecoder().decode(blob.getContent());
							fileContent = new String(decoded, "UTF-8") + "\n";
							
							ChangedFile changedFile = new ChangedFile(f.getFilename(), f.getPatch(), fileContent); //imcomplete because of content arg, is wrong
							newPRCommit.addChangedFile(changedFile);
						}
						newPR.addPrCommit(newPRCommit);
					}
					addPr(newPR);
					return;
				}
			}
		} catch (IOException e) {
			System.out.println("Error: Make sure the requested repository is public.");
		}
		
	}
	
	/*Refactored version of getPubRepositorySinglePR, getPubRepositorySinglePR to be removed later*/
	public PR getRepositoryPR(int prNumber) {
		for(PR pullRequest : this.getPrs()) {
			if(pullRequest.getNumber() == prNumber) {
				return pullRequest;
			}
		}
		try {
			Repository repository = this.repoService.getRepository(this.repoToScanOwner, this.repoToScanName);
			PR pullRequest = getPRData(repository, prNumber);
			if(pullRequest != null) {
				addPr(pullRequest);
			}
			return pullRequest;
			
		}catch(IOException e) {
			System.out.println("Error: Something went wrong while retrieving pull request data.");
			return null;
		}
	}
	
	
	public PR getPRData(Repository repository, int prNumber) throws IOException {
		List<RepositoryCommit> prCommits = new ArrayList<RepositoryCommit>();
		DataService dataService = new DataService(this.client);
		
		for(PullRequest pr : this.prService.getPullRequests(repository, "open")) {
			if(pr.getNumber() == prNumber) {
				PR newPR = new PR(pr.getTitle(), pr.getState(), pr.getId(), pr.getNumber());
				prCommits = this.prService.getCommits(repository, pr.getNumber());
				for(RepositoryCommit rc : prCommits) {
					PRCommit newPRCommit = new PRCommit(rc.getSha(), rc.getCommit().getMessage());
					RepositoryCommit anotherCommit = this.commitService.getCommit(repository, rc.getSha());
					
					for(CommitFile f : anotherCommit.getFiles()) {
						ChangedFile changedFile = getChangedFileData(dataService, repository, f);
						newPRCommit.addChangedFile(changedFile);
					}
					newPR.addPrCommit(newPRCommit);
				}
				return newPR;
			}
		}
		return null;
	}
	
	public ChangedFile getChangedFileData(DataService dataService, Repository repository, CommitFile f) throws IOException {
		Blob blob = dataService.getBlob(repository, f.getSha());
		String fileContent = "";
		byte[] decoded = Base64.getMimeDecoder().decode(blob.getContent());
		fileContent = new String(decoded, "UTF-8") + "\n";
		ChangedFile changedFile = new ChangedFile(f.getFilename(), f.getPatch(), fileContent);
		return changedFile;
	}
	
	public void printRepositoryPRsInfo() {
		try {
			Repository pubRepo = this.repoService.getRepository(this.repoToScanOwner, this.repoToScanName);
			
			for(PullRequest pr : this.prService.getPullRequests(pubRepo, "open")) {
				System.out.println(pr.getNumber() + " ---- " + pr.getTitle());
			}
		} catch (IOException e) {
			System.out.println("Error: Make sure the requested repository is public.");
		}
		
	}
	
	public SortedMap<Integer, String> getRepositoryPRsInfo(){
		SortedMap<Integer, String> existingPRs = new TreeMap<Integer, String>(Collections.reverseOrder());
		
		try {
			for(PullRequest pr : this.prService.getPullRequests(this.repo, "open")) {
				existingPRs.put(pr.getNumber(), pr.getTitle());
			}
		} catch (IOException e) {
			System.out.println("Error: Make sure the requested repository is public.");
			throw new APIException("Error: The requested repository is not public or doesn't exist.");
		}
		return existingPRs;
	}
	
	public SortedMap<String, String> printRepositoryPRCommits(int prNumber){
		SortedMap<String, String> existingCommits = new TreeMap<String, String>();
		
		try {
			for(RepositoryCommit rc : prService.getCommits(getRepo(), prNumber)){
				existingCommits.put(rc.getSha(), rc.getCommit().getMessage());
			}
		} catch (IOException e) {
			System.out.println("Error: Something went wrong while retrieving commits data.");
			throw new APIException("Error: Something went wrong while retrieving commits data.");
		}
		return existingCommits;
	}
	
	
	public PRCommit getRepositoryPRCommit(int prNumber, String commitSha) {
		
		for(PR pr : this.getPrs()) {
			if(pr.getNumber() == prNumber) {
				for(PRCommit commit : pr.getPrCommits()) {
					if(commit.getSha().equals(commitSha)){
						return commit;
					}
				}
				//commit is not present in the prCommit vector of pr yet
				try {
					for(RepositoryCommit rc : prService.getCommits(getRepo(), prNumber)) {
						if(rc.getSha().equals(commitSha)) {
							PRCommit newPRCommit = new PRCommit(rc.getSha(), rc.getCommit().getMessage());
							RepositoryCommit anotherCommit = this.commitService.getCommit(getRepo(), rc.getSha());
							
							for(CommitFile f : anotherCommit.getFiles()) {
								ChangedFile changedFile = getChangedFileData(new DataService(this.client), getRepo(), f);
								changedFile.setFileMasterUrl(createFilePathURL(changedFile.getName()));
								changedFile.setFileCommitUrl(createFilePathURLCommit(changedFile.getName(), commitSha));
								newPRCommit.addChangedFile(changedFile);
							}
							pr.addPrCommit(newPRCommit);
							return newPRCommit;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					throw new APIException("Error: Something went wrong while retrieving commits data.");
				}
			}
		}
		//if it reaches here, means pr isnt yet on the vector too
		addRepositoryPR(prNumber);
		return getRepositoryPRCommit(prNumber, commitSha);
	}
	
	public void addRepositoryPR(int prNumber) {
		try {
			for(PullRequest pr : this.prService.getPullRequests(getRepo(), "open")) {
				if(pr.getNumber() == prNumber) {
					PR newPR = new PR(pr.getTitle(), pr.getState(), pr.getId(), pr.getNumber());
					addPr(newPR);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new APIException("Error: Something went wrong while retrieving commits data.");
		}
	}
	
	
	/**
	* Get the array with all the existing pull requests
	*
	* @return prs
	*/
	public List<PR> getPrs() {
		return prs;
	}

	
	/**
	* Sets the array with pull requests
	* 
	* @param prs 
	*/
	public void setPrs(List<PR> prs) {
		this.prs = prs;
	}
	
	
	/**
	* Adds one pull request to the array with pull requests
	* 
	* @param pr 
	*/
	public void addPr (PR pr) {
		this.prs.add(pr);
	}
	
	
	/**
	* Get the name of the owner of the repository to scan
	*
	* @return repoToScanOwner
	*/
	public String getRepoToScanOwner() {
		return this.repoToScanOwner;
	}
	
	
	/**
	* Set the name of the owner of the repository to scan
	* 
	*/
	public void setRepoToScanOwner(String owner) {
		this.repoToScanOwner = owner;
	}
	
	
	/**
	* Get the name of the repository to scan
	*
	* @return repoToScanName
	*/
	public String getRepoToScanName() {
		return this.repoToScanName;
	}
	
	
	/**
	* Set the name of the repository to scan
	* 
	*/
	public void setRepoToScanName(String name) {
		this.repoToScanName = name;
	}
	
	
	/**
	* Get the GitHub client used to connect to the API
	*
	* @return client
	*/
	public GitHubClient getClient() {
		return client;
	}
	
	
	/**
	* Set the GitHub client used to connect to the API
	*
	*/
	public void setClient(GitHubClient client) {
		this.client = client;
	}
	
	
	/**
	* Get the repository service
	*
	* @return repoService
	*/
	public RepositoryService getRepoService() {
		return repoService;
	}
	
	
	/**
	* Set the repository service
	*
	*/
	public void setRepoService(RepositoryService repoService) {
		this.repoService = repoService;
	}
	
	
	/**
	* Get the commit service
	*
	* @return commitService
	*/
	public CommitService getCommitService() {
		return commitService;
	}
	
	
	/**
	* Set the commit service
	*
	*/
	public void setCommitService(CommitService commitService) {
		this.commitService = commitService;
	}
	
	
	/**
	* Get the pull request service
	*
	* @return prService
	*/
	public PullRequestService getPrService() {
		return prService;
	}
	
	
	/**
	* Set the pull request service
	*
	*/
	public void setPrService(PullRequestService prService) {
		this.prService = prService;
	}
	
	

	public Repository getRepo() {
		return repo;
	}

	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	
	

	public Map<String, String> getRepositoryJavaFiles() {
		return repositoryJavaFiles;
	}

	public void setRepositoryJavaFiles(Map<String, String> repositoryJavaFiles) {
		this.repositoryJavaFiles = repositoryJavaFiles;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public static void main(String[] args) throws Exception {
		
		//mvn exec:java -Dexec.args="eclipse egit-github"
		
		/*criar autenticaçao de utilizador*/
		API api = new API(args[0], args[1]);
		

		//api.printPubRepositoryRecursive(client, service, null, repoToScanOwner, repoToScanName);
				
		//api.getPubRepositoryPRs(client, service, prs, commits, repoToScanOwner, repoToScanName);
		//api.getCommitsOnly(api.getClient(), api.getRepoService(), api.getPrService(), api.getCommitService(), api.getRepoToScanOwner(), api.getRepoToScanName());
		api.printRepositoryPRsInfo();
		boolean reviewing = true;
		while(reviewing) {
			Scanner myInput = new Scanner( System.in );
		    int input;
		    System.out.print( "Enter Pull Request Number to Review (0 to exit): " );
		    input = myInput.nextInt();
			if(input != 0) {
				PrintStream originalOut = System.out;
				PR pullRequest = api.getRepositoryPR(input);
				for(PRCommit commit : pullRequest.getPrCommits()) {
					for(ChangedFile cf : commit.getChangedFiles()) {
						// Create a new file output stream.
				    	String outputFileName = cf.getName().replace("/", "");
				    	outputFileName = outputFileName.replace(".", "");
				    	File file = new File("manualTests" + File.separator + api.getRepoToScanName() + " repository" + File.separator + pullRequest.getPrTitle().replace(":", " ") + File.separator + commit.getSha() + File.separator + outputFileName + ".txt");
				    	file.getParentFile().mkdirs();
				    	file.createNewFile();
				        FileOutputStream fos = new FileOutputStream(file);

			           // Create new print stream for file.
			           PrintStream ps = new PrintStream(fos);

			           // Set file print stream.
			           System.setOut(ps);
			           System.out.println(cf.getName() + "\n\n\n##############################\n\n" + cf.getContent() + "\n\n\n##############################\n\n" + cf.getPatch());
					}
				}
				System.setOut(originalOut);
			} else {
				reviewing = false;
			}
		    
		}
	}

}