package midend.Ir;

/**
 * 返回指令：
 *   ret void
 *   ret i32 %v
 */
public class RetInst extends IrInstruction {

    private final IrValue value; // null 表示 void

    public RetInst(IrValue value) {
        super(value == null ? IrType.VOID : value.getType(), null);
        this.value = value;
    }

    public IrValue getValue() {
        return value;
    }

    @Override
    public String format() {
        if (value == null) {
            return "ret void";
        }
        return "ret " + value.getType().toString() + " " + value.getRef();
    }
}
