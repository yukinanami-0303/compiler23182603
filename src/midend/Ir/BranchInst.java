package midend.Ir;

/**
 * br label %L        （无条件跳转）
 * br i1 %cond, label %T, label %F  （条件跳转）
 */
public class BranchInst extends IrInstruction {

    private final IrValue condition;      // null 表示无条件跳转
    private final IrBasicBlock trueBlock;
    private final IrBasicBlock falseBlock; // 对无条件跳转为 null

    // 无条件跳转
    public BranchInst(IrBasicBlock target) {
        super(IrType.VOID, null);
        this.condition = null;
        this.trueBlock = target;
        this.falseBlock = null;
    }

    // 条件跳转
    public BranchInst(IrValue condition, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        super(IrType.VOID, null);
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    @Override
    public String format() {
        if (condition == null) {
            return "br label %" + trueBlock.getLabel();
        }
        return "br i1 " + condition.getRef() +
                ", label %" + trueBlock.getLabel() +
                ", label %" + falseBlock.getLabel();
    }
}
