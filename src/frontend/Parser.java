package frontend;

import frontend.ast.CompUnit;

import java.io.IOException;

import static frontend.TokenStream.Peek;


public class Parser {
    private static CompUnit compUnit=new CompUnit();
    public static void parse() throws IOException {
        // 调用CompUnit的parse方法构建语法树
        compUnit.parse();
        // 调用formatOutput方法将语法树输出到parser.txt
        compUnit.formatOutput();
    }
    public static CompUnit GetAstTree(){
        return compUnit;
    }
}
