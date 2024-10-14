package com.syntax_checker;

import java.util.List;

public class Parser {

    private List<Tokenizer.Token> tokens;
    private int currentTokenIndex;

    public Parser(List<Tokenizer.Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    private Tokenizer.Token getCurrentToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return null;
    }

    private void consumeToken() {
        currentTokenIndex++;
    }

    private void error(String message) throws SyntaxErrorException {
        Tokenizer.Token token = getCurrentToken();
        if (token != null) {
            throw new SyntaxErrorException(
                    "Syntax error at line " + token.line + " (column " + token.column + "): " + message);
        } else {
            throw new SyntaxErrorException("Syntax error: " + message);
        }
    }

    // Parsing methods for output statements
    public String parseOutputStatement() throws SyntaxErrorException {
        int startIndex = currentTokenIndex;
        optionalWhitespace();
        if (matchMultiWord("System.out.print", true)) {
            parsePrintStatement();
        } else if (matchMultiWord("System.out.println", true)) {
            parsePrintlnStatement();
        } else {
            error("Expected 'System.out.print' or 'System.out.println'");
        }
        return reconstructStatement(startIndex, currentTokenIndex);
    }

    private void parsePrintStatement() throws SyntaxErrorException {
        if (optionalWhitespace() && match("(") && optionalWhitespace()
                && parseArgument()
                && optionalWhitespace()
                && match(")") && match(";")) {
            // Successful parsing of print statement
        } else {
            error("Invalid print statement");
        }
    }

    private void parsePrintlnStatement() throws SyntaxErrorException {
        if (optionalWhitespace() && match("(") && optionalWhitespace()
                && parseArgument()
                && optionalWhitespace()
                && match(")") && match(";")) {
            // Successfully parsed println statement
        } else {
            error("Invalid println statement");
        }
    }

    private boolean parseArgument() throws SyntaxErrorException {
        if (parseStringLiteral() || parseNumericLiteral() || parseVariable() || parseExpression()) {
            return true;
        }
        error("Expected a valid argument (string literal, numeric literal, variable, or expression)");
        return false; // Unreachable, but required for compilation
    }

    private boolean parseStringLiteral() {
        Tokenizer.Token currentToken = getCurrentToken();
        if (currentToken != null && currentToken.type == Tokenizer.TokenType.STRING_LITERAL) {
            consumeToken(); // Consume the whole string literal token
            return true;
        }
        return false;
    }

    private boolean parseNumericLiteral() {
        Tokenizer.Token currentToken = getCurrentToken();
        if (currentToken != null && currentToken.type == Tokenizer.TokenType.INTEGER_LITERAL) {
            consumeToken();
            return true;
        }
        return false;
    }

    private boolean parseVariable() {
        Tokenizer.Token currentToken = getCurrentToken();
        if (currentToken != null && currentToken.type == Tokenizer.TokenType.IDENTIFIER) {
            consumeToken();
            return true;
        }
        return false;
    }

    private boolean parseExpression() throws SyntaxErrorException {
        return parseTerm() && parseExpressionTail();
    }

    private boolean parseExpressionTail() throws SyntaxErrorException {
        while (true) {
            Tokenizer.Token currentToken = getCurrentToken();
            if (currentToken == null)
                break;

            if (isOperator(currentToken.value)) {
                consumeToken();
                if (!parseTerm()) {
                    error("Expected a term after operator");
                }
            } else {
                break; // No more operators
            }
        }
        return true;
    }

    private boolean parseTerm() throws SyntaxErrorException {
        if (parseVariable() || parseNumericLiteral() || (match("(") && parseExpression() && match(")"))) {
            return true;
        }
        return false;
    }

    private boolean isOperator(String value) {
        return "+".equals(value) || "-".equals(value) || "*".equals(value) || "/".equals(value) || "=".equals(value)
                || "<".equals(value) || ">".equals(value) || "!".equals(value) || "&".equals(value)
                || "|".equals(value) || "^".equals(value) || "%".equals(value) || "~".equals(value)
                || "?".equals(value);
    }

    // Parsing methods for input statements
    public String parseInputStatement() throws SyntaxErrorException {
        int startIndex = currentTokenIndex;
        optionalWhitespace();

        if (parseScanner() || parseBufferedReader()) {
            // Successfully parsed input statement
        } else {
            error("Invalid input statement");
        }
        return reconstructStatement(startIndex, currentTokenIndex);
    }

