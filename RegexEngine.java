import java.util.Scanner;
import java.util.Stack;

public class RegexEngine {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Show menu to the user
        System.out.println("Choose mode:");
        System.out.println("1. Normal Mode");
        System.out.println("2. Verbose Mode");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        System.out.print("Enter regex: ");
        String regex = scanner.nextLine();

        NFA nfa = constructNFA(regex);

        if (choice == 2) {
            nfa.printTransitionTable();
        }

        System.out.println("ready");

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("q")) {
                break;
            }

            System.out.println(nfa.evaluate(input));
        }

        scanner.close();
    }

    private static NFA constructNFA(String regex) {
        return parseRegex(regex);
    }

    // I using the logic stack for handling the method that parses regex
    private static NFA parseRegex(String regex) {
        Stack<Character> operators = new Stack<>();
        Stack<NFA> operands = new Stack<>();

        int balance = 0;
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);

            if (c == '(') {
                balance++;
                operators.push(c);
            } else if (c == ')') {
                balance--;
                if (balance < 0) {
                    throw new IllegalArgumentException("Unmatched ) in regex.");
                }
                while (!operators.isEmpty() && operators.peek() != '(') {
                    processOperator(operators, operands);
                }
                operators.pop(); // Pop '('
            } else if (c == '[') {
                i++; // Move to the next character
                StringBuilder charClass = new StringBuilder();
                while (i < regex.length() && regex.charAt(i) != ']') {
                    charClass.append(regex.charAt(i));
                    i++;
                }
                if (i == regex.length()) {
                    throw new IllegalArgumentException("Unmatched [ in regex.");
                }
                operands.push(NFA.characterClass(charClass.toString()));
            } else if (c == '\\') {
                i++; // Move to the next character
                if (i == regex.length()) {
                    throw new IllegalArgumentException("Trailing backslash at end of regex.");
                }
                char escapedChar = regex.charAt(i);
                operands.push(NFA.basic(escapedChar));
            } else if (c == '*' || c == '+' || c == '?' || c == '|') {
                if (c == '*' || c == '+' || c == '?') {
                    if (i == 0 || "|*+?(".indexOf(regex.charAt(i - 1)) >= 0) {
                        throw new IllegalArgumentException("Invalid position for operator: " + c);
                    }
                }
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    processOperator(operators, operands);
                }
                operators.push(c);
            }
            else{
            operands.push(NFA.basic(c));
            }
            // else {
            //     operands.push(NFA.basic(c));
            //     if (i < regex.length() - 1 && isLiteralCharacter(regex.charAt(i + 1))) {
            //         while (!operators.isEmpty() && precedence(operators.peek()) >= precedence('.')) {
            //             processOperator(operators, operands);
            //         }
            //         operators.push('.'); // Implicit concatenation
            //     }
            // }
        }

        if (balance != 0) {
            throw new IllegalArgumentException("Unmatched ( in regex.");
        }

        while (!operators.isEmpty()) {
            processOperator(operators, operands);
        }

        return operands.pop();
    }

    // private static boolean isLiteralCharacter(char c) {
    //     return !(c == '(' || c == ')' || c == '*' || c == '+' || c == '?' || c == '|' || c == '[' || c == '\\');
    // }
    

    private static void processOperator(Stack<Character> operators, Stack<NFA> operands) {
        char operator = operators.pop();
        switch (operator) {
            case '*':
                NFA operand = operands.pop();
                operands.push(NFA.kleeneStar(operand));
                break;
            case '|':
                NFA right = operands.pop();
                NFA left = operands.pop();
                operands.push(NFA.alternation(left, right));
                break;
            case '+':
                operand = operands.pop();
                operands.push(NFA.oneOrMore(operand));
                break;
            case '?':
                operand = operands.pop();
                operands.push(NFA.zeroOrOne(operand));
                break;
            // case '.':
            //     NFA right = operands.pop();
            //     NFA left = operands.pop();
            //     operands.push(NFA.concatenation(left, right));
            //     break;
            //  }
        }
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '|':
                return 1;
            case '*':
                return 2;
            case '+':
                return 2;
            case '.':
                return 3;
            default:
                return 0;
        }
    }

    public static boolean matches(String pattern, String input) {
        NFA nfa = constructNFA(pattern);
        return nfa.evaluate(input);
    }

    public void printNFA(String pattern) {
        NFA nfa = constructNFA(pattern);
        nfa.printTransitionTable();
        
    }
}
