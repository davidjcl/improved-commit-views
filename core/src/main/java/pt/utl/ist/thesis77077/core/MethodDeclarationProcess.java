package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.body.MethodDeclaration;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class MethodDeclarationProcess {
	
	private static final double IMPACT_VALUE = 0.6;
	
	public void process(MethodDeclaration n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<Integer, String> auxChangedFilePatchWLines) {
		
		int methodDeclarationBegin = n.getBegin().get().line;
		int methodDeclarationEnd;
		if(n.getBody().isPresent()) {
			methodDeclarationEnd = n.getBody().get().getBegin().get().line;
		}else {
			methodDeclarationEnd = n.getEnd().get().line;
		}
		
		for(int key : changedFilePatchWLines.keySet()) {
			try {
				if (methodDeclarationBegin <= key && key <= methodDeclarationEnd) {
					double bonusImpact = 0;
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node declaration content [Lines = " + methodDeclarationBegin + " to " + methodDeclarationEnd + "]: " + n.removeComment().toString().replaceFirst("(?s)\\R.*", "") + "\n");
					
            		//System.out.println(n.getClass().getSimpleName() + "  -------AQUI------- " + n.toString().replaceFirst("(?s)\\R.*", ""));
            		if(isNewMethod(n, auxChangedFilePatchWLines)) {
            			bonusImpact = calculateBonus(n);
            			//System.out.println("This is a whole new method! BONUS IMPACT PLEASE!\n");
            		} else {
            			//System.out.println("Not a whole new method! Just changed the declaration. Normal Impact!\n");
            		}
            		
            		changedFilePatchWLines.remove(key);
            		changedFile.incrementImpactValue(IMPACT_VALUE + bonusImpact);
            		//System.out.println("Case found! Impact Value incremented by: " + IMPACT_VALUE + " plus an additional " + bonusImpact + " bonus!\n");
            		//System.out.println("##################### END of patch line analysis #####################\n");
            		break;
            	}
			} catch (NoSuchElementException e) {
				continue;
			}
			
		}
	}
	
	
	public boolean isNewMethod(MethodDeclaration n, Map<Integer, String> auxChangedFilePatchWLines) {
		
		int methodBegin = n.getBegin().get().line;
		int methodEnd = n.getEnd().get().line;
		
		for(int i = methodBegin; i <= methodEnd; i++) {
			if(!(auxChangedFilePatchWLines.containsKey(i))) {
				return false;
			}
		}
		return true;
		
	}
	
	public double calculateBonus(MethodDeclaration n) {
		double methodBodyLines = 0;
		if(n.getBody().isPresent()) {
			double methodBodyBegin = n.getBody().get().getBegin().get().line;
			double methodBodyEnd = n.getBody().get().getEnd().get().line;
			
			if(methodBodyBegin == n.getBegin().get().line) {
				methodBodyBegin++;
			}
			
			methodBodyLines = methodBodyEnd - methodBodyBegin;
		}
		double bonus = methodBodyLines / 10;
		return bonus;
	}
	
}
