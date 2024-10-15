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

    private String generateErrorMessage(String context, String message) {
        Tokenizer.Token token = getCurrentToken();
        StringBuilder errorMessage = new StringBuilder("Syntax error");

        if (token != null) {
            errorMessage.append(" at line ").append(token.line)
                    .append(" (column ").append(token.column).append("): ")
                    .append(message).append(" (Found: '").append(token.value).append("')");
        } else {
            errorMessage.append(": ").append(message);
        }

        // Include the context in the error message if needed
        if (context != null) {
            errorMessage.append(" [Context: ").append(context).append("]");
        }

        return errorMessage.toString();
    }

    private void error(String message) throws SyntaxErrorException {
        String errorMessage = generateErrorMessage(null, message);
        throw new SyntaxErrorException(errorMessage);
    }

    // Parsing methods for output statements
    public String parseOutputStatement() throws SyntaxErrorException {
        int startIndex = currentTokenIndex;
        optionalWhitespace();

        if (matchMultiWord("System.out.print")) {
            parsePrintStatement();
        } else if (matchMultiWord("System.out.println")) {
            parsePrintlnStatement();
        } else {
            error("Expected 'System.out.print' or 'System.out.println'");
        }

        return reconstructStatement(startIndex, currentTokenIndex);
    }

    private void parsePrintStatement() throws SyntaxErrorException {
        optionalWhitespace();
        if (!match("(")) {
            error("Expected opening parenthesis after 'print'");
        }

        parsePrintExpression();
        optionalWhitespace();

        if (!match(")")) {
            error("Expected closing parenthesis after expression");
        }

        optionalWhitespace();
        if (!match(";")) {
            error("Expected semicolon at the end of print statement");
        }
    }

    private void parsePrintlnStatement() throws SyntaxErrorException {
        optionalWhitespace();
        if (!match("(")) {
            error("Expected opening parenthesis after 'println'");
        }

        parsePrintExpression();
        optionalWhitespace();

        if (!match(")")) {
            error("Expected closing parenthesis after expression");
        }

        optionalWhitespace();
        if (!match(";")) {
            error("Expected semicolon at the end of println statement");
        }
    }

    private void parsePrintExpression() throws SyntaxErrorException {
        if (!parseExpression() && !optionalWhitespace()) {
            error("Expected a valid expression inside print statement");
        }
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
        if (currentToken != null && currentToken.type == Tokenizer.TokenType.INTEGER_LITERAL
                || currentToken.type == Tokenizer.TokenType.FLOAT_LITERAL) {
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
        if (!parseTerm()) {
            return false;
        }
        return parseExpressionTail();
    }

    private boolean parseExpressionTail() throws SyntaxErrorException {
        while (true) {
            Tokenizer.Token currentToken = getCurrentToken();
            if (currentToken == null)
                break;

            if (isOperator(currentToken.value)) {
                consumeToken();
                if (!parseTerm()) {
                    error("Expected a term after operator" + currentToken.value);
                }
            } else {
                break; // No more operators
            }
        }
        return true;
    }

    private boolean parseBooleanLiteral() {
        Tokenizer.Token currentToken = getCurrentToken();
        if (currentToken != null && currentToken.type == Tokenizer.TokenType.BOOLEAN_LITERAL) {
            consumeToken();
            return true;
        }
        return false;
    }

    private boolean parseTerm() throws SyntaxErrorException {
        Tokenizer.Token currentToken = getCurrentToken(); // Get the current token once
        if (currentToken == null) {
            return false; // If no current token, cannot parse a term
        }

        // Now check the type of the current token
        if (parseStringLiteral() || parseBooleanLiteral() || parseVariable() || parseNumericLiteral()
                || (match("(") && parseExpression() && match(")"))) {
            return true;
        }
        return false;
    }

    private boolean isOperator(String value) {
        return "+".equals(value) || "-".equals(value) || "*".equals(value) || "/".equals(value) || "=".equals(value)
                || "<".equals(value) || ">".equals(value) || "!".equals(value) || "&".equals(value)
                || "|".equals(value) || "^".equals(value) || "%".equals(value) || "~".equals(value)
                || "?".equals(value) || ">=".equals(value) || "<=".equals(value);
    }

    // Parsing methods for input statements
    public String parseInputStatement() throws SyntaxErrorException {
        int startIndex = currentTokenIndex;
        optionalWhitespace();

        if (!parseScanner() && !parseBufferedReader()) {
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
                (matchMultiWord("System.in") || parseVariable()) &&
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
                matchMultiWord("System.in") &&
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
            return parseOutputStatement();
        } catch (SyntaxErrorException e) {
            // If output statement parsing fails, try input statement
            try {
                return parseInputStatement();
            } catch (SyntaxErrorException ignored) {
                // If both fail, throw the original error from output statement
                throw e;
            }
        }
    }

    public void parseAllStatements() throws SyntaxErrorException {
        int totalParsedStatements = 0; // Counter for total parsed statements

        while (currentTokenIndex < tokens.size()) {
            optionalWhitespace(); // Consume whitespace/newlines before attempting to parse

            if (currentTokenIndex < tokens.size()) { // Check if there are more tokens
                try {
                    String parsedStatement = parseStatement(); // Parse a single statement
                    System.out.println("\nParsed statement successfully:\n" + parsedStatement); // Indicate success
                    totalParsedStatements++; // Increment the counter for each successful parse
                } catch (SyntaxErrorException e) {
                    System.err.println("Error: " + e.getMessage());
                    consumeToken(); // Move to the next token to continue parsing
                }
            }
        }

        // Summary message reflecting the total number of parsed statements
        if (totalParsedStatements > 0) {
            System.out.println("Parsing successful. Total statements parsed: " + totalParsedStatements);
        } else {
            System.out.println("No valid statements parsed.");
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

    private boolean matchMultiWord(String expectedValue) {
        String[] words = expectedValue.split("\\.");
        int lookaheadIndex = currentTokenIndex;

        for (int i = 0; i < words.length; i++) {
            Tokenizer.Token currentToken = tokens.get(lookaheadIndex);

            if (currentToken == null || !currentToken.value.equals(words[i])) {
                return false;
            }

            lookaheadIndex++;

            if (i < words.length - 1) {
                currentToken = tokens.get(lookaheadIndex);
                if (currentToken == null || !currentToken.value.equals(".")) {
                    return false;
                }
                lookaheadIndex++;
            }
        }

        currentTokenIndex = lookaheadIndex;
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
        while (getCurrentToken() != null &&
                (getCurrentToken().type == Tokenizer.TokenType.WHITESPACE ||
                        getCurrentToken().type == Tokenizer.TokenType.NEWLINE)) {
            consumeToken();
        }
        return true;
    }

    public static void main(String[] args) {
        String code = """
                Scanner scanner = new Scanner(System.in);
                System.out.println(num);
                int num = scanner.nextInt();
                """;
        Tokenizer tokenizer = new Tokenizer();
        try {
            List<Tokenizer.Token> tokens = tokenizer.tokenize(code);

            // Print all tokens
            for (int i = 0; i < tokens.size(); i++) {
                Tokenizer.Token token = tokens.get(i);
                System.out.printf("Token %d: Type=%s, Value='%s', Line=%d, Column=%d%n",
                        i, token.type, token.value, token.line, token.column);
            }

            Parser parser = new Parser(tokens);
            parser.parseAllStatements();
        } catch (Tokenizer.LexicalException e) {
            System.err.println(e.getMessage());
        } catch (SyntaxErrorException e) {
            System.err.println(e.getMessage());
        }
    }
}