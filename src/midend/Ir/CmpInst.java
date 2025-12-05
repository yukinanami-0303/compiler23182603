package midend.Ir;

/**
 * 整型比较指令：icmp eq/ne/slt/sle/sgt/sge。
 */
public class CmpInst extends IrInstruction {

    public enum Cond {
        EQ("eq"),
        NE("ne"),
        SLT("slt"),
        SLE("sle"),
        SGT("sgt"),
        SGE("sge");

        private final String irName;

        Cond(String irName) {
            this.irName = irName;
        }

        public String getIrName() {
            return irName;
        }
    }

    private final Cond cond;
    private final IrValue lhs;
    private final IrValue rhs;

    public CmpInst(String name, Cond cond, IrValue lhs, IrValue rhs) {
        super(IrType.I1, name);
        this.cond = cond;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String format() {
        return resultPrefix() + "icmp " +
                cond.getIrName() + " " +
                lhs.getType().toString() + " " + lhs.getRef() + ", " + rhs.getRef();
    }
}
