package midend.Symbol;

public class Symbol {
    private final String symbolName;
    private final String symbolType;
    public Symbol(String symbolName, String symbolType) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
    }
    public String GetSymbolName() {
        return this.symbolName;
    }

    public String GetSymbolType() {
        return this.symbolType;
    }

}
