package frontend.ast.value;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.func.FuncFParam;

import java.io.IOException;
import java.util.ArrayList;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class ConstInitVal extends Node{
    //ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
    private int Utype;
    private ConstExp constExp0=null;
    //-------------------------------------------------
    private Token lbraceToken1=null;
    private ConstExp constExp1=null;
    private ArrayList<Token> commaTokens1=null;
    private ArrayList<ConstExp> constExps1=null;
    private Token rbraceToken1;

    public ConstInitVal(ConstExp constExp) {
        super(SyntaxType.CONST_INIT_VAL);
        this.constExp0=constExp;
        this.Utype=0;
    }

    public ConstInitVal(Token lbraceToken,
                        ConstExp constExp,
                        ArrayList<Token> commaTokens,
                        ArrayList<ConstExp> constExps1,
                        Token rbraceToken1
                        ) {
        super(SyntaxType.CONST_INIT_VAL);
        this.lbraceToken1=lbraceToken;
        this.constExp1=constExp;
        this.commaTokens1=commaTokens;
        this.constExps1=constExps1;
        this.rbraceToken1=rbraceToken1;
        this.Utype=1;
    }
    //ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
    @Override
    public void formatOutput() throws IOException {
        //ConstExp
        if(this.constExp0!=null){
            this.constExp0.formatOutput();
        }
        //'{' [ ConstExp { ',' ConstExp } ] '}'
        else {
            //'{'
            this.lbraceToken1.formatOutput();
            //[ConstExp { ',' ConstExp} ]
            if (this.constExp1 != null) {
                this.constExp1.formatOutput();
                for (int i = 0; i < this.commaTokens1.size(); i++) {
                    this.commaTokens1.get(i).formatOutput();
                    this.constExps1.get(i).formatOutput();
                }
            }
            this.rbraceToken1.formatOutput();
        }
        outputSelf();
    }
    @Override
    public void parse(){
        //'{' [ ConstExp { ',' ConstExp } ] '}'
        if(Peek(0).getType().equals("LBRACE")){
            this.lbraceToken1=Peek(0);
            nextToken();
            if(!Peek(0).getType().equals("RBRACE")){
                ConstExp constExp1=new ConstExp();
                constExp1.parse();
                this.constExp1=constExp1;
                if(Peek(0).getType().equals("COMMA")){//如果可选先创建ArrayList
                    this.commaTokens1=new ArrayList<Token>();
                    this.constExps1=new ArrayList<ConstExp>();
                }
                while(Peek(0).getType().equals("COMMA")){
                    this.commaTokens1.add(Peek(0));
                    nextToken();
                    ConstExp constExp=new ConstExp();
                    constExp.parse();
                    this.constExps1.add(constExp);
                }
            }
            this.rbraceToken1=Peek(0);
            nextToken();
        }
        //ConstExp
        else {
            ConstExp constExp0 = new ConstExp();
            constExp0.parse();
            this.constExp0=constExp0;
        }
    }


    //ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
    /*
    public ArrayList<Integer> GetInitValueList() {
        ArrayList<Integer> initValueList = new ArrayList<>();
        if(constExp0!=null){
            initValueList.add(constExp0.GetValue());
        }
        else{
            if(constExp1!=null) {
                initValueList.add(constExp1.GetValue());
                if (constExps1 != null) {
                    for (int i = 0; i < constExps1.size(); i++) {
                        initValueList.add(constExps1.get(i).GetValue());
                    }
                }
            }
        }
        return initValueList;
    }
     */


    @Override
    public void visit(){
        if(constExp0!=null){
            constExp0.visit();
        }
        else{
            if(constExp1!=null){
                constExp1.visit();
            }
            if(constExps1!=null){
                for(int i=0;i<constExps1.size();i++){
                    constExps1.get(i).visit();
                }
            }
        }
    }

    public ConstInitVal(){
        super(SyntaxType.CONST_INIT_VAL);
    }


}