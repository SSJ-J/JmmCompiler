package jmmCompiler.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import jmmCompiler.lexical.Node;
import jmmCompiler.lexical.Scanner;

public class Analysis {
	public String ana_lex(File file){
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return Scanner.scan(in);
	}
	
	public Node ana_syn(File file){ 
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return Scanner.parse(in);
	}
}