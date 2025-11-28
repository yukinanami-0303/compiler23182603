package frontend.ast.decl;
import java.io.*;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.def.VarDef;
import frontend.ast.token.BType;
import java.util.ArrayList;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class VarDecl extends Node {
    //VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
    private Token staticToken;
    private BType bType;
    private VarDef varDef;
    private ArrayList<Token> commaTokens;
    private ArrayList<VarDef> varDefs;
    private Token semicnToken;
    public VarDecl(Token staticToken,
                   BType bType,
                   VarDef varDef,
                   ArrayList<Token> commaTokens,
                   ArrayList<VarDef> varDefs,
                   Token semicnToken) {
        super(SyntaxType.VAR_DECL);
        this.staticToken=staticToken;
        this.bType=bType;
        this.varDef=varDef;
        this.commaTokens=commaTokens;
        this.varDefs=varDefs;
        this.semicnToken=semicnToken;
    }
    @Override
    public void formatOutput() throws IOException {
        if (staticToken != null) {
            staticToken.formatOutput();
        }
        bType.formatOutput();
        varDef.formatOutput();
        if(commaTokens!=null) {
            for (int i = 0; i < commaTokens.size(); i++) {
                commaTokens.get(i).formatOutput();
                varDefs.get(i).formatOutput();
            }
        }
        if(semicnToken!=null) {//如果发生错误i则没有分号
            semicnToken.formatOutput();
        }
        outputSelf();
    }


    //可能的错误：缺少分号 i 报错行号为分号前一个非终结符所在行号。
    //VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
    @Override
    public void parse(){
        //'static'
        if(Peek(0).getType().equals("STATICTK")){
            this.staticToken=Peek(0);
            nextToken();
        }
        //Btype
        BType bType=new BType();
        bType.parse();
        this.bType=bType;
        //VarDef
        VarDef varDef=new VarDef();
        varDef.parse();
        this.varDef=varDef;
        //{',' VarDef}
        if(Peek(0).getType().equals("COMMA")){//如果可选先创建ArrayList
            this.commaTokens=new ArrayList<Token>();
            this.varDefs=new ArrayList<VarDef>();
        }
        while(Peek(0).getType().equals("COMMA")){
            this.commaTokens.add(Peek(0));
            nextToken();
            VarDef varDefins =new VarDef();
            varDefins.parse();
            this.varDefs.add(varDefins);
        }

        //关于分号
        if(Peek(0).getType().equals("SEMICN")){
            this.semicnToken=Peek(0);
            nextToken();
        }else{//缺失分号，报错为i
            this.semicnToken=new Token("SEMICN",";",this.bType.GetBtypeLineNumber());
            addError(GetBeforeLineNumber(), "i");
        }

    }
    //VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
    @Override
    public void visit(){
        if(this.staticToken!=null){
            this.varDef.isStatic=true;
        }
        varDef.visit();
        if(this.varDefs!=null) {
            for (int i = 0; i < varDefs.size(); i++) {
                if (this.staticToken != null) {
                    this.varDefs.get(i).isStatic = true;
                }
                varDefs.get(i).visit();
            }
        }
    }


    public VarDecl(){
        super(SyntaxType.VAR_DECL);
    }
}
