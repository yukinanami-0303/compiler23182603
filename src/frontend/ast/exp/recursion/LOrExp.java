package frontend.ast.exp.recursion;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrBuilder;
import midend.Ir.IrFactory;
import midend.Ir.IrFunction;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class LOrExp extends Node{
    //LOrExp → LAndExp | LOrExp '||' LAndExp
    private int Utype;
    private LAndExp lAndExp0=null;
    //------------------------------------
    private LOrExp lOrExp1=null;
    private Token orToken1=null;
    private LAndExp lAndExp1=null;
    public LOrExp(LAndExp lAndExp) {
        super(SyntaxType.LOR_EXP);
        this.lAndExp0=lAndExp;
        this.Utype=0;
    }
    //----------------------------------------
    public LOrExp(LOrExp lOrExp,
                  Token orToken,
                  LAndExp lAndExp) {
        super(SyntaxType.LOR_EXP);
        this.lOrExp1=lOrExp;
        this.orToken1=orToken;
        this.lAndExp1=lAndExp;
        this.Utype=1;
    }
    //LOrExp → LAndExp | LOrExp '||' LAndExp
    @Override
    public void formatOutput() throws IOException {
        if(this.Utype==0){
            lAndExp0.formatOutput();
        }
        else{
            lOrExp1.formatOutput();
            orToken1.formatOutput();
            lAndExp1.formatOutput();
        }
        outputSelf();
    }



    //LOrExp → LAndExp | LOrExp '||' LAndExp
    //改写成LOrExp → LAndExp { '||' LAndExp }
    @Override
    public void parse() {
        // 1. 解析第一个LAndExp（必须存在，作为基础）
        LAndExp currentLAnd = new LAndExp();
        currentLAnd.parse();
        // 初始LOrExp为Utype=0（仅包含一个LAndExp）
        LOrExp currentLOr = new LOrExp(currentLAnd); // 调用构造函数Utype=0

        // 2. 循环解析后续的'||'和LAndExp（可选，0次或多次）
        while (Peek(0).getType().equals("OR")) { // 匹配逻辑或运算符'||'
            // 2.1 解析运算符
            Token orOp = Peek(0);
            nextToken(); // 消费'||'运算符

            // 2.2 解析运算符后的LAndExp
            LAndExp nextLAnd = new LAndExp();
            nextLAnd.parse();

            // 2.3 构建新的LOrExp（Utype=1），将当前LOrExp作为左部
            currentLOr = new LOrExp(currentLOr, orOp, nextLAnd); // 调用构造函数Utype=1
        }

        // 3. 将最终构建的链式LOrExp赋值给当前对象的变量
        if (currentLOr.Utype == 0) {
            this.lAndExp0 = currentLOr.lAndExp0; // 单个LAndExp的情况
            this.Utype = 0;
        } else {
            this.lOrExp1 = currentLOr.lOrExp1; // 左部LOrExp
            this.orToken1 = currentLOr.orToken1; // '||'运算符
            this.lAndExp1 = currentLOr.lAndExp1; // 右部LAndExp
            this.Utype = 1;
        }
    }

    @Override
    public void visit(){
        if(this.Utype==0){
            lAndExp0.visit();
        }
        else{
            lOrExp1.visit();
            lAndExp1.visit();
        }
    }


    /**
     * 生成逻辑或表达式的 IR 值，返回 i32（0 或 1）。
     * LOrExp → LAndExp | LOrExp '||' LAndExp
     */
    public String generateIr(IrBasicBlock curBlock) {
        if (this.Utype == 0) {
            // LOrExp -> LAndExp
            return lAndExp0.generateIr(curBlock);
        } else {
            // LOrExp -> LOrExp '||' LAndExp
            String left = lOrExp1.generateIr(curBlock);
            String right = lAndExp1.generateIr(curBlock);

            IrFactory factory = IrFactory.getInstance();

            // left != 0
            String leftCmp = factory.newTemp();
            curBlock.addInstruction(leftCmp + " = icmp ne i32 " + left + ", 0");

            // right != 0
            String rightCmp = factory.newTemp();
            curBlock.addInstruction(rightCmp + " = icmp ne i32 " + right + ", 0");

            // or
            String orRes = factory.newTemp();
            curBlock.addInstruction(orRes + " = or i1 " + leftCmp + ", " + rightCmp);

            // 扩展到 i32
            String zext = factory.newTemp();
            curBlock.addInstruction(zext + " = zext i1 " + orRes + " to i32");

            return zext;
        }
    }
    public void generateCondBr(IrBasicBlock curBlock,
                               IrBasicBlock trueBlock,
                               IrBasicBlock falseBlock) {
        IrFactory factory = IrFactory.getInstance();

        if (this.Utype == 0) {
            // 只有一个 LAndExp，直接交给 LAndExp 的短路逻辑
            lAndExp0.generateCondBr(curBlock, trueBlock, falseBlock);
        } else {
            // LOrExp -> LOrExp '||' LAndExp
            // 左边：lOrExp1   右边：lAndExp1
            IrFunction curFunc = IrBuilder.getCurrentFunction();
            // 只有左边为假时才会到达 rhsBlock
            IrBasicBlock rhsBlock = factory.createBasicBlock(curFunc, "lor_rhs");

            // 先对左边生成短路控制流：
            //   左真 -> trueBlock
            //   左假 -> rhsBlock
            lOrExp1.generateCondBr(curBlock, trueBlock, rhsBlock);

            // 在 rhsBlock 中继续用 LAndExp 的短路逻辑处理右边
            IrBuilder.setCurrentBlock(rhsBlock);
            lAndExp1.generateCondBr(rhsBlock, trueBlock, falseBlock);
        }
    }

    public LOrExp(){
        super(SyntaxType.LOR_EXP);
    }


}