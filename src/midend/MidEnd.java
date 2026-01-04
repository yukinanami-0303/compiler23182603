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

        // ✅ 必须先初始化符号表根节点和当前指针，否则 CreateSonSymbolTable 会 NPE
        SymbolManager.Init();

        rootNode = GetAstTree();
        rootNode.visit();
    }

    /** 第二遍：生成 IR（假设已无语义错误） */
    public static void GenerateLLVMIR() {
        phase = Phase.IR;

        // 1) 重置 IR 全局状态
        IrGenerator.reset();

        // 2) 复用第一遍建立好的符号表树：回到 root，并复位子表遍历游标
        SymbolManager.GoBackToRootSymbolTable();
        SymbolManager.ResetSonTableIterators();

        // 3) 再走一遍 AST：这一次各节点的 visit 只生成 IR
        if (rootNode == null) {
            rootNode = GetAstTree();
        }
        rootNode.visit();
    }
}
