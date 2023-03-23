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
 * �﷨������
 *
 * @author KB
 */
public class Parser {

    /**
     * @param args
     */


    private LexAnalyse lexAnalyse;//�ʷ�������
    ArrayList<Word> wordList = new ArrayList<Word>();//���ʱ�
    Stack<AnalyseNode> analyseStack = new Stack<AnalyseNode>();//����ջ
    Stack<String> productionStack = new Stack<String>();//����ʽջ
    Stack<String> semanticStack = new Stack<String>();//����ջ
    public ArrayList<FourElement> fourElemList = new ArrayList<FourElement>();//��Ԫʽ�б�

    public ArrayList<Error> errorList = new ArrayList<Error>();//������Ϣ�б�
    StringBuffer bf; //��ÿ������������ջ������
    int errorCount = 0;//ͳ�ƴ������
    public boolean graErrorFlag = false;//�﷨���������־
    int tempCount = 0; //����������ʱ����
    int fourElemCount = 0;//ͳ����Ԫʽ����

    AnalyseNode S, B, A, C, X, Y, R, Z, Z1, U, U1, E, E1, H, H1, G, M, D, L, L1, T, T1, F, O, P, Q;//���ս��

    //���嶯��----------------------------------------------------------------------------
    AnalyseNode ADD_SUB, DIV_MUL, ADD, SUB, DIV, MUL, SINGLE, SINGLE_OP;//�������㣨+��-��*��/��ΪADD_SUB,DIV_MUL��OP�ĸ�ֵ������
    AnalyseNode ASS, ASS_U, TRAN_LF;//��ʼ��(��ֵ)  ASS_U(=)
    AnalyseNode EQ;//, EQ_U1;//��ֵ;EQ_U1Ϊ�ָ�ɾ�����
    AnalyseNode COMPARE, COMPARE_OP, SCANF, PRINTF;//�߼�
    AnalyseNode IF_FJ, IF_RJ, IF_BACKPATCH_FJ, IF_BACKPATCH_RJ;//if;
    AnalyseNode WHILE_FJ, WHILE_RJ, WHILE_BACKPATCH_FJ;//while
    AnalyseNode FOR_FJ, FOR_RJ, FOR_BACKPATCH_FJ;//for
    AnalyseNode analyseTop;//��ǰջ��Ԫ��
    Word firstWord;//����������
    String OP = null;
    String ARG1, ARG2, RES;
    Error error;
    Stack<Integer> if_fj, if_rj, while_fj, while_rj, for_fj, for_rj;//if while for ��ת��ַջ
    Stack<String> for_op = new Stack<String>();
    public ArrayList<String> fourElemT = new ArrayList<String>();

    public Parser() {

    }

    //�ʷ������Ľ��
    public Parser(LexAnalyse lexAnalyse) {
        this.lexAnalyse = lexAnalyse;
        this.wordList = lexAnalyse.wordList;
        init();
    }

    //������Ԫʽ�е���ʱ���� T1��T2
    private String newTemp() {
        tempCount++;  //��ʱ������Ŀ
        fourElemT.add("T" + tempCount);
        return "T" + tempCount;
    }

    //��ʼ��AnalyseNode(���ս�������嶯��)
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

        //�����������嶯��
        ADD_SUB = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ADD_SUB");
        ADD = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ADD");
        SUB = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SUB");
        DIV_MUL = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@DIV_MUL");
        DIV = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@DIV");
        MUL = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@MUL");
        SINGLE = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SINGLE");
        SINGLE_OP = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SINGLE_OP");

        //��ʼ��(��ֵ)���嶯��
        ASS = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS");
        ASS_U = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS_U");
        TRAN_LF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@TRAN_LF");

        //��ֵ���嶯��
        EQ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@EQ");
        //EQ_U1 = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@EQ_U'");

        //�߼��������嶯��
        COMPARE = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@COMPARE");
        COMPARE_OP = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@COMPARE_OP");

        //if������嶯��
        IF_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_FJ");
        SCANF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SCANF");
        PRINTF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@PRINTF");
        IF_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_RJ");
        IF_BACKPATCH_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_BACKPATCH_FJ");
        IF_BACKPATCH_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_BACKPATCH_RJ");

        //while������嶯��
        WHILE_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@WHILE_FJ");
        WHILE_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@WHILE_RJ");
        WHILE_BACKPATCH_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@WHILE_BACKPATCH_FJ");

        //for������嶯��
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


