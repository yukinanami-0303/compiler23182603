package frontend.ast.exp;
import frontend.Parser;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.recursion.LOrExp;
import midend.Ir.IrBasicBlock;

import java.io.IOException;

import static frontend.TokenStream.GetcurrentIndex;
import static frontend.TokenStream.Peek;

public class Cond extends Node{

    //Cond → LOrExp
    private LOrExp lOrExp;
    public Cond(LOrExp lOrExp) {
        super(SyntaxType.COND_EXP);
        this.lOrExp=lOrExp;
    }
    @Override
    public void formatOutput() throws IOException {
        lOrExp.formatOutput();
        outputSelf();
    }

    @Override
    public void parse(){
        LOrExp lOrExp=new LOrExp();
        lOrExp.parse();
        this.lOrExp=lOrExp;
    }

    @Override
    public void visit(){
        lOrExp.visit();
    }
    /**
     * 生成条件表达式的 IR 值，返回 i32（0 或 1）。
     */
    public String generateIr(IrBasicBlock curBlock) {
        return lOrExp.generateIr(curBlock);
    }
    public void generateCondBr(IrBasicBlock curBlock,
                               IrBasicBlock trueBlock,
                               IrBasicBlock falseBlock) {
        if (lOrExp != null) {
            lOrExp.generateCondBr(curBlock, trueBlock, falseBlock);
        }
    }


    public Cond(){
        super(SyntaxType.COND_EXP);
    }
}
