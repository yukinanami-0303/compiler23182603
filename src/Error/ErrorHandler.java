package Error;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static frontend.TokenStream.Peek;


public class ErrorHandler {
    private static ArrayList<ErrorInfo> errors=new ArrayList<ErrorInfo>();
    // 错误信息类
    public static class ErrorInfo {
        private int lineNumber;
        private String type;

        public ErrorInfo(int lineNumber, String type) {
            this.lineNumber = lineNumber;
            this.type = type;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return lineNumber + " " + type;
        }
    }


    public static int CurrentErrorsSize(){
        return errors.size();
    }

    public static void BackToBeforeErrors(int BeforeErrorsSize){
        // 确保快照大小合法
        if (BeforeErrorsSize < 0 || BeforeErrorsSize > errors.size()) {
            return;
        }
        // 移除快照后新增的错误
        while (errors.size() > BeforeErrorsSize) {
            errors.remove(errors.size() - 1);
        }
    }

    public static void addError(int lineNumber,String errorType) {
        errors.add(new ErrorInfo(lineNumber, errorType));
    }

    public ArrayList<ErrorInfo> getErrors() {
        return errors;
    }


    public static boolean hasErrors() {
        return !errors.isEmpty();
    }
    private static void sortErrors() {
        // 使用稳定排序的Comparator，按lineNumber升序排列
        errors.sort(Comparator.comparingInt(ErrorInfo::getLineNumber));
    }
    public static void writeErrorsToFile(String filename) {
        // 若错误列表为空，直接返回（可选逻辑，也可写入空文件）
        if (errors.isEmpty()) {
            return;
        }
        sortErrors();
        // 使用try-with-resources自动关闭流
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (ErrorInfo error : errors) {
                // 按行写入错误信息（格式：行号 错误类型）
                writer.write(error.getLineNumber() + " " + error.getType());
                writer.newLine();  // 换行
            }
        } catch (IOException e) {
            // 输出文件写入失败信息
            System.err.println("错误信息写入文件失败：" + e.getMessage());
        }
    }
    public void clearErrors() {
        errors.clear();
    }
}
