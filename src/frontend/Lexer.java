package frontend;

import Error.ErrorHandler;
import java.util.*;

import static Error.ErrorHandler.addError;


public class Lexer {



    private final Map<String, String> keywords;
    private final Map<String, String> operators;
    private List<Token> tokens;

    private boolean inComment;

    public Lexer() {
        this.keywords = initializeKeywords();
        this.operators = initializeOperators();
        this.tokens = new ArrayList<>();
        this.inComment = false;
    }

    private Map<String, String> initializeKeywords() {
        Map<String, String> keywords = new HashMap<>();
        keywords.put("const", "CONSTTK");
        keywords.put("int", "INTTK");
        keywords.put("static", "STATICTK");
        keywords.put("break", "BREAKTK");
        keywords.put("continue", "CONTINUETK");
        keywords.put("if", "IFTK");
        keywords.put("else", "ELSETK");
        keywords.put("for", "FORTK");
        keywords.put("return", "RETURNTK");
        keywords.put("void", "VOIDTK");
        keywords.put("main", "MAINTK");
        keywords.put("printf", "PRINTFTK");
        return keywords;
    }

    private Map<String, String> initializeOperators() {
        Map<String, String> operators = new HashMap<>();
        operators.put("+", "PLUS");
        operators.put("-", "MINU");
        operators.put("*", "MULT");
        operators.put("/", "DIV");
        operators.put("%", "MOD");
        operators.put("<", "LSS");
        operators.put(">", "GRE");
        operators.put("<=", "LEQ");
        operators.put(">=", "GEQ");
        operators.put("==", "EQL");
        operators.put("!=", "NEQ");
        operators.put("=", "ASSIGN");
        operators.put("!", "NOT");
        operators.put("&&", "AND");
        operators.put("||", "OR");
        operators.put(";", "SEMICN");
        operators.put(",", "COMMA");
        operators.put("(", "LPARENT");
        operators.put(")", "RPARENT");
        operators.put("[", "LBRACK");
        operators.put("]", "RBRACK");
        operators.put("{", "LBRACE");
        operators.put("}", "RBRACE");
        return operators;
    }

    public void tokenize(String code) {
        tokens.clear();
        inComment = false;

        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {

            processLine(lines[i], i + 1);
        }
    }

    private void processLine(String line, int lineNum) {
        if (line == null || line.isEmpty()) {
            return;
        }

        int pos = 0;
        int length = line.length();

        while (pos < length) {
            // 跳过空白字符
            if (Character.isWhitespace(line.charAt(pos))) {
                pos++;
                continue;
            }

            // 处理注释
            if (inComment) {
                int commentEnd = line.indexOf("*/", pos);
                if (commentEnd != -1) {
                    pos = commentEnd + 2;
                    inComment = false;
                } else {
                    break;
                }
                continue;
            }

            // 检查行注释
            if (pos + 1 < length && line.charAt(pos) == '/' && line.charAt(pos + 1) == '/') {
                break;
            }

            // 检查块注释开始
            if (pos + 1 < length && line.charAt(pos) == '/' && line.charAt(pos + 1) == '*') {
                inComment = true;
                pos += 2;
                continue;
            }

            char currentChar = line.charAt(pos);

            // 检查是否为多字符运算符
            boolean matched = false;
            if (pos + 1 < length) {
                String twoCharOp = line.substring(pos, pos + 2);
                if (operators.containsKey(twoCharOp)) {
                    tokens.add(new Token(operators.get(twoCharOp), twoCharOp, lineNum));
                    pos += 2;
                    matched = true;
                    continue;
                }
            }

            // 检查是否为单字符运算符或界符
            String charStr = String.valueOf(currentChar);
            if (operators.containsKey(charStr)) {
                tokens.add(new Token(operators.get(charStr), charStr, lineNum));
                pos++;
                continue;
            }

            // 匹配字符串常量
            if (currentChar == '"') {
                int start = pos;
                pos++;

                StringBuilder stringBuilder = new StringBuilder("\"");
                boolean stringClosed = false;

                while (pos < length) {
                    if (line.charAt(pos) == '"') {
                        stringBuilder.append('"');
                        pos++;
                        stringClosed = true;
                        break;
                    } else if (line.charAt(pos) == '\\' && pos + 1 < length) {
                        // 处理转义字符
                        stringBuilder.append(line.charAt(pos));
                        stringBuilder.append(line.charAt(pos + 1));
                        pos += 2;
                    } else {
                        stringBuilder.append(line.charAt(pos));
                        pos++;
                    }
                }

                if (stringClosed) {
                    tokens.add(new Token("STRCON", stringBuilder.toString(), lineNum));
                } else {
                    // 字符串没有正确闭合
                    addError(lineNum, "a");
                    break;
                }
                continue;
            }

            // 检查单个 '&' 和 '|' 字符（词法错误）
            if (currentChar == '&' || currentChar == '|') {

                if(currentChar == '&'){//虽然报错但仍然将其识别为正确Token避免影响语法分析
                    tokens.add(new Token("AND","&&",lineNum));
                }
                else{
                    tokens.add(new Token("OR","||",lineNum));
                }

                addError(lineNum, "a");
                pos++;
                continue;
            }

            // 匹配标识符或关键字
            if (Character.isLetter(currentChar) || currentChar == '_') {
                int start = pos;
                while (pos < length && (Character.isLetterOrDigit(line.charAt(pos)) || line.charAt(pos) == '_')) {
                    pos++;
                }
                String identifier = line.substring(start, pos);
                if (keywords.containsKey(identifier)) {
                    tokens.add(new Token(keywords.get(identifier), identifier, lineNum));
                } else {
                    tokens.add(new Token("IDENFR", identifier, lineNum));
                }
                continue;
            }

            // 匹配整数常量
            if (Character.isDigit(currentChar)) {
                int start = pos;
                while (pos < length && Character.isDigit(line.charAt(pos))) {
                    pos++;
                }
                String number = line.substring(start, pos);
                tokens.add(new Token("INTCON", number, lineNum));
                continue;
            }

            // 无法识别的字符
            addError(lineNum, "a");
            pos++;
        }
    }

    public ArrayList<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
}