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
        this.depth = depth;//当前作用域标号
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
        }
        // 当前层有相同名，重定义，报错为b
        else {
            addError(line,"b");
        }
    }
    public SymbolTable GetNextSonTable() {
        return this.sonTables.get(++index);
    }
    public String OutputSymbolTable() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : symbolList) {
            stringBuilder.append(depth + " " + symbol.GetSymbolName() + " " + symbol.GetSymbolType() + "\n");
        }
        for (SymbolTable sonTable : sonTables) {
            stringBuilder.append(sonTable.OutputSymbolTable());
        }
        return stringBuilder.toString();
    }
}
