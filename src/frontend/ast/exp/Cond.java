package frontend.ast.exp;
import frontend.Parser;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.recursion.LOrExp;

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



    public Cond(){
        super(SyntaxType.COND_EXP);
    }
}
