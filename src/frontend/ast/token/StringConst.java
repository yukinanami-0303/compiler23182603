package frontend.ast.token;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class StringConst extends Node{
    //StringConst只有Token
    private Token stringConstToken;
    public StringConst(Token stringConstToken) {
        super(SyntaxType.STRING_CONST);
        this.stringConstToken=stringConstToken;
    }
    @Override
    public void formatOutput() throws IOException {
        stringConstToken.formatOutput();
    }
    @Override
    public void parse(){
        this.stringConstToken=Peek(0);
        nextToken();
    }
    @Override
    public void visit(){

    }
    public String GetConstString(){
        return this.stringConstToken.getValue();
    }
    public StringConst(){
        super(SyntaxType.STRING_CONST);
    }
}
