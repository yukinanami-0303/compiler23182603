package midend.Ir.Const;

import midend.Ir.IrType.IrType;
import midend.Ir.IrValue.IrValue;

public abstract class IrConst extends IrValue {
    public IrConst(IrType irType, String irName){
        super(irType, irName);
    }

    public static class IrConstString extends IrConst {
        private final String stringValue;

        public IrConstString(String stringValue,String name) {
            super(new IrPointerType(new IrArrayType(GetStringLength(stringValue), IrBaseType.INT8)), name);
            this.stringValue = stringValue;
        }
    }
}
