package frontend.ast.exp;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.func.FuncFParams;
import frontend.ast.func.FuncRParams;
import frontend.ast.token.Ident;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrFactory;
import midend.Symbol.FuncSymbol;
import midend.Symbol.Symbol;
import midend.Symbol.SymbolManager;
import midend.Symbol.SymbolTable;

import java.io.IOException;
import java.util.ArrayList;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;
import static midend.Symbol.SymbolManager.GetSymbolTable;


public class UnaryExp extends Node {
    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp

    private int Utype;
    private PrimaryExp primaryExp0=null;
    //-----------------------------------------
    private Ident ident1=null;
    private Token lparentToken1=null;
    private FuncRParams funcRParams1=null;
    private Token rparentToken1=null;
    //----------------------------------------------------
    private UnaryOp unaryOp2=null;
    private UnaryExp unaryExp2=null;
    public UnaryExp(PrimaryExp primaryExp) {
        super(SyntaxType.UNARY_EXP);
        this.primaryExp0=primaryExp;
        this.Utype=0;
    }
    //-----------------------------------------------------------
    public UnaryExp(Ident ident,
                    Token lparentToken,
                    FuncRParams funcRParams,
                    Token rparentToken) {
        super(SyntaxType.UNARY_EXP);
        this.ident1=ident;
        this.lparentToken1=lparentToken;
        this.funcRParams1=funcRParams;
        this.rparentToken1=rparentToken;
        this.Utype=1;
    }
    //-------------------------------------------------------------
    public UnaryExp(UnaryOp unaryOp,
                    UnaryExp unaryExp) {
        super(SyntaxType.UNARY_EXP);
        this.unaryOp2=unaryOp;
        this.unaryExp2=unaryExp;
        this.Utype=2;
    }
    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    @Override
    public void formatOutput() throws IOException {
        if(primaryExp0!=null){
            primaryExp0.formatOutput();
        }
        else if(ident1!=null){
            ident1.formatOutput();
            lparentToken1.formatOutput();
            if(funcRParams1!=null){
                funcRParams1.formatOutput();
            }
            if(rparentToken1!=null) {//如果发生错误j则没有右小括号
                rparentToken1.formatOutput();
            }
        }
        else {
            unaryOp2.formatOutput();
            unaryExp2.formatOutput();
        }
        outputSelf();
    }

    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    //可能的错误：缺少右小括号’)’   j  报错行号为右小括号前一个非终结符所在行号。
    @Override
    public void parse(){
        //Ident '(' [FuncRParams] ')'函数调用
        if(Peek(0).getType().equals("IDENFR")&&
        Peek(1).getType().equals("LPARENT")){
            //Ident
            Ident ident =new Ident();
            ident.parse();
            this.ident1=ident;
            //'('
            this.lparentToken1=Peek(0);
            nextToken();
            //FuncFRarams
            if(Peek(0).getType().equals("INTCON")||
                    Peek(0).getType().equals("IDENFR")||
                    Peek(0).getType().equals("PLUS")||
                    Peek(0).getType().equals("MINU")||
                    Peek(0).getType().equals("NOT")||
                    Peek(0).getType().equals("LPARENT")){
                FuncRParams funcRParams=new FuncRParams();
                funcRParams.parse();
                this.funcRParams1=funcRParams;
            }
            //')'并处理错误
            if(Peek(0).getType().equals("RPARENT")) {
                this.rparentToken1 = Peek(0);
                nextToken();
            }else{//缺失右小括号，报错为j
                this.rparentToken1 = new Token("RPARENT",")",this.lparentToken1.getLineNumber());
                addError(GetBeforeLineNumber(), "j");
            }
        }
        // UnaryOp UnaryExp带符号的表达式（右递归）
        else if(Peek(0).getType().equals("PLUS")||
                Peek(0).getType().equals("MINU")||
                Peek(0).getType().equals("NOT")){
            UnaryOp unaryOp=new UnaryOp();
            unaryOp.parse();
            this.unaryOp2=unaryOp;
            UnaryExp unaryExp=new UnaryExp();
            unaryExp.parse();
            this.unaryExp2=unaryExp;
        }
        //PrimaryExp
        else {
            PrimaryExp primaryExp=new PrimaryExp();
            primaryExp.parse();
            this.primaryExp0=primaryExp;
        }
    }


    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    @Override
    public void visit(){
        if(primaryExp0!=null){
            this.primaryExp0.visit();
        }
        else if(unaryOp2!=null){
            this.unaryOp2.visit();
            this.unaryExp2.visit();
        }
        else{//产生式含有Ident
            String identName = ident1.GetTokenValue();
            int line = ident1.GetTokenLineNumber();
            Symbol symbol = SymbolManager.GetSymbol(identName);
            if(this.ident1.GetTokenValue().equals("getint")){//是getint
                if(this.funcRParams1!=null){
                    addError(line, "d");//给无参函数传参
                }
            }
            else if(symbol==null){//未定义，c
                addError(line, "c");
            }
            if(symbol instanceof FuncSymbol funcSymbol){// 如果是函数，检查参数
                //无实际参数情况
                if(funcRParams1==null){
                    if (!funcSymbol.GetFormalParamList().isEmpty()) {
                        addError(line, "d");//不给有参函数传参
                    }
                }
                //有实际参数情况
                else {
                    ArrayList<Symbol> formalParamList = funcSymbol.GetFormalParamList();//形参表
                    int formalParamCount = funcSymbol.GetFormalParamList().size();//函数形参个数
                    //无参函数
                    if (formalParamCount==0) {
                        addError(line, "d");//给无参函数传参
                    }
                    //有参函数检查传入参数匹配问题
                    else {
                        ArrayList<Exp> realParamList = this.funcRParams1.GetRealParamList();//实参表
                        int realParamCount = this.funcRParams1.GetRealParamList().size();//传入实参个数
                        if (realParamCount != formalParamCount) {
                            addError(line, "d");//传入参数个数不匹配
                            return;
                        }
                        //检查传入参数类型匹配问题
                        for (int i = 0; i < formalParamCount; i++) {
                            Symbol formalSymbol = formalParamList.get(i);//形参
                            String formalType = formalSymbol.GetSymbolType();//形参类型
                            String realParamName = realParamList.get(i).GetFirstToken().getValue();
                            Symbol realSymbol = SymbolManager.GetSymbol(realParamName);
                            if(formalType.equals("IntArray") ){//检查实参是否是变量（错误：给数组传递变量）
                                if (realSymbol == null) {//符号表里找不到肯定不是数组
                                    if(!realParamList.get(i).GetFirstToken().getType().equals("IDENFR")) {//排除有c类错误的情况
                                        addError(line, "e");//传递变量给数组
                                        return;
                                    }
                                }
                                if (!realSymbol.GetSymbolType().equals("IntArray") ) {//符号表里找到了但不是数组
                                    addError(line, "e");//传递变量给数组
                                }

                                else{//找到了是数组,要排除是a[1]的情况这也是变量
                                    if(realParamList.get(i).GetSecondToken().getValue().equals("[")){
                                        addError(line, "e");//传递变量给数组
                                    }
                                }

                            }
                            else if(formalType.equals("Int")){//检查实参是否是数组（错误：给变量传递数组）
                                if (realSymbol != null) {//符号表里能找到的才是数组
                                    if (realSymbol.GetSymbolType().equals("IntArray")||
                                            realSymbol.GetSymbolType().equals("ConstIntArray")||
                                            realSymbol.GetSymbolType().equals("StaticIntArray")) {
                                        if(!realParamList.get(i).GetSecondToken().getValue().equals("[")) {//排除是a[i]的可能性
                                            addError(line, "e");//传递数组给变量
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public String generateIr(IrBasicBlock curBlock) {
        // 1) UnaryExp → PrimaryExp
        if (primaryExp0 != null) {
            return primaryExp0.generateIr(curBlock);
        }

// 2) UnaryExp → Ident '(' [FuncRParams] ')'
        if (ident1 != null) {
            String funcName = ident1.GetTokenValue();

            // 特殊内建：getint() —— 无参、返回 i32，且不会走下面通用逻辑
            if ("getint".equals(funcName)) {
                String res = IrFactory.getInstance().newTemp();
                curBlock.addInstruction(res + " = call i32 @getint()");
                return res;
            }

            IrFactory factory = IrFactory.getInstance();
            String retType;

            // 内建库函数的返回类型在这里强制指定
            if ("putint".equals(funcName) || "putch".equals(funcName) || "putstr".equals(funcName)) {
                retType = "void";
            } else {
                // 其它（用户自定义）函数，从工厂里查；查不到就默认 i32
                retType = factory.getFuncRetType(funcName);  // "i32" 或 "void"
            }

            // 生成实参表达式
            java.util.List<String> argVals = new java.util.ArrayList<>();
            if (funcRParams1 != null) {
                argVals = funcRParams1.generateArgsIr(curBlock);
            }

            // 把实参拼成 "i32 v1, i32 v2, ..."
            StringBuilder argsSb = new StringBuilder();
            for (int i = 0; i < argVals.size(); i++) {
                if (i > 0) {
                    argsSb.append(", ");
                }
                argsSb.append("i32 ").append(argVals.get(i));
            }
            String argsStr = argsSb.toString();

            // 按返回类型生成不同的 call
            if ("void".equals(retType)) {
                curBlock.addInstruction("call void @" + funcName + "(" + argsStr + ")");
                // 作为表达式时，无值可用，返回一个占位立即数 "0"
                return "0";
            } else {
                String res = factory.newTemp();
                curBlock.addInstruction(res + " = call i32 @" + funcName + "(" + argsStr + ")");
                return res;
            }
        }


        // 3) UnaryExp → UnaryOp UnaryExp（一元 + - !）
        if (unaryOp2 != null && unaryExp2 != null) {
            String v = unaryExp2.generateIr(curBlock);
            String op = unaryOp2.GetUnaryOp();  // "+", "-", "!"
            if (op.equals("+")) {
                return v;
            } else if (op.equals("-")) {
                String res = IrFactory.getInstance().newTemp();
                curBlock.addInstruction(res + " = sub i32 0, " + v);
                return res;
            } else { // "!"
                String tmp = IrFactory.getInstance().newTemp();
                String res = IrFactory.getInstance().newTemp();
                curBlock.addInstruction(tmp + " = icmp eq i32 " + v + ", 0");
                curBlock.addInstruction(res + " = zext i1 " + tmp + " to i32");
                return res;
            }
        }

        // 理论上不会走到这里
        return "0";
    }





    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
// 这里只处理 PrimaryExp 和 UnaryOp UnaryExp，函数调用不允许出现在 ConstExp 中
    public int GetValue() {
        // UnaryExp → PrimaryExp
        if (primaryExp0 != null) {
            return primaryExp0.GetValue();
        }

        // UnaryExp → UnaryOp UnaryExp
        if (unaryOp2 != null && unaryExp2 != null) {
            int v = unaryExp2.GetValue();
            String op = unaryOp2.GetUnaryOp();  // "+", "-", "!"
            if (op.equals("+")) {
                return v;
            } else if (op.equals("-")) {
                return -v;
            } else { // "!"
                return (v == 0) ? 1 : 0;
            }
        }

        // Ident '(' ... ')' 作为常量是非法的，这里防御性返回 0
        return 0;
    }

    public UnaryExp(){
        super(SyntaxType.UNARY_EXP);
    }
}