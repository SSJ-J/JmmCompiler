package jmmCompiler.semantic;

public class SemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1571536151725934582L;
	private String message;
	
    public SemException(String error){
        message = (error + "\n");
    }
    public String getMessage(){
        return message;
    }
}
