package midend.Ir;

import frontend.ast.CompUnit;

import static frontend.Parser.GetAstTree;

/**
 * 把 AST 转成 LLVM IR 的后端入口。
 * 真正的“造 IR”逻辑嵌在各个 AST 节点的 visit / generateIr 方法里，
 * 这里只负责把整个 IrModule 转成字符串返回。
 */
public class IrGenerator {

    /**
     * 入口：返回 LLVM IR 文本（由 Compiler 调用，再写入 llvm_ir.txt）
     */
    public static String generate() {
        // 这里不再重置，也不再重新遍历 AST，
        // IR 在 MidEnd.GenerateSymbolTable 时就已经构建完了。
        CompUnit root = GetAstTree();  // 暂时不用，但保留以便以后扩展
        IrModule module = IrFactory.getModule();
        return module.emit();
    }
}
