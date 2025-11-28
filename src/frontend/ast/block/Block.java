package frontend.ast.block;

import frontend.Parser;
import frontend.Token;
import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.io.IOException;
import java.util.ArrayList;

import static frontend.TokenStream.*;

public class Block extends Node {
//Block → '{' { BlockItem } '}'
    private Token lbraceToken;
    private ArrayList<BlockItem> blockItems;
    private Token rbraceToken;
    public Block(Token lbraceToken,
                 ArrayList<BlockItem> blockItems,
                 Token rbraceToken) {
        super(SyntaxType.BLOCK);
        this.lbraceToken=lbraceToken;
        this.blockItems=blockItems;
        this.rbraceToken=rbraceToken;
    }
    @Override
    public void formatOutput() throws IOException{
        lbraceToken.formatOutput();
        if(blockItems!=null) {
            for (int i = 0; i < blockItems.size(); i++) {
                blockItems.get(i).formatOutput();
            }
        }
        rbraceToken.formatOutput();
        outputSelf();
    }
    @Override
    public void parse(){

        this.lbraceToken=Peek(0);
        nextToken();

        if(!Peek(0).getType().equals("RBRACE")){//如果{}可选先创建ArrayList
            this.blockItems=new ArrayList<BlockItem>();
        }

        while(!Peek(0).getType().equals("RBRACE")){
           // System.out.println(Peek(0).getType());
            BlockItem blockItem=new BlockItem();
            blockItem.parse();
            this.blockItems.add(blockItem);
        }

        this.rbraceToken=Peek(0);
        nextToken();
    }


    @Override
    public void visit(){
        if(blockItems!=null) {
            for (int i = 0; i < blockItems.size(); i++) {
                blockItems.get(i).visit();
            }
        }
    }


    public boolean haveReturnStmt(){
        if(this.blockItems!=null){//检查最后一个BlockItem是否是return
            if(this.blockItems.get(this.blockItems.size()-1).isReturnStmt()) {
                return true;
            }
        }
        return false;
    }
    public int GetRbraceLineNumber(){
        return this.rbraceToken.getLineNumber();
    }

    public Block(){
        super(SyntaxType.BLOCK);
    }



}
