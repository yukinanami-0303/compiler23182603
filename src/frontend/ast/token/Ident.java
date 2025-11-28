package frontend.ast.token;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class Ident extends Node{
    //Ident只有Token
    private Token identToken;
    public Ident(Token identToken) {
        super(SyntaxType.IDENT);
        this.identToken=identToken;
    }
    @Override
    public void formatOutput() throws IOException {
        identToken.formatOutput();
    }
    @Override
    public void parse(){
        this.identToken=Peek(0);
        nextToken();
    }
    @Override
    public void visit(){}

    public Ident(){
        super(SyntaxType.IDENT);
    }

    public String GetTokenValue() {
        return this.identToken.getValue();
    }
    public String GetTokenType() {
        return this.identToken.getType();
    }
    public int GetTokenLineNumber(){
        return this.identToken.getLineNumber();
    }
}
