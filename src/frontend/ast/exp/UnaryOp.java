package frontend.ast.exp;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class UnaryOp extends Node {
    //UnaryOp → '+' | '−' | '!'

    private Token plusToken0=null;
    //--------------------------
    private Token minuToken1=null;
    //------------------------------
    private Token notToken2=null;
    public UnaryOp(Token token) {
        super(SyntaxType.UNARY_OP);
        if(token.getType().equals("PLUS")) {
            this.plusToken0 = token;
        }
        else if (token.getType().equals("MINU")) {
            this.minuToken1=token;
        }
        else if(token.getType().equals("NOT")){
            this.notToken2=token;
        }
    }
    @Override
    public void formatOutput() throws IOException {
        if(plusToken0!=null){
            plusToken0.formatOutput();
        }
        else if(minuToken1!=null){
            minuToken1.formatOutput();
        }
        else {
            notToken2.formatOutput();
        }
        outputSelf();
    }


    //UnaryOp → '+' | '−' | '!'
    @Override
    public void parse(){
        if(Peek(0).getType().equals("PLUS")){
            this.plusToken0=Peek(0);
            nextToken();
        }
        else if(Peek(0).getType().equals("MINU")){
            this.minuToken1=Peek(0);
            nextToken();
        }
        else {
            this.notToken2=Peek(0);
            nextToken();
        }
    }

    @Override
    public void visit(){

    }
    public String GetUnaryOp(){
        if(this.plusToken0!=null) {
            return "+";
        }
        else if(this.minuToken1!=null){
            return "-";
        }
        else{
            return "!";
        }
    }
    public UnaryOp(){
        super(SyntaxType.UNARY_OP);
    }
}
