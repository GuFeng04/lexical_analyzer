package com.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
 * ͼ�ν���
 * �������ո��ע�����
 * */
import javax.swing.*;
import javax.swing.text.BadLocationException;

import com.compiler.*;

public class Mainform extends JFrame {
	String sourcePath; // Դ�ļ�·��
	String LL1Path;  //LL1�ķ�����
	String wordListPath; //������ֶ�
	String fourElementPath; //��Ԫʽ
	LexAnalyse lexAnalyse; //�ʷ�����
	Parser parser;  //�﷨����
	
	/*
	 * ����������õ��Ĳ���
	 * */
	JTextArea sourseFile;//������ʾԴ�ļ����ı���
	TextArea jTextField; //�ı��༭
	JSplitPane jSplitPane1; //�б�
	JScrollPane jScrollPane; //���
	String a[];
	JList row;
	public Mainform() {
		this.init();
	}

	//��ʼ�����沼��
	public void init() {
		//���Ĳ�������
		Toolkit toolkit = Toolkit.getDefaultToolkit(); //Ĭ�Ϲ��߰�
		Dimension screen = toolkit.getScreenSize();  //��ȡ������Ļ��С
		setTitle("������");
		setSize(1100, 600); //�̶���ʼ��ʱ�����С
		super.setResizable(true); //����Ϊ�ɸı䴰���С
		super.setLocation(screen.width / 2 - this.getWidth() / 2, screen.height
				/ 2 - this.getHeight() / 2);
		this.setContentPane(this.createContentPane());//��ʼ��һ����������������������ӿؼ�
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); //�����趨���ڱ��رյ���Ϊ
	}

	private JPanel createContentPane() { //��ӿؼ�����
		JPanel p = new JPanel(new BorderLayout()); //�߽粼�ַ�����һ������

		//NORTH����䣬���ҵ���creatBottomPane()����
		//CENTER���Ĳ��֣����ҵ���createcCenterPane()����*1*
		p.add(BorderLayout.NORTH, creatBottomPane());
		p.add(BorderLayout.CENTER, createcCenterPane()); 
		return p;
	}

	//*1*
	private Component createcCenterPane() {
		
		jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
		JPanel p = new JPanel(new BorderLayout());
		JLabel bt = new JLabel(" ����");
		sourseFile = new JTextArea(); //�����µ� JTextArea
		
		Font mf = new Font("����",Font.PLAIN,16); //����ʹ�С
		sourseFile.setFont(mf);
		
		a=new String[15]; //��ָ����Χ����ʾ���
		for(int i=0;i<15;i++){
			a[i]=String.valueOf(i+1);
		}
		row=new JList(a); //���ù�������
		row.setForeground(Color.red); //������� 
		
		
		jScrollPane=new JScrollPane(sourseFile);
		mf = new Font("����",Font.PLAIN,16);
		row.setFont(mf);
		jScrollPane.setRowHeaderView(row);
		sourseFile.setForeground(Color.BLACK);
		p.add( bt,BorderLayout.NORTH);
		p.add(jScrollPane,BorderLayout.CENTER );
		
		
		JPanel p2 = new JPanel(new BorderLayout());
		JLabel label2 = new JLabel(" �����");
		jTextField = new TextArea(1,100);
		mf = new Font("����",Font.PLAIN,16);
		jTextField.setFont(mf);
		jTextField.setEditable(false);
		jTextField.setForeground(Color.BLACK); //������ɫ
		p2.add(label2, BorderLayout.NORTH);
		p2.add(jTextField, BorderLayout.CENTER);
		p2.setEnabled(true);
		
		jSplitPane1.add(p, JSplitPane.TOP);   
        jSplitPane1.add(p2, JSplitPane.BOTTOM);   
        jSplitPane1.setEnabled(true); 
        jSplitPane1.setOneTouchExpandable(true);
        
        jSplitPane1.setDividerSize(4);
		return jSplitPane1;
	}

	//Դ�ļ����ݲ��Ϸ�����Ӧ
	public void seterrorline(int i){
		try {
			sourseFile.requestFocus();
			int selectionStart = sourseFile.getLineStartOffset(i-1);
			int selectionEnd = sourseFile.getLineEndOffset(i-1);
			sourseFile.setSelectionStart(selectionStart);
			sourseFile.setSelectionEnd(selectionEnd);
			sourseFile.setSelectionColor(Color.red); //��������������Ϊ��ɫ
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	//*2*
	private Component creatBottomPane() {
		JPanel p = new JPanel(new FlowLayout());

		JButton bt0 = new JButton("ѡ���ļ�");
		bt0.setPreferredSize(new Dimension(200,50));
		bt0.setBackground(Color.white);
		Font f=new Font("����",Font.BOLD,14);
		bt0.setFont(f);

		JButton bt1 = new JButton("�ʷ�����");
		bt1.setPreferredSize(new Dimension(200,50));
		bt1.setBackground(Color.white);
		bt1.setFont(f);
		
		JButton bt2 = new JButton("�﷨����");
		bt2.setPreferredSize(new Dimension(200,50));
		bt2.setBackground(Color.white);
		bt2.setFont(f);
		
		JButton bt3 = new JButton("����������м��������");
		bt3.setPreferredSize(new Dimension(200,50));
		bt3.setBackground(Color.white);
		bt3.setFont(f);
		
		JButton bt4 = new JButton("Ŀ���������");
		bt4.setPreferredSize(new Dimension(200,50));
		bt4.setBackground(Color.white);
		bt4.setFont(f);
		//��ӿؼ�
		p.add(bt0);
		p.add(bt1);
		p.add(bt2);
		p.add(bt3);
		p.add(bt4);
		bt0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(".")); //��ת����ǰĿ¼
				int ret = chooser.showOpenDialog(new JPanel());
				if (ret == JFileChooser.APPROVE_OPTION) { //��ȡ����·��
					String text;
					try {
						sourcePath =chooser.getSelectedFile().getPath();
						text = readFile(sourcePath);
						sourseFile.setText(text);		  //����ȡ��������ʾ���ı�����
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		//���ʷ���������ť��������
		bt1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(sourseFile.getText().equals("")){ //��ȡ�ı��������
					return;
				}
				try {
					lexAnalyse=new LexAnalyse(sourseFile.getText());
					wordListPath = lexAnalyse.outputWordList(); //��Ҫ�õ����ʱ�
					if(lexAnalyse.isFail()){ //�ʷ��������ɹ���ָ������������
						int i=lexAnalyse.errorList.get(0).line;
						seterrorline(i);
					}
					jTextField.setText(readFile(wordListPath));
					jTextField.setCaretPosition(jTextField.getText().length()); //
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			
		});
		//�﷨����
		bt2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(sourseFile.getText().equals("")){
					return;
				}
			lexAnalyse=new LexAnalyse(sourseFile.getText());
			parser=new Parser(lexAnalyse);
				try {
					parser.grammerAnalyse();
					if(parser.graErrorFlag){
						int i=parser.errorList.get(0).line;
						seterrorline(i);
					}
					LL1Path= parser.outputLL1();
					jTextField.setText(readFile(LL1Path));
					jTextField.setCaretPosition(jTextField.getText().length());
					LexAnalyse.typelist.clear();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		//�м��������
		bt3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(sourseFile.getText().equals("")){
					return;
				}
				try {
					lexAnalyse=new LexAnalyse(sourseFile.getText());
					parser=new Parser(lexAnalyse);
					parser.grammerAnalyse();
					fourElementPath=parser.outputFourElem(); //������Ԫʽ���ɵ�class
					jTextField.setText(readFile(fourElementPath));
					jTextField.setCaretPosition(jTextField.getText().length());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		//Ŀ���������
		bt4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {			
				//��ȡ��ʶ��
				lexAnalyse = new LexAnalyse(sourseFile.getText());
				ArrayList<Word> wordList;
				wordList = lexAnalyse.getWordList();
				ArrayList<String> id = new ArrayList<String>();
				for (int i = 0; i < wordList.size(); i++) { //�ȶԻ�ȡ�ĵ��ʺ��趨���Ƿ�һ��
					if(wordList.get(i).type.equals(Word.IDENTIFIER)){					
					if(!id.contains(wordList.get(i).value)){
					id.add(wordList.get(i).value);
					System.out.println(wordList.get(i).value);
					}
					}
				}
		
				parser = new Parser(lexAnalyse);
				parser.grammerAnalyse(); //�Ա��﷨����
				Asm asm = new Asm(parser.fourElemList, id, parser.fourElemT);
				 
				try {
					jTextField.setText(readFile(asm.getAsmFile()));
					jTextField.setCaretPosition(jTextField.getText().length());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		return p;
	}
	//�����ļ�����������
	public static String readFile(String fileName) throws IOException {
		StringBuilder sbr = new StringBuilder();
		String str;
		FileInputStream fis = new FileInputStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(fis);
		 //���ֽ���ת�����ַ�������ָ���ַ���
		InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
		/*
		 * ���ַ����Ի������ʽһ��һ�����
		 * ������Դ�ļ���ʱ���е���
		 * */
		BufferedReader in = new BufferedReader(isr);
		while ((str = in.readLine()) != null) {
			sbr.append(str).append('\n');//������ʾ
		}
		in.close();
		return sbr.toString();
	}

	public static void main(String[] args) {
		Mainform mf = new Mainform();
		mf.setVisible(true); //��ʾ����
		mf.jSplitPane1.setDividerLocation(0.7);
	}
	
}
