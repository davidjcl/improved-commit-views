package pt.utl.ist.thesis77077.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pt.utl.ist.thesis77077.api.API;
import pt.utl.ist.thesis77077.api.ChangedFile;
import pt.utl.ist.thesis77077.api.PR;
import pt.utl.ist.thesis77077.api.PRCommit;

public class Core {
	
	private List<PR> prs;
	private List<PRCommit> commits;  //this is just for core module development purposes
	private List<ChangedFile> javaFiles;
	private List<ChangedFile> notJavaFiles;
	private Visitor visitor;
	
	public Core() {
		this.prs = new ArrayList<PR>();
		this.commits = new ArrayList<PRCommit>();
		this.javaFiles = new ArrayList<ChangedFile>();
		this.notJavaFiles = new ArrayList<ChangedFile>();
		this.visitor = new Visitor();
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
	* Set the array with all the existing pull requests
	*
	*/
	public void setPrs(List<PR> prs) {
		this.prs = prs;
	}
	
	public void addPr(PR pullRequest) {
		prs.add(pullRequest);
	}


	public List<PRCommit> getCommits() {
		return commits;
	}


	public void setCommits(List<PRCommit> commits) {
		this.commits = commits;
	}
	
	
	/**
	* Get the array with all the changed java files of a commit
	*
	* @return javaFiles
	*/
	public List<ChangedFile> getJavaFiles() {
		return this.javaFiles;
	}
	
	
	/**
	* Adds a java file to its corresponding list
	* 
	* @param javaFile
	*
	*/
	public void addJavaFile(ChangedFile javaFile) {
		this.javaFiles.add(javaFile);
	}
	
	
	/**
	* Get the array with all the changed non java files of a commit
	*
	* @return notJavaFiles
	*/
	public List<ChangedFile> getNotJavaFiles() {
		return this.notJavaFiles;
	}
	
	
	/**
	* Adds a non java file to its corresponding list
	* 
	* @param notJavaFile
	*
	*/
	public void addNotJavaFile(ChangedFile notJavaFile) {
		this.notJavaFiles.add(notJavaFile);
	}
	
	
	/**
	* Filters java files and non java files into two different lists
	*
	* @param commit
	* 
	*/
	public void filterJavaFiles(PRCommit commit) {
		for(ChangedFile cf : commit.getChangedFiles()) {
			if(cf.getName().endsWith(".java")) {
				addJavaFile(cf);
			} else {
				addNotJavaFile(cf);
			}
		}
	}
	
	
	/**
	* Filters the additions of the patch of a changed file and returns them, filters out imports
	*
	* @param changedFilePatch
	* 
	* @return dataToProcess
	*/
	public ArrayList<String> filterChangedFilePatchAdditions(String changedFilePatch) {
		
		String[] splitData = changedFilePatch.split("(?=\\n\\+)|(?=\\n\\-)|\n"); //splits the (patch) by "\n+" or "\n-" while keeping them. removes "\n"
		ArrayList<String> dataToProcess = new ArrayList<String>();
		
		for(String s : splitData) {
			if(s.startsWith("\n+") && !(s.startsWith("\n+import"))) {
				s = s.replace("\n+", ""); //removes the "+" sign of patch lines to compare with source file content
				dataToProcess.add(s);
			}
		}
		return dataToProcess;
	}
	
	
	
	public Map<Integer, String> addChangedPatchAdditionsLines(String changedFilePatch) {
	     
        String[] splitData = changedFilePatch.split("(?=\\n\\+)|(?=\\n\\-)|\n"); //splits the (patch) by "\n+" or "\n-" while keeping them. removes "\n"
        Map<Integer, String> dataToProcess = new HashMap<Integer, String>();
        int lineNr = 0;
        String lineNumber ="";
        boolean b = false;
       
        for(String s : splitData) {
            if(s.startsWith("\n+")) {
                s = s.replace("\n+", ""); //removes the "+" sign of patch lines to compare with source file content
                dataToProcess.put(lineNr, s);
            }
            if(!(s.startsWith("\n-"))) //only increments if line wasn't deleted
                lineNr++;
            if(s.startsWith("@@ -")) {
                for (int i = 0, n = s.length(); i < n; i++) {
                    char c = s.charAt(i);
                    if(c == ',' || c == ' ') {
                        b = false;
                    }
                    if(b == true) {
                        lineNumber += c;
                    }
                    if(c == '+') {
                        b = true;
                    }
                }
                lineNr = Integer.parseInt(lineNumber);
                lineNumber = "";
            }
        }
 
        return dataToProcess;
    }
	
	
	public void processCommit(API api, PRCommit prCommit) {
		javaFiles.clear();
		notJavaFiles.clear();
		if(prCommit.isProcessed()) {
			return;
		}else {
			filterJavaFiles(prCommit);
			for (ChangedFile cf : getJavaFiles()) {
				this.visitor.process(cf, addChangedPatchAdditionsLines(cf.getPatch()), api.getRepositoryJavaFiles());
			}
			prCommit.setProcessStatus(true);
		}
		
	}
	
	public void sortCommitByDescendingImpact (PRCommit prCommit) {
		Collections.sort(prCommit.getChangedFiles());
	}
	
	
	public void runOverPR(API api, Core core, Visitor visitor, int prNumber) {
		PR prToWork = null;
		for(PR pullRequest : core.getPrs()) {
			if(pullRequest.getNumber() == prNumber) {
				prToWork = pullRequest;
				break;
			}
		}
		
		if(prToWork == null) {
			prToWork = api.getRepositoryPR(prNumber);
			if(prToWork != null) {
				core.addPr(prToWork);
			}else {
				System.out.println("That Pull Request does not exist!");
				return;
			}
		}
		
		for(PRCommit commit : prToWork.getPrCommits()) {
			javaFiles.clear();
			notJavaFiles.clear();
			core.filterJavaFiles(commit);
			
			PrintStream originalOut = System.out;
	        String commitID = commit.getSha();
		    for(ChangedFile cf : core.getJavaFiles()){
		    	
		    	try {
		    		// Create a new file output stream.
			    	String outputFileName = cf.getName().replace("/", "");
			    	outputFileName = outputFileName.replace(".", "");
			    	File file = new File("manualTests" + File.separator + api.getRepoToScanName() + " repository" + File.separator + prToWork.getPrTitle().replace(":", " ") + File.separator + commitID + File.separator + outputFileName + ".txt");
			    	file.getParentFile().mkdirs();
			    	file.createNewFile();
			        FileOutputStream fos = new FileOutputStream(file);

		           // Create new print stream for file.
		           PrintStream ps = new PrintStream(fos);

		           // Set file print stream.
		           System.setOut(ps);
		          
		           visitor.process(cf, core.addChangedPatchAdditionsLines(cf.getPatch()), api.getRepositoryJavaFiles());
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	} catch(NullPointerException e) {
		    		e.printStackTrace(); //this happens when the api fails to retrieve the patch and changedfilepatch is null.
		    	}
		    	
		    	
		    }
		    printCommitFilesResults(api.getRepoToScanName(), prToWork.getPrTitle(), commit);
	        System.setOut(originalOut);
		}
	}
	
	
	public void printCommitFilesResults(String repoName, String prTitle, PRCommit prCommit) {
		PrintStream originalOut = System.out;
		String commitID = prCommit.getSha();
		
		try {
    		// Create a new file output stream.
	    	String outputFileName = "Commit_File_Order_Results";
	    	outputFileName = outputFileName.replace(".", "");
	    	File file = new File("manualTests" + File.separator + repoName + " repository" + File.separator + prTitle.replace(":", " ") + File.separator + commitID + File.separator + outputFileName + ".txt");
	    	file.getParentFile().mkdirs();
	    	file.createNewFile();
	        FileOutputStream fos = new FileOutputStream(file);

           // Create new print stream for file.
           PrintStream ps = new PrintStream(fos);

           // Set file print stream.
           System.setOut(ps);
           for(ChangedFile cf : prCommit.getChangedFiles()) {
        	   System.out.println("File Name: " + cf.getName() + "\t|||||\t Impact Value: " + cf.getImpactValue());
           }
          
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
		System.setOut(originalOut);
	}
	
	public void runTreeWalkerOverPR(API api, Core core, TreeWalkerTest treeWalker, int prNumber) {
		PR prToWork = null;
		for(PR pullRequest : core.getPrs()) {
			if(pullRequest.getNumber() == prNumber) {
				prToWork = pullRequest;
			}
		}
		
		if(prToWork == null) {
			prToWork = api.getRepositoryPR(prNumber);
			if(prToWork != null) {
				core.addPr(prToWork);
			}else {
				System.out.println("That Pull Request does not exist!");
				return;
			}
		}
		
		for(PRCommit commit : prToWork.getPrCommits()) {
			javaFiles.clear();
			notJavaFiles.clear();
			core.filterJavaFiles(commit);
			
			PrintStream originalOut = System.out;
	        String commitID = commit.getSha();
		    for(ChangedFile cf : core.getJavaFiles()){
		    	
		    	try {
		    		// Create a new file output stream.
			    	String outputFileName = cf.getName().replace("/", "");
			    	outputFileName = outputFileName.replace(".", "");
			    	File file = new File("manualTests" + File.separator + "treewalker" + File.separator + api.getRepoToScanName() + " repository" + File.separator + prToWork.getPrTitle().replace(":", " ") + File.separator + commitID + File.separator + outputFileName + ".txt");
			    	file.getParentFile().mkdirs();
			    	file.createNewFile();
			        FileOutputStream fos = new FileOutputStream(file);

		           // Create new print stream for file.
		           PrintStream ps = new PrintStream(fos);

		           // Set file print stream.
		           System.setOut(ps);
		          
		           treeWalker.run(cf.getContent());
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	} catch(NullPointerException e) {
		    		e.printStackTrace(); //this happens when the api fails to retrieve the patch and changedfilepatch is null.
		    	}
		    	
		    	
		    }
	        System.setOut(originalOut);
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		//mvn exec:java -Dexec.args="davidfbpereira sec18"
		Core core = new Core();

		API api = new API(args[0], args[1]);
		api.printRepositoryPRsInfo();
		
	
		Visitor visitor = new Visitor();
		TreeWalkerTest treeWalker = new TreeWalkerTest();
		
		boolean reviewing = true;
		while(reviewing) {
			Scanner myInput = new Scanner( System.in );
		    int input;
		    System.out.print( "Enter Pull Request Number to Review (0 to exit): " );
		    input = myInput.nextInt();
			if(input != 0) {
				core.runOverPR(api, core, visitor, input);
				//core.runTreeWalkerOverPR(api, core, treeWalker, input);
			} else {
				reviewing = false;
			}
		    
		}
		
		
		
	}
	
}