package midend.Ir;

import midend.Ir.Const.IrConstString;
import midend.Ir.IrValue.IrValue;

import java.util.ArrayList;
import java.util.HashMap;

public class IrModule {
    // 库函数声明
    private final ArrayList<String> declares;
    // 字符串
    private final HashMap<String, IrConstString> stringConstMap;
    // 全局变量
    private final ArrayList<IrValue.IrGlobalValue> globalValues;
    // 一切运行对象皆为function，包括main
    private final ArrayList<IrValue.IrFunction> funcs;
    public IrModule() {
        this.declares = new ArrayList<>();
        this.stringConstMap = new HashMap<>();
        this.globalValues = new ArrayList<>();
        this.funcs = new ArrayList<>();

        this.declares.add("declare i32 @getint()");
        this.declares.add("declare void @putch(i32) ");
        this.declares.add("declare void @putstr(i8*)");
        this.declares.add("declare void @putint(i32)");
    }
    public void Check() {
        for (IrValue.IrFunction irFunction : this.funcs) {
            irFunction.CheckNoEmptyBlock();
        }
    }
    public void AddIrGlobalValue(IrValue.IrGlobalValue globalValue) {
        this.globalValues.add(globalValue);
    }

    public void AddIrFunction(IrValue.IrFunction function) {
        this.funcs.add(function);
    }
}
