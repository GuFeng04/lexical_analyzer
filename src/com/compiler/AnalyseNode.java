package com.compiler;

import java.util.ArrayList;

/**
 * 分析栈节点类
 *
 * @author KB
 * String type;//节点类型
 * String name;//节点名
 * Object value;//节点值
 */
public class AnalyseNode {
    public final static String NONTERMINAL = "非终结符";
    public final static String TERMINAL = "终结符";
    public final static String ACTIONSIGN = "动作符";
    public final static String END = "结束符";
    static ArrayList<String> nonterminal = new ArrayList<String>();//非终结符集合
    static ArrayList<String> actionSign = new ArrayList<String>();//动作符集合

    static {
        //N:S,B,A,C,,X,R,Z,Z’,U,U’,E,E’,H,H’,G,M,D,L,L’,T,T’,F,O,P,Q
        nonterminal.add("S");
        nonterminal.add("A");
        nonterminal.add("B");
        nonterminal.add("C");
        nonterminal.add("D");
        nonterminal.add("E");
        nonterminal.add("F");
        nonterminal.add("G");
        nonterminal.add("H");
        nonterminal.add("L");
        nonterminal.add("M");
        nonterminal.add("O");
        nonterminal.add("P");
        nonterminal.add("Q");
        nonterminal.add("X");
        nonterminal.add("Y");
        nonterminal.add("Z");
        nonterminal.add("R");
        nonterminal.add("U");
        nonterminal.add("Z'");
        nonterminal.add("U'");
        nonterminal.add("E'");
        nonterminal.add("H'");
        nonterminal.add("L'");
        nonterminal.add("T");
        nonterminal.add("T'");
        //----------------------------------------------
        //符号表项生成相关语义动作(作用在标识符声明时)

        //算术运算语义动作
        actionSign.add("@ADD_SUB");
        actionSign.add("@ADD");
        actionSign.add("@SUB");
        actionSign.add("@DIV_MUL");
        actionSign.add("@DIV");
        actionSign.add("@MUL");
        actionSign.add("@SINGLE");
        actionSign.add("@SINGTLE_OP");
        //初始化(赋值)语义动作
        actionSign.add("@ASS_R");
        actionSign.add("@ASS_Q");
        actionSign.add("@ASS_F");
        actionSign.add("@ASS_U");       //定义
        actionSign.add("@TRAN_LF");
        //赋值语义动作
        actionSign.add("@EQ");
        actionSign.add("@EQ_U'");
        //逻辑操作语义动作
        actionSign.add("@COMPARE");
        actionSign.add("@COMPARE_OP");
        //if语句语义动作
        actionSign.add("@IF_FJ");
        actionSign.add("@IF_BACKPATCH_FJ");
        actionSign.add("@IF_RJ");
        actionSign.add("@IF_BACKPATCH_RJ");
        //while语句语义动作
        actionSign.add("@WHILE_FJ");
        actionSign.add("@WHILE_BACKPATCH_FJ");
        //for语句语义动作
        actionSign.add("@FOR_FJ");
        actionSign.add("@FOR_RJ");
        actionSign.add("@FOR_BACKPATCH_FJ");
    }

    String type;//节点类型
    String name;//节点名
    String value;//节点值

    public static boolean isNonterm(AnalyseNode node) {
        return nonterminal.contains(node.name);
    }

    public static boolean isTerm(AnalyseNode node) {
        return Word.isKey(node.name) || Word.isOperator(node.name) || Word.isBoundarySign(node.name)
                || node.name.equals("id") || node.name.equals("num") || node.name.equals("ch")
                || node.name.equals("\"%d\"") || node.name.equals("&");
    }

    public static boolean isActionSign(AnalyseNode node) {
        return actionSign.contains(node.name);
    }

    public AnalyseNode() {

    }

    public AnalyseNode(String type, String name) {
        this.type = type;
        this.name = name;
    }

}
