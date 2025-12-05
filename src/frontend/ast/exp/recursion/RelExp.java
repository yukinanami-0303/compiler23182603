package frontend.ast.exp.recursion;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrFactory;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class RelExp extends Node{
    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp

    private int Utype;
    private AddExp addExp0;
    //----------------------------------
    private RelExp relExp1;
    private Token lssToken1;
    private Token greToken1;
    private Token leqToken1;
    private Token geqToken1;
    private AddExp addExp1;

    public RelExp(AddExp addExp) {
        super(SyntaxType.REL_EXP);
        this.addExp0=addExp;
        this.Utype=0;
    }
    public RelExp(RelExp relExp,
                  Token token,
                  AddExp addExp) {
        super(SyntaxType.REL_EXP);
        this.relExp1=relExp;
        if(token.getType().equals("LSS")){
            this.lssToken1=token;
        }
        else if(token.getType().equals("GRE")){
            this.greToken1=token;
        }
        else if(token.getType().equals("LEQ")){
            this.leqToken1=token;
        }
        else if(token.getType().equals("GEQ")){
            this.geqToken1=token;
        }
        this.addExp1=addExp;
        this.Utype=1;
    }
    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    @Override
    public void formatOutput() throws IOException {
        if(this.Utype==0){
            addExp0.formatOutput();
        }
        else{
            relExp1.formatOutput();
            if(lssToken1!=null){
                lssToken1.formatOutput();
            }
            else if(greToken1!=null){
                greToken1.formatOutput();
            }
            else if(leqToken1!=null){
                leqToken1.formatOutput();
            }
            else{
                geqToken1.formatOutput();
            }
            addExp1.formatOutput();
        }
        outputSelf();
    }

    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    //改写成RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    @Override
    public void parse() {
        // 1. 解析第一个AddExp（必须存在，作为基础）
        AddExp currentAdd = new AddExp();
        currentAdd.parse();
        // 初始RelExp为Utype=0（仅包含一个AddExp）
        RelExp currentRel = new RelExp(currentAdd); // 调用构造函数Utype=0

        // 2. 循环解析后续的关系运算符和AddExp（可选，0次或多次）
        String currentType = Peek(0).getType();
        while (currentType.equals("LSS")    // '<'
                || currentType.equals("GRE") // '>'
                || currentType.equals("LEQ") // '<='
                || currentType.equals("GEQ")) { // '>='
            // 2.1 解析运算符
            Token relOp = Peek(0);
            nextToken(); // 消费当前关系运算符

            // 2.2 解析运算符后的AddExp
            AddExp nextAdd = new AddExp();
            nextAdd.parse();

            // 2.3 构建新的RelExp（Utype=1），将当前RelExp作为左部
            currentRel = new RelExp(currentRel, relOp, nextAdd); // 调用构造函数Utype=1

            // 更新循环条件（检查下一个token是否仍为关系运算符）
            currentType = Peek(0).getType();
        }

        // 3. 将最终构建的链式RelExp赋值给当前对象的变量
        if (currentRel.Utype == 0) {
            this.addExp0 = currentRel.addExp0; // 单个AddExp的情况
            this.Utype = 0;
        } else {
            this.relExp1 = currentRel.relExp1; // 左部RelExp
            this.lssToken1 = currentRel.lssToken1; // '<'运算符（按需赋值）
            this.greToken1 = currentRel.greToken1; // '>'运算符（按需赋值）
            this.leqToken1 = currentRel.leqToken1; // '<='运算符（按需赋值）
            this.geqToken1 = currentRel.geqToken1; // '>='运算符（按需赋值）
            this.addExp1 = currentRel.addExp1; // 右部AddExp
            this.Utype = 1;
        }
    }

    @Override
    public void visit(){
        if(this.Utype==0){
            addExp0.visit();
        }
        else{
            relExp1.visit();
            addExp1.visit();
        }
    }



    /**
     * 生成关系表达式的 IR 值。
     * 有比较运算符时返回 i32（0 或 1），否则返回算术表达式的值。
     * RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
     */
    public String generateIr(IrBasicBlock curBlock) {
        if (this.Utype == 0) {
            // RelExp -> AddExp
            return addExp0.generateIr(curBlock);
        } else {
            // RelExp -> RelExp op AddExp
            String left = relExp1.generateIr(curBlock);
            String right = addExp1.generateIr(curBlock);

            IrFactory factory = IrFactory.getInstance();
            String cmp = factory.newTemp();
            String op;
            if (lssToken1 != null) {
                op = "slt";
            } else if (greToken1 != null) {
                op = "sgt";
            } else if (leqToken1 != null) {
                op = "sle";
            } else {
                // geqToken1 != null
                op = "sge";
            }

            curBlock.addInstruction(cmp + " = icmp " + op + " i32 " + left + ", " + right);
            String zext = factory.newTemp();
            curBlock.addInstruction(zext + " = zext i1 " + cmp + " to i32");
            return zext;
        }
    }

    public RelExp(){
        super(SyntaxType.REL_EXP);
    }

}
