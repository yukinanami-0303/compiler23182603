package midend;

import frontend.ast.Node;
import midend.Ir.IrGenerator;
import midend.Symbol.SymbolManager;

import static OutputHelper.OutputHelper.write;
import static frontend.Parser.GetAstTree;
import static midend.Symbol.SymbolManager.GetSymbolTable;

public class MidEnd {
    public enum Phase { SEMANTIC, IR }
    private static Phase phase = Phase.SEMANTIC;

    public static boolean isSemantic() { return phase == Phase.SEMANTIC; }
    public static boolean isIR() { return phase == Phase.IR; }

    private static Node rootNode;

    /** 第一遍：语义 + 建符号表（不生成 IR） */
    public static void GenerateSymbolTable() {
        phase = Phase.SEMANTIC;

        // ✅ 必须初始化符号表
        SymbolManager.Init();

        rootNode = GetAstTree();
        rootNode.visit();

        // ✅ 把符号表内容写到 symbol.txt（Compiler 已经 initialize("symbol.txt")）
        write(GetSymbolTable().OutputSymbolTable());
    }

    /** 第二遍：生成 IR（假设无语义错误） */
    public static void GenerateLLVMIR() {
        phase = Phase.IR;

        // ✅ 清空 IR 全局状态
        IrGenerator.reset();

        // ✅ 复用第一遍符号表树：回到 root，并复位子表遍历游标
        SymbolManager.GoBackToRootSymbolTable();
        SymbolManager.ResetSonTableIterators();

        // ✅ 复用同一棵 AST（不要重新 GetAstTree() 生成新对象）
        if (rootNode == null) {
            rootNode = GetAstTree();
        }
        rootNode.visit();
    }
}
