package midend.Symbol;

import java.util.ArrayList;

public class ValueSymbol extends Symbol{
    private boolean isArrayParam = false;  // 是否为函数形参数组
    private String irParamName = null;     // 对应 IR 参数名，例如 "%arg.a"
    private String irName = null;
    private int arrayLength = -1;
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
    public void SetIsArrayParam(boolean is){
        this.isArrayParam=is;
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
    public void SetArrayLength(int len) {
        this.arrayLength = len;
    }
    public String GetIrParamName() { return this.irParamName; }

    public boolean IsArrayParam() { return this.isArrayParam; }
    public void SetIrParamName(String n) { this.irParamName = n; }
    public int GetArrayLength() {
        return this.arrayLength;
    }
    public ArrayList<Integer> GetValueList() {
        return this.valueList;
    }
    public void SetIrName(String irName) {
        this.irName = irName;
    }

    public String GetIrName() {
        return this.irName;
    }
}
