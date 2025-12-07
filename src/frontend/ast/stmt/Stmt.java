package frontend.ast.stmt;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.block.Block;
import frontend.ast.exp.Cond;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.func.FuncFParam;
import frontend.ast.token.StringConst;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrBuilder;
import midend.Ir.IrFactory;
import midend.Ir.IrFunction;
import midend.Symbol.Symbol;
import midend.Symbol.SymbolManager;

import java.io.IOException;
import java.util.ArrayList;

import static Error.ErrorHandler.*;
import static frontend.TokenStream.*;

public class Stmt extends Node{
    /*
    Stmt → LVal '=' Exp ';'     0
           |    [Exp] ';'       1
           |    Block       2
           |    'if' '(' Cond ')' Stmt [ 'else' Stmt ]      3
           |    'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt       4
           |    'break' ';'     5
           |    'continue' ';'      6
           |    'return' [Exp] ';'      7
           |    'printf''('StringConst {','Exp}')'';'       8

    */
    //Stmt → LVal '=' Exp ';'     0
    private int Utype;
    private LVal lVal0=null;
    private Token assignToken0=null;
    private Exp exp0=null;
    private Token semicnToken0=null;


    //Stmt → LVal '=' Exp ';'     0
    public Stmt(LVal lVal,
                Token assignToken,
                Exp exp,
                Token semicnToken) {
        super(SyntaxType.STMT);
        this.lVal0=lVal;
        this.assignToken0=assignToken;
        this.exp0=exp;
        this.semicnToken0=semicnToken;
        this.Utype=0;
    }



    //Stmt →  [Exp] ';'       1
    private Exp exp1=null;
    private Token semicnToken1=null;
    public Stmt(Exp exp,
                Token semicnToken) {
        super(SyntaxType.STMT);
        this.exp1=exp;
        this.semicnToken1=semicnToken;
        this.Utype=1;
    }


    //Stmt →  Block       2
    private Block block2=null;
    public Stmt(Block block) {
        super(SyntaxType.STMT);
        this.block2=block;
        this.Utype=2;
    }


    //Stmt →  'if' '(' Cond ')' Stmt [ 'else' Stmt ]      3
    private Token ifToken3=null;
    private Token lparentToken3=null;
    private Cond cond3=null;
    private Token rparentToken3=null;
    private Stmt stmt31=null;
    private Token elseToken3=null;
    private Stmt stmt32=null;
    public Stmt(Token ifToken,
                Token lparentToken,
                Cond cond,
                Token rparentToken,
                Stmt stmt1,
                Token elseToken,
                Stmt stmt2) {
        super(SyntaxType.STMT);
        this.ifToken3=ifToken;
        this.lparentToken3=lparentToken;
        this.cond3=cond;
        this.rparentToken3=rparentToken;
        this.stmt31=stmt1;
        this.stmt32=stmt2;
        this.elseToken3=elseToken;
        this.Utype=3;
    }


    //Stmt →    'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt       4
    private Token forToken4=null;
    private Token lparentToken4=null;
    private ForStmt forStmt41=null;
    private Token semicnToken41=null;
    private Cond cond4=null;
    private Token semicnToken42=null;
    private ForStmt forStmt42=null;
    private Token rparentToken4=null;
    private Stmt stmt4=null;
    public Stmt(Token forToken,
                Token lparentToken,
                ForStmt forStmt1,
                Token semicnToken1,
                Cond cond,
                Token semicnToken2,
                ForStmt forStmt2,
                Token rparentToken,
                Stmt stmt) {
        super(SyntaxType.STMT);
        this.forToken4=forToken;
        this.lparentToken4=lparentToken;
        this.forStmt41=forStmt1;
        this.semicnToken41=semicnToken1;
        this.cond4=cond;
        this.semicnToken42=semicnToken2;
        this.forStmt42=forStmt2;
        this.rparentToken4=rparentToken;
        this.stmt4=stmt;
        this.Utype=4;
    }


