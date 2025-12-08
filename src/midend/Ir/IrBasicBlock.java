package midend.Ir;

import java.util.ArrayList;
import java.util.List;

/**
 * IrBasicBlock 表示一个基本块（label + 指令列表）。
 * 这里只做文本级别的 LLVM IR 维护，每条指令是一行原始 IR 文本。
 */
public class IrBasicBlock {

    /** 基本块标签，不包含末尾的冒号 */
    private final String label;

    /** 指令列表，每个元素是一整行 LLVM IR 文本，不包含前导缩进和换行 */
    private final List<String> instructions = new ArrayList<>();

    public IrBasicBlock(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    /**
     * 向基本块中添加一条指令
     */
    public void addInstruction(String inst) {
        if (inst != null && !inst.isEmpty()) {
            instructions.add(inst);
        }
    }

    /**
     * 生成当前基本块的 LLVM IR 文本
     */
    public String emit() {
        StringBuilder sb = new StringBuilder();
        // 基本块标签行
        sb.append(this.label).append(":\n");
        // 每条指令前加两个空格作为缩进
        for (String inst : instructions) {
            sb.append("  ").append(inst).append("\n");
        }
        return sb.toString();
    }
}
