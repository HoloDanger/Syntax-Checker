import java.util.List;

import com.syntax_checker.Tokenizer;

public class TokenizerTest {

    public static void main(String[] args) {

        // Test invalid token scenarios
        testInvalidToken("System.out.println(\"Hello, World!\";"); // Missing closing parenthesis
        testInvalidToken("int 123var = 5;"); // Invalid identifier starting with a digit
        testInvalidToken("class #MyClass {}"); // Invalid character '#'
        testInvalidToken("int x = 5 # This is a comment"); // Invalid token after a number
        testInvalidToken("if (x == 5) { print(x); }"); // 'print' is not recognized
        testInvalidToken("while true);"); // Incorrect syntax for while loop
    }

    private static void testInvalidToken(String code) {
        Tokenizer tokenizer = new Tokenizer();
        try {
            List<Tokenizer.Token> tokens = tokenizer.tokenize(code);
            // If no exception is thrown, print the tokens for verification
            System.out.println("Tokens: " + tokens);
        } catch (Tokenizer.LexicalException e) {
            // Print the exception message for invalid token detection
            System.out.println(e.getMessage());
        }
    }
}
