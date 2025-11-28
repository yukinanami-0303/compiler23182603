package frontend.ast.token;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class FuncType extends Node{
    //FuncType → 'void' | 'int'
    private Token voidToken=null;
    private Token intToken=null;
    public FuncType(Token token) {
        super(SyntaxType.FUNC_TYPE);
        if(token.getType().equals("VOIDTK")){
            this.voidToken=token;
        }
        else if(token.getType().equals("INTTK")){
            this.intToken=token;
        }
    }

    @Override
    public void formatOutput() throws IOException {
        if(voidToken!=null){
            voidToken.formatOutput();
        }
        else if(intToken!=null){
            intToken.formatOutput();
        }
        outputSelf();
    }
    @Override
    public void parse(){
        if(Peek(0).getType().equals("VOIDTK")){
            this.voidToken=Peek(0);
            nextToken();
        }
        else{
            this.intToken=Peek(0);
            nextToken();
        }
    }
    @Override
    public void visit(){}

    public String GetFuncType(){
        if(this.intToken!=null){
            return "int";
        }
        else{
            return "void";
        }
    }
    public FuncType(){
        super(SyntaxType.FUNC_TYPE);
    }
}
