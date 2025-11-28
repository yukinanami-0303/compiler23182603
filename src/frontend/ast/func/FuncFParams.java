package frontend.ast.func;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import midend.Symbol.Symbol;

import java.io.IOException;
import java.util.ArrayList;

import static frontend.TokenStream.Peek;
import static frontend.TokenStream.nextToken;

public class FuncFParams extends Node{
    //FuncFParams → FuncFParam { ',' FuncFParam }
    private FuncFParam funcFParam;
    private ArrayList<Token> commaTokens;
    private ArrayList<FuncFParam> funcFParams;
    public FuncFParams(FuncFParam funcFParam,
                       ArrayList<Token> commaTokens,
                       ArrayList<FuncFParam> funcFParams) {
        super(SyntaxType.FUNC_FORMAL_PARAM_S);
        this.funcFParam=funcFParam;
        this.commaTokens=commaTokens;
        this.funcFParams=funcFParams;
    }
    @Override
    public void formatOutput() throws IOException {
        funcFParam.formatOutput();
        if(commaTokens!=null) {
            for (int i = 0; i < commaTokens.size(); i++) {
                commaTokens.get(i).formatOutput();
                funcFParams.get(i).formatOutput();
            }
        }
        outputSelf();
    }
    //FuncFParams → FuncFParam { ',' FuncFParam }
    @Override
    public void parse(){
        FuncFParam funcFParam=new FuncFParam();
        funcFParam.parse();
        this.funcFParam=funcFParam;
        if(Peek(0).getType().equals("COMMA")){//如果可选先创建ArrayList
            this.commaTokens=new ArrayList<Token>();
            this.funcFParams=new ArrayList<FuncFParam>();
        }
        while(Peek(0).getType().equals("COMMA")){
            this.commaTokens.add(Peek(0));
            nextToken();
            FuncFParam funcFParams=new FuncFParam();
            funcFParams.parse();
            this.funcFParams.add(funcFParams);
        }
    }


    @Override
    public void visit(){
        this.funcFParam.visit();
        if(funcFParams!=null) {
            for (int i = 0; i < funcFParams.size(); i++) {
                funcFParams.get(i).visit();
            }
        }
    }

    public ArrayList<Symbol> GetFormalParamList() {
        ArrayList<Symbol> formalParamList = new ArrayList<>();
        formalParamList.add(this.funcFParam.GetSymbol());
        if(this.funcFParams!=null) {
            for (int i = 0; i < this.funcFParams.size(); i++) {
                formalParamList.add(this.funcFParams.get(i).GetSymbol());
            }
        }
        return formalParamList;
    }
    public FuncFParams(){
        super(SyntaxType.FUNC_FORMAL_PARAM_S);
    }
}