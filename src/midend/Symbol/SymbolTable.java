package midend.Symbol;

import java.util.ArrayList;
import java.util.Hashtable;

import static Error.ErrorHandler.addError;

public class SymbolTable {
    private final int depth;
    private int index;

    private final ArrayList<Symbol> symbolList;
    private final Hashtable<String, Symbol> symbolTable;

    private final SymbolTable fatherTable;
    private final ArrayList<SymbolTable> sonTables;

    public SymbolTable(int depth, SymbolTable fatherTable) {
        this.depth = depth;
        this.index = -1;

        this.symbolList = new ArrayList<>();
        this.symbolTable = new Hashtable<>();

        this.fatherTable = fatherTable;
        this.sonTables = new ArrayList<>();
    }

    public Symbol GetSymbol(String symbolName) {
        return this.symbolTable.get(symbolName);
    }

    public SymbolTable GetFatherTable() {
        return this.fatherTable;
    }

    public void AddSonTable(SymbolTable symbolTable) {
        this.sonTables.add(symbolTable);
    }

    public void AddSymbol(Symbol symbol, int line) {
        String symbolName = symbol.GetSymbolName();
        if (!this.symbolTable.containsKey(symbolName)) {
            this.symbolList.add(symbol);
            this.symbolTable.put(symbolName, symbol);
        } else {
            addError(line, "b");
        }
    }

    public SymbolTable GetNextSonTable() {
        return this.sonTables.get(++index);
    }

    /**
     * 第二遍遍历前复位“子表遍历游标”，保证 GoToSonSymbolTable 从第 0 个儿子开始走
     */
    public void ResetSonIteratorRecursively() {
        this.index = -1;
        for (SymbolTable son : sonTables) {
            son.ResetSonIteratorRecursively();
        }
    }

    public String OutputSymbolTable() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : symbolList) {
            stringBuilder.append(depth).append(" ")
                    .append(symbol.GetSymbolName()).append(" ")
                    .append(symbol.GetSymbolType()).append("\n");
        }
        for (SymbolTable sonTable : sonTables) {
            stringBuilder.append(sonTable.OutputSymbolTable());
        }
        return stringBuilder.toString();
    }
}
