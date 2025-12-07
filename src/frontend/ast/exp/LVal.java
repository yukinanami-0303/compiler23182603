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
            // 包括普通 int、const int、以及“整个数组名”这种情况（数组但没有下标）
            if (!isArray || this.exp == null) {
                // 优先使用符号上记录的 IR 名（static / 全局 / alloca 均可）
                if (valueSymbol instanceof ValueSymbol) {
                    String irName = ((ValueSymbol) valueSymbol).GetIrName();
                    if (irName != null && !irName.isEmpty()) {
                        return irName;
                    }
                }

                if (symbolType.startsWith("Static")) {
                    return "@__static_" + identName;
                } else if (valueSymbol.IsGlobal()) {
                    return "@" + identName;
                } else {
                    return "%" + identName;
                }
            }


            // ==== 数组元素：a[Exp] ====
            String idx = this.exp.generateIr(curBlock);   // 下标值（i32）

            // 2.1 形参数组：int a[] —— 本质类型是 i32*
            if (valueSymbol.IsArrayParam()) {
                // 在 FuncFParam.visit() 中，数组形参已经设置过 irParamName = "%arg.a"
                String basePtr = valueSymbol.GetIrParamName();
                if (basePtr == null || basePtr.isEmpty()) {
                    // 防御性兜底：若没记录 irParamName，就退回用 %a
                    basePtr = "%" + identName;
                }

                String ptr = IrFactory.getInstance().newTemp();
                // 对 i32* 做 GEP：getelementptr i32, i32* %base, i32 idx
                curBlock.addInstruction(
                        ptr + " = getelementptr i32, i32* " + basePtr + ", i32 " + idx
                );
                return ptr;
            }

            // 2.2 普通数组（全局 / 静态 / 局部 alloca [N x i32]）
            int len = valueSymbol.GetArrayLength();       // 在 VarDef / ConstDef 中设置
            // 防御：理论上这里 len 必须 > 0，避免出现 [-1 x i32]
            if (len <= 0) {
                // 这里给个兜底（不会影响正常的 a[5] 等情况，主要防止 [-1 x i32] 再次出现）
                len = 1;
            }

            String base = null;
            // 若在 VarDef / ConstDef 中已经为该符号设置了 irName（static / 全局 / alloca）
            String recorded = null;
            if (valueSymbol instanceof ValueSymbol) {
                recorded = ((ValueSymbol) valueSymbol).GetIrName();
            }
            if (recorded != null && !recorded.isEmpty()) {
                base = recorded;
            } else if (symbolType.startsWith("Static")) {
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

    /**
     * 仅用于 ConstExp / ConstInitVal / 全局初始化 等编译期常量求值场景。
     *
     * 支持两类情况：
     *  1) const 标量：    a
     *  2) const 一维数组：b[a] 其中 b 为 const 数组、a 为编译期常量下标
     *
     * 其它情况（非常量、非常量下标、多维等）统一返回 0，表示“不是合法常量表达式”。
     */
    public int GetConstValue() {
        String identName = ident.GetTokenValue();
        Symbol symbol = SymbolManager.GetSymbol(identName);
        if (!(symbol instanceof ValueSymbol)) {
            // 找不到符号或不是 ValueSymbol，都视为非常量表达式
            return 0;
        }
        ValueSymbol vSym = (ValueSymbol) symbol;

        // 所有常量求值都要求符号本身是 const，并且有保存初始化后的 valueList
        java.util.ArrayList<Integer> valueList = vSym.GetValueList();
        if (!vSym.IsConst() || valueList == null || valueList.isEmpty()) {
            return 0;
        }

        // 1) const 标量：没有下标，直接取 valueList[0]
        if (this.exp == null) {
            return valueList.get(0);
        }

        // 2) const 一维数组：有一个下标表达式，比如 b[a]
        //    这里要求下标本身也是编译期常量（Exp.GetValue() 已经走完整个常量求值链）
        int index = this.exp.GetValue();

        // 越界都视为“非法常量表达式”，按之前约定返回 0
        if (index < 0 || index >= valueList.size()) {
            return 0;
        }
        return valueList.get(index);
    }


    public LVal(){
        super(SyntaxType.LVAL_EXP);
    }
}
