import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
    public static int stateCounter = 0;
    
    public String name;
    public Map<Character, List<State>> transitions = new HashMap<>();
    public boolean isFinal = false;

    public State() {
        this.name = "q" + stateCounter;
        stateCounter++;
    }

    public void addTransition(char input, State destination) {
        if (!transitions.containsKey(input)) {
            transitions.put(input, new ArrayList<>());
        }
        transitions.get(input).add(destination);
    }

    public Collection<? extends State> getTransitions(char symbol) {
        return transitions.getOrDefault(symbol, new ArrayList<>());
    }
}
