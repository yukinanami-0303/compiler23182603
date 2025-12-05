package midend.Ir;

import java.util.List;

/**
 * getelementptr inbounds.
 *
 * 典型用法：
 *   %p = getelementptr inbounds [10 x i32], [10 x i32]* @a, i32 0, i32 %idx
 */
public class GetElementPtrInst extends IrInstruction {

    private final IrType baseType;  // 比如 [10 x i32]
    private final IrValue pointer;  // 比如 @a
    private final List<IrValue> indices;

    public GetElementPtrInst(String name,
                             IrType baseType,
                             IrValue pointer,
                             List<IrValue> indices,
                             IrType resultType) {
        super(resultType, name);
        this.baseType = baseType;
        this.pointer = pointer;
        this.indices = indices;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(resultPrefix())
                .append("getelementptr inbounds ")
                .append(baseType.toString())
                .append(", ")
                .append(IrType.pointerTo(baseType).toString())
                .append(" ")
                .append(pointer.getRef());

        for (IrValue index : indices) {
            sb.append(", ")
                    .append(index.getType().toString())
                    .append(" ")
                    .append(index.getRef());
        }

        return sb.toString();
    }
}
