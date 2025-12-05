package frontend.ast.stmt;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.func.FuncFParam;
import frontend.ast.token.Ident;
import midend.Symbol.Symbol;
import midend.Symbol.SymbolManager;

import java.io.IOException;
import java.util.ArrayList;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;
import static midend.Symbol.SymbolManager.GetSymbol;
import midend.Ir.IrBasicBlock;

public class ForStmt extends Node{
    //ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
    private LVal lVal;
    private Token assignToken;
    private Exp exp;
    private ArrayList<Token> commaTokens;
    private ArrayList<LVal> lVals;
    private ArrayList<Token> assignTokens;
    private ArrayList<Exp> exps;
    public ForStmt(LVal lVal,
                   Token assignToken,
                   Exp exp,
                   ArrayList<Token> commaTokens,
                   ArrayList<LVal> lVals,
                   ArrayList<Token> assignTokens,
                   ArrayList<Exp> exps
                   ) {
        super(SyntaxType.FOR_STMT);
        this.lVal=lVal;
        this.assignToken=assignToken;
        this.exp=exp;
        this.commaTokens=commaTokens;
        this.lVals=lVals;
        this.assignTokens=assignTokens;
        this.exps=exps;
    }
    //ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
    @Override
    public void formatOutput() throws IOException {
        lVal.formatOutput();
        assignToken.formatOutput();
        exp.formatOutput();
        if(commaTokens!=null){
            for(int i=0;i<commaTokens.size();i++){
                commaTokens.get(i).formatOutput();
                lVals.get(i).formatOutput();
                assignTokens.get(i).formatOutput();
                exps.get(i).formatOutput();
            }
        }
        outputSelf();
    }


    @Override
    public void parse(){
        LVal lVal=new LVal();
        lVal.parse();
        this.lVal=lVal;
        this.assignToken=Peek(0);
        nextToken();
        Exp exp=new Exp();
        exp.parse();
        this.exp=exp;
        if(Peek(0).getType().equals("COMMA")){//如果可选先创建ArrayList
            this.commaTokens=new ArrayList<Token>();
            this.lVals=new ArrayList<LVal>();
            this.assignTokens=new ArrayList<Token>();
            this.exps=new ArrayList<Exp>();
        }
        while(Peek(0).getType().equals("COMMA")){
            this.commaTokens.add(Peek(0));
            nextToken();
            LVal lVals =new LVal();
            lVals.parse();
            this.lVals.add(lVals);
            this.assignTokens.add(Peek(0));
            nextToken();
            Exp exps=new Exp();
            exps.parse();
            this.exps.add(exps);
        }
    }
    //ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
    @Override
    public void visit(){
        lVal.visit();
        Symbol symbol= SymbolManager.GetSymbol(this.lVal.GetIdent().GetTokenValue());
        if(symbol==null||symbol.GetSymbolType().equals("ConstInt")||symbol.GetSymbolType().equals("ConstIntArray")){
            addError(this.lVal.GetIdent().GetTokenLineNumber(),"h");
        }
        exp.visit();
        if(commaTokens!=null){
            for(int i=0;i<commaTokens.size();i++){
                lVals.get(i).visit();
                symbol= SymbolManager.GetSymbol(this.lVals.get(i).GetIdent().GetTokenValue());
                if(symbol==null||symbol.GetSymbolType().equals("ConstInt")||symbol.GetSymbolType().equals("ConstIntArray")){
                    addError(this.lVals.get(i).GetIdent().GetTokenLineNumber(),"h");
                }
                exps.get(i).visit();
            }
        }
    }

    /**
     * 生成 ForStmt 中一组赋值的 IR：
     *   ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
     * 在给定的基本块 block 中依次生成：
     *   store i32 <Exp>, i32* <LVal>
     */
    public void generateIr(IrBasicBlock block) {
        if (block == null) {
            return;
        }

        // 第一组：lVal = exp
        if (lVal != null && exp != null) {
            String value = exp.generateIr(block);      // 右值
            String addr  = lVal.generateAddr(block);   // 左值地址
            block.addInstruction("store i32 " + value + ", i32* " + addr);
        }

        // 后续的 { ',' LVal '=' Exp }
        if (commaTokens != null) {
            for (int i = 0; i < commaTokens.size(); i++) {
                LVal lv = lVals.get(i);
                Exp  e  = exps.get(i);
                String value = e.generateIr(block);
                String addr  = lv.generateAddr(block);
                block.addInstruction("store i32 " + value + ", i32* " + addr);
            }
        }
    }

    public ForStmt(){
        super(SyntaxType.FOR_STMT);
    }
}
