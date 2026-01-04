package frontend.ast.def;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.token.Ident;
import frontend.ast.value.ConstInitVal;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrBuilder;
import midend.Ir.IrFactory;
import midend.Ir.IrModule;
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



    @Override
    public void visit() {
        // 两阶段：第一遍语义/符号表，第二遍 IR
        if (midend.MidEnd.isSemantic()) {
            visitSemantic();
        } else {
            visitIR();
        }
    }

    private void visitSemantic() {
        String symbolName = ident.GetTokenValue();
        ArrayList<Integer> initValueList = new ArrayList<>();

        // 语义阶段：计算常量值/数组长度，写入符号表；绝不生成 IR
        if (constExp != null) { // const 数组
            constExp.visit();
            constInitVal.visit();
            initValueList = constInitVal.GetInitValueList();

            int len = constExp.GetValue(); // 题目保证是常量表达式
            ValueSymbol symbol = new ValueSymbol(symbolName, "ConstIntArray");
            symbol.SetIsConst(true);
            symbol.SetArrayLength(len);
            symbol.SetValueList(initValueList);
            this.symbol = symbol;

            SymbolManager.AddSymbol(this.symbol, ident.GetTokenLineNumber());
        } else { // const 标量
            constInitVal.visit();
            initValueList = constInitVal.GetInitValueList();

            ValueSymbol symbol = new ValueSymbol(symbolName, "ConstInt");
            symbol.SetIsConst(true);
            symbol.SetValueList(initValueList);
            this.symbol = symbol;

            SymbolManager.AddSymbol(this.symbol, ident.GetTokenLineNumber());
        }
    }

    private void visitIR() {
        String symbolName = ident.GetTokenValue();

        // 第二遍 IR：复用第一遍创建的 symbol（同一棵 AST），不要再 AddSymbol
        ValueSymbol vSym = (this.symbol instanceof ValueSymbol) ? (ValueSymbol) this.symbol : null;
        if (vSym == null) {
            // 兜底：从符号表查
            midend.Symbol.Symbol s = SymbolManager.GetSymbol(symbolName);
            if (s instanceof ValueSymbol) vSym = (ValueSymbol) s;
        }
        if (vSym == null) return;

        // const 数组
        if (constExp != null || (vSym.GetSymbolType() != null && vSym.GetSymbolType().endsWith("Array"))) {
            int len = vSym.GetArrayLength();
            ArrayList<Integer> initList = vSym.GetValueList();
            if (initList == null) initList = new ArrayList<>();

            // 全局 const 数组：生成全局 constant
            if (vSym.IsGlobal() || SymbolManager.IsGlobal()) {
                IrModule module = IrFactory.getModule();
                String irName = "@" + symbolName;
                vSym.SetIrName(irName);

                StringBuilder elems = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    if (i > 0) elems.append(", ");
                    int v = (i < initList.size()) ? initList.get(i) : 0;
                    elems.append("i32 ").append(v);
                }
                module.addGlobalDef(irName + " = constant [" + len + " x i32] [" + elems + "]");
            }
            // 局部 const 数组：alloca + store 初始化
            else {
                IrBasicBlock block = IrBuilder.getCurrentBlock();
                if (block != null) {
                    String addr = IrFactory.getInstance().newTemp();
                    vSym.SetIrName(addr);

                    block.addInstruction(addr + " = alloca [" + len + " x i32]");
                    for (int i = 0; i < len; i++) {
                        String gep = IrFactory.getInstance().newTemp();
                        block.addInstruction(
                                gep + " = getelementptr [" + len + " x i32], [" + len + " x i32]* " + addr +
                                        ", i32 0, i32 " + i
                        );
                        int v = (i < initList.size()) ? initList.get(i) : 0;
                        block.addInstruction("store i32 " + v + ", i32* " + gep);
                    }
                }
            }
        }
        // const 标量：只需要为全局 const 生成 constant 定义
        else {
            ArrayList<Integer> initList = vSym.GetValueList();
            if ((vSym.IsGlobal() || SymbolManager.IsGlobal()) && initList != null && !initList.isEmpty()) {
                int v = initList.get(0);
                IrModule module = IrFactory.getModule();
                String irName = "@" + symbolName;
                vSym.SetIrName(irName);
                module.addGlobalDef(irName + " = constant i32 " + v);
            }
        }
    }





    public ConstDef(){
        super(SyntaxType.CONST_DEF);
    }
}