    public void grammerAnalyse() {//LL1�������������﷨����
        if (lexAnalyse.isFail())
            javax.swing.JOptionPane.showMessageDialog(null, "�ʷ�����δͨ�������ܽ����﷨����");
        bf = new StringBuffer();
        error = null;
        analyseStack.add(0, S);    //0Ϊջ��
        int gcount = 0;//��¼���еĲ��裬��ʼֵΪ0
        analyseStack.add(1, new AnalyseNode(AnalyseNode.END, "#"));//��ջS,#
        semanticStack.add("#");

        //����ջ��Ϊ�գ��������Ŵ���Ϊ��
        //wordList�ɴʷ������Ľ���л�ȡ
        while (!analyseStack.empty() && !wordList.isEmpty()) {
            bf.append('\n');               //����ջ���'\n'
            bf.append("����" + gcount + "\t"); //��ǰ����
            System.out.println("����Ϊ��" + gcount);
            productionStack.add(gcount, "��");

            analyseTop = analyseStack.get(0);   //��ǰ����ջջ��Ԫ��
            firstWord = wordList.get(0); //����������

            //����ջ�����ǰ����ջ���������Ŵ�������ջ
            bf.append("��ǰ����ջ:");
            for (int i = 0; i < analyseStack.size(); i++) {
                bf.append(analyseStack.get(i).name);
            }
            bf.append("\t").append("�������Ŵ���");
            for (int j = 0; j < wordList.size(); j++) {
                bf.append(wordList.get(j).value);
            }
            bf.append("\t").append("����ջ:");
            for (int k = semanticStack.size() - 1; k >= 0; k--) {
                bf.append(semanticStack.get(k));
            }

            //��ʼ����
            if (firstWord.value.equals("#") && analyseTop.name.equals("#")) {
                bf.append("\n");
                analyseStack.remove(0);
                wordList.remove(0);
            } else if (analyseTop.name.equals("#")) { //ֻ�з���ջջ��Ԫ��Ϊ#
                analyseStack.remove(0); //��ջ
                graErrorFlag = true; //�����ʶ����Ǵ��˳�
                break;
            }
            //��ջ��Ԫ��Ϊ�ս��
            else if (AnalyseNode.isTerm(analyseTop)) { //�ս��ʱ�Ĵ���
                System.out.println(analyseTop.name + "���ս��");
                termOP(analyseTop.name, gcount); //����ջ���������Ŵ���ջ��Ԫ�ض�������
            }
            //��ջ��Ԫ��Ϊ���ս��
            else if (AnalyseNode.isNonterm(analyseTop)) {//���ս���Ĵ���
                System.out.println(analyseTop.name + "�����ս��");
                nonTermOP(analyseTop.name, gcount);
            } else if (analyseTop.type.equals(AnalyseNode.ACTIONSIGN)) {//�������ŵĴ���Ϊ�м������׼��
                System.out.println(analyseTop.name + "��������");
                actionSignOP();
            }

            bf.append("\n");
            bf.append("\t").append("�Ƶ����ò���ʽ:");
            bf.append(productionStack.get(gcount));


            bf.append("\n");
            if (gcount++ > 10000) { //������>10000,�����ʶ����Ǵ��˳�������һ���Ƚϴ�� ��
                graErrorFlag = true;
                break;
            }
        }
    }

    //�ս������
/* �����ַ�����������Ŵ���ȡ���ַ����ͣ�������Ϊ�����int,char,double�ȹؼ���,�������ֺ͸����Զ������͵��Զ����ַ�
   ջ��Ԫ�ص�ջ���������Ŵ����ַ�Ҳ�Ƴ�������Ļ�����������һ��ͬ�����е�ջ������������﷨��������λ�ú��ַ����Ѵ������Ĵ����б��У�
   �����ʶ����ʶ����*/
    private void termOP(String term, int gcount) {//term�Ƿ���Ԫ��
        if ( //�������ս����ʱ��ջ
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
                error = new Error(errorCount - 1, "�﷨����", firstWord.line, firstWord);
            else
                error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
            analyseStack.remove(0);
            wordList.remove(0);
            System.out.println("name:" + term + "   " + "defeat" + "  " + errorCount);
            errorList.add(error);
            graErrorFlag = true;
        }
    }

