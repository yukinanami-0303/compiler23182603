package midend.Ir.IrUse;

import midend.Ir.IrType.IrType;
import midend.Ir.IrValue.IrValue;

import java.util.ArrayList;

public class IrUser extends IrValue {
    // 使用的value的list
    protected final ArrayList<IrValue> useValueList;
    public IrUser(IrType valueType, String name) {
        super(valueType, name);
        this.useValueList = new ArrayList<>();
    }

    public void AddUseValue(IrValue irValue) {
        this.useValueList.add(irValue);
        // 在添加的同时登记Use关系
        if (irValue != null) {
            irValue.AddUse(new IrUse(this, irValue));
        }
    }


}
