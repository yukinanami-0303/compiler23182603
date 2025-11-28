package midend.Symbol;

public class SymbolManager {
    private static SymbolTable rootSymbolTable;
    private static SymbolTable currentSymbolTable;
    private static int depth;

    private static int forDepth;
    private static String funcReturnType = "";

    public static void Init() {
        depth = 1;
        rootSymbolTable = new SymbolTable(depth, null);
        currentSymbolTable = rootSymbolTable;

        forDepth = 0;
        funcReturnType = "";
    }

    public static void GoBackToRootSymbolTable() {
        currentSymbolTable = rootSymbolTable;
    }

    public static boolean IsGlobal() {
        return currentSymbolTable == rootSymbolTable;
    }

    public static void AddSymbol(Symbol symbol, int line) {
        currentSymbolTable.AddSymbol(symbol, line);
        if (symbol instanceof ValueSymbol valueSymbol) {
            valueSymbol.SetIsGlobal(IsGlobal());
        }
    }

    public static Symbol GetSymbol(String name) {
        SymbolTable currenttable = currentSymbolTable;
        while (currenttable != null) {
            Symbol symbol = currenttable.GetSymbol(name);
            if (symbol != null) {
                return symbol;
            }
            currenttable = currenttable.GetFatherTable();
        }
        return null;
    }

    public static Symbol GetSymbolFromFather(String symbolname) {
        SymbolTable currenttable = currentSymbolTable.GetFatherTable();
        while (currenttable != null) {
            Symbol symbol = currenttable.GetSymbol(symbolname);
            if (symbol != null) {
                return symbol;
            }
            currenttable = currenttable.GetFatherTable();
        }
        return null;
    }

    public static SymbolTable GetSymbolTable() {
        return rootSymbolTable;
    }

    public static void GoToFatherSymbolTable() {
        SymbolTable fatherTable = currentSymbolTable.GetFatherTable();
        if (fatherTable != null) {
            currentSymbolTable = fatherTable;
        }
    }

    public static void CreateSonSymbolTable() {
        SymbolTable sonTable = new SymbolTable(++depth, currentSymbolTable);
        currentSymbolTable.AddSonTable(sonTable);
        currentSymbolTable = sonTable;
    }

    public static void GoToSonSymbolTable() {
        currentSymbolTable = currentSymbolTable.GetNextSonTable();
    }

    public static void EnterForBlock() {
        forDepth++;
    }

    public static void LeaveForBlock() {
        forDepth--;
    }

    public static boolean NotInForBlock() {
        return !(forDepth > 0);
    }

    public static void EnterFunc(String type) {
        funcReturnType = type;
    }

    public static void LeaveFunc() {
        funcReturnType = "";
    }

    public static String GetFuncType() {
        return funcReturnType;
    }
}
