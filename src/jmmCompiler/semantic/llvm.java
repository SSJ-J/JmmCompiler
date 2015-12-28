package jmmCompiler.semantic;
import jmmCompiler.lexical.SimpleNode;
import jmmCompiler.lexical.ScannerTreeConstants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LightYear112 on 2015/12/24.
 */
public class LLVM {
	SemanticCheckable sc=new SemChecker();
	FileOutputStream out;
	PrintStream p;
	String filename;
	Map<String,class_var_info> class_var = new HashMap<String, class_var_info>();
	Map<String,func_var_info> func_var = new HashMap<String, func_var_info>();
    Map<String,array_info> class_array_var = new HashMap<String, array_info>();
    Map<String,array_info> func_array_var = new HashMap<String, array_info>();
    private static int var_num=0;
    //if语句会用到的变量，记录if的状态
    private static int then_num=0;
    private static int else_num=0;
    private static int end_num=0;
    private static int cmp_num=0;
    private static int while_num=0;

    private static int call_num=0;
    //表达式会用到的变量
    private static int add_num=0;
    private static int sum_num=0;
    private static int mul_num=0;
    private static int div_num=0;
    private static int mod_num=0;

    private static int arrayidx=0;

    private static int arraydecay =0;

    public LLVM(SimpleNode s) throws SemException{
		sc.semCheck(s);
		try {
			filename = "llvm.ll";
			out = new FileOutputStream(filename);
			p=new PrintStream(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //PrintStream p=new PrintStream(out);
	}
	public void generate(SimpleNode root) throws SemException{
		run_code(root);
	}
	    public void func_init(){
        var_num = 0;

        then_num=0;
        else_num=0;
        end_num=0;
        cmp_num=0;
        while_num = 0;
        call_num = 0;

        add_num=0;
        sum_num=0;
        mul_num=0;
        div_num=0;
        mod_num=0;
        arrayidx=0;
        arraydecay=0;

        func_array_var.clear();
        func_var.clear();
     //   last_ope=0;
    }
	public void class_init(){
		func_init();
		class_var.clear();
		class_array_var.clear();
	}
	public void run_code(SimpleNode node)throws SemException{
		p.println("@.str = private unnamed_addr constant [5 x i8] c\"%d  \\00\", align 1");
		p.println("@.str1 = private unnamed_addr constant [2 x i8] c\"\\0A\\00\", align 1"); 
		all_class(node);
		p.println("declare i32 @printf(i8*, ...)");
	}
	public void all_class(SimpleNode node)throws SemException{
		int class_num = node.jjtGetNumChildren();
		for(int i=0;i< class_num;++i){
			class_init();
			SimpleNode class_node = (SimpleNode)node.jjtGetChild(i);
            if(class_node.getId()==ScannerTreeConstants.JJTCLASSDECLARATION){
                run_class(class_node);
            }
            class_init();
		}
		class_init();
	}
	public void run_class(SimpleNode node)throws SemException{
		String classname = ((SimpleNode)node.jjtGetChild(0)).jjtGetFirstToken().toString();
		node.jjtGetChild(1);
		int i=1;
		//-------------------------------------------------------------------------------------struct unfinish
		
		sc.getClassSymbol(classname);
		
		for(i=0;i<node.jjtGetNumChildren();++i){
			SimpleNode child = (SimpleNode)node.jjtGetChild(i);
			//System.out.println("function");
			func_init();
			//System.out.println(child.getId());
			if(child.getId()==ScannerTreeConstants.JJTMETHODDECL){
				//System.out.println("function");
				run_function(child,"",classname);
			}
			func_init();
		}
	}
	public void run_function(SimpleNode node,String prefix,String path)throws SemException{
		//p.println("run_function");
		SimpleNode parameter = (SimpleNode)node;
		String res_type="";
		String func_name="";
		String instr="";
		for(int i=0;i<parameter.jjtGetNumChildren();++i){
			SimpleNode tmp = (SimpleNode)parameter.jjtGetChild(i);
			if(tmp.getId()==ScannerTreeConstants.JJTRESULTTYPE){
				res_type=tmp.jjtGetFirstToken().toString();
				if(res_type.equals("void")){
					res_type ="void";
				}
				else{
					res_type = "i32";
				}
				continue;
			}
			if(tmp.getId() ==ScannerTreeConstants.JJTMETHODDECLARATOR){
				func_name = tmp.jjtGetFirstToken().toString();
				parameter = tmp;
				continue;
			}
		}
		//func_name change
		instr="define "+res_type+" @"+func_name+"(";
		MethodSymbol symbol = (MethodSymbol) sc.getMethodSymbol(path+"@"+func_name);
		if(parameter.jjtGetChild(1).jjtGetNumChildren()==0){
			instr = instr + ") nounwind {\nentry:\n";
		}
		else{
			//add parameters
			for(int i=0;i<symbol.parameters.size();++i){
				if(symbol.paraTypes.get(i).equals("int")||symbol.paraTypes.get(i).equals("bool")
						||symbol.paraTypes.get(i).equals("double")||symbol.paraTypes.get(i).equals("char")){
					instr = instr+prefix+"i32 %"+symbol.parameters.get(i);
				}
				else if(symbol.paraTypes.get(i).equals("int[]")||symbol.paraTypes.get(i).equals("bool[]")
						||symbol.paraTypes.get(i).equals("double[]")||symbol.paraTypes.get(i).equals("char[]")){
					instr = instr+prefix+"i32* %"+symbol.parameters.get(i);
				}
				if(i == symbol.parameters.size()-1){
					instr = instr+") nounwind {\n"+"entry:\n";
				}
				else{
					instr = instr + ", ";
				}
			}
		}
		p.println(instr);
		prefix = "  ";
		//print parameter
		for(int i=0;i<symbol.parameters.size();++i){
			if(symbol.paraTypes.get(i).equals("int")||symbol.paraTypes.get(i).equals("bool")
					||symbol.paraTypes.get(i).equals("double")||symbol.paraTypes.get(i).equals("char")){
				instr = prefix+"%"+symbol.parameters.get(i)+".addr = alloca i32, align 4\n";
                instr = instr +prefix+"store i32 %"+symbol.parameters.get(i)+", i32* %"+symbol.parameters.get(i)+".addr, align 4";
			}
			else if(symbol.paraTypes.get(i).equals("int[]")||symbol.paraTypes.get(i).equals("bool[]")
					||symbol.paraTypes.get(i).equals("double[]")||symbol.paraTypes.get(i).equals("char[]")){
				instr = prefix+"%"+symbol.parameters.get(i)+".addr = alloca i32*, align 4\n";
                instr = instr +prefix+"store i32* %"+symbol.parameters.get(i)+", i32** %"+symbol.parameters.get(i)+".addr, align 4";
			}
			p.println(instr);
		}
		//print localvars
//		for(int i=0;i<symbol.localVars.size();++i){
//			if(symbol.paraTypes.get(i).equals("int")||symbol.paraTypes.get(i).equals("bool")
//					||symbol.paraTypes.get(i).equals("double")||symbol.paraTypes.get(i).equals("char")){
//				ge_alloc(symbol.localVars.get(i),prefix);
//			}
//			else if(symbol.paraTypes.get(i).equals("int[]")||symbol.paraTypes.get(i).equals("bool[]")
//					||symbol.paraTypes.get(i).equals("double[]")||symbol.paraTypes.get(i).equals("char[]")){
//				ge_alloc_array(symbol.localVars.get(i),prefix);
//			}
//		}
		for(int i=0;i<node.jjtGetNumChildren();++i){
			SimpleNode tmp = (SimpleNode)node.jjtGetChild(i);
			if(tmp.getId()==ScannerTreeConstants.JJTBLOCK){
				node = tmp;
				break;
			}
		}
		run_block(node,path+"@"+func_name,prefix);
		if(res_type.equals("void")){
			p.println("  ret void");
		}
		else
			p.println("  ret i32 0");
		p.println("}");
	}
	public void run_block(SimpleNode node,String path,String prefix)throws SemException{
		//System.out.println("block");
		//System.out.println(node.getId());
		SimpleNode block=node;
		int instr_num = block.jjtGetNumChildren();
		sc.getMethodSymbol(path);
		for(int i=0;i< instr_num;++i){
			SimpleNode key = (SimpleNode)block.jjtGetChild(i);
			if(key.getId()==ScannerTreeConstants.JJTLOCALVARDECL){
				SimpleNode tmp = (SimpleNode)key.jjtGetChild(1);
				ge_alloc(tmp.jjtGetFirstToken().toString(),prefix);
			}
			else if(key.getId()==ScannerTreeConstants.JJTINITIALIZEREXP){
				if(((SimpleNode)key.jjtGetChild(1)).getId()==ScannerTreeConstants.JJTALLOCATIONEXPRESSION){	//数组初始化
					String type = key.jjtGetFirstToken().toString();
					String varname = ((SimpleNode)((SimpleNode)key.jjtGetChild(0)).jjtGetChild(1)).jjtGetFirstToken().toString();
					SimpleNode tmp = (SimpleNode)key.jjtGetChild(1);
					tmp = (SimpleNode)tmp.jjtGetChild(1);
					tmp = (SimpleNode)tmp.jjtGetChild(0);
					String num = tmp.jjtGetFirstToken().toString();
					ge_alloc_array(varname,num,prefix);
					array_info info = new array_info(type,Integer.valueOf(num).intValue());
					func_array_var.put(varname, info);
				}
				else{																						//变量初始化
					key.jjtGetFirstToken().toString();
					String varname = ((SimpleNode)((SimpleNode)key.jjtGetChild(0)).jjtGetChild(1)).jjtGetFirstToken().toString();
					ge_alloc(varname,prefix);
					SimpleNode next = (SimpleNode)key.jjtGetChild(1);
					if(next.getId()==ScannerTreeConstants.JJTNAME){
						String val_name = next.jjtGetFirstToken().toString();
						String tmp_name="";
						if(((MethodSymbol)sc.getMethodSymbol(path)).parameters.contains(val_name)){
							val_name = val_name +".addr";
						}
						tmp_name = ge_load(val_name,path,prefix);
						ge_store(tmp_name,varname,path,prefix);
					}
					else if(next.getId()==ScannerTreeConstants.JJTADDEXP ||next.getId()==ScannerTreeConstants.JJTMULTIEXP){
						String name = ge_addop(next,path,prefix);
						ge_store(name,varname,path,prefix);
					}
					else if(next.getId()==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
						String name = ge_loadarray(next,path,prefix);
						ge_store(name,varname,path,prefix);
					}
					else if(next.getId()==ScannerTreeConstants.JJTLITERAL){
						String vaname = next.jjtGetFirstToken().toString();
						String instr = prefix+"store i32 "+ vaname+", i32* %"+varname+", align 4";
						p.println(instr);
					}
				}
			}
			else if(key.getId()==ScannerTreeConstants.JJTNAME){
				String varname = key.jjtGetFirstToken().toString();
				SimpleNode next = (SimpleNode)block.jjtGetChild(i+1);
				if(next.getId()==ScannerTreeConstants.JJTNAME){
					String val_name = next.jjtGetFirstToken().toString();
					String tmp_name="";
					if(((MethodSymbol)sc.getMethodSymbol(path)).parameters.contains(val_name)){
						val_name = val_name +".addr";
					}
					tmp_name = ge_load(val_name,path,prefix);
					ge_store(tmp_name,varname,path,prefix);
				}
				else if(next.getId()==ScannerTreeConstants.JJTADDEXP ||next.getId()==ScannerTreeConstants.JJTMULTIEXP){
					String name = ge_addop(next,path,prefix);
					ge_store(name,varname,path,prefix);
				}
				else if(next.getId()==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
					String name = ge_loadarray(next,path,prefix);
					ge_store(name,varname,path,prefix);
				}
				else if(next.getId()==ScannerTreeConstants.JJTLITERAL){
					String valname = next.jjtGetFirstToken().toString();
					String instr = prefix+"store i32 "+ valname+", i32* %"+varname+", align 4";
					p.println(instr);
				}
				i = i+1;
			}
			else if(key.getId()==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
				//System.out.println("1234");
				String varname = key.jjtGetFirstToken().toString();
				//System.out.println(varname);
				if(key.jjtGetFirstToken().toString().equals("print")){
					//System.out.println("12345");
					ge_print(key,path,prefix);
				}
				else if(((SimpleNode)key.jjtGetChild(1)).getId() ==ScannerTreeConstants.JJTARGUMENTS){
					//System.out.println("hehehe");
					ge_call(key,path,prefix);
				}
				else if(((SimpleNode)key.jjtGetChild(1)).getId() ==ScannerTreeConstants.JJTARRAYACCESS){
					SimpleNode next = (SimpleNode)block.jjtGetChild(i+1);
					key.jjtGetFirstToken().toString();
					//String instr1 = prefix + "%arraydecay" + String.valueOf(arraydecay) + " = getelementptr inbounds [" + String.valueOf(func_array_var.get(names).num) + " x i32]* %" + names + ", i32 0, i32 "+index;
					//p.println(instr1);
					//varname = "arraydecay"+String.valueOf(arraydecay);
					//arraydecay = arraydecay + 1;
					varname = ge_loadarray(key,path,prefix);
					if(next.getId()==ScannerTreeConstants.JJTNAME){
						String val_name = next.jjtGetFirstToken().toString();
						String tmp_name="";
						if(((MethodSymbol)sc.getMethodSymbol(path)).parameters.contains(val_name)){
							val_name = val_name +".addr";
						}
						tmp_name = ge_load(val_name,path,prefix);
						ge_store(tmp_name,varname,path,prefix);
					}
					else if(next.getId()==ScannerTreeConstants.JJTADDEXP ||next.getId()==ScannerTreeConstants.JJTMULTIEXP){
						String name = ge_addop(next,path,prefix);
						ge_store(name,varname,path,prefix);
					}
					else if(next.getId()==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
						String name = ge_loadarray(next,path,prefix);
						ge_store(name,varname,path,prefix);
					}
					else if(next.getId()==ScannerTreeConstants.JJTLITERAL){
						String valname = next.jjtGetFirstToken().toString();
						String instr = prefix+"store i32 "+ valname+", i32* %"+varname+", align 4";
						p.println(instr);
					}
					i = i+1;
				}
			}
			else if(key.getId()==ScannerTreeConstants.JJTFORSTATEMENT){
				
			}
			else if(key.getId()==ScannerTreeConstants.JJTIFSTATEMENT){
				ge_if(key,path,prefix);
			}
			else if(key.getId()==ScannerTreeConstants.JJTWHILESTATEMENT){
				ge_while(key,path,prefix);
			}
		}
	}
	public String logical(SimpleNode node,String path,String prefix)throws SemException{
		//System.out.println(node.getId());
		SimpleNode relation = node;
		String op="";
		String prename = "";
		String fixname = "";
		for(int i=0;i<relation.jjtGetNumChildren();++i){
			SimpleNode tmp = (SimpleNode)relation.jjtGetChild(i);
			if(i==1){
				op = tmp.jjtGetFirstToken().toString();
				//System.out.println(op);
			}
			else if(i==0){
				if(tmp.getId()==ScannerTreeConstants.JJTNAME){
					//System.out.println(tmp.jjtGetFirstToken().toString());
					String tmps = tmp.jjtGetFirstToken().toString();
					if(((MethodSymbol)sc.getMethodSymbol(path)).parameters.contains(tmps)){
						tmps = tmps + ".addr";
					}
					prename = ge_load(tmp.jjtGetFirstToken().toString(),path,prefix);
				}
				else if(tmp.getId()==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
					prename = ge_loadarray(tmp,path,prefix);
				}
			}
			else if(i ==2){
				if(tmp.getId()==ScannerTreeConstants.JJTNAME){
					String tmps = tmp.jjtGetFirstToken().toString();
					if(((MethodSymbol)sc.getMethodSymbol(path)).parameters.contains(tmps)){
						tmps = tmps +".addr";
					}
					fixname = "%"+ge_load(tmps,path,prefix);
				}
				else if(tmp.getId()==ScannerTreeConstants.JJTADDEXP ||tmp.getId()==ScannerTreeConstants.JJTMULTIEXP){
					fixname = "%"+ge_addop(tmp,path,prefix);
				}
				else if(tmp.getId()==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
					fixname = "%"+ge_loadarray(tmp,path,prefix);
				}
				else if(tmp.getId()==ScannerTreeConstants.JJTLITERAL){
					fixname = tmp.jjtGetFirstToken().toString();
				}
			}
		}
		//String l_name = ge_load(prename,path,prefix);
		//String r_name = ge_load(fixname,path,prefix);
		if(prename.length() >8){
			String tmp = prename.substring(0,8);
			//System.out.println(tmp);
			if(tmp.equals("arrayidx")){
				prename = "%"+ge_load(prename,path,prefix);
			}
		}
		else{
			prename = "%"+prename;
		}
		if(fixname.length() >9){
			String tmp = fixname.substring(0,9);
			//System.out.println(tmp);
			if(tmp.equals("%arrayidx")){
				fixname = "%"+ge_load(fixname.substring(1,fixname.length()),path,prefix);
			}
		}
		String ret_name = ge_cmp(op,prename,fixname,prefix,1);
		return ret_name;
	}
	public String ge_addop(SimpleNode node,String path,String prefix)throws SemException{
		String op = ((SimpleNode)node.jjtGetChild(1)).jjtGetFirstToken().toString();
		SimpleNode vars = (SimpleNode)node.jjtGetChild(0);
		String name ="";
		String ret_name="";
		if(vars.getId() ==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
			name = ge_loadarray(vars,path,prefix);
		}
		else if(vars.getId() ==ScannerTreeConstants.JJTNAME){
			name = vars.jjtGetFirstToken().toString();
		}
		else if(vars.getId() ==ScannerTreeConstants.JJTLITERAL){
			name = vars.jjtGetFirstToken().toString();
		}
		int preid = vars.getId();
		vars = (SimpleNode)node.jjtGetChild(2);
		String lastname = "";
		name = ge_load(name,path,prefix);
		if(vars.getId() ==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
			lastname = ge_loadarray(vars,path,prefix);
			if(preid !=ScannerTreeConstants.JJTLITERAL){
				ret_name = ge_operation(op,"%"+name,"%"+lastname,path,prefix);
			}
			else{
				ret_name = ge_operation(op,name,"%"+lastname,path,prefix);
			}
		}
		else if(vars.getId() ==ScannerTreeConstants.JJTNAME){
			String val_name = vars.jjtGetFirstToken().toString();
			if(((MethodSymbol)sc.getMethodSymbol(path)).parameters.contains(val_name)){
				val_name = val_name +".addr";
			}
			lastname = ge_load(val_name,path,prefix);
			if(preid !=ScannerTreeConstants.JJTLITERAL){
				ret_name = ge_operation(op,"%"+name,"%"+lastname,path,prefix);
			}
			else{
				ret_name = ge_operation(op,name,"%"+lastname,path,prefix);
			}
		}
		else if(vars.getId() ==ScannerTreeConstants.JJTLITERAL){
			lastname = vars.jjtGetFirstToken().toString();
			if(preid !=ScannerTreeConstants.JJTLITERAL){
				ret_name = ge_operation(op,"%"+name,lastname,path,prefix);
			}
			else{
				ret_name = ge_operation(op,name,lastname,path,prefix);
			}
		}
		return ret_name;
	}
	public String ge_loadarray(SimpleNode node,String path,String prefix)throws SemException{
		String ret_name="";
		SimpleNode ld = node;
		String name = node.jjtGetFirstToken().toString();
		array_info info = func_array_var.get(name);
		String index="";
		String instr="";
		if(((SimpleNode)ld.jjtGetChild(1)).jjtGetChild(0).getId() ==ScannerTreeConstants.JJTLITERAL){
			index = ((SimpleNode)((SimpleNode)ld.jjtGetChild(1)).jjtGetChild(0)).jjtGetFirstToken().toString();
			if(!func_array_var.containsKey(name)) {
                instr = prefix + "%arrayidx" + String.valueOf(arrayidx) +" = getelementptr inbounds i32* %" + name + ", i32 " + index + "\n";
            }
            else
                instr = prefix+"%arrayidx"+String.valueOf(arrayidx) +" = getelementptr inbounds ["+info.num+" x i32]* %"+name+", i32 0, i32 "+index+"\n";
			ret_name = "arrayidx"+String.valueOf(arrayidx);
			arrayidx=arrayidx+1;
		}
		else if(((SimpleNode)ld.jjtGetChild(1)).jjtGetChild(0).getId() ==ScannerTreeConstants.JJTNAME){
			index = ge_load(((SimpleNode)((SimpleNode)ld.jjtGetChild(1)).jjtGetChild(0)).jjtGetFirstToken().toString(),path,prefix);
			System.out.println(name);
			if(((MethodSymbol)sc.getMethodSymbol(path)).parameters.contains(name)){
				System.out.println("hah");
				name = ge_load(name+".addr",path,prefix);
			}
			else{
				name = ge_load(name,path,prefix);
			}
			if(!func_array_var.containsKey(name)) {
                instr = prefix + "%arrayidx" +String.valueOf(arrayidx) +" = getelementptr inbounds i32* %" + name + ", i32 %" + index;
            }
            else
                instr = prefix+"%arrayidx"+String.valueOf(arrayidx) +" = getelementptr inbounds ["+info.num+" x i32]* %"+name+", i32 0, i32 %"+index;
			ret_name = "arrayidx"+String.valueOf(arrayidx);
			arrayidx=arrayidx+1;
		}
		else if(((SimpleNode)ld.jjtGetChild(1)).jjtGetChild(0).getId() ==ScannerTreeConstants.JJTADDEXP||
				((SimpleNode)ld.jjtGetChild(1)).jjtGetChild(0).getId() ==ScannerTreeConstants.JJTMULTIEXP){
			index = ge_addop(ld,path,prefix);
			if(!func_array_var.containsKey(name)) {
                instr = prefix + "%arrayidx" + " = getelementptr inbounds i32* %" + name + ", i32 " + index + "\n";
            }
            else
                instr = prefix+"%arrayidx"+" = getelementptr inbounds ["+info.num+" x i32]* %"+name+", i32 0, i32 %"+index+"\n";
			ret_name = "arrayidx"+String.valueOf(arrayidx);
			arrayidx=arrayidx+1;
		}
		p.println(instr);
		return ret_name;
	}
//	public pair ge_add_expression(SimpleNode node,String prefix)throws SemException{
//        SimpleNode left = (SimpleNode) node.jjtGetChild(0);
//
//        int ex_num = node.jjtGetNumChildren();
//        if(ex_num==1)
//            return ge_mul_expression(left, prefix);
//        else {
//            pair left_pair =ge_mul_expression(left, prefix);
//
//            for (int i = 0; i < ex_num - 2; ) {
//
//                SimpleNode right = (SimpleNode) node.jjtGetChild(i + 2);
//                pair right_pair =ge_mul_expression(right,prefix);
//
//                SimpleNode ope = (SimpleNode) node.jjtGetChild(i + 1);
//                if(ope.jjtGetFirstToken().toString().equals("+"))
//                {
//                    ge_operation("+",left_pair.var,right_pair.var,prefix);
//                    left_pair.ret=1;
//                    left_pair.var="%add"+String.valueOf(add_num-1);
//                }
//                else if(ope.jjtGetFirstToken().toString().equals("-")){
//                    ge_operation("-",left_pair.var,right_pair.var,prefix);
//                    left_pair.ret=2;
//                    left_pair.var="%sub"+String.valueOf(sum_num-1);
//                }
//                i = i + 2;
//
//            }
//            return left_pair;
//        }
//    }
//	public pair ge_mul_expression(SimpleNode node,String prefix)throws SemException{
//        SimpleNode left = (SimpleNode) node.jjtGetChild(0);
//
//        int ex_num = node.jjtGetNumChildren();
//        if(ex_num==1)
//            return ge_pri_expression(left, prefix);
//        else {
//            pair left_pair =ge_pri_expression(left, prefix);
//
//            for (int i = 0; i < ex_num - 2; ) {
//
//                SimpleNode right = (SimpleNode) node.jjtGetChild(i + 2);
//                pair right_pair =ge_pri_expression(right,prefix);
//
//                SimpleNode ope = (SimpleNode) node.jjtGetChild(i + 1);
//                if(ope.jjtGetFirstToken().toString().equals("+"))
//                {
//                    ge_operation("*",left_pair.var,right_pair.var,prefix);
//                    left_pair.ret=1;
//                    left_pair.var="%mul"+String.valueOf(mul_num-1);
//                }
//                else if(ope.jjtGetFirstToken().toString().equals("-")){
//                    ge_operation("/",left_pair.var,right_pair.var,prefix);
//                    left_pair.ret=2;
//                    left_pair.var="%div"+String.valueOf(div_num-1);
//                }
//                else if(ope.jjtGetFirstToken().toString().equals("-")){
//                    ge_operation("%",left_pair.var,right_pair.var,prefix);
//                    left_pair.ret=3;
//                    left_pair.var="%mod"+String.valueOf(mod_num-1);
//                }
//                i = i + 2;
//
//            }
//            return left_pair;
//        }
//    }
//	public pair ge_pri_expression(SimpleNode node,String prefix)throws SemException{
//		SimpleNode left = (SimpleNode) node.jjtGetChild(0);
//		pair leftex = ge_prefix_expression(left,prefix);
//		if(leftex.ret ==2){
//			
//		}
//		return leftex;
//		
//	}
//	public pair ge_prefix_expression(SimpleNode node,String prefix)throws SemException{
//		SimpleNode left = (SimpleNode) node.jjtGetChild(0);
//		if(left.getId() ==ScannerTreeConstants.JJTNAME){
//			pair add = new pair(2,left.jjtGetFirstToken().toString());
//			return add;
//		}
//		else if(left.getId() ==ScannerTreeConstants.JJTLITERAL){
//			pair add = new pair(0,left.jjtGetFirstToken().toString());
//			return add;
//		}
//		else if(left.jjtGetFirstToken().toString().equals("(")){
//			pair add = ge_add_expression(left,prefix);
//			return add;
//		}
//		else if(left.getId() ==ScannerTreeConstants.JJTPRIMARYEXPRESSION){
//			
//		}
//		return null;
//	}
	public static String returnclassname(String s){
		String[] strs=s.split("[@]");
		return strs[0];
	}
	public static String return_class_name(String s){
		String[] strs=s.split("[.]");
		return strs[0];
	}
	public void ge_call(SimpleNode node,String path,String prefix)throws SemException{
		//System.out.println(path);
		SimpleNode call = node;
		String func_name = node.jjtGetFirstToken().toString();
		String class_name =returnclassname(path);
		//System.out.println(class_name);
		String tmp = class_name+"@"+func_name;
		String instr="";
		sc.getVarSymbol(class_name);
		MethodSymbol symbol1 = (MethodSymbol)sc.getMethodSymbol(tmp);
		//System.out.println(tmp);
		if(!symbol1.retType.equals("void")){
            instr = instr+"%call"+String.valueOf(call_num)+" = "+"call i32 @"+func_name+"(";
            call_num++;
        }
        else{
            instr = instr +"call void @"+func_name+"(";
        }
		System.out.println(instr);
		SimpleNode Args = (SimpleNode)call.jjtGetChild(1);
		for(int i=0;i<symbol1.paraTypes.size();++i){
			SimpleNode Arg = (SimpleNode)Args.jjtGetChild(i);
			if(Arg.getId() ==ScannerTreeConstants.JJTLITERAL){
				instr = instr + "i32 "+Arg.jjtGetFirstToken().toString();
			}
			else if(Arg.getId() ==ScannerTreeConstants.JJTNAME){
				String varname = Arg.jjtGetFirstToken().toString();
				if(func_array_var.containsKey(varname)){
					String instrs = "%arraydecay"+String.valueOf(arraydecay) +"= getelementptr inbounds ["+String.valueOf(func_array_var.get(varname).num)+" x i32]* %"+varname+", i32 0, i32 0";
					String names = "arraydecay"+String.valueOf(arraydecay);
					arraydecay = arraydecay+1;
					p.println(instrs);
					instr = instr + "i32* %"+names;
				}
				else if(is_para_array(varname,path)){
					instr = instr +"i32* %"+varname;
				}
				else{
					String aaa = ge_load(Arg.jjtGetFirstToken().toString(),path,prefix);
					instr = instr + "i32 %"+aaa;
				}
			}
			if(i ==symbol1.paraTypes.size()-1){
				instr = instr +")";
			}
			else{
				instr = instr +", ";
			}
		}
		p.println(instr);
	}
	public boolean is_para_array(String varname,String path){
		MethodSymbol symbol = (MethodSymbol)sc.getMethodSymbol(path);
		if(symbol.parameters.contains(varname)){
			for(int i=0;i<symbol.parameters.size();++i){
				if(symbol.parameters.get(i).equals(varname)){
					if(symbol.paraTypes.get(i).equals("int[]")){
						return true;
					}
				}
			}
		}
		return false;
	}
    public void ge_if(SimpleNode node,String path,String prefix)throws SemException{
        int child_num = node.jjtGetNumChildren();
        int cur_end_num = end_num;
        end_num = end_num+1;
        if(child_num==2) {
		}
        for(int i=0;i<child_num;i++){
            SimpleNode child = (SimpleNode)node.jjtGetChild(i);
            if(i==0){
                child.jjtGetChild(0);
                logical(child,path, prefix);
                ge_if_jump(prefix, cur_end_num);
            }
            else if(i==1){
                String sub_prefix = prefix.substring(0,prefix.length()-2);
                String instr = sub_prefix+"if.then"+String.valueOf(then_num)+":";
                p.println(instr);
                then_num = then_num+1;
               // SimpleNode block_node = (SimpleNode)
                run_block(child,path,prefix);
                String instr2 = prefix+"br label %if.end"+String.valueOf(cur_end_num);
                p.println(instr2);
            }
            else if(i==2){
                if(i==child_num-1) {
				}
                String sub_prefix = prefix.substring(0,prefix.length()-2);
                String instr = sub_prefix+"if.else"+String.valueOf(else_num)+":";
                else_num = else_num+1;
                p.println(instr);
                child.jjtGetChild(0).jjtGetChild(0);

                String sub_prefix2 = prefix.substring(0,prefix.length()-2);
                String instr2 = sub_prefix2+"if.then"+String.valueOf(then_num)+":";
                p.println(instr2);
                then_num = then_num+1;
                SimpleNode block_node = (SimpleNode)child.jjtGetChild(1);
                run_block(block_node,path,prefix+"  ");

                String instr3 = prefix+"br label %if.end"+String.valueOf(cur_end_num)+"";
                p.println(instr3);

            }
        }
        String sub_prefix = prefix.substring(0,prefix.length()-2);
        String instr =sub_prefix+ "if.end"+String.valueOf(cur_end_num)+":";
        p.println(instr);
    }
	public void ge_while(SimpleNode node,String path,String prefix)throws SemException{
        int cur_while_num=while_num;
        while_num=while_num+1;
        String sub_prefix = prefix.substring(0,prefix.length()-2);
        String instr =prefix+ "br label %while.cond"+String.valueOf(cur_while_num)+"\n";
        p.println(instr);

        String instr1 = sub_prefix+ "while.cond"+String.valueOf(cur_while_num)+":";
        p.println(instr1);

        SimpleNode logical = (SimpleNode)node.jjtGetChild(0);
        logical(logical,path,prefix);
        ge_while_jump(prefix,cur_while_num);
        SimpleNode block = (SimpleNode)node.jjtGetChild(1);
        String instr2 = sub_prefix+ "while.body"+String.valueOf(cur_while_num)+":";
        p.println(instr2);
        run_block(block,path,prefix+"  ");
        String instr3 = prefix+ "br label %while.cond"+String.valueOf(cur_while_num)+"\n";
        p.println(instr3);
        String instr4 = sub_prefix+ "while.end"+String.valueOf(cur_while_num)+":";
        p.println(instr4);
    }
	public void ge_alloc(String var,String prefix){
		String instr=prefix+"%"+var+" = "+"alloca i32, align 4";
	    p.println(instr);
	}

	public void ge_alloc_array(String var,String cnt,String prefix){
		String instr=prefix+"%"+var+" = "+"alloca ["+String.valueOf(cnt)+" x i32], align 4";
		p.println(instr);
	}
	public String ge_load(String var,String path,String prefix){
		String varname = return_class_name(var);
		MethodSymbol symbol = (MethodSymbol)sc.getMethodSymbol(path);
		String instr="";
		if(symbol.parameters.contains(varname)){
			int i=0;
			for(;i<symbol.parameters.size();++i){
				if(symbol.parameters.get(i).equals(varname)){
					if(symbol.paraTypes.get(i).equals("int[]") ||symbol.paraTypes.get(i).equals("bool[]")
							||symbol.paraTypes.get(i).equals("double[]")||symbol.paraTypes.get(i).equals("char[]")){
						instr=prefix+"%"+String.valueOf(var_num)+" = load i32** %"+var+", align 4";
					}
					else{
						instr=prefix+"%"+String.valueOf(var_num)+" = load i32* %"+varname+".addr, align 4";
					}
				}
			}
		}
		else{
			instr=prefix+"%"+String.valueOf(var_num)+" = load i32* %"+var+", align 4";
		}
	    p.println(instr);
	    String ret = String.valueOf(var_num);
	    var_num=var_num+1;
	    return ret;
	}
	public void ge_store(String value,String var,String path,String prefix){
		String instr="";
		if(value.length() >8){
			String tmp = value.substring(0,8);
			//System.out.println(tmp);
			if(tmp.equals("arrayidx")){
				value = ge_load(value,path,prefix);
			}
		}
			instr = prefix+"store i32 %"+ value+", i32* %"+var+", align 4";
	    p.println(instr);
    }
	public String ge_operation(String ope,String left_var,String right_var,String path,String prefix){
		String name="";
		//String left = ge_load(left_var,path,prefix);
		if(ope.equals("+")){
			String instr = prefix + "%add"+String.valueOf(add_num)+" = add nsw i32 "+left_var+", "+right_var;
			name = "add"+String.valueOf(add_num);
	        p.println(instr);
	        add_num = add_num+1;
	    }
        else if(ope.equals("-")){
        	String instr = prefix + "%sum"+String.valueOf(sum_num)+" = sub nsw i32 "+left_var+", "+right_var;
        	name = "sum"+String.valueOf(sum_num);
	        p.println(instr);
	        sum_num = sum_num+1;
        }
        else if(ope.equals("*")){
	        String instr = prefix + "%mul"+String.valueOf(mul_num)+" = mul nsw i32 "+left_var+", "+right_var;
	        name = "mul"+String.valueOf(mul_num);
	        p.println(instr);
	        mul_num = mul_num+1;
        }
        else if(ope.equals("/")){
	        String instr = prefix + "%div"+String.valueOf(div_num)+" = sdiv i32 "+left_var+", "+right_var;
	        name = "div"+String.valueOf(div_num);
	        p.println(instr);
	        div_num = div_num+1;
        }
	    else if(ope.equals("%")){
	    	String instr = prefix + "%rem"+String.valueOf(mod_num)+" = srem i32 "+left_var+", "+right_var;
	    	name = "rem"+String.valueOf(mod_num);
	        p.println(instr);
	        mod_num = mod_num+1;
	    }
		return name;
    }
	public void ge_print(SimpleNode node,String path,String prefix){
		//System.out.println("print");
		SimpleNode print = (SimpleNode)((SimpleNode)node.jjtGetChild(1)).jjtGetChild(0);
        String instr="";
        String var ="";
        if(print.getId() ==ScannerTreeConstants.JJTLITERAL){
        	var = print.jjtGetFirstToken().toString();
        	instr = prefix + "%call" + String.valueOf(call_num) + " = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.str,     i32 0, i32 0), i32 " + var + ")";
        }
        else if(print.getId() ==ScannerTreeConstants.JJTNAME){
        	var = print.jjtGetFirstToken().toString();
        	String tmp = ge_load(var,path,prefix);
        	instr = prefix + "%call" + String.valueOf(call_num) + " = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.str,     i32 0, i32 0), i32 %" + tmp + ")";
        }
        p.println(instr);
        call_num = call_num+1;
    }
    public void ge_return(String ret,String prefix){
        String instr =prefix+ "ret i32 "+ret;
        var_num++;
        p.println(instr);

    }
    public void ge_if_jump(String prefix,int cur_end_num){
//        if(or_last<=0 && and_last<=0){
//            String instr =prefix+ "br i1 %cmp"+String.valueOf(cmp_num-1)+", label %land.lhs.true"+String.valueOf(true_num)+", label %lor.lhs.false"+String.valueOf(false_num)+"\n\n";
//            p.println(instr);
//        }
//        else if(or_last>=1 && and_last<=0){
//            if(if_last==0) {
//                String instr = prefix + "br i1 %cmp" + String.valueOf(cmp_num - 1) + ", label %land.lhs.true" + String.valueOf(true_num) + ", label %if.else" + String.valueOf(else_num) + "\n\n";
//                p.println(instr);
//            }
//            else{
//                String instr = prefix + "br i1 %cmp" + String.valueOf(cmp_num - 1) + ", label %land.lhs.true" + String.valueOf(true_num) + ", label %if.end" + String.valueOf(cur_end_num) + "\n\n";
//                p.println(instr);
//            }
//        }
//        else if(or_last<=0 && and_last>=1){
//            String instr = prefix +"br i1 %cmp"+String.valueOf(cmp_num-1)+", label %if.then"+String.valueOf(then_num)+", label %lor.lhs.false"+String.valueOf(false_num)+"\n\n";
//            p.println(instr);
//        }
//        else if(or_last>=1 && and_last>=1){
//            if(if_last==0) {
//                String instr = prefix + "br i1 %cmp" + String.valueOf(cmp_num-1) + ", label %if.then" + String.valueOf(then_num) + ", label %if.else" + String.valueOf(else_num) + "\n\n";
//                p.println(instr);
//            }
//            else if(if_last==1){
                String instr = prefix + "br i1 %cmp" + String.valueOf(cmp_num-1) + ", label %if.then" + String.valueOf(then_num) + ", label %if.end" + String.valueOf(cur_end_num) + "\n\n";
                p.println(instr);
//            }
//        }

    }
    public void ge_while_jump(String prefix,int cur_while_num){
//        if(or_last<=0 && and_last<=0){
//            String instr =prefix+ "br i1 %cmp"+String.valueOf(cmp_num-1)+", label %land.lhs.true"+String.valueOf(true_num)+", label %lor.lhs.false"+String.valueOf(false_num)+"\n\n";
//            p.println(instr);
//        }
//        else if(or_last>=1 && and_last<=0){
//                String instr = prefix + "br i1 %cmp" + String.valueOf(cmp_num - 1) + ", label %land.lhs.true" + String.valueOf(true_num) + ", label %while.end" + String.valueOf(cur_while_num) + "\n\n";
//                p.println(instr);
//        }
//        else if(or_last<=0 && and_last>=1){
//            String instr = prefix +"br i1 %cmp"+String.valueOf(cmp_num-1)+", label %while.body"+String.valueOf(cur_while_num)+", label %lor.lhs.false"+String.valueOf(false_num)+"\n\n";
//            p.println(instr);
//        }
//        else if(or_last>=1 && and_last>=1){
                String instr = prefix + "br i1 %cmp" + String.valueOf(cmp_num-1) + ", label %while.body" + String.valueOf(cur_while_num) + ", label %while.end" + String.valueOf(cur_while_num) + "\n\n";
                p.println(instr);
        //}
    }
    public String ge_cmp(String cmp,String left_var,String right_var,String prefix,int con){
    	String name = "cmp"+String.valueOf(cmp_num);
    	if((cmp.equals("=="))){
    		String instr =prefix+ "%cmp"+String.valueOf(cmp_num)+" = icmp eq i32 "+left_var+", "+right_var;
    		cmp_num = cmp_num+1;
    		p.println(instr);
    	}
    	else if((cmp.equals("!="))){
    		String instr =prefix+ "%cmp"+String.valueOf(cmp_num)+" = icmp ne i32 "+left_var+", "+right_var;
    		cmp_num = cmp_num+1;
    		p.println(instr);
    	}
    	else if((cmp.equals(">"))){
    		String instr =prefix+ "%cmp"+String.valueOf(cmp_num)+" = icmp sgt i32 "+left_var+", "+right_var;
    		cmp_num = cmp_num+1;
    		p.println(instr);
    	}
    	else if((cmp.equals(">="))){
    		String instr =prefix+ "%cmp"+String.valueOf(cmp_num)+" = icmp sge i32 "+left_var+", "+right_var;
    		cmp_num = cmp_num+1;
    		p.println(instr);
    	}
    	else if((cmp.equals("<"))){
    		String instr =prefix+"%cmp"+String.valueOf(cmp_num)+" = icmp slt i32 "+left_var+", "+right_var;
    		cmp_num = cmp_num+1;
    		p.println(instr);
    	}
    	else if((cmp.equals("<="))){
    		String instr = prefix+"%cmp"+String.valueOf(cmp_num)+" = icmp sle i32 "+left_var+", "+right_var;
    		cmp_num = cmp_num+1;
    		p.println(instr);
    	}
    	return name;
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
    int num;

    array_info(String d,int c2){
        type=d;
        num=c2;
    }
}
class class_var_info{
	String type;
	String preority;
	String name;
	String value;
	class_var_info(String p,String t,String n,String v){
		type = t;
		preority=p;
		name=n;
		value = v;
	}
}
class func_var_info{
	String type;
	String name;
	String value;
	func_var_info(String t,String n,String v){
		type = t;
		name=n;
		value = v;
	}
}