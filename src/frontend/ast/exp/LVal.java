package frontend.ast.exp;
import frontend.Parser;
import frontend.Token;
import frontend.ast.SyntaxType;
import frontend.ast.Node;
import frontend.ast.token.Ident;
import midend.Symbol.Symbol;
import midend.Symbol.SymbolManager;
import midend.Symbol.ValueSymbol;

import java.io.IOException;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class LVal extends Node {
    //LVal → Ident ['[' Exp ']']


    private Ident ident=null;
    private Token lbrackToken=null;
    private Exp exp=null;
    private Token rbrackToken=null;
    public LVal(Ident ident,
                Token lbrackToken,
                Exp exp,
                Token rbrackToken) {
        super(SyntaxType.LVAL_EXP);
        this.ident=ident;
        this.lbrackToken=lbrackToken;
        this.exp=exp;
        this.rbrackToken=rbrackToken;
    }
    @Override
    public void formatOutput() throws IOException {
        ident.formatOutput();
        if(lbrackToken!=null){
            lbrackToken.formatOutput();
            exp.formatOutput();
            if(rbrackToken!=null) {//如果发生错误k则没有右中括号
                rbrackToken.formatOutput();
            }
        }
        outputSelf();
    }

    //LVal → Ident ['[' Exp ']']
    //可能的错误：缺少右中括号’]’ k  报错行号为右中括号前一个非终结符所在行号。
    @Override
    public void parse(){
        //Ident
        Ident ident =new Ident();
        ident.parse();
        this.ident=ident;
        //'['
         if(Peek(0).getType().equals("LBRACK")){
             this.lbrackToken=Peek(0);
             nextToken();
             //Exp
             Exp exp=new Exp();
             exp.parse();
             this.exp=exp;
             //']'并处理错误
             if(Peek(0).getType().equals("RBRACK")) {
                 this.rbrackToken = Peek(0);
                 nextToken();
             }else{//缺失右中括号，报错为k
                 this.rbrackToken = new Token("RBRACK","]",this.ident.GetTokenLineNumber());
                 addError(GetBeforeLineNumber(), "k");
             }
         }
    }

    //LVal → Ident ['[' Exp ']']
    @Override
    public void visit(){
        String identName=ident.GetTokenValue();
        if(SymbolManager.GetSymbol(identName)==null){
            addError(ident.GetTokenLineNumber(), "c");
        }
    }



    public Ident GetIdent(){
        return this.ident;
    }


    public LVal(){
        super(SyntaxType.LVAL_EXP);
    }
}
