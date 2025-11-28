package midend;

import frontend.ast.CompUnit;
import midend.Symbol.SymbolManager;
import midend.Symbol.SymbolTable;


import static OutputHelper.OutputHelper.write;
import static frontend.Parser.GetAstTree;
import static midend.Symbol.SymbolManager.GetSymbolTable;

public class MidEnd {
    private static CompUnit rootNode;
    public static void GenerateSymbolTable() {
        SymbolManager.Init();
        rootNode = GetAstTree();
        rootNode.visit();
        SymbolTable rootSymbolTable = GetSymbolTable();
        write(rootSymbolTable.OutputSymbolTable());
        SymbolManager.GoBackToRootSymbolTable();
    }


}
