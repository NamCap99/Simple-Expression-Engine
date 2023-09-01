import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

class JunitTest {

    private RegexEngine regexEngine;

    @BeforeEach
    void setUp() {
        regexEngine = new RegexEngine();
    }

    @Test
    void testMatches_singleCharacter() {
        assertTrue(regexEngine.matches("a", "a"));
        assertFalse(regexEngine.matches("a", "b"));
    }

    @Test
    void testMatches_kleeneStar() {
        assertTrue(regexEngine.matches("a*", ""));
        assertTrue(regexEngine.matches("a*", "a"));
        assertTrue(regexEngine.matches("a*", "aa"));
        assertTrue(regexEngine.matches("a*", "aaa"));
        assertFalse(regexEngine.matches("a*", "b"));
    }

    @Test
    void testMatches_concatenation() {
        assertTrue(regexEngine.matches("ab", "ab"));
        assertFalse(regexEngine.matches("ab", "ba"));
        assertFalse(regexEngine.matches("ab", "a"));
        assertFalse(regexEngine.matches("ab", "b"));
    }

    @Test
    void testMatches_union() {
        assertTrue(regexEngine.matches("a|b", "a"));
        assertTrue(regexEngine.matches("a|b", "b"));
        assertFalse(regexEngine.matches("a|b", "ab"));
    }

    @Test
    void testMatches_complexPatterns() {
        assertTrue(regexEngine.matches("a*b*", "aabb"));
        assertTrue(regexEngine.matches("a*b*", "aa"));
        assertTrue(regexEngine.matches("a*b*", "bb"));
        assertFalse(regexEngine.matches("a*b*", "ba"));
    }

    @Test
    void testEpsilonClosure() {
        State s1 = new State();
        State s2 = new State();
        s1.addTransition('\0', s2);
        NFA nfa = new NFA(s1, s2);

        Set<State> closure = nfa.epsilonClosure(s1);
        assertTrue(closure.contains(s1));
        assertTrue(closure.contains(s2));
    }

}
