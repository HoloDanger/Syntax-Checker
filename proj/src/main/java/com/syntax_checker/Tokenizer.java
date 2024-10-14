package com.syntax_checker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final String KEYWORDS = "abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while";
    private static final String OPERATORS = "[+\\-*/=<>!&\\|\\^%~\\?\\(\\)\\{\\}\\[\\]\\.]";
    private static final String IO_CLASS = "System|Scanner|BufferedReader|PrintWriter";
    private static final String IO_METHOD = "out|in|err|println|print|readLine|nextInt|nextDouble";
    private static final String IDENTIFIER = "[a-zA-Z_$][a-zA-Z0-9_$]*";
    private static final String STRING_LITERAL = "\"[^\"]*\"";
    private static final String INTEGER_LITERAL = "[0-9]+";
    private static final String FLOAT_LITERAL = "[0-9]+\\.[0-9]+";
    private static final String BOOLEAN_LITERAL = "true|false";
    private static final String CHAR_LITERAL = "\\\\[nrtbf\\\"']";
    private static final String WHITESPACE = "\\s+";
    private static final String SEMICOLON = "\\;";

    public enum TokenType {
        KEYWORD, IO_CLASS, IO_METHOD, IDENTIFIER, OPERATOR, STRING_LITERAL, INTEGER_LITERAL, FLOAT_LITERAL,
        BOOLEAN_LITERAL, CHAR_LITERAL, WHITESPACE, SEMICOLON
    }

    public static class Token {
        public TokenType type;
        public String value;
        public int line;
        public int column;

        public Token(TokenType type, String value, int line, int column) {
            this.type = type;
            this.value = value;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return "Token{" + "type=" + type + ", value ='" + value + '\'' + '}';
        }
    }

    public List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "(" + KEYWORDS + ")|(" + IO_CLASS + ")|(" + IO_METHOD + ")|(" + BOOLEAN_LITERAL + ")|("
                        + IDENTIFIER + ")|(" + OPERATORS + ")|(" + STRING_LITERAL + ")|("
                        + FLOAT_LITERAL + ")|(" + INTEGER_LITERAL + ")|(" + CHAR_LITERAL + ")|(" + WHITESPACE + ")|("
                        + SEMICOLON + ")|(.)");
        Matcher matcher = pattern.matcher(code);

        int lineNumber = 1;
        int columnNumber = 1;

        while (matcher.find()) {
            String tokenValue = matcher.group();
            TokenType tokenType = determineTokenType(matcher);

            if (tokenType == TokenType.WHITESPACE) {
                // Update line and column numbers for whitespace
                for (char c : tokenValue.toCharArray()) {
                    if (c == '\n') {
                        lineNumber++;
                        columnNumber = 1;
                    } else {
                        columnNumber++;
                    }
                }
                continue;
            }

            tokens.add(new Token(tokenType, tokenValue, lineNumber, columnNumber));

            // Update column count
            columnNumber += tokenValue.length();
        }

        return tokens;
    }

    private TokenType determineTokenType(Matcher matcher) {
        if (matcher.group(1) != null)
            return TokenType.KEYWORD;
        if (matcher.group(2) != null)
            return TokenType.IO_CLASS;
        if (matcher.group(3) != null)
            return TokenType.IO_METHOD;
        if (matcher.group(4) != null)
            return TokenType.BOOLEAN_LITERAL;
        if (matcher.group(5) != null)
            return TokenType.IDENTIFIER;
        if (matcher.group(6) != null)
            return TokenType.OPERATOR;
        if (matcher.group(7) != null)
            return TokenType.STRING_LITERAL;
        if (matcher.group(8) != null)
            return TokenType.FLOAT_LITERAL;
        if (matcher.group(9) != null)
            return TokenType.INTEGER_LITERAL;
        if (matcher.group(10) != null)
            return TokenType.CHAR_LITERAL;
        if (matcher.group(11) != null)
            return TokenType.WHITESPACE; // Change this line
        if (matcher.group(12) != null)
            return TokenType.SEMICOLON;
        return null;
    }

    // Custom exception class for lexical errors
    public static class LexicalException extends RuntimeException {
        public LexicalException(String message, int line, int column) {
            super("Lexical error at line " + line + " (column " + column + "): " + message);
        }
    }

    public static void main(String[] args) {
        String code = "System.out.println(\"Hello, World!\");";
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(code);

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}