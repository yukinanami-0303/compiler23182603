package midend.Ir.Const;

import midend.Ir.IrType.IrType;
import midend.Ir.IrValue.IrValue;

public abstract class IrConst extends IrValue {
    public IrConst(IrType irType, String irName){
        super(irType, irName);
    }
}
