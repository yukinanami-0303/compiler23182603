package midend.Ir;

import java.util.List;

/**
 * 函数调用：
 *   %t = call i32 @foo(i32 %a, i32 %b)
 *   call void @putint(i32 %x)
 */
public class CallInst extends IrInstruction {

    private final String funcName;
    private final List<IrValue> args;

    public CallInst(String name, IrType retType, String funcName, List<IrValue> args) {
        super(retType, name);
        this.funcName = funcName;
        this.args = args;
    }

    @Override
    public String format() {
        StringBuilder argsSb = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) {
                argsSb.append(", ");
            }
            IrValue v = args.get(i);
            argsSb.append(v.getType().toString())
                    .append(" ")
                    .append(v.getRef());
        }

        boolean hasResult = getType() != IrType.VOID &&
                getName() != null && !getName().isEmpty();

        StringBuilder sb = new StringBuilder();
        if (hasResult) {
            sb.append(resultPrefix());
        }
        sb.append("call ");
        if (getType() == IrType.VOID) {
            sb.append("void");
        } else {
            sb.append(getType().toString());
        }
        sb.append(" @").append(funcName)
                .append("(").append(argsSb).append(")");
        return sb.toString();
    }
}
