package frontend.ast.func;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.BType;
import frontend.ast.token.Ident;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrBuilder;
import midend.Ir.IrFunction;
import midend.Symbol.FuncSymbol;
import midend.Symbol.Symbol;
import midend.Symbol.SymbolManager;
import midend.Symbol.ValueSymbol;

import java.io.IOException;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class FuncFParam extends Node{
    //FuncFParam → BType Ident ['[' ']']
    private BType bType;
    private Ident ident;
    private ValueSymbol symbol;
    private Token lbrackToken;
    private Token rbrackToken;
    public FuncFParam(BType bType,
                      Ident ident,
                      Token lbrackToken,
                      Token rbrackToken) {
        super(SyntaxType.FUNC_FORMAL_PARAM);
        this.bType=bType;
        this.ident=ident;
        this.lbrackToken=lbrackToken;
        this.rbrackToken=rbrackToken;
    }

    @Override
    public void formatOutput() throws IOException {
        bType.formatOutput();
        ident.formatOutput();
        if(lbrackToken!=null){
            lbrackToken.formatOutput();
        }
        if(rbrackToken!=null){
            rbrackToken.formatOutput();
        }
        outputSelf();
    }
    //FuncFParam → BType Ident ['[' ']']
    //可能的错误：缺少右中括号’]’ k  报错行号为右中括号前一个非终结符所在行号。
    @Override
    public void parse(){
        BType bType =new BType();
        bType.parse();
        this.bType=bType;
        Ident ident =new Ident();
        ident.parse();
        this.ident=ident;
        //'['
        if(Peek(0).getType().equals("LBRACK")){
            this.lbrackToken=Peek(0);
            nextToken();
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
    //FuncFParam → BType Ident ['[' ']']
    @Override
    public void visit(){
        String symbolName = ident.GetTokenValue();
        ValueSymbol vSym;

        if (this.lbrackToken != null) { // 数组形参
            vSym = new ValueSymbol(symbolName, "IntArray");
            vSym.SetIsArrayParam(true);
        } else {                        // 普通 int 形参
            vSym = new ValueSymbol(symbolName, "Int");
        }
        this.symbol = vSym;
        SymbolManager.AddSymbol(this.symbol, ident.GetTokenLineNumber());


        // ===== IR: 形参加入当前函数 =====
        IrFunction func = IrBuilder.getCurrentFunction();
        IrBasicBlock entry = IrBuilder.getCurrentBlock();
        if (func != null && entry != null) {
            String irParamName = "%arg." + symbolName;
            vSym.SetIrParamName(irParamName);

            if (this.lbrackToken != null) {
                // 数组形参：参数类型 i32*
                func.addParam("i32*", irParamName);
                // 不再对 a 做 alloca i32，后面 LVal 直接用这个指针做 GEP
            } else {
                // 普通 int 形参
                func.addParam("i32", irParamName);
                entry.addInstruction("%" + symbolName + " = alloca i32");
                entry.addInstruction("store i32 " + irParamName + ", i32* %" + symbolName);
            }
        }
    }
    public Symbol GetSymbol(){
        return this.symbol;
    }
    public FuncFParam(){
        super(SyntaxType.FUNC_FORMAL_PARAM);
    }
}
