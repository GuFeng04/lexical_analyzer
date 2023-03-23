package com.compiler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

/**
 * 语法分析器
 *
 * @author KB
 */
public class Parser {

    /**
     * @param args
     */


    private LexAnalyse lexAnalyse;//词法分析器
    ArrayList<Word> wordList = new ArrayList<Word>();//单词表
    Stack<AnalyseNode> analyseStack = new Stack<AnalyseNode>();//分析栈
    Stack<String> productionStack = new Stack<String>();//产生式栈
    Stack<String> semanticStack = new Stack<String>();//语义栈
    public ArrayList<FourElement> fourElemList = new ArrayList<FourElement>();//四元式列表

    public ArrayList<Error> errorList = new ArrayList<Error>();//错误信息列表
    StringBuffer bf; //（每个操作）分析栈缓冲流
    int errorCount = 0;//统计错误个数
    public boolean graErrorFlag = false;//语法分析出错标志
    int tempCount = 0; //用于生成临时变量
    int fourElemCount = 0;//统计四元式个数

    AnalyseNode S, B, A, C, X, Y, R, Z, Z1, U, U1, E, E1, H, H1, G, M, D, L, L1, T, T1, F, O, P, Q;//非终结符

    //语义动作----------------------------------------------------------------------------
    AnalyseNode ADD_SUB, DIV_MUL, ADD, SUB, DIV, MUL, SINGLE, SINGLE_OP;//算术运算（+，-，*，/，为ADD_SUB,DIV_MUL做OP的赋值操作）
    AnalyseNode ASS, ASS_U, TRAN_LF;//初始化(赋值)  ASS_U(=)
    AnalyseNode EQ;//, EQ_U1;//赋值;EQ_U1为恢复删除结点
    AnalyseNode COMPARE, COMPARE_OP, SCANF, PRINTF;//逻辑
    AnalyseNode IF_FJ, IF_RJ, IF_BACKPATCH_FJ, IF_BACKPATCH_RJ;//if;
    AnalyseNode WHILE_FJ, WHILE_RJ, WHILE_BACKPATCH_FJ;//while
    AnalyseNode FOR_FJ, FOR_RJ, FOR_BACKPATCH_FJ;//for
    AnalyseNode analyseTop;//当前栈顶元素
    Word firstWord;//待分析单词
    String OP = null;
    String ARG1, ARG2, RES;
    Error error;
    Stack<Integer> if_fj, if_rj, while_fj, while_rj, for_fj, for_rj;//if while for 跳转地址栈
    Stack<String> for_op = new Stack<String>();
    public ArrayList<String> fourElemT = new ArrayList<String>();

    public Parser() {

    }

    //词法分析的结果
    public Parser(LexAnalyse lexAnalyse) {
        this.lexAnalyse = lexAnalyse;
        this.wordList = lexAnalyse.wordList;
        init();
    }

    //生成四元式中的临时变量 T1，T2
    private String newTemp() {
        tempCount++;  //临时变量数目
        fourElemT.add("T" + tempCount);
        return "T" + tempCount;
    }

