package frontend.ast.def;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.token.Ident;
import frontend.ast.value.InitVal;
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

public class VarDef extends Node{
    //VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    public boolean isStatic=false;
    private static int staticIdCounter = 0;
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
    public void visit() {
        // 两阶段：第一遍语义/符号表，第二遍 IR
        if (midend.MidEnd.isSemantic()) {
            visitSemantic();
        } else {
            visitIR();
        }
    }

    /**
     * 第一遍：只做语义检查 + 构建符号表 + 计算能在编译期求值的初始化（供第二遍 IR 使用）。
     * 注意：这里绝不生成 IR。
     */
    private void visitSemantic() {
        // 取本条 VarDef 的关键信息（取决于 Utype：是否带初始化）
        final Ident ident = (this.Utype == 0) ? this.ident0 : this.ident1;
        final ConstExp cExp = (this.Utype == 0) ? this.constExp0 : this.constExp1;
        final InitVal init = (this.Utype == 0) ? null : this.initVal1;

        final String symbolName = ident.GetTokenValue();

        // 语义遍历：用于常量表达式求值/错误收集
        if (cExp != null) {
            cExp.visit();
        }
        if (init != null) {
            init.visit();
        }

        // 构造符号
        final boolean isArray = (cExp != null);
        final String symType;
        if (this.isStatic) {
            symType = isArray ? "StaticIntArray" : "StaticInt";
        } else {
            symType = isArray ? "IntArray" : "Int";
        }

        ValueSymbol vSym = new ValueSymbol(symbolName, symType);
        if (isArray) {
            vSym.SetArrayLength(cExp.GetValue());
        }

        // 只有“全局变量/全局数组/静态局部变量”的初始化才要求编译期可求值；
        // 这里把它们的 initValueList 记录进 symbol，供第二遍 IR 直接使用。
        if (init != null && (this.isStatic || SymbolManager.IsGlobal())) {
            ArrayList<Integer> initList = init.GetInitValueList();
            vSym.SetValueList(initList);
        }

        this.symbol = vSym;
        SymbolManager.AddSymbol(this.symbol, ident.GetTokenLineNumber());
    }

    /**
     * 第二遍：只生成 IR。不要再 AddSymbol/不要再 Create 符号表。
     * 这里假设第一遍已经保证语义正确。
     */
    private void visitIR() {
        final Ident ident = (this.Utype == 0) ? this.ident0 : this.ident1;
        final ConstExp cExp = (this.Utype == 0) ? this.constExp0 : this.constExp1;
        final InitVal init = (this.Utype == 0) ? null : this.initVal1;

        final String symbolName = ident.GetTokenValue();

        // 复用第一遍创建的 symbol（同一棵 AST）
        ValueSymbol vSym = this.symbol;
        if (vSym == null) {
            midend.Symbol.Symbol s = SymbolManager.GetSymbol(symbolName);
            if (s instanceof ValueSymbol) vSym = (ValueSymbol) s;
        }
        if (vSym == null) return;

        final boolean isArray = (cExp != null) || (vSym.GetSymbolType() != null && vSym.GetSymbolType().endsWith("Array"));

        // =========================
        // 数组变量 / static 数组变量
        // =========================
        if (isArray) {
            int len = vSym.GetArrayLength();
            if (len < 0 && cExp != null) {
                // 兜底：如果 arrayLength 没写入（理论上第一遍已写入）
                len = cExp.GetValue();
                vSym.SetArrayLength(len);
            }

            // static 局部数组：隐藏全局变量
            if (this.isStatic) {
                IrModule module = IrFactory.getModule();
                String irName = "@__static_" + symbolName + "." + staticIdCounter++;
                vSym.SetIrName(irName);

                ArrayList<Integer> initList = vSym.GetValueList();
                if (initList == null) initList = new ArrayList<>();

                StringBuilder elems = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    if (i > 0) elems.append(", ");
                    int v = (i < initList.size()) ? initList.get(i) : 0;
                    elems.append("i32 ").append(v);
                }
                module.addGlobalDef(irName + " = global [" + len + " x i32] [" + elems + "]");
                return;
            }

            // 顶层全局数组：@a = global [N x i32] [...]
            if (vSym.IsGlobal() || SymbolManager.IsGlobal()) {
                IrModule module = IrFactory.getModule();
                String irName = "@" + symbolName;
                vSym.SetIrName(irName);

                ArrayList<Integer> initList = vSym.GetValueList();
                if (initList == null) initList = new ArrayList<>();

                StringBuilder elems = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    if (i > 0) elems.append(", ");
                    int v = (i < initList.size()) ? initList.get(i) : 0;
                    elems.append("i32 ").append(v);
                }
                module.addGlobalDef(irName + " = global [" + len + " x i32] [" + elems + "]");
                return;
            }

            // 普通局部数组：alloca + (可选) 初始化 store
            IrBasicBlock block = IrBuilder.getCurrentBlock();
            if (block != null) {
                String addr = IrFactory.getInstance().newTemp();
                vSym.SetIrName(addr);
                block.addInstruction(addr + " = alloca [" + len + " x i32]");

                if (init != null) {
                    // InitVal 里一般能拿到按顺序的 Exp 列表
                    java.util.List<frontend.ast.exp.Exp> exps = init.getExpList();
                    for (int i = 0; i < exps.size() && i < len; i++) {
                        String gep = IrFactory.getInstance().newTemp();
                        block.addInstruction(
                                gep + " = getelementptr [" + len + " x i32], [" + len + " x i32]* " + addr +
                                        ", i32 0, i32 " + i
                        );
                        String val = exps.get(i).generateIr(block);
                        block.addInstruction("store i32 " + val + ", i32* " + gep);
                    }
                }
            }
            return;
        }

        // =========================
        // 标量变量 / static 标量变量
        // =========================

        // static 局部标量：隐藏全局变量
        if (this.isStatic) {
            IrModule module = IrFactory.getModule();
            String irName = "@__static_" + symbolName + "." + staticIdCounter++;
            vSym.SetIrName(irName);

            int initVal = 0;
            ArrayList<Integer> initList = vSym.GetValueList();
            if (initList != null && !initList.isEmpty()) {
                initVal = initList.get(0);
            }
            module.addGlobalDef(irName + " = global i32 " + initVal);
            return;
        }

        // 全局标量：@g = global i32 init
        if (vSym.IsGlobal() || SymbolManager.IsGlobal()) {
            IrModule module = IrFactory.getModule();
            String irName = "@" + symbolName;
            vSym.SetIrName(irName);

            int initVal = 0;
            ArrayList<Integer> initList = vSym.GetValueList();
            if (initList != null && !initList.isEmpty()) {
                initVal = initList.get(0);
            }
            module.addGlobalDef(irName + " = global i32 " + initVal);
            return;
        }

        // 普通局部标量：alloca + (可选) store 初始化
        IrBasicBlock block = IrBuilder.getCurrentBlock();
        if (block != null) {
            String addr = IrFactory.getInstance().newTemp();
            vSym.SetIrName(addr);
            block.addInstruction(addr + " = alloca i32");

            if (init != null) {
                String value = init.generateScalarIr(block);
                block.addInstruction("store i32 " + value + ", i32* " + addr);
            }
        }
    }


    public VarDef(){
        super(SyntaxType.VAR_DEF);
    }
}
