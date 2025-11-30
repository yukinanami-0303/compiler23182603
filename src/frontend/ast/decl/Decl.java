package frontend.ast.decl;
import frontend.Parser;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;

import static frontend.TokenStream.*;

public class Decl extends Node{
    //Decl → ConstDecl | VarDecl
    private  boolean isConst=false;
    private int Utype;
    private ConstDecl constDecl0=null;
    //---------------------------------------------
    private VarDecl varDecl1=null;
    public Decl(ConstDecl constDecl) {
        super(SyntaxType.DECL);
        this.constDecl0=constDecl;
        this.Utype=0;
    }
    //---------------------------------
    public Decl(VarDecl varDecl){
        super(SyntaxType.DECL);
        this.varDecl1=varDecl;
        this.Utype=1;
    }
    @Override
    public void formatOutput() throws IOException {
        if(constDecl0!=null){
            constDecl0.formatOutput();
        }
        else{
            varDecl1.formatOutput();
        }
    }



    @Override
    public void parse(){
        //ConstDecl
        if(Peek(0).getType().equals("CONSTTK")){
            ConstDecl constDecl=new ConstDecl();
            constDecl.parse();
            this.constDecl0=constDecl;
            this.isConst=true;
        }
        //VarDecl
        else{
            VarDecl varDecl=new VarDecl();
            varDecl.parse();
            this.varDecl1=varDecl;
        }
    }

    @Override
    public void visit(){
        if(constDecl0!=null){
            constDecl0.visit();
        }
        else{
            varDecl1.visit();
        }
    }

    public Decl(){
        super(SyntaxType.DECL);
    }
}
