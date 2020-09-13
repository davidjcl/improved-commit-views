package pt.utl.ist.thesis77077.core;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedType;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class MethodCallExprProcess {
	
private static final double IMPACT_VALUE_OTHER_PROJ_OBJECTS_SCOPE = 1;
private static final double IMPACT_VALUE_CHANGED_CLASS_SCOPE = 0.7;
private static final double IMPACT_VALUE_JAVA_LANG_LIBS_SCOPE = 0.4;
	
	public double process(MethodCallExpr n, Object arg, Map<String, String> repositoryJavaFiles, ChangedFile changedFile) {
		
		CompilationUnit methodCompilationUnit = n.findCompilationUnit().get();
		if(n.getScope().isEmpty()) {
			//System.out.println("Scope is empty! MethodCall belongs to this class");
			//System.out.println(n.getScope().toString());
			
			//System.out.println("----------------\n");
			return IMPACT_VALUE_CHANGED_CLASS_SCOPE;
		}else {
			try {
				
				ResolvedType resolvedType = n.getScope().get().calculateResolvedType();
				if(isCallFromParsedClass(methodCompilationUnit, resolvedType)) {
					//System.out.println("Type was resolved BUT IT IS FROM PARSED CLASS -> " + resolvedType.describe() + "\t-----------\n");
					return IMPACT_VALUE_CHANGED_CLASS_SCOPE;
				}
				
				//System.out.println("Type was resolved, it is from Java Lang or running library!  -> " + resolvedType.describe() + "\t-----------\n");

				return IMPACT_VALUE_JAVA_LANG_LIBS_SCOPE;
				
			} catch (UnsolvedSymbolException  e) {
				
				String className = e.getName().replace("Solving ", "");
				//System.out.println("Not Resolved by Java Symbol, Type/Class of the caller object -> " + className + "\t--------------------------\n");
				if(isClassFromRepositoryProject(className, repositoryJavaFiles, changedFile, methodCompilationUnit)) {
					return IMPACT_VALUE_OTHER_PROJ_OBJECTS_SCOPE;
				}else {
					return IMPACT_VALUE_JAVA_LANG_LIBS_SCOPE;
				}
				
			} catch(Exception e) {
				//e.printStackTrace();
				//System.out.println(e.getMessage());
			}
		}
		
		//System.out.println(n.getScope().toString());
		
		//System.out.println("----------------\n");
		return IMPACT_VALUE_OTHER_PROJ_OBJECTS_SCOPE;
	}
	
	public boolean isClassFromRepositoryProject(String className, Map<String, String> repositoryJavaFiles, ChangedFile changedFile, CompilationUnit methodCompilationUnit) {
		String callerObjectPath = findClassPackagePath(methodCompilationUnit, changedFile, className);
		boolean classesFound = false;
		
		for(String javaFileName : changedFile.getRelatedJavaFiles().keySet()) {
			if(javaFileName.endsWith(callerObjectPath)) {
				return true;
			}
		}
		
		for(String javaFileName : repositoryJavaFiles.keySet()) {
			if(javaFileName.endsWith(callerObjectPath)) {
				//System.out.println("YES! " + className + " belongs to the project. File path is: " + javaFileName);
				changedFile.getRelatedJavaFiles().putIfAbsent(javaFileName, repositoryJavaFiles.get(javaFileName));
				classesFound = true;
			}
		}
		return classesFound;
		
	}
	
	public boolean isCallFromParsedClass (CompilationUnit methodCompilationUnit, ResolvedType resolvedType) {
		
		String packageDeclarationString = methodCompilationUnit.findFirst(PackageDeclaration.class).get().getNameAsString();
		String classNameString = methodCompilationUnit.findFirst(ClassOrInterfaceDeclaration.class).get().getNameAsString();
		String classPackage = packageDeclarationString + "." + classNameString;
		
		if(classPackage.equals(resolvedType.describe())) {
			return true;
		}
		return false;
	}
	
	public String findClassPackagePath(CompilationUnit methodCompilationUnit, ChangedFile changedFile, String className) {
		List<ImportDeclaration> changedFileImports = methodCompilationUnit.findAll(ImportDeclaration.class);
		
		//checking on imports if the class is there and returns the path of the package if true.
		for(ImportDeclaration importDeclaration : changedFileImports) {
			if(importDeclaration.getNameAsString().endsWith("." + className)) {
				String packagePath = importDeclaration.getNameAsString().replace(".", "/");
				return packagePath + ".java";
			}
		}
		
		//if it reaches here, means called class is not on imports so it belongs to the changed class package. edits changedfile name to the called class
		String packageDeclarationString = methodCompilationUnit.findFirst(PackageDeclaration.class).get().getNameAsString();
		String changedFilePath = packageDeclarationString.replace(".", "/");
		return changedFilePath + "/" + className + ".java";
	}

}
