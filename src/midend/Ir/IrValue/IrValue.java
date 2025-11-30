package midend.Ir.IrValue;

import midend.Ir.IrType.IrType;
import midend.Ir.IrUse.IrUse;
import midend.Ir.IrUse.IrUser;

import java.util.ArrayList;
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
    public static class IrGlobalValue {

    }

    public static class IrFunc {
    }
}