    //初始化AnalyseNode(非终结符、语义动作)
    public void init() {
        S = new AnalyseNode(AnalyseNode.NONTERMINAL, "S");
        A = new AnalyseNode(AnalyseNode.NONTERMINAL, "A");
        B = new AnalyseNode(AnalyseNode.NONTERMINAL, "B");
        C = new AnalyseNode(AnalyseNode.NONTERMINAL, "C");
        X = new AnalyseNode(AnalyseNode.NONTERMINAL, "X");
        Y = new AnalyseNode(AnalyseNode.NONTERMINAL, "Y");
        Z = new AnalyseNode(AnalyseNode.NONTERMINAL, "Z");
        Z1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "Z'");
        U = new AnalyseNode(AnalyseNode.NONTERMINAL, "U");
        U1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "U'");
        E = new AnalyseNode(AnalyseNode.NONTERMINAL, "E");
        E1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "E'");
        H = new AnalyseNode(AnalyseNode.NONTERMINAL, "H");
        H1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "H'");
        G = new AnalyseNode(AnalyseNode.NONTERMINAL, "G");
        F = new AnalyseNode(AnalyseNode.NONTERMINAL, "F");
        D = new AnalyseNode(AnalyseNode.NONTERMINAL, "D");
        L = new AnalyseNode(AnalyseNode.NONTERMINAL, "L");
        L1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "L'");
        T = new AnalyseNode(AnalyseNode.NONTERMINAL, "T");
        T1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "T'");
        O = new AnalyseNode(AnalyseNode.NONTERMINAL, "O");
        P = new AnalyseNode(AnalyseNode.NONTERMINAL, "P");
        Q = new AnalyseNode(AnalyseNode.NONTERMINAL, "Q");
        R = new AnalyseNode(AnalyseNode.NONTERMINAL, "R");

        //算术运算语义动作
        ADD_SUB = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ADD_SUB");
        ADD = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ADD");
        SUB = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SUB");
        DIV_MUL = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@DIV_MUL");
        DIV = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@DIV");
        MUL = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@MUL");
        SINGLE = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SINGLE");
        SINGLE_OP = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SINGLE_OP");

        //初始化(赋值)语义动作
        ASS = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS");
        ASS_U = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS_U");
        TRAN_LF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@TRAN_LF");

        //赋值语义动作
        EQ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@EQ");
        //EQ_U1 = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@EQ_U'");

        //逻辑操作语义动作
        COMPARE = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@COMPARE");
        COMPARE_OP = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@COMPARE_OP");

        //if语句语义动作
        IF_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_FJ");
        SCANF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SCANF");
        PRINTF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@PRINTF");
        IF_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_RJ");
        IF_BACKPATCH_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_BACKPATCH_FJ");
        IF_BACKPATCH_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_BACKPATCH_RJ");

        //while语句语义动作
        WHILE_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@WHILE_FJ");
        WHILE_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@WHILE_RJ");
        WHILE_BACKPATCH_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@WHILE_BACKPATCH_FJ");

        //for语句语义动作
        FOR_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@FOR_FJ");
        FOR_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@FOR_RJ");
        FOR_BACKPATCH_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@FOR_BACKPATCH_FJ");

        if_fj = new Stack<Integer>();
        if_rj = new Stack<Integer>();
        while_fj = new Stack<Integer>();
        while_rj = new Stack<Integer>();
        for_fj = new Stack<Integer>();
        for_rj = new Stack<Integer>();

    }


    public void grammerAnalyse() {//LL1分析方法进行语法分析
        if (lexAnalyse.isFail())
            javax.swing.JOptionPane.showMessageDialog(null, "词法分析未通过，不能进行语法分析");
        bf = new StringBuffer();
        error = null;
        analyseStack.add(0, S);    //0为栈顶
        int gcount = 0;//记录进行的步骤，初始值为0
        analyseStack.add(1, new AnalyseNode(AnalyseNode.END, "#"));//进栈S,#
        semanticStack.add("#");

        //分析栈不为空，余留符号串不为空
        //wordList由词法分析的结果中获取
        while (!analyseStack.empty() && !wordList.isEmpty()) {
            bf.append('\n');               //缓冲栈输出'\n'
            bf.append("步骤" + gcount + "\t"); //当前步骤
            System.out.println("步骤为：" + gcount);
            productionStack.add(gcount, "空");

            analyseTop = analyseStack.get(0);   //当前分析栈栈顶元素
            firstWord = wordList.get(0); //待分析单词

            //缓冲栈输出当前分析栈、余留符号串、语义栈
            bf.append("当前分析栈:");
            for (int i = 0; i < analyseStack.size(); i++) {
                bf.append(analyseStack.get(i).name);
            }
            bf.append("\t").append("余留符号串：");
            for (int j = 0; j < wordList.size(); j++) {
                bf.append(wordList.get(j).value);
            }
            bf.append("\t").append("语义栈:");
            for (int k = semanticStack.size() - 1; k >= 0; k--) {
                bf.append(semanticStack.get(k));
            }

            //开始分析
            if (firstWord.value.equals("#") && analyseTop.name.equals("#")) {
                bf.append("\n");
                analyseStack.remove(0);
                wordList.remove(0);
            } else if (analyseTop.name.equals("#")) { //只有分析栈栈顶元素为#
                analyseStack.remove(0); //弹栈
                graErrorFlag = true; //错误标识符标记错，退出
                break;
            }
            //当栈顶元素为终结符
            else if (AnalyseNode.isTerm(analyseTop)) { //终结符时的处理
                System.out.println(analyseTop.name + "：终结符");
                termOP(analyseTop.name, gcount); //分析栈和余留符号串的栈顶元素都弹出来
            }
            //当栈顶元素为非终结符
            else if (AnalyseNode.isNonterm(analyseTop)) {//非终结符的处理
                System.out.println(analyseTop.name + "：非终结符");
                nonTermOP(analyseTop.name, gcount);
            } else if (analyseTop.type.equals(AnalyseNode.ACTIONSIGN)) {//动作符号的处理，为中间代码做准备
                System.out.println(analyseTop.name + "：动作符");
                actionSignOP();
            }

            bf.append("\n");
            bf.append("\t").append("推导所用产生式:");
            bf.append(productionStack.get(gcount));


            bf.append("\n");
            if (gcount++ > 10000) { //当步数>10000,错误标识符标记错，退出（设置一个比较大的 ）
                graErrorFlag = true;
                break;
            }
        }
    }

    //终结符操作
