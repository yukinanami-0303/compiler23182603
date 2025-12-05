package frontend.ast.exp;
import frontend.Parser;
import frontend.ast.SyntaxType;
import frontend.ast.Node;
import frontend.ast.exp.recursion.AddExp;

import java.io.IOException;

public class ConstExp extends Node {


    private AddExp addExp;
    //ConstExp → AddExp
    public ConstExp(AddExp addExp) {
        super(SyntaxType.CONST_EXP);
        this.addExp=addExp;
    }
    @Override
    public void formatOutput() throws IOException {
        addExp.formatOutput();
        outputSelf();
    }
    @Override
    public void parse(){
        AddExp addExp =new AddExp();
        addExp.parse();
        this.addExp=addExp;
    }
    @Override
    public void visit(){
        addExp.visit();
    }

    // ConstExp → AddExp
    // 仅用于编译期常量求值（如 ConstInitVal、全局初始化等）
    public int GetValue() {
        if (addExp == null) {
            return 0;
        }
        return addExp.GetValue();
    }

    public ConstExp(){
        super(SyntaxType.CONST_EXP);
    }
}
