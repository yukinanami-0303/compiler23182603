package frontend.ast.block;

import frontend.Parser;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.decl.Decl;
import frontend.ast.stmt.Stmt;

import java.io.IOException;

import static frontend.TokenStream.*;

public class BlockItem extends Node {
    //BlockItem → Decl | Stmt
    private int Utype;
    private Decl decl0=null;
    //---------------------
    private Stmt stmt1=null;

    public BlockItem(Decl decl) {
        super(SyntaxType.BLOCK_ITEM);
        this.decl0=decl;
        this.Utype=0;
    }
    //-----------------------------------
    public BlockItem(Stmt stmt) {
        super(SyntaxType.BLOCK_ITEM);
        this.stmt1=stmt;
        this.Utype=1;
    }


    @Override
    public void formatOutput() throws IOException{
        if(decl0!=null){
            decl0.formatOutput();
        }
        else if(stmt1!=null){
            stmt1.formatOutput();
        }
    }


    @Override
    public void parse(){
        //Decl
        if(Peek(0).getType().equals("INTTK")||
        Peek(0).getType().equals("STATICTK")||
        Peek(0).getType().equals("CONSTTK")){

            Decl decl=new Decl();
            decl.parse();
            this.decl0=decl;
        }
        //Stmt
        else {
            Stmt stmt=new Stmt();
            stmt.parse();
            this.stmt1=stmt;
        }
    }
    @Override
    public void visit(){
        if(decl0!=null){
            decl0.visit();
        }
        else if(stmt1!=null){
            stmt1.visit();
        }
    }
    public boolean isReturnStmt(){
        if(this.stmt1!=null){
            if(this.stmt1.isReturnStmt()) {
                return true;
            }
        }
        return false;
    }
    public BlockItem(){
        super(SyntaxType.BLOCK_ITEM);
    }
}
