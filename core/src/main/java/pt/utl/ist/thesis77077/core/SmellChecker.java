package pt.utl.ist.thesis77077.core;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean.OutputStreamOptions;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import org.apache.commons.lang3.SystemUtils;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class SmellChecker {
	
	private Checker checker;
	
	public SmellChecker() {
		
		try {
			this.checker = new Checker();
			this.checker.setModuleClassLoader(Checker.class.getClassLoader());
			this.checker.configure(ConfigurationLoader.loadConfiguration(".." + File.separator + "core" + File.separator + "styleConventions" + File.separator + "google_checks.xml", 
																		new PropertiesExpander(System.getProperties()), ConfigurationLoader.IgnoredModulesOptions.OMIT));
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(".." + File.separator + "core" + File.separator + "temp" + File.separator + "processout.txt"));
			this.checker.addListener(new DefaultLogger(output, OutputStreamOptions.NONE));
		
		}catch (CheckstyleException | IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong while creating the SmellChecker.");
		} 
		
	}
	
	public void findChangedFileSmells(ChangedFile cf) {
		try {
			File file = new File(".." + File.separator + "core" + File.separator + "temp" + File.separator + "processfile.java" );
			file.getParentFile().mkdirs();
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(cf.getContent());
			bw.close();
			List<File> fileToProcess = new ArrayList<File>();
			fileToProcess.add(file);
		
			
//			String[] args = new String[] {"-d","../core/temp/processfile.java","-R", "pmd/quickstart.xml", "-language", "java", "-version", "12", "-f", "text", "-r", "../core/temp/processoutpmd.txt"};
//			pmd.run(args);
			
			String cmdPmdCall = findPmdProcessCall();
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(cmdPmdCall);
			pr.waitFor();
			
			this.checker.process(fileToProcess);
			
			this.checker.destroy();
			
		} catch (CheckstyleException | IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong while trying to find smells on the changed file.");
		} 
	}
	
	private void processSmellsOutput(ChangedFile cf, Map<Integer, String> auxChangedFilePatchWLines) {
		List<String> outputFiles = new ArrayList<String>();
		outputFiles.add(".." + File.separator + "core" + File.separator + "temp" + File.separator + "processout.txt");
		outputFiles.add(".." + File.separator + "core" + File.separator + "temp" + File.separator + "processoutpmd.txt");
		
		for(String file: outputFiles) {
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			    	String[] splitLine= line.split(".java:");//splits into 2 elements, the path of the file and the smell message
			    	if(splitLine.length == 2) {
			    		String smellLineString = splitLine[1].split(":")[0]; //splits the message into line, column and message, and selects the line, first element.
				    	int smellLine = Integer.parseInt(smellLineString);
				    
				    	if(auxChangedFilePatchWLines.keySet().contains(smellLine) && !(splitLine[1].contains("The name of the outer type and the file do not match. [OuterTypeFilename]"))) {
				    		if(file.endsWith("processout.txt")) {
				    			cf.getPatchSmells().add(splitLine[1]);
				    		} else if (file.endsWith("processoutpmd.txt")) {
				    			cf.getPatchSmellsPmd().add(splitLine[1]);
				    		}
				    	}
			    	}
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Something went wrong while trying to process the smells of the changed file.");
			}
		}

	}
	
	private String findPmdProcessCall() {
		String command = "";
		if(SystemUtils.IS_OS_WINDOWS) {
			command = "cmd.exe /c cd ../core/pmd/bin && pmd.bat -d ../../temp/processfile.java -R rulesets/java/quickstart.xml -f text -r ../../temp/processoutpmd.txt";
		
		} else if(SystemUtils.IS_OS_UNIX) {
			command = "bash ../core/pmd/bin/run.sh pmd -d ../core/temp/processfile.java -R rulesets/java/quickstart.xml -f text -r ../core/temp/processoutpmd.txt";
		}
		return command;
	}
	
	public void process(ChangedFile cf, Map<Integer, String> auxChangedFilePatchWLines) {
		findChangedFileSmells(cf);
		processSmellsOutput(cf, auxChangedFilePatchWLines);
	}
}
