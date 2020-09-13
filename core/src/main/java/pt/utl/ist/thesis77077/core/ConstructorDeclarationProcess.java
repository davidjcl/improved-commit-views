package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.body.ConstructorDeclaration;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class ConstructorDeclarationProcess {
	
	private static final double IMPACT_VALUE = 0.6;
	
	public void process(ConstructorDeclaration n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<Integer, String> auxChangedFilePatchWLines) {

		int constructorDeclarationBegin = n.getBegin().get().line;
		int constructorDeclarationEnd;
		if(!(n.getBody().isEmpty())) {
			constructorDeclarationEnd = n.getBody().getBegin().get().line;
		}else {
			constructorDeclarationEnd = n.getEnd().get().line;
		}
		
		for(int key : changedFilePatchWLines.keySet()) {
			try {
				if (constructorDeclarationBegin <= key && key <= constructorDeclarationEnd) {
					double bonusImpact = 0;
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node declaration content [Lines = " + constructorDeclarationBegin + " to " + constructorDeclarationEnd + "]: " + n.removeComment().toString().replaceFirst("(?s)\\R.*", "") + "\n");
					
            		//System.out.println(n.getClass().getSimpleName() + "  -------AQUI------- " + n.removeComment().toString().replaceFirst("(?s)\\R.*", ""));
            		if(isNewConstructor(n, auxChangedFilePatchWLines)) {
            			bonusImpact = calculateBonus(n);
            			//System.out.println("This is a whole new constructor! BONUS IMPACT PLEASE!\n");
            		} else {
            			//System.out.println("Not a whole new constructor! Just changed the declaration. Normal Impact!\n");
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
	
	
	public boolean isNewConstructor(ConstructorDeclaration n, Map<Integer, String> auxChangedFilePatchWLines) {
		
		int constructorDeclarationBegin = n.getBegin().get().line;
		int constructorDeclarationEnd = n.getEnd().get().line;
		
		for(int i = constructorDeclarationBegin; i <= constructorDeclarationEnd; i++) {
			if(!(auxChangedFilePatchWLines.containsKey(i))) {
				return false;
			}
		}
		return true;
		
	}
	
	public double calculateBonus(ConstructorDeclaration n) {
		double constructorBodyLines = 0;
		if(!(n.getBody().isEmpty())) {
			double constructorBodyBegin = n.getBody().getBegin().get().line;
			double constructorBodyEnd = n.getBody().getEnd().get().line;
			
			if(constructorBodyBegin == n.getBegin().get().line) {
				constructorBodyBegin++;
			}

			constructorBodyLines = constructorBodyEnd - constructorBodyBegin;
		}
		double bonus = constructorBodyLines / 10;
		return bonus;
	}
	

}
