package jmmCompiler.semantic;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
	
	public enum SymbolKind {
		CLASS,
		METHOD,
		VARIABLE,
		TYPE
	};
	
	List<Symbol> symbolTable = new ArrayList<>();
	List<ClassSymbol> classSymbolTable = new ArrayList<>();
	List<MethodSymbol> methodSymbolTable = new ArrayList<>();
	List<VariableSymbol> varSymbolTable = new ArrayList<>();
	List<TypeSymbol> typeSymbolTable = new ArrayList<>();
	
}

class Symbol {
	String name;
	SymbolTable.SymbolKind type;
}

class ClassSymbol extends Symbol {
	String[] methods;
	String[] variables;
	ClassSymbol() {
		super();
		this.type = SymbolTable.SymbolKind.CLASS;
	}
}

class MethodSymbol extends Symbol {
	String[] parameters;
	String[] paraTypes;
	String retType;
	String[] localVars;
	MethodSymbol() {
		super();
		this.type = SymbolTable.SymbolKind.METHOD;
	}
}

class VariableSymbol extends Symbol {
	String varType;
	Object initVal;
	VariableSymbol() {
		super();
		this.type = SymbolTable.SymbolKind.METHOD;
	}
}

class TypeSymbol extends Symbol {
	String arrayType;
	String arrayNum;
	TypeSymbol() {
		super();
		this.type = SymbolTable.SymbolKind.TYPE;
	}
}






