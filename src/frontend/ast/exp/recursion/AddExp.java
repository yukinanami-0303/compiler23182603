package frontend.ast.exp.recursion;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.Ident;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class AddExp extends Node{
    //AddExp → MulExp | AddExp ('+' | '−') MulExp

    private int Utype;
    private MulExp mulExp0=null;
    //-----------------------------------------
    private AddExp addExp1=null;
    private Token plusToken1=null;
    private Token minuToken1=null;
    private MulExp mulExp1=null;
    public AddExp(MulExp mulExp) {
        super(SyntaxType.ADD_EXP);
        this.mulExp0=mulExp;
        this.Utype=0;
    }
    //-------------------------------------
    public AddExp(AddExp addExp,
                  Token token,
                  MulExp mulExp){
        super(SyntaxType.ADD_EXP);
        this.addExp1=addExp;
        if(token.getType().equals("PLUS")) {
            this.plusToken1 = token;
        }
        else if(token.getType().equals("MINU")){
            this.minuToken1=token;
        }
        this.mulExp1=mulExp;
        this.Utype=1;
    }
    //AddExp → MulExp | AddExp ('+' | '−') MulExp
    @Override
    public void formatOutput() throws IOException {
        if(this.Utype==0){
            mulExp0.formatOutput();
        }
        else{
            addExp1.formatOutput();
            if(plusToken1!=null){
                plusToken1.formatOutput();
            }
            else{
                minuToken1.formatOutput();
            }
            mulExp1.formatOutput();
        }
        outputSelf();
    }


    //AddExp → MulExp | AddExp ('+' | '−') MulExp
    //改写成AddExp → MulExp { ('+' | '-') MulExp }
    @Override
    public void parse() {
        // 1. 解析第一个MulExp（必须存在，作为基础）
        MulExp currentMul = new MulExp();
        currentMul.parse();
        // 初始AddExp为Utype=0（仅包含一个MulExp）
        AddExp currentAdd = new AddExp(currentMul); // 调用构造函数Utype=0

        // 2. 循环解析后续的'+'/'-'和MulExp（可选，0次或多次）
        while (Peek(0).getType().equals("PLUS") || Peek(0).getType().equals("MINU")) {
            // 2.1 解析运算符
            Token op = Peek(0);
            nextToken(); // 消费运算符

            // 2.2 解析运算符后的MulExp
            MulExp nextMul = new MulExp();
            nextMul.parse();

            // 2.3 构建新的AddExp（Utype=1），将当前AddExp作为左部
            currentAdd = new AddExp(currentAdd, op, nextMul); // 调用构造函数Utype=1
        }

        // 3. 将最终构建的链式AddExp赋值给当前对象的变量
        if (currentAdd.Utype == 0) {
            this.mulExp0 = currentAdd.mulExp0;
            this.Utype = 0;
        } else {
            this.addExp1 = currentAdd.addExp1;
            this.plusToken1 = currentAdd.plusToken1;
            this.minuToken1 = currentAdd.minuToken1;
            this.mulExp1 = currentAdd.mulExp1;
            this.Utype = 1;
        }
    }



    @Override
    public void visit(){
        if(this.Utype==0){
            mulExp0.visit();
        }
        else{
            addExp1.visit();
            mulExp1.visit();
        }
    }




    public AddExp(){
        super(SyntaxType.ADD_EXP);
    }
}
