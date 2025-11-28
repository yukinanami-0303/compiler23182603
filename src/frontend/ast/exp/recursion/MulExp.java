package frontend.ast.exp.recursion;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.UnaryExp;
import frontend.ast.token.Ident;

import java.io.IOException;

import static frontend.TokenStream.*;


public class MulExp extends Node{
    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private int Utype;
    private UnaryExp unaryExp0=null;
    //-------------------------------------
    private MulExp mulExp1=null;
    private Token multToken1=null;
    private Token divToken1=null;
    private Token modToken1=null;
    private UnaryExp unaryExp1=null;
    public MulExp(UnaryExp unaryExp) {
        super(SyntaxType.MUL_EXP);
        this.unaryExp0=unaryExp;
        this.Utype=0;
    }
    //-----------------------------------------
    public MulExp(MulExp mulExp,
                  Token token,
                  UnaryExp unaryExp) {
        super(SyntaxType.MUL_EXP);
        this.mulExp1=mulExp;
        if(token.getType().equals("MULT")){
            this.multToken1=token;
        }
        else if(token.getType().equals("DIV")){
            this.divToken1=token;
        }
        else if(token.getType().equals("MOD")){
            this.modToken1=token;
        }
        this.unaryExp1=unaryExp;
        this.Utype=1;
    }
    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    @Override
    public void formatOutput() throws IOException {
        if(this.Utype==0){
            unaryExp0.formatOutput();
        }
        else{
            mulExp1.formatOutput();
            if(modToken1!=null){
                modToken1.formatOutput();
            }
            else if(divToken1!=null){
                divToken1.formatOutput();
            }
            else{
                multToken1.formatOutput();
            }
            unaryExp1.formatOutput();
        }
        outputSelf();
    }
    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    //改写成MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
    @Override
    public void parse() {
        // 1. 解析第一个UnaryExp（必须存在，作为基础）
        UnaryExp currentUnary = new UnaryExp();
        currentUnary.parse();
        // 初始MulExp为Utype=0（仅包含一个UnaryExp）
        MulExp currentMul = new MulExp(currentUnary); // 调用构造函数Utype=0

        // 2. 循环解析后续的'*'/'/'/'%'和UnaryExp（可选，0次或多次）
        String currentTokenType = Peek(0).getType();
        while (currentTokenType.equals("MULT")   // 乘法运算符'*'
                || currentTokenType.equals("DIV") // 除法运算符'/'
                || currentTokenType.equals("MOD")) { // 取模运算符'%'
            // 2.1 解析运算符
            Token op = Peek(0);
            nextToken(); // 消费当前运算符

            // 2.2 解析运算符后的UnaryExp
            UnaryExp nextUnary = new UnaryExp();
            nextUnary.parse();

            // 2.3 构建新的MulExp（Utype=1），将当前MulExp作为左部
            currentMul = new MulExp(currentMul, op, nextUnary); // 调用构造函数Utype=1

            // 更新循环条件（检查下一个token是否仍为乘法类运算符）
            currentTokenType = Peek(0).getType();
        }

        // 3. 将最终构建的链式MulExp赋值给当前对象的变量
        if (currentMul.Utype == 0) {
            this.unaryExp0 = currentMul.unaryExp0; // 单个UnaryExp的情况
            this.Utype = 0;
        } else {
            this.mulExp1 = currentMul.mulExp1; // 左部MulExp
            this.multToken1 = currentMul.multToken1; // '*'运算符（按需赋值）
            this.divToken1 = currentMul.divToken1;   // '/'运算符（按需赋值）
            this.modToken1 = currentMul.modToken1;   // '%'运算符（按需赋值）
            this.unaryExp1 = currentMul.unaryExp1;   // 右部UnaryExp
            this.Utype = 1;
        }
    }

    @Override
    public void visit(){
        if(this.Utype==0){
            unaryExp0.visit();
        }
        else{
            mulExp1.visit();
            unaryExp1.visit();
        }
    }






    public MulExp(){
        super(SyntaxType.MUL_EXP);
    }
}