    //Stmt →    'break' ';'     5
    //Stmt →    'continue' ';'      6
    private Token breakToken5=null;
    private Token semicnToken5=null;
    private Token continueToken6=null;
    private Token semicnToken6=null;
    public Stmt(Token token1,
                Token token2) {
        super(SyntaxType.STMT);
        if(token1.getType().equals("BREAKTK") && token2.getType().equals("SEMICN")) {
            this.breakToken5 = token1;
            this.semicnToken5 = token2;
            this.Utype=5;
        }
        else if(token1.getType().equals("CONTINUETK") && token2.getType().equals("SEMICN")){
            this.continueToken6=token1;
            this.semicnToken6=token2;
            this.Utype=6;
        }
    }


    //Stmt →    'return' [Exp] ';'      7
    private Token returnToken7=null;
    private Exp exp7=null;
    private Token semicnToken7=null;
    public Stmt(Token returnToken,
                Exp exp,
                Token semicnToken) {
        super(SyntaxType.STMT);
        this.returnToken7=returnToken;
        this.exp7=exp;
        this.semicnToken7=semicnToken;
        this.Utype=7;
    }


    //Stmt →    'printf''('StringConst {','Exp}')'';'       8
    private Token printfToken8=null;
    private Token lparentToken8=null;
    private StringConst stringConst8=null;
    private ArrayList<Token> commaTokens8=null;
    private ArrayList<Exp> exps8=null;
    private Token rparentToken8=null;
    private Token semicnToken8=null;
    public Stmt(Token printfToken,
                Token lparentToken,
                StringConst stringConst,
                ArrayList<Token> commaTokens,
                ArrayList<Exp> exps,
                Token rparentToken,
                Token semicnToken
                ) {
        super(SyntaxType.STMT);
        this.printfToken8=printfToken;
        this.lparentToken8=lparentToken;
        this.stringConst8=stringConst;
        this.commaTokens8=commaTokens;
        this.exps8=exps;
        this.rparentToken8=rparentToken;
        this.semicnToken8=semicnToken;
        this.Utype=8;
    }

    @Override
    public void formatOutput() throws IOException {

        //Stmt → LVal '=' Exp ';'     0
        if(Utype==0){
            lVal0.formatOutput();
            assignToken0.formatOutput();
            exp0.formatOutput();
            if(semicnToken0!=null) {//如果发生错误i则没有分号
                semicnToken0.formatOutput();
            }
        }
        //Stmt →  [Exp] ';'       1
        else if(Utype==1){
            if(exp1!=null){
                exp1.formatOutput();
            }
            if(semicnToken1!=null) {//如果发生错误i则没有分号
                semicnToken1.formatOutput();
            }
        }
        //Stmt →  Block       2
        else if(Utype==2){
            block2.formatOutput();
        }
        //Stmt →  'if' '(' Cond ')' Stmt [ 'else' Stmt ]      3
        else if(Utype==3){
            ifToken3.formatOutput();
            lparentToken3.formatOutput();
            cond3.formatOutput();
            if(rparentToken3!=null) {
                rparentToken3.formatOutput();
            }
            stmt31.formatOutput();
            if(elseToken3!=null){
                elseToken3.formatOutput();
                stmt32.formatOutput();
            }
        }
        //Stmt →    'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt       4
        else if(Utype==4){
            forToken4.formatOutput();
            lparentToken4.formatOutput();
            if(forStmt41!=null){
                forStmt41.formatOutput();
            }
            semicnToken41.formatOutput();
            if(cond4!=null){
                cond4.formatOutput();
            }
            semicnToken42.formatOutput();
            if(forStmt42!=null){
                forStmt42.formatOutput();
            }
            if(rparentToken4!=null) {//如果发生错误j则没有右小括号
                rparentToken4.formatOutput();
            }
            stmt4.formatOutput();
        }
        //Stmt →    'break' ';'     5
        else if(Utype==5){
            breakToken5.formatOutput();
            if(semicnToken5!=null) {//如果发生错误i则没有分号
                semicnToken5.formatOutput();
            }
        }
        //Stmt →    'continue' ';'      6
        else if(Utype==6){
            continueToken6.formatOutput();
            if(semicnToken6!=null) {//如果发生错误i则没有分号
                semicnToken6.formatOutput();
            }
        }
        //Stmt →    'return' [Exp] ';'      7
        else if(Utype==7){
            returnToken7.formatOutput();
            if(exp7!=null){
                exp7.formatOutput();
            }
            if(semicnToken7!=null) {//如果发生错误i则没有分号
                semicnToken7.formatOutput();
            }
        }
        //Stmt →    'printf''('StringConst {','Exp}')'';'       8
        else{
            printfToken8.formatOutput();
            lparentToken8.formatOutput();
            stringConst8.formatOutput();
            if(commaTokens8!=null){
                for(int i=0;i<commaTokens8.size();i++){
                    commaTokens8.get(i).formatOutput();
                    exps8.get(i).formatOutput();
                }
            }
            if(rparentToken8!=null) {//如果发生错误j则没有右小括号
                rparentToken8.formatOutput();
            }
            if(semicnToken8!=null) {//如果发生错误i则没有分号
                semicnToken8.formatOutput();
            }
        }
        outputSelf();
    }


