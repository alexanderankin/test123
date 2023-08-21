package beauty.parsers.json;

import beauty.parsers.ErrorListener;
import beauty.parsers.ParserException;
import static beauty.parsers.json.JSONParser.*;

import java.io.*;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


/**
 * There is NO support for comments.
 */
public class JSONBeautyListener extends JSONBaseListener {
    private String output = "";    

    private int tabCount = 0;

    private String tab;
    
    // bracket styles
    public static final int ATTACHED = 1;
    public static final int BROKEN = 2;
    private boolean brokenBracket = true;
    

    // stack for holding intermediate formatted parts
    private Deque<String> stack = new ArrayDeque<String>();

    public JSONBeautyListener(boolean softTabs, int tabWidth) {

        if (softTabs) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < tabWidth; i++) {
                sb.append(' ');
            }
            tab = sb.toString();
        }
        else {
            tab = "\t";
        }
    }

    // for testing
    public static void main(String [] args) {
        if (args == null) {
            return;
        }
        ErrorListener errorListener = null;
        try {
            // set up the parser
            java.io.FileReader input = new java.io.FileReader(args[0]);
            CharStream antlrInput = CharStreams.fromReader(input);
            JSONLexer lexer = new JSONLexer(antlrInput);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JSONParser jsonParser = new JSONParser(tokens);
            // add an error handler that stops beautifying on any parsing error
            jsonParser.removeErrorListeners();
            errorListener = new ErrorListener();
            jsonParser.addErrorListener(errorListener);
            jsonParser.setErrorHandler(new DefaultErrorStrategy());
            // parse and beautify the buffer contents
            ParseTree tree = jsonParser.json();
            ParseTreeWalker walker = new ParseTreeWalker();
            JSONBeautyListener listener = new JSONBeautyListener(true, 4);
            walker.walk(listener, tree);
            System.out.println(listener.getText());
        }
        catch ( Exception e) {
            java.util.List<ParserException> errors = errorListener.getErrors();

            if (errors != null && errors.size() > 0) {
                System.out.println("+++++ json error: " + errors.get(0));
            }
        }
    }

    public void setBracketStyle(int style) {
        brokenBracket = BROKEN == style;
    }
    
    // the final product
    public String getText() {
        return output;
    }

    // ==============================================================================
    // parser methods
    // ==============================================================================
    
    /**
     * json:   object
     *     |   array
     *     ;
     */
    @Override
    public void exitJson(JSONParser.JsonContext ctx) {
        if (ctx.object() != null || ctx.array() != null) {
            output = stack.pop().trim();
        }
    }

    /**
     * object
     *     :   LBRACE pair (',' pair)* RBRACE
     *     |   LBRACE RBRACE // empty object
     *     ;
     */
    @Override public void exitObject(JSONParser.ObjectContext ctx) {
        String rb = stack.pop();    // }
        String pairs = "";
        
        if (ctx.pair() != null && ctx.pair().size() > 0) {
            List<String> parts = new ArrayList<String>();
            for (int i = 0; i < ctx.pair().size() * 2 - 1; i++) {     // pairs and commas
                parts.add(0, stack.pop());
            }
            StringBuilder sb = new StringBuilder();
            for (String p : parts) {
                sb.append(p);
                if (p.equals(",")) {
                    sb.append('\n');    
                }
            }
            pairs = sb.toString();
        }
        String lb = stack.pop();    // {
        StringBuilder sb = new StringBuilder();
        if (brokenBracket) {
            sb.append('\n');
            sb.append(indent(lb));
        }
        else {
            sb.append(lb);
        }
        ++tabCount;
        if (!pairs.isEmpty()) {
            sb.append('\n');
            sb.append(indent(pairs));
            sb.append('\n');
        }
        --tabCount;
        if (brokenBracket) {
            sb.append(indent(rb));
        }
        else {
            sb.append(rb);
        }
        stack.push(sb.toString());
    }

    /**
     * pair:   STRING ':' value ;
     */
    @Override public void exitPair(JSONParser.PairContext ctx) {
        String value = stack.pop();
        String colon = stack.pop();
        String name = stack.pop();
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(colon).append(' ').append(value);
        stack.push(sb.toString());
    }

    /**
     * array
     *     :   LSQUARE value (',' value)* RSQUARE
     *     |   LSQUARE RSQUARE // empty array
     *     ;
     */
    @Override public void exitArray(JSONParser.ArrayContext ctx) {
        String rs = stack.pop();    // }
        String values = "";
        
        if (ctx.value() != null && ctx.value().size() > 0) {
            List<String> parts = new ArrayList<String>();
            for (int i = 0; i < ctx.value().size() * 2 - 1; i++) {     // values and commas
                parts.add(0, stack.pop());
            }
            StringBuilder sb = new StringBuilder();
            for (String p : parts) {
                sb.append(p.trim());
                if (p.equals(",")) {
                    sb.append('\n');    
                }
            }
            values = sb.toString();
        }
        String ls = stack.pop();    // {
        StringBuilder sb = new StringBuilder();
        if (brokenBracket) {
            sb.append('\n');
            sb.append(indent(ls));
            sb.append('\n');
        }
        else {
            sb.append(ls);
        }
        ++tabCount;
        if (!values.isEmpty()) {
            sb.append(indent(values));
        }
        --tabCount;
        if (brokenBracket) {
            sb.append('\n');
            sb.append(indent(rs));
        }
        else {
            sb.append(rs);
        }
        stack.push(sb.toString());
    }

    /**
     * value
     *     :   STRING
     *     |   NUMBER
     *     |   object  // recursion
     *     |   array   // recursion
     *     ;
     */
    @Override public void exitValue(JSONParser.ValueContext ctx) {
        // nothing to do here, one of the choices should already be on the stack
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        String terminalText = node.getText().trim();
        stack.push(terminalText);
    }

    // ==============================================================================
    // formatting methods
    // ==============================================================================
    private String getIndent() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tabCount; i++) {
            sb.append(tab);
        }
        return sb.toString();
    }

    /**
     * Split the given string into lines, trim each line, then add tabcount * tab
     * whitespace to the start of each line and a new line at the end of each line.
     * All trailing new lines are removed.
     */
    private String indent(String s) {
        StringBuilder sb = new StringBuilder();
        String [] lines = s.split("\n");
        String indent = getIndent();

        for ( String line : lines) {
            sb.append(indent);
            //line = line.trim();
            sb.append(line).append('\n');
        }
        return trimEnd(sb);
    }

    /**
     * StringBuilder doesn't have a "trim" method. This trims whitespace from
     * the end of the string builder. Whitespace on the front is not touched.
     * There are two ways to use this, pass in a StringBuilder then use the same
     * StringBuilder, it's end will have been trimmed, or pass in a StringBuilder
     * and use the returned String.
     * @param sb The StringBuilder to trim.
     * @return The trimmed string.
     */
    public String trimEnd(StringBuilder sb) {
        if (sb == null || sb.length() == 0) {
            return "";
        }
        while(sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    /**
     * Removes whitespace from the end of a string.
     * @param s The string to trim.
     * @return The trimmed string.
     */
    public String trimEnd(String s) {
        return trimEnd(new StringBuilder(s));   
    }
    
}
