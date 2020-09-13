package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class ForEachStmtProcess {
	
	public void process(ForEachStmt n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<String, String> repositoryJavaFiles) {
		
		int forLoopBegin = n.getBegin().get().line;
		int forLoopEnd = n.getBody().getBegin().get().line;
		
		for(int key : changedFilePatchWLines.keySet()) {
			try {
				if (forLoopBegin <= key && key <= forLoopEnd) {
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node loop content [Lines =  " + forLoopBegin + " to " + forLoopEnd + "]: " + n.removeComment().toString().replaceFirst("(?s)\\R.*", "") + "\n");
					//System.out.println("Iterator class simple name: " + n.getIterable().getClass().getSimpleName());
					
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
								
					}.visit(n.getVariable(), null);
					
					if(n.getIterable().isMethodCallExpr()) {
						
						new VoidVisitorAdapter<Object>() {
							
							double valueFromProcess = 0;
							
							@Override
							public  void visit(MethodCallExpr n, Object arg) {
								super.visit(n, arg);
								//System.out.println(n.getClass().getSimpleName() + "  -------AQUI------- " + n.toString());
								MethodCallExprProcess methodCall = new MethodCallExprProcess();
								valueFromProcess = methodCall.process(n, arg, repositoryJavaFiles, changedFile);
								changedFile.incrementImpactValue(valueFromProcess);
								//System.out.println("Case found! Impact Value incremented by: " + valueFromProcess + "\n");
								
							}
									
						}.visit(n.getIterable().asMethodCallExpr(), null);
						
					}
					
					
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
