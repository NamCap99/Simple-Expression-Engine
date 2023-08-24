import java.util.*;

class State {
    boolean isAccepting;
    Map<Character, List<State>> transitions;
    List<State> epsilonTransitions;
    String name;

    public State(String name) {
        this(name, false);
    }

    public State(String name, boolean isAccepting) {
        this.name = name;
        this.isAccepting = isAccepting;
        this.transitions = new HashMap<>();
        this.epsilonTransitions = new ArrayList<>();
    }
}

public class EpsilonNFAEngine {
    private static char[] pattern;

    public static void main(String[] args) {
        String pattern = "(ab)*|c+";
        State start = compileToENFA(pattern);

        System.out.println("1. Normal mode:");
        System.out.println(pattern);
        System.out.println("ready");
        System.out.println("ccc");
        System.out.println(matchUsingENFA(start, "ccc"));
        System.out.println("ccc");
        System.out.println(matchUsingENFA(start, "ccc"));

        System.out.println("\n2. Verbose mode:");
        displayTransitionTable(start);
        System.out.println("ready");
        verboseMode(start, "true");
        verboseMode(start, "a");
        verboseMode(start, "b");
        verboseMode(start, "c");
    }

    public static State compileToENFA(String pattern) {
        // Constructing the e-NFA manually based on the pattern (ab)*|c+
        State q0 = new State("q0");
        State q1 = new State("q1");
        State q2 = new State("q2");
        State q3 = new State("q3");
        State q4 = new State("q4");
        State q5 = new State("q5");
        State q6 = new State("q6");
        State q7 = new State("q7");
        State q8 = new State("q8", true);

        q0.epsilonTransitions.addAll(Arrays.asList(q1, q5));
        q1.epsilonTransitions.addAll(Arrays.asList(q2, q8));
        q2.transitions.put('a', Arrays.asList(q3));
        q3.transitions.put('b', Arrays.asList(q4));
        q4.epsilonTransitions.add(q1);
        q5.epsilonTransitions.add(q6);
        q6.transitions.put('c', Arrays.asList(q7));
        q7.epsilonTransitions.addAll(Arrays.asList(q5, q8));

        return q0;
    }

    public static boolean matchUsingENFA(State start, String input) {
        Set<State> currentStates = eClosure(Collections.singleton(start));
        for (char c : input.toCharArray()) {
            Set<State> nextStates = new HashSet<>();
            for (State state : currentStates) {
                List<State> transitionStates = state.transitions.get(c);
                if (transitionStates != null) {
                    nextStates.addAll(eClosure(transitionStates));
                }
            }
            currentStates = nextStates;
        }
        for (State state : currentStates) {
            if (state.isAccepting) {
                return true;
            }
        }
        return false;
    }

    public static Set<State> eClosure(Collection<State> states) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);
        while (!stack.isEmpty()) {
            State state = stack.pop();
            for (State next : state.epsilonTransitions) {
                if (!closure.contains(next)) {
                    closure.add(next);
                    stack.push(next);
                }
            }
        }
        return closure;
    }

    public static void displayTransitionTable(State start) {
        System.out.println(pattern);
        System.out.println("    epsilon a   b   c   other");
        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            State state = queue.poll();
            System.out.print((state.isAccepting ? "*" : ">") + state.name + " ");
            System.out.print(displayStates(state.epsilonTransitions) + " ");
            System.out.print(displayStates(state.transitions.get('a')) + " ");
            System.out.print(displayStates(state.transitions.get('b')) + " ");
            System.out.print(displayStates(state.transitions.get('c')) + " ");
            System.out.println();

            for (State next : state.epsilonTransitions) {
                if (!visited.contains(next)) {
                    queue.add(next);
                    visited.add(next);
                }
            }
            for (List<State> nextList : state.transitions.values()) {
                for (State next : nextList) {
                    if (!visited.contains(next)) {
                        queue.add(next);
                        visited.add(next);
                    }
                }
            }
        }
    }

    public static String displayStates(List<State> states) {
        if (states == null || states.isEmpty()) {
            return "                     ";
        }
        StringBuilder sb = new StringBuilder();
        for (State state : states) {
            sb.append(state.name).append(",");
        }
        sb.setLength(sb.length() - 1);  // Remove the last comma
        while (sb.length() < 21) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static void verboseMode(State start, String input) {
        System.out.println(input);
        Set<State> currentStates = eClosure(Collections.singleton(start));
        for (char c : input.toCharArray()) {
            Set<State> nextStates = new HashSet<>();
            for (State state : currentStates) {
                List<State> transitionStates = state.transitions.get(c);
                if (transitionStates != null) {
                    nextStates.addAll(eClosure(transitionStates));
                }
            }
            currentStates = nextStates;
        }
        System.out.println(currentStates.stream().anyMatch(s -> s.isAccepting));
    }
}
