package jmmCompiler.lexical;

public class SimpleNamedNode extends SimpleNode {

	private String name = "";
	
	public SimpleNamedNode(int i) {
		super(i);
		// TODO Auto-generated constructor stub
	}

	public SimpleNamedNode(Scanner p, int i) {
		super(p, i);
		// TODO Auto-generated constructor stub
	}
	
	public void setName(String n) {
	    name = n;
	}
	  
	public String getName() {
		return name;
	}
	  
	@Override
	public String toString() {
		return super.toString() + ": " + name;
	}
}
