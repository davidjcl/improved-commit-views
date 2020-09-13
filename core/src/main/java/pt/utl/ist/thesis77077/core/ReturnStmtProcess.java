package pt.utl.ist.thesis77077.core;

import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class ReturnStmtProcess {

public void process(ReturnStmt n, ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<String, String> repositoryJavaFiles) {
		
		int returnBegin = n.getBegin().get().line;
		int returnEnd = n.getEnd().get().line;

		for(int key : changedFilePatchWLines.keySet()) {
			try {
				if (returnBegin <= key && key <= returnEnd) {
					//System.out.println("##################### Patch line being analysed -> \t" + "[Line = " + key + "]\t\t\t" + changedFilePatchWLines.get(key) + "\n");
					//System.out.println("Node content [Lines = " + returnBegin + " to " + returnEnd + "]: " + n.removeComment().toString() + "\n");
					
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
						
						@Override
						public void visit(LambdaExpr n, Object arg) {
							
						}
						
						@Override
						public void visit(ObjectCreationExpr n, Object arg) {
							
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
