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

    public String emit() {
        StringBuilder sb = new StringBuilder();

        // ===== 函数头 =====
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

        // ===== 第一步：先收集所有被 br 指令引用到的 label =====
        java.util.Set<String> usedLabels = new java.util.HashSet<>();

        for (IrBasicBlock block : basicBlocks) {
            java.util.List<String> insts = block.getInstructions();
            if (insts == null) {
                continue;
            }
            for (String inst : insts) {
                if (inst == null) {
                    continue;
                }
                String trim = inst.trim();
                // 只关心 br 开头的指令
                if (!trim.startsWith("br ")) {
                    continue;
                }

                // 形如：
                //   br label %xxx
                //   br i1 %t, label %xxx, label %yyy
                int idx = trim.indexOf("label %");
                while (idx >= 0) {
                    int start = idx + "label %".length();
                    int end = start;
                    while (end < trim.length()) {
                        char ch = trim.charAt(end);
                        if (Character.isLetterOrDigit(ch) || ch == '.' || ch == '_') {
                            end++;
                        } else {
                            break;
                        }
                    }
                    if (end > start) {
                        String label = trim.substring(start, end);
                        usedLabels.add(label);
                    }
                    idx = trim.indexOf("label %", end);
                }
            }
        }

        // ===== 第二步：给所有“需要存在的 block”补上终结指令 =====
        for (IrBasicBlock block : basicBlocks) {
            java.util.List<String> insts = block.getInstructions();
            if (insts == null) {
                continue;
            }

            // 空并且从来没有 br 跳到它的 block，会在下面直接被过滤掉
            if (insts.isEmpty() && !usedLabels.contains(block.getLabel())) {
                continue;
            }

            // 找到最后一条“非空指令”
            int lastIdx = insts.size() - 1;
            while (lastIdx >= 0) {
                String s = insts.get(lastIdx);
                if (s != null && !s.trim().isEmpty()) {
                    break;
                }
                lastIdx--;
            }

            if (lastIdx < 0) {
                // 说明这是一个“被跳到但逻辑上完全空”的块，需要至少有个 ret
                if ("void".equals(retType)) {
                    insts.add("  ret void");
                } else {
                    insts.add("  ret " + retType + " 0");
                }
                continue;
            }

            String last = insts.get(lastIdx).trim();
            // 如果已经以 br / ret 结尾，就不用补
            if (last.startsWith("br ") || last.startsWith("ret ")) {
                continue;
            }

            // 否则补一个默认 ret
            if ("void".equals(retType)) {
                insts.add("  ret void");
            } else {
                insts.add("  ret " + retType + " 0");
            }
        }

        // ===== 第三步：输出 block，继续过滤“空死块” =====
        for (IrBasicBlock block : basicBlocks) {
            java.util.List<String> insts = block.getInstructions();
            boolean isEmpty = (insts == null || insts.isEmpty());

            // 完全空、且没有任何 br 跳到它的 block，认为是死块，直接不打印
            if (isEmpty && !usedLabels.contains(block.getLabel())) {
                continue;
            }

            sb.append(block.emit());
        }

        sb.append("}\n");
        return sb.toString();
    }


}
