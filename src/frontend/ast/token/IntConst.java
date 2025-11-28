package frontend.ast.token;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class IntConst extends Node{
    //IntConst只有Token
    private Token intConstToken;
    public IntConst(Token intConstToken) {
        super(SyntaxType.INT_CONST);
        this.intConstToken=intConstToken;
    }
    @Override
    public void formatOutput() throws IOException {
        intConstToken.formatOutput();
    }
    @Override
    public void parse(){
        this.intConstToken=Peek(0);
        nextToken();
    }
    @Override
    public void visit(){

    }
    public String GetNumberName(){
        return this.intConstToken.getValue();
    }

    public IntConst(){
        super(SyntaxType.INT_CONST);
    }
}
