package jmmCompiler.gui;

import java.awt.BorderLayout;
//首先导入Swing需要的包  
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import jsyntaxpane.DefaultSyntaxKit;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import jmmCompiler.lexical.Node;

public class Gui {
	//创建主方法    
	static MenuBar mb;  
	static Menu menuFile,menuAnalysis,menuRun;  
	static MenuItem mItemNew;
	static MenuItem mItemOpen;
	static MenuItem mItemSave;
	static MenuItem mItemLexical;
	static MenuItem mItemSyntax;
	static MenuItem mItemRun;
	static File file = new File("default.jmm");
	
	public Gui(){
		FileOutputStream out;
		try {
			out = new FileOutputStream(file.getAbsolutePath());
			String filecontent = "";
			for (int i=0;i < filecontent.length();i++)
				out.write(filecontent.charAt(i));
			out.close();  
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
	}
	
	public static void main(String[] args) {  
		try {        //try语句块，监视该段程序 
			//设置窗口风格 
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
		} catch (Exception e) {     //捕获异常 
			e.printStackTrace();    //异常信息输出 
		} 
		JFrame frame = new JFrame("jmm编译器");//创建顶层容器并初始化  
		final Analysis ana = new Analysis();
		
		//-------------代码显示区域----------------
		final JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);
        final JEditorPane codeEditor = new JEditorPane();
		
		//-------------------------菜单-------------------------------
		mb = new MenuBar(); // 创建菜单栏MenuBar  
		
		menuFile = new Menu("文件"); 
		mItemOpen = new MenuItem("打开"); 
		menuFile.add(mItemOpen);  
		mItemSave = new MenuItem("保存");  //menuFile.addSeparator();  // 加入分割线 
		menuFile.add(mItemSave);  
		mb.add(menuFile);   // 菜单栏中加入“文件”菜单  

		menuAnalysis = new Menu("分析");
		mItemLexical = new MenuItem("词法分析"); 
		menuAnalysis.add(mItemLexical);
		mItemSyntax = new MenuItem("语法分析");
		menuAnalysis.add(mItemSyntax);
		mb.add(menuAnalysis);

		menuRun = new Menu("运行");
		mItemRun = new MenuItem("运行程序"); 
		menuRun.add(mItemRun);
		mb.add(menuRun);
      
		//-------------词法分析结果--------
		final JTextArea jta_lex = new JTextArea(10, 52);
		jta_lex.setLineWrap(true);// 如果内容过长。自动换行
		jta_lex.setText("词法分析结果显示区");
		final JScrollPane jsp_lex = new JScrollPane(jta_lex);
      
		//--------------------语法分析结果-------------- 
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode("语法分析结果");
		final JTree tree = new JTree(top);
		final JScrollPane jsp_syn = new JScrollPane(tree);

		//--------------------输出显示--------------
		final JTextArea jta_output = new JTextArea(10, 52);
		jta_output.setLineWrap(true);// 如果内容过长。自动换行
		jta_output.setText("");
		final JScrollPane jsp_output = new JScrollPane(jta_output);

		//--------------------错误显示--------------
		final JTextArea jta_error = new JTextArea(10, 52);
		jta_error.setLineWrap(true);// 如果内容过长。自动换行
		jta_error.setText("");
		final JScrollPane jsp_error = new JScrollPane(jta_error);
		
		//-----------------响应与监听-----------------
		mItemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				jfc.showDialog(new JLabel(), "选择");
				file=jfc.getSelectedFile();
				String filecontent="";
		        try {  
		            // 读取文件内容 (输入流)  
		            FileInputStream out = new FileInputStream(file);  
		            InputStreamReader isr = new InputStreamReader(out);  
		            int ch = 0; 
		            while ((ch = isr.read()) != -1) {
		            	filecontent+=(char)ch;
		            }
		            isr.close();
		            out.close();
		        } catch (Exception e1) {  
		            e1.printStackTrace();  
		        }
		        codeEditor.setText(filecontent);
			}
		});
		
		mItemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileOutputStream out;
				try {
					out = new FileOutputStream(file.getAbsolutePath());
					String filecontent = codeEditor.getText();
					for (int i=0;i < filecontent.length();i++)
						out.write(filecontent.charAt(i));
					out.close();  
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}   
			}
		});

		mItemLexical.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//开始词法分析后，计算并显示结果
				try{
					String result = ana.ana_lex(file);
					jta_lex.setText(result);
					tab.setSelectedComponent(jsp_lex);
				} catch(Error elex){
					jta_error.setText(elex.getMessage());
					tab.setSelectedComponent(jsp_error);
				}
			}
		});

		mItemSyntax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//开始语法分析后，计算并显示结果
				try {
					Node root =ana.ana_syn(file);
					top.removeAllChildren();
					top.add(build(root));
					
					//----------更新节点-------------
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();  
					model.reload(top);
					tab.setSelectedComponent(jsp_syn);
				} catch(Exception esyn){
					jta_error.setText(esyn.getMessage());
					tab.setSelectedComponent(jsp_error);
				}
			}
			public DefaultMutableTreeNode build(Node curn){
				DefaultMutableTreeNode n = new DefaultMutableTreeNode(curn.toString());
				int i,num = curn.jjtGetNumChildren();
				for (i=0;i<num;i++) n.add(build(curn.jjtGetChild(i)));
				return n;
			}
		});

		mItemRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//开始词法分析后，计算并显示结果
				try{
					String result = ana.run(file);
					jta_output.setText(result);
					tab.setSelectedComponent(jsp_output);
				} catch(Exception erun){
					jta_error.setText(erun.getMessage());
					tab.setSelectedComponent(jsp_error);
				}
			}
		});
      
		//-----------------最终面板摆放-----------------
		DefaultSyntaxKit.initKit(); 
        JScrollPane scrPane = new JScrollPane(codeEditor);
        codeEditor.setContentType("text/java");
        codeEditor.setText("");
        frame.getContentPane().add(scrPane);
        
		tab.add(jsp_lex,"词法分析");
		tab.add(jsp_syn,"语法分析");
		tab.add(jsp_output,"运行输出");
		tab.add(jsp_error,"报错信息");
		frame.getContentPane().add(tab, BorderLayout.SOUTH);
      
		// setMenuBar:将此窗体的菜单栏设置为指定的菜单栏。  
		frame.setMenuBar(mb);   
		frame.setVisible(true); 
		
		//窗口设置结束，开始显示 
		frame.addWindowListener(new WindowAdapter() {  
			//匿名类用于注册监听器 
			public void windowClosing(WindowEvent e) { 
				System.exit(0);
			}     //程序退出  
		});  
		frame.setSize(1920,1080);    //设置窗口大小 
		frame.setVisible(true);     //显示窗口 
	} 
}