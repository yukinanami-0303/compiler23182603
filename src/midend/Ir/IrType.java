package midend.Ir;

public class IrType {
    public enum Kind {
        I32, I1, I8, VOID, POINTER, ARRAY
    }

    private final Kind kind;
    private final IrType elementType; // POINTER / ARRAY 的元素类型
    private final int arraySize;      // ARRAY 的长度

    private IrType(Kind kind, IrType elementType, int arraySize) {
        this.kind = kind;
        this.elementType = elementType;
        this.arraySize = arraySize;
    }

    private IrType(Kind kind) {
        this(kind, null, 0);
    }

    // 常用基础类型常量
    public static final IrType I32 = new IrType(Kind.I32);
    public static final IrType I1 = new IrType(Kind.I1);
    public static final IrType I8 = new IrType(Kind.I8);
    public static final IrType VOID = new IrType(Kind.VOID);

    public static IrType pointerTo(IrType elementType) {
        if (elementType == null) {
            throw new IllegalArgumentException("Pointer element type cannot be null");
        }
        return new IrType(Kind.POINTER, elementType, 0);
    }

    public static IrType arrayOf(int size, IrType elementType) {
        if (size <= 0) {
            throw new IllegalArgumentException("Array size must be positive");
        }
        if (elementType == null) {
            throw new IllegalArgumentException("Array element type cannot be null");
        }
        return new IrType(Kind.ARRAY, elementType, size);
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isPointer() {
        return kind == Kind.POINTER;
    }

    public boolean isArray() {
        return kind == Kind.ARRAY;
    }

    public IrType getElementType() {
        return elementType;
    }

    public int getArraySize() {
        return arraySize;
    }

    @Override
    public String toString() {
        switch (kind) {
            case I32:
                return "i32";
            case I1:
                return "i1";
            case I8:
                return "i8";
            case VOID:
                return "void";
            case POINTER:
                return elementType.toString() + "*";
            case ARRAY:
                return "[" + arraySize + " x " + elementType.toString() + "]";
            default:
                throw new IllegalStateException("Unknown IR type kind: " + kind);
        }
    }
}
