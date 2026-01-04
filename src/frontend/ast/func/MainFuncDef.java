package frontend.ast.func;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.block.Block;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrBuilder;
import midend.Symbol.SymbolManager;

import java.io.IOException;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class MainFuncDef extends Node{
    //MainFuncDef → 'int' 'main' '(' ')' Block
    private Token intToken;
    private Token mainToken;
    private Token lparentToken;
    private Token rparentToken;
    private Block block;
    public MainFuncDef(Token intToken,
                       Token mainToken,
                       Token lparentToken,
                       Token rparentToken,
                       Block block) {
        super(SyntaxType.MAIN_FUNC_DEF);
        this.intToken=intToken;
        this.mainToken=mainToken;
        this.lparentToken=lparentToken;
        this.rparentToken=rparentToken;
        this.block=block;
    }
    //MainFuncDef → 'int' 'main' '(' ')' Block
    @Override
    public void formatOutput() throws IOException {
        intToken.formatOutput();
        mainToken.formatOutput();
        lparentToken.formatOutput();
        if(rparentToken!=null) {//如果发生错误j则没有右小括号
            rparentToken.formatOutput();
        }
        block.formatOutput();
        outputSelf();
    }


    //缺少右小括号’)’   j  报错行号为右小括号前一个非终结符所在行号。
    @Override
    public void parse(){

        //MainFuncDef → 'int' 'main' '(' ')' Block
        //'int'
        this.intToken=Peek(0);
        nextToken();
        //'main'
        this.mainToken=Peek(0);
        nextToken();
        //'('
        this.lparentToken=Peek(0);
        nextToken();
        //')'错误检测
        if(Peek(0).getType().equals("RPARENT")) {
            this.rparentToken = Peek(0);
            nextToken();
        }else{//缺失右小括号，报错为j
            this.rparentToken = new Token("RPARENT",")",this.intToken.getLineNumber());
            addError(GetBeforeLineNumber(), "j");
        }
        //Block
        Block block=new Block();
        block.parse();
        this.block=block;
    }


    // MainFuncDef → 'int' 'main' '(' ')' Block
    @Override
    public void visit() {
        if (midend.MidEnd.isSemantic()) {
            visitSemantic();
        } else {
            visitIR();
        }
    }

    private void visitSemantic() {
        // 语义阶段：不生成 IR，只建符号表/检查错误
        SymbolManager.CreateSonSymbolTable(); // main 的 Block 作用域
        SymbolManager.EnterFunc("int");

        block.visit();

        SymbolManager.LeaveFunc();

        if (!this.block.haveReturnStmt()) { // 缺 return（g）
            addError(this.block.GetRbraceLineNumber(), "g");
        }

        SymbolManager.GoToFatherSymbolTable();
    }

    private void visitIR() {
        // IR 阶段：生成 main 函数 IR，并复用第一遍的符号表树
        IrBuilder.enterFunction("i32", "main");

        // 进入第一遍已经创建好的 main-block 子作用域
        SymbolManager.GoToSonSymbolTable();
        SymbolManager.EnterFunc("int");

        block.visit();

        SymbolManager.LeaveFunc();

        // 正确程序应当有 return；兜底补 ret i32 0
        if (!this.block.haveReturnStmt()) {
            IrBasicBlock cur = IrBuilder.getCurrentBlock();
            if (cur != null) {
                cur.addInstruction("ret i32 0");
            }
        }

        SymbolManager.GoToFatherSymbolTable();
        IrBuilder.leaveFunction();
    }


    public MainFuncDef(){
        super(SyntaxType.MAIN_FUNC_DEF);
    }

}
