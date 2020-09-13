package pt.utl.ist.thesis77077.core;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import pt.utl.ist.thesis77077.api.ChangedFile;

public class VariableDeclarationExprProcess {
	
	private static final double IMPACT_VALUE_PROJECT_CLASS_SCOPE = 0.3;
	private static final double IMPACT_VALUE_PRIMITE_JAVA_LANG_LIBS_SCOPE = 0.1;
	
	public double process(VariableDeclarationExpr n, Object arg, Map<String, String> repositoryJavaFiles, ChangedFile changedFile) {
		
		CompilationUnit compilationUnit = n.findCompilationUnit().get();
		
		List<ClassOrInterfaceType> variableType = n.findAll(ClassOrInterfaceType.class);
//		for(ClassOrInterfaceType type : variableType) {
//			System.out.println("Variable class found: " + type.getNameAsString());
//		}
		
		if((!(variableType.isEmpty())) && isRepositoryClass(variableType, repositoryJavaFiles, changedFile, compilationUnit)) {  //falta a verificaçao se para alem de nao estar vazio, alguma das classes presentes pertence ao projeto
			return IMPACT_VALUE_PROJECT_CLASS_SCOPE;
		}
		//System.out.println("---------------------\n");
		return IMPACT_VALUE_PRIMITE_JAVA_LANG_LIBS_SCOPE;
		//TO DO
		//se o tipo de variavel for de uma classe, ie, o vetor variabletype tem elementos, verificar se alguma das classes presentes no vetor
		//sao alguma classe existente no projeto (criada neste)
		//para isso é necessario a API ir buscar as classes java existentes no repositorio e passar para o core a informaçao (falta fazer isto)
		//arranque da tool vai ficar mais lento por causa da pesquisa inicial sobre o repositorio inteiro.....
	}
	
	public boolean isRepositoryClass(List<ClassOrInterfaceType> variableType, Map<String, String> repositoryJavaFiles, ChangedFile changedFile, CompilationUnit compilationUnit) {
		boolean classesFound = false;
		
		for(ClassOrInterfaceType type : variableType) {
			String classStringToCompare = findClassPackagePath(compilationUnit, changedFile, type.getNameAsString());
			
			for(String javaFileName : repositoryJavaFiles.keySet()) {
				if(javaFileName.endsWith(classStringToCompare)) {
					changedFile.getRelatedJavaFiles().putIfAbsent(javaFileName, repositoryJavaFiles.get(javaFileName));
					classesFound = true;
				}
			}
		}
		return classesFound;
	}
	
	public String findClassPackagePath(CompilationUnit compilationUnit, ChangedFile changedFile, String className) {
		List<ImportDeclaration> changedFileImports = compilationUnit.findAll(ImportDeclaration.class);
		
		//checking on imports if the class is there and returns the path of the package if true.
		for(ImportDeclaration importDeclaration : changedFileImports) {
			if(importDeclaration.getNameAsString().endsWith("." + className)) {
				String packagePath = importDeclaration.getNameAsString().replace(".", "/");
				return packagePath + ".java";
			}
		}
		
		//if it reaches here, means called class is not on imports so it belongs to the changed class package. edits changedfile name to the called class
		String packageDeclarationString = compilationUnit.findFirst(PackageDeclaration.class).get().getNameAsString();
		String changedFilePath = packageDeclarationString.replace(".", "/");
		return changedFilePath + "/" + className + ".java";
	}

}
