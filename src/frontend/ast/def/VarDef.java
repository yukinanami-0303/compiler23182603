package frontend.ast.def;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.token.Ident;
import frontend.ast.value.InitVal;
import midend.Symbol.SymbolManager;
import midend.Symbol.ValueSymbol;

import java.io.IOException;
import java.util.ArrayList;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class VarDef extends Node{
    //VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    public boolean isStatic=false;
    private ValueSymbol symbol;
    private int Utype;
    private Ident ident0=null;
    private Token lbrackToken0=null;
    private ConstExp constExp0=null;
    private Token rbrackToken0=null;
    //-----------------------------------
    private Ident ident1=null;
    private Token lbrackToken1=null;
    private ConstExp constExp1=null;
    private Token rbrackToken1=null;
    private Token assignToken1=null;
    private InitVal initVal1=null;
    public VarDef(Ident ident,
                  Token lbrackToken,
                  ConstExp constExp,
                  Token rbrackToken
                  ) {
        super(SyntaxType.VAR_DEF);
        this.ident0=ident;
        this.lbrackToken0=lbrackToken;
        this.constExp0=constExp;
        this.rbrackToken0=rbrackToken;
        this.Utype=0;
    }
    //----------------------------------------------
    public VarDef(Ident ident,
                  Token lbrackToken,
                  ConstExp constExp,
                  Token rbrackToken,
                  Token assignToken,
                  InitVal initVal
                  ) {
        super(SyntaxType.VAR_DEF);
        this.ident1=ident;
        this.lbrackToken1=lbrackToken;
        this.constExp1=constExp;
        this.rbrackToken1=rbrackToken;
        this.assignToken1=assignToken;
        this.initVal1=initVal;
        this.Utype=1;
    }
    //VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    @Override
    public void formatOutput() throws IOException {
        if(this.Utype==0){
            ident0.formatOutput();
            if(lbrackToken0!=null){//选择了'['ConstExp']'
                lbrackToken0.formatOutput();
                constExp0.formatOutput();
                if(rbrackToken0!=null) {//如果发生错误k则没有右中括号
                    rbrackToken0.formatOutput();
                }
            }
        }
        else{
            ident1.formatOutput();
            if(lbrackToken1!=null){//选择了'['ConstExp']'
                lbrackToken1.formatOutput();
                constExp1.formatOutput();
                if(rbrackToken1!=null) {//如果发生错误k则没有右中括号
                    rbrackToken1.formatOutput();
                }
            }
            assignToken1.formatOutput();
            initVal1.formatOutput();
        }
        outputSelf();
    }

    //VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    //可能的错误：缺少右中括号’]’ k  报错行号为右中括号前一个非终结符所在行号。
    @Override
    public void parse(){

        Ident ident=new Ident();
        Token lbrackToken=null;
        ConstExp constExp=null;
        Token rbrackToken=null;
        ident.parse();

        //若为多维数组，则将下面的if改成while
        if(Peek(0).getType().equals("LBRACK")){
            //[
            lbrackToken=Peek(0);
            nextToken();
            //ConstExp
            constExp=new ConstExp();
            constExp.parse();
            //]
            if(Peek(0).getType().equals("RBRACK")) {
                rbrackToken = Peek(0);
                nextToken();
            }else{//缺失右中括号，报错为k
                rbrackToken = new Token("RBRACK","]",ident.GetTokenLineNumber());
                addError(GetBeforeLineNumber(), "k");
            }
        }
        //赋值的情况
        if(Peek(0).getType().equals("ASSIGN")){//Utype=1
            this.ident1=ident;
            this.lbrackToken1=lbrackToken;
            this.constExp1=constExp;
            this.rbrackToken1=rbrackToken;
            //'='
            this.assignToken1=Peek(0);
            nextToken();
            InitVal initVal=new InitVal();
            initVal.parse();
            this.initVal1=initVal;
            this.Utype=1;
        }
        //不赋值的情况
        else{//Utype=0
            this.ident0=ident;
            this.lbrackToken0=lbrackToken;
            this.constExp0=constExp;
            this.rbrackToken0=rbrackToken;
            this.Utype=0;
        }
    }
    //VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    @Override
    public void visit(){
        //是static
        if(this.isStatic){
            if(this.assignToken1==null){//不赋值
                String symbolName=ident0.GetTokenValue();
                //ArrayList<Integer> initValueList=new ArrayList<Integer>();
                if(constExp0!=null){//是数组
                    this.constExp0.visit();
                    this.symbol=new ValueSymbol(symbolName,"StaticIntArray");
                }
                else{//不是数组
                    this.symbol=new ValueSymbol(symbolName,"StaticInt");
                }
                SymbolManager.AddSymbol(this.symbol, ident0.GetTokenLineNumber());
            }
            else{//赋值
                String symbolName=ident1.GetTokenValue();
                //ArrayList<Integer> initValueList=new ArrayList<Integer>();
                if(constExp1!=null){//是数组
                    this.constExp1.visit();
                    this.initVal1.visit();
                    //initValueList=this.initVal1.GetInitValueList();
                    this.symbol=new ValueSymbol(symbolName,"StaticIntArray");
                    //symbol.SetValueList(initValueList);
                }
                else{//不是数组
                    this.initVal1.visit();
                    //initValueList=this.initVal1.GetInitValueList();
                    this.symbol=new ValueSymbol(symbolName,"StaticInt");//变量，相当于数组数量为0
                    //symbol.SetValueList(initValueList);
                }
                SymbolManager.AddSymbol(this.symbol, ident1.GetTokenLineNumber());
            }
        }
        //不是static
        else{
            if(this.assignToken1==null){//不赋值
                String symbolName=ident0.GetTokenValue();
                //ArrayList<Integer> initValueList=new ArrayList<Integer>();
                if(constExp0!=null){//是数组
                    this.constExp0.visit();
                    this.symbol=new ValueSymbol(symbolName,"IntArray");
                }
                else{//不是数组
                    this.symbol=new ValueSymbol(symbolName,"Int");
                }
                SymbolManager.AddSymbol(this.symbol, ident0.GetTokenLineNumber());
            }
            else{//赋值
                String symbolName=ident1.GetTokenValue();
                ArrayList<Integer> initValueList=new ArrayList<Integer>();
                if(constExp1!=null){//是数组
                    this.constExp1.visit();
                    this.initVal1.visit();
                    //initValueList=this.initVal1.GetInitValueList();
                    this.symbol=new ValueSymbol(symbolName,"IntArray");
                    symbol.SetValueList(initValueList);
                }
                else{//不是数组
                    this.initVal1.visit();
                    //initValueList=this.initVal1.GetInitValueList();
                    this.symbol=new ValueSymbol(symbolName,"Int");//变量，相当于数组数量为0
                    symbol.SetValueList(initValueList);
                }
                SymbolManager.AddSymbol(this.symbol, ident1.GetTokenLineNumber());
            }
        }
    }

    public VarDef(){
        super(SyntaxType.VAR_DEF);
    }
}
