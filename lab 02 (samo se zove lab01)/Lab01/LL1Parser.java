import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Stack;

public class LL1Parser {
    private MPLexer lexer;
    private Yytoken currentToken;

    // Sintaksna tabela
    private static final int[][] SYNTAX_TABLE = {
        // for,   ID, CONST,  ,,   +,   [,   ], apply,  $
        { 1,    0,    0,    0,   0,   0,   0,    0,   0}, // ApplyExpression
        { 0,    2,    0,    0,   0,   0,   0,    0,   0}, // NameList
        { 0,    0,    0,    3,   0,   0,   4,    0,   0}, // NameList'
        { 0,    5,    5,    0,   0,   0,   0,    0,   0}, // Expression
        { 0,    0,    0,    0,   6,   0,   0,    0,   7}, // Expression'
        { 0,    8,    9,    0,   0,   0,   0,    0,   0}  // Term
    };

    // Pravila gramatike
    private static final String[][] PRODUCTIONS = {
        {}, // prazno
        {"for", "ID", "in", "[", "NameList", "]", "apply", "Expression"}, // Rule 1
        {"ID", "NameList'"},                                             // Rule 2
        {",", "ID", "NameList'"},                                        // Rule 3
        {},                                                              // Rule 4 (epsilon)
        {"Term", "Expression'"},                                         // Rule 5
        {"+", "Term", "Expression'"},                                    // Rule 6
        {},                                                              // Rule 7 (epsilon)
        {"ID"},                                                          // Rule 8
        {"CONST"}                                                        // Rule 9
    };

    public LL1Parser(MPLexer lexer) {
        this.lexer = lexer;
        this.currentToken = getNextToken();
    }

    private Yytoken getNextToken() {
        try {
            return lexer.next_token();
        } catch (Exception e) {
            throw new RuntimeException("Error reading token: " + e.getMessage());
        }
    }

    public void parse() {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push("ApplyExpression");

        while (!stack.isEmpty()) {
            String top = stack.pop();

            // If top is a terminal
            if (isTerminal(top)) {
                if (top.equals(getTerminalName(currentToken.m_index))) {
                    currentToken = getNextToken();
                } else {
                    throw new RuntimeException("Syntax error: unexpected token 1 " + currentToken.m_text);
                }
            } else {
                // Non-terminal
                int row = getNonTerminalIndex(top);
                int col = getTerminalIndex(currentToken.m_index);

                int rule = SYNTAX_TABLE[row][col];
                //System.out.println(SYNTAX_TABLE[0][0]);
                if (rule == 0) {
                    throw new RuntimeException("Syntax error: unexpected token THIS " + currentToken.m_text + SYNTAX_TABLE[0][0] + "HEY");
                }

                String[] production = PRODUCTIONS[rule];
                for (int i = production.length - 1; i >= 0; i--) {
                    if (!production[i].equals("")) { // Ignore epsilon
                        stack.push(production[i]);
                    }
                }
            }
        }

        if (currentToken.m_index != sym.EOF) {
            throw new RuntimeException("Syntax error: input not fully consumed");
        }

        System.out.println("Parsing successful!");
    }
    private String getTerminalName(int tokenIndex) {
        switch (tokenIndex) {
            case sym.FOR: return "for";
            case sym.ID: return "ID";
            case sym.CONST: return "CONST";
            case sym.COMMA: return ",";
            case sym.PLUS: return "+";
            case sym.SQUARELEFTPAR: return "[";
            case sym.SQUARERIGHTPAR: return "]";
            case sym.APPLY: return "apply";
            case sym.EOF: return "$";
            case sym.IN: return "in";
            default: throw new RuntimeException("Unknown token index: " + tokenIndex);
        }
    }

    private int getTerminalIndex(int tokenIndex) {
        switch (tokenIndex) {
            case sym.FOR: return 0;
            case sym.ID: return 1;
            case sym.CONST: return 2;
            case sym.COMMA: return 3;
            case sym.PLUS: return 4;
            case sym.SQUARELEFTPAR: return 5;
            case sym.SQUARERIGHTPAR: return 6;
            case sym.APPLY: return 7;
            case sym.EOF: return 8;
            default: throw new RuntimeException("Unknown terminal index: " + tokenIndex);
        }
    }


    private boolean isTerminal(String symbol) {
        return symbol.equals("for") ||
        		symbol.equals("in") ||
               symbol.equals("ID") ||
               symbol.equals("CONST") ||
               symbol.equals(",") ||
               symbol.equals("+") ||
               symbol.equals("[") ||
               symbol.equals("]") ||
               symbol.equals("apply") ||
               symbol.equals("$");
    }


    private int getNonTerminalIndex(String nonTerminal) {
        switch (nonTerminal) {
            case "ApplyExpression": return 0;
            case "NameList": return 1;
            case "NameList'": return 2;
            case "Expression": return 3;
            case "Expression'": return 4;
            case "Term": return 5;
            default: throw new RuntimeException("Unknown non-terminal: " + nonTerminal);
        }
    }

    private int getTerminalIndex(String terminal) {
        switch (terminal) {
            case "for": return 0;
            case "ID": return 1;
            case "CONST": return 2;
            case ",": return 3;
            case "+": return 4;
            case "[": return 5;
            case "]": return 6;
            case "apply": return 7;
            case "$": return 8;
            default: throw new RuntimeException("Unknown terminal: " + terminal);
        }
    }

    public static void main(String[] args) {
        //MPLexer lexer = new MPLexer(System.in);
    	//MPLexer lexer = new MPLexer(new java.io.InputStreamReader(System.in));
    	MPLexer lexer = null;
		try {
			lexer = new MPLexer(new FileReader("testinput.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LL1Parser parser = new LL1Parser(lexer);

        try {
            parser.parse();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
