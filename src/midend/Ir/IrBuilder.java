package midend.Ir;
import java.util.ArrayDeque;
import java.util.Deque;
/**
 * 维护当前正在生成 IR 的函数和基本块的简单上下文。
 * 目前只支持顺序控制流：只有一个当前 basic block。
 */
public class IrBuilder {

    /** 当前正在生成 IR 的函数 */
    private static IrFunction currentFunction = null;

    /** 当前正在生成 IR 的基本块 */
    private static IrBasicBlock currentBlock = null;

    /** 当前嵌套循环的 break 目标基本块栈（最上层是最近的那一层循环） */
    private static final Deque<IrBasicBlock> breakTargetStack = new ArrayDeque<>();

    /** 当前嵌套循环的 continue 目标基本块栈 */
    private static final Deque<IrBasicBlock> continueTargetStack = new ArrayDeque<>();


    /**
     * 进入一个函数：创建 IrFunction 和 entry 基本块，并设置为当前上下文。
     *
     * @param retType 返回类型，如 "i32" 或 "void"
     * @param name    函数名（不带 @）
     */
    public static void enterFunction(String retType, String name) {
        IrFactory factory = IrFactory.getInstance();
        currentFunction = factory.createFunction(retType, name);
        currentBlock = factory.createBasicBlock(currentFunction, "entry");
    }

    /** 离开当前函数（简单清空上下文） */
    public static void leaveFunction() {
        currentFunction = null;
        currentBlock = null;
    }

    public static IrFunction getCurrentFunction() {
        return currentFunction;
    }

    public static IrBasicBlock getCurrentBlock() {
        return currentBlock;
    }

    public static void setCurrentBlock(IrBasicBlock block) {
        currentBlock = block;
    }
    /**
     * 进入一个循环时调用：记录本层循环的 break / continue 目标基本块。
     * breakTarget：循环结束后要跳转到的块（for-end）
     * continueTarget：执行 continue 时要跳转到的块（通常是条件判断或步进块）
     */
    public static void pushLoop(IrBasicBlock breakTarget, IrBasicBlock continueTarget) {
        breakTargetStack.push(breakTarget);
        continueTargetStack.push(continueTarget);
    }

    /**
     * 退出当前循环时调用：弹出一层循环上下文。
     */
    public static void popLoop() {
        if (!breakTargetStack.isEmpty()) {
            breakTargetStack.pop();
        }
        if (!continueTargetStack.isEmpty()) {
            continueTargetStack.pop();
        }
    }

    /**
     * 获取当前最近一层循环的 break 目标块。
     */
    public static IrBasicBlock getCurrentBreakTarget() {
        return breakTargetStack.isEmpty() ? null : breakTargetStack.peek();
    }

    /**
     * 获取当前最近一层循环的 continue 目标块。
     */
    public static IrBasicBlock getCurrentContinueTarget() {
        return continueTargetStack.isEmpty() ? null : continueTargetStack.peek();
    }
}

