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
    public void parseOutputStatement() throws SyntaxErrorException {
        optionalWhitespace();
        if (matchMultiWord("System.out.print", true)) {
            // Consume tokens if "System.out.print is found"
            parsePrintStatement();
        } else if (matchMultiWord("System.out.println", true)) {
            // Consume tokens if "System.out.println is found"
            parsePrintlnStatement();
        } else {
            error("Expected 'System.out.print' or 'System.out.println'");
        }
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
    public void parseInputStatement() throws SyntaxErrorException {
        optionalWhitespace();
        if (match("Scanner") && parseVariable() && match("=") && match("new") && match("Scanner") && match("(")
                && matchMultiWord("System.in", true) && match(")") && match(";")) {
            // Successfully parsed input statement
        } else {
            error("Invalid input statement");
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
        int originalIndex = currentTokenIndex; // Store the original index

        for (String word : words) {
            Tokenizer.Token currentToken = getCurrentToken();
            if (currentToken == null || !currentToken.value.equals(word)) {
                currentTokenIndex = originalIndex; // Reset the index if a word doesn't match
                return false;
            }
            consumeToken(); // Move to the next token for each word

            // Check for dots between words, but don't consume the final word's dot
            if (word != words[words.length - 1]) {
                currentToken = getCurrentToken();
                if (currentToken == null || !currentToken.value.equals(".")) {
                    currentTokenIndex = originalIndex;
                    return false;
                }
                consumeToken(); // Consume the dot
            }
        }

        return true;
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
        String code = "Scanner variable = new Scanner(System.in);";
        Tokenizer tokenizer = new Tokenizer();
        List<Tokenizer.Token> tokens = tokenizer.tokenize(code);

        Parser parser = new Parser(tokens);
        try {
            parser.parseInputStatement();
            System.out.println("Parsing successful!");
        } catch (SyntaxErrorException e) {
            System.err.println(e.getMessage());
        }
    }
}

class SyntaxErrorException extends Exception {
    public SyntaxErrorException(String message) {
        super(message);
    }
}