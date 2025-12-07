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
                if(constExp0!=null){//是数组
                    this.constExp0.visit();
                    this.symbol=new ValueSymbol(symbolName,"StaticIntArray");

                    if (this.symbol instanceof ValueSymbol) {
                        ValueSymbol vSym = (ValueSymbol) this.symbol;
                        String type = vSym.GetSymbolType();  // "IntArray", "StaticIntArray", ...

                        // 只处理一维 int 数组
                        if (type.endsWith("Array")) {
                            String varName = vSym.GetSymbolName();
                            int len = 0;
                            // 根据你当前所在分支选择合适的 ConstExp 获取维度：
                            // - 若在 constExp0 分支：len = constExp0.GetValue();
                            // - 若在 constExp1 分支：len = constExp1.GetValue();
                            len = constExp0.GetValue();
                            vSym.SetArrayLength(len);
                            // === 1) static 局部数组：隐藏全局变量 @__static_name ===
                            IrModule module = IrFactory.getModule();
                            String irName = "@__static_" + varName;
                            vSym.SetIrName(irName);
                            ArrayList<Integer> initList = new ArrayList<>();
                            if (initVal1 != null) {               // 带初始化
                                initList = initVal1.GetInitValueList(); // 题设保证 static 初始值编译期可求
                            }
                            StringBuilder elems = new StringBuilder();
                            for (int i = 0; i < len; i++) {
                                if (i > 0) elems.append(", ");
                                int v = (i < initList.size()) ? initList.get(i) : 0;
                                elems.append("i32 ").append(v);
                            }
                            module.addGlobalDef(irName + " = global [" + len + " x i32] [" + elems + "]");
                        }
                    }


                }
                else{//不是数组
                    this.symbol=new ValueSymbol(symbolName,"StaticInt");
                }
                SymbolManager.AddSymbol(this.symbol, ident0.GetTokenLineNumber());
                // ===== IR：static 标量，无初值 → 0 =====
                if (this.symbol.GetSymbolType().equals("StaticInt")) {
                    IrModule module = IrFactory.getModule();
                    String irName = "@__static_" + symbolName;

                    int init = 0; // 规则 3：未赋值默认 0
                    module.addGlobalDef(irName + " = global i32 " + init);
                }
            }
            else{//赋值
                String symbolName=ident1.GetTokenValue();
                if(constExp1!=null){//是数组
                    this.constExp1.visit();
                    this.initVal1.visit();
                    this.symbol=new ValueSymbol(symbolName,"StaticIntArray");
                    if (this.symbol instanceof ValueSymbol) {
                        ValueSymbol vSym = (ValueSymbol) this.symbol;
                        String type = vSym.GetSymbolType();  // "IntArray", "StaticIntArray", ...
                        // 只处理一维 int 数组
                        if (type.endsWith("Array")) {
                            String varName = vSym.GetSymbolName();
                            int len = 0;
                            len = constExp1.GetValue();
                            vSym.SetArrayLength(len);
                            // === 1) static 局部数组：隐藏全局变量 @__static_name ===
                            IrModule module = IrFactory.getModule();
                            String irName = "@__static_" + varName;
                            vSym.SetIrName(irName);
                            ArrayList<Integer> initList = new ArrayList<>();
                            if (initVal1 != null) {               // 带初始化
                                initList = initVal1.GetInitValueList(); // 题设保证 static 初始值编译期可求
                            }
                            StringBuilder elems = new StringBuilder();
                            for (int i = 0; i < len; i++) {
                                if (i > 0) elems.append(", ");
                                int v = (i < initList.size()) ? initList.get(i) : 0;
                                elems.append("i32 ").append(v);
                            }
                            module.addGlobalDef(irName + " = global [" + len + " x i32] [" + elems + "]");
                        }
                    }
                }
                else{//不是数组
                    this.initVal1.visit();
                    this.symbol=new ValueSymbol(symbolName,"StaticInt");//变量，相当于数组数量为0
                }
                SymbolManager.AddSymbol(this.symbol, ident1.GetTokenLineNumber());
                // ===== IR：static 标量，有初值 =====
                if (this.symbol.GetSymbolType().equals("StaticInt")) {
                    IrModule module = IrFactory.getModule();
                    String irName = "@__static_" + symbolName;

                    int init = 0;
                    // 初值必须是编译期常量，直接用 InitVal.GetInitValueList()
                    if (this.initVal1 != null) {
                        ArrayList<Integer> initList = this.initVal1.GetInitValueList();
                        if (initList != null && !initList.isEmpty()) {
                            init = initList.get(0);
                        }
                    }
                    module.addGlobalDef(irName + " = global i32 " + init);
                }
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

                    if (this.symbol instanceof ValueSymbol) {
                        ValueSymbol vSym = (ValueSymbol) this.symbol;
                        String type = vSym.GetSymbolType();  // "IntArray", "StaticIntArray", ...

                        // 只处理一维 int 数组
                        if (type.endsWith("Array")) {
                            String varName = vSym.GetSymbolName();
                            int len = constExp0.GetValue();
                            vSym.SetArrayLength(len);

                            boolean isGlobalArray = vSym.IsGlobal() || SymbolManager.IsGlobal();

                            // === 顶层普通数组：全局 [N x i32] ===
                            if (isGlobalArray) {
                                IrModule module = IrFactory.getModule();
                                String irName = "@" + varName;
                                vSym.SetIrName(irName);

                                ArrayList<Integer> initList = new ArrayList<>();
                                if (initVal1 != null) {              // 有初始化
                                    initList = initVal1.GetInitValueList(); // 题设保证全局初始值是常量
                                }

                                StringBuilder elems = new StringBuilder();
                                for (int i = 0; i < len; i++) {
                                    if (i > 0) elems.append(", ");
                                    int v = (i < initList.size()) ? initList.get(i) : 0;
                                    elems.append("i32 ").append(v);
                                }
                                module.addGlobalDef(irName + " = global [" + len + " x i32] [" + elems + "]");
                            }
                            // === 函数内普通局部数组：alloca [N x i32]，初始化用 store ===
                            else {
                                IrBasicBlock block = IrBuilder.getCurrentBlock();
                                if (block != null) {
                                    String addr = IrFactory.getInstance().newTemp();
                                    vSym.SetIrName(addr);
                                    block.addInstruction(addr + " = alloca [" + len + " x i32]");

                                    // 局部数组的初始化：InitVal → '{' [Exp {',' Exp}] '}'
                                    if (initVal1 != null) {
                                        // 只处理一维数组初始化，按顺序填到 a[0..k-1]
                                        java.util.List<frontend.ast.exp.Exp> exps = initVal1.getExpList(); // 下面给 getExpList 的实现
                                        for (int i = 0; i < exps.size() && i < len; i++) {
                                            String idxVal = Integer.toString(i); // 编译期常量下标
                                            String gep = IrFactory.getInstance().newTemp();
                                            block.addInstruction(
                                                    gep + " = getelementptr [" + len + " x i32], [" + len + " x i32]* " + addr +
                                                            ", i32 0, i32 " + idxVal
                                            );
                                            String val = exps.get(i).generateIr(block);
                                            block.addInstruction("store i32 " + val + ", i32* " + gep);
                                        }
                                    }
                                }
                            }
                        }
                    }


                }
                else{//不是数组
                    this.symbol=new ValueSymbol(symbolName,"Int");
                }
                SymbolManager.AddSymbol(this.symbol, ident0.GetTokenLineNumber());
                // ===== 非 static、未赋值的普通 int 变量的 IR =====
                if (this.symbol instanceof ValueSymbol) {
                    ValueSymbol vSym = (ValueSymbol) this.symbol;
                    boolean isGlobal = vSym.IsGlobal();    // 由符号表深度决定
                    String varName = vSym.GetSymbolName();

                    // constExp0 == null 时就是标量 int
                    if (constExp0 == null) {
                        if (isGlobal) {
                            // 顶层的 "int g;" → @g = global i32 0
                            IrModule module = IrFactory.getModule();
                            String irName = "@" + varName;
                            module.addGlobalDef(irName + " = global i32 0");
                            vSym.SetIrName(irName);
                        } else {
                            // 函数内的 "int c;" → alloca i32
                            IrBasicBlock block = IrBuilder.getCurrentBlock();
                            if (block != null) {
                                String addr = IrFactory.getInstance().newTemp();  // 不再用 "%varName"
                                vSym.SetIrName(addr);
                                block.addInstruction(addr + " = alloca i32");
                            }
                        }
                    }
                }
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

                    if (this.symbol instanceof ValueSymbol) {
                        ValueSymbol vSym = (ValueSymbol) this.symbol;
                        String type = vSym.GetSymbolType();  // "IntArray", "StaticIntArray", ...

                        // 只处理一维 int 数组
                        if (type.endsWith("Array")) {
                            String varName = vSym.GetSymbolName();
                            int len = constExp1.GetValue();
                            vSym.SetArrayLength(len);

                            boolean isGlobalArray = vSym.IsGlobal() || SymbolManager.IsGlobal();

                            // === 顶层普通数组：全局 [N x i32] ===
                            if (isGlobalArray) {
                                IrModule module = IrFactory.getModule();
                                String irName = "@" + varName;
                                vSym.SetIrName(irName);
                                ArrayList<Integer> initList = new ArrayList<>();
                                if (initVal1 != null) {
                                    initList = initVal1.GetInitValueList();
                                }

                                StringBuilder elems = new StringBuilder();
                                for (int i = 0; i < len; i++) {
                                    if (i > 0) elems.append(", ");
                                    int v = (i < initList.size()) ? initList.get(i) : 0;
                                    elems.append("i32 ").append(v);
                                }
                                module.addGlobalDef(irName + " = global [" + len + " x i32] [" + elems + "]");
                            }
                            // === 函数内普通局部数组：alloca [N x i32]，初始化用 store ===
                            else {
                                IrBasicBlock block = IrBuilder.getCurrentBlock();
                                if (block != null) {
                                    String addr = IrFactory.getInstance().newTemp();
                                    vSym.SetIrName(addr);
                                    block.addInstruction(addr + " = alloca [" + len + " x i32]");

                                    // 局部数组的初始化：InitVal → '{' [Exp {',' Exp}] '}'
                                    if (initVal1 != null) {
                                        // 只处理一维数组初始化，按顺序填到 a[0..k-1]
                                        java.util.List<frontend.ast.exp.Exp> exps = initVal1.getExpList(); // 下面给 getExpList 的实现
                                        for (int i = 0; i < exps.size() && i < len; i++) {
                                            String idxVal = Integer.toString(i); // 编译期常量下标
                                            String gep = IrFactory.getInstance().newTemp();
                                            block.addInstruction(
                                                    gep + " = getelementptr [" + len + " x i32], [" + len + " x i32]* " + addr +
                                                            ", i32 0, i32 " + idxVal
                                            );
                                            String val = exps.get(i).generateIr(block);
                                            block.addInstruction("store i32 " + val + ", i32* " + gep);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                else{//不是数组
                    this.initVal1.visit();
                    //initValueList=this.initVal1.GetInitValueList();
                    this.symbol=new ValueSymbol(symbolName,"Int");//变量，相当于数组数量为0
                    symbol.SetValueList(initValueList);
                }
                SymbolManager.AddSymbol(this.symbol, ident1.GetTokenLineNumber());
                // ===== IR：变量声明 =====
                if (this.symbol instanceof ValueSymbol) {
                    ValueSymbol vSym = (ValueSymbol) this.symbol;
                    if (vSym.GetSymbolType().endsWith("Array")) {
                        return;
                    }
                    // static 修饰的变量：只初始化一次 → 视作“全局存储”（只影响 IR）
                    if (this.isStatic) {
                        vSym.SetIsGlobal(true);
                    }

                    boolean isGlobal = vSym.IsGlobal();
                    String varName = vSym.GetSymbolName();

                    if (isGlobal) {
                        // 全局 / static 变量：生成
                        int init = 0;
                        if (this.initVal1 != null) {
                            initValueList = this.initVal1.GetInitValueList();
                        }
                        if (initValueList != null && !initValueList.isEmpty()) {
                            init = initValueList.get(0);
                        }

                        IrModule module = IrFactory.getModule();
                        String irName = "@" + varName;          // 如需区分 static，可以用 "@__static_"+varName
                        module.addGlobalDef(irName + " = global i32 " + init);
                        vSym.SetIrName(irName);
                    }
                    else {
                        // 普通局部变量：alloca + (可选) store 初始化
                        IrBasicBlock block = IrBuilder.getCurrentBlock();
                        if (block != null) {
                            String addr = IrFactory.getInstance().newTemp();
                            vSym.SetIrName(addr);
                            block.addInstruction(addr + " = alloca i32");
                            if (this.initVal1 != null) {
                                String value = this.initVal1.generateScalarIr(block);
                                block.addInstruction("store i32 " + value + ", i32* " + addr);
                            }
                        }
                    }
                }
            }
        }
    }

    public VarDef(){
        super(SyntaxType.VAR_DEF);
    }
}
