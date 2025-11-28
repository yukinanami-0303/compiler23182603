package frontend.ast.decl;
import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.def.ConstDef;
import frontend.ast.token.BType;

import java.io.IOException;
import java.util.ArrayList;

import static Error.ErrorHandler.addError;
import static frontend.TokenStream.*;

public class ConstDecl extends Node{
    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    private Token constToken;
    private BType bType;
    private ConstDef constDef;
    private ArrayList<Token> commaTokens;
    private ArrayList<ConstDef> constDefs;
    private Token semicnToken;
    public ConstDecl(Token constToken,
                     BType bType,
                     ConstDef constDef,
                     ArrayList<Token> commaTokens,
                     ArrayList<ConstDef> constDefs,
                     Token semicnToken) {
        super(SyntaxType.CONST_DECL);
        this.constToken=constToken;
        this.bType=bType;
        this.constDef=constDef;
        this.commaTokens=commaTokens;
        this.constDefs=constDefs;
        this.semicnToken=semicnToken;
    }
    @Override
    public void formatOutput() throws IOException {
        constToken.formatOutput();
        bType.formatOutput();
        constDef.formatOutput();
        if(commaTokens!=null) {
            for (int i = 0; i < commaTokens.size(); i++) {
                commaTokens.get(i).formatOutput();
                constDefs.get(i).formatOutput();
            }
        }
        if(semicnToken!=null) {//如果发生错误i则没有分号
            semicnToken.formatOutput();
        }
        outputSelf();
    }


    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    //可能的错误：缺少分号 i 报错行号为分号前一个非终结符所在行号。
    @Override
    public void parse(){
        this.constToken=Peek(0);
        nextToken();
        BType bTypein=new BType();
        bTypein.parse();
        this.bType=bTypein;
        ConstDef constDefin=new ConstDef();
        constDefin.parse();
        this.constDef=constDefin;
        if(Peek(0).getType().equals("COMMA")){//如果{}可选先创建ArrayList
            this.commaTokens=new ArrayList<Token>();
            this.constDefs=new ArrayList<ConstDef>();
        }
        while(Peek(0).getType().equals("COMMA")){
            this.commaTokens.add(Peek(0));
            nextToken();
            ConstDef constDefins=new ConstDef();
            constDefins.parse();
            this.constDefs.add(constDefins);
        }
        //关于分号
        if(Peek(0).getType().equals("SEMICN")){
            this.semicnToken=Peek(0);
            nextToken();
        }else{//缺失分号，报错为i
            this.semicnToken=new Token("SEMICN",";",this.constToken.getLineNumber());
            addError(GetBeforeLineNumber(), "i");
        }
    }


    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    @Override
    public void visit(){
        constDef.visit();
        if(this.constDefs!=null) {
            for (int i = 0; i < constDefs.size(); i++) {
                constDefs.get(i).visit();
            }
        }
    }

    public ConstDecl(){
        super(SyntaxType.CONST_DECL);
    }
}
