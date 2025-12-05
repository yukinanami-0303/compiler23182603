package midend.Ir;

/**
 * 整型常量 i32 N。
 */
public  class IrConstantInt extends IrValue {

    private final int value;

    public IrConstantInt(int value) {
        super(IrType.I32, null);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getRef() {
        return Integer.toString(value);
    }

    @Override
    public String toString() {
        return getRef();
    }
}
