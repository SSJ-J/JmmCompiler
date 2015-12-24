package jmmCompiler.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import jmmCompiler.lexical.Node;
import jmmCompiler.lexical.ParseException;
import jmmCompiler.lexical.Scanner;

/** throws TokenMgrError */
public class Analysis {
		
	public String ana_lex(File file){
		InputStream in = null;
		Scanner scanner = null;
		
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		scanner = new Scanner(in);
		return scanner.scan();
	}
	
	public Node ana_syn(File file) throws ParseException{ 
		InputStream in = null;
		Scanner scanner = null;
		
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		scanner = new Scanner(in);
		return scanner.parse();
	}
	
	public String run(File file){
		String s="run is not implent";
		return s;
	}
	
}