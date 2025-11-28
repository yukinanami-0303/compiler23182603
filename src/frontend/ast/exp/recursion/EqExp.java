package frontend.ast.exp.recursion;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;

import static frontend.TokenStream.*;

public class EqExp extends Node{
    //EqExp → RelExp | EqExp ('==' | '!=') RelExp

    private int Utype;
    private RelExp relExp0=null;
    //------------------------------------
    private EqExp eqExp1=null;
    private Token eqlToken1=null;
    private Token neqToken1=null;
    private RelExp relExp1=null;
    public EqExp(RelExp relExp) {
        super(SyntaxType.EQ_EXP);
        this.relExp0=relExp;
        this.Utype=0;
    }
    //-------------------------------
    public EqExp(EqExp eqExp,
                 Token token,
                 RelExp relExp) {
        super(SyntaxType.EQ_EXP);
        this.eqExp1=eqExp;
        if(token.getType().equals("EQL")){
            this.eqlToken1=token;
        }
        else if (token.getType().equals("NEQ")) {
            this.neqToken1=token;
        }
        this.relExp1=relExp;
        this.Utype=1;
    }
    //EqExp → RelExp | EqExp ('==' | '!=') RelExp
    //改写成EqExp → RelExp { ('==' | '!=') RelExp }
    @Override
    public void formatOutput() throws IOException {

        if(this.Utype==0){
            relExp0.formatOutput();
        }
        else{
            eqExp1.formatOutput();
            if(eqlToken1!=null){
                eqlToken1.formatOutput();
            }
            else{
                neqToken1.formatOutput();
            }
            relExp1.formatOutput();
        }
        outputSelf();
    }
    //EqExp → RelExp | EqExp ('==' | '!=') RelExp
    //改写成EqExp → RelExp { ('==' | '!=') RelExp }
    @Override
    public void parse() {

        // 1. 解析第一个RelExp（必须存在，作为基础）
        RelExp currentRel = new RelExp();
        currentRel.parse();
        // 初始EqExp为Utype=0（仅包含一个RelExp）
        EqExp currentEq = new EqExp(currentRel); // 调用构造函数Utype=0

        // 2. 循环解析后续的'=='/'!='和RelExp（可选，0次或多次）
        while (Peek(0).getType().equals("EQL") || Peek(0).getType().equals("NEQ")) {
            // 2.1 解析运算符
            Token op = Peek(0);
            nextToken(); // 消费运算符

            // 2.2 解析运算符后的RelExp
            RelExp nextRel = new RelExp();
            nextRel.parse();

            // 2.3 构建新的EqExp（Utype=1），将当前EqExp作为左部
            currentEq = new EqExp(currentEq, op, nextRel); // 调用构造函数Utype=1
        }

        // 3. 将最终构建的链式EqExp赋值给当前对象的变量
        if (currentEq.Utype == 0) {
            this.relExp0 = currentEq.relExp0;
            this.Utype = 0;
        } else {
            this.eqExp1 = currentEq.eqExp1;
            this.eqlToken1 = currentEq.eqlToken1;
            this.neqToken1 = currentEq.neqToken1;
            this.relExp1 = currentEq.relExp1;
            this.Utype = 1;
        }
    }

    @Override
    public void visit(){
        if(this.Utype==0){
            relExp0.visit();
        }
        else{
            eqExp1.visit();
            relExp1.visit();
        }
    }



    public EqExp(){
        super(SyntaxType.EQ_EXP);
    }


}
