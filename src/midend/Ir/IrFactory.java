package midend.Ir;

import midend.Ir.Const.IrConst;
import midend.Ir.Instr.Instr;
import midend.Ir.IrType.IrType;
import midend.Ir.IrValue.IrValue;

import java.util.HashMap;
import java.util.Stack;

public class IrFactory {
    private static final String GLOBAL_VAR_NAME_PREFIX = "@g_";
    private static final String STRING_LITERAL_NAME_PREFIX = "@s_";
    private static final String LOCAL_VAR_NAME_PREFIX = "%v";
    private static final String BasicBlock_NAME_PREFIX = "b_";
    private static final String FUNC_NAME_PREFIX = "@f_";

    private static IrModule currentModule = null;
    private static IrValue.IrBasicBlock currentBasicBlock = null;
    private static IrValue.IrFunction currentFunction = null;
    private static final Stack<IrValue.IrLoop> loopStack = new Stack<>();

    private static int basicBlockCount = 0;
    private static int globalVarNameCount = 0;
    private static int stringConstNameCount = 0;
    private static final HashMap<IrValue.IrFunction, Integer> localVarNameCountMap = new HashMap<>();

    public static void SetCurrentModule(IrModule irModule) {
        currentModule = irModule;
    }

    public static IrModule GetCurrentModule() {
        return currentModule;
    }

    public static void Check() {
        currentModule.Check();
    }

    public static IrValue.IrFunction GetNewFunctionIr(String name, IrType returnType) {
        // 创建新Function
        IrValue.IrFunction irFunction = new IrValue.IrFunction(GetFuncName(name), returnType);
        currentModule.AddIrFunction(irFunction);
        // 设置为当前处理的Function
        currentFunction = irFunction;
        // 为Function添加一个基础basic block
        IrValue.IrBasicBlock irBasicBlock = GetNewBasicBlockIr();
        // 设置当前的basic block
        currentBasicBlock = irBasicBlock;

        // 添加计数表
        localVarNameCountMap.put(irFunction, 0);

        return irFunction;
    }

    public static IrValue.IrBasicBlock GetNewBasicBlockIr() {
        IrValue.IrBasicBlock basicBlock = new IrValue.IrBasicBlock(GetBasicBlockName(), currentFunction);
        // 添加到当前的处理中
        currentFunction.AddBasicBlock(basicBlock);

        return basicBlock;
    }

    public static IrValue.IrBasicBlock GetNewBasicBlockIr(IrValue.IrFunction irFunction, IrValue.IrBasicBlock afterBlock) {
        IrValue.IrBasicBlock basicBlock = new IrValue.IrBasicBlock(GetBasicBlockName(), irFunction);
        // 添加到当前的处理中
        irFunction.AddBasicBlock(basicBlock, afterBlock);

        return basicBlock;
    }

    public static void SetCurrentBasicBlock(IrValue.IrBasicBlock irBasicBlock) {
        currentBasicBlock = irBasicBlock;
    }

    public static IrValue.IrGlobalValue GetNewIrGlobalValue(IrType valueType, IrConst initValue) {
        IrValue.IrGlobalValue globalValue = new IrValue.IrGlobalValue(valueType, GetGlobalVarName(), initValue);
        currentModule.AddIrGlobalValue(globalValue);
        return globalValue;
    }

    public static IrConst.IrConstString GetNewIrConstString(String string) {
        return currentModule.GetNewIrConstString(string);
    }

    public static String GetFuncName(String name) {
        return name.equals("main") ? "@" + name : FUNC_NAME_PREFIX + name;
    }

    public static String GetBasicBlockName() {
        return BasicBlock_NAME_PREFIX + basicBlockCount++;
    }

    public static String GetGlobalVarName() {
        return GLOBAL_VAR_NAME_PREFIX + globalVarNameCount++;
    }

    public static String GetLocalVarName() {
        int count = localVarNameCountMap.get(currentFunction);
        localVarNameCountMap.put(currentFunction, count + 1);
        return LOCAL_VAR_NAME_PREFIX + count;
    }

    public static String GetLocalVarName(IrValue.IrFunction irFunction) {
        int count = localVarNameCountMap.get(irFunction);
        localVarNameCountMap.put(irFunction, count + 1);
        return LOCAL_VAR_NAME_PREFIX + count;
    }

    public static String GetStringConstName() {
        return STRING_LITERAL_NAME_PREFIX + stringConstNameCount++;
    }

    public static void AddInstr(Instr instr) {
        currentBasicBlock.AddInstr(instr);
        instr.SetInBasicBlock(currentBasicBlock);
    }

    public static IrValue.IrBasicBlock GetCurrentBasicBlock() {
        return currentBasicBlock;
    }

    public static IrType GetCurrentFunctionReturnType() {
        return currentFunction.GetReturnType();
    }

    public static void LoopStackPush(IrValue.IrLoop loop) {
        loopStack.push(loop);
    }

    public static void LoopStackPop() {
        loopStack.pop();
    }

    public static IrValue.IrLoop LoopStackPeek() {
        return loopStack.peek();
    }
}