package frontend.ast.value;
import frontend.Parser;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.IntConst;
import midend.Ir.IrBasicBlock;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class Number extends Node{
    //Number → IntConst

    private IntConst intConst;
    public Number(IntConst intConst) {
        super(SyntaxType.NUMBER);
        this.intConst=intConst;
    }
    @Override
    public void formatOutput() throws IOException {
        intConst.formatOutput();
        outputSelf();
    }
    @Override
    public void parse(){
        IntConst intConst=new IntConst();
        intConst.parse();
        this.intConst=intConst;

    }
    @Override
    public void visit(){
        intConst.visit();
    }

    public String generateIr(IrBasicBlock curBlock) {
        // 这里复用 IntConst 的 GetNumberName() 方法
        return this.intConst.GetNumberName();
    }

    // Number → IntConst
// 直接把字面量字符串转成 int
    public int GetValue() {
        return Integer.parseInt(this.intConst.GetNumberName());
    }

    public Number(){
        super(SyntaxType.NUMBER);
    }
}
