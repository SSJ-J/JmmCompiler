package jmmCompiler.semantic;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
	
	private Map<String, Symbol> symbols = new HashMap<>();
	private Map<String, ClassSymbol> classSymbols = new HashMap<>();
	private Map<String, MethodSymbol> methodSymbols = new HashMap<>();
	private Map<String, VariableSymbol> varSymbols = new HashMap<>();
	private Map<String, TypeSymbol> typeSymbols = new HashMap<>();
	
	public boolean addSymbol(String key, Symbol value) {
		if(symbols.containsKey(key))
			return false;
		symbols.put(key, value);
		return true;
	}
	
	public boolean addClassSymbol(String key, ClassSymbol value) {
		if(classSymbols.containsKey(key))
			return false;
		classSymbols.put(key, value);
		return true;
	}
	
	public boolean addMethodSymbol(String key, MethodSymbol value) {
		if(methodSymbols.containsKey(key))
			return false;
		methodSymbols.put(key, value);
		return true;
	}
	
	public boolean addVarSymbol(String key, VariableSymbol value) {
		if(varSymbols.containsKey(key))
			return false;
		varSymbols.put(key, value);
		return true;
	}
	
	public boolean addTypeSymbol(String key, TypeSymbol value) {
		if(typeSymbols.containsKey(key))
			return false;
		typeSymbols.put(key, value);
		return true;
	}
	
	public enum SymbolKind {
		CLASS,
		METHOD,
		VARIABLE,
		TYPE
	};
	
	public static class Symbol {
		public String name;
		public SymbolTable.SymbolKind type;
	}

	public static class ClassSymbol extends Symbol {
		public String[] methods;
		public String[] variables;
		public String superClass;
		public ClassSymbol() {
			super();
			this.type = SymbolTable.SymbolKind.CLASS;
		}
	}

	public static class MethodSymbol extends Symbol {
		public String[] parameters;
		public String[] paraTypes;
		public String retType;
		public String[] localVars;
		public MethodSymbol() {
			super();
			this.type = SymbolTable.SymbolKind.METHOD;
		}
	}

	public static class VariableSymbol extends Symbol {
		public String varType;
		public Object initVal;
		public VariableSymbol() {
			super();
			this.type = SymbolTable.SymbolKind.VARIABLE;
		}
	}

	public static class TypeSymbol extends Symbol {
		public String arrayType;
		public String arrayNum;
		public TypeSymbol() {
			super();
			this.type = SymbolTable.SymbolKind.TYPE;
		}
	}
	
}


