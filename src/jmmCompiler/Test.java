package jmmCompiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import jmmCompiler.lexical.SimpleNode;
import jmmCompiler.lexical.Scanner;

public class Test {
	
	public static void test(String args []) {
		File file=new File("F:/Qsort.jmm");
		InputStream in = null;
		
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		System.out.println(Scanner.scan(in));
		
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		((SimpleNode)Scanner.parse(in)).dump("");
	}

}
