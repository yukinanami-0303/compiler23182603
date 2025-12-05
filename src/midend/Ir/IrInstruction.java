package midend.Ir;

/**
 * 所有指令的基类。
 * type 为结果值类型（若指令无返回值则为 void），name 为结果寄存器名（可为 null）。
 */
public abstract class IrInstruction extends IrValue {

    protected IrInstruction(IrType type, String name) {
        super(type, name);
    }

    /**
     * 若该指令有结果寄存器，则返回 "%name = " 这一前缀，否则为空串。
     */
    protected String resultPrefix() {
        String name = getName();
        if (name == null || name.isEmpty()) {
            return "";
        }
        return "%" + name + " = ";
    }

    /**
     * 子类实现核心内容（不含前导两个空格）。
     */
    public abstract String format();

    @Override
    public String toString() {
        // 每条指令前面缩进两个空格，保持 LLVM IR 常用风格
        return "  " + format();
    }
}