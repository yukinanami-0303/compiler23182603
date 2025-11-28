package frontend.ast.exp;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.recursion.AddExp;
import frontend.ast.token.Ident;

import java.io.IOException;

import static frontend.TokenStream.Peek;

public class Exp extends Node {

    protected Token FirstToken;
    protected Token SecondToken;
    //Exp → AddExp
    private AddExp addExp;
    public Exp(AddExp addExp) {
        super(SyntaxType.EXP);
        this.addExp=addExp;
    }
    @Override
    public void formatOutput() throws IOException {
        addExp.formatOutput();
        outputSelf();
    }
    @Override
    public void parse(){
        this.FirstToken=Peek(0);
        this.SecondToken=Peek(1);
        AddExp addExp =new AddExp();
        addExp.parse();
        this.addExp=addExp;
    }

    @Override
    public void visit(){
        addExp.visit();
    }


    public Token GetFirstToken(){
        return FirstToken;
    }
    public Token GetSecondToken(){
        return SecondToken;
    }
    public Exp(){
        super(SyntaxType.EXP);
    }
}