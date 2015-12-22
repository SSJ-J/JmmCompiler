package jmmCompiler.gui;

//首先导入Swing需要的包  
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
	static Menu menuFile,menuAnalysis;  
	static MenuItem mItemNew;
	static MenuItem mItemOpen;
	static MenuItem mItemSave;
	static MenuItem mItemLexical;
	static MenuItem mItemSyntax;
	static File file;
	public static void main(String[] args) {  
		try {        //try语句块，监视该段程序 
			//设置窗口风格 
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
		} catch (Exception e) {     //捕获异常 
			e.printStackTrace();    //异常信息输出 
		} 
		JFrame frame = new JFrame("jmm编译器");//创建顶层容器并初始化  
		JPanel pana = new JPanel();
		Analysis ana = new Analysis();
		pana.setLayout(new BoxLayout(pana,BoxLayout.Y_AXIS));
		
		//-------------------------菜单-------------------------------
		mb = new MenuBar(); // 创建菜单栏MenuBar  
		menuFile = new Menu("文件"); 
		mItemNew = new MenuItem("新建");  
		mItemNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		menuFile.add(mItemNew);  
      
		mItemOpen = new MenuItem("打开"); 
		menuFile.add(mItemOpen);  
        
		mItemSave = new MenuItem("保存");  
		menuFile.addSeparator();  // 加入分割线 
		menuFile.add(mItemSave);  
		mb.add(menuFile);   // 菜单栏中加入“文件”菜单  
      
		menuAnalysis = new Menu("分析");
		mItemLexical = new MenuItem("词法分析"); 
		menuAnalysis.add(mItemLexical);
		mItemSyntax = new MenuItem("语法分析");
		menuAnalysis.add(mItemSyntax);
		mb.add(menuAnalysis);
      
		//-------------代码显示区域----------------
		JTextArea jt = new JTextArea(50, 120);
		jt.setLineWrap(true);// 如果内容过长。自动换行
		JScrollPane scr = new JScrollPane(jt);
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
		            	// System.out.println(ch);
		            	filecontent+=(char)ch;
		            }

		            isr.close();
		            out.close();
		        } catch (Exception e1) {  
		            e1.printStackTrace();  
		        }
		        jt.setText(filecontent);
			}
		});
		mItemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileOutputStream out;
				try {
					out = new FileOutputStream(file.getAbsolutePath());
					String filecontent = jt.getText();
					for (int i=0;i < filecontent.length();i++)
						out.write(filecontent.charAt(i));
					out.close();  
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}   
			}
		});
      
		//-------------词法分析结果--------
		JTextArea jtl = new JTextArea(10, 52);
		jtl.setLineWrap(true);// 如果内容过长。自动换行
		jtl.setText("词法分析结果显示区");
		JScrollPane scrlex = new JScrollPane(jtl);
		mItemLexical.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//开始词法分析后，计算并显示结果
				String result = ana.ana_lex(file);
				jtl.setText(result);
			}
		});
      
		//--------------------语法分析结果-------------- 

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("语法分析");
		final JTree tree = new JTree(top);
		JScrollPane scrtree = new JScrollPane(tree);
		mItemSyntax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//开始语法分析后，计算并显示结果
				Node root =ana.ana_syn(file);
				top.removeAllChildren();
				top.add(build(root));
				
				//----------更新节点-------------
				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();  
				model.reload(top);
			}
			public DefaultMutableTreeNode build(Node curn){
				DefaultMutableTreeNode n = new DefaultMutableTreeNode(curn.toString());
				int i,num = curn.jjtGetNumChildren();
				for (i=0;i<num;i++) n.add(build(curn.jjtGetChild(i)));
				return n;
			}
		});
      
		//-----------------最终面板摆放-----------------
		pana.add(scrlex);
		pana.add(scrtree);
		frame.getContentPane().add(scr, BorderLayout.WEST);
		frame.getContentPane().add(pana, BorderLayout.EAST);
      
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
