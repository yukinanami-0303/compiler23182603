package midend.Symbol;

import java.util.ArrayList;

public class ValueSymbol extends Symbol{

    private boolean isGlobal;
    private int size;
    private boolean isConst;
    private final ArrayList<Integer> initValueList;
    private ArrayList<Integer> valueList;
    public ValueSymbol(String symbolName, String symbolType) {
        super(symbolName, symbolType);
        this.size=0;
        this.isGlobal = false;
        this.isConst = false;
        this.initValueList = new ArrayList<>();
        this.valueList = new ArrayList<>();
    }
    public ValueSymbol(String symbolName, String symbolType,ArrayList<Integer> initValueList) {
        super(symbolName, symbolType);
        this.size=0;
        this.isGlobal = false;
        this.isConst = false;
        this.initValueList = initValueList == null ? new ArrayList<>() : initValueList;
        this.valueList = new ArrayList<>();
    }
    public void SetIsGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }
    public boolean IsGlobal() {
        return this.isGlobal;
    }
    public void SetIsConst(boolean isConst) {
        this.isConst = isConst;
    }

    public boolean IsConst() {
        return this.isConst;
    }
    public ArrayList<Integer> GetInitValueList() {
        return this.initValueList;
    }
    public void SetValueList(ArrayList<Integer> valueList) {
        this.valueList = valueList;
    }

    public ArrayList<Integer> GetValueList() {
        return this.valueList;
    }
}
