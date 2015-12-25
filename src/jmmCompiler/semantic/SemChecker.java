package jmmCompiler.semantic;

import jmmCompiler.lexical.ScannerTreeConstants;
import jmmCompiler.lexical.SimpleNode;

public class SemChecker implements SemanticCheckable {
	private SymbolTable sTable = new SymbolTable();
	
	/* add symbol of a program */
	@Override
	public void semCheck(SimpleNode root) throws SemException {
		int num = root.jjtGetNumChildren();
		for(int i = 0; i < num; i++) {
			SimpleNode classRoot = (SimpleNode)root.jjtGetChild(i);
			classCheck(classRoot);
		}
		/** test */
		System.out.println(sTable.toString());
	}
	
	@Override
	public Symbol getSymbol(String name) {
		return sTable.getSymbol(name);
	}

	@Override
	public Symbol getClassSymbol(String name) {
		return sTable.getClassSymbol(name);
	}

	@Override
	public Symbol getMethodSymbol(String name) {
		return sTable.getMethodSymbol(name);
	}

	@Override
	public Symbol getVarSymbol(String name) {
		return sTable.getVarSymbol(name);
	}
	
	/* add symbol of a class */
	private void classCheck(SimpleNode classRoot) throws SemException {
		ClassSymbol symbol = new ClassSymbol();
		SimpleNode nameNode = (SimpleNode)classRoot.jjtGetChild(0);
		SimpleNode node = (SimpleNode)classRoot.jjtGetChild(1);
		int next = 1;		// next token
		int num = classRoot.jjtGetNumChildren();
		
		// name and parent name
		symbol.name = nameNode.jjtGetFirstToken().image;
		if(node.getId() == ScannerTreeConstants.JJTNAME) {
			symbol.superClass = node.jjtGetFirstToken().image;
			next ++;
		} else {
			symbol.superClass = null;
		}
		
		// body
		for(int i = next; i < num; i++) {
			node = (SimpleNode)classRoot.jjtGetChild(i);
			switch(node.getId()) {
			case ScannerTreeConstants.JJTFIELDDECL:
				symbol.variables.add(fieldCheck(symbol.name, node));
				break;
			case ScannerTreeConstants.JJTMETHODDECL:
				symbol.methods.add(methodCheck(symbol.name, node));
				break;
			default:
				break;
			}
		}
		
		// add to symbol table
		if(!sTable.addClassSymbol(symbol.name, symbol))
			throw new SemException("class: " + symbol.name + "defined more than once");
	}
	
	private String fieldCheck(String className, SimpleNode root) throws SemException {
		SimpleNode node = (SimpleNode) root.jjtGetChild(0);
		String str = node.jjtGetFirstToken().toString();
		VariableSymbol symbol = new VariableSymbol();
		int next = 1;
		
		// modifier
		if(str.equals("public")) {
			symbol.modifier = SymbolTable.Modifier.PUBLIC;
		} else if(str.equals("privtae")) {
			symbol.modifier = SymbolTable.Modifier.PRIVATE;
		} else {
			symbol.modifier = SymbolTable.Modifier.PROTECTED;
		} 
		
		// static
		node = (SimpleNode) root.jjtGetChild(next);
		if(node.getId() == ScannerTreeConstants.JJTMODIFIERSTATIC) {
			symbol.isStatic = true;
			next ++;
			node = (SimpleNode) root.jjtGetChild(next);
		}
		
		// type
		symbol.varType = node.jjtGetFirstToken().toString();
		next ++;
		
		// name("className@varName")
		node = (SimpleNode) root.jjtGetChild(next);
		str = node.jjtGetFirstToken().toString();
		symbol.name = className + "@" + str;
		
		if(!sTable.addVarSymbol(symbol.name, symbol))
			throw new SemException("member: " + str + " in " + className +" defined more than once");
		
		return symbol.name;
	}
	
	private String methodCheck(String className, SimpleNode root) throws SemException {
		SimpleNode node = (SimpleNode) root.jjtGetChild(0);
		SimpleNode declNode = (SimpleNode) root.jjtGetChild(root.jjtGetNumChildren() - 2);
		SimpleNode bodyNode = (SimpleNode) root.jjtGetChild(root.jjtGetNumChildren() - 1);
		String str = node.jjtGetFirstToken().toString();
		MethodSymbol symbol = new MethodSymbol();
		int next = 1;
		String name = "";
		
		// modifier
		if(str.equals("public")) {
			symbol.modifier = SymbolTable.Modifier.PUBLIC;
		} else if(str.equals("privtae")) {
			symbol.modifier = SymbolTable.Modifier.PRIVATE;
		} else {
			symbol.modifier = SymbolTable.Modifier.PROTECTED;
		} 
		
		// static
		node = (SimpleNode) root.jjtGetChild(next);
		if(node.getId() == ScannerTreeConstants.JJTMODIFIERSTATIC) {
			symbol.isStatic = true;
			next ++;
			node = (SimpleNode) root.jjtGetChild(next);
		}
		
		// return type
		symbol.retType = node.jjtGetFirstToken().toString();
		next ++;
		
		//------------------declarator-----------------
		// name
		node = (SimpleNode) declNode.jjtGetChild(0);
		name = node.jjtGetFirstToken().toString();
		symbol.name = className + "@" + name;
		
		// parameters
		node = (SimpleNode) declNode.jjtGetChild(1);
		for(int i = 0; i < node.jjtGetNumChildren(); i++) {
			SimpleNode cNode = (SimpleNode)node.jjtGetChild(i);
			/* parameter name */
			String cName = ((SimpleNode)cNode.jjtGetChild(1)).jjtGetFirstToken().image;
			symbol.parameters.add(cName);
			
			/* parameter type */
			cName = ((SimpleNode)cNode.jjtGetChild(0)).jjtGetFirstToken().image;
			// array type
			if(cNode.jjtGetChild(0).getId() == ScannerTreeConstants.JJTARRAYTYPE) {
				cName += "[]";
			}
			symbol.paraTypes.add(cName);
		}
		
		//--------------------body--------------------
		for(int i = 0; i < bodyNode.jjtGetNumChildren(); i++) {
			SimpleNode expNode = (SimpleNode)bodyNode.jjtGetChild(i);
			String vName = "";
			String tName = "";
			switch(expNode.getId()) {
			case ScannerTreeConstants.JJTINITIALIZEREXP:
				expNode = (SimpleNode)expNode.jjtGetChild(0);
				// no break, continue
			case ScannerTreeConstants.JJTLOCALVARDECL: 
				vName = ((SimpleNode)expNode.jjtGetChild(1)).jjtGetFirstToken().image;
				tName = ((SimpleNode)expNode.jjtGetChild(0)).jjtGetFirstToken().image;
				// array type
				if(expNode.jjtGetChild(0).getId() == ScannerTreeConstants.JJTARRAYTYPE) {
					tName += "[]";
				}
				symbol.localVars.put(vName, tName);
				break;
			default:
				break;
			}
		}
		
		if(!sTable.addMethodSymbol(symbol.name, symbol))
			throw new SemException("method: " + name + " in " + className +" defined more than once");
		
		return symbol.name;
	}

}
