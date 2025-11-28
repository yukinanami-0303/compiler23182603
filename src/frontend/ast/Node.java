package frontend.ast;
import OutputHelper.OutputHelper;

import java.io.*;
public abstract class Node {
    // 节点类型名称
    protected final SyntaxType syntaxType;

    public Node (SyntaxType syntaxType){
        this.syntaxType = syntaxType;
    }

    // 获取节点类型
    public SyntaxType getNodeType() {
        return syntaxType;
    }



//抽象类递归输出
    public abstract void formatOutput() throws IOException;



    //抽象递归分析
    public abstract void parse();



    // 统一的输出语法结构名方法
    protected void output(String content) throws IOException {
        OutputHelper.write(content);
    }

    // 输出当前节点的语法结构名
    protected void outputSelf() throws IOException {
        output("<" + syntaxType.typeName + ">\n");
    }


    //抽象visit函数
    public abstract void visit();
}
