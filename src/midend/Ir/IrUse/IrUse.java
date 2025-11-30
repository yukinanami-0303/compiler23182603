package midend.Ir.IrUse;

import midend.Ir.IrValue.IrValue;

public class IrUse {
    private final IrUser user;
    private final IrValue value;
    public IrUse(IrUser user, IrValue value) {
        this.user = user;
        this.value = value;
    }
    public IrUser GetUser() {
        return this.user;
    }

    public IrValue GetValue() {
        return this.value;
    }
}
