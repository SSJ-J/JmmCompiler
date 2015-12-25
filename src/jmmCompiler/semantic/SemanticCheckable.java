package jmmCompiler.semantic;

import jmmCompiler.lexical.SimpleNode;

public interface SemanticCheckable {

	/* initialize symbol table */
	public void semCheck(SimpleNode root) throws SemException;
	
	/* return symbol by name (null if not exsits) */
	public Symbol getSymbol(String name);
	
	/* return class symbol by name (null if not exsits) */
	public Symbol getClassSymbol(String name);
	
	/* return method symbol by name (null if not exsits) */
	public Symbol getMethodSymbol(String name);
	
	/* return variable symbol by name (null if not exsits) */
	public Symbol getVarSymbol(String name);
	
}
