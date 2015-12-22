package jmmCompiler.gui;

//���ȵ���Swing��Ҫ�İ�  
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
	//����������    
	static MenuBar mb;  
	static Menu menuFile,menuAnalysis;  
	static MenuItem mItemNew;
	static MenuItem mItemOpen;
	static MenuItem mItemSave;
	static MenuItem mItemLexical;
	static MenuItem mItemSyntax;
	static File file;
	public static void main(String[] args) {  
		try {        //try���飬���Ӹöγ��� 
			//���ô��ڷ�� 
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
		} catch (Exception e) {     //�����쳣 
			e.printStackTrace();    //�쳣��Ϣ��� 
		} 
		JFrame frame = new JFrame("jmm������");//����������������ʼ��  
		JPanel pana = new JPanel();
		Analysis ana = new Analysis();
		pana.setLayout(new BoxLayout(pana,BoxLayout.Y_AXIS));
		
		//-------------------------�˵�-------------------------------
		mb = new MenuBar(); // �����˵���MenuBar  
		menuFile = new Menu("�ļ�"); 
		mItemNew = new MenuItem("�½�");  
		mItemNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		menuFile.add(mItemNew);  
      
		mItemOpen = new MenuItem("��"); 
		menuFile.add(mItemOpen);  
        
		mItemSave = new MenuItem("����");  
		menuFile.addSeparator();  // ����ָ��� 
		menuFile.add(mItemSave);  
		mb.add(menuFile);   // �˵����м��롰�ļ����˵�  
      
		menuAnalysis = new Menu("����");
		mItemLexical = new MenuItem("�ʷ�����"); 
		menuAnalysis.add(mItemLexical);
		mItemSyntax = new MenuItem("�﷨����");
		menuAnalysis.add(mItemSyntax);
		mb.add(menuAnalysis);
      
		//-------------������ʾ����----------------
		JTextArea jt = new JTextArea(50, 120);
		jt.setLineWrap(true);// ������ݹ������Զ�����
		JScrollPane scr = new JScrollPane(jt);
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
      
		//-------------�ʷ��������--------
		JTextArea jtl = new JTextArea(10, 52);
		jtl.setLineWrap(true);// ������ݹ������Զ�����
		jtl.setText("�ʷ����������ʾ��");
		JScrollPane scrlex = new JScrollPane(jtl);
		mItemLexical.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//��ʼ�ʷ������󣬼��㲢��ʾ���
				String result = ana.ana_lex(file);
				jtl.setText(result);
			}
		});
      
		//--------------------�﷨�������-------------- 

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("�﷨����");
		final JTree tree = new JTree(top);
		JScrollPane scrtree = new JScrollPane(tree);
		mItemSyntax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//��ʼ�﷨�����󣬼��㲢��ʾ���
				Node root =ana.ana_syn(file);
				top.removeAllChildren();
				top.add(build(root));
				
				//----------���½ڵ�-------------
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
      
		//-----------------�������ڷ�-----------------
		pana.add(scrlex);
		pana.add(scrtree);
		frame.getContentPane().add(scr, BorderLayout.WEST);
		frame.getContentPane().add(pana, BorderLayout.EAST);
      
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