/* 根据字符表获余留符号串的取首字符类型，当类型为定义的int,char,double等关键字,或者数字和复合自定义类型的自定义字符
   栈顶元素弹栈，余留符号串首字符也移出。否则的话错误数量加一，同样进行弹栈处理，但是输出语法错误发生的位置和字符，把错误加入的错误列表中，
   错误标识符标识错误。*/
    private void termOP(String term, int gcount) {//term是分析元素
        if ( //是正常终结符的时候弹栈
                (firstWord.type.equals(Word.INT_CONST) || firstWord.type.equals(Word.CHAR_CONST)
                        || firstWord.type.equals(Word.STRING_CONST) || firstWord.type.equals(Word.FLOAT_CONST))
                        || (firstWord.type.equals(Word.OPERATOR) && term.equals(firstWord.value))
                        || (firstWord.type.equals(Word.BOUNDARYSIGN) && term.equals(firstWord.value))
                        || (firstWord.type.equals(Word.KEY) && term.equals(firstWord.value))
                        || (term.equals("id") && firstWord.type.equals(Word.IDENTIFIER))
                        || (term.equals("num") && firstWord.type.equals(Word.INT_CONST))
                        || (term.equals("ch") && firstWord.type.equals(Word.IDENTIFIER))
                        || (term.equals("st") && firstWord.type.equals(Word.IDENTIFIER))
                        || (term.equals("fl") && firstWord.type.equals(Word.IDENTIFIER))
                        || (term.equals("bo") && firstWord.type.equals(Word.IDENTIFIER))
                        || (term.equals("\"%d\"") && firstWord.type.equals(Word.IDENTIFIER))
        ) {
            System.out.println("name:" + term + "   " + "succeful");
            analyseStack.remove(0);
            wordList.remove(0);
        } else {
            errorCount++;
            if (analyseStack.get(0).equals(";"))
                error = new Error(errorCount - 1, "语法错误", firstWord.line, firstWord);
            else
                error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
            analyseStack.remove(0);
            wordList.remove(0);
            System.out.println("name:" + term + "   " + "defeat" + "  " + errorCount);
            errorList.add(error);
            graErrorFlag = true;
        }
    }

    //非终结符操作
