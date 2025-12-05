package midend.Ir;

/**
 * 全局变量 / 全局常量。
 * 注意：作为 SSA 值，它的类型是“指向 valueType 的指针”（与 LLVM 一致），
 * 但在定义语句中打印的是 valueType。
 */
public class IrGlobalVariable extends IrValue {

    private final IrType valueType;
    private final boolean isConst;
    private final IrValue init;

    public IrGlobalVariable(String name, IrType valueType, boolean isConst, IrValue init) {
        super(IrType.pointerTo(valueType), name);
        this.valueType = valueType;
        this.isConst = isConst;
        this.init = init;
    }

    public IrType getValueType() {
        return valueType;
    }

    public boolean isConst() {
        return isConst;
    }

    public IrValue getInit() {
        return init;
    }

    @Override
    public String getRef() {
        return "@" + getName();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRef()).append(" = dso_local ");
        if (isConst) {
            sb.append("constant ");
        } else {
            sb.append("global ");
        }
        sb.append(valueType.toString()).append(" ");
        if (init == null) {
            sb.append("zeroinitializer");
        } else {
            sb.append(init.toString());
        }
        return sb.toString();
    }
}