    //可能的错误：缺少分号 i 报错行号为分号前一个非终结符所在行号。(Utype=0,1,5,6,7,8)
    //可能的错误：缺少右小括号’)’ j  报错行号为右小括号前一个非终结符所在行号。（Utype=3，8）
    @Override
    public void parse(){
        //Stmt →    'printf''('StringConst {','Exp}')'';'       8
        if(Peek(0).getType().equals("PRINTFTK")){
            //'printf'
            this.printfToken8=Peek(0);
            nextToken();
            //'('
            this.lparentToken8=Peek(0);
            nextToken();
            //StringConst
            StringConst stringConst =new StringConst();
            stringConst.parse();
            this.stringConst8=stringConst;
            if(Peek(0).getType().equals("COMMA")){//如果可选先创建ArrayList
                this.commaTokens8=new ArrayList<Token>();
                this.exps8=new ArrayList<Exp>();
            }
            //{','Exp}
            while(Peek(0).getType().equals("COMMA")){
                this.commaTokens8.add(Peek(0));
                nextToken();
                Exp exps=new Exp();
                exps.parse();
                this.exps8.add(exps);
            }
            //')'处理缺失情况
            if(Peek(0).getType().equals("RPARENT")) {
                this.rparentToken8 = Peek(0);
                nextToken();
            }else{//缺失右小括号，报错为j
                this.rparentToken8 = new Token("RPARENT",")",this.lparentToken8.getLineNumber());
                addError(GetBeforeLineNumber(), "j");
            }
            //';'处理缺失情况
            if(Peek(0).getType().equals("SEMICN")){
                this.semicnToken8=Peek(0);
                nextToken();
            }else{//缺失分号，报错为i
                this.semicnToken8 = new Token("SEMICN",";",this.lparentToken8.getLineNumber());
                addError(GetBeforeLineNumber(), "i");
            }
            this.Utype=8;
        }

        //Stmt →    'return' [Exp] ';'      7
        else if(Peek(0).getType().equals("RETURNTK")){
            this.returnToken7=Peek(0);
            nextToken();
            if(Peek(0).getType().equals("IDENFR")||
                    Peek(0).getType().equals("INTCON")||
                    Peek(0).getType().equals("PLUS")||
                    Peek(0).getType().equals("MINU")||
                    Peek(0).getType().equals("NOT")||
                    Peek(0).getType().equals("LPARENT")){
                Exp exp=new Exp();
                exp.parse();
                this.exp7=exp;
            }
            //';'处理缺失情况
            if(Peek(0).getType().equals("SEMICN")){
                this.semicnToken7=Peek(0);
                nextToken();
            }else{//缺失分号，报错为i
                this.semicnToken7 = new Token("SEMICN",";",this.returnToken7.getLineNumber());
                addError(GetBeforeLineNumber(), "i");
            }
            this.Utype=7;
        }

        //Stmt →    'continue' ';'      6
        else if(Peek(0).getType().equals("CONTINUETK")){
            this.continueToken6=Peek(0);
            nextToken();
            //';'处理缺失情况
            if(Peek(0).getType().equals("SEMICN")){
                this.semicnToken6=Peek(0);
                nextToken();
            }else{//缺失分号，报错为i
                this.semicnToken6 = new Token("SEMICN",";",this.continueToken6.getLineNumber());
                addError(GetBeforeLineNumber(), "i");
            }
            this.Utype=6;
        }

        //Stmt →    'break' ';'     5
        else if(Peek(0).getType().equals("BREAKTK")){
            this.breakToken5=Peek(0);
            nextToken();
            //';'处理缺失情况
            if(Peek(0).getType().equals("SEMICN")){
                this.semicnToken5=Peek(0);
                nextToken();
            }else{//缺失分号，报错为i
                this.semicnToken5 = new Token("SEMICN",";",this.breakToken5.getLineNumber());
                addError(GetBeforeLineNumber(), "i");
            }
            this.Utype=5;
        }

        //Stmt →    'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt       4
        else if(Peek(0).getType().equals("FORTK")){
            this.forToken4=Peek(0);
            nextToken();
            this.lparentToken4=Peek(0);
            nextToken();
            //[ForStmt]
            if(!Peek(0).getType().equals("SEMICN")){
                ForStmt forStmt1=new ForStmt();
                forStmt1.parse();
                this.forStmt41=forStmt1;
            }
            //';'
            this.semicnToken41=Peek(0);
            nextToken();
            //[Cond]
            if(!Peek(0).getType().equals("SEMICN")){
                Cond cond=new Cond();
                cond.parse();
                this.cond4=cond;
            }
            //';'
            this.semicnToken42=Peek(0);
            nextToken();
            //[ForStmt]
            if(!Peek(0).getType().equals("RPARENT")){
                ForStmt forStmt2=new ForStmt();
                forStmt2.parse();
                this.forStmt42=forStmt2;
            }
            //')'
            this.rparentToken4=Peek(0);
            nextToken();
            //Stmt
            Stmt stmt=new Stmt();
            stmt.parse();
            this.stmt4=stmt;
            this.Utype=4;
        }

        //Stmt →  'if' '(' Cond ')' Stmt [ 'else' Stmt ]      3
        else if(Peek(0).getType().equals("IFTK")){
            //'if'
            this.ifToken3=Peek(0);
            nextToken();
            //'('
            this.lparentToken3=Peek(0);
            nextToken();
            //Cond

            Cond cond=new Cond();
            cond.parse();
            this.cond3=cond;
            //')'处理缺失情况
            if(Peek(0).getType().equals("RPARENT")) {
                this.rparentToken3 = Peek(0);
                nextToken();
            }else{//缺失右小括号，报错为j
                this.rparentToken3 = new Token("RPARENT",")",this.lparentToken3.getLineNumber());
                addError(GetBeforeLineNumber(), "j");
            }
            //Stmt
            Stmt stmt1 =new Stmt();
            stmt1.parse();
            this.stmt31=stmt1;
            //['else' Stmt]
            if(Peek(0).getType().equals("ELSETK")){
                //'else'
                this.elseToken3=Peek(0);
                nextToken();
                //Stmt
                Stmt stmt2=new Stmt();
                stmt2.parse();
                this.stmt32=stmt2;
            }
            this.Utype=3;
        }
        //Stmt →  Block       2
        else if(Peek(0).getType().equals("LBRACE")){
            Block block=new Block();
            block.parse();
            this.block2=block;
            this.Utype=2;
        }
        //Stmt → LVal '=' Exp ';'     0    |    [Exp] ';'       1
        else if(Peek(0).getType().equals("IDENFR")||
                Peek(0).getType().equals("INTCON")||
                Peek(0).getType().equals("PLUS")||
                Peek(0).getType().equals("MINU")||
                Peek(0).getType().equals("NOT")||
                Peek(0).getType().equals("LPARENT")){//确定是不是Exp
                int saveCurrentIndex = GetcurrentIndex();//保存当前光标位置方便回退
                int saveCurrentErrorsSize=CurrentErrorsSize();//保存当前errors的大小方便回退



                Exp exp1 =new Exp();
                exp1.parse();//无论Exp还是LVal都是Exp，先parse找到产生式0中'='应该出现的位置

                if(Peek(0).getType().equals("ASSIGN")){//Stmt → LVal '=' Exp ';'     0
                    setbackToken(saveCurrentIndex);//回退先前parse的Exp
                    BackToBeforeErrors(saveCurrentErrorsSize);//回退先前parse的Exp产生的错误
                    //LVal
                    LVal lVal0=new LVal();
                    lVal0.parse();
                    this.lVal0=lVal0;

                    //'='
                    this.assignToken0=Peek(0);
                    nextToken();
                    //Exp
                    Exp exp0 =new Exp();
                    exp0.parse();
                    this.exp0=exp0;
                    //';'处理缺失情况
                    if(Peek(0).getType().equals("SEMICN")){
                        this.semicnToken0=Peek(0);
                        nextToken();
                    }else{//缺失分号，报错为i
                        this.semicnToken0 = new Token("SEMICN",";",this.assignToken0.getLineNumber());
                        addError(GetBeforeLineNumber(), "i");
                    }
                    this.Utype=0;
                }
                else {//Stmt → [Exp] ';'       1(一定选择了Exp的情况)
                    this.exp1=exp1;//先前已经创建并且parse
                    //';'处理缺失情况
                    if(Peek(0).getType().equals("SEMICN")){
                        this.semicnToken1=Peek(0);
                        nextToken();
                    }else{//缺失分号，报错为i
                        this.semicnToken1 = new Token("SEMICN",";",this.exp1.GetFirstToken().getLineNumber());
                        addError(GetBeforeLineNumber(), "i");
                    }
                    this.Utype=1;
                }
        }
        else{//Stmt → ';'  1（空语句的情况）
            //';'处理缺失情况
            if(Peek(0).getType().equals("SEMICN")){
                this.semicnToken1=Peek(0);
                nextToken();
            }else{//缺失分号，报错为i
                this.semicnToken1=new Token("SEMICN",";",Peek(0).getLineNumber());
                addError(GetBeforeLineNumber(), "i");
            }
            this.Utype=1;
        }
    }

