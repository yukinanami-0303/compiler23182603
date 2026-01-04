package midend.Ir;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 维护当前正在生成 IR 的函数和基本块的简单上下文。
 */
public class IrBuilder {

    /** 当前正在生成 IR 的函数 */
    private static IrFunction currentFunction = null;

    /** 当前正在生成 IR 的基本块 */
    private static IrBasicBlock currentBlock = null;

    /** 当前嵌套循环的 break 目标基本块栈 */
    private static final Deque<IrBasicBlock> breakTargetStack = new ArrayDeque<>();

    /** 当前嵌套循环的 continue 目标基本块栈 */
    private static final Deque<IrBasicBlock> continueTargetStack = new ArrayDeque<>();

    /**
     * 重置 Builder 上下文：用于第二遍 IR 生成开始前
     */
    public static void reset() {
        currentFunction = null;
        currentBlock = null;
        breakTargetStack.clear();
        continueTargetStack.clear();
    }

    /**
     * 进入一个函数：创建 IrFunction 和 entry 基本块，并设置为当前上下文。
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

    /** 进入循环：记录 break/continue 目标块 */
    public static void pushLoop(IrBasicBlock breakTarget, IrBasicBlock continueTarget) {
        breakTargetStack.push(breakTarget);
        continueTargetStack.push(continueTarget);
    }

    /** 退出循环：弹出一层循环上下文 */
    public static void popLoop() {
        if (!breakTargetStack.isEmpty()) breakTargetStack.pop();
        if (!continueTargetStack.isEmpty()) continueTargetStack.pop();
    }

    public static IrBasicBlock getCurrentBreakTarget() {
        return breakTargetStack.isEmpty() ? null : breakTargetStack.peek();
    }

    public static IrBasicBlock getCurrentContinueTarget() {
        return continueTargetStack.isEmpty() ? null : continueTargetStack.peek();
    }
}
