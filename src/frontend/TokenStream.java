package frontend;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import frontend.Token;

public class TokenStream {
    private static ArrayList<Token> tokens;
    private static int currentIndex = 0;


    public static void setTokens(ArrayList<Token> token) {
        tokens = token;
    }

    // 获取当前Token（不移动指针）
    public static Token Peek(int peekstep) {
        if (currentIndex+peekstep >= tokens.size()) {
            return new Token("EOF", "end of token stream", -1);// 已到末尾
        }
        return tokens.get(currentIndex+peekstep);
    }
    //获取当前currentIndex
    public static int GetcurrentIndex(){
        return currentIndex;
    }

    //移动到目标Token
    public static void setbackToken(int savecurrentIndex){
        currentIndex=savecurrentIndex;
    }

    // 移动到下一个Token
    public static void nextToken() {
        if (currentIndex < tokens.size()) {
            currentIndex++;
        }
    }

    //返回上一个非终结符的linenumber
    public static int GetBeforeLineNumber(){
        return Peek(-1).getLineNumber();
    }



}
