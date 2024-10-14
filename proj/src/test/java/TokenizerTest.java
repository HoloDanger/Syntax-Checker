import java.util.List;
import com.syntax_checker.Tokenizer;

public class TokenizerTest {

    public static void main(String[] args) {
        // Test invalid token scenarios
        testInvalidToken("Scanner scanner = new Scanner(System.in);"); // Missing closing parenthesis
        testInvalidToken("BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));");
        testInvalidToken("PrintWriter writer = new PrintWriter(System.out, true);"); // Invalid character '#'
        testInvalidToken("int x = 5 # This is a comment"); // Invalid token after a number
        testInvalidToken("if (x == 5) { print(x); }"); // 'print' is not recognized
        testInvalidToken("while true);"); // Incorrect syntax for while loop
    }

    private static void testInvalidToken(String code) {
        Tokenizer tokenizer = new Tokenizer();
        try {
            List<Tokenizer.Token> tokens = tokenizer.tokenize(code);
            // If no exception is thrown, print the tokens for verification
            System.out.println("Tokens for the input code:\n");
            printTokens(tokens);
        } catch (Tokenizer.LexicalException e) {
            // Print the exception message for invalid token detection
            System.out.println(e.getMessage());
        }
    }

    private static void printTokens(List<Tokenizer.Token> tokens) {
        System.out.println("------------------------------------------------------");
        for (Tokenizer.Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("------------------------------------------------------");
    }
}
