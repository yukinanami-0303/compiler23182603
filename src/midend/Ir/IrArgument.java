package midend.Ir;

/**
 * 函数形参，在函数签名和函数体内被当作局部寄存器使用。
 */
public class IrArgument extends IrValue {

    public IrArgument(IrType type, String name) {
        super(type, name);
    }
}