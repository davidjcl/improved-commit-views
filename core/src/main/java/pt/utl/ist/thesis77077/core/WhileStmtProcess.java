package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class WhileStmtProcess {

	public void process(WhileStmt n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<String, String> repositoryJavaFiles) {
		
		int conditionBegin = n.getCondition().getBegin().get().line;
		int conditionEnd = n.getCondition().getEnd().get().line;
		
		for(int key : changedFilePatchWLines.keySet()) {
			try {
				//System.out.println("If condition: " + n.getCondition().toString() + "\t\t----Printing If condition line range: " + n.getCondition().getRange().toString() + "\t\t\n");
				if (conditionBegin <= key && key <= conditionEnd) {
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node content: " + n.removeComment().toString().replaceFirst("(?s)\\R.*", "") + "\n");
					//System.out.println("Node condition content [Lines =  " + conditionBegin + " to " + conditionEnd + "]: " + n.getCondition().toString() + "\n");
					
					if(n.getCondition().isBinaryExpr()) {
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
									
						}.visit(n.getCondition().asBinaryExpr(), null);
					}
					
					if(n.getCondition().isMethodCallExpr()) {
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
									
						}.visit(n.getCondition().asMethodCallExpr(), null);
					}
					
					if(n.getCondition().isUnaryExpr()) {
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
									
						}.visit(n.getCondition().asUnaryExpr(), null);
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