    public boolean isReturnStmt(){
        if(this.returnToken7!=null){
            return true;
        }
        return false;
    }

    /*
    Stmt → LVal '=' Exp ';'     0
           |    [Exp] ';'       1
           |    Block       2
           |    'if' '(' Cond ')' Stmt [ 'else' Stmt ]      3
           |    'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt       4
           |    'break' ';'     5
           |    'continue' ';'      6
           |    'return' [Exp] ';'      7
           |    'printf''('StringConst {','Exp}')'';'       8

    */
    @Override
    public void visit(){
        //LVal '=' Exp ';'     0   h
        if(this.Utype==0){
            this.lVal0.visit();
            Symbol symbol= SymbolManager.GetSymbol(this.lVal0.GetIdent().GetTokenValue());
            if(!(symbol==null&&this.lVal0.GetIdent().GetTokenType().equals("IDENFR"))) {//排除LVal是未定义Ident
                if (symbol == null || symbol.GetSymbolType().equals("ConstInt") || symbol.GetSymbolType().equals("ConstIntArray")) {
                    addError(this.lVal0.GetIdent().GetTokenLineNumber(), "h");
                }
            }
            this.exp0.visit();
            // ===== IR：赋值语句 LVal = Exp; =====
            IrBasicBlock block = IrBuilder.getCurrentBlock();
            if (block != null) {
                // 右侧表达式求值
                String value = this.exp0.generateIr(block);
                // 左值地址
                String addr = this.lVal0.generateAddr(block);
                // store
                block.addInstruction("store i32 " + value + ", i32* " + addr);
            }
        }
        //[Exp] ';'       1
        else if(this.Utype==1){
            if(this.exp1!=null){
                this.exp1.visit();
                IrBasicBlock block = IrBuilder.getCurrentBlock();
                if (block != null) {
                    // 这里不关心返回值，只是为了执行它
                    this.exp1.generateIr(block);
                }
            }
        }
        //Block       2
        else if(this.Utype==2){
            SymbolManager.CreateSonSymbolTable();
            this.block2.visit();
            SymbolManager.GoToFatherSymbolTable();
        }

//'if' '(' Cond ')' Stmt [ 'else' Stmt ]      3
        else if (this.Utype == 3) {
            // 1. 先做 Cond 的语义分析
            this.cond3.visit();

            // 2. IR 生成上下文
            IrBasicBlock curBlock = IrBuilder.getCurrentBlock();
            IrFunction curFunc = IrBuilder.getCurrentFunction();
            if (curBlock == null || curFunc == null) {
                // 没在函数里就不生成 IR（一般不会发生）
                return;
            }

            IrFactory factory = IrFactory.getInstance();

            // 3. 生成 cond 的 i32 值，并转成 i1
            String condVal = this.cond3.generateIr(curBlock);
            String condBool = factory.newTemp();
            curBlock.addInstruction(condBool + " = icmp ne i32 " + condVal + ", 0");

            // 4. 创建基本块
            IrBasicBlock thenBlock = factory.createBasicBlock(curFunc, "if_then");
            IrBasicBlock endBlock  = factory.createBasicBlock(curFunc, "if_end");
            IrBasicBlock elseBlock = null;

            // 5. 条件跳转
            if (this.stmt32 != null) {
                elseBlock = factory.createBasicBlock(curFunc, "if_else");
                curBlock.addInstruction(
                        "br i1 " + condBool +
                                ", label %" + thenBlock.getLabel() +
                                ", label %" + elseBlock.getLabel()
                );
            } else {
                curBlock.addInstruction(
                        "br i1 " + condBool +
                                ", label %" + thenBlock.getLabel() +
                                ", label %" + endBlock.getLabel()
                );
            }

            // 6. then 分支
            IrBuilder.setCurrentBlock(thenBlock);
            this.stmt31.visit();
            IrBasicBlock thenLast = IrBuilder.getCurrentBlock();
            if (thenLast != null && !blockEndsWithTerminator(thenLast)) {
                // 注意这里用的是 thenLast，而不是 thenBlock
                thenLast.addInstruction("br label %" + endBlock.getLabel());
            }

            // 7. else 分支（如果有）
            if (this.stmt32 != null) {
                IrBuilder.setCurrentBlock(elseBlock);
                this.stmt32.visit();
                IrBasicBlock elseLast = IrBuilder.getCurrentBlock();
                if (elseLast != null && !blockEndsWithTerminator(elseLast)) {
                    // 同样用 elseLast
                    elseLast.addInstruction("br label %" + endBlock.getLabel());
                }
            }

            // 8. 合流到 endBlock
            IrBuilder.setCurrentBlock(endBlock);
        }



        //'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt       4
        else if(this.Utype==4){
            // 先做语义检查：初始化、条件、步进部分
            if (this.forStmt41 != null) {
                this.forStmt41.visit();
            }
            if (this.cond4 != null) {
                this.cond4.visit();
            }
            if (this.forStmt42 != null) {
                this.forStmt42.visit();
            }

            IrBasicBlock curBlock = IrBuilder.getCurrentBlock();
            IrFunction   curFunc  = IrBuilder.getCurrentFunction();

            // 如果当前不在函数里，只做语义分析
            if (curBlock == null || curFunc == null) {
                SymbolManager.EnterForBlock();
                this.stmt4.visit();
                SymbolManager.LeaveForBlock();
            } else {
                IrFactory factory = IrFactory.getInstance();

                // 1. for 初始化：ForStmt1 在当前块中执行
                if (this.forStmt41 != null) {
                    this.forStmt41.generateIr(curBlock);
                }

                // 2. 创建 for 的各个基本块
                IrBasicBlock condBlock = factory.createBasicBlock(curFunc, "for_cond");
                IrBasicBlock bodyBlock = factory.createBasicBlock(curFunc, "for_body");
                IrBasicBlock stepBlock = null;
                if (this.forStmt42 != null) {
                    stepBlock = factory.createBasicBlock(curFunc, "for_step");
                }
                IrBasicBlock endBlock  = factory.createBasicBlock(curFunc, "for_end");

                // 3. 从当前块跳到 cond 块
                curBlock.addInstruction("br label %" + condBlock.getLabel());

                // 4. cond 块：判断循环是否继续
                IrBuilder.setCurrentBlock(condBlock);
                if (this.cond4 != null) {
                    // 使用 Cond 的短路接口：真 -> bodyBlock，假 -> endBlock
                    this.cond4.generateCondBr(condBlock, bodyBlock, endBlock);
                } else {
                    // for(;;) 无条件循环：从 cond 直接跳 body
                    condBlock.addInstruction("br label %" + bodyBlock.getLabel());
                }


                // 5. 注册本层循环的 break / continue 目标
                IrBasicBlock continueTarget = (stepBlock != null) ? stepBlock : condBlock;
                IrBuilder.pushLoop(endBlock, continueTarget);
                SymbolManager.EnterForBlock();

                // 6. 循环体 body
                IrBuilder.setCurrentBlock(bodyBlock);
                this.stmt4.visit();

                IrBasicBlock lastBodyBlock = IrBuilder.getCurrentBlock();

                if (!blockEndsWithTerminator(lastBodyBlock)) {
                    IrBasicBlock afterBody = (stepBlock != null) ? stepBlock : condBlock;
                    lastBodyBlock.addInstruction("br label %" + afterBody.getLabel());
                }


                SymbolManager.LeaveForBlock();
                IrBuilder.popLoop();

                // 7. 步进块 stepBlock：ForStmt2 执行完再回 cond
                if (stepBlock != null) {
                    IrBuilder.setCurrentBlock(stepBlock);
                    this.forStmt42.generateIr(stepBlock);

                    IrBasicBlock lastStepBlock = IrBuilder.getCurrentBlock();
                    if (!blockEndsWithTerminator(lastStepBlock)) {
                        lastStepBlock.addInstruction("br label %" + condBlock.getLabel());
                    }
                }


                // 8. 循环结束后的代码从 endBlock 开始
                IrBuilder.setCurrentBlock(endBlock);
            }
        }

        //'break' ';'     5 m
        else if(this.Utype==5){
            if (SymbolManager.NotInForBlock()) {
                // 不在 for 中使用 break —— 语义错误 m
                addError(this.breakToken5.getLineNumber(), "m");
            } else {
                // ===== IR：生成 break 的跳转 =====
                IrBasicBlock block = IrBuilder.getCurrentBlock();
                IrBasicBlock breakTarget = IrBuilder.getCurrentBreakTarget();
                if (block != null && breakTarget != null) {
                    // 直接跳转到当前循环的 break 目标基本块
                    block.addInstruction("br label %" + breakTarget.getLabel());
                } else if (block != null) {
                    // TODO: for 循环的 IR 尚未实现，这里暂时不生成 break 的跳转 IR
                    // 后续在 for 的 IR 里补上 IrBuilder.pushLoop(...) 后，此处就会生效。
                }
            }
        }
        //'continue' ';'      6 m
        else if(this.Utype==6){
            if (SymbolManager.NotInForBlock()) {
                // 不在 for 中使用 continue —— 语义错误 m
                addError(this.continueToken6.getLineNumber(), "m");
            } else {
                // ===== IR：生成 continue 的跳转 =====
                IrBasicBlock block = IrBuilder.getCurrentBlock();
                IrBasicBlock contTarget = IrBuilder.getCurrentContinueTarget();
                if (block != null && contTarget != null) {
                    // 跳转到当前循环的 continue 目标基本块
                    block.addInstruction("br label %" + contTarget.getLabel());
                } else if (block != null) {
                    // TODO: for 循环的 IR 尚未实现，这里暂时不生成 continue 的跳转 IR
                    // 后续在 for 的 IR 里补上 IrBuilder.pushLoop(...) 后，此处就会生效。
                }
            }
        }

        //'return' [Exp] ';'      7 f
        else if(this.Utype==7){
            if(SymbolManager.GetFuncType().equals("void")){ //给void函数返回
                if(this.exp7!=null){
                    addError(this.returnToken7.getLineNumber(),"f");
                    this.exp7.visit();
                }
                //IR：void 函数返回
                IrBasicBlock block = IrBuilder.getCurrentBlock();
                if (block != null) {
                    block.addInstruction("ret void");
                }
            }
            else {
                if(this.exp7!=null){
                    this.exp7.visit();
                    // 再生成 IR：计算表达式的值，然后 ret i32 <value>
                    IrBasicBlock block = IrBuilder.getCurrentBlock();
                    if (block != null) {
                        String value = this.exp7.generateIr(block);
                        block.addInstruction("ret i32 " + value);
                    }
                }
                else {
                    // IR 兜底：给一个 ret i32 0，避免 IR 不完整
                    IrBasicBlock block = IrBuilder.getCurrentBlock();
                    if (block != null) {
                        block.addInstruction("ret i32 0");
                    }
                }
            }
        }
        //'printf''('StringConst {','Exp}')'';'       8
        else {
            int realcount = 0;
            int formatcount = GetFormatStringCount(this.stringConst8.GetConstString());

            // 当前基本块（在 MainFuncDef.visit 里已经调用 IrBuilder.enterFunction）
            IrBasicBlock block = IrBuilder.getCurrentBlock();

            // 先做语义检查 & 生成每个实参的 IR 值
            java.util.ArrayList<String> argValues = new java.util.ArrayList<>();
            if (this.exps8 != null) {
                realcount = this.exps8.size();
                for (int i = 0; i < this.exps8.size(); i++) {
                    Exp e = this.exps8.get(i);
                    e.visit();  // 原有语义检查

                    if (block != null) {
                        // 利用前面实现好的 Exp.generateIr(...)
                        String v = e.generateIr(block);
                        argValues.add(v);
                    }
                }
            }

            if (realcount != formatcount) { // printf 中格式字符与表达式个数不匹配
                addError(this.printfToken8.getLineNumber(), "l");
            }

            // ===== IR：用 putch / putint 实现 printf("%d", ...) =====
            if (block != null) {
                String fmt = this.stringConst8.GetConstString();

                // 去掉最外层的双引号（比如 "\"%d\"" -> "%d"）
                if (fmt.length() >= 2 && fmt.charAt(0) == '"' && fmt.charAt(fmt.length() - 1) == '"') {
                    fmt = fmt.substring(1, fmt.length() - 1);
                }

                int argIndex = 0;

                for (int i = 0; i < fmt.length(); i++) {
                    char c = fmt.charAt(i);

                    // 处理 %d
                    if (c == '%' && i + 1 < fmt.length()
                            && fmt.charAt(i + 1) == 'd') {

                        if (argIndex < argValues.size()) {
                            String v = argValues.get(argIndex++);
                            block.addInstruction("call void @putint(i32 " + v + ")");
                        }
                        i++; // 跳过 'd'
                    }
                    // 处理转义字符 \n
                    else if (c == '\\' && i + 1 < fmt.length()
                            && fmt.charAt(i + 1) == 'n') {

                        block.addInstruction("call void @putch(i32 10)");
                        i++; // 跳过 'n'
                    }
                    // 普通字符
                    else {
                        block.addInstruction("call void @putch(i32 " + (int) c + ")");
                    }
                }
            }
        }
    }


    /**
     * 判断一个基本块是否已经以终结指令结束（ret 或 br）。
     */
    private boolean blockEndsWithTerminator(IrBasicBlock block) {
        if (block == null) {
            return false;
        }
        java.util.List<String> insts = block.getInstructions();
        if (insts == null || insts.isEmpty()) {
            return false;
        }
        String last = insts.get(insts.size() - 1).trim();
        return last.startsWith("ret") || last.startsWith("br");
    }


    private int GetFormatStringCount(String formatString) {
        int count = 0;
        for (int i = 0; i < formatString.length() - 1; i++) {
            if (formatString.charAt(i) == '%' && formatString.charAt(i + 1) == 'd') {
                count++;
            }
        }
        return count;
    }



    public Stmt(){
        super(SyntaxType.STMT);
    }
}