    private boolean parseScanner() throws SyntaxErrorException {
        int backtrackIndex = currentTokenIndex;

        if (match("Scanner") &&
                optionalWhitespace() &&
                parseVariable() &&
                optionalWhitespace() &&
                match("=") &&
                optionalWhitespace() &&
                match("new") &&
                optionalWhitespace() &&
                match("Scanner") &&
                optionalWhitespace() &&
                match("(") &&
                optionalWhitespace() &&
                (matchMultiWord("System.in", true) || parseVariable()) &&
                optionalWhitespace() &&
                match(")") &&
                optionalWhitespace() &&
                match(";")) {
            return true;
        }

        currentTokenIndex = backtrackIndex;
        return false;
    }

    private boolean parseBufferedReader() throws SyntaxErrorException {
        int backtrackIndex = currentTokenIndex;

        if (match("BufferedReader") &&
                optionalWhitespace() &&
                parseVariable() &&
                optionalWhitespace() &&
                match("=") &&
                optionalWhitespace() &&
                match("new") &&
                optionalWhitespace() &&
                match("BufferedReader") &&
                optionalWhitespace() &&
                match("(") &&
                optionalWhitespace() &&
                match("new") &&
                optionalWhitespace() &&
                match("InputStreamReader") &&
                optionalWhitespace() &&
                match("(") &&
                optionalWhitespace() &&
                matchMultiWord("System.in", true) &&
                optionalWhitespace() &&
                match(")") &&
                optionalWhitespace() &&
                match(")") &&
                optionalWhitespace() &&
                match(";")) {
            return true;
        }

        currentTokenIndex = backtrackIndex;
        return false;
    }

    public String parseStatement() throws SyntaxErrorException {
        try {
            return parseInputStatement();
        } catch (SyntaxErrorException e) {
            // If it's not an input statement, try parsing it as an output statement
            return parseOutputStatement();
        }
    }

    private boolean match(String expectedValue) {
        Tokenizer.Token currentToken = getCurrentToken();
        if (currentToken != null && currentToken.value.equals(expectedValue)) {
            consumeToken();
            return true;
        }
        return false;
    }

    private boolean matchMultiWord(String expectedValue, boolean consume) {
        String[] words = expectedValue.split("\\.");
        int originalIndex = currentTokenIndex;

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            Tokenizer.Token currentToken = getCurrentToken();
            if (currentToken == null || !currentToken.value.equals(word)) {
                currentTokenIndex = originalIndex;
                return false;
            }
            consumeToken();

            if (i < words.length - 1) {
                currentToken = getCurrentToken();
                if (currentToken == null || !currentToken.value.equals(".")) {
                    currentTokenIndex = originalIndex;
                    return false;
                }
                consumeToken();
            }
        }

        return true;
    }

    private String reconstructStatement(int startIndex, int endIndex) {
        StringBuilder statement = new StringBuilder();
        statement.append("Parsed statement:\n");

        for (int i = startIndex; i < endIndex; i++) {
            Tokenizer.Token token = tokens.get(i);
            statement.append("  Token Type: ").append(token.type.name())
                    .append(", Value: \"").append(token.value).append("\"\n");
        }

        return statement.toString();
    }

    // Helper function to handle optional whitespace
    private boolean optionalWhitespace() {
        while (getCurrentToken() != null && getCurrentToken().type == Tokenizer.TokenType.WHITESPACE
                && getCurrentToken().value.trim().isEmpty()) {
            consumeToken();
        }
        return true; // Always succeeds, as it's optional
    }

    public static void main(String[] args) {
        String code = "Scanner sc = new Scanner(System.in); System.out.println(\"Hello, World!\");";
        Tokenizer tokenizer = new Tokenizer();
        List<Tokenizer.Token> tokens = tokenizer.tokenize(code);

        Parser parser = new Parser(tokens);
        try {
            while (parser.getCurrentToken() != null) {
                String parsedStatement = parser.parseStatement();
                System.out.println(parsedStatement);
                System.out.println("Parsing successful!\n");
            }
        } catch (SyntaxErrorException e) {
            System.err.println(e.getMessage());
        }
    }
}