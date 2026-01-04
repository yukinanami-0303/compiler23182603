package midend.Ir;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * IrFactory 用于维护唯一的 IrModule，并提供统一的构造辅助方法，
 * 相当于一个全局的 IR “工厂”。
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

    /** 记录函数返回类型，供调用表达式等查询 */
    private final java.util.Map<String, String> funcRetTypeMap = new java.util.HashMap<>();

    private IrFactory() {
        initBuiltinFuncRetTypes();
    }

    private void initBuiltinFuncRetTypes() {
        // 内建库函数的返回类型
        funcRetTypeMap.put("getint", "i32");
        funcRetTypeMap.put("putint", "void");
        funcRetTypeMap.put("putch", "void");
        funcRetTypeMap.put("putstr", "void");
    }

    public static IrFactory getInstance() {
        return INSTANCE;
    }

    /** 直接获取全局唯一的模块对象 */
    public static IrModule getModule() {
        return INSTANCE.module;
    }

    /**
     * 创建一个新的函数并加入模块
     * @param retType 返回类型，如 "i32" 或 "void"
     * @param name    函数名（不加 @ 的裸名）
     */
    public IrFunction createFunction(String retType, String name) {
        IrFunction func = new IrFunction(retType, name);
        module.addFunction(func);
        funcRetTypeMap.put(name, retType);
        return func;
    }

    /**
     * 在指定函数下创建一个基本块。
     * 如果 prefix 为空，则自动使用 "bb" 前缀。
     */
    public IrBasicBlock createBasicBlock(IrFunction func, String prefix) {
        if (func == null) {
            throw new IllegalArgumentException("func is null when creating basic block");
        }
        String base = (prefix == null || prefix.isEmpty()) ? "bb" : prefix;
        String label = base;
        int id = blockId.getAndIncrement();
        if (id > 0) {
            label = base + "." + id;
        }
        IrBasicBlock block = new IrBasicBlock(label);
        func.addBasicBlock(block);
        return block;
    }

    /** 生成一个新的 SSA 临时寄存器名，如 "%t0" */
    public String newTemp() {
        int id = tempId.getAndIncrement();
        return "%t" + id;
    }

    public String getFuncRetType(String name) {
        String t = funcRetTypeMap.get(name);
        // 默认当作 i32
        return (t != null) ? t : "i32";
    }

    /**
     * 从头重新生成 IR：清空 module + 重置计数器 + 恢复内建函数表
     */
    public void reset() {
        tempId.set(0);
        blockId.set(0);
        module.clear();

        funcRetTypeMap.clear();
        initBuiltinFuncRetTypes();
    }
}
