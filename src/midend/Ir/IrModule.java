package midend.Ir;

import java.util.ArrayList;
import java.util.List;

/**
 * IrModule 是整个 LLVM IR 的根节点。
 * 负责维护全局变量和函数列表，并最终生成完整的 IR 文本。
 */
public class IrModule {

    /** 全局变量/常量等定义的原始 IR 行 */
    private final List<String> globalDefs = new ArrayList<>();

    /** 模块中包含的函数定义 */
    private final List<IrFunction> functions = new ArrayList<>();

    public IrModule() {
    }

    /**
     * 添加一条全局定义（必须是一整行合法的 LLVM IR，末尾不要加换行）
     */
    public void addGlobalDef(String irLine) {
        if (irLine != null && !irLine.isEmpty()) {
            globalDefs.add(irLine);
        }
    }

    /**
     * 向模块中添加一个函数
     */
    public void addFunction(IrFunction function) {
        if (function != null) {
            functions.add(function);
        }
    }

    public List<IrFunction> getFunctions() {
        return functions;
    }

    public List<String> getGlobalDefs() {
        return globalDefs;
    }

    /**
     * 生成整个模块的 LLVM IR 文本
     */
    public String emit() {
        StringBuilder sb = new StringBuilder();

        // 1. 必要的外部 IO 函数声明
        // 按题目要求，这些函数用于评测机的输入输出
        sb.append("declare i32 @getint()\n");
        sb.append("declare void @putint(i32)\n");
        sb.append("declare void @putch(i32)\n");
        sb.append("declare void @putstr(i8*)\n");
        sb.append("\n");

        // 2. 全局变量 / 常量
        for (String def : globalDefs) {
            sb.append(def).append("\n");
        }
        if (!globalDefs.isEmpty()) {
            sb.append("\n");
        }

        // 3. 函数定义
        for (IrFunction func : functions) {
            sb.append(func.emit()).append("\n");
        }

        return sb.toString();
    }
    /** 清空整个模块（全局定义 + 函数），用于编译新输入文件之前重置状态 */
    public void clear() {
        globalDefs.clear();
        functions.clear();
    }
}