    //���ս������
//�Կ�ʼ��SΪ�������������Ŵ����ַ�Ϊvoid����LL(1)�������н���ƥ�䣬���ֿ���ƥ�䲢��S�������Ƶ�����ջ��Ԫ�ص�ջ��
//�ڷ���ջ��ѹջS֮����Ƶ�Ԫ�أ���ѹ��ķ��ս��ǰ��ѹ��һ���������ԣ����ں������������������Ļ�����ͳ������һ��
//����ջ�����������Ŵ�������һ��Ԫ���Ƴ�����������﷨��������λ�ú��ַ����Ѵ������Ĵ����б��У������ʶ����ʶ����
    private void nonTermOP(String nonTerm, int gcount) {//Ϊ�˱���ʶ����󣬶���Ϊ123������
        if (nonTerm.equals("Z'")) nonTerm = "1";
        if (nonTerm.equals("U'")) nonTerm = "2";
        if (nonTerm.equals("E'")) nonTerm = "3";
        if (nonTerm.equals("H'")) nonTerm = "4";
        if (nonTerm.equals("L'")) nonTerm = "5";
        if (nonTerm.equals("T'")) nonTerm = "6";
        switch (nonTerm.charAt(0)) {//ջ��Ϊ���ս������
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
                if (firstWord.value.equals("void")) {   //�������Ŵ���ջ��Ԫ��Ϊvoid�������ķ������ұߵ�ʽ��ջ
                    System.out.println("����ջ������Ԫ��" + analyseStack.get(0).name);
                    //System.out.println("����ջ��������Ԫ�أ�");
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
                    error = new Error(errorCount, "������û�з���ֵ", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            /*
             * LL1�ݹ��½���������
             * �����ķ��ͷ�����ʶ��*/
            case 'A':
                if (firstWord.value.equals("int") || firstWord.value.equals("char")
                        || firstWord.value.equals("string") || firstWord.value.equals("float")
                        || firstWord.value.equals("bool")) {
                    System.out.println("����ջ������Ԫ��" + analyseStack.get(0).name);
                    System.out.println("����ջ��������Ԫ�أ�");
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
                    productionStack.set(gcount, "A->��");
                }
                break;

            case 'B':
                if (firstWord.value.equals("printf")) {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL, "printf"));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL, "("));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL, "\"%d\""));//ת���ַ� \"����һ��˫����
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
                    productionStack.set(gcount, "B->��");
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
                    productionStack.set(gcount, "X->��");
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
                    error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;

            case 'Z':
                if (firstWord.type.equals(Word.IDENTIFIER)) {
                    analyseStack.remove(0);
                    analyseStack.add(0, U);
                    analyseStack.add(1, Z1);    //Z1��ΪZ'
                    productionStack.set(gcount, "Z->UZ'");
                } else {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "�Ƿ���ʶ��", firstWord.line, firstWord);
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
                    productionStack.set(gcount, "Z'->��");
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
                    error = new Error(errorCount, "�Ƿ���ʶ��", firstWord.line, firstWord);
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
                    productionStack.set(gcount, "U'->��");
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
                    productionStack.set(gcount, "R->��");
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
                    error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
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
                    error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
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
                    error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
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
                    error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
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
                    error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
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
                    error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
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
                    productionStack.set(gcount, "L'->��");
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
                    error = new Error(errorCount, "�﷨����", firstWord.line, firstWord);
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
                    productionStack.set(gcount, "T'->��");
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

    //ջ���Ƕ�������ʱ�Ĵ���//���ݶ�Ӧ���嶯��������Ԫʽ
    private void actionSignOP() {//�߼���������嶯��
        if (analyseTop.name.equals("@ADD_SUB")) { //���ջ����+��
            if (OP != null && (OP.equals("+") || OP.equals("-"))) {
                ARG2 = semanticStack.pop();
                ARG1 = semanticStack.pop();
                RES = newTemp();  //�õ�T1
                FourElement fourElem = new FourElement(++fourElemCount, OP, ARG1, ARG2, RES);
                fourElemList.add(fourElem);//��Ԫʽд����Ԫʽ�б���
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
        } else if (analyseTop.name.equals("@TRAN_LF")) {//���Ը�ֵ���嶯��
            F.value = L.value;
            analyseStack.remove(0);
        }
        //��ʼ��(��ֵ)���嶯��
        else if (analyseTop.name.equals("@ASS")) {
            //System.out.println("ǰ����ջ�ڵ�F.value��" + F.value);
            //F.value = firstWord.value;//�÷�����������ֵ���ڵ�ǰ���ŵ�����ֵ
            //System.out.println("�������Ŵ�firstWord.value��" + firstWord.value);
            //System.out.println("�����ջ�ڵ㣺" + F.value);
            if (!LexAnalyse.getTypelist().contains(firstWord.value) && (firstWord.value.charAt(0) > 64)) {     //ASCII��ֵ��A65��a97
                errorCount++;
                error = new Error(errorCount, "û�ж���      ", firstWord.line, firstWord);
                errorList.add(error);
                graErrorFlag = true;
            }
            semanticStack.push(firstWord.value);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@ASS_U")) {  //�������
            U.value = firstWord.value;  // U.valueԭ��Ϊnull
            for (String x : semanticStack) {
                if (U.value.equals(x)) {//����,�ض���
                    System.out.println("�ظ�����" + firstWord);
                    errorCount++;
                    error = new Error(errorCount, "�ظ�����      " + x, firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
            }
            semanticStack.push(U.value);
            analyseStack.remove(0);
            System.out.println("����ջ��ջ��" + analyseStack.get(0).name);
            System.out.println("����ջ��ջ������ջ��ջ��Ԫ�أ���" + U.value);
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
        //�߼��������嶯��
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
        } else if (analyseTop.name.equals("@IF_FJ")) {   //������������Ԫʽ
            OP = "FJ";//�޸���Ԫʽ
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, RES, ARG1, "/");
            if_fj.push(fourElemCount); //��Ԫʽ�ĸ���
            System.out.println("��Ԫʽ�ĸ���Ϊ��" + fourElemCount);
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
            OP = "RJ";//if����
            FourElement fourElem = new FourElement(++fourElemCount, OP, "/", "/", "/");
            if_rj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@IF_BACKPATCH_RJ")) {
            System.out.println("��ʱ��Ԫʽ�ĸ���Ϊ��" + fourElemCount);
            backpatch(if_rj.pop(), fourElemCount + 1);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@WHILE_FJ")) {
            OP = "FJ";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, "/", ARG1, "/");    //�ڴ��Ȳ�������ת�����
            while_fj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@WHILE_RJ")) {
            OP = "RJ";
            RES = (while_fj.peek() - 1) + "";//�˵�while�߼����ʽ���    get()��ȡ��ɾ��,  peek()��ȡ����ɾ��
            FourElement fourElem = new FourElement(++fourElemCount, OP, RES, "/", "/");
            for_rj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@WHILE_BACKPATCH_FJ")) {
            backpatch(while_fj.pop(), fourElemCount + 1);
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@FOR_FJ")) {     //������������Ԫʽ
            OP = "FJ";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount, OP, "/", ARG1, "/");
            for_fj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } else if (analyseTop.name.equals("@FOR_RJ")) {     //������������Ԫʽ
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

    private void backpatch(int i, int res) {       //���iΪ�������ţ�resΪ
        FourElement temp = fourElemList.get(i - 1);//ջ���������Ҫ-1����Ϊ�±��0��ʼ
        temp.arg1 = res + "";
        fourElemList.set(i - 1, temp);
    }

    public String outputLL1() throws IOException {
        File file = new File("./result/");
        if (!file.exists()) {
            file.mkdirs();
            file.createNewFile();//�������ļ������ھʹ�����
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
            pw1.println("���������Ϣ���£�");

            pw1.println("�������\t\t������Ϣ\t\t���������� \t\t���󵥴�");
            for (int i = 0; i < errorList.size(); i++) {
                error = errorList.get(i);
                pw1.println(error.id + "\t\t\t" + error.info + "\t\t\t" + error.line + "\t\t\t" + error.word.value);
            }
        } else {
            pw1.println("�﷨����ͨ����");
        }
        pw1.close();
        return path + "/LL1.txt";
    }

    //�����Ԫʽ
    public String outputFourElem() throws IOException {

        File file = new File("./result/");
        if (!file.exists()) {
            file.mkdirs();
            file.createNewFile();//�������ļ������ھʹ�����
        }
        String path = file.getAbsolutePath();//��ȡ����·������ȫ·����
        FileOutputStream fos = new FileOutputStream(path + "/FourElement.txt");  //����һ����ָ�� File �����ʾ���ļ���д�����ݵ��ļ������
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");//ȷ����������ļ��ͱ����ʽ
        PrintWriter pw1 = new PrintWriter(osw1);
        pw1.println("���ɵ���Ԫʽ���£�");
        pw1.println("��ţ�OP,ARG1��ARG2��RESULT��");
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
