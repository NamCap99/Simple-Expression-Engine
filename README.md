# Regular Expression Engine

A custom-built regular expression engine designed to match simple patterns. The engine operates in two modes: normal mode for straightforward pattern matching and a verbose mode for visualizing and understanding the transition process of the regular expression.

## Features

### 1. Pattern Matching
Supports basic regular expression patterns, including:
   - Literal characters (e.g., `a`, `b`, `c`)
   - The Kleene star (`*`)
   - Concatenation
   - Alternation (`|`)

### 2. Normal Mode
In this mode, the engine evaluates if a given string matches the provided regular expression pattern.

### 3. Verbose Mode
Provides a deep dive into the matching process:
   - Outputs a detailed transition table for the given regular expression pattern.
   - Visual representation of the process helps in understanding the intricate workings of regular expressions.

## Usage

### Initialization
Initialize the `RegexEngine` class.

```java
RegexEngine regexEngine = new RegexEngine();

/** Normal mode
 * boolean result = regexEngine.matches("a*b", "aaab"); // true
 */ 

/** Verbose mode
 * regexEngine.verboseMode("(ab)*|c+", "aabbcc");
 */ 

Testing: Comprehensive unit tests are available in the JunitTest.java file to ensure the reliability and correctness of the engine.

Limitations:
- This engine is intended for educational purposes and to provide an introduction to the world of regular expressions.
- While it captures the essence of regex pattern matching, it may not cover all edge cases or possess the performance of commercial regex libraries.

Future Enhancements:
- Introduction of more complex regex functionalities such as character classes, quantifiers, and lookaheads.
- Performance optimizations and enhancements.

Contributors
[Nam Manh Cap]
