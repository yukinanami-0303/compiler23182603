package frontend.ast.token;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class BType extends Node{
    //BType → 'int'
    private Token intToken;
    public BType(Token intToken) {
        super(SyntaxType.BTYPE);
        this.intToken=intToken;
    }


    @Override
    public void formatOutput() throws IOException {
        intToken.formatOutput();
    }
    @Override
    public void parse(){
        this.intToken=Peek(0);
        nextToken();
    }
    @Override
    public void visit(){}

    public int GetBtypeLineNumber(){
        return this.intToken.getLineNumber();
    }
    public BType(){
        super(SyntaxType.BTYPE);
    }
}