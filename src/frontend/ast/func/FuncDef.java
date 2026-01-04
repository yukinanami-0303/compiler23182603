package frontend.ast.func;
import frontend.Token;
import frontend.Parser;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.FuncType;
import frontend.ast.token.Ident;
import frontend.ast.block.Block;
import frontend.ast.token.FuncType;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrBuilder;
import midend.Symbol.FuncSymbol;
import midend.Symbol.SymbolManager;

import java.io.IOException;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class FuncDef extends Node {
    //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    private FuncType funcType;
    private FuncSymbol symbol;
    private Ident ident;
    private Token lparentToken;
    private FuncFParams funcFParams;
    private Token rparentToken;
    private Block block;
    public FuncDef(FuncType funcType,
                   Ident ident,
                   Token lparentToken,
                   FuncFParams funcFParams,
                   Token rparentToken,
                   Block block) {
        super(SyntaxType.FUNC_DEF);
        this.funcType = funcType;
        this.ident = ident;
        this.lparentToken = lparentToken;
        this.funcFParams = funcFParams;
        this.rparentToken = rparentToken;
        this.block = block;

    }
    @Override
    public void formatOutput() throws IOException {
        funcType.formatOutput();
        ident.formatOutput();
        lparentToken.formatOutput();
        if(funcFParams!=null){
            funcFParams.formatOutput();
        }
        if(rparentToken!=null) {//如果发生错误j则没有右小括号
            rparentToken.formatOutput();
        }
        block.formatOutput();
        outputSelf();
    }

    //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    //可能的错误：缺少右小括号’)’ j  报错行号为右小括号前一个非终结符所在行号。
    @Override
    public void parse(){
        FuncType funcType=new FuncType();
        funcType.parse();
        this.funcType=funcType;

        Ident ident =new Ident();
        ident.parse();
        this.ident=ident;

        //'('
        this.lparentToken=Peek(0);
        nextToken();
        //FuncFParams
        if(Peek(0).getType().equals("INTTK")){
            FuncFParams funcFParams=new FuncFParams();
            funcFParams.parse();
            this.funcFParams=funcFParams;
        }
        //')'错误检测
        if(Peek(0).getType().equals("RPARENT")) {
            this.rparentToken = Peek(0);
            nextToken();
        }else{//缺失右小括号，报错为j
            this.rparentToken = new Token("RPARENT",")",this.lparentToken.getLineNumber());
            addError(GetBeforeLineNumber(), "j");
        }
        //Block
        Block block=new Block();
        block.parse();
        this.block=block;
    }


    //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block  b,g

    @Override
    public void visit() {
        if (midend.MidEnd.isSemantic()) {
            visitSemantic();
        } else {
            visitIR();
        }
    }

    private void visitSemantic() {
        String symbolName = ident.GetTokenValue();

        if (this.funcType.GetFuncType().equals("int")) {
            // ===== 语义：加入符号表 =====
            this.symbol = new FuncSymbol(symbolName, "IntFunc");
            SymbolManager.AddSymbol(this.symbol, this.ident.GetTokenLineNumber());

            // ===== 语义：函数形参作用域是函数名作用域的子作用域 =====
            SymbolManager.CreateSonSymbolTable();

            if (this.funcFParams != null) {
                this.funcFParams.visit();
                this.symbol.SetFormalParamList(this.funcFParams.GetFormalParamList());
            }

            SymbolManager.EnterFunc("int");

            block.visit();

            SymbolManager.LeaveFunc();

            // 缺失 return（g）
            if (!this.block.haveReturnStmt()) {
                addError(this.block.GetRbraceLineNumber(), "g");
            }

            SymbolManager.GoToFatherSymbolTable();
        } else {
            // ===== void 函数 =====
            this.symbol = new FuncSymbol(symbolName, "VoidFunc");
            SymbolManager.AddSymbol(this.symbol, this.ident.GetTokenLineNumber());

            SymbolManager.CreateSonSymbolTable();

            if (this.funcFParams != null) {
                this.funcFParams.visit();
                this.symbol.SetFormalParamList(this.funcFParams.GetFormalParamList());
            }

            SymbolManager.EnterFunc("void");

            block.visit();

            SymbolManager.LeaveFunc();
            SymbolManager.GoToFatherSymbolTable();
        }
    }

    private void visitIR() {
        String funcName = ident.GetTokenValue();

        // 第二遍：不再 AddSymbol，不再 CreateSonSymbolTable，只复用第一遍的符号表树
        // 进入函数参数/局部作用域（第一遍已经 Create 过）
        SymbolManager.GoToSonSymbolTable();

        if (this.funcType.GetFuncType().equals("int")) {
            // ===== IR：进入新函数 =====
            IrBuilder.enterFunction("i32", funcName);

            // 为了兼容你项目里其他节点可能会读 GetFuncType，这里仍设置一下
            SymbolManager.EnterFunc("int");

            if (this.funcFParams != null) {
                this.funcFParams.visit(); // IR 阶段：FuncFParam.visit 会只生成参数 IR
            }

            block.visit();

            SymbolManager.LeaveFunc();

            // 正确程序一般不会缺 return；这里兜底，保证 LLVM 结构完整
            if (!this.block.haveReturnStmt()) {
                IrBasicBlock cur = IrBuilder.getCurrentBlock();
                if (cur != null) {
                    cur.addInstruction("ret i32 0");
                }
            }

            // 退出函数作用域
            SymbolManager.GoToFatherSymbolTable();

            // ===== IR：离开函数 =====
            IrBuilder.leaveFunction();
        } else {
            // ===== void 函数 =====
            IrBuilder.enterFunction("void", funcName);

            SymbolManager.EnterFunc("void");

            if (this.funcFParams != null) {
                this.funcFParams.visit();
            }

            block.visit();

            SymbolManager.LeaveFunc();

            // 如果最后没 return，补 ret void（void 函数允许省略 return）
            if (!this.block.haveReturnStmt()) {
                IrBasicBlock cur = IrBuilder.getCurrentBlock();
                if (cur != null) {
                    cur.addInstruction("ret void");
                }
            }

            SymbolManager.GoToFatherSymbolTable();
            IrBuilder.leaveFunction();
        }
    }


    public FuncDef(){
        super(SyntaxType.FUNC_DEF);
    }
}