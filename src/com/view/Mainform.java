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
 * 图形界面
 * 可跳过空格和注释语句
 * */
import javax.swing.*;
import javax.swing.text.BadLocationException;

import com.compiler.*;

public class Mainform extends JFrame {
	String sourcePath; // 源文件路径
	String LL1Path;  //LL1文法分析
	String wordListPath; //定义的字段
	String fourElementPath; //四元式
	LexAnalyse lexAnalyse; //词法分析
	Parser parser;  //语法分析
	
	/*
	 * 窗体界面设置到的参数
	 * */
	JTextArea sourseFile;//用来显示源文件的文本框
	TextArea jTextField; //文本编辑
	JSplitPane jSplitPane1; //列表
	JScrollPane jScrollPane; //面板
	String a[];
	JList row;
	public Mainform() {
		this.init();
	}

	//初始化界面布局
	public void init() {
		//面板的参数设置
		Toolkit toolkit = Toolkit.getDefaultToolkit(); //默认工具包
		Dimension screen = toolkit.getScreenSize();  //获取本机屏幕大小
		setTitle("编译器");
		setSize(1100, 600); //固定初始化时窗体大小
		super.setResizable(true); //设置为可改变窗体大小
		super.setLocation(screen.width / 2 - this.getWidth() / 2, screen.height
				/ 2 - this.getHeight() / 2);
		this.setContentPane(this.createContentPane());//初始化一个容器，用来在容器上添加控件
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); //用来设定窗口被关闭的行为
	}

	private JPanel createContentPane() { //添加控件函数
		JPanel p = new JPanel(new BorderLayout()); //边界布局法创建一个容器

		//NORTH满填充，并且调用creatBottomPane()函数
		//CENTER中心布局，并且调用createcCenterPane()函数*1*
		p.add(BorderLayout.NORTH, creatBottomPane());
		p.add(BorderLayout.CENTER, createcCenterPane()); 
		return p;
	}

	//*1*
	private Component createcCenterPane() {
		
		jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
		JPanel p = new JPanel(new BorderLayout());
		JLabel bt = new JLabel(" 程序：");
		sourseFile = new JTextArea(); //构造新的 JTextArea
		
		Font mf = new Font("宋体",Font.PLAIN,16); //字体和大小
		sourseFile.setFont(mf);
		
		a=new String[15]; //在指定范围内显示序号
		for(int i=0;i<15;i++){
			a[i]=String.valueOf(i+1);
		}
		row=new JList(a); //设置滚动换行
		row.setForeground(Color.red); //序号字体 
		
		
		jScrollPane=new JScrollPane(sourseFile);
		mf = new Font("宋体",Font.PLAIN,16);
		row.setFont(mf);
		jScrollPane.setRowHeaderView(row);
		sourseFile.setForeground(Color.BLACK);
		p.add( bt,BorderLayout.NORTH);
		p.add(jScrollPane,BorderLayout.CENTER );
		
		
		JPanel p2 = new JPanel(new BorderLayout());
		JLabel label2 = new JLabel(" 结果：");
		jTextField = new TextArea(1,100);
		mf = new Font("宋体",Font.PLAIN,16);
		jTextField.setFont(mf);
		jTextField.setEditable(false);
		jTextField.setForeground(Color.BLACK); //字体颜色
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

	//源文件内容不合法的响应
	public void seterrorline(int i){
		try {
			sourseFile.requestFocus();
			int selectionStart = sourseFile.getLineStartOffset(i-1);
			int selectionEnd = sourseFile.getLineEndOffset(i-1);
			sourseFile.setSelectionStart(selectionStart);
			sourseFile.setSelectionEnd(selectionEnd);
			sourseFile.setSelectionColor(Color.red); //将错误的语句设置为红色
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	//*2*
	private Component creatBottomPane() {
		JPanel p = new JPanel(new FlowLayout());

		JButton bt0 = new JButton("选择文件");
		bt0.setPreferredSize(new Dimension(200,50));
		bt0.setBackground(Color.white);
		Font f=new Font("宋体",Font.BOLD,14);
		bt0.setFont(f);

		JButton bt1 = new JButton("词法分析");
		bt1.setPreferredSize(new Dimension(200,50));
		bt1.setBackground(Color.white);
		bt1.setFont(f);
		
		JButton bt2 = new JButton("语法分析");
		bt2.setPreferredSize(new Dimension(200,50));
		bt2.setBackground(Color.white);
		bt2.setFont(f);
		
		JButton bt3 = new JButton("语义分析和中间代码生成");
		bt3.setPreferredSize(new Dimension(200,50));
		bt3.setBackground(Color.white);
		bt3.setFont(f);
		
		JButton bt4 = new JButton("目标代码生成");
		bt4.setPreferredSize(new Dimension(200,50));
		bt4.setBackground(Color.white);
		bt4.setFont(f);
		//添加控件
		p.add(bt0);
		p.add(bt1);
		p.add(bt2);
		p.add(bt3);
		p.add(bt4);
		bt0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(".")); //跳转到当前目录
				int ret = chooser.showOpenDialog(new JPanel());
				if (ret == JFileChooser.APPROVE_OPTION) { //获取绝对路径
					String text;
					try {
						sourcePath =chooser.getSelectedFile().getPath();
						text = readFile(sourcePath);
						sourseFile.setText(text);		  //将读取的内容显示在文本框中
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		//“词法分析”按钮动作监听
		bt1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(sourseFile.getText().equals("")){ //获取文本框的内容
					return;
				}
				try {
					lexAnalyse=new LexAnalyse(sourseFile.getText());
					wordListPath = lexAnalyse.outputWordList(); //需要用到单词表
					if(lexAnalyse.isFail()){ //词法分析不成功，指出错误所在行
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
		//语法分析
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
		//中间代码生成
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
					fourElementPath=parser.outputFourElem(); //调用四元式生成的class
					jTextField.setText(readFile(fourElementPath));
					jTextField.setCaretPosition(jTextField.getText().length());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		//目标代码生成
		bt4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {			
				//获取标识符
				lexAnalyse = new LexAnalyse(sourseFile.getText());
				ArrayList<Word> wordList;
				wordList = lexAnalyse.getWordList();
				ArrayList<String> id = new ArrayList<String>();
				for (int i = 0; i < wordList.size(); i++) { //比对获取的单词和设定的是否一致
					if(wordList.get(i).type.equals(Word.IDENTIFIER)){					
					if(!id.contains(wordList.get(i).value)){
					id.add(wordList.get(i).value);
					System.out.println(wordList.get(i).value);
					}
					}
				}
		
				parser = new Parser(lexAnalyse);
				parser.grammerAnalyse(); //对比语法规则
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
	//读入文件（含出错处理）
	public static String readFile(String fileName) throws IOException {
		StringBuilder sbr = new StringBuilder();
		String str;
		FileInputStream fis = new FileInputStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(fis);
		 //将字节流转化成字符流，并指定字符集
		InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
		/*
		 * 将字符流以缓存的形式一行一行输出
		 * 即导入源文件的时候按行导入
		 * */
		BufferedReader in = new BufferedReader(isr);
		while ((str = in.readLine()) != null) {
			sbr.append(str).append('\n');//换行显示
		}
		in.close();
		return sbr.toString();
	}

	public static void main(String[] args) {
		Mainform mf = new Mainform();
		mf.setVisible(true); //显示窗体
		mf.jSplitPane1.setDividerLocation(0.7);
	}
	
}
