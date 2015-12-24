package jmmCompiler.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
	
	private Map<String, Symbol> symbols = new HashMap<>();
	private Map<String, ClassSymbol> classSymbols = new HashMap<>();
	private Map<String, MethodSymbol> methodSymbols = new HashMap<>();
	private Map<String, VariableSymbol> varSymbols = new HashMap<>();
	
	public boolean addClassSymbol(String key, ClassSymbol value) {
		if(classSymbols.containsKey(key))
			return false;
		classSymbols.put(key, value);
		symbols.put(key, value);
		return true;
	}
	
	public boolean addMethodSymbol(String key, MethodSymbol value) {
		if(methodSymbols.containsKey(key))
			return false;
		methodSymbols.put(key, value);
		symbols.put(key, value);
		return true;
	}
	
	public boolean addVarSymbol(String key, VariableSymbol value) {
		if(varSymbols.containsKey(key))
			return false;
		varSymbols.put(key, value);
		symbols.put(key, value);
		return true;
	}
	
	public String toString() {
		String result = "";
		result += ("classSymbols......\n");
		for(ClassSymbol s : classSymbols.values()) {
			result += (s.name + "\t");
			result += (s.superClass + "\t");
			result += "\n";
		}
		result += ("\nMethodSymbols......\n");
		for(MethodSymbol s : methodSymbols.values()) {
			result += (s.name + "\t");
			result += (s.modifier + "\t");
			result += (s.isStatic + "\t");
			result += (s.retType + "\t");
			result += "\n";
		}
		result += ("\nVariableSymbols......\n");
		for(VariableSymbol s : varSymbols.values()) {
			result += (s.name + "\t");
			result += (s.modifier + "\t");
			result += (s.isStatic + "\t");
			result += (s.varType + "\t");
			result += "\n";
		}
		result += "\n";
		
		return result;
	}
	
	public enum SymbolKind {
		CLASS,
		METHOD,
		VARIABLE,
	};
	
	public enum Modifier {
		PUBLIC,
		PRIVATE,
		PROTECTED
	}
	
}

class Symbol {
	public String name;
	public SymbolTable.SymbolKind type;
}

class ClassSymbol extends Symbol {
	public String superClass;
	public List<String> methods = new ArrayList<>();
	public List<String> variables = new ArrayList<>();
	public ClassSymbol() {
		super();
		this.type = SymbolTable.SymbolKind.CLASS;
	}
}

class MethodSymbol extends Symbol {
	public SymbolTable.Modifier modifier;
	public boolean isStatic = false;
	public String[] parameters;
	public String[] paraTypes;
	public String retType;
	public String[] localVars;
	public MethodSymbol() {
		super();
		this.type = SymbolTable.SymbolKind.METHOD;
	}
}

class VariableSymbol extends Symbol {
	public SymbolTable.Modifier modifier;
	public boolean isStatic = false;
	public String varType;
	public Object initVal;
	public VariableSymbol() {
		super();
		this.type = SymbolTable.SymbolKind.VARIABLE;
	}
}


