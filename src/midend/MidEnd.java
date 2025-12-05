package midend;

import frontend.ast.CompUnit;
import midend.Symbol.SymbolManager;
import midend.Symbol.SymbolTable;
import midend.Ir.IrFactory;   // 新增 import

import static OutputHelper.OutputHelper.write;
import static frontend.Parser.GetAstTree;
import static midend.Symbol.SymbolManager.GetSymbolTable;

public class MidEnd {
    private static CompUnit rootNode;
    public static void GenerateSymbolTable() {
        SymbolManager.Init();
        IrFactory.getInstance().reset();   // 新增：重置 IR 工厂

        rootNode = GetAstTree();
        rootNode.visit();                  // 在 visit 过程中顺便构建 IR

        SymbolTable rootSymbolTable = GetSymbolTable();
        write(rootSymbolTable.OutputSymbolTable());
        SymbolManager.GoBackToRootSymbolTable();
    }
}
