package frontend.ast.exp;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.Ident;
import frontend.ast.value.Number;
import midend.Ir.IrBasicBlock;

import java.io.IOException;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class PrimaryExp extends Node {
    //PrimaryExp → '(' Exp ')' | LVal | Number

    private int Utype;
    private Token lparentToken0=null;
    private Exp exp0=null;
    private Token rparentToken0=null;
    //------------------------------
    private LVal lVal1=null;
    //------------------------------
    private Number number2=null;
    public PrimaryExp(Token lparentToken,
                      Exp exp,
                      Token rparentToken) {
        super(SyntaxType.PRIMARY_EXP);
        this.lparentToken0=lparentToken;
        this.exp0=exp;
        this.rparentToken0=rparentToken;
        this.Utype=0;
    }
    //-------------------------
    public PrimaryExp(LVal lVal){
        super(SyntaxType.PRIMARY_EXP);
        this.lVal1=lVal;
        this.Utype=1;
    }
    //-----------------------------------------------
    public PrimaryExp(Number number){
        super(SyntaxType.PRIMARY_EXP);
        this.number2=number;
        this.Utype=2;
    }
    //PrimaryExp → '(' Exp ')' | LVal | Number
    @Override
    public void formatOutput() throws IOException {
        if(lparentToken0!=null){
            lparentToken0.formatOutput();
            exp0.formatOutput();
            if(rparentToken0!=null) {//如果发生错误j则没有右小括号
                rparentToken0.formatOutput();
            }
        }
        else if(lVal1!=null){
            lVal1.formatOutput();
        }
        else{
            number2.formatOutput();
        }
        outputSelf();
    }

    //PrimaryExp → '(' Exp ')' | LVal | Number
    //可能的错误：缺少右小括号’)’   j  报错行号为右小括号前一个非终结符所在行号。
    @Override
    public void parse(){
        //'(' Exp ')'
        if(Peek(0).getType().equals("LPARENT")){
            this.lparentToken0=Peek(0);
            nextToken();
            Exp exp=new Exp();
            exp.parse();
            this.exp0=exp;
            //')'错误检测
            if(Peek(0).getType().equals("RPARENT")) {
                this.rparentToken0 = Peek(0);
                nextToken();
            }else{//缺失右小括号，报错为j
                this.rparentToken0 = new Token("RPARENT",")",this.lparentToken0.getLineNumber());
                addError(GetBeforeLineNumber(), "j");
            }
        }
        //Number
        else if(Peek(0).getType().equals("INTCON")){
            Number number =new Number();
            number.parse();
            this.number2=number;
        }
        //LVal
        else {
            LVal lVal =new LVal();
            lVal.parse();
            this.lVal1=lVal;
        }
    }
    @Override
    public void visit(){
        if(this.exp0!=null){
            this.exp0.visit();
        }
        else if(this.lVal1!=null){
            this.lVal1.visit();
        }
        else{
            this.number2.visit();
        }
    }

    /**
     * 生成 PrimaryExp 的 IR 结果：
     *   '(' Exp ')'  → 借用内部 Exp 的结果
     *   LVal         → 借用 LVal.generateIr
     *   Number       → 字面量
     */
    public String generateIr(IrBasicBlock curBlock) {
        if (this.exp0 != null) {
            return this.exp0.generateIr(curBlock);
        }
        else if (this.lVal1 != null) {
            return this.lVal1.generateIr(curBlock);
        }
        else if (this.number2 != null) {
            return this.number2.generateIr(curBlock);
        }
        else {
            return "0";
        }
    }

    // PrimaryExp → '(' Exp ')' | LVal | Number
    // 计算 PrimaryExp 的常量值
    public int GetValue() {
        if (exp0 != null) {
            // '(' Exp ')'
            return exp0.GetValue();
        } else if (lVal1 != null) {
            // LVal：要求是常量（如 const int a = 0;）
            return lVal1.GetConstValue();
        } else if (number2 != null) {
            // Number → IntConst
            return number2.GetValue();
        } else {
            return 0;
        }
    }

    public PrimaryExp(){
        super(SyntaxType.PRIMARY_EXP);
    }
}