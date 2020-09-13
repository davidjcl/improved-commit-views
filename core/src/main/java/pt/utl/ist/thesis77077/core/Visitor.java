package pt.utl.ist.thesis77077.core;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class Visitor {
		
	
	public void process(ChangedFile changedFile, Map<Integer, String> changedFilePatchWLines, Map<String, String> repositoryJavaFiles) {
		CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser parser = new JavaParser();
        parser.getParserConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit compilationUnit = parser.parse(changedFile.getContent()).getResult().get();
        Map<Integer, String> auxChangedFilePatchWLines = new HashMap<Integer, String> (changedFilePatchWLines);
        SmellChecker smellChecker = new SmellChecker();
        
        new VoidVisitorAdapter<Object>() {
        	
        	@Override
			public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        		super.visit(n, arg);
        		ClassOrInterfaceDeclarationProcess clintpr = new ClassOrInterfaceDeclarationProcess();
        		clintpr.process(n, changedFile, changedFilePatchWLines, auxChangedFilePatchWLines);
			}
        	
        	@Override
			public void visit(ConstructorDeclaration n, Object arg) {
        		super.visit(n, arg);
        		ConstructorDeclarationProcess constpr = new ConstructorDeclarationProcess();
        		constpr.process(n, changedFile, changedFilePatchWLines, auxChangedFilePatchWLines);
			}
        	
			@Override
			public void visit(MethodDeclaration n, Object arg) {
				super.visit(n, arg);
				MethodDeclarationProcess mdpr = new MethodDeclarationProcess();
				mdpr.process(n, changedFile, changedFilePatchWLines, auxChangedFilePatchWLines);
			}
			
			@Override
			public  void visit(ExpressionStmt n, Object arg) {
				super.visit(n, arg);
				ExpressionStmtProcessRefined espr = new ExpressionStmtProcessRefined();
				espr.process(n, changedFile, changedFilePatchWLines, repositoryJavaFiles);
			}
			
			@Override
			public void visit(FieldDeclaration n, Object arg) {
				super.visit(n, arg);
				FieldDeclarationProcess fdpr = new FieldDeclarationProcess();
				fdpr.process(n, changedFile, changedFilePatchWLines);
			}
			
			@Override
			public void visit(IfStmt n, Object arg) {
				super.visit(n, arg);
				IfStmtProcess ifspr = new IfStmtProcess();
				ifspr.process(n, changedFile, changedFilePatchWLines, repositoryJavaFiles);
			}
			
			@Override
			public void visit(ForEachStmt n, Object arg) {
				super.visit(n, arg);
				ForEachStmtProcess foreachpr = new ForEachStmtProcess();
				foreachpr.process(n, changedFile, changedFilePatchWLines, repositoryJavaFiles);
			}
			
			@Override
			public void visit(ForStmt n, Object arg) {
				super.visit(n, arg);
				ForStmtProcess forpr = new ForStmtProcess();
				forpr.process(n, changedFile, changedFilePatchWLines, repositoryJavaFiles);
			}
			
			@Override
			public void visit(WhileStmt n, Object arg) {
				super.visit(n, arg);
				WhileStmtProcess whilepr = new WhileStmtProcess();
				whilepr.process(n, changedFile, changedFilePatchWLines, repositoryJavaFiles);
			}
			
			@Override
			public void visit(ReturnStmt n, Object arg) {
				super.visit(n, arg);
				ReturnStmtProcess returnpr = new ReturnStmtProcess();
				returnpr.process(n, changedFile, changedFilePatchWLines, repositoryJavaFiles);
			}
			
			@Override
			public void visit(TryStmt n, Object arg) {
				super.visit(n, arg);
				TryStmtProcess trypr = new TryStmtProcess();
				trypr.process(n, changedFile, changedFilePatchWLines, repositoryJavaFiles);
			}
			
			
			
		}.visit(compilationUnit, null);
        //System.out.println("Changed file impact value is: " + changedFile.getImpactValue());
        smellChecker.process(changedFile, auxChangedFilePatchWLines);
        //changedFile.getRelatedJavaFiles().forEach((k,v)->System.out.println("File : " + k));
    }
	
}