//以开始符S为例，当余留符号串首字符为void，与LL(1)分析表中进行匹配，发现可以匹配并且S往下有推导，将栈顶元素弹栈，
//在分析栈中压栈S之后的推导元素，在压入的非终结符前边压入一个操作属性，便于后续的语义分析。否则的话错误统计数加一，
//分析栈还有余留符号串都将第一个元素移出，但是输出语法错误发生的位置和字符，把错误加入的错误列表中，错误标识符标识错误。
    private void nonTermOP(String nonTerm, int gcount) {//为了避免识别错误，定义为123等数字
        if (nonTerm.equals("Z'")) nonTerm = "1";
        if (nonTerm.equals("U'")) nonTerm = "2";
        if (nonTerm.equals("E'")) nonTerm = "3";
        if (nonTerm.equals("H'")) nonTerm = "4";
        if (nonTerm.equals("L'")) nonTerm = "5";
        if (nonTerm.equals("T'")) nonTerm = "6";
        switch (nonTerm.charAt(0)) {//栈顶为非终结符处理
            case 'S': //S->void main(){A}
//		if (firstWord.value.equals("#")) {
//			analyseStack.remove(0);
//			analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "#", null));
//			analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "include", null));
//			analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "<", null));
//			analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, "stdio.h", null));
//			analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, ">", null));
//			analyseStack.add(5,S);
//			productionStack.set(gcount,"S->#include <stdio.h>S");
//		}
                if (firstWord.value.equals("void")) {   //余留符号串的栈顶元素为void，符合文法，将右边等式进栈
                    System.out.println("分析栈顶弹出元素" + analyseStack.get(0).name);
                    //System.out.println("分析栈进入以下元素：");
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "void"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "main"));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(5, A);
                    analyseStack.add(6, new AnalyseNode(AnalyseNode.TERMINAL, "}"));
                    productionStack.set(gcount, "S->void main (){A}");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "主函数没有返回值", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            /*
             * LL1递归下降分析程序
             * 根据文法和分析表识别*/
            case 'A':
                if (firstWord.value.equals("int") || firstWord.value.equals("char")
                        || firstWord.value.equals("string") || firstWord.value.equals("float")
                        || firstWord.value.equals("bool")) {
                    System.out.println("分析栈顶弹出元素" + analyseStack.get(0).name);
                    System.out.println("分析栈进入以下元素：");
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    productionStack.set(gcount, "A->CA");
                } else if (firstWord.value.equals("printf")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    productionStack.set(gcount, "A->CA");
                } else if (firstWord.value.equals("scanf")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    productionStack.set(gcount, "A->CA");
                } else if (firstWord.value.equals("if")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    productionStack.set(gcount, "A->CA");
                } else if (firstWord.value.equals("while")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    productionStack.set(gcount, "A->CA");
                } else if (firstWord.value.equals("for")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    productionStack.set(gcount, "A->CA");
                } else if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    productionStack.set(gcount, "A->CA");
                } else {
                    analyseStack.remove(0);
                    productionStack.set(gcount, "A->ε");
                }
                break;

            case 'B':
                if (firstWord.value.equals("printf")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "printf"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "\"%d\""));//转义字符 \"代表一个双引号
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ","));
                    analyseStack.add(4, F);
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(6, PRINTF);
                    analyseStack.add(7, A);
                    analyseStack.add(8, new AnalyseNode(AnalyseNode.TERMINAL, ";"));
                    productionStack.set(gcount, "B->printf (\"%d\",F) @PRINTF A");
                } else if (firstWord.value.equals("scanf")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,
                            "scanf"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "\"%d\""));
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ","));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, "&"));
                    analyseStack.add(5, F);
                    analyseStack.add(6, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(7, SCANF);
                    analyseStack.add(8, A);
                    analyseStack.add(9, new AnalyseNode(AnalyseNode.TERMINAL, ";"));
                    productionStack.set(gcount, "B->scanf(\"%d\",&F) SCANF A)");
                } else if (firstWord.value.equals("if")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "if"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, G);
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(4, IF_FJ);
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(6, A);
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL, "}"));
                    analyseStack.add(8, IF_BACKPATCH_FJ);
                    analyseStack.add(9, IF_RJ);
                    analyseStack.add(10, new AnalyseNode(AnalyseNode.TERMINAL, "else"));
                    analyseStack.add(11, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(12, A);
                    analyseStack.add(13, new AnalyseNode(AnalyseNode.TERMINAL, "}"));
                    analyseStack.add(14, IF_BACKPATCH_RJ);
                    productionStack.set(gcount, "B->if(G) IF_FJ {A} IF_BACKPATCH_FJ IF_RJ else {A} IF_BACKPATCH_RJ");
                } else if (firstWord.value.equals("while")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "while"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, G);
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(4, WHILE_FJ);
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(6, A);
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL, "}"));
                    analyseStack.add(8, WHILE_RJ);
                    analyseStack.add(9, WHILE_BACKPATCH_FJ);
                    productionStack.set(gcount, "B->while(G) WHILE_FJ{A} WHILE_RJ WHILE_BACKPATCH_FJ");
                } else if (firstWord.value.equals("for")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "for"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, Y);
                    analyseStack.add(3, Z);
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL, ";"));
                    analyseStack.add(5, G);
                    analyseStack.add(6, FOR_FJ);
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL, ";"));
                    analyseStack.add(8, Q);
                    analyseStack.add(9, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    analyseStack.add(10, new AnalyseNode(AnalyseNode.TERMINAL, "{"));
                    analyseStack.add(11, A);
                    analyseStack.add(12, SINGLE);
                    analyseStack.add(13, new AnalyseNode(AnalyseNode.TERMINAL, "}"));
                    analyseStack.add(14, FOR_RJ);
                    analyseStack.add(15, FOR_BACKPATCH_FJ);
                    productionStack.set(gcount, "B->for(YZ;G FOR_FJ;Q){A SINGLE} FOR_FJ FOR_BACKPATCH_FJ");
                } else {
                    analyseStack.remove(0);
                    productionStack.set(gcount, "B->ε");
                }
                break;

            case 'C':
                analyseStack.remove(0);
                analyseStack.add(0, X);
                analyseStack.add(1, B);
                analyseStack.add(2, R);
                productionStack.set(gcount, "C->XBR");
                break;

            case 'X':
                if (firstWord.value.equals("int") || firstWord.value.equals("char")
                        || firstWord.value.equals("string") || firstWord.value.equals("float")
                        || firstWord.value.equals("bool")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, Y);
                    analyseStack.add(1, Z);
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, ";"));
                    productionStack.set(gcount, "X->YZ;");
                } else {
                    analyseStack.remove(0);
                    productionStack.set(gcount, "X->ε");
                }
                break;

            case 'Y':
                if (firstWord.value.equals("int")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "int"));
                    productionStack.set(gcount, "Y->int");
                } else if (firstWord.value.equals("char")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "char"));
                    productionStack.set(gcount, "Y->char");
                } else if (firstWord.value.equals("string")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "string"));
                    productionStack.set(gcount, "Y->string");
                } else if (firstWord.value.equals("float")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "float"));
                    productionStack.set(gcount, "Y->float");
                } else if (firstWord.value.equals("bool")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "bool"));
                    productionStack.set(gcount, "Y->bool");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case 'Z':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, U);
                    analyseStack.add(1, Z1);    //Z1即为Z'
                    productionStack.set(gcount, "Z->UZ'");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "非法标识符", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case '1'://z'
                if (firstWord.value.equals(",")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, ","));
                    analyseStack.add(1, Z);
                    productionStack.set(gcount, "Z'->,Z");
                } else {
                    analyseStack.remove(0);
                    productionStack.set(gcount, "Z'->ε");
                }
                break;
            case 'U':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, ASS_U);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "id"));
                    analyseStack.add(2, U1);
                    productionStack.set(gcount, "U->@ASS_U id U'");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "非法标识符", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '2'://U'
                if (firstWord.value.equals("=")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "="));
                    analyseStack.add(1, L);
                    analyseStack.add(2, EQ);     //analyseStack.add(2, EQ_U1);
                    productionStack.set(gcount, "U'->=L EQ");    //productionStack.set(gcount, "U'->=L EQ_U1");
                } else {
                    analyseStack.remove(0);
                    productionStack.set(gcount, "U'->ε");
                }
                break;
            case 'R':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "id"));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "="));
                    analyseStack.add(3, L);
                    analyseStack.add(4, EQ);
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL, ";"));
                    productionStack.set(gcount, "R->@ASS id = L EQ;");
                } else {
                    analyseStack.remove(0);
                    productionStack.set(gcount, "R->ε");
                }
                break;
            case 'P':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "id"));
                    productionStack.set(gcount, "P->id");
                } else if (firstWord.type.equals(Word.INT_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "num"));
                    productionStack.set(gcount, "P->num");
                } else if (firstWord.type.equals(Word.STRING_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "st"));
                    productionStack.set(gcount, "P->string");
                } else if (firstWord.type.equals(Word.FLOAT_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "fl"));
                    productionStack.set(gcount, "P->float");
                } else if (firstWord.type.equals(Word.CHAR_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "ch"));
                    productionStack.set(gcount, "P->char");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case 'E':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, H);
                    analyseStack.add(1, E1);
                    productionStack.set(gcount, "E->HE'");
                } else if (firstWord.type.equals(Word.INT_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, H);
                    analyseStack.add(1, E1);
                    productionStack.set(gcount, "E->HE'");
                } else if (firstWord.value.equals("(")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, H);
                    analyseStack.add(1, E1);
                    productionStack.set(gcount, "E->HE'");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '3'://E'
                if (firstWord.value.equals("&&")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "&&"));
                    analyseStack.add(1, E);
                    productionStack.set(gcount, "E'->&&E");
                } else {
                    analyseStack.remove(0);
                }
                break;
            case 'H':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, G);
                    analyseStack.add(1, H1);
                    productionStack.set(gcount, "H->GH'");
                } else if (firstWord.type.equals(Word.INT_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, G);
                    analyseStack.add(1, H1);
                    productionStack.set(gcount, "H->GH'");
                } else if (firstWord.value.equals("(")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, G);
                    analyseStack.add(1, H1);
                    productionStack.set(gcount, "H->GH'");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '4'://H'
                if (firstWord.value.equals("||")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "||"));
                    analyseStack.add(1, E);
                    productionStack.set(gcount, "H'->||E");
                } else {
                    analyseStack.remove(0);
                }
                break;
            case 'D':
                if (firstWord.value.equals("==")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "=="));
                    productionStack.set(gcount, "D->@COMPARE_OP ==");
                } else if (firstWord.value.equals("!=")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "!="));
                    productionStack.set(gcount, "D->@COMPARE_OP !='");
                } else if (firstWord.value.equals(">")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, ">"));
                    productionStack.set(gcount, "D->@COMPARE_OP >'");
                } else if (firstWord.value.equals("<")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "<"));
                    productionStack.set(gcount, "D->@COMPARE_OP <");
                } else if (firstWord.value.equals(">=")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, ">="));
                    productionStack.set(gcount, "D->@COMPARE_OP >=");
                } else if (firstWord.value.equals("<=")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "<="));
                    productionStack.set(gcount, "D->@COMPARE_OP <=");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    errorCount++;
                    error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case 'G':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, D);
                    analyseStack.add(2, F);
                    analyseStack.add(3, COMPARE);
                    productionStack.set(gcount, "G->FDF @COMPARE");
                } else if (firstWord.type.equals(Word.INT_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, D);
                    analyseStack.add(2, F);
                    analyseStack.add(3, COMPARE);
                    productionStack.set(gcount, "G->FDF @COMPARE");
                } else if (firstWord.value.equals("(")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(1, E);
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, ")"));
                    productionStack.set(gcount, "G->(E)");
                } else if (firstWord.value.equals("!")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "!"));
                    analyseStack.add(1, E);
                    productionStack.set(gcount, "G->!E");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case 'L':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);
                    analyseStack.add(2, ADD_SUB);
                    productionStack.set(gcount, "L->TL' @ADD_SUB");
                } else if (firstWord.type.equals(Word.INT_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);
                    analyseStack.add(2, ADD_SUB);
                    productionStack.set(gcount, "L->TL' @ADD_SUB");
                } else if (firstWord.value.equals("(")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);
                    analyseStack.add(2, ADD_SUB);
                    productionStack.set(gcount, "L->TL' @ADD_SUB");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '5':   //L'
                if (firstWord.value.equals("+")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "+"));
                    analyseStack.add(1, L);
                    analyseStack.add(2, ADD);
                    productionStack.set(gcount, "L'-> +L ADD");
                } else if (firstWord.value.equals("-")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "-"));
                    analyseStack.add(1, L);
                    analyseStack.add(2, SUB);
                    productionStack.set(gcount, "L'-> -L SUB");
                } else {
                    analyseStack.remove(0);
                    productionStack.set(gcount, "L'->ε");
                }
                break;

            case 'T':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);
                    analyseStack.add(2, DIV_MUL);
                    productionStack.set(gcount, "T-> FT' @DIV_MUL");
                } else if (firstWord.type.equals(Word.INT_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);
                    analyseStack.add(2, DIV_MUL);
                    productionStack.set(gcount, "T-> FT' @DIV_MUL");
                } else if (firstWord.value.equals("(")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);
                    analyseStack.add(2, DIV_MUL);
                    productionStack.set(gcount, "T-> FT' @DIV_MUL");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    errorCount++;
                    error = new Error(errorCount, "语法错误", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '6'://T'
                if (firstWord.value.equals("*")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "*"));
                    analyseStack.add(1, T);
                    analyseStack.add(2, MUL);
                    productionStack.set(gcount, "T'-> * T MUL");
                } else if (firstWord.value.equals("/")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "/"));
                    analyseStack.add(1, T);
                    analyseStack.add(2, DIV);
                    productionStack.set(gcount, "T'-> / T DIV");
                } else {
                    analyseStack.remove(0);
                    productionStack.set(gcount, "T'->ε");
                }
                break;
            case 'F':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, ASS);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "id"));
                    productionStack.set(gcount, "F-> @ASS id");
                } else if (firstWord.type.equals(Word.INT_CONST)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, ASS);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "num"));
                    productionStack.set(gcount, "F-> @ASS num");
                } else {
                    analyseStack.remove(0);
                }
                break;
            case 'O':
                if (firstWord.value.equals("++")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SINGLE_OP"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "++"));
                    productionStack.set(gcount, "O-> @SINEGLE_OP ++");
                } else if (firstWord.value.equals("--")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SINGLE_OP"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "--"));
                    productionStack.set(gcount, "O-> @SINEGLE_OP --");
                } else {
                    analyseStack.remove(0);
                }
                break;
            case 'Q'://Q
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "id"));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "O"));
                    productionStack.set(gcount, "Q-> @ASS id O");
                } else {
                    analyseStack.remove(0);
                }
                break;

        }
    }

    //栈顶是动作符号时的处理//根据对应语义动作生成四元式
    private void actionSignOP() {//逻辑运算的语义动作
        if (analyseTop.name.equals("@ADD_SUB")) { //如果栈顶是+号
            if (OP != null && (OP.equals("+") || OP.equals("-"))) {
                ARG2 = semanticStack.pop();
                ARG1 = semanticStack.pop();
                RES = newTemp();  //得到T1
                FourElement fourElem = new FourElement(++fourElemCount, OP, ARG1, ARG2, RES);
                fourElemList.add(fourElem);//四元式写入四元式列表中
                L.value = RES;
                semanticStack.push(L.value);
                OP = null;
            }
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@ADD")) {
            OP = "+";
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@SUB")) {
            OP = "-";
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@DIV_MUL")) {
            if (OP != null && (OP.equals("*") || OP.equals("/"))) {
                ARG2 = semanticStack.pop();
                ARG1 = semanticStack.pop();
                RES = newTemp();
                FourElement fourElem = new FourElement(++fourElemCount, OP, ARG1, ARG2, RES);
                fourElemList.add(fourElem);
                T.value = RES;
                semanticStack.push(T.value);
                OP = null;
            }
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@DIV")) {
            OP = "/";
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@MUL")) {
            OP = "*";
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@TRAN_LF")) {//属性赋值语义动作
            F.value = L.value;
            analyseStack.remove(0);
        }
        //初始化(赋值)语义动作
        else if (analyseTop.name.equals("@ASS")) {
            //System.out.println("前分析栈节点F.value：" + F.value);
            //F.value = firstWord.value;//让分析结点的属性值等于当前符号的属性值
            //System.out.println("余留符号串firstWord.value：" + firstWord.value);
            //System.out.println("后分析栈节点：" + F.value);
            if (!LexAnalyse.getTypelist().contains(firstWord.value) && (firstWord.value.charAt(0) > 64)) {     //ASCII码值：A65，a97
                errorCount++;
                error = new Error(errorCount, "没有定义      ", firstWord.line, firstWord);
                errorList.add(error);
                graErrorFlag = true;
            }
            semanticStack.push(firstWord.value);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@ASS_U")) {  //定义变量
            U.value = firstWord.value;  // U.value原来为null
            for (String x : semanticStack) {
                if (U.value.equals(x)) {//出错,重定义
                    System.out.println("重复定义" + firstWord);
                    errorCount++;
                    error = new Error(errorCount, "重复定义      " + x, firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
            }
            semanticStack.push(U.value);
            analyseStack.remove(0);
            System.out.println("分析栈出栈：" + analyseStack.get(0).name);
            System.out.println("语义栈进栈（余留栈的栈顶元素）：" + U.value);
        } else if (analyseTop.name.equals("@SINGLE")) {
            if (for_op.peek() != null) {
                ARG1 = semanticStack.pop();
                RES = ARG1;
                FourElement fourElem = new FourElement(++fourElemCount, for_op.pop(), ARG1, "/", RES);
                fourElemList.add(fourElem);
            }
            analyseStack.remove(0);
            } else if (analyseTop.name.equals("@SINGLE_OP")) {
            for_op.push(firstWord.value);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@EQ")) {
            OP = "=";
            ARG1 = semanticStack.pop();
            RES = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, ARG1, "/", RES);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        }
        //逻辑操作语义动作
        else if (analyseTop.name.equals("@COMPARE")) {
            ARG2 = semanticStack.pop();
            OP = semanticStack.pop();
            ARG1 = semanticStack.pop();
            RES = newTemp();
            FourElement fourElem = new FourElement(++fourElemCount, OP, ARG1, ARG2, RES);
            fourElemList.add(fourElem);
            G.value = RES;
            semanticStack.push(G.value);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@COMPARE_OP")) {
            D.value = firstWord.value;
            semanticStack.push(D.value);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@IF_FJ")) {   //！！！产生四元式
            OP = "FJ";//修改四元式
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, RES, ARG1, "/");
            if_fj.push(fourElemCount); //四元式的个数
            System.out.println("四元式的个数为：" + fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@SCANF")) {
            OP = "SCANF";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, ARG1, "/", "/");
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@PRINTF")) {
            OP = "PRINTF";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, ARG1, "/", "/");
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@IF_BACKPATCH_FJ")) {
            backpatch(if_fj.pop(), fourElemCount + 2);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@IF_RJ")) {
            OP = "RJ";//if继续
            FourElement fourElem = new FourElement(++fourElemCount, OP, "/", "/", "/");
            if_rj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@IF_BACKPATCH_RJ")) {
            System.out.println("此时四元式的个数为：" + fourElemCount);
            backpatch(if_rj.pop(), fourElemCount + 1);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@WHILE_FJ")) {
            OP = "FJ";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, "/", ARG1, "/");    //在此先不填入跳转的序号
            while_fj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@WHILE_RJ")) {
            OP = "RJ";
            RES = (while_fj.peek() - 1) + "";//退到while逻辑表达式入口    get()读取，删除,  peek()读取，不删除
            FourElement fourElem = new FourElement(++fourElemCount, OP, RES, "/", "/");
            for_rj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@WHILE_BACKPATCH_FJ")) {
            backpatch(while_fj.pop(), fourElemCount + 1);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@FOR_FJ")) {     //！！！产生四元式
            OP = "FJ";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, "/", ARG1, "/");
            for_fj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@FOR_RJ")) {     //！！！产生四元式
            OP = "RJ";
            RES = (for_fj.peek() - 1) + "";
            FourElement fourElem = new FourElement(++fourElemCount, OP, RES, "/", "/");
            for_rj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@FOR_BACKPATCH_FJ")) {
            backpatch(for_fj.pop(), fourElemCount + 1);
            analyseStack.remove(0);
        }
    }

    private void backpatch(int i, int res) {       //回填，i为回填的序号，res为
        FourElement temp = fourElemList.get(i - 1);//栈里面的内容要-1，因为下标从0开始
        temp.arg1 = res + "";
        fourElemList.set(i - 1, temp);
    }

    public String outputLL1() throws IOException {
        File file = new File("./result/");
        if (!file.exists()) {
            file.mkdirs();
            file.createNewFile();//如果这个文件不存在就创建它
        }
        String path = file.getAbsolutePath();
        FileOutputStream fos = new FileOutputStream(path + "/LL1.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");
        PrintWriter pw1 = new PrintWriter(osw1);
        pw1.println(bf.toString());
        bf.delete(0, bf.length());
        if (graErrorFlag) {
            Error error;
            pw1.println("语义错误，信息如下：");

            pw1.println("错误序号\t\t错误信息\t\t错误所在行 \t\t错误单词");
            for (int i = 0; i < errorList.size(); i++) {
                error = errorList.get(i);
                pw1.println(error.id + "\t\t\t" + error.info + "\t\t\t" + error.line + "\t\t\t" + error.word.value);
            }
        } else {
            pw1.println("语法分析通过！");
        }
        pw1.close();
        return path + "/LL1.txt";
    }

    //输出四元式
    public String outputFourElem() throws IOException {

        File file = new File("./result/");
        if (!file.exists()) {
            file.mkdirs();
            file.createNewFile();//如果这个文件不存在就创建它
        }
        String path = file.getAbsolutePath();//获取绝对路径（即全路径）
        FileOutputStream fos = new FileOutputStream(path + "/FourElement.txt");  //创建一个向指定 File 对象表示的文件中写入数据的文件输出流
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");//确认流的输出文件和编码格式
        PrintWriter pw1 = new PrintWriter(osw1);
        pw1.println("生成的四元式如下：");
        pw1.println("序号（OP,ARG1，ARG2，RESULT）");
        FourElement temp;
        for (int i = 0; i < fourElemList.size(); i++) {
            temp = fourElemList.get(i);
            pw1.println(temp.id + "(" + temp.op + "," + temp.arg1 + "," + temp.arg2 + "," + temp.result + ")");
            System.out.println(temp.id + "(" + temp.op + "," + temp.arg1 + "," + temp.arg2 + "," + temp.result + ")");
        }
        pw1.close();

        return path + "/FourElement.txt";
    }

    public static void main(String[] args) {

    }

}
