import java.io.*;
import java.nio.file.*;
import java.util.*;

// 导入错误处理包
import Error.ErrorHandler;
import OutputHelper.OutputHelper;
import frontend.TokenStream;
import midend.Ir.IrFactory;
import midend.Ir.IrModule;

import static frontend.Parser.parse;
import static midend.MidEnd.GenerateSymbolTable;


public class Compiler {
    public static void main(String[] args) {
        try {
            // 读取源程序
            String sourceCode = readFile("testfile.txt");
            if (sourceCode == null) {
                System.err.println("无法读取源程序文件");
                return;
            }

            // 进行词法分析
            frontend.Lexer lexer = new frontend.Lexer();
            lexer.tokenize(sourceCode);
            //将词法分析结果的Token流输入TokenStream
            ArrayList<frontend.Token> tokens = lexer.getTokens();
            TokenStream.setTokens(tokens);
            writeTokensToFile("lexer.txt",tokens);
            //进行语法分析
            OutputHelper.initialize("parser.txt");
            parse();
            OutputHelper.close();
            //进行语义分析
            OutputHelper.initialize("symbol.txt");
            GenerateSymbolTable();
            OutputHelper.close();

            //LLVM输出
            OutputHelper.initialize("llvm_ir.txt");
            OutputHelper.write(midend.Ir.IrGenerator.generate());
            OutputHelper.close();
            // 根据是否有错误选择输出
            ErrorHandler.writeErrorsToFile("error.txt");


        } catch (Exception e) {
            System.err.println("程序执行错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String readFile(String filename) {
        try {
            return Files.readString(Paths.get(filename));
        } catch (IOException e) {
            System.err.println("文件读取错误: " + e.getMessage());
            return null;
        }
    }

    private static void writeTokensToFile(String filename, List<frontend.Token> tokens) {
        try {
            List<String> outputLines = new ArrayList<>();
            for (frontend.Token token : tokens) {
                outputLines.add(token.getType() + " " + token.getValue());
            }
            Files.write(Paths.get(filename), outputLines);
        } catch (IOException e) {
            System.err.println("文件写入错误: " + e.getMessage());
        }
    }


}