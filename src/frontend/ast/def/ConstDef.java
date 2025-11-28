package frontend.ast.def;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.token.Ident;
import frontend.ast.value.ConstInitVal;
import midend.Symbol.SymbolManager;
import midend.Symbol.ValueSymbol;

import java.io.IOException;
import java.util.ArrayList;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;
import static midend.Symbol.SymbolManager.AddSymbol;

public class ConstDef extends Node{
    //ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    private ValueSymbol symbol;
    private Ident ident;
    private Token lbrackToken;
    private ConstExp constExp;
    private Token rbrackToken;
    private Token assignToken;
    private ConstInitVal constInitVal;
    public ConstDef(Ident ident,
                    Token lbrackToken,
                    ConstExp constExp,
                    Token rbrackToken,
                    Token assignToken,
                    ConstInitVal constInitVal) {
        super(SyntaxType.CONST_DEF);
        this.ident=ident;
        this.lbrackToken=lbrackToken;
        this.constExp=constExp;
        this.rbrackToken=rbrackToken;
        this.assignToken=assignToken;
        this.constInitVal=constInitVal;
    }



    //ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    @Override
    public void formatOutput() throws IOException {
        ident.formatOutput();
        if(lbrackToken!=null){
            lbrackToken.formatOutput();
            constExp.formatOutput();
            if(rbrackToken!=null) {//如果发生错误k则没有右中括号
                rbrackToken.formatOutput();
            }
        }
        assignToken.formatOutput();
        constInitVal.formatOutput();
        outputSelf();
    }


    //可能的错误：缺少右中括号’]’  k  报错行号为右中括号前一个非终结符所在行号。
    @Override
    public void parse(){
        Ident ident =new Ident();
        ident.parse();
        this.ident=ident;
        if(Peek(0).getType().equals("LBRACK")){
            //[
            this.lbrackToken=Peek(0);
            nextToken();
            //ConstExp
            ConstExp constExp=new ConstExp();
            constExp.parse();
            this.constExp=constExp;
            //]
            if(Peek(0).getType().equals("RBRACK")) {
                this.rbrackToken = Peek(0);
                nextToken();
            }else{//缺失右中括号，报错为k
                this.rbrackToken = new Token("RBRACK","]",this.ident.GetTokenLineNumber());
                addError(GetBeforeLineNumber(), "k");
            }
        }
        //'='
        this.assignToken=Peek(0);
        nextToken();
        //ConstIntiVal
        ConstInitVal constInitVal =new ConstInitVal();
        constInitVal.parse();
        this.constInitVal=constInitVal;
    }



    //ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    @Override
    public void visit(){
        String symbolName=ident.GetTokenValue();
        ArrayList<Integer> initValueList=new ArrayList<Integer>();
        if(constExp!=null){//是数组
            constExp.visit();
            constInitVal.visit();
            //initValueList = constInitVal.GetInitValueList();
            ValueSymbol symbol=new ValueSymbol(symbolName,"ConstIntArray");
            symbol.SetIsConst(true);
            symbol.SetValueList(initValueList);
            this.symbol = symbol;
            SymbolManager.AddSymbol(this.symbol, ident.GetTokenLineNumber());
        }
        else {
            constInitVal.visit();
            //initValueList = constInitVal.GetInitValueList();
            //const常量
            ValueSymbol symbol = new ValueSymbol(symbolName, "ConstInt");//变量，相当于数组数量为0
            symbol.SetIsConst(true);
            symbol.SetValueList(initValueList);
            this.symbol = symbol;
            SymbolManager.AddSymbol(this.symbol, ident.GetTokenLineNumber());
        }
    }



    public ConstDef(){
        super(SyntaxType.CONST_DEF);
    }
}
