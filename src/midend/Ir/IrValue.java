package midend.Ir;

public abstract class IrValue {

    private final IrType type;
    private final String name;

    protected IrValue(IrType type, String name) {
        this.type = type;
        this.name = name;
    }

    public IrType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * 缺省认为是局部寄存器值，用 %name 引用。
     * 常量 / 全局变量等子类会重写这个方法。
     */
    public String getRef() {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Unnamed value cannot be referenced");
        }
        return "%" + name;
    }

    @Override
    public String toString() {
        return getRef();
    }
}
