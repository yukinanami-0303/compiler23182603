package midend.Ir;

/**
 * 从指针中读值。
 * %x = load i32, i32* %p
 */
public class LoadInst extends IrInstruction {

    private final IrValue pointer;

    public LoadInst(String name, IrType resultType, IrValue pointer) {
        super(resultType, name);
        this.pointer = pointer;
    }

    public IrValue getPointer() {
        return pointer;
    }

    @Override
    public String format() {
        return resultPrefix() + "load " +
                getType().toString() + ", " +
                pointer.getType().toString() + " " +
                pointer.getRef();
    }
}