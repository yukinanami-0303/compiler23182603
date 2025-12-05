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
    public void visit(){
        String symbolName=ident.GetTokenValue();
        if(this.funcType.GetFuncType().equals("int")){//int类型函数
            this.symbol=new FuncSymbol(symbolName,"IntFunc");
            SymbolManager.AddSymbol(this.symbol, this.ident.GetTokenLineNumber());
            //因为函数形参表的作用域是函数名作用域的子作用域

            String funcName = ident.GetTokenValue();          // 函数名
            // IR：进入一个新函数（createFunction + entry 基本块）
            IrBuilder.enterFunction("i32", funcName);

            SymbolManager.CreateSonSymbolTable();//所以创建子符号表并进入子符号表
            if(this.funcFParams!=null){//有参数
                this.funcFParams.visit();
                this.symbol.SetFormalParamList(this.funcFParams.GetFormalParamList());
            }



            SymbolManager.EnterFunc("int");



            block.visit();


            SymbolManager.LeaveFunc();

            if(!this.block.haveReturnStmt()){//检查return的缺失
                addError(this.block.GetRbraceLineNumber(),"g");
            }
            SymbolManager.GoToFatherSymbolTable();
            // IR：离开当前函数
            IrBuilder.leaveFunction();
        }


        else{//void类型函数
            this.symbol=new FuncSymbol(symbolName,"VoidFunc");
            SymbolManager.AddSymbol(this.symbol, this.ident.GetTokenLineNumber());


            String funcName = ident.GetTokenValue();          // 函数名
            // IR：进入一个新函数（createFunction + entry 基本块）
            IrBuilder.enterFunction("void", funcName);


            //因为函数形参表的作用域是函数名作用域的子作用域
            SymbolManager.CreateSonSymbolTable();//所以创建子符号表并进入子符号表
            if(this.funcFParams!=null){//有参数
                this.funcFParams.visit();
                this.symbol.SetFormalParamList(this.funcFParams.GetFormalParamList());
            }
            SymbolManager.EnterFunc("void");



            block.visit();




            SymbolManager.LeaveFunc();
            SymbolManager.GoToFatherSymbolTable();
            // 如果最后一个语句不是 return，就补一条 ret void
            if (!this.block.haveReturnStmt()) {
                IrBasicBlock cur = IrBuilder.getCurrentBlock();
                if (cur != null) {
                    cur.addInstruction("ret void");
                }
            }
            // IR：离开当前函数
            IrBuilder.leaveFunction();
        }
    }

    public FuncDef(){
        super(SyntaxType.FUNC_DEF);
    }
}