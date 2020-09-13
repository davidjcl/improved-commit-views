package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class ClassOrInterfaceDeclarationProcess {
		
private static final double IMPACT_VALUE = 0.8;
	
	public void process(ClassOrInterfaceDeclaration n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<Integer, String> auxChangedFilePatchWLines) {

		int classDeclarationBegin = n.getBegin().get().line;
		int classDeclarationEnd;
		if(n.getMembers().isNonEmpty()) {
			classDeclarationEnd = n.getMembers().get(0).getBegin().get().line - 1;
		}else {
			classDeclarationEnd = n.getEnd().get().line;
		}
		
		for(int key : changedFilePatchWLines.keySet()) {
			try {
				if (classDeclarationBegin <= key && key <= classDeclarationEnd) {
					double bonusImpact = 0;
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node declaration content [Lines = " + classDeclarationBegin + " to " + classDeclarationEnd + "]: " + n.removeComment().toString().replaceFirst("(?s)\\R.*", "") + "\n");
					
            		//System.out.println(n.getClass().getSimpleName() + "  -------AQUI------- " + n.removeComment().toString().replaceFirst("(?s)\\R.*", ""));
            		if(isNewClass(n, auxChangedFilePatchWLines)) {
            			bonusImpact = calculateBonus(n);
            			//System.out.println("This is a whole new class! BONUS IMPACT PLEASE!\n");
            		} else {
            			//System.out.println("Not a whole new class! Just changed the declaration. Normal Impact!\n");
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
	
	
	public boolean isNewClass(ClassOrInterfaceDeclaration n, Map<Integer, String> auxChangedFilePatchWLines) {
		
		int classBegin = n.getBegin().get().line;
		int classEnd = n.getEnd().get().line;
		
		for(int i = classBegin; i <= classEnd; i++) {
			if(!(auxChangedFilePatchWLines.containsKey(i))) {
				return false;
			}
		}
		return true;
		
	}
	
	public double calculateBonus(ClassOrInterfaceDeclaration n) {
		double bonus = 0;
		
		if(n.getMembers().isNonEmpty()) {
			double classBegin = n.getBegin().get().line;
			double classEnd = n.getEnd().get().line;
			double classLines = classEnd - classBegin;
			double classMethodsNumber = n.getMethods().size();
			double classConstructorsNumber = n.getConstructors().size();
			//System.out.println("ClassBegin: " + classBegin + " -- ClassEnd: " + classEnd + " -- NrMethods: " + classMethodsNumber + " -- NrConstructors: " + classConstructorsNumber);
			bonus = classLines / (1 + classMethodsNumber + classConstructorsNumber) / 10;
		}
		
		return bonus;
	}
}
