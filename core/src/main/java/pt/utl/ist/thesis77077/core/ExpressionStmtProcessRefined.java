package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class ExpressionStmtProcessRefined {
	
	public void process(ExpressionStmt n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<String, String> repositoryJavaFiles) {
		
		int statementBegin = n.getBegin().get().line;
		int statementEnd = n.getEnd().get().line;
		
		for(int key : changedFilePatchWLines.keySet()) {
			try {
				if (statementBegin <= key && key <= statementEnd) {
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node content [Lines = " + statementBegin + " to " + statementEnd + "]: " + n.removeComment().toString() + "\n");
					new VoidVisitorAdapter<Object>() {
						
						double valueFromProcess = 0;
						
						@Override
						public void visit(VariableDeclarationExpr n, Object arg) {
							super.visit(n, arg);
							//System.out.println(n.getClass().getSimpleName() + "  -------AQUI------- " + n.toString());
							VariableDeclarationExprProcess variableDeclaration = new VariableDeclarationExprProcess();
							valueFromProcess = variableDeclaration.process(n, arg, repositoryJavaFiles, changedFile);
							changedFile.incrementImpactValue(valueFromProcess);
							//System.out.println("Case found! Impact Value incremented by: " + valueFromProcess + "\n");
							
						}
				
						@Override
						public  void visit(MethodCallExpr n, Object arg) {
							super.visit(n, arg);
							//System.out.println(n.getClass().getSimpleName() + "  -------AQUI------- " + n.toString());
							MethodCallExprProcess methodCall = new MethodCallExprProcess();
							valueFromProcess = methodCall.process(n, arg, repositoryJavaFiles, changedFile);
							changedFile.incrementImpactValue(valueFromProcess);
							//System.out.println("Case found! Impact Value incremented by: " + valueFromProcess + "\n");
							
						}
						
						@Override
						public void visit(LambdaExpr n, Object arg) {
							//this visit is empty, so methodcalls and variabledeclarations are not counted twice
							//if they are inside a lambda expression. that is, the lambda expression block is analysed only once
							//from the context of the big statement itself
						}
								
					}.visit(n, null);
					
            		changedFilePatchWLines.remove(key);
            		//System.out.println("##################### END of patch line analysis #####################\n");
            		break;
            	}
			} catch (NoSuchElementException e) {
				continue;
			}
			
		}
		

	}
	
	
}
