package frontend.ast.value;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.Exp;
import frontend.ast.func.FuncFParam;
import midend.Ir.IrBasicBlock;

import java.io.IOException;
import java.util.ArrayList;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class InitVal extends Node{
    //InitVal → Exp | '{' [ Exp { ',' Exp } ] '}'
    private int Utype;
    private Exp exp0;
    //-------------------------------------
    private Token lbraceToken1;
    private Exp exp1;
    private ArrayList<Token> commaTokens1;
    private ArrayList<Exp> exps1;
    private Token rbraceToken1;
    public InitVal(Exp exp) {
        super(SyntaxType.INIT_VAL);
        this.exp0=exp;
        this.Utype=0;
    }
    //----------------------------------------------------------
    public InitVal(Token lbraceToken,
                   Exp exp,
                   ArrayList<Token> commaTokens,
                   ArrayList<Exp> exps,
                   Token rbraceToken) {
        super(SyntaxType.INIT_VAL);
        this.lbraceToken1=lbraceToken;
        this.exp1=exp;
        this.commaTokens1=commaTokens;
        this.exps1=exps;
        this.rbraceToken1=rbraceToken;
        this.Utype=1;
    }
    //InitVal → Exp | '{' [ Exp { ',' Exp } ] '}'
    @Override
    public void formatOutput() throws IOException {
        if(exp0!=null){
            exp0.formatOutput();
        }
        else{
            lbraceToken1.formatOutput();
            if(exp1!=null){
                exp1.formatOutput();
            }
            if(commaTokens1!=null){
                for(int i=0;i<commaTokens1.size();i++){
                    commaTokens1.get(i).formatOutput();
                    exps1.get(i).formatOutput();
                }
            }
            rbraceToken1.formatOutput();
        }
        outputSelf();
    }


    @Override
    public void parse(){
        //'{' [ Exp { ',' Exp } ] '}'
        if(Peek(0).getType().equals("LBRACE")){
            this.lbraceToken1=Peek(0);
            nextToken();
            if(!Peek(0).getType().equals("RBRACE")){
                Exp exp1=new Exp();
                exp1.parse();
                this.exp1=exp1;
                if(Peek(0).getType().equals("COMMA")){//如果可选先创建ArrayList
                    this.commaTokens1=new ArrayList<Token>();
                    this.exps1=new ArrayList<Exp>();
                }
                while(Peek(0).getType().equals("COMMA")){
                    this.commaTokens1.add(Peek(0));
                    nextToken();
                    Exp exp=new Exp();
                    exp.parse();
                    this.exps1.add(exp);
                }
            }
            this.rbraceToken1=Peek(0);
            nextToken();
        }
        //Exp
        else {
            Exp exp0=new Exp();
            exp0.parse();
            this.exp0=exp0;
        }
    }
    @Override
    public void visit(){
        if(exp0!=null){
            exp0.visit();
        }
        else{
            if(exp1!=null){
                exp1.visit();
            }
            if(exps1!=null){
                for(int i=0;i<exps1.size();i++){
                    exps1.get(i).visit();
                }
            }
        }
    }
    // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}'
    public ArrayList<Integer> GetInitValueList() {
        ArrayList<Integer> initValueList = new ArrayList<>();
        if (exp0 != null) {
            // 标量初始化：InitVal → Exp
            initValueList.add(exp0.GetValue());
        } else {
            // 数组初始化：InitVal → '{' [ Exp { ',' Exp } ] '}'
            if (exp1 != null) {
                initValueList.add(exp1.GetValue());
            }
            if (exps1 != null) {
                for (int i = 0; i < exps1.size(); i++) {
                    initValueList.add(exps1.get(i).GetValue());
                }
            }
        }
        return initValueList;
    }
    public String generateScalarIr(IrBasicBlock curBlock) {
        if (exp0 != null) {
            // InitVal → Exp
            return exp0.generateIr(curBlock);
        }
        return "0";
    }


    /**
     * 返回数组初始化用的 Exp 列表（仅一维），顺序为 { e0, e1, e2, ... }。
     * 供局部数组的 IR 初始化使用。
     */
    public java.util.List<Exp> getExpList() {
        ArrayList<Exp> list = new ArrayList<>();
        if (exp0 != null) {
            // 标量 InitVal → Exp，当成长度为 1 的数组看
            list.add(exp0);
        } else {
            if (exp1 != null) {
                list.add(exp1);
            }
            if (exps1 != null) {
                list.addAll(exps1);
            }
        }
        return list;
    }

    public InitVal(){
        super(SyntaxType.INIT_VAL);
    }
}