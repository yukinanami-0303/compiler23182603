package midend.Ir;

/**
 * 二元算术运算：add/sub/mul/sdiv/srem。
 */
public class BinaryInst extends IrInstruction {

    public enum Op {
        ADD("add"),
        SUB("sub"),
        MUL("mul"),
        SDIV("sdiv"),
        SREM("srem");

        private final String irName;

        Op(String irName) {
            this.irName = irName;
        }

        public String getIrName() {
            return irName;
        }
    }

    private final Op op;
    private final IrValue lhs;
    private final IrValue rhs;

    public BinaryInst(String name, Op op, IrValue lhs, IrValue rhs) {
        // 默认 lhs / rhs 类型相同
        super(lhs.getType(), name);
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Op getOp() {
        return op;
    }

    @Override
    public String format() {
        return resultPrefix() + op.getIrName() + " " +
                getType().toString() + " " +
                lhs.getRef() + ", " + rhs.getRef();
    }
}
