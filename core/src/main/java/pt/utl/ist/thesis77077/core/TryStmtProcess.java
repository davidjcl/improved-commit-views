package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class TryStmtProcess {
		
public void process(TryStmt n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<String, String> repositoryJavaFiles) {
		
		int tryStmtBegin = n.getBegin().get().line;
		int tryStmtEnd = n.getTryBlock().getBegin().get().line;
		
		for(int key : changedFilePatchWLines.keySet()) {
			try {
				if (tryStmtBegin <= key && key <= tryStmtEnd) {
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node content [Lines = " + tryStmtBegin + " to " + tryStmtEnd + "]: " + n.getResources().toString() + "\n");
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
								
					}.visit(n.getResources(), null);
					
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
