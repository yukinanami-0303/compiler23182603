package midend.Ir.IrValue;

import midend.Ir.IrType.IrType;
import midend.Ir.IrUse.IrUse;

import java.util.ArrayList;

public class IrValue {
    protected final IrType irType;
    protected final String irName;
    // 使用当前value的use关系，即使用该value的user列表
    protected final ArrayList<IrUse> useList;
    public IrValue(IrType irType, String irName) {
        this.irType = irType;
        this.irName = irName;
        this.useList = new ArrayList<>();
    }

    public static class IrGlobalValue {

    }

    public static class IrFunc {
    }
}
