package midend.Ir;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * IrFactory 用于维护唯一的 IrModule，并提供统一的构造辅助方法，
 * 相当于一个全局的 IR “工厂”。
 *
 * 使用方式示例（后续在 AST 的 visit 中调用）：
 *   IrModule m = IrFactory.getModule();
 *   IrFunction f = IrFactory.getInstance().createFunction("i32", "main");
 *   IrBasicBlock entry = IrFactory.getInstance().createBasicBlock(f, "entry");
 *   entry.addInstruction("%1 = add i32 %a, %b");
 *   entry.addInstruction("ret i32 %1");
 *   String irText = m.emit();
 */
public class IrFactory {

    /** 单例实例 */
    private static final IrFactory INSTANCE = new IrFactory();

    /** 整个编译过程只维护一个模块 */
    private final IrModule module = new IrModule();

    /** 用于生成唯一的临时寄存器名，例如 %t0, %t1, ... */
    private final AtomicInteger tempId = new AtomicInteger(0);

    /** 用于生成唯一的基本块标签，例如 bb0, bb1, ... */
    private final AtomicInteger blockId = new AtomicInteger(0);
    private final java.util.Map<String, String> funcRetTypeMap = new java.util.HashMap<>();


    private IrFactory() {
        // 记录内建库函数的返回类型，供 UnaryExp.generateIr 使用
        funcRetTypeMap.put("getint", "i32");
        funcRetTypeMap.put("putint", "void");
        funcRetTypeMap.put("putch", "void");
        funcRetTypeMap.put("putstr", "void");
    }

    public static IrFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 直接获取全局唯一的模块对象
     */
    public static IrModule getModule() {
        return INSTANCE.module;
    }

    /**
     * 创建一个新的函数并加入模块
     *
     * @param retType 返回类型，如 "i32" 或 "void"
     * @param name    函数名（不加 @ 的裸名）
     */
    public IrFunction createFunction(String retType, String name) {
        IrFunction func = new IrFunction(retType, name);
        module.addFunction(func);
        funcRetTypeMap.put(name, retType);  // 记录函数返回类型
        return func;
    }


    /**
     * 在指定函数下创建一个基本块。
     * 如果 prefix 为空，则自动使用 "bb" 前缀。
     *
     * @param func   所属函数
     * @param prefix 标签前缀（如 "entry"、"if_true" 等）
     */
    public IrBasicBlock createBasicBlock(IrFunction func, String prefix) {
        if (func == null) {
            throw new IllegalArgumentException("func is null when creating basic block");
        }
        String base = (prefix == null || prefix.isEmpty()) ? "bb" : prefix;
        String label = base;
        // 为避免同名，统一在末尾加一个序号
        int id = blockId.getAndIncrement();
        if (id > 0) {
            label = base + "." + id;
        }
        IrBasicBlock block = new IrBasicBlock(label);
        func.addBasicBlock(block);
        return block;
    }

    /**
     * 生成一个新的 SSA 临时寄存器名，如 "%t0"
     */
    public String newTemp() {
        int id = tempId.getAndIncrement();
        return "%t" + id;
    }


    public String getFuncRetType(String name) {
        String t = funcRetTypeMap.get(name);
        // 默认当作 i32（比如前向调用或没记录到的情况）
        return (t != null) ? t : "i32";
    }
    /**
     * 如需从头重新生成 IR，可以在编译新 testfile.txt 前调用。
     * 目前只重置计数器；模块清空可以在 IrModule 里新增 clear() 再在此调用。
     */
    public void reset() {
        tempId.set(0);
        blockId.set(0);
        module.clear();
        funcRetTypeMap.clear();
    }



}
