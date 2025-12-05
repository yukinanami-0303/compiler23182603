package midend.Ir;

/**
 * 在栈上分配局部变量。
 * %x = alloca i32
 */
public class AllocaInst extends IrInstruction {

    private final IrType allocType;

    public AllocaInst(String name, IrType allocType) {
        super(IrType.pointerTo(allocType), name);
        this.allocType = allocType;
    }

    public IrType getAllocType() {
        return allocType;
    }

    @Override
    public String format() {
        return resultPrefix() + "alloca " + allocType.toString();
    }
}
