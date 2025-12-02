package midend.Ir.Const;

public class IrConstString extends IrConst {
    private final String stringValue;

    public IrConstString(String stringValue,String name) {
        super(new IrPointerType(new IrArrayType(GetStringLength(stringValue), IrBaseType.INT8)), name);
        this.stringValue = stringValue;
    }
}
