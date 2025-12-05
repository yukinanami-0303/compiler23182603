package frontend.ast.func;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.Exp;
import midend.Ir.IrBasicBlock;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class FuncRParams extends Node{
    //FuncRParams → Exp { ',' Exp }
    private Exp exp;
    private ArrayList<Token> commaTokens;
    private ArrayList<Exp> exps;
    public FuncRParams(Exp exp,
                       ArrayList<Token> commaTokens,
                       ArrayList<Exp> exps) {
        super(SyntaxType.FUNC_REAL_PARAM_S);
        this.exp=exp;
        this.commaTokens=commaTokens;
        this.exps=exps;
    }
    @Override
    public void formatOutput() throws IOException {
        exp.formatOutput();
        if(commaTokens!=null) {
            for (int i = 0; i < commaTokens.size(); i++) {
                commaTokens.get(i).formatOutput();
                exps.get(i).formatOutput();
            }
        }
        outputSelf();
    }
    //FuncRParams → Exp { ',' Exp }
    @Override
    public void parse(){
        Exp exp =new Exp();
        exp.parse();
        this.exp=exp;
        if(Peek(0).getType().equals("COMMA")){//如果可选先创建ArrayList
            this.commaTokens=new ArrayList<Token>();
            this.exps=new ArrayList<Exp>();
        }
        while(Peek(0).getType().equals("COMMA")){
            this.commaTokens.add(Peek(0));
            nextToken();
            Exp exps=new Exp();
            exps.parse();
            this.exps.add(exps);
        }
    }
    @Override
    public void visit(){
        this.exp.visit();
        if(this.exps!=null) {
            for (int i = 0; i < exps.size(); i++) {
                exps.get(i).visit();
            }
        }
    }
    public ArrayList<Exp> GetRealParamList() {
        ArrayList<Exp> realParamList = new ArrayList<>();
        realParamList.add(this.exp);
        if(this.exps!=null) {//有多个实际参数
            for (int i = 0; i < exps.size(); i++) {
                realParamList.add(exps.get(i));
            }
        }
        return realParamList;
    }
    /**
     * 生成实参表达式的 IR，返回每个实参的 SSA 名或立即数。
     * 假定 visit() 已经在语义阶段被调用过，这里只做 IR。
     */
    public List<String> generateArgsIr(IrBasicBlock block) {
        ArrayList<String> res = new ArrayList<>();
        res.add(exp.generateIr(block));
        if (exps != null) {  // 这里的 exps 是你保存所有 Exp 实参的列表字段
            for (int i = 0; i < exps.size(); i++) {
                Exp e = exps.get(i);
                String v = e.generateIr(block);
                res.add(v);
            }
        }
        return res;
    }

    public FuncRParams(){
        super(SyntaxType.FUNC_REAL_PARAM_S);
    }
}
