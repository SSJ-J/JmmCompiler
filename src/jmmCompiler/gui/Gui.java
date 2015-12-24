package jmmCompiler.gui;

import java.awt.BorderLayout;
//���ȵ���Swing��Ҫ�İ�  
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
	//����������    
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
		try {        //try���飬���Ӹöγ��� 
			//���ô��ڷ�� 
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
		} catch (Exception e) {     //�����쳣 
			e.printStackTrace();    //�쳣��Ϣ��� 
		} 
		JFrame frame = new JFrame("jmm������");//����������������ʼ��  
		final Analysis ana = new Analysis();
		
		//-------------������ʾ����----------------
		final JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);
        final JEditorPane codeEditor = new JEditorPane();
		
		//-------------------------�˵�-------------------------------
		mb = new MenuBar(); // �����˵���MenuBar  
		
		menuFile = new Menu("�ļ�"); 
		mItemOpen = new MenuItem("��"); 
		menuFile.add(mItemOpen);  
		mItemSave = new MenuItem("����");  //menuFile.addSeparator();  // ����ָ��� 
		menuFile.add(mItemSave);  
		mb.add(menuFile);   // �˵����м��롰�ļ����˵�  

		menuAnalysis = new Menu("����");
		mItemLexical = new MenuItem("�ʷ�����"); 
		menuAnalysis.add(mItemLexical);
		mItemSyntax = new MenuItem("�﷨����");
		menuAnalysis.add(mItemSyntax);
		mb.add(menuAnalysis);

		menuRun = new Menu("����");
		mItemRun = new MenuItem("���г���"); 
		menuRun.add(mItemRun);
		mb.add(menuRun);
      
		//-------------�ʷ��������--------
		final JTextArea jta_lex = new JTextArea(10, 52);
		jta_lex.setLineWrap(true);// ������ݹ������Զ�����
		jta_lex.setText("�ʷ����������ʾ��");
		final JScrollPane jsp_lex = new JScrollPane(jta_lex);
      
		//--------------------�﷨�������-------------- 
		final DefaultMutableTreeNode top = new DefaultMutableTreeNode("�﷨�������");
		final JTree tree = new JTree(top);
		final JScrollPane jsp_syn = new JScrollPane(tree);

		//--------------------�����ʾ--------------
		final JTextArea jta_output = new JTextArea(10, 52);
		jta_output.setLineWrap(true);// ������ݹ������Զ�����
		jta_output.setText("");
		final JScrollPane jsp_output = new JScrollPane(jta_output);

		//--------------------������ʾ--------------
		final JTextArea jta_error = new JTextArea(10, 52);
		jta_error.setLineWrap(true);// ������ݹ������Զ�����
		jta_error.setText("");
		final JScrollPane jsp_error = new JScrollPane(jta_error);
		
		//-----------------��Ӧ�����-----------------
		mItemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				jfc.showDialog(new JLabel(), "ѡ��");
				file=jfc.getSelectedFile();
				String filecontent="";
		        try {  
		            // ��ȡ�ļ����� (������)  
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
				//��ʼ�ʷ������󣬼��㲢��ʾ���
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
				//��ʼ�﷨�����󣬼��㲢��ʾ���
				try {
					Node root =ana.ana_syn(file);
					top.removeAllChildren();
					top.add(build(root));
					
					//----------���½ڵ�-------------
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
				//��ʼ�ʷ������󣬼��㲢��ʾ���
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
      
		//-----------------�������ڷ�-----------------
		DefaultSyntaxKit.initKit(); 
        JScrollPane scrPane = new JScrollPane(codeEditor);
        codeEditor.setContentType("text/java");
        codeEditor.setText("");
        frame.getContentPane().add(scrPane);
        
		tab.add(jsp_lex,"�ʷ�����");
		tab.add(jsp_syn,"�﷨����");
		tab.add(jsp_output,"�������");
		tab.add(jsp_error,"������Ϣ");
		frame.getContentPane().add(tab, BorderLayout.SOUTH);
      
		// setMenuBar:���˴���Ĳ˵�������Ϊָ���Ĳ˵�����  
		frame.setMenuBar(mb);   
		frame.setVisible(true); 
		
		//�������ý�������ʼ��ʾ 
		frame.addWindowListener(new WindowAdapter() {  
			//����������ע������� 
			public void windowClosing(WindowEvent e) { 
				System.exit(0);
			}     //�����˳�  
		});  
		frame.setSize(1920,1080);    //���ô��ڴ�С 
		frame.setVisible(true);     //��ʾ���� 
	} 
}