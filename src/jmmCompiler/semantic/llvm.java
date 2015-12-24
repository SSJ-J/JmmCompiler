package jmmCompiler.semantic;
import jmmCompiler.lexical.SimpleNode;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LightYear112 on 2015/12/24.
 */
public class llvm {
	SymbolTable st;
	Map<String,class_var_info> class_var = new HashMap<String, class_var_info>();
	Map<String,class_var_info> func_var = new HashMap<String, class_var_info>();
    Map<String,array_info> class_array_var = new HashMap<String, array_info>();
    Map<String,array_info> func_array_var = new HashMap<String, array_info>();
    private static int var_num=0;
    //if语句会用到的变量，记录if的状态
    private static int then_num=0;
    private static int else_num=0;
    private static int end_num=0;
    private static int cmp_num=0;
    private static int true_num=0;
    private static int false_num=0;
    private static int while_num=0;

    private static int and_last=0;
    private static int or_last=0;
    private static int if_last=0;
    private static int call_num=0;
    //表达式会用到的变量
    private static int add_num=0;
    private static int sum_num=0;
    private static int mul_num=0;
    private static int div_num=0;
    private static int mod_num=0;

    private static int arrayidx=0;

    private static int arraydecay =0;

    private static int in_function=0;
    private static int in_class=0;
    private static boolean call_dir=false;
	
	llvm(SimpleNode s){
		//st = new SymbolTable(s);
	}
	public void generate(){
//		try{
//			st.analyse();		//load table
//			runcode(st.root);
//		}
//		catch(SemException e){
//								//output to file .ll
//		}
	}
	    public void func_init(){
        var_num = 0;

        then_num=0;
        else_num=0;
        end_num=0;
        cmp_num=0;
        true_num=0;
        false_num=0;
        while_num = 0;
        and_last=0;
        or_last=0;
        if_last=0;
        call_num = 0;

        add_num=0;
        sum_num=0;
        mul_num=0;
        div_num=0;
        mod_num=0;
        arrayidx=0;
        arraydecay=0;

        in_function = 0;
        call_dir = false;
        func_array_var.clear();
        func_var.clear();
     //   last_ope=0;
    }
	public void class_init(){
		func_init();
		class_var.clear();
		class_array_var.clear();
	    in_class=0;
	}
	public void run_code(SimpleNode node)throws SemException{
		FileOutputStream out;
		try {
			out = new FileOutputStream("D:/test.txt");
		    PrintStream p=new PrintStream(out);
		    p.println("@.str = private unnamed_addr constant [5 x i8] c\"%d  \\00\", align 1\n");
		    p.println("@.str1 = private unnamed_addr constant [2 x i8] c\"\\0A\\00\", align 1\n");
		    p.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		all_class(node);
		
	}
	public void all_class(SimpleNode node)throws SemException{
		int class_num = node.jjtGetNumChildren();
		for(int i=0;i< class_num;++i){
			class_init();
			SimpleNode class_node = (SimpleNode)node.jjtGetChild(i);
            if(class_node.toString().equals("ClassDeclaration")){
                in_class =1;
                run_class(class_node);
            }
            class_init();
		}
		in_class=0;
        class_init();
	}
	public void run_class(SimpleNode node)throws SemException{
		String classname = node.jjtGetChild(0).toString();
		SimpleNode mathod = (SimpleNode)node.jjtGetChild(1);
		int i=0;
		for(;i<mathod.jjtGetNumChildren();++i){
			
		}
		run_all_functions(mathod,classname);
	}
	public void run_all_functions(SimpleNode node,String prefix)throws SemException{
		int func_num  = node.jjtGetNumChildren();
        for(int i = 0;i<func_num;i++)
        {
            func_init();
            SimpleNode func_node = (SimpleNode)node.jjtGetChild(i).jjtGetChild(0);
            if(func_node.toString().equals("MethodDeclarator")){

                in_function =1;
                run_function(func_node,prefix);

            }
            func_init();
        }
        in_function=0;
        func_init();
	}
	public void run_function(SimpleNode node,String prefix)throws SemException{
		
	}
}
class pair{
    int ret;
    String var;
    pair(int r,String v){
        ret =r;
        var = v;
    }
}
class array_info{
    String type;
    String name;
    int num;

    array_info(String d,String c1,int c2){
        type=d;
        name=c1;
        num=c2;
    }
}
class class_var_info{
	String type;
	String preority;
	String name;
	class_var_info(String t,String p,String n){
		type = t;
		preority=p;
		name=n;
	}
}