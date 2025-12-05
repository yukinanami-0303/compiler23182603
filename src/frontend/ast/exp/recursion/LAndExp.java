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

public class LAndExp extends Node{
    //LAndExp → EqExp | LAndExp '&&' EqExp
    private int Utype;
    private EqExp eqExp0=null;
    //-----------------------------------
    private LAndExp lAndExp1=null;
    private Token andToken1=null;
    private EqExp eqExp1=null;
    public LAndExp(EqExp eqExp) {
        super(SyntaxType.LAND_EXP);
        this.eqExp0=eqExp;
        this.Utype=0;
    }
    //-----------------------------------------
    public LAndExp(LAndExp lAndExp,
                   Token andToken,
                   EqExp eqExp) {
        super(SyntaxType.LAND_EXP);
        this.lAndExp1=lAndExp;
        this.andToken1=andToken;
        this.eqExp1=eqExp;
        this.Utype=1;
    }
    //LAndExp → EqExp | LAndExp '&&' EqExp
    @Override
    public void formatOutput() throws IOException {
        if(this.Utype==0){
            eqExp0.formatOutput();
        }
        else{
            lAndExp1.formatOutput();
            andToken1.formatOutput();
            eqExp1.formatOutput();
        }
        outputSelf();
    }


    //LAndExp → EqExp | LAndExp '&&' EqExp
    //改写成LAndExp → EqExp { ('&&') EqExp }
    @Override
    public void parse() {
        // 1. 解析第一个EqExp（必须存在，作为基础）
        EqExp currentEq = new EqExp();
        currentEq.parse();
        // 初始LAndExp为Utype=0（仅包含一个EqExp）
        LAndExp currentLAnd = new LAndExp(currentEq); // 调用构造函数Utype=0

        // 2. 循环解析后续的'&&'和EqExp（可选，0次或多次）
        while (Peek(0).getType().equals("AND")) { // 匹配逻辑与运算符'&&'
            // 2.1 解析运算符
            Token andOp = Peek(0);
            nextToken(); // 消费'&&'运算符

            // 2.2 解析运算符后的EqExp
            EqExp nextEq = new EqExp();
            nextEq.parse();

            // 2.3 构建新的LAndExp（Utype=1），将当前LAndExp作为左部
            currentLAnd = new LAndExp(currentLAnd, andOp, nextEq); // 调用构造函数Utype=1
        }

        // 3. 将最终构建的链式LAndExp赋值给当前对象的变量
        if (currentLAnd.Utype == 0) {
            this.eqExp0 = currentLAnd.eqExp0; // 单个EqExp的情况
            this.Utype = 0;
        } else {
            this.lAndExp1 = currentLAnd.lAndExp1; // 左部LAndExp
            this.andToken1 = currentLAnd.andToken1; // '&&'运算符
            this.eqExp1 = currentLAnd.eqExp1; // 右部EqExp
            this.Utype = 1;
        }
    }

    @Override
    public void visit(){
        if(this.Utype==0){
            eqExp0.visit();
        }
        else{
            lAndExp1.visit();
            eqExp1.visit();
        }
    }


    /**
     * 生成逻辑与表达式的 IR 值，返回 i32（0 或 1）。
     * LAndExp → EqExp | LAndExp '&&' EqExp
     */
    public String generateIr(IrBasicBlock curBlock) {
        if (this.Utype == 0) {
            // LAndExp -> EqExp
            return eqExp0.generateIr(curBlock);
        } else {
            // LAndExp -> LAndExp '&&' EqExp
            String left = lAndExp1.generateIr(curBlock);
            String right = eqExp1.generateIr(curBlock);

            IrFactory factory = IrFactory.getInstance();

            // left != 0
            String leftCmp = factory.newTemp();
            curBlock.addInstruction(leftCmp + " = icmp ne i32 " + left + ", 0");

            // right != 0
            String rightCmp = factory.newTemp();
            curBlock.addInstruction(rightCmp + " = icmp ne i32 " + right + ", 0");

            // and
            String andRes = factory.newTemp();
            curBlock.addInstruction(andRes + " = and i1 " + leftCmp + ", " + rightCmp);

            // 扩展到 i32
            String zext = factory.newTemp();
            curBlock.addInstruction(zext + " = zext i1 " + andRes + " to i32");

            return zext;
        }
    }
    public LAndExp(){
        super(SyntaxType.LAND_EXP);
    }
}
