import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class NFA {
    private State startState;
    private State finalState;
    // private static int stateCounter = 0;
    Set<State> states = new HashSet<>(); // This will store all states of the NFA

    public NFA(State start, State end) {
        this.startState = start;
        this.finalState = end;
        states.add(start);
        states.add(finalState);
    }

    public void addState(State s) {
        states.add(s);
    }

    public State getStartState() {
        return startState;
    }

    public State getFinalState() {
        return finalState;
    }

    public static NFA basic(char c) {
        State start = new State();
        State end = new State();
        start.addTransition(c, end);
        return new NFA(start, end);
    }

    public static NFA kleeneStar(NFA nfa) {
        State start = new State();
        State end = new State();

        start.addTransition('\0', nfa.startState); // epsilon-transition
        start.addTransition('\0', end); // epsilon-transition to accept the empty string

        nfa.finalState.addTransition('\0', nfa.startState); // loop back for repetitions
        nfa.finalState.addTransition('\0', end); // transition to final state after repetitions

        NFA result = new NFA(start, end);
        result.states.addAll(nfa.states);
        result.addState(start);
        result.addState(end);
        return result;
    }

    public static NFA alternation(NFA nfa1, NFA nfa2) {
        State start = new State();
        State end = new State();

        start.addTransition('\0', nfa1.startState);
        start.addTransition('\0', nfa2.startState);
        nfa1.finalState.addTransition('\0', end);
        nfa2.finalState.addTransition('\0', end);

        NFA result = new NFA(start, end);
        result.states.addAll(nfa1.states);
        result.states.addAll(nfa2.states);
        result.addState(start);
        result.addState(end);
        return result;
    }

    public static NFA concatenation(NFA nfa1, NFA nfa2) {
        State newFinalState = new State();
        nfa1.finalState.addTransition('\0', nfa2.startState);
        nfa2.finalState.addTransition('\0', newFinalState);
        
        NFA result = new NFA(nfa1.startState, newFinalState);
        result.states.addAll(nfa1.states);
        result.states.addAll(nfa2.states);
        result.addState(newFinalState);
        return result;
    }
    

    // Method for evaluating a string an NFA, I need to simulate the NFA's
    // transition based on the characters of the string
    public boolean evaluate(String input) {
        Set<State> currentStates = epsilonClosure(this.startState);

        System.out.println("Start states: " + statesToString(currentStates));

        for (char symbol : input.toCharArray()) {
            Set<State> symbolStates = move(currentStates, symbol);

             // Debugging print
            System.out.println("After consuming '" + symbol + "': " + statesToString(symbolStates));


            // Get epsilon-closures of the next states
            Set<State> nextStates = new HashSet<>();
            for (State state : symbolStates) {
                nextStates.addAll(epsilonClosure(state));
            }
             // Debugging print
            System.out.println("After ε-closure: " + statesToString(nextStates));

            currentStates = nextStates;
        }

        return currentStates.contains(this.finalState);
    }

    private String statesToString(Set<State> states) {
        StringBuilder sb = new StringBuilder();
        for (State s : states) {
            sb.append(s.name).append(" ");
        }
        return sb.toString().trim();
    }

    private void addEpsilonTransitions(Set<State> states) {
        boolean added;
        do {
            Set<State> newStates = new HashSet<>(states);
            for (State state : states) {
                if (state.transitions.containsKey('\0')) {
                    newStates.addAll(state.transitions.get('\0'));
                }
            }
            added = states.size() < newStates.size();
            states.addAll(newStates);
        } while (added);
    }

    // A method to print the transition table for Verbose Mode
    public void printTransitionTable() {
        System.out.println("       epsilon     a     b     c     other");

        List<State> orderedStates = new ArrayList<>(states);
        orderedStates.sort(Comparator.comparingInt(s -> Integer.parseInt(s.name.substring(1))));

        for (State s : states) {
            System.out.print(s.name + "   ");
            for (char c : new char[] { '\0', 'a', 'b', 'c' }) {
                if (s.transitions.containsKey(c)) {
                    for (State dest : s.transitions.get(c)) {
                        System.out.print(dest.name + " ");
                    }
                }
                System.out.print("   ");
            }
            System.out.println();
        }
    }

    // Handles one or more repetitions of a pattern
    public static NFA oneOrMore(NFA nfa) {
        State start = new State();
        State end = new State();

        start.addTransition('\0', nfa.startState); // epsilon transition
        nfa.finalState.isFinal = false;
        nfa.finalState.addTransition('\0', end);
        nfa.finalState.addTransition('\0', nfa.startState); // loop back for one or more

        end.isFinal = true;

        return new NFA(start, end);
    }

    // Handles zero or one repetition of a pattern
    public static NFA zeroOrOne(NFA nfa) {
        State start = new State();
        State end = new State();

        start.addTransition('\0', nfa.startState); // epsilon transition
        start.addTransition('\0', end); // epsilon transition for zero occurrences
        nfa.finalState.isFinal = false;
        nfa.finalState.addTransition('\0', end);

        end.isFinal = true;

        return new NFA(start, end);
    }

    // ... [Existing code]

    // Get all states reachable from 'state' using only epsilon-transitions
    Set<State> epsilonClosure(State state) {
        Set<State> closure = new HashSet<>();
        Stack<State> stack = new Stack<>();
        stack.push(state);

        while (!stack.isEmpty()) {
            State current = stack.pop();
            if (!closure.contains(current)) {
                closure.add(current);
                for (State next : current.getTransitions('\0')) { // '\0' represents epsilon
                    stack.push(next);
                }
            }
        }
        return closure;
    }

    // Move from the given states by consuming 'symbol'
    private Set<State> move(Set<State> states, char symbol) {
        Set<State> nextStates = new HashSet<>();
        for (State state : states) {
            nextStates.addAll(state.getTransitions(symbol));
        }
        return nextStates;
    }

    public static NFA characterClass(String chars) {
        State start = new State();
        State end = new State();
        end.isFinal = true;
        for (char c : chars.toCharArray()) {
            start.addTransition(c, end);
        }
        return new NFA(start, end);
    }

    public void printEpsilonClosures() {
        for (State s : states) {
            Set<State> closure = epsilonClosure(s);
            System.out.print(s.name + " -> ");
            for (State stateInClosure : closure) {
                System.out.print(stateInClosure.name + " ");
            }
            System.out.println();
        }
    }
    

}
