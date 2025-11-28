package frontend.ast;
import frontend.Parser;
import frontend.TokenStream;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.decl.Decl;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;

import java.io.IOException;
import java.util.ArrayList;

import static frontend.TokenStream.GetcurrentIndex;
import static frontend.TokenStream.Peek;

public class CompUnit extends Node{
    //CompUnit → {Decl} {FuncDef} MainFuncDef
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    public CompUnit(ArrayList<Decl> decls,
                    ArrayList<FuncDef> funcDefs,
                    MainFuncDef mainFuncDef) {
        super(SyntaxType.COMP_UNIT);
        this.decls=decls;
        this.funcDefs=funcDefs;
        this.mainFuncDef=mainFuncDef;
    }

    @Override
    public void formatOutput() throws IOException {
        if(this.decls!=null) {
            for (int i = 0; i < decls.size(); i++) {
                decls.get(i).formatOutput();
            }
        }
        if(this.funcDefs!=null) {
            for (int i = 0; i < funcDefs.size(); i++) {
                funcDefs.get(i).formatOutput();
            }
        }
        mainFuncDef.formatOutput();
        outputSelf();
    }



    public void parse() {
        this.funcDefs=new ArrayList<FuncDef>();
        this.decls=new ArrayList<Decl>();
        while(!Peek(0).getType().equals("EOF")){
            //MainFuncDef
            if(Peek(0).getType().equals("INTTK")&&Peek(1).getType().equals("MAINTK")){
                MainFuncDef mainFuncDef =new MainFuncDef();
                mainFuncDef.parse();
                this.mainFuncDef=mainFuncDef;
            }
            //{FuncDefs}
            else if(Peek(2).getType().equals("LPARENT")){
                FuncDef funcDef = new FuncDef();
                funcDef.parse();
                this.funcDefs.add(funcDef);
            }
            //{Decls}
            else if(Peek(0).getType().equals("CONSTTK")||
            Peek(0).getType().equals("INTTK")){
                Decl decl = new Decl();
                decl.parse();
                this.decls.add(decl);
            }
        }
    }

    @Override
    public void visit(){
        if(this.decls!=null) {
            for (int i = 0; i < decls.size(); i++) {
                decls.get(i).visit();
            }
        }
        if(this.funcDefs!=null) {
            for (int i = 0; i < funcDefs.size(); i++) {
                funcDefs.get(i).visit();
            }
        }
        mainFuncDef.visit();
    }

    public CompUnit(){
        super(SyntaxType.COMP_UNIT);
    }
}
