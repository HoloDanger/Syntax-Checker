import java.util.ArrayList;
import java.util.List;

import com.syntax_checker.Parser;
import com.syntax_checker.SyntaxErrorException;
import com.syntax_checker.Tokenizer;

public class ParserTest {

    public static void main(String[] args) {
        // Run all test cases
        testValidPrintlnStatement();
        testValidPrintStatement();
        testInvalidPrintStatementMissingSemicolon();
        testValidScannerInputStatement();
        testInvalidScannerStatement();
        testMixedValidStatements();
    }

    // Test Case 1: Valid `System.out.println` statement parsing
    public static void testValidPrintlnStatement() {
        List<Tokenizer.Token> tokens = new ArrayList<>();
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "System", 1, 1));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 1, 7));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "out", 1, 8));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 1, 11));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "println", 1, 12));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, "(", 1, 19));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.STRING_LITERAL, "\"Hello, World!\"", 1, 20));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ")", 1, 35));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ";", 1, 36));

        Parser parser = new Parser(tokens);

        try {
            String parsedStatement = parser.parseOutputStatement();
            System.out.println("Test Valid Println Statement: " + parsedStatement);
        } catch (SyntaxErrorException e) {
            System.out.println("Test Valid Println Statement failed: " + e.getMessage());
        }
    }

    // Test Case 2: Valid `System.out.print` statement parsing
    public static void testValidPrintStatement() {
        List<Tokenizer.Token> tokens = new ArrayList<>();
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "System", 1, 1));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 1, 7));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "out", 1, 8));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 1, 11));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "print", 1, 12));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, "(", 1, 18));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.STRING_LITERAL, "\"Hello\"", 1, 19));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ")", 1, 26));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ";", 1, 27));

        Parser parser = new Parser(tokens);

        try {
            String parsedStatement = parser.parseOutputStatement();
            System.out.println("Test Valid Print Statement: " + parsedStatement);
        } catch (SyntaxErrorException e) {
            System.out.println("Test Valid Print Statement failed: " + e.getMessage());
        }
    }

    // Test Case 3: Invalid `System.out.print` statement (missing semicolon)
    public static void testInvalidPrintStatementMissingSemicolon() {
        List<Tokenizer.Token> tokens = new ArrayList<>();
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "System", 1, 1));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 1, 7));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "out", 1, 8));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 1, 11));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "print", 1, 12));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, "(", 1, 18));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.STRING_LITERAL, "\"Hello\"", 1, 19));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ")", 1, 26));

        Parser parser = new Parser(tokens);

        try {
            String parsedStatement = parser.parseOutputStatement();
            System.out.println("Test Invalid Print Statement (Missing Semicolon) failed: Expected error, but got "
                    + parsedStatement);
        } catch (SyntaxErrorException e) {
            System.out.println("Test Invalid Print Statement (Missing Semicolon) passed: " + e.getMessage());
        }
    }

    // Test Case 4: Valid `Scanner` input statement parsing
    public static void testValidScannerInputStatement() {
        List<Tokenizer.Token> tokens = new ArrayList<>();
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "Scanner", 1, 1));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "sc", 1, 9));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.OPERATOR, "=", 1, 12));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.KEYWORD, "new", 1, 14));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "Scanner", 1, 18));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, "(", 1, 25));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "System", 1, 26));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 1, 32));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "in", 1, 33));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ")", 1, 36));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ";", 1, 37));

        Parser parser = new Parser(tokens);

        try {
            String parsedStatement = parser.parseInputStatement();
            System.out.println("Test Valid Scanner Input Statement: " + parsedStatement);
        } catch (SyntaxErrorException e) {
            System.out.println("Test Valid Scanner Input Statement failed: " + e.getMessage());
        }
    }

    // Test Case 5: Invalid `Scanner` statement (missing parentheses)
    public static void testInvalidScannerStatement() {
        List<Tokenizer.Token> tokens = new ArrayList<>();
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "Scanner", 1, 1));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "sc", 1, 9));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.OPERATOR, "=", 1, 12));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.KEYWORD, "new", 1, 14));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "Scanner", 1, 18));
        // Intentionally missing a parenthesis and semicolon to trigger an error.

        Parser parser = new Parser(tokens);

        try {
            String parsedStatement = parser.parseInputStatement();
            System.out.println("Test Invalid Scanner Statement failed: Expected error, but got " + parsedStatement);
        } catch (SyntaxErrorException e) {
            System.out.println("Test Invalid Scanner Statement passed: " + e.getMessage());
        }
    }

    // Test Case 6: Mixed valid input (Scanner) and output (println) statements
    public static void testMixedValidStatements() {
        List<Tokenizer.Token> tokens = new ArrayList<>();
        // Scanner statement
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "Scanner", 1, 1));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "sc", 1, 9));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.OPERATOR, "=", 1, 12));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.KEYWORD, "new", 1, 14));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "Scanner", 1, 18));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, "(", 1, 25));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "System", 1, 26));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 1, 32));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "in", 1, 33));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ")", 1, 36));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ";", 1, 37));

        // Println statement
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "System", 2, 1));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 2, 7));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "out", 2, 8));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ".", 2, 11));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.IDENTIFIER, "println", 2, 12));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, "(", 2, 19));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.STRING_LITERAL, "\"Hello, World!\"", 2, 20));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ")", 2, 35));
        tokens.add(new Tokenizer.Token(Tokenizer.TokenType.SEPARATOR, ";", 2, 36));

        Parser parser = new Parser(tokens);

        try {
            // First parse the Scanner statement
            String parsedScannerStatement = parser.parseStatement();
            System.out.println("Test Mixed Valid Statements (Scanner): " + parsedScannerStatement);

            // Then parse the Println statement
            String parsedPrintlnStatement = parser.parseStatement();
            System.out.println("Test Mixed Valid Statements (Println): " + parsedPrintlnStatement);
        } catch (SyntaxErrorException e) {
            System.out.println("Test Mixed Valid Statements failed: " + e.getMessage());
        }
    }
}