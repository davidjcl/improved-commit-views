package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.body.FieldDeclaration;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class FieldDeclarationProcess {
	
	private static final double IMPACT_VALUE = 0.5;
	
	public void process(FieldDeclaration n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines) {
		
		int statementBegin = n.getBegin().get().line;
		int statementEnd = n.getEnd().get().line;
		
		for(int key : changedFilePatchWLines.keySet()) {
			
			try {
				if (statementBegin <= key && key <= statementEnd) {
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node content [Lines = " + statementBegin + " to " + statementEnd + "]: " + n.removeComment().toString() + "\n");
            		//System.out.println(n.getClass().getSimpleName() + "  -------AQUI------- " + n.toString().replaceFirst("(?s)\\R.*", ""));
           
            		changedFilePatchWLines.remove(key);
            		changedFile.incrementImpactValue(IMPACT_VALUE);
            		//System.out.println("Case found! Impact Value incremented by: " + IMPACT_VALUE + "\n");
            		//System.out.println("##################### END of patch line analysis #####################\n");
            		break;
            	}
			} catch (NoSuchElementException e) {
				continue;
			}
			
		}
	}

}
