package midend.Ir;

/**
 * 向指针中写值。
 * store i32 %x, i32* %p
 */
public class StoreInst extends IrInstruction {

    private final IrValue value;
    private final IrValue pointer;

    public StoreInst(IrValue value, IrValue pointer) {
        super(IrType.VOID, null);
        this.value = value;
        this.pointer = pointer;
    }

    public IrValue getValue() {
        return value;
    }

    public IrValue getPointer() {
        return pointer;
    }

    @Override
    public String format() {
        return "store " +
                value.getType().toString() + " " + value.getRef() + ", " +
                pointer.getType().toString() + " " + pointer.getRef();
    }
}