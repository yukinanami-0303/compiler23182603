package midend.Ir.IrValue;

import midend.Ir.Const.IrConst;
import midend.Ir.IrType.IrType;
import midend.Ir.IrUse.IrUse;
import midend.Ir.IrUse.IrUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class IrValue {
    protected final IrType irType;
    protected final String irName;
    // 使用当前value的use关系，即使用该value的user列表
    protected final ArrayList<IrUse> useList;
    public IrValue(IrType irType, String irName) {
        this.irType = irType;
        this.irName = irName;
        this.useList = new ArrayList<>();
    }
    public IrType GetIrType() {
        return this.irType;
    }

    public String GetIrName() {
        return this.irName;
    }
    public void AddUse(IrUse use) {
        this.useList.add(use);
    }

    public ArrayList<IrUse> GetUseList() {
        return this.useList;
    }
    public void DeleteUser(IrUser user) {
        Iterator<IrUse> iterator = this.useList.iterator();
        while (iterator.hasNext()) {
            IrUse use = iterator.next();
            if (use.GetUser() == user) {
                iterator.remove();
                return;
            }
        }
    }
    public static class IrGlobalValue extends IrUser{
        private final IrConst globalValue;
        public IrGlobalValue(IrType valueType, String name, IrConst globalValue) {
            super(valueType, name);
            this.globalValue = globalValue;
        }
        @Override
        public String toString() {
            return this.irName + " = dso_local global " + this.globalValue;
        }
    }

    public static class IrFunction extends IrValue{
        private final ArrayList<IrParameter> parameterList;
        private final ArrayList<IrBasicBlock> basicBlockList;
        public IrFunction(String name, IrType returnType) {
            super(new IrFunctionType(returnType), name);
            this.parameterList = new ArrayList<>();
            this.basicBlockList = new ArrayList<>();
        }
        public void AddBasicBlock(IrBasicBlock basicBlock) {
            this.basicBlockList.add(basicBlock);
        }
    }
    public static class IrBasicBlock {

    }
    public static class IrLoop {

    }
}
