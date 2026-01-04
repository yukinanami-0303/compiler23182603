package midend.Ir;

/**
 * 把 IrModule 转成 LLVM IR 文本的后端入口。
 * 真正“造 IR”的逻辑在 AST 的 visit 中（第二遍 IR 阶段）。
 */
public class IrGenerator {

    /**
     * 第二遍 IR 开始前，统一清空 IR 全局状态
     */
    public static void reset() {
        IrFactory.getInstance().reset();
        IrBuilder.reset();
    }

    /**
     * 输出 LLVM IR 文本（由 Compiler 调用写入 llvm_ir.txt）
     * 注意：必须在 MidEnd.GenerateLLVMIR() 完成后调用
     */
    public static String generate() {
        IrModule module = IrFactory.getModule();
        return module.emit();
    }
}
