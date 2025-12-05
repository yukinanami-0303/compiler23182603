package midend.Ir;

import java.util.ArrayList;
import java.util.List;

/**
 * IrFunction 表示一个函数定义。
 * 只关心函数头（返回类型、名称、形参列表）和包含的基本块列表。
 *
 * 例子：
 *   define i32 @main(i32 %a, i32 %b) {
 *   entry:
 *     %1 = add i32 %a, %b
 *     ret i32 %1
 *   }
 */
public class IrFunction {

    /** 函数返回类型（如 "i32" 或 "void"） */
    private final String retType;

    /** 函数名（不包含 '@'，打印时自动加上） */
    private final String name;

    /** 形参列表，每个元素是完整的 "i32 %x" 形式 */
    private final List<String> params = new ArrayList<>();

    /** 函数体由若干基本块组成，按顺序依次打印 */
    private final List<IrBasicBlock> basicBlocks = new ArrayList<>();

    public IrFunction(String retType, String name) {
        this.retType = retType;
        this.name = name;
    }

    public String getRetType() {
        return retType;
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }

    public List<IrBasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    /**
     * 添加一个形参。例如：
     *   addParam("i32", "%x");
     * 打印时会变成 "i32 %x"
     */
    public void addParam(String type, String name) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("param type is empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("param name is empty");
        }
        params.add(type + " " + name);
    }

    /**
     * 手动添加已经拼好的形参字符串（如 "i32 %x"）
     */
    public void addRawParam(String paramIr) {
        if (paramIr != null && !paramIr.isEmpty()) {
            params.add(paramIr);
        }
    }

    /**
     * 向函数中添加一个基本块
     */
    public void addBasicBlock(IrBasicBlock block) {
        if (block != null) {
            basicBlocks.add(block);
        }
    }

    /**
     * 便捷方法：在当前函数下创建一个基本块并自动加入 basicBlocks
     */
    public IrBasicBlock newBasicBlock(String label) {
        IrBasicBlock block = new IrBasicBlock(label);
        basicBlocks.add(block);
        return block;
    }

    /**
     * 生成当前函数的 LLVM IR 文本
     */
    public String emit() {
        StringBuilder sb = new StringBuilder();

        // 函数头
        sb.append("define ")
                .append(retType)
                .append(" @")
                .append(name)
                .append("(");

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i));
            if (i + 1 < params.size()) {
                sb.append(", ");
            }
        }
        sb.append(") {\n");

        // 函数体
        for (IrBasicBlock block : basicBlocks) {
            sb.append(block.emit());
        }

        sb.append("}\n");
        return sb.toString();
    }
}
