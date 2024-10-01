package com.syntax_checker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final String KEYWORDS = "abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while";
    private static final String OPERATORS = "\\+|\\-|\\*|\\/|\\=|\\<|\\>|\\!|\\&|\\||\\^|\\%|\\~|\\?|\\(|\\)|\\{|\\}|\\[|\\]|\\.";
    private static final String IO_CLASS = "System|Scanner|BufferedReader|PrintWriter";
    private static final String IO_METHOD = "out|in|err|println|readLine|nextInt|nextDouble";
    private static final String IDENTIFIER = "[a-zA-Z_$][a-zA-Z0-9_$]*";
    private static final String STRING_LITERAL = "\"[^\"]*\"";
    private static final String INTEGER_LITERAL = "[0-9]+";
    private static final String FLOAT_LITERAL = "[0-9]+\\.[0-9]+";
    private static final String BOOLEAN_LITERAL = "true|false";
    private static final String WHITESPACE = "\\s+";
    private static final String SEMICOlON = "\\;";

    public enum TokenType {
        KEYWORD, IO_CLASS, IO_METHOD, IDENTIFIER, OPERATOR, STRING_LITERAL, INTEGER_LITERAL, FLOAT_LITERAL,
        BOOLEAN_LITERAL, WHITESPACE, SEMICOlON
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
        String patternString = "(" + KEYWORDS + ")|(" + IO_CLASS + ")|(" + IO_METHOD + ")|(" + BOOLEAN_LITERAL + ")|("
                + IDENTIFIER + ")|(" + OPERATORS + ")|(" + STRING_LITERAL + ")|("
                + FLOAT_LITERAL + ")|(" + INTEGER_LITERAL + ")|(" + WHITESPACE + ")|(" + SEMICOlON + ")|(.)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(code);

        int line = 1;
        int column = 1;

        while (matcher.find()) {
            String tokenValue = matcher.group();
            TokenType tokenType = null;

            if (matcher.group(1) != null) {
                tokenType = TokenType.KEYWORD;
            } else if (matcher.group(2) != null) {
                tokenType = TokenType.IO_CLASS;
            } else if (matcher.group(3) != null) {
                tokenType = TokenType.IO_METHOD;
            } else if (matcher.group(4) != null) {
                tokenType = TokenType.BOOLEAN_LITERAL;
            } else if (matcher.group(5) != null) {
                tokenType = TokenType.IDENTIFIER;
            } else if (matcher.group(6) != null) {
                tokenType = TokenType.OPERATOR;
            } else if (matcher.group(7) != null) {
                tokenType = TokenType.STRING_LITERAL;
            } else if (matcher.group(8) != null) {
                tokenType = TokenType.FLOAT_LITERAL;
            } else if (matcher.group(9) != null) {
                tokenType = TokenType.INTEGER_LITERAL;
            } else if (matcher.group(10) != null) {
                // Ignore whitespace
                continue;
            } else if (matcher.group(11) != null) {
                tokenType = TokenType.SEMICOlON;
            } else if (matcher.group(12) != null) {
                // Handle invalid characters
                throw new LexicalException("Invalid character: " + tokenValue + "'", line, column);
            }

            if (tokenType != null) {
                tokens.add(new Token(tokenType, tokenValue, line, column));
            }

            // Update column cont
            column += tokenValue.length();
            if (tokenValue.contains("\n")) {
                line++;
                column = 1; // Reset column for the new line
            }
        }

        // Check for unterminated string literals
        if (code.endsWith("\"")) {
            throw new LexicalException("Unterminated string literal", line, column);
        }

        return tokens;
    }

    // Custom exception class for lexical errors
    public static class LexicalException extends RuntimeException {
        public LexicalException(String message, int line, int column) {
            super("Lexical error at line " + line + " (column " + column + "): " + message);
        }
    }

    public static void main(String[] args) {
        String code = "boolean isTrue = true;";
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(code);

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}