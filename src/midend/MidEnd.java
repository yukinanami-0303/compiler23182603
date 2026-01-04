package midend;

import frontend.ast.Node;
import midend.Ir.IrGenerator;
import midend.Symbol.SymbolManager;

import static frontend.Parser.GetAstTree;

public class MidEnd {
    public enum Phase { SEMANTIC, IR }
    private static Phase phase = Phase.SEMANTIC;

    public static boolean isSemantic() { return phase == Phase.SEMANTIC; }
    public static boolean isIR() { return phase == Phase.IR; }

    private static Node rootNode;

    /** 第一遍：语义 + 建符号表（不生成 IR） */
    public static void GenerateSymbolTable() {
        phase = Phase.SEMANTIC;
        rootNode = GetAstTree();
        rootNode.visit();
    }

    /** 第二遍：生成 IR（假设已无语义错误） */
    public static void GenerateLLVMIR() {
        phase = Phase.IR;

        // 1) 重置 IR 全局状态：module、编号计数器、builder 上下文等
        IrGenerator.reset();

        // 2) 第二遍会复用第一遍建立好的符号表树：
        //    必须把 currentSymbolTable 拉回 root，并复位每个 SymbolTable 的 son index 游标
        SymbolManager.GoBackToRootSymbolTable();
        SymbolManager.ResetSonTableIterators();

        // 3) 再走一遍 AST：这一次各节点的 visit 才应该生成 IR
        if (rootNode == null) {
            rootNode = GetAstTree();
        }
        rootNode.visit();
    }
}
