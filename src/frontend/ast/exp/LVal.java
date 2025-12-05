package frontend.ast.exp;
import frontend.Parser;
import frontend.Token;
import frontend.ast.SyntaxType;
import frontend.ast.Node;
import frontend.ast.token.Ident;
import midend.Ir.IrBasicBlock;
import midend.Ir.IrFactory;
import midend.Symbol.Symbol;
import midend.Symbol.SymbolManager;
import midend.Symbol.ValueSymbol;

import java.io.IOException;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class LVal extends Node {
    //LVal → Ident ['[' Exp ']']


    private Ident ident=null;
    private Token lbrackToken=null;
    private Exp exp=null;
    private Token rbrackToken=null;
    public LVal(Ident ident,
                Token lbrackToken,
                Exp exp,
                Token rbrackToken) {
        super(SyntaxType.LVAL_EXP);
        this.ident=ident;
        this.lbrackToken=lbrackToken;
        this.exp=exp;
        this.rbrackToken=rbrackToken;
    }
    @Override
    public void formatOutput() throws IOException {
        ident.formatOutput();
        if(lbrackToken!=null){
            lbrackToken.formatOutput();
            exp.formatOutput();
            if(rbrackToken!=null) {//如果发生错误k则没有右中括号
                rbrackToken.formatOutput();
            }
        }
        outputSelf();
    }

    //LVal → Ident ['[' Exp ']']
    //可能的错误：缺少右中括号’]’ k  报错行号为右中括号前一个非终结符所在行号。
    @Override
    public void parse(){
        //Ident
        Ident ident =new Ident();
        ident.parse();
        this.ident=ident;
        //'['
         if(Peek(0).getType().equals("LBRACK")){
             this.lbrackToken=Peek(0);
             nextToken();
             //Exp
             Exp exp=new Exp();
             exp.parse();
             this.exp=exp;
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

    //LVal → Ident ['[' Exp ']']
    @Override
    public void visit(){
        String identName=ident.GetTokenValue();
        if(SymbolManager.GetSymbol(identName)==null){
            addError(ident.GetTokenLineNumber(), "c");
        }
    }

    /**
     * 在表达式中计算 LVal 的值。
     * - const 标量：直接返回立即数
     * - 非 const 标量：load
     * - 数组元素 a[i]：用 GEP 得到地址，再 load
     */
    public String generateIr(IrBasicBlock curBlock) {
        String identName = ident.GetTokenValue();
        Symbol symbol = SymbolManager.GetSymbol(identName);

        if (symbol instanceof ValueSymbol) {
            ValueSymbol valueSymbol = (ValueSymbol) symbol;

            // 1) 编译期常量标量（const int x = 1; x）→ 立即数
            if (valueSymbol.IsConst()
                    && this.exp == null
                    && valueSymbol.GetValueList() != null
                    && !valueSymbol.GetValueList().isEmpty()
                    && !valueSymbol.GetSymbolType().endsWith("Array")) {  // 避免 const 数组元素
                int v = valueSymbol.GetValueList().get(0);
                return Integer.toString(v);
            }

            // 2) 其它情况：包括标量变量和数组元素，统一用 load
            String addr = this.generateAddr(curBlock);  // 会处理 a / a[i]
            String tmp = IrFactory.getInstance().newTemp();
            curBlock.addInstruction(tmp + " = load i32, i32* " + addr);
            return tmp;
        }

        return "0";
    }




    /**
     * 生成 LVal 对应的地址（指针），供 store / load 使用。
     * - 标量：
     *     static   → @__static_a
     *     全局     → @a
     *     局部     → %a
     * - 数组元素 a[i]：
     *     base 为上述三者之一，再用 GEP 得到 i32*。
     */
    public String generateAddr(IrBasicBlock curBlock) {
        String identName = ident.GetTokenValue();
        Symbol symbol = SymbolManager.GetSymbol(identName);

        if (symbol instanceof ValueSymbol) {
            ValueSymbol valueSymbol = (ValueSymbol) symbol;
            String symbolType = valueSymbol.GetSymbolType();
            boolean isArray = symbolType.endsWith("Array");

            // ==== 标量：没有下标，沿用原来的规则 ====
            if (!isArray || this.exp == null) {
                if (symbolType.startsWith("Static")) {
                    return "@__static_" + identName;
                } else if (valueSymbol.IsGlobal()) {
                    return "@" + identName;
                } else {
                    return "%" + identName;
                }
            }

            // ==== 数组元素：a[Exp] ====
            int len = valueSymbol.GetArrayLength();   // 在 ConstDef / VarDef 中已设置
            String idx = this.exp.generateIr(curBlock);

            String base;
            if (symbolType.startsWith("Static")) {
                base = "@__static_" + identName;
            } else if (valueSymbol.IsGlobal()) {
                base = "@" + identName;
            } else {
                base = "%" + identName;
            }

            String ptr = IrFactory.getInstance().newTemp();
            curBlock.addInstruction(
                    ptr + " = getelementptr [" + len + " x i32], [" + len + " x i32]* " + base +
                            ", i32 0, i32 " + idx
            );
            return ptr;
        }

        // 防御性兜底
        return "%undef";
    }



    public Ident GetIdent(){
        return this.ident;
    }

    // 仅用于 ConstExp / ConstInitVal 等编译期常量求值场景
    // 要求该 LVal 对应的是 const 标量，并且没有下标（不是数组 / 非 const 的情况直接兜底）
    public int GetConstValue() {
        String identName = ident.GetTokenValue();
        Symbol symbol = SymbolManager.GetSymbol(identName);
        if (symbol instanceof ValueSymbol) {
            ValueSymbol valueSymbol = (ValueSymbol) symbol;
            if (valueSymbol.IsConst()
                    && this.exp == null
                    && valueSymbol.GetValueList() != null
                    && !valueSymbol.GetValueList().isEmpty()) {
                return valueSymbol.GetValueList().get(0);
            }
        }
        // 非法常量表达式场景防御性返回 0，语义上一般不会走到这里
        return 0;
    }

    public LVal(){
        super(SyntaxType.LVAL_EXP);
    }
}
