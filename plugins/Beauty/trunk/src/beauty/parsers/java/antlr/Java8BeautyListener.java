// :folding=explicit:
package beauty.parsers.java.antlr;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

/**
 * Beautifier for Java 8 and below.
 * TODO: the ending folding markers that jEdit recognizes, // }}} are treated as comments
 *   and associated with the next token rather than the previous
 */
public class Java8BeautyListener implements Java8Listener {
    
    // token stream, need this to capture comments
    BufferedTokenStream tokens;
    
    // accumulate final output here
    private StringBuilder output;       // NOPMD
    
    // for tabs, 'tabCount' is current tab level, 'tab' is the string to use
    // for a tab, either '\t' or some number of spaces
    private int tabCount = 0;
    private String tab;
    
    // stack for holding intermediate formatted parts 
    private Deque<String> stack = new ArrayDeque<String>();
    
    // constants for trimming strings
    public static final int START = 1;
    public static final int END = 2;
    public static final int BOTH = 3;
    
    // bracket styles
    public static final int ATTACHED = 1;
    public static final int BROKEN = 2;
    
    // format settings
    private boolean softTabs = true;
    private int tabWidth = 4;
    private int bracketStyle = ATTACHED;        
    private boolean brokenBracket = false;
    private boolean breakElse = true;
    private boolean padParens = false;
    private boolean padOperators = true;
    private int blankLinesBeforePackage = 0;            
    private int blankLinesAfterPackage = 1;             
    private int blankLinesAfterImports = 2;             
    private boolean sortImports = true;                 
    private boolean groupImports = true;                
    private int blankLinesBetweenImportGroups = 1;      
    private int blankLinesAfterClassDeclaration = 1; 
    private int blankLinesAfterClassBody = 2;
    private int blankLinesBeforeMethods = 1;  
    private int blankLinesAfterMethods = 1;
    private boolean sortModifiers = true;               
    private int collapseMultipleBlankLinesTo = 2; 
    
    
    /**
     * @param initialSize Initial size of the output buffer.
     * @param softTabs If true, use spaces rather than '\t' for tabs.
     * @param tabWidth The number of spaces to use if softTabs.
     */
    public Java8BeautyListener(int initialSize, BufferedTokenStream tokenStream ) {
        output = new StringBuilder(initialSize);
        this.tokens = tokenStream;
    }
    
    /**
     * Set up tabs.    
     */
    private void init() {
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
    
/*
--------------------------------------------------------------------------------
Formatting settings
--------------------------------------------------------------------------------
*/
//{{{
    public void setIndentWidth(int i) {
        tabWidth = i;
    }

    public void setUseSoftTabs(boolean b) {
        softTabs = b;
    }
            
    public void setBracketStyle(int style) {
        bracketStyle = style;
        brokenBracket = BROKEN == style;
    }
    
    public void setBreakElse(boolean b) {
        breakElse = b;
    }
    
    public void setPadParens(boolean pad) {
        padParens = pad; 
    }
    
    public void setPadOperators(boolean pad) {
        padOperators = pad;   
    }
    
    public void setBlankLinesBeforePackage(int lines) {
        blankLinesBeforePackage = lines;    
    }
    
    public void setBlankLinesAfterPackage(int lines) {
        blankLinesAfterPackage = lines;
    }
    
    public void setBlankLinesAfterImports(int lines) {
        blankLinesAfterImports = lines;
    }

    public void setSortImports(boolean sort) {
        sortImports = sort;
    }
    
    public void setGroupImports(boolean group) {
        groupImports = group;
    }
    
    public void setBlankLinesBetweenImportGroups(int lines) {
        blankLinesBetweenImportGroups = lines;
    }
    
    public void setBlankLinesAfterClassDeclaration(int lines) {
        blankLinesAfterClassDeclaration = lines;
    }
    
    public void setBlankLinesAfterClassBody(int lines) {
        blankLinesAfterClassBody = lines;
    }
    
    public void setBlankLinesBeforeMethods(int lines) {
        blankLinesBeforeMethods = lines;
    }
    
    public void setBlankLinesAfterMethods(int lines) {
        blankLinesAfterMethods = lines;
    }
    
    public void setSortModifiers(boolean sort) {
        sortModifiers = sort;
    }
    
    public void setCollapseMultipleBlankLinesTo(int lines) {
        collapseMultipleBlankLinesTo = lines;        
    }
    
//}}}    
/*
--------------------------------------------------------------------------------
Formatting methods.
--------------------------------------------------------------------------------
*/
//{{{    
    /**
     * @return The formatted string for the file contents.    
     */
    public String getText() {
        return output.toString();   
    }
    
    /**
     * @return A string with the current indent amount.   
     */
    private String getIndent() {
        StringBuilder sb = new StringBuilder();    
        for ( int i = 0; i < tabCount; i++ ) {
            sb.insert(0, tab );
        }
        return sb.toString();
    }
    
    /**
     * Add tabCount tabs to the beginning of the given string builder.
     */
    private void indent(StringBuilder sb) {
        if (sb == null) {
            sb = output;   
        }
        for ( int i = 0; i < tabCount; i++ ) {
            sb.insert(0, tab );
        }
    }
    
    /**
     * Split the given string into lines and indent each line.    
     */
    private String indent(String s) {
        StringBuilder sb = new StringBuilder();
        String[] lines = s.split("\n");
        String indent = getIndent();
        for (String line : lines) {
            if (indent.isEmpty() || !line.startsWith(indent)) {
                line = line.trim();
                if (line.startsWith("*")) {
                    // assume it's a comment line
                    // TODO: verify this is always true
                    line = new StringBuilder(indent).append(' ').append(line).toString();
                }
                else if (!"".equals(line)) {
                    line = new StringBuilder(indent).append(line).toString();
                }
            }
            sb.append(line);
            if (!line.endsWith("\n")) {
                sb.append('\n');    
            }
        }
        return sb.toString();
    }
    
    /**
     * Assumes the given string is already indented but needs one additional indentation.    
     */
    private String indentAgain(String s) {
        StringBuilder sb = new StringBuilder();
        String[] lines = s.split("\n");
        for (String line : lines) {
            sb.append(tab).append(line);
            if (!line.endsWith("\n")) {
                sb.append('\n');    
            }
        }
        return sb.toString();
    }
    
    private String indentRBrace(String s) {
        s = s.trim();
        String start = s.substring(0, s.indexOf('}'));
        String rbrace = s.substring(s.indexOf('}'));
        if (!start.isEmpty()) {
            ++ tabCount;
            start = indent(start);
            -- tabCount;
        }
        rbrace = indent(rbrace);
        return start + rbrace;
    }
    
    /**
     * Add blank lines to the end of the last item on the stack.  Note that all
     * blank lines are first removed then exactly <code>howMany</code> are added.
     * @return true if blank lines were added to the last item on the stack, false
     * if there were no items on the stack.
     */
    private boolean addBlankLines(int howMany) {
        if (!stack.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < howMany + 1; i++) {
                sb.append('\n');    
            }
            String ending = sb.toString();
            String last = stack.pop();
            last = new StringBuilder(removeBlankLines(last, END)).append(ending).toString();
            stack.push(last);
            return true;
        }
        return false;
    }
    
    /**
     * Remove blank lines from the ends of the given string.
     * @param s The string to remove blank lines from.
     * @param whichEnd Which end of the string to remove blank lines from, 
     * one of START, END, or BOTH.
     */
    private String removeBlankLines(String s, int whichEnd) {
        if (s == null || s.isEmpty()) {
            return s;    
        }
        StringBuilder sb = new StringBuilder(s);
        switch(whichEnd) {
            case START:
                while(sb.length() > 0 && sb.charAt(0) == '\n') {
                    sb.deleteCharAt(0);
                }
                break;
            case END:
                while(sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                break;
            case BOTH:
            default:
                while(sb.length() > 0 && sb.charAt(0) == '\n') {
                    sb.deleteCharAt(0);
                }
                while(sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                break;
        }
        return sb.toString();
    }
    
    /**
     * @return A string with the appropriate amount of blank lines.    
     */
    private String getBlankLines(int howMany) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < howMany; i++) {
            sb.append('\n');    
        }
        return sb.toString();
    }
    
    /**
     * Pads parenthesis so "(" becomes "( " and ")" becomes " )".
     * @param paren The paren to pad
     */
    private String padParen(String paren) {
        return padParen(paren, " ");    
    }
    
    /**
     * Pads parenthesis so "(" becomes "( " and ")" becomes " )".
     * @param paren The paren to pad
     * @param item The item immediately following or preceding the paren.
     */
    private String padParen(String paren, String item) {
        if (paren == null || paren.isEmpty() || !padParens || item == null || item.isEmpty()) {
            return paren;    
        }
        StringBuilder sb = new StringBuilder(paren);
        int index = sb.indexOf("(");
        if (index > -1) {
            sb.insert(index + 1, " ");
        }
        else {
            index = sb.indexOf(")");
            if (index > -1) {
                sb.insert(index == 0 ? 0 : index - 1, " ");
            }
        }
        return sb.toString();
    }
    
    /**
     * StringBuilder doesn't have an "endsWith" method.
     * @param sb The string builder to test.
     * @param s The string to test for.
     * @return true if the string builder ends with <code>s</code>.
     */
    public boolean endsWith(StringBuilder sb, String s) {
        if (sb == null && s == null)
            return true;
        if (sb == null && s != null)
            return false;
        if (sb.length() < s.length())
            return false;
        String end = sb.substring(sb.length() - s.length());
        return end.equals(s);
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
    
    /**
     * StringBuilder doesn't have a "trim" method. This trims whitespace from
     * the start of the string builder. Whitespace on the end is not touched.
     * There are two ways to use this, pass in a StringBuilder then use the same
     * StringBuilder, it's start will have been trimmed, or pass in a StringBuilder
     * and use the returned String.
     * @param sb The StringBuilder to trim.
     * @return The trimmed string.
     */
    public String trimFront(StringBuilder sb) {
        if (sb == null || sb.length() == 0) {
            return "";
        }
        while(sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }
    
    /**
     * Removes whitespace from the start of a string.
     * @param s The string to trim.
     * @return The trimmed string.
     */
    public String trimFront(String s) {
        return trimFront(new StringBuilder(s));    
    }
    
    /**
     * Pops 'howMany' items off of the stack, reverses the order, then
     * assembles a string using 'separator' between the items. A separator is
     * not appended to the end of the string, and the separator is only inserted
     * every 'howOften' items. For example:
     * Given "a,b,c" on the stack, where each character is a separate string on the stack,
     * calling <code>reverse(5, " ", 2)</code> would return "a, b, c".
     * @param howMany How many items to pop off of the stack and assemble into a string.
     * @param separator A string to be placed between each item. The separator is only
     * added if the item does not already end with the separator.
     * @param howOften How often a separator should be inserted.
     * @return A string with the items reversed and separated.
     */
    private String reverse(int howMany, String separator, int howOften) {
        StringBuilder sb = new StringBuilder();
	    List<String> list = reverse(howMany);
        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            sb.append(item);
            if (i < list.size() - 1) {
                if ((howOften == 1 || i % howOften == 1) && !item.endsWith(separator)) {    // NOPMD
                    sb.append(separator); 
                }
            }
        }
	    return sb.toString();
    }
    
    /**
     * Pops 'howMany' items off of the stack, reverses the order, then
     * assembles a string using 'separator' between the items. A separator is
     * not appended to the end of the string, and the separator is inserted
     * each item popped off of the stack.
     * Given "a,b,c" on the stack, where each character is a separate string on the stack,
     * calling <code>reverse(5, " ")</code> would return "a , b , c".
     * @param howMany How many items to pop off of the stack and assemble into a string.
     * @param separator A string to be placed between each item.
     * @return A string with the items reversed and separated.
     */
    private String reverse(int howMany, String separator) {
        return reverse(howMany, separator, 1);
    }
    
    /**
     * Pops 'howMany' items off of the stack, puts the items into a list, and reverses
     * the list. 
     * @param howMany How many items to pop off of the stack to add to the list.
     * @return A list of stack items in reverse order.
     */
    private List<String> reverse(int howMany) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < howMany && !stack.isEmpty(); i++) {
            list.add(stack.pop());    
        }
        Collections.reverse(list);
        return list;
    }
    
    /**
     * All comments are handled here.    
     */
	private void processComments(TerminalNode node) {
        Token token = node.getSymbol(); 
        int tokenIndex = token.getTokenIndex();

        // ...
        // End of Line Comments
        // ...
	    // check to the right of the current token for an end of line comment,
	    // this handles both // and /* end of line comments. Only end of line
	    // comments are handled in this section, and such comments are appended
	    // to the end of the previous stack item. All other comments are handled
	    // by looking to the left of the current token and are prepended to the
	    // previous stack item.
        List<Token> commentTokens = tokens.getHiddenTokensToRight(tokenIndex, Java8Lexer.COMMENTS);
        if (commentTokens != null && commentTokens.size() > 0 && token.getType() != Token.EOF) {
            // get the very next comment
            Token nextCommentToken = commentTokens.get(0);
            int commentIndex = nextCommentToken.getTokenIndex();
            
            // get the hidden tokens between the current token and the next non-hidden token
            List<Token> hiddenTokens = tokens.getHiddenTokensToRight(tokenIndex);
            
            // check if there is a line ender between the current token and the comment token
            // is it "token \n comment" or "token comment \n"?
            boolean hasLineEnder = false;
            if (hiddenTokens != null && hiddenTokens.size() > 0) {
                for (int i = 0; i < commentIndex - tokenIndex; i++) {
                    Token t = hiddenTokens.get(i);
                    String tokenText = t.getText();
                    if (t.getChannel() == Java8Lexer.WHITESPACE &&
                        (tokenText.indexOf('\n') > -1 || tokenText.indexOf('\r') > -1)) {
                        hasLineEnder = true;
                        break;
                    }
                }
                if (!hasLineEnder) {
                    // have 'token comment' now check for line ender after the comment
                    hasLineEnder = false;
                    for (int i = commentIndex - tokenIndex; i < hiddenTokens.size(); i++) {
                        Token t = hiddenTokens.get(i);
                        String tokenText = t.getText();
                        if (t.getChannel() == Java8Lexer.WHITESPACE &&
                            (tokenText.indexOf('\n') > -1 || tokenText.indexOf('\r') > -1)) {
                            hasLineEnder = true;
                            break;
                        }
                    }
                    if (hasLineEnder) {
                        // have "token comment \n", append this comment to the end
                        // of the previous item.
                        StringBuilder item = new StringBuilder(stack.pop());
                        String comment = nextCommentToken.getText();
                        if (item.indexOf(comment) == -1) {
                            item.append(tab).append(comment);
                            stack.push(item.toString());
                            tokens.seek(commentIndex + 1);
                            return;
                        }
                    }
                }
            }
	    }
        
	    // ...
	    // Line, Regular, and Doc Comments, and jEdit fold markers
	    // ...
	    // check to the left of the current token for line. regular, and doc comments
        commentTokens = tokens.getHiddenTokensToLeft(tokenIndex, Java8Lexer.COMMENTS);
        if (commentTokens != null && commentTokens.size() > 0) {
            // there could be multiple line comments
            // like this comment is on two lines
            Collections.reverse(commentTokens);
            for (Token commentToken : commentTokens) {
                if ( commentToken != null) {
                    String comment = commentToken.getText();
                    String current = stack.pop();
                    String previous = stack.peek();
                    stack.push(current);
                    if (previous != null && previous.indexOf(comment) > -1 && previous.indexOf(comment.trim()) > -1) {
                        // already have this comment added as an end of line comment
                        // for the previous token
                        continue;        
                    }
                    switch(commentToken.getType()) {
                        case Java8Lexer.LINE_COMMENT:
                            comment = formatLineComment(comment);
                            break;
                        case Java8Lexer.JEDIT_FOLD_MARKER:
                            comment = formatJEditFoldMarkerComment(comment);
                            current = stack.pop();
                            previous = stack.peek();
                            if (previous != null && previous.indexOf(comment) == -1) {
                                previous = stack.pop();
                                previous = new StringBuilder(trimEnd(previous)).append(comment).toString();
                                stack.push(previous);
                            }
                            stack.push(current);
                            continue;
                        case Java8Lexer.COMMENT:
                            // check if this is an in-line  or trailing comment, e.g.
                            // something /star comment star/ more things or
                            // something /star comment star/\n
                            int commentTokenIndex = commentToken.getTokenIndex();
                            boolean tokenOnLeft = true;
                            boolean tokenOnRight = true;
                            List<Token> wsTokens = tokens.getHiddenTokensToLeft(commentTokenIndex, Java8Lexer.WHITESPACE);
                            if (wsTokens != null && wsTokens.size() > 0) {
                                for (Token wsToken : wsTokens) {
                                    String wsText = wsToken.getText();
                                    if (wsText.indexOf('\n') > -1 || wsText.indexOf('\r') > -1) {
                                        tokenOnLeft = false;
                                        break;
                                    }
                                }
                            }
                            wsTokens = tokens.getHiddenTokensToRight(commentTokenIndex, Java8Lexer.WHITESPACE);
                            if (wsTokens != null && wsTokens.size() > 0) {
                                for (Token wsToken : wsTokens) {
                                    String wsText = wsToken.getText();
                                    if (wsText.indexOf('\n') > -1 || wsText.indexOf('\r') > -1) {
                                        tokenOnRight = false;
                                        break;
                                    }
                                }
                            }
                            if (tokenOnLeft && tokenOnRight) {
                                comment += " ";     // NOPMD
                            }
                            else {
                                comment = formatComment(comment);   
                            }
                            break;
                        case Java8Lexer.DOC_COMMENT: 
                            // TODO: About javadoc comments, this is from the Sun documentation at
                            // http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html
                            // A doc comment is written in HTML and must precede a class, field, constructor
                            // or method declaration.
                            // So -- should I only worry about doc comments in those places?
                            comment = '\n'+ formatDocComment(comment);
                            break;
                    }
                    if (stack.size() == 0) {
                        stack.push(comment);
                    }
                    else {
                        String last = stack.peek();
                        if (last != null && last.indexOf(comment) == -1) {
                            last = stack.pop();
                            last = new StringBuilder(comment).append(last).toString();
                            stack.push(last);
                        }
                    }
                }
            }
        }
	}
	
	/**
 	 * Formats a line comment. Current rule is to ensure there is a space following the
 	 * slashes, so "//comment" becomes "// comment". Also handle the case where there
 	 * are more than 2 slashes the same way, so "////comment" becomes "//// comment".
 	 * @param comment An end of line comment.
 	 * @return The formatted comment.
 	 */
	private String formatLineComment(String comment) {
	    // ensure there is a space after the comment start. Handle the case
	    // of multiple /, e.g. ////this is a comment
	    // should look like    //// this is a comment
	    String c = comment.trim();
	    int slashCount = 0;
	    for (int i = 0; i < c.length(); i++) {
	        if (c.charAt(i) == '/') {
	            ++ slashCount;    
	        }
	        else {
	            break;    
	        }
	    }
	    c = c.substring(slashCount).trim();
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < slashCount; i++) {
	        sb.append('/');
	    }
	    sb.append(' ').append(c);
	    c = sb.toString();
	    return indent(c);
	}
	
	/**
 	 * Formats a jEdit fold marker, // }}}. Current rule is to ensure there is a space following the
 	 * slashes, so "//}}}" becomes "// }}}". Also handle the case where there
 	 * are more than 2 slashes the same way, so "////}}}" becomes "//// }}}".
 	 * @param comment A jEdit fold marker.
 	 * @return The formatted fold marker.
 	 */
	private String formatJEditFoldMarkerComment(String comment) {
	    // ensure there is a space after the comment start. Handle the case
	    // of multiple /, e.g. ////}}}
	    // should look like    //// }}}
	    String c = comment.trim();
	    int slashCount = 0;
	    for (int i = 0; i < c.length(); i++) {
	        if (c.charAt(i) == '/') {
	            ++ slashCount;    
	        }
	        else {
	            break;    
	        }
	    }
	    c = c.substring(slashCount).trim();
	    StringBuilder sb = new StringBuilder();
	    sb.append('\n');
	    for (int i = 0; i < slashCount; i++) {
	        sb.append('/');
	    }
	    sb.append(' ').append(c).append('\n');
	    c = sb.toString();
	    c = indent(c);
	    return c;
	}
	
	/**
 	 * Formats a regular block comment. This method splits the 
 	 * given comment into lines, indents each line, and ensures there is a * at the
 	 * start of each line.
 	 * @param comment A block comment.
 	 * @return The formatted comment.
 	 */
	private String formatComment(String comment) {
	    if (comment == null || comment.isEmpty()) {
	        return "";
	    }
        String[] lines = comment.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("/*")) {
                sb.append(line).append('\n');    
            }
            else if (line.startsWith("*")) {
                sb.append(' ').append(line).append('\n');
            }
            else {
                sb.append(" * ").append(line).append('\n');    
            }
        }
        return sb.toString();
	}
	
	/**
	 * Notes from Sun's javadoc documentation:
 	 * Each line in the comment is indented to align with the code below the comment.
 	 * The first line contains the begin-comment delimiter ( /**).
 	 * Starting with Javadoc 1.4, the leading asterisks are optional.
 	 * Write the first sentence as a short summary of the method, as Javadoc automatically places it in the method summary table (and index).
 	 * Notice the inline tag {@link URL}, which converts to an HTML hyperlink pointing to the documentation for the URL class. This inline tag can be used anywhere that a comment can be written, such as in the text following block tags.
 	 * If you have more than one paragraph in the doc comment, separate the paragraphs with a <p> paragraph tag, as shown.
 	 * Insert a blank comment line between the description and the list of tags, as shown.
 	 * The first line that begins with an "@" character ends the description. There is only one description block per doc comment; you cannot continue the description following block tags.
 	 * The last line contains the end-comment delimiter. Note that unlike the begin-comment delimiter, the end-comment contains only a single asterisk.
 	 */
	private String formatDocComment(String comment) {
	    return formatComment(comment);
	}
	
	/**
 	 * Handles new line tokens preceding the <code>node</code>.	
 	 */
	private void processWhitespace(TerminalNode node) {
        Token token = node.getSymbol();
        
        int tokenIndex = token.getTokenIndex();
        List<Token> wsTokens = tokens.getHiddenTokensToLeft(tokenIndex, Java8Lexer.WHITESPACE);
        if (wsTokens != null && wsTokens.size() > 0) {
            // count the number of new line tokens preceding this token
            int nlCount = 0;
            for (Token ws : wsTokens) {
                String s = ws.getText();
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '\n') {
                        ++ nlCount;    
                    }
                }
            }
            // add no more than collapseMultipleBlankLinesTo new lines to the
            // front of the token
            if (nlCount > 0) {
                if (nlCount > collapseMultipleBlankLinesTo) {
                    nlCount = collapseMultipleBlankLinesTo;    
                }
                String blankLines = getBlankLines(nlCount);
                String last = stack.peek();
                if (last != null) {
                    last = stack.pop();
                    last = blankLines + removeBlankLines(last, START);
                    stack.push(last);
                }
            }
        }
	}
	
	/**
 	 * Pops <code>howMany</code> items from the stack. Assumes these items are modifiers
 	 * for a constructor, method, field, or class. Formats the modifiers. Annotations
 	 * are on a separate line above the other modifiers, remaining modifiers are separated
 	 * by spaces.
 	 * @param howMany The number of modifiers to pop off of the stack.
 	 * @return The formatted modifiers.
 	 */
	private String formatModifiers(int howMany) {
	    if (howMany <= 0) {
	        return "";   
	    }
	    
	    // gather the modifiers into a list, then reverse the list so they are
	    // in the right order
	    List<String> modifiers = reverse(howMany);
	    if (sortModifiers) {
	        Collections.sort(modifiers, modifierComparator);    
	    }
        
        // modifiers shouldn't be on multiple lines, but comments preceding the
        // modifier may already be attached and can have multiple lines.
        // Handle the multiple lines here.
        StringBuilder sb = new StringBuilder();
        for (String modifier : modifiers) {
            modifier = modifier.trim();
            String[] lines = modifier.split("\n");
            if (lines.length == 1) {
                // something like "public "
                sb.append(modifier).append(' ');    
                if (modifier.startsWith("@")) {
                    sb.append('\n');   
                }
            }
            else {
                // multiple lines are likely comment lines
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    sb.append(line);
                    // annotations should be on a separate line also, but they
                    // should have already been handled above
                    if (i < lines.length - 1 || line.startsWith("@")) {
                        sb.append('\n');
                    }
                }
                sb.append(' ');
            }
        }
        
        // next indent each line of the modifier
        String indented = indent(sb.toString());
        sb = new StringBuilder(indented);
        if (sb.length() > 0) {
            sb.append(' ');    
        }
	    if (sb.length() == 0) {
	        indent(sb);   
	    }
	    return sb.toString();
	}
	
	/**
 	 * This is for sorting the modifiers.	
 	 */
	private static Comparator<String> modifierComparator = new Comparator<String>() {
	    // From Java 8 language specification: "If two or more (distinct) class modifiers 
	    // appear in a class declaration, then it is customary, though not required, 
	    // that they appear in the order consistent with that shown above in the production 
	    // for ClassModifier."
	    // This list contains all possible modifiers in the correct order. Not all
	    // modifiers are allowed for all constructs, but this method does not check
	    // for illegal modifiers. The parser should flag an error in those cases.
        private String modifiers = "public protected private abstract static final synchronized native strictfp transient volatile";
        
        @Override
	    public int compare(String a, String b) {
	        String a_ = a.trim();
	        String b_ = b.trim();
	        
	        // comments may be included in the choices
	        if (a_.startsWith("/") || a_.startsWith("*")) {
	            return -1;    
	        }
	        if (b_.startsWith("/") || b_.startsWith("*")) {
	            return 1;    
	        }
	        
	        // annotations may be included in the choices
	        if (a_.startsWith("@")) {
	            return -1;   
	        }
	        if (b_.startsWith("@")) {
	            return 1;   
	        }
	        
	        return modifiers.indexOf(a_) - modifiers.indexOf(b_);
	    }
	};
	
	private String sortAndGroupImports(List<String> importList) {
	    if (sortImports == false && groupImports == false) {
	        StringBuilder sb = new StringBuilder();
	        for (String imp : importList) {
	            sb.append(imp);    
	        }
	        return sb.toString();    
	    }
	    
	    // remove null items and blank strings
	    ListIterator<String> it = importList.listIterator();
	    while(it.hasNext()) {
	        String next = it.next();
	        if (next == null || next.trim().isEmpty()) {
	            it.remove();    
	        }
	        it.set(next.trim() + '\n');
	    }
	    
	    // sort imports
	    if (sortImports || groupImports) {
	        // grouping imports requires sorting them first
	        Collections.sort(importList, importComparator);            
	    }
	    
	    // remove duplicates but keep order
	    importList = new ArrayList<String>(new LinkedHashSet<String>(importList));
	    
	    // group imports
	    if (groupImports) {
	        String blankLines = getBlankLines(blankLinesBetweenImportGroups);
            List<String> groups = new ArrayList<String>();
            for (int i = 0; i < importList.size(); i++) {
                String imp = importList.get(i);
                if (i == 0) {
                    groups.add(imp);
                    continue;
                }
                String a = getImportName(importList.get(i - 1));
                if (a.indexOf('.') > -1) {
                    a = a.substring(0, a.indexOf('.'));    
                }
                String b = getImportName(importList.get(i));
                if (b.indexOf('.') > -1) {
                    b = b.substring(0, b.indexOf('.'));    
                }
                if (!a.equals(b)) {
                    groups.add(blankLines);
                }
                groups.add(imp);
            }
            importList.clear();
            importList.addAll(groups);
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    for (String imp : importList) {
	        sb.append(imp);    
	    }
	    return sb.toString();
	}
	
	/**
 	 * Get just the name of the import in the import statement, e.g. given "import java.util.*;"
 	 * this method would return "java.util.*;". Note that there may be comments included
 	 * with <code>s</code>.
 	 */
	private String getImportName(String s) {
	    String[] lines = s.split("\n");
	    // just check the last non-blank line
	    for (int i = lines.length - 1; i >= 0; i--) {
	        String line = lines[i].trim();
	        if (line.startsWith("import ")) {
	            return line.substring(line.lastIndexOf(' '));    
	        }
	    }
	    return "";
	}
	
	private Comparator<String> importComparator = new Comparator<String>() {
        @Override
	    public int compare(String a, String b) {
	        String a_ = getImportName(a);
	        String b_ = getImportName(b);
	        return a_.compareTo(b_);
	    }
	};
	
	/**
 	 * Removes all excess whitespace from each of the lines in <code>s</code>.	
 	 */
	private String removeExcessWhitespace(String s) {
	    String[] lines = s.split("\n");
	    StringBuilder sb = new StringBuilder();
	    for (String line : lines) {
	        line = trimEnd(line);
	        sb.append(line).append('\n');
	    }
	    while (endsWith(sb, "\n\n")) {
	        sb.deleteCharAt(sb.length() - 1);    
	    }
	    return sb.toString();
	}

	// puts a space before and after the given operator
	private String padOperator(String operator) {
	    operator = operator.trim();
	    if (padOperators) {
	        StringBuilder sb = new StringBuilder();
	        sb.append(' ').append(operator).append(' ');
	        operator = sb.toString();
	    }
	    return operator;
	}
	
	
//}}}    
    
/*
--------------------------------------------------------------------------------
Testing and debugging methods.
--------------------------------------------------------------------------------
*/
//{{{
    // this is useful for debugging
    private void printStack() {         // NOPMD
        printStack("");
    }
    
    // this is useful for debugging
    private void printStack(String name) {
        System.out.println("+++++ stack " + name + " +++++");
        for (String item : stack) {
            System.out.println("-------------------------------------");
            System.out.println(item);    
        }
        System.out.println("+++++ end stack " + name + " +++++");
    }
    
    // for testing, first arg is a java file to parse, optional second arg is
    // 'trace' to turn on parser tracing, e.g.
    // java -cp .:/home/danson/apps/antlr/antlr-4.4-complete.jar Java8BeautyListener /home/danson/tmp/test/LambdaScopeTest.java trace
    public static void main (String[] args) {
        if (args == null)
            return;
        try {
            // set up the parser
            long startTime = System.currentTimeMillis();
            java.io.FileReader input = new java.io.FileReader(args[0]);
            ANTLRInputStream antlrInput = new ANTLRInputStream( input );
            Java8Lexer lexer = new Java8Lexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            Java8Parser javaParser = new Java8Parser( tokens );
            
            // for debugging
            javaParser.setTrace(args.length > 1 && "trace".equals(args[1]));
            
            // parse and beautify the buffer contents
            Java8BeautyListener listener = new Java8BeautyListener(16 * 1024, tokens);
            listener.setUseSoftTabs(true);
            listener.setIndentWidth(4);
            ParseTreeWalker walker = new ParseTreeWalker();
            ParseTree tree = null;
            try {
                javaParser.getInterpreter().setPredictionMode(org.antlr.v4.runtime.atn.PredictionMode.SLL);
                javaParser.getInterpreter().tail_call_preserves_sll = false;
                javaParser.removeErrorListeners();
                javaParser.setErrorHandler(new BailErrorStrategy());
                tree = javaParser.compilationUnit();
            }
            catch(org.antlr.v4.runtime.misc.ParseCancellationException ex) {
                //ex.printStackTrace();
                System.out.println("+++++ first pass: " + (System.currentTimeMillis() - startTime) + " ms");
                System.out.println("+++++ Encountered possible syntax error, reparsing...");
                tokens.reset(); // rewind input stream
                javaParser.reset();
                javaParser.setTrace(args.length > 1 && "trace".equals(args[1]));
                // back to standard listeners/handlers
                javaParser.addErrorListener(ConsoleErrorListener.INSTANCE);
                javaParser.setErrorHandler(new DefaultErrorStrategy());
                // full now with full LL(*)
                javaParser.getInterpreter().setPredictionMode(org.antlr.v4.runtime.atn.PredictionMode.LL);
                javaParser.getInterpreter().tail_call_preserves_sll = false;
                javaParser.getInterpreter().enable_global_context_dfa = true;
                
                long parseTime = System.currentTimeMillis();
                tree = javaParser.compilationUnit();        
                System.out.println("+++++ second pass: " + (System.currentTimeMillis() - parseTime) + " ms");
            }
            walker.walk( listener, tree );

            System.out.println(listener.getText());
            
            long elapsed = System.currentTimeMillis() - startTime;
            System.out.println("+++++ elapsed time: " + elapsed + " ms");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//}}}    
    
/*
--------------------------------------------------------------------------------
Parser methods follow.
--------------------------------------------------------------------------------
*/
//{{{    
	@Override public void enterClassMemberDeclaration(@NotNull Java8Parser.ClassMemberDeclarationContext ctx) { 
	    // FieldDeclaration
	    // MethodDeclaration
	    // ClassDeclaration
	    // InterfaceDeclaration
	    // ;
	}
	@Override public void exitClassMemberDeclaration(@NotNull Java8Parser.ClassMemberDeclarationContext ctx) { 
	    if (";".equals(stack.peek())) {
	        String semi = stack.pop() + '\n';
	        stack.push(semi);
	    }
	}

	@Override public void enterStatementNoShortIf(@NotNull Java8Parser.StatementNoShortIfContext ctx) { 
	    // StatementWithoutTrailingSubstatement
	    // LabeledStatementNoShortIf
	    // IfThenElseStatementNoShortIf
	    // WhileStatementNoShortIf
	    // ForStatementNoShortIf
	}
	@Override public void exitStatementNoShortIf(@NotNull Java8Parser.StatementNoShortIfContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx) {
	    // annotationTypeElementModifier* unannType Identifier '(' ')' dims? defaultValue? ';'
	}
	@Override public void exitAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String defaultValue = ctx.defaultValue() == null ? "" : stack.pop();
	    String dims = ctx.dims() == null ? "" : stack.pop();
	    String rparen = stack.pop();
	    String lparen = stack.pop();
	    String identifier = stack.pop();
	    String type = stack.pop();
	    if (ctx.annotationTypeElementModifier() != null) {
	        sb.append(formatModifiers(ctx.annotationTypeElementModifier().size()));
	    }
	    sb.append(type).append(' ').append(identifier).append(lparen).append(rparen);
	    if (!dims.isEmpty()) {
	        sb.append(' ').append(dims);    
	    }
	    if (!defaultValue.isEmpty()) {
	        sb.append(' ').append(defaultValue);    
	    }
	    sb.append(semi);
	    stack.push(sb.toString());
	}

	@Override public void enterMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) {
	    // 	'.' typeArguments? Identifier '(' argumentList? ')'
	}
	@Override public void exitMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rparen = stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String identifier = stack.pop();
	    String typeArguments = ctx.typeArguments() == null ? "" : stack.pop();
	    String dot = stack.pop();
	    sb.append(dot);
	    if (!typeArguments.isEmpty()) {
	        sb.append(typeArguments).append(' ');
	    }
	    sb.append(identifier).append(padParen(lparen, argumentList)).append(argumentList).append(padParen(rparen, argumentList));
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) { 
	    // do nothing here
	}
	@Override public void exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) { 
	    // do nothing here
	}

	@Override public void enterAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx) { 
	    // '{' annotationTypeMemberDeclaration* '}'	
	}
	@Override public void exitAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rbrace = stack.pop().trim();
	    String body = "";
	    if (ctx.annotationTypeMemberDeclaration() != null && ctx.annotationTypeMemberDeclaration().size() > 0) {
	        body = reverse(ctx.annotationTypeMemberDeclaration().size(), "");
	        ++ tabCount;
	        body = indent(body);
	        body = trimEnd(body);
	        -- tabCount;
	    }
	    String lbrace = stack.pop();
	    sb.append(lbrace).append(body).append('\n').append(rbrace);
	    stack.push(sb.toString());
	}

	@Override public void enterRelationalOperator(@NotNull Java8Parser.RelationalOperatorContext ctx) { }
	
	/**
 	 * relationalOperator
 	 *     :   '<'
 	 *     |   '>'
 	 *     |   '<='
 	 *     |   '>='
 	 *     |   'instanceof'
 	 *     ;
 	 */
	@Override public void exitRelationalOperator(@NotNull Java8Parser.RelationalOperatorContext ctx) {
	    // now doing the padding in exitRelationalExpression
	    //stack.pop();
	    //stack.push(padOperator(ctx.getText()));
	}
	
	@Override public void enterConstantModifier(@NotNull Java8Parser.ConstantModifierContext ctx) {
	    // (one of) Annotation public 
	    // static final	
	}
	@Override public void exitConstantModifier(@NotNull Java8Parser.ConstantModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterLambdaBody(@NotNull Java8Parser.LambdaBodyContext ctx) { 
	    // Expression 
	    // Block
	}
	@Override public void exitLambdaBody(@NotNull Java8Parser.LambdaBodyContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterArgumentList(@NotNull Java8Parser.ArgumentListContext ctx) {
	    // expression (',' expression)*
	}
	@Override public void exitArgumentList(@NotNull Java8Parser.ArgumentListContext ctx) {
	    StringBuilder sb = new StringBuilder();
        sb.append(reverse(ctx.expression().size() * 2 - 1, " ", 2));
	    stack.push(sb.toString());
	}

	@Override public void enterShiftOperator(@NotNull Java8Parser.ShiftOperatorContext ctx) {
        // :   '<<'
        // |   '>>>'
        // |   '>>'  
 	}
	@Override public void exitShiftOperator(@NotNull Java8Parser.ShiftOperatorContext ctx) {
	    //stack.pop();
	    //stack.push(padOperator(ctx.getText()));
	}
	
	@Override public void enterClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx) {
	    // ClassMemberDeclaration
	    // InstanceInitializer
	    // StaticInitializer
	    // ConstructorDeclaration
	}
	@Override public void exitClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterClassInstanceCreationExpression_lfno_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) { 
	    // :	'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
	    // |	expressionName '.' 'new' typeArguments? annotationIdentifier          typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
	}
	@Override public void exitClassInstanceCreationExpression_lfno_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    // common ending
	    String classBody = ctx.classBody() == null ? "" : stack.pop().trim();
	    if (!classBody.isEmpty() && brokenBracket && classBody.startsWith("{")) {
	        classBody = new StringBuilder("\n").append(classBody).toString();
	    }
	    String rparen = stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String typeArgumentsOrDiamond = ctx.typeArgumentsOrDiamond() == null ? "" : stack.pop();
	    
	    // expression name choice
	    if (ctx.expressionName() != null) {
	        String annotationIdentifier = stack.pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop();
	        String new_ = stack.pop();    // 'new'
	        String dot = stack.pop();
	        String name = stack.pop();    // expression name
	        sb.append(name).append(dot).append(new_).append(typeArguments).append(annotationIdentifier);
	    }
	    else {
            // 'new' choice
            String annotationIdentifiers = "";
            if (ctx.annotationIdentifier() != null && ctx.annotationIdentifier().size() > 0) {
                annotationIdentifiers = reverse(ctx.annotationIdentifier().size() * 2 - 1, "");
            }
            String typeArguments = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
            String new_ = stack.pop();
            sb.append(new_).append(' ').append(typeArguments).append(annotationIdentifiers);
	    }
	    
	    // append common ending
	    sb.append(typeArgumentsOrDiamond).append(padParen(lparen, argumentList)).append(argumentList).append(padParen(rparen, argumentList)).append(classBody);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {
        // :	classInstanceCreationExpression_lf_primary
        // |	fieldAccess_lf_primary
        // |	methodInvocation_lf_primary
        // |	methodReference_lf_primary
	
	}
	@Override public void exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnaryExpression(@NotNull Java8Parser.UnaryExpressionContext ctx) { 
	    // PreIncrementExpression 
	    // PreDecrementExpression 
	    // + UnaryExpression -- changed to AdditiveOperator UnaryExpression 
	    // - UnaryExpression -- changed to AdditiveOperator UnaryExpression
	    // UnaryExpressionNotPlusMinus	
	}
	@Override public void exitUnaryExpression(@NotNull Java8Parser.UnaryExpressionContext ctx) {
	    
	    StringBuilder sb = new StringBuilder();
	    String expression = stack.pop();
	    if (ctx.additiveOperator() != null) {
	        String operator = stack.pop().trim();
	        sb.append(operator);
	    }
	    sb.append(expression);
	    stack.push(sb.toString());
	}

	@Override public void enterClassType_lfno_classOrInterfaceType(@NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
	    // annotationIdentifier typeArguments?
	}
	@Override public void exitClassType_lfno_classOrInterfaceType(@NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String typeArgs = ctx.typeArguments() == null ? "" : " " + stack.pop();
	    sb.append(stack.pop()).append(typeArgs);
	    stack.push(sb.toString());
	}

	@Override public void enterArrayType(@NotNull Java8Parser.ArrayTypeContext ctx) { 
	    // :	primitiveType dims
	    // |	classOrInterfaceType dims
	    // |	typeVariable dims        
	}
	@Override public void exitArrayType(@NotNull Java8Parser.ArrayTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String dims = stack.pop();
	    String type = stack.pop();
	    sb.append(type);
	    if (dims.startsWith("@")) {
	        sb.append(' ');
	    }
	    sb.append(dims);
	    stack.push(sb.toString());
	}

	@Override public void enterSimpleTypeName(@NotNull Java8Parser.SimpleTypeNameContext ctx) {
	    // Identifier
	}
	@Override public void exitSimpleTypeName(@NotNull Java8Parser.SimpleTypeNameContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) { 
	    // Identifier
	    // AmbiguousName . Identifier
	}
	@Override public void exitExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    if (ctx.ambiguousName() != null) {
	        String dot = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot);
	    }
	    sb.append(identifier);
	    stack.push(sb.toString());
	    
	}

	@Override public void enterStatementWithoutTrailingSubstatement(@NotNull Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
	    // Block
	    // EmptyStatement
	    // ExpressionStatement 
        // AssertStatement 
        // SwitchStatement 
        // DoStatement 
        // BreakStatement 
        // ContinueStatement 
        // ReturnStatement 
        // SynchronizedStatement 
        // ThrowStatement 
        // TryStatement

	}
	@Override public void exitStatementWithoutTrailingSubstatement(@NotNull Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterConstructorDeclarator(@NotNull Java8Parser.ConstructorDeclaratorContext ctx) {
	    // [TypeParameters] SimpleTypeName ( [FormalParameterList] )
	}
	@Override public void exitConstructorDeclarator(@NotNull Java8Parser.ConstructorDeclaratorContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rparen = stack.pop();
	    String formalParameters = "";
	    if (ctx.formalParameterList() != null) {
	        formalParameters = stack.pop();    
	    }
	    String lparen = stack.pop();
	    String typeName = stack.pop();
	    String typeParameters = "";
	    if (ctx.typeParameters() != null) {
	        typeParameters = stack.pop();    
	    }
	    if (!typeParameters.isEmpty()) {
	        sb.append(typeParameters).append(' ');    
	    }
	    sb.append(typeName).append(padParen(lparen, formalParameters));
        sb.append(formalParameters);
        sb.append(padParen(rparen, formalParameters));
        stack.push(sb.toString());
	}

	@Override public void enterAssertStatement(@NotNull Java8Parser.AssertStatementContext ctx) {
        // :	'assert' expression ';'
        // |	'assert' expression ':' expression ';'	
	}
	@Override public void exitAssertStatement(@NotNull Java8Parser.AssertStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    if (ctx.expression() != null) {
	        switch(ctx.expression().size()) {
                case 1:
                    String expression = stack.pop();
                    sb.append(expression).append(semi).append('\n');
                    break;
                case 2:
                    String expression2 = stack.pop();
                    String colon = stack.pop();
                    String expression1 = stack.pop();
                    sb.append(expression1).append(' ').append(colon).append(' ').append(expression2).append(semi).append('\n');
	        }
	    }
	    stack.push(sb.toString());
	}
	@Override public void enterArrayCreationExpression(@NotNull Java8Parser.ArrayCreationExpressionContext ctx) {
	    // :	'new' primitiveType dimExprs dims?
	    // |	'new' classOrInterfaceType dimExprs dims?
	    // |	'new' primitiveType dims arrayInitializer
	    // |	'new' classOrInterfaceType dims arrayInitializer	
	}
	@Override public void exitArrayCreationExpression(@NotNull Java8Parser.ArrayCreationExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.arrayInitializer() != null) {
	        // last two choices 
	        String ai = stack.pop();
	        String dims = stack.pop();
	        String type = stack.pop();
	        String new_ = stack.pop();
	        sb.append(new_).append(' ').append(type).append(dims).append(' ').append(ai);
	    }
	    else {
	        // first two choices    
	        String dims = "";
	        if (ctx.dims() != null) {
	            dims = stack.pop();    
	        }
	        String dimExprs = stack.pop();
	        String type = stack.pop();
	        String new_ = stack.pop();
	        sb.append(new_).append(' ').append(type).append(' ').append(dimExprs);
	        if (!dims.isEmpty()) {
	            sb.append(dims);    
	        }
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterUnannArrayType(@NotNull Java8Parser.UnannArrayTypeContext ctx) {
	    // UnannPrimitiveType Dims
	    // UnannClassOrInterfaceType Dims
	    // UnannTypeVariable Dims
	}
	@Override public void exitUnannArrayType(@NotNull Java8Parser.UnannArrayTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String dims = stack.pop();
	    String type = stack.pop();
	    sb.append(type);
	    if (dims.startsWith("@")) {
	        sb.append(' ');   
	    }
	    sb.append(dims);
	    stack.push(sb.toString());
	}

	@Override public void enterVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) { 
	    // Identifier [Dims]
	}
	@Override public void exitVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String dims = ctx.dims() == null ? "" : stack.pop();
	    String identifier = stack.pop();
	    sb.append(identifier).append(dims);
	    stack.push(sb.toString());
	}

	@Override public void enterTryWithResourcesStatement(@NotNull Java8Parser.TryWithResourcesStatementContext ctx) { 
	    // 'try' resourceSpecification block catches? finally_?	
	}
	@Override public void exitTryWithResourcesStatement(@NotNull Java8Parser.TryWithResourcesStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String finally_ = "";
	    if (ctx.finally_() != null) {
	        finally_ = stack.pop(); 
	    }
	    String catches = "";
	    if (ctx.catches() != null) {
	        catches = stack.pop().trim();   
	    }
	    String block = stack.pop();
	    String resources = stack.pop();
	    if (resources.indexOf('\n') > -1) {
	        resources = trimFront(resources);    
	    }
	    String try_ = stack.pop();
	    sb.append(try_).append(' ').append(resources).append(' ').append(block).append(breakElse ? '\n' : ' ').append(catches).append(breakElse ? '\n' : ' ').append(finally_);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx) {
	    // < TypeArgumentList >
	}
	@Override public void exitTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rangle = stack.pop();
	    String list = stack.pop();
	    String langle = stack.pop();
	    sb.append(langle).append(list).append(rangle);
	    stack.push(sb.toString());
	    
	}
	@Override public void enterExceptionType(@NotNull Java8Parser.ExceptionTypeContext ctx) { 
	    // ClassType
	    // TypeVariable
	}
	@Override public void exitExceptionType(@NotNull Java8Parser.ExceptionTypeContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterExceptionTypeList(@NotNull Java8Parser.ExceptionTypeListContext ctx) { 
	    // ExceptionType {, ExceptionType}
	}
	@Override public void exitExceptionTypeList(@NotNull Java8Parser.ExceptionTypeListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.exceptionType() != null) {
	        sb.append(reverse(ctx.exceptionType().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterAdditiveExpression(@NotNull Java8Parser.AdditiveExpressionContext ctx) { 
	    // :	multiplicativeExpression
	    // |	additiveExpression additiveOperator multiplicativeExpression 
	    // |	additiveExpression additiveOperator multiplicativeExpression	
	}
	@Override public void exitAdditiveExpression(@NotNull Java8Parser.AdditiveExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
        String multiplicativeExpression = stack.pop();
	    if (ctx.additiveExpression() != null) {
	        // one of the last two choices
	        String additiveOperator = stack.pop();
	        String additiveExpression = stack.pop().trim();
	        sb.append(additiveExpression).append(padOperator(additiveOperator));
	        multiplicativeExpression = multiplicativeExpression.trim();
	    }
	    sb.append(multiplicativeExpression);
	    stack.push(sb.toString());
	}

	@Override public void enterRelationalExpression(@NotNull Java8Parser.RelationalExpressionContext ctx) {
	    // ShiftExpression 
	    // RelationalExpression RelationalOperator ShiftExpression 
	    // RelationalExpression RelationalOperator ShiftExpression 
	    // RelationalExpression RelationalOperator ShiftExpression 
	    // RelationalExpression RelationalOperator ShiftExpression 
	    // RelationalExpression RelationalOperator ReferenceType	
	}
	@Override public void exitRelationalExpression(@NotNull Java8Parser.RelationalExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String shiftExpression = stack.pop();
	    String operator = "";
	    String relationalExpression = "";
	    if (ctx.relationalOperator() != null) {
	        operator = stack.pop();
	        relationalExpression = stack.pop();
	        sb.append(relationalExpression).append(padOperator(operator));
	    }
	    sb.append(shiftExpression);
	    stack.push(sb.toString());
	}

	@Override public void enterReferenceType(@NotNull Java8Parser.ReferenceTypeContext ctx) { 
	    // ClassOrInterfaceType
	    // TypeVariable
	    // ArrayType
	}
	@Override public void exitReferenceType(@NotNull Java8Parser.ReferenceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterArrayAccess_lf_primary(@NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx) { 
        // :   (	primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary '[' expression ']'
        //     )
        //     (	primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary '[' expression ']'
        //     )*
	}
	@Override public void exitArrayAccess_lf_primary(@NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    StringBuilder sh = new StringBuilder();
	    if (ctx.primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary() != null) {
	        for (int i = 0; i < ctx.primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary().size(); i++) {
	            String rsquare = stack.pop();
                String expr = stack.pop();
                String lsquare = stack.pop();
                // primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary doesn't put anything on the stack
                sb.append(lsquare).append(expr).append(rsquare).append(' ');
	        }
	    }
	    String rsquare = stack.pop();
	    String expr = stack.pop();
	    String lsquare = stack.pop();
	    String pn = stack.pop();
	    sb.append(pn).append(lsquare).append(expr).append(rsquare).append(' ').append(sh);
	    stack.push(sb.toString());
	}

	@Override public void enterInferredFormalParameterList(@NotNull Java8Parser.InferredFormalParameterListContext ctx) { 
	    // Identifier {, Identifier}	
	}
	@Override public void exitInferredFormalParameterList(@NotNull Java8Parser.InferredFormalParameterListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(reverse(ctx.Identifier().size() * 2 - 1, " ", 2));
	    stack.push(sb.toString());
	}

	@Override public void enterReturnStatement(@NotNull Java8Parser.ReturnStatementContext ctx) {
	    // return [Expression] ;
	}
	@Override public void exitReturnStatement(@NotNull Java8Parser.ReturnStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String expression = ctx.expression() == null ? "" : stack.pop();
	    String return_ = stack.pop();
	    sb.append(return_);
	    if (!expression.isEmpty()) {
	        sb.append(' ').append(expression);
	    }
	    sb.append(semi).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterTypeImportOnDemandDeclaration(@NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx) { 
	    // import PackageOrTypeName . * ;
	}
	@Override public void exitTypeImportOnDemandDeclaration(@NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String star = stack.pop();
	    String dot = stack.pop();
	    String name = stack.pop();
	    String import_ = stack.pop();
	    sb.append(import_).append(' ').append(name).append(dot).append(star).append(semi).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx) { 
	    // < TypeParameterList >
	}
	@Override public void exitTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rangle = stack.pop();
	    String list = stack.pop();
	    String langle = stack.pop();
	    sb.append(langle).append(list).append(rangle);
	    stack.push(sb.toString());
	}
	@Override public void enterLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx) { 
	    // {VariableModifier} UnannType {Annotation} ... VariableDeclaratorId
	    // FormalParameter
	}
	@Override public void exitLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    // only need to handle the first choice
	    if (ctx.unannType() != null) {
            String vdi = stack.pop();
            String ellipsis = stack.pop();
            String annotations = "";
            if (ctx.annotation() != null && !ctx.annotation().isEmpty()) {
                annotations = reverse(ctx.annotation().size(), " ") + ' ';
            }
            String type = stack.pop();
            if (ctx.variableModifier() != null) {
                for (int i = 0; i < ctx.variableModifier().size(); i++) {
                    sb.append(stack.pop()).append(' ');    
                }
            }
            sb.append(type);
            if (!annotations.isEmpty()) {
                sb.append(' ').append(annotations);
            } else {
                trimEnd(sb);   // remove space after type
            }
            sb.append(ellipsis).append(' ').append(vdi);
            stack.push(sb.toString());
	    }
	}

	@Override public void enterLiteral(@NotNull Java8Parser.LiteralContext ctx) {
	    // IntegerLiteral
	    // FloatingPointLiteral
	    // BooleanLiteral
	    // CharacterLiteral
	    // StringLiteral
	    // NullLiteral
	}
	@Override public void exitLiteral(@NotNull Java8Parser.LiteralContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterResult(@NotNull Java8Parser.ResultContext ctx) {
	    // UnannType
	    // void
	}
	@Override public void exitResult(@NotNull Java8Parser.ResultContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterFieldAccess_lfno_primary(@NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx) {
	    // :	'super' '.' Identifier
	    // |	typeName '.' 'super' '.' Identifier
	
	}
	@Override public void exitFieldAccess_lfno_primary(@NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    String dot = stack.pop();
	    String super_ = stack.pop();
	    if (ctx.typeName() != null) {
	        String dot2 = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot2);    
	    }
	    sb.append(super_).append(dot).append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) {
	    // VariableDeclaratorId [= VariableInitializer]
	}
	@Override public void exitVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String vi = ctx.variableInitializer() == null ? "" : stack.pop();
	    String equal = ctx.variableInitializer() == null ? "" : stack.pop();
	    equal = padOperator(equal);
	    String vd = stack.pop();
	    sb.append(vd);
	    if (!vi.isEmpty()) {
	        sb.append(equal).append(vi);    
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterFinally_(@NotNull Java8Parser.Finally_Context ctx) {
	    // finally Block
	}
	@Override public void exitFinally_(@NotNull Java8Parser.Finally_Context ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String block = stack.pop();
	    block = trimFront(block);
        if (bracketStyle == BROKEN) {
            block = new StringBuilder("\n").append(block).toString();
        }
	    String finally_ = stack.pop();
	    sb.append(finally_).append(' ').append(block);
	    stack.push(sb.toString());
	}

	@Override public void enterClassBody(@NotNull Java8Parser.ClassBodyContext ctx) {
	    // '{' classBodyDeclaration* '}'
	    ++tabCount;
	}
	@Override public void exitClassBody(@NotNull Java8Parser.ClassBodyContext ctx) {
	    String rbrace = stack.pop();    // }
	    StringBuilder sb = new StringBuilder();
	    String body = "";
	    if (ctx.classBodyDeclaration() != null) {
	        body = reverse(ctx.classBodyDeclaration().size(), "\n");
	        body = indent(body);
	        body = removeBlankLines(body, BOTH);
	    }
	    addBlankLines(blankLinesAfterClassDeclaration);    // added to lbrace
	    String lbrace = stack.pop();    // {
	    sb.append(brokenBracket ? "\n" : "").append(lbrace);
	    sb.append(body);
	    --tabCount;
	    rbrace = indentRBrace(rbrace);    // indent adds a \n
	    if (!body.isEmpty()) {
	        sb.append('\n');   
	    }
	    sb.append(rbrace);
	    stack.push(sb.toString());
	}
	
	@Override public void enterUnannInterfaceType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {
	    // unannClassType_lfno_unannClassOrInterfaceType
	}
	@Override public void exitUnannInterfaceType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx) {
	    // classModifiers 'enum' Identifier superinterfaces? enumBody
	}
	@Override public void exitEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String enumBody = stack.pop();
	    String superInterfaces = "";
	    if (ctx.superinterfaces() != null) {
	        superInterfaces = stack.pop();    
	    }
	    String identifier = stack.pop();
	    String enum_ = stack.pop();
	    String modifiers = stack.pop();
	    sb.append(modifiers);
	    sb.append(enum_).append(' ').append(identifier);
	    if (!superInterfaces.isEmpty()) {
	        sb.append(' ').append(superInterfaces).append(' ');    
	    }
	    sb.append(enumBody);
	    stack.push(sb.toString());
	}

	@Override public void enterConstantDeclaration(@NotNull Java8Parser.ConstantDeclarationContext ctx) {
	    // {ConstantModifier} UnannType VariableDeclaratorList ;	
	}
	@Override public void exitConstantDeclaration(@NotNull Java8Parser.ConstantDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String vdl = stack.pop();
	    String type = stack.pop();
	    if (ctx.constantModifier() != null) {
	        sb.append(reverse(ctx.constantModifier().size(), " ")).append(' ');
	    }
	    sb.append(type).append(' ').append(vdl).append(semi).append('\n');
	    addBlankLines(1);
	    stack.push(sb.toString());
	}

	@Override public void enterPostfixExpression(@NotNull Java8Parser.PostfixExpressionContext ctx) { 
	    //    :	(	primary
	    //    	|	expressionName
	    //    	)
	    //    	(	postIncrementExpression_lf_postfixExpression
	    //    	|	postDecrementExpression_lf_postfixExpression
	    //    	)*
	    //    ;
	}
	@Override public void exitPostfixExpression(@NotNull Java8Parser.PostfixExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    // second half
	    String postfix = "";
	    if (ctx.postIncrementExpression_lf_postfixExpression() != null || ctx.postDecrementExpression_lf_postfixExpression() != null) {
	        int howMany = ctx.postIncrementExpression_lf_postfixExpression() == null ? 0 : ctx.postIncrementExpression_lf_postfixExpression().size();
	        howMany += ctx.postDecrementExpression_lf_postfixExpression() == null ? 0 : ctx.postDecrementExpression_lf_postfixExpression().size();
	        postfix = howMany > 0 ? reverse(howMany, "") : "";
	    }
	    sb.append(stack.pop());        // first half
	    if (!postfix.isEmpty()) {
	        sb.append(postfix);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterAnnotation(@NotNull Java8Parser.AnnotationContext ctx) {
	    // :	normalAnnotation
	    // |	markerAnnotation
	    // |	singleElementAnnotation
	}
	@Override public void exitAnnotation(@NotNull Java8Parser.AnnotationContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx) {
	    // Expression
	    // ArrayInitializer
	}
	@Override public void exitVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterStaticImportOnDemandDeclaration(@NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx) { 
	    // import static TypeName . * ;
	}
	@Override public void exitStaticImportOnDemandDeclaration(@NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String star = stack.pop();
	    String dot = stack.pop();
	    String name = stack.pop();
	    String static_ = stack.pop();
	    String import_ = stack.pop();
	    sb.append(import_).append(' ').append(static_).append(' ').append(name).append(dot).append(star).append(semi).append('\n');
	    stack.push(sb.toString());
	}
	
	@Override public void enterExpression(@NotNull Java8Parser.ExpressionContext ctx) { 
	    // LambdaExpression 
	    // AssignmentExpression	
	}
	@Override public void exitExpression(@NotNull Java8Parser.ExpressionContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	    
	}

	@Override public void enterThrowStatement(@NotNull Java8Parser.ThrowStatementContext ctx) {
	    // throw Expression ;
	}
	@Override public void exitThrowStatement(@NotNull Java8Parser.ThrowStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String expression = stack.pop();
	    String throw_ = stack.pop();
	    sb.append(throw_).append(' ').append(expression).append(semi).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) { 
        //                                         methodName '(' argumentList? ')'
        //             typeName '.' typeArguments? Identifier '(' argumentList? ')'
        //       expressionName '.' typeArguments? Identifier '(' argumentList? ')'
        //              primary '.' typeArguments? Identifier '(' argumentList? ')'
        //              'super' '.' typeArguments? Identifier '(' argumentList? ')'
        // typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'  	    
	}
	@Override public void exitMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    // common ending
	    String rparen = stack.pop();
        String argList = ctx.argumentList() == null ? "" : stack.pop();
        if (argList.trim().startsWith("new") && argList.split("\n").length > 1) {
            // anonymous inner classes need indented one more time
            argList = indentAgain(argList);        
        }
        argList = argList.trim();
        String lparen = stack.pop();
        StringBuilder ending = new StringBuilder();
        ending.append(padParen(lparen, argList)).append(argList).append(padParen(rparen, argList)); 
        
	    if (ctx.methodName() != null) {
	        // 1st choice
	        String name = stack.pop();
	        sb.append(name).append(ending);   
	    }
	    else if (ctx.expressionName() != null || ctx.primary() != null) {
	        // 3rd and 4th choices
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String dot = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot).append(typeArgs).append(identifier).append(ending);
	    }
	    else if (ctx.typeName() != null) {
	        // 2nd and 6th choices
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
            boolean hasSuper = ctx.SUPER() != null;
            String dot = stack.pop();
            String super_ = "";
            String dot2 = "";
            if (hasSuper) {
                super_ = stack.pop();
                dot2 = stack.pop();
            }
            String typename = stack.pop();
            sb.append(typename).append(dot2).append(super_).append(dot).append(typeArgs).append(identifier).append(ending);
	    }
	    else {
	        // 5th choice
	        // 'super' '.' typeArguments? Identifier '(' argumentList? ')'
	        String identifier = ctx.Identifier() == null ? "" : stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String dot = stack.pop();
	        String super_ = stack.pop();
	        sb.append(super_).append(dot).append(typeArgs).append(identifier).append(ending);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterSingleStaticImportDeclaration(@NotNull Java8Parser.SingleStaticImportDeclarationContext ctx) {
	    // import static TypeName . Identifier ; 
	}
	@Override public void exitSingleStaticImportDeclaration(@NotNull Java8Parser.SingleStaticImportDeclarationContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String identifier = stack.pop();
	    String dot = stack.pop();
	    String name = stack.pop();
	    String static_ = stack.pop();
	    String import_ = stack.pop();
	    sb.append(import_).append(' ').append(static_).append(' ').append(name).append(dot).append(identifier).append(semi).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterLambdaParameters(@NotNull Java8Parser.LambdaParametersContext ctx) { 
	    // Identifier
	    // ( [FormalParameterList] ) 
	    // ( InferredFormalParameterList )	
	}
	@Override public void exitLambdaParameters(@NotNull Java8Parser.LambdaParametersContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.Identifier() != null) {
	        sb.append(stack.pop());
	    }
	    else {
	        String rparen = stack.pop();
	        String list = "";
	        if (ctx.formalParameterList() != null || ctx.inferredFormalParameterList() != null) {
	            list = stack.pop();
	        }
	        String lparen = stack.pop();
	        sb.append(padParen(lparen, list)).append(list).append(padParen(rparen, list));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterConditionalAndExpression(@NotNull Java8Parser.ConditionalAndExpressionContext ctx) {
	    // InclusiveOrExpression 
	    // ConditionalAndExpression && InclusiveOrExpression	
	}
	@Override public void exitConditionalAndExpression(@NotNull Java8Parser.ConditionalAndExpressionContext ctx) {
	    
	    StringBuilder sb = new StringBuilder();
	    String ioe = stack.pop();
	    if (ctx.conditionalAndExpression() != null) {
	        String and = stack.pop();
	        String cae = stack.pop();
	        sb.append(cae).append(padOperator(and));
	    }
	    sb.append(ioe);
	    stack.push(sb.toString());
	}

	@Override public void enterMultiplicativeExpression(@NotNull Java8Parser.MultiplicativeExpressionContext ctx) {
	    // UnaryExpression 
	    // MultiplicativeExpression MultiplicativeOperator UnaryExpression 
	}
	@Override public void exitMultiplicativeExpression(@NotNull Java8Parser.MultiplicativeExpressionContext ctx) { 
	    
	    StringBuilder sb = new StringBuilder();
	    String ue = stack.pop();
	    if (ctx.multiplicativeExpression() != null) {
	        String operator = stack.pop();
	        String me = stack.pop();
	        sb.append(me).append(padOperator(operator));
	    }
	    sb.append(ue);
	    stack.push(sb.toString());
	}

	@Override public void enterPackageModifier(@NotNull Java8Parser.PackageModifierContext ctx) {
	    // Annotation
	}
	@Override public void exitPackageModifier(@NotNull Java8Parser.PackageModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx) {
	    // constructorModifiers constructorDeclarator throws_? constructorBody
	}
	@Override public void exitConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String body = stack.pop();
	    String throws_ = ctx.throws_() == null ? "" : stack.pop() + ' ';
	    String cd = stack.pop();
	    if (ctx.constructorModifiers().constructorModifier().size() > 0) {
            String modifiers = stack.pop();
            modifiers = removeBlankLines(modifiers, START);  
            sb.append(modifiers).append(' ');
	    }
	    else {
	        indent(sb);    
	    }
	    sb.append(cd).append(' ').append(throws_).append(body);
        addBlankLines(blankLinesBeforeMethods);
        stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lfno_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) { 
        // :	literal
        // |	typeName (squareBrackets)* '.' 'class'
        // |	'void' '.' 'class'
        // |	'this'
        // |	typeName '.' 'this'
        // |	'(' expression ')'
        // |	classInstanceCreationExpression
        // |	fieldAccess
        // |	methodInvocation
        // |	methodReference
	}
	@Override public void exitPrimaryNoNewArray_lfno_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) { 
	    if (ctx.literal() != null || 
	        ctx.classInstanceCreationExpression() != null ||
	        ctx.fieldAccess() != null ||
	        ctx.methodInvocation() != null || 
	        ctx.methodReference() != null) {
	        // one of these choices is already on the stack
	        return;
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    
	    // expression
	    if (ctx.expression() != null) {
	        String rparen = stack.pop();
	        String expression = stack.pop();
	        String lparen = stack.pop();
	        sb.append(padParen(lparen)).append(expression).append(padParen(rparen));
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 2 typeName choices
	    String raw = ctx.getText();
	    if (ctx.typeName() != null) {
	        if (raw.indexOf("class") > -1) {
	            // first typeName choice
	            String class_ = stack.pop();
	            String dot = stack.pop();
	            StringBuilder brackets = new StringBuilder();
	            if (ctx.squareBrackets() != null && ctx.squareBrackets().size() > 0) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append(stack.pop());
	                }
	            }
	            String name = stack.pop();
	            sb.append(name).append(brackets).append(dot).append(class_);
	        }
	        else {
	            // second typeName choice
	            String this_ = stack.pop();
	            String dot = stack.pop();
	            String name = stack.pop();
	            sb.append(name).append(dot).append(this_);
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 'void' and 'this' choice
	    if (raw.indexOf("void") > -1) {
	        String class_ = stack.pop();
	        String dot = stack.pop();
	        String void_ = stack.pop();
	        sb.append(void_).append(dot).append(class_);
	    }
	    else {
	        sb.append(stack.pop());    // 'this' choice   
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterUnannTypeVariable(@NotNull Java8Parser.UnannTypeVariableContext ctx) { 
	    // Identifier
	}
	@Override public void exitUnannTypeVariable(@NotNull Java8Parser.UnannTypeVariableContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterNormalInterfaceDeclaration(@NotNull Java8Parser.NormalInterfaceDeclarationContext ctx) {
	    // interfaceModifiers 'interface' Identifier typeParameters? extendsInterfaces? interfaceBody	
	}
	@Override public void exitNormalInterfaceDeclaration(@NotNull Java8Parser.NormalInterfaceDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String body = stack.pop();
	    String ifs = ctx.extendsInterfaces() == null ? "" : stack.pop() + ' ';
	    String params = ctx.typeParameters() == null ? "" : stack.pop() + ' ';
	    String identifier = stack.pop();
	    String interface_ = stack.pop().trim();
	    String modifiers = "";
	    if (ctx.interfaceModifiers().interfaceModifier().size() > 0) {
	        modifiers = stack.pop();
	    }
	    sb.append(modifiers).append(modifiers.isEmpty() ? "" : " ").append(interface_).append(' ').append(identifier).append(params).append(ifs).append(body);
	    sb.append(getBlankLines(blankLinesAfterClassBody));
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceType_lfno_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
	    // classType_lfno_classOrInterfaceType
	}
	@Override public void exitInterfaceType_lfno_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterConstructorModifiers(@NotNull Java8Parser.ConstructorModifiersContext ctx) { }
	@Override public void exitConstructorModifiers(@NotNull Java8Parser.ConstructorModifiersContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.constructorModifier() != null && ctx.constructorModifier().size() > 0) {
	        sb.append(formatModifiers(ctx.constructorModifier().size()));
	    }
	    if (sb.length() > 0) {
            trimEnd(sb);
            stack.push(sb.toString());
        }
	}
	@Override public void enterConstructorModifier(@NotNull Java8Parser.ConstructorModifierContext ctx) {
	    // (one of) Annotation public protected private
	}
	@Override public void exitConstructorModifier(@NotNull Java8Parser.ConstructorModifierContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx) {
	    // Identifier
	}
	@Override public void exitEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterClassInstanceCreationExpression(@NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        // :    'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // |	expressionName '.' 'new' typeArguments? annotationIdentifier          typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // |	primary        '.' 'new' typeArguments? annotationIdentifier          typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
	}
	@Override public void exitClassInstanceCreationExpression(@NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    String classBody = ctx.classBody() == null ? "" : stack.pop();
	    if (!classBody.isEmpty() && brokenBracket && classBody.startsWith("{")) {
	        classBody = new StringBuilder("\n").append(classBody).toString();
	    }
	    String rparen = stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String typeArgumentsOrDiamond = ctx.typeArgumentsOrDiamond() == null ? "" : stack.pop();
	    
	    // expression name and primary
	    if (ctx.expressionName() != null || ctx.primary() != null) {
	        String annotationIdentifier = stack.pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop();
	        String new_ = stack.pop();
	        String dot = stack.pop();
	        String name = stack.pop();    // expression name or primary
	        sb.append(name).append(dot).append(new_).append(typeArguments).append(annotationIdentifier);
	    }
	    else {
            // 'new' choice
            StringBuilder annotationIdentifiers = new StringBuilder();
            if (ctx.annotationIdentifier() != null && ctx.annotationIdentifier().size() > 0) {
                annotationIdentifiers.append(reverse(ctx.annotationIdentifier().size(), ""));
            }
            String typeArguments = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
            String new_ = stack.pop();
            sb.append(trimEnd(new_)).append(' ').append(typeArguments).append(annotationIdentifiers.toString());
	    }
	    
	    // common ending
	    sb.append(typeArgumentsOrDiamond).append(padParen(lparen, argumentList)).append(argumentList).append(padParen(rparen, argumentList)).append(classBody);
	    stack.push(sb.toString());
	}

	@Override public void enterMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) {
	    // Identifier ( [FormalParameterList] ) [Dims]
	}
	@Override public void exitMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String dims = ctx.dims() == null ? "" : stack.pop();
	    String rparen = stack.pop();
	    String params = ctx.formalParameterList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String identifier = stack.pop();
	    sb.append(identifier).append(padParen(lparen, params)).append(params).append(padParen(rparen, params)).append(dims);
	    stack.push(sb.toString());
	}

	@Override public void enterAnnotationTypeMemberDeclaration(@NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx) {
	    // :	annotationTypeElementDeclaration
	    // |	constantDeclaration
	    // |	classDeclaration
	    // |	interfaceDeclaration
	    // |	';'                             
	}
	@Override public void exitAnnotationTypeMemberDeclaration(@NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx) { 
	    if (";".equals(stack.peek())) {
	        String semi = stack.pop() + '\n';
	        stack.push(semi);
	    }
	}

	@Override public void enterPreDecrementExpression(@NotNull Java8Parser.PreDecrementExpressionContext ctx) {
	    // -- UnaryExpression	
	}
	@Override public void exitPreDecrementExpression(@NotNull Java8Parser.PreDecrementExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expression = stack.pop();
	    String dec = stack.pop();
	    sb.append(dec).append(expression);
	    stack.push(sb.toString());
	}

	@Override public void enterMultiplicativeOperator(@NotNull Java8Parser.MultiplicativeOperatorContext ctx) { 
	    // one of: * / %
	}
	@Override public void exitMultiplicativeOperator(@NotNull Java8Parser.MultiplicativeOperatorContext ctx) {
	    stack.pop();
	    stack.push(padOperator(ctx.getText()));
	}
	
	@Override public void enterVariableInitializerList(@NotNull Java8Parser.VariableInitializerListContext ctx) {
	    // variableInitializer (',' variableInitializer)*
	}
	@Override public void exitVariableInitializerList(@NotNull Java8Parser.VariableInitializerListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.variableInitializer() != null) {
	        String vi = reverse(ctx.variableInitializer().size() * 2 - 1, " ", 2);
	        sb.append(vi);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterExtendsInterfaces(@NotNull Java8Parser.ExtendsInterfacesContext ctx) {
	    // extends InterfaceTypeList
	}
	@Override public void exitExtendsInterfaces(@NotNull Java8Parser.ExtendsInterfacesContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String list = stack.pop();
	    String extends_ = stack.pop();
	    sb.append(extends_).append(' ').append(list);
	    stack.push(sb.toString());
	}

	@Override public void enterElementValue(@NotNull Java8Parser.ElementValueContext ctx) {
	    // ConditionalExpression 
	    // ElementValueArrayInitializer 
	    // Annotation	
	}
	@Override public void exitElementValue(@NotNull Java8Parser.ElementValueContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx) { 
	    // '{' variableInitializerList? COMMA? '}'
	}
	@Override public void exitArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rbracket = stack.pop().trim();
	    String vil = ctx.variableInitializerList() == null ? "" : stack.pop().trim();
	    if (ctx.COMMA() != null) {
	         stack.pop();    // TODO: keep this? there is no need for it.    
	    }
	    String lbracket = stack.pop();
	    String[] lines = vil.split("\n");
	    if (lines.length > 1) {
	        StringBuilder vilIndented = new StringBuilder();
	        ++tabCount;
	        for (String line : lines) {
	            vilIndented.append(indent(line));    
	        }
	        --tabCount;
	        vil = '\n' + vilIndented.toString();
	    }
	    sb.append(lbracket);
	    sb.append(vil);
	    sb.append(rbracket);
	    stack.push(sb.toString());
	}
	
	@Override public void enterArrayAccess(@NotNull Java8Parser.ArrayAccessContext ctx) {
        // (	expressionName '[' expression ']'
        // |	primaryNoNewArray_lfno_arrayAccess '[' expression ']'
        // )
        // (	primaryNoNewArray_lf_arrayAccess '[' expression ']'
        // )* 
    }
    
	@Override public void exitArrayAccess(@NotNull Java8Parser.ArrayAccessContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    // second half
	    StringBuilder sh = new StringBuilder();
	    if (ctx.primaryNoNewArray_lf_arrayAccess() != null) {
	        for (int i = 0; i < ctx.primaryNoNewArray_lf_arrayAccess().size(); i++) {
	            String rsquare = stack.pop();
	            String expr = stack.pop();
	            String lsquare = stack.pop();
	            // primaryNoNewArray_lf_arrayAccess doesn't put anything on the stack
	            sh.append(lsquare).append(expr).append(rsquare);
	        }
	    }
	    String rsquare = stack.pop();
	    String expr = stack.pop();
	    String lsquare = stack.pop();
	    String name = stack.pop();
	    sb.append(name).append(lsquare).append(expr).append(rsquare);
	    if (sh.length() > 0) {
	        sb.append(sh);    
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterMethodModifiers(@NotNull Java8Parser.MethodModifiersContext ctx) { }
	@Override public void exitMethodModifiers(@NotNull Java8Parser.MethodModifiersContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.methodModifier() != null && ctx.methodModifier().size() > 0) {
	        String modifiers = formatModifiers(ctx.methodModifier().size());
	        sb.append(modifiers);
	    }
	    if (sb.length() > 0) {
            trimEnd(sb);
            stack.push(sb.toString());
        }
	}
	@Override public void enterMethodModifier(@NotNull Java8Parser.MethodModifierContext ctx) {
	    // (one of) Annotation public protected private
	    // abstract static final synchronized native strictfp
	}
	@Override public void exitMethodModifier(@NotNull Java8Parser.MethodModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	    if (ctx.annotation() != null) {
	        String ann = indent(stack.pop());
	        stack.push(ann + '\n');
	    }
	}

	@Override public void enterUnannClassType(@NotNull Java8Parser.UnannClassTypeContext ctx) {
	    // Identifier typeArguments?
	    // unannClassOrInterfaceType '.' annotationIdentifier typeArguments?
	}
	@Override public void exitUnannClassType(@NotNull Java8Parser.UnannClassTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.unannClassOrInterfaceType() == null) {
	        // have first option
	        String typeargs = "";
	        if (ctx.typeArguments() != null) {
	            typeargs = stack.pop();
	        }
	        sb.append(stack.pop());    // identifier
	        if (!typeargs.isEmpty()) {
	            sb.append(typeargs);   
	        }
	    }
	    else {
	        String typeargs = "";
	        if (ctx.typeArguments() != null) {
	            typeargs = stack.pop();
	        }
	        String identifier = stack.pop();
	        String dot = stack.pop();
	        String type = stack.pop();
	        sb.append(type).append(dot);
	        sb.append(identifier);
	        sb.append(typeargs);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterLambdaExpression(@NotNull Java8Parser.LambdaExpressionContext ctx) { 
	    // LambdaParameters -> LambdaBody	
	}
	@Override public void exitLambdaExpression(@NotNull Java8Parser.LambdaExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String lb = stack.pop();
	    String pointer = stack.pop();
	    String lp = stack.pop();
	    sb.append(lp).append(padOperator(pointer)).append(lb);
	    stack.push(sb.toString());
	}



	@Override public void enterAssignmentExpression(@NotNull Java8Parser.AssignmentExpressionContext ctx) {
	    // ConditionalExpression 
	    // Assignment	
	}
	@Override public void exitAssignmentExpression(@NotNull Java8Parser.AssignmentExpressionContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}


	@Override public void enterTypeParameterList(@NotNull Java8Parser.TypeParameterListContext ctx) {
	    // TypeParameter {, TypeParameter}
	}
	@Override public void exitTypeParameterList(@NotNull Java8Parser.TypeParameterListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.typeParameter() != null) {
	        sb.append(reverse(ctx.typeParameter().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterNormalClassDeclaration(@NotNull Java8Parser.NormalClassDeclarationContext ctx) {
	    // classModifiers 'class' Identifier typeParameters? superclass? superinterfaces? classBody
	}
	@Override public void exitNormalClassDeclaration(@NotNull Java8Parser.NormalClassDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
        String body = stack.pop();
        body = trimFront(body);
        if (bracketStyle == BROKEN) {
            body = new StringBuilder("\n").append(body).toString();
        }
        String superInterfaces = ctx.superinterfaces() == null ? "" : stack.pop() + ' ';
        String superClass = ctx.superclass() == null ? "" : stack.pop() + ' ';
        String params = ctx.typeParameters() == null ? "" : stack.pop() + ' ';
        String identifier = stack.pop();
        String classNode = stack.pop();
        String modifiers = "";
        if ( ctx.classModifiers() != null && ctx.classModifiers().classModifier() != null && ctx.classModifiers().classModifier().size() > 0) {
            modifiers = stack.pop();    
        }
        String previous = stack.peek();
        String blankLinesBefore = getBlankLines(blankLinesBeforeMethods);
        if (previous != null && !previous.endsWith(blankLinesBefore) && !previous.startsWith("import")) {
            previous = stack.pop();
            previous = removeBlankLines(previous, END) + blankLinesBefore;
            stack.push(previous);
        } 
        else {
            sb.append(blankLinesBefore);    
        }
        addBlankLines(blankLinesAfterClassDeclaration);
        sb.append(modifiers).append(modifiers.isEmpty() ? "" : " ").append(classNode).append(' ').append(identifier).append(' ').append(params).append(superClass).append(superInterfaces).append(body);
        sb.append(getBlankLines(blankLinesAfterClassBody));
        stack.push(sb.toString());
	}

	@Override public void enterFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx) { 
	    // ReceiverParameter
	    // FormalParameters , LastFormalParameter
	    // LastFormalParameter
	}
	@Override public void exitFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx) { 
	    // only need to handle 2nd choice, others should already be on the stack
	    if (ctx.formalParameters() != null) {
	        String lfp = stack.pop();
	        String comma = stack.pop();
	        String fp = stack.pop();
	        StringBuilder sb = new StringBuilder(fp);
	        sb.append(comma).append(' ').append(lfp);
	        stack.push(sb.toString());
	    }
	}

	@Override public void enterEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
	    // for ( {VariableModifier} UnannType VariableDeclaratorId : Expression ) StatementNoShortIf
	    
	}
	@Override public void exitEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    stmt = trimEnd(stmt);
	    if (!stmt.startsWith("{")) {
	        ++tabCount;
	        stmt = "{\n" + indent(stmt) + "}";
	        --tabCount;
	    }
	    if (brokenBracket) {
	        stmt = new StringBuilder("\n").append(stmt).toString();    
	    }
	    String rparen = stack.pop();
	    String expression = stack.pop();
	    String colon = stack.pop();
	    String vdi = stack.pop();
	    String type = stack.pop();
	    String vm = "";
	    if (ctx.variableModifier() != null && ctx.variableModifier().size() > 0) {
	         vm = reverse(ctx.variableModifier().size(), " ") + ' ';   
	    }
	    String lparen = stack.pop();
	    String for_ = stack.pop();
	    sb.append(for_).append(' ').append(padParen(lparen)).append(vm).append(type).append(' ').append(vdi).append(' ').append(colon).append(' ').append(expression).append(padParen(rparen)).append(' ').append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx) {
	    // interfaceModifier* '@' 'interface' Identifier annotationTypeBody
	    
	}
	@Override public void exitAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String body = stack.pop().trim();
	    String identifier = stack.pop();
	    String interface_ = stack.pop();
	    String at = stack.pop().trim();                                                      
	    String ifm = "";
	    if (ctx.interfaceModifier() != null && ctx.interfaceModifier().size() > 0) {
	        ifm = formatModifiers(ctx.interfaceModifier().size()).trim();
	    }
	    addBlankLines(blankLinesBeforeMethods);
	    if (!ifm.isEmpty()) {
	        sb.append(ifm).append(' ');    
	    }
	    sb.append(at).append(interface_).append(' ').append(identifier).append(' ').append(body);
	    stack.push(sb.toString());
	}
	
	/**
 	 * Main entry point.	
 	 */
	@Override public void enterCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx) {
	    // [PackageDeclaration] {ImportDeclaration} {TypeDeclaration}
	    init();    // initialize the formatting settings
	}
	@Override public void exitCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx) {
	    String end = stack.pop();    // EOF and maybe some comments
	    end = end.replace("<EOF>", "");
	    end = removeExcessWhitespace(end);
	    
	    String typeDeclarations = "";
	    if (ctx.typeDeclaration() != null && ctx.typeDeclaration().size() > 0) {
	        typeDeclarations = reverse(ctx.typeDeclaration().size(), "");
	        typeDeclarations = removeBlankLines(typeDeclarations, BOTH) + '\n';
	        typeDeclarations = removeExcessWhitespace(typeDeclarations);
	    }
	    String importDeclarations = "";
	    if (ctx.importDeclaration() != null && ctx.importDeclaration().size() > 0) {
	        importDeclarations = sortAndGroupImports(reverse(ctx.importDeclaration().size()));
	        importDeclarations = removeBlankLines(importDeclarations, BOTH);
	        importDeclarations = removeExcessWhitespace(importDeclarations) + getBlankLines(blankLinesAfterImports);
	    }
	    String packageDeclaration = "";
	    if (ctx.packageDeclaration() != null) {
	        packageDeclaration = stack.pop();
	        packageDeclaration = getBlankLines(blankLinesBeforePackage) + removeBlankLines(packageDeclaration, BOTH);
	        packageDeclaration = removeExcessWhitespace(packageDeclaration) + getBlankLines(blankLinesAfterPackage);
	    }
	    
        output.append(packageDeclaration);
        output.append(importDeclarations);
        output.append(typeDeclarations);
        output.append(end);
	    // all done!
	}

	@Override public void enterWildcardBounds(@NotNull Java8Parser.WildcardBoundsContext ctx) { 
	    // extends ReferenceType
	    // super ReferenceType
	}
	@Override public void exitWildcardBounds(@NotNull Java8Parser.WildcardBoundsContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String type = stack.pop();
	    String word = stack.pop();    // extends or super
	    sb.append(word).append(' ').append(type);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lf_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) {
	    // do nothing here
	}
	@Override public void exitPrimaryNoNewArray_lf_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) { 
	    // do nothing here
	}

	@Override public void enterEnhancedForStatement(@NotNull Java8Parser.EnhancedForStatementContext ctx) { 
	    // for ( {VariableModifier} UnannType VariableDeclaratorId : Expression ) Statement    
	}
	@Override public void exitEnhancedForStatement(@NotNull Java8Parser.EnhancedForStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String statement = stack.pop().trim();
	    if (!statement.startsWith("{")) {
	        ++tabCount;
	        statement = "{\n" + indent(statement) + "}";
	        --tabCount;
	    }
	    if (brokenBracket) {
	         statement = new StringBuilder("\n").append(statement).toString();
	    }
	    String rparen = stack.pop();
	    String expression = stack.pop();
	    String colon = stack.pop();
	    colon = padOperator(colon);
	    String vdi = stack.pop();
	    String type = stack.pop();
	    String modifiers = "";
	    if (ctx.variableModifier() != null && ctx.variableModifier().size() > 0) {
	        modifiers = reverse(ctx.variableModifier().size(), " ") + ' ';
	    }
	    String lparen = stack.pop();
	    String for_ = stack.pop();
	    sb.append(for_).append(' ').append(padParen(lparen)).append(modifiers).append(type).append(' ').append(vdi).append(colon).append(expression).append(padParen(rparen)).append(' ').append(statement);
	    stack.push(sb.toString());
	}

	@Override public void enterSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx) {
	    // switchLabels blockStatements
	    ++ tabCount;
	}
	@Override public void exitSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String bs = ctx.blockStatements() == null ? "" : stack.pop();
	    bs = removeBlankLines(bs, BOTH);
	    bs = indent(bs);
	    bs = indentAgain(bs);
	    -- tabCount;
	    String sl = ctx.switchLabels() == null ? "" : stack.pop();
	    sb.append(sl).append(bs);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeVariable(@NotNull Java8Parser.TypeVariableContext ctx) {
	    // annotationIdentifier
	}
	@Override public void exitTypeVariable(@NotNull Java8Parser.TypeVariableContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    sb.append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx) { 
	    // typeParameterModifier* Identifier typeBound?
	}
	@Override public void exitTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String typeBound = ctx.typeBound() == null ? "" : stack.pop();
	    String identifier = stack.pop();
	    if (ctx.typeParameterModifier() != null && ctx.typeParameterModifier().size() > 0) {
	        sb.append(formatModifiers(ctx.typeParameterModifier().size()));
	    }
	    sb.append(identifier);
	    if (!typeBound.isEmpty()) {
	        sb.append(' ').append(typeBound);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) {
	    // methodModifiers methodHeader methodBody
	}
	@Override public void exitMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
        String body = stack.pop().trim();
	    String header = stack.pop();
	    String modifiers = "";
	    boolean isAbstract = false;
	    if (ctx.methodModifiers().methodModifier().size() > 0) {
            modifiers = stack.pop().trim();
            modifiers = removeBlankLines(modifiers, START);
            isAbstract = modifiers.indexOf("abstract") > -1;
	    }
	    else {
	        indent(sb);   
	    }
	    addBlankLines(blankLinesBeforeMethods);
	    sb.append(modifiers).append(' ').append(header);
	    if (!isAbstract) {
	        sb.append(brokenBracket ? '\n' : ' ');
	    }
	    sb.append(body);
	    sb.append(getBlankLines(blankLinesAfterMethods + 1));
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx) { 
	    // '{' interfaceMemberDeclaration* '}'
	    ++tabCount;
	}
	@Override public void exitInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx) { 
	    String rbrace = stack.pop();    // }
	    rbrace = removeBlankLines(rbrace, START);
	    StringBuilder sb = new StringBuilder();
	    String body = "";
	    if (ctx.interfaceMemberDeclaration() != null && ctx.interfaceMemberDeclaration().size() > 0) {
	        body = reverse(ctx.interfaceMemberDeclaration().size(), "");
	        body = indent(body);
	        body = removeBlankLines(body, BOTH);
	    }
	    addBlankLines(blankLinesAfterClassDeclaration);    // added to lbrace
	    String lbrace = stack.pop();    // {
	    sb.append(brokenBracket ? "\n" : "").append(lbrace);
	    sb.append(body);
	    --tabCount;
	    trimEnd(sb);
	    rbrace = indentRBrace(rbrace);    // indent adds a \n
	    if (!body.isEmpty()) {
	        sb.append('\n');   
	    }
	    sb.append(rbrace);
	    stack.push(sb.toString());
	}

	@Override public void enterMethodBody(@NotNull Java8Parser.MethodBodyContext ctx) {
	    // Block
	    // ;
	}
	@Override public void exitMethodBody(@NotNull Java8Parser.MethodBodyContext ctx) {
	    if (";".equals(stack.peek())) {
	        String semi = stack.pop() + '\n';
	        stack.push(semi);
	    }
	}

	@Override public void enterDims(@NotNull Java8Parser.DimsContext ctx) { 
	    // {Annotation} [ ] {{Annotation} [ ]}
	    // changed this to:
	    // annotationDim (annotationDim)*
	}
	@Override public void exitDims(@NotNull Java8Parser.DimsContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.annotationDim() != null && ctx.annotationDim().size() > 0) {
            sb.append(reverse(ctx.annotationDim().size(), ""));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx) {
	    // ReferenceType
	    // Wildcard
	}
	@Override public void exitTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnannPrimitiveType(@NotNull Java8Parser.UnannPrimitiveTypeContext ctx) {
	    // NumericType 
	    // boolean
	}
	@Override public void exitUnannPrimitiveType(@NotNull Java8Parser.UnannPrimitiveTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterExplicitConstructorInvocation(@NotNull Java8Parser.ExplicitConstructorInvocationContext ctx) { 
	    // [TypeArguments] this                   ( [ArgumentList] ) ;
	    // [TypeArguments] super                  ( [ArgumentList] ) ;
	    // ExpressionName . [TypeArguments] super ( [ArgumentList] ) ;
	    // Primary        . [TypeArguments] super ( [ArgumentList] ) ;
	}
	@Override public void exitExplicitConstructorInvocation(@NotNull Java8Parser.ExplicitConstructorInvocationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String rparen = stack.pop();
	    String args = ctx.argumentList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String word = stack.pop();    // 'this' or 'super'
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	    
	    // 3rd and 4th choice
	    if (ctx.expressionName() != null || ctx.primary() != null) {
	        String dot = stack.pop();
	        String name = stack.pop();    // expression name or primary
	        sb.append(name).append(dot).append(typeArgs).append(word).append(padParen(lparen, args)).append(args).append(padParen(rparen, args)).append(semi);
	    }
	    else {
	        // 1st and second choice
	        sb.append(typeArgs);
	        if (!typeArgs.isEmpty()) {
	            sb.append(' ');    
	        }
	        sb.append(word).append(padParen(lparen, args)).append(args).append(padParen(rparen, args)).append(semi);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterEnumBody(@NotNull Java8Parser.EnumBodyContext ctx) {
	    // { [EnumConstantList] [,] [EnumBodyDeclarations] }
	}
	@Override public void exitEnumBody(@NotNull Java8Parser.EnumBodyContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rbracket = stack.pop();
	    String body = ctx.enumBodyDeclarations() == null ? "" : stack.pop();
	    String comma = ctx.COMMA() == null ? "" : stack.pop() + ' ';
	    String list = ctx.enumConstantList() == null ? "" : stack.pop();
	    String lbracket = stack.pop();
	    sb.append(lbracket).append('\n').append(list).append(comma).append(body).append(rbracket).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterAdditionalBound(@NotNull Java8Parser.AdditionalBoundContext ctx) { 
	    // '&' interfaceType
	}
	@Override public void exitAdditionalBound(@NotNull Java8Parser.AdditionalBoundContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String type = stack.pop();
	    String amp = stack.pop();
	    sb.append(amp).append(type);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) {
        // :	literal
        // |	typeName (squareBrackets)* '.' 'class'
        // |	unannPrimitiveType (squareBrackets)* '.' 'class'
        // |	'void' '.' 'class'
        // |	'this'
        // |	typeName '.' 'this'
        // |	'(' expression ')'
        // |	classInstanceCreationExpression_lfno_primary
        // |	fieldAccess_lfno_primary
        // |	arrayAccess_lfno_primary
        // |	methodInvocation_lfno_primary
        // |	methodReference_lfno_primary
	}
	@Override public void exitPrimaryNoNewArray_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) { 
	    if (ctx.literal() != null || ctx.classInstanceCreationExpression_lfno_primary() != null ||
	        ctx.fieldAccess_lfno_primary() != null || ctx.arrayAccess_lfno_primary() != null ||
	        ctx.methodInvocation_lfno_primary() != null || ctx.methodReference_lfno_primary() != null) {
	        // one of these choices is already on the stack
	        return;
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    
	    // expression
	    if (ctx.expression() != null) {
	        String rparen = stack.pop();
	        String expression = stack.pop();
	        String lparen = stack.pop();
	        sb.append(padParen(lparen)).append(expression).append(padParen(rparen));
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 2 typeName choices
	    String raw = ctx.getText();
	    if (ctx.typeName() != null) {
	        if (raw.indexOf("class") > -1) {
	            // first typeName choice
	            String class_ = stack.pop();
	            String dot = stack.pop();
	            StringBuilder brackets = new StringBuilder();
	            if (ctx.squareBrackets() != null && ctx.squareBrackets().size() > 0) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    brackets.append(stack.pop());
	                }
	            }
	            String typename = stack.pop();
	            sb.append(typename).append(brackets).append(dot).append(class_);
	        }
	        else {
	            // second typeName choice
	            String this_ = stack.pop();
	            String dot = stack.pop();
	            String typename = stack.pop();
	            sb.append(typename).append(dot).append(this_);
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // unann primitive type
	    if (ctx.unannPrimitiveType() != null) {
            String class_ = stack.pop();
            String dot = stack.pop();
            StringBuilder brackets = new StringBuilder();
            if (ctx.squareBrackets() != null && ctx.squareBrackets().size() > 0) {
                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
                    brackets.append(stack.pop());
                }
            }
            String type = stack.pop();
            sb.append(type).append(brackets).append(dot).append(class_);
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 'void' and 'this' choice
	    if (raw.indexOf("void") > -1) {
            String class_ = stack.pop();
            String dot = stack.pop();
	        String void_ = stack.pop();
	        sb.append(void_).append(dot).append(class_);
	    }
	    else {
	        sb.append(stack.pop());    // 'this'   
	    }
	    stack.push(sb.toString());
	    
	}

	@Override public void enterUnannClassOrInterfaceType(@NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx) { 
	    // :(	unannClassType_lfno_unannClassOrInterfaceType
	    // |	unannInterfaceType_lfno_unannClassOrInterfaceType
	    // )
	    // (	unannClassType_lf_unannClassOrInterfaceType
	    // |	unannInterfaceType_lf_unannClassOrInterfaceType
	    // )*
	}
	@Override public void exitUnannClassOrInterfaceType(@NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    // second half
	    String sh = "";
	    if (ctx.unannClassType_lf_unannClassOrInterfaceType() != null || ctx.unannInterfaceType_lf_unannClassOrInterfaceType() != null) {
	        int howMany = ctx.unannClassType_lf_unannClassOrInterfaceType() == null ? 0 : ctx.unannClassType_lf_unannClassOrInterfaceType().size();
	        howMany += ctx.unannInterfaceType_lf_unannClassOrInterfaceType() == null ? 0 : ctx.unannInterfaceType_lf_unannClassOrInterfaceType().size();
	        sh = howMany > 0 ? reverse(howMany, "") : "";
	    }
	    sb.append(stack.pop());        // first half
	    if (!sh.isEmpty()) {
	        sb.append(sh);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) { 
	    // 'if' '(' expression ')' statement
	}
	@Override public void exitIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop().trim();
	    if (!stmt.startsWith("{")) {
	        ++tabCount;
	        stmt = "{\n" + indent(stmt) + "}";
	        --tabCount;
	    }
	    String rparen = stack.pop();
	    String expr = stack.pop();
	    String lparen = stack.pop();
	    String if_ = stack.pop();
	    sb.append(if_).append(' ').append(padParen(lparen)).append(expr).append(padParen(rparen)).append(brokenBracket? '\n' : ' ').append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterPostDecrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) {
	    // '--'
	}
	@Override public void exitPostDecrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterInterfaceType(@NotNull Java8Parser.InterfaceTypeContext ctx) {
	    // ClassType
	}
	@Override public void exitInterfaceType(@NotNull Java8Parser.InterfaceTypeContext ctx) {
	    // nothing to do here, class type should already be on the stack.
	}

	@Override public void enterMethodReference_lf_primary(@NotNull Java8Parser.MethodReference_lf_primaryContext ctx) { 
	    // '::' typeArguments? Identifier
	}
	@Override public void exitMethodReference_lf_primary(@NotNull Java8Parser.MethodReference_lf_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	    String colons = stack.pop();
	    sb.append(colons).append(' ').append(typeArgs).append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterAndExpression(@NotNull Java8Parser.AndExpressionContext ctx) { 
	    // equalityExpression
	    // andExpression '&' equalityExpression	
	}
	@Override public void exitAndExpression(@NotNull Java8Parser.AndExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String equalityExpression = stack.pop();
	    if (ctx.andExpression() != null) {
	        String ampersand = stack.pop();
	        String andExpression = stack.pop();
	        sb.append(andExpression).append(padOperator(ampersand));
	    }
	    sb.append(equalityExpression);
	    stack.push(sb.toString());
	}

	@Override public void enterEnumConstantModifier(@NotNull Java8Parser.EnumConstantModifierContext ctx) { 
	    // Annotation
	}
	@Override public void exitEnumConstantModifier(@NotNull Java8Parser.EnumConstantModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnannClassType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
	    // Identifier typeArguments?
	}
	@Override public void exitUnannClassType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop();
	    String identifier = stack.pop();
	    sb.append(identifier).append(typeArgs);
	    stack.push(sb.toString());
	}

	@Override public void enterStatement(@NotNull Java8Parser.StatementContext ctx) {
	    // StatementWithoutTrailingSubstatement
	    // LabeledStatement
	    // IfThenStatement
	    // IfThenElseStatement
	    // WhileStatement
	    // ForStatement
	}
	@Override public void exitStatement(@NotNull Java8Parser.StatementContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterFieldModifiers(@NotNull Java8Parser.FieldModifiersContext ctx) { 
	    // fieldModifier*
	}
	@Override public void exitFieldModifiers(@NotNull Java8Parser.FieldModifiersContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.fieldModifier() != null && ctx.fieldModifier().size() > 0) {
	        sb.append(formatModifiers(ctx.fieldModifier().size()));
	    }
	    if (sb.length() > 0) {
            trimEnd(sb);
            stack.push(sb.toString());
        }
	}
	@Override public void enterFieldModifier(@NotNull Java8Parser.FieldModifierContext ctx) { 
	    // (one of) Annotation public protected private
	    // static final transient volatile
	}
	@Override public void exitFieldModifier(@NotNull Java8Parser.FieldModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterMarkerAnnotation(@NotNull Java8Parser.MarkerAnnotationContext ctx) { 
	    // @ TypeName	
	}
	@Override public void exitMarkerAnnotation(@NotNull Java8Parser.MarkerAnnotationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typename = stack.pop();
	    String at = stack.pop();
	    sb.append(at).append(typename);
	    stack.push(sb.toString());
	}

	@Override public void enterResourceList(@NotNull Java8Parser.ResourceListContext ctx) {
	    // resource (';' resource)*	
	}
	@Override public void exitResourceList(@NotNull Java8Parser.ResourceListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.resource() != null) {
	        sb.append(reverse(ctx.resource().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterArrayAccess_lfno_primary(@NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx) {
        // (	expressionName '[' expression ']'
        // |	primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary '[' expression ']'
        // )
        // (	primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary '[' expression ']'
        // )*
	}
	@Override public void exitArrayAccess_lfno_primary(@NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    // second half
	    StringBuilder sh = new StringBuilder();
	    if (ctx.primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary() != null) {
	        for (int i = 0; i < ctx.primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary().size(); i++) {
	            String rsquare = stack.pop();
	            String expr = stack.pop();
	            String lsquare = stack.pop();
	            // primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary doesn't put anything on the stack
	            sh.append(lsquare).append(expr).append(rsquare);
	        }
	    }
	    String rsquare = stack.pop();
	    String expr = stack.pop();
	    String lsquare = stack.pop();
	    String name = stack.pop();
	    sb.append(name).append(lsquare).append(expr).append(rsquare);
	    if (sh.length() > 0) {
	        sb.append(sh);    
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterStaticInitializer(@NotNull Java8Parser.StaticInitializerContext ctx) { 
	    // static Block
	}
	@Override public void exitStaticInitializer(@NotNull Java8Parser.StaticInitializerContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    sb.append('\n');
	    String block = stack.pop();
	    String static_ = stack.pop();
	    sb.append(indent(static_)).append(' ').append(block).append(getBlankLines(blankLinesAfterMethods + 1));
	    stack.push(sb.toString());
	}

	@Override public void enterConditionalExpression(@NotNull Java8Parser.ConditionalExpressionContext ctx) {
	    // ConditionalOrExpression 
	    // ConditionalOrExpression ? Expression : ConditionalExpression 
	    // ConditionalOrExpression ? Expression : LambdaExpression 	
	}
	@Override public void exitConditionalExpression(@NotNull Java8Parser.ConditionalExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    // only need to handle the last 2 choices
	    if (ctx.conditionalExpression() != null || ctx.lambdaExpression() != null) {
	        String last = stack.pop();
	        String colon = stack.pop();
	        String expr = stack.pop();
	        String q = stack.pop();
	        String coe = stack.pop();
	        sb.append(coe).append(padOperator(q)).append(expr).append(padOperator(colon)).append(last);
	        stack.push(sb.toString());
	    }
	}

	@Override public void enterFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx) {
	    // fieldModifiers unannType variableDeclaratorList ';'
	}
	@Override public void exitFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String vdl = stack.pop();
	    String type = stack.pop().trim();
	    if (ctx.fieldModifiers().fieldModifier().size() > 0) { 
            String modifiers = stack.pop();     // modifiers are already combined on the stack
            sb.append(modifiers).append(' ');
	    }
	    else {
	        indent(sb);    
	    }
	    sb.append(type).append(' ');
	    sb.append(vdl).append(semi).append('\n');
	    stack.push(sb.toString());
	}

	@Override public void enterLeftHandSide(@NotNull Java8Parser.LeftHandSideContext ctx) {
	    // :	expressionName
	    // |	fieldAccess
	    // |	arrayAccess
	
	}
	@Override public void exitLeftHandSide(@NotNull Java8Parser.LeftHandSideContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterBasicForStatement(@NotNull Java8Parser.BasicForStatementContext ctx) { 
	    // for ( [ForInit] ; [Expression] ; [ForUpdate] ) Statement
	}
	@Override public void exitBasicForStatement(@NotNull Java8Parser.BasicForStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop().trim();
	    if (!stmt.startsWith("{")) {
	        ++tabCount;
	        stmt = (brokenBracket ? "\n" : "") + "{\n" + indent(stmt) + "}";
	        --tabCount;
	    }
	    if (brokenBracket) {
	        stmt = new StringBuilder("\n").append(stmt).toString();    
	    }
	    String rparen = stack.pop();
	    String update = ctx.forUpdate() == null ? "" : stack.pop();
	    String semi2 = stack.pop();
	    String expr = ctx.expression() == null ? "" : stack.pop();
	    String semi1 = stack.pop();
	    String init = ctx.forInit() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String for_ = stack.pop();
	    sb.append(for_).append(' ').append(padParen(lparen)).append(init).append(semi1).append(' ').append(expr).append(semi2).append(' ').append(update).append(padParen(rparen)).append(' ').append(stmt);

	    stack.push(sb.toString());
	}

	@Override public void enterWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) { 
	    // while ( Expression ) Statement
	}
	@Override public void exitWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop().trim();
	    stmt = trimEnd(stmt);
	    if (!stmt.startsWith("{")) {
	        ++tabCount;
	        stmt = "{\n" + indent(stmt) + "}";
	        --tabCount;
	    }
	    String rparen = stack.pop();
	    String expr = stack.pop();
	    String lparen = stack.pop();
	    String while_ = stack.pop();
	    sb.append(while_).append(' ').append(padParen(lparen)).append(expr).append(padParen(rparen));
	    if (!";".equals(stmt)) {
	        sb.append(' ');
	    }
	    sb.append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) { 
	    // packageModifier* 'package' Identifier ('.' Identifier)* ';'
	}
	@Override public void exitPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String identifiers = "";
	    if (ctx.Identifier() != null ) {
	        identifiers = reverse(ctx.Identifier().size() * 2 - 1, "");
	    }
	    String package_ = stack.pop();
	    if (ctx.packageModifier() != null && !ctx.packageModifier().isEmpty()) {
	        sb.append(formatModifiers(ctx.packageModifier().size()));
	    }
	    sb.append(package_).append(' ').append(identifiers).append(semi);
	    stack.push(sb.toString());
	}

	@Override public void enterLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx) { 
	    // variableModifier* unannType variableDeclaratorList
	}
	@Override public void exitLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
        String vdl = stack.pop();
        String type = stack.pop();
	    if (ctx.variableModifier() != null && !ctx.variableModifier().isEmpty()) {
	        sb.append(reverse(ctx.variableModifier().size(), " ")).append(' ');
	    }
        sb.append(type).append(' ').append(vdl);
        stack.push(sb.toString());
	}

	@Override public void enterSuperinterfaces(@NotNull Java8Parser.SuperinterfacesContext ctx) { 
	    // implements InterfaceTypeList
	}
	@Override public void exitSuperinterfaces(@NotNull Java8Parser.SuperinterfacesContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String list = stack.pop();
	    String implements_ = stack.pop();
	    sb.append(implements_).append(' ').append(list);
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx) { 
	    // ConstantDeclaration 
	    // InterfaceMethodDeclaration 
	    // ClassDeclaration 
	    // InterfaceDeclaration 
	    // ;	
	}
	@Override public void exitInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx) {
	    if (";".equals(stack.peek())) {
	        String semi = stack.pop() + '\n';
	        stack.push(semi);
	    }
	}

	@Override public void enterClassInstanceCreationExpression_lf_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
	    // '.' 'new' typeArguments? annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
	}
	@Override public void exitClassInstanceCreationExpression_lf_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String classBody = ctx.classBody() == null ? "" : stack.pop();
	    if (!classBody.isEmpty() && brokenBracket && classBody.startsWith("{")) {
	        classBody = new StringBuilder("\n").append(classBody).toString();
	    }
	    String rparen = stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String typeOrDiamond = ctx.typeArgumentsOrDiamond() == null ? "" : stack.pop();
	    String identifier = stack.pop();
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	    String new_ = stack.pop();
	    String dot = stack.pop();
	    sb.append(dot).append(new_).append(typeArgs).append(identifier).append(typeOrDiamond).append(padParen(lparen, argumentList)).append(argumentList).append(padParen(rparen, argumentList)).append(classBody);
	    stack.push(sb.toString());
	}

	@Override public void enterSwitchBlock(@NotNull Java8Parser.SwitchBlockContext ctx) { 
	    // '{' switchBlockStatementGroup* switchLabel* '}'
	}
	@Override public void exitSwitchBlock(@NotNull Java8Parser.SwitchBlockContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rbracket = stack.pop().trim();
	    ++ tabCount;
	    String label = "";
	    if (ctx.switchLabel() != null && ctx.switchLabel().size() > 0) {
	        label = reverse(ctx.switchLabel().size(), " ");
	        label = indent(label);
	    }
	    String group = "";
	    if (ctx.switchBlockStatementGroup() != null && ctx.switchBlockStatementGroup().size() > 0) {
	        group = reverse(ctx.switchBlockStatementGroup().size(), "");
	        group = indent(group);
	    }
	    -- tabCount;
	    String lbracket = stack.pop().trim();
	    sb.append(brokenBracket ? "\n" : "").append(lbracket).append('\n').append(group).append(label).append(rbracket).append('\n');
	    stack.push(sb.toString());
	}

	@Override public void enterForInit(@NotNull Java8Parser.ForInitContext ctx) { 
	    // StatementExpressionList
	    // LocalVariableDeclaration
	}
	@Override public void exitForInit(@NotNull Java8Parser.ForInitContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterAnnotationDim(@NotNull Java8Parser.AnnotationDimContext ctx) { 
	    // annotation* squareBrackets
	}
	@Override public void exitAnnotationDim(@NotNull Java8Parser.AnnotationDimContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String squareBrackets = stack.pop();
	    if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                sb.append(stack.pop());
                if (i < ctx.annotation().size() - 1) {
                    sb.append(' ');                
                }
            }
	    }
	    sb.append(squareBrackets);
	    stack.push(sb.toString());
	}

	@Override public void enterBlockStatements(@NotNull Java8Parser.BlockStatementsContext ctx) {
	    // BlockStatement {BlockStatement}
	}
	@Override public void exitBlockStatements(@NotNull Java8Parser.BlockStatementsContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.blockStatement() != null) {
	        sb.append(reverse(ctx.blockStatement().size(), ""));
	    }
	    trimEnd(sb);
	    sb.append('\n');
	    stack.push(sb.toString());
	}

	@Override public void enterFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx) { 
	    // FormalParameter {, FormalParameter}
	    // ReceiverParameter {, FormalParameter}
	}
	@Override public void exitFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.formalParameter() != null) {
	        sb.append(reverse(ctx.formalParameter().size() * 2 - 1, " ", 2));
	    }
        if (ctx.receiverParameter() != null) {
            sb.insert(0, stack.pop());    
        }
	    stack.push(sb.toString());
	}

	@Override public void enterTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx) {
	    // TypeArguments 
	    // <>	
	}
	@Override public void exitTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) { 
        // :	literal
        // |	typeName (squareBrackets)* '.' 'class'
        // |	unannPrimitiveType (squareBrackets)* '.' 'class'
        // |	'void' '.' 'class'
        // |	'this'
        // |	typeName '.' 'this'
        // |	'(' expression ')'
        // |	classInstanceCreationExpression_lfno_primary
        // |	fieldAccess_lfno_primary
        // |	methodInvocation_lfno_primary
        // |	methodReference_lfno_primary
	
	}
	@Override public void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) { 
	    if (ctx.literal() != null || ctx.classInstanceCreationExpression_lfno_primary() != null ||
	        ctx.fieldAccess_lfno_primary() != null  ||
	        ctx.methodInvocation_lfno_primary() != null || ctx.methodReference_lfno_primary() != null) {
	        // one of these choices is already on the stack
	        return;
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    
	    // expression
	    if (ctx.expression() != null) {
	        String rparen = stack.pop();
	        String expr = stack.pop();
	        String lparen = stack.pop();
	        sb.append(padParen(lparen)).append(expr).append(padParen(rparen));
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 2 typeName choices
	    String raw = ctx.getText();
	    if (ctx.typeName() != null) {
	        if (raw.indexOf("class") > -1) {
	            // first typeName choice
	            String class_ = stack.pop();
	            String dot = stack.pop();
	            if (ctx.squareBrackets() != null && ctx.squareBrackets().size() > 0) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append(stack.pop());
	                }
	            }
	            String typename = stack.pop();
	            sb.insert(0, typename);
	            sb.append(dot).append(class_);
	        }
	        else {
	            // second typeName choice
	            String this_ = stack.pop();
	            String dot = stack.pop();
	            String typename = stack.pop();
	            sb.append(typename).append(dot).append(this_);
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // unann primitive type
	    if (ctx.unannPrimitiveType() != null) {
            String class_ = stack.pop();
            String dot = stack.pop();
            if (ctx.squareBrackets() != null && ctx.squareBrackets().size() > 0) {
                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
                    sb.append(stack.pop());
                }
            }
            String type = stack.pop();
            sb.insert(0, type);
            sb.append(dot).append(class_);
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 'void' and 'this' choice
	    if (raw.indexOf("void") > -1) {
            String class_ = stack.pop();
            String dot = stack.pop();
	        sb.append(stack.pop()).append(dot).append(class_);
	    }
	    else {
	        sb.append(stack.pop());    // 'this'   
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx) { 
	    // default ElementValue	
	}
	@Override public void exitDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String value = stack.pop();
	    String def = stack.pop();
	    sb.append(def).append(' ').append(value);
	    stack.push(sb.toString());
	}

	@Override public void enterType(@NotNull Java8Parser.TypeContext ctx) {
	    // :	primitiveType
	    // |	referenceType
	}
	@Override public void exitType(@NotNull Java8Parser.TypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterElementValuePairList(@NotNull Java8Parser.ElementValuePairListContext ctx) { 
	    // ElementValuePair {, ElementValuePair}	
	}
	@Override public void exitElementValuePairList(@NotNull Java8Parser.ElementValuePairListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.elementValuePair() != null) {
	        sb.append(reverse(ctx.elementValuePair().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterSynchronizedStatement(@NotNull Java8Parser.SynchronizedStatementContext ctx) {
	    // synchronized ( Expression ) Block
	}
	@Override public void exitSynchronizedStatement(@NotNull Java8Parser.SynchronizedStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String block = stack.pop();
	    String rparen = stack.pop();
	    String expr = stack.pop();
	    String lparen = stack.pop();
	    String sync = stack.pop();
	    sb.append(sync).append(' ').append(padParen(lparen)).append(expr).append(padParen(rparen)).append(block);
	    stack.push(sb.toString());
	}

	@Override public void enterUnannClassType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) { 
	    // '.' annotationIdentifier typeArguments?
	}
	@Override public void exitUnannClassType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String args = ctx.typeArguments() == null ? "" : ' ' + stack.pop();
	    String ann = stack.pop();
	    String dot = stack.pop();
	    sb.append(dot).append(ann).append(args);
	    stack.push(sb.toString());
	}

	@Override public void enterAdditiveOperator(@NotNull Java8Parser.AdditiveOperatorContext ctx) { 
        // :   '+'
        // |   '-'
	}
	@Override public void exitAdditiveOperator(@NotNull Java8Parser.AdditiveOperatorContext ctx) {
	    stack.pop();
	    stack.push(padOperator(ctx.getText()));
	}

	@Override public void enterSuperclass(@NotNull Java8Parser.SuperclassContext ctx) { 
	    // extends ClassType
	}
	@Override public void exitSuperclass(@NotNull Java8Parser.SuperclassContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String ct = stack.pop();
	    String ext = stack.pop();
	    sb.append(ext).append(' ').append(ct);
	    stack.push(sb.toString());
	}

	@Override public void enterBlock(@NotNull Java8Parser.BlockContext ctx) {
	    // { [BlockStatements] }
	    ++tabCount;
	}
	@Override public void exitBlock(@NotNull Java8Parser.BlockContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rbracket = stack.pop();
	    while(rbracket.startsWith("\n")) {
	        rbracket = rbracket.substring(1, rbracket.length());    
	    }
	    if (ctx.blockStatements() != null) {
	        String stmt = indent(stack.pop());
            if (!stmt.startsWith("\n")) {
                sb.append('\n');
            }
            sb.append(stmt);
            if (!stmt.endsWith("\n")) {
                sb.append('\n');   
            }
	    }
	    --tabCount;
	    String lbracket = stack.pop();
	    rbracket = trimEnd(indent(rbracket));
	    sb.insert(0, lbracket);
	    if (brokenBracket) {
	        sb.insert(0, "\n");    
	    }
	    if (sb.charAt(sb.length() - 1) != '\n') {
	        sb.append('\n');    
	    }
	    sb.append(rbracket);
	    stack.push(sb.toString());
	}

	@Override public void enterExpressionStatement(@NotNull Java8Parser.ExpressionStatementContext ctx) { 
	    // StatementExpression ;
	}
	@Override public void exitExpressionStatement(@NotNull Java8Parser.ExpressionStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String stmt = stack.pop();
	    sb.append(stmt).append(semi);
	    stack.push(sb.toString());
	}

	@Override public void enterPreIncrementExpression(@NotNull Java8Parser.PreIncrementExpressionContext ctx) { 
	    // ++ UnaryExpression	
	}
	@Override public void exitPreIncrementExpression(@NotNull Java8Parser.PreIncrementExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
	    String plus = stack.pop();
	    sb.append(plus).append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx) { 
	    // ';' classBodyDeclaration*
	}
	@Override public void exitEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String body = "";
	    if (ctx.classBodyDeclaration() != null) {
	        body = reverse(ctx.classBodyDeclaration().size(), " ");
	    }
	    String semi = stack.pop();
	    sb.append(semi).append(' ').append(body);
	    stack.push(sb.toString());
	}

	@Override public void enterDimExprs(@NotNull Java8Parser.DimExprsContext ctx) { 
	    // DimExpr {DimExpr}
	}
	@Override public void exitDimExprs(@NotNull Java8Parser.DimExprsContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.dimExpr() != null && ctx.dimExpr().size() > 0) {
	        sb.append(reverse(ctx.dimExpr().size(), " ")).append(' ');
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterForUpdate(@NotNull Java8Parser.ForUpdateContext ctx) { 
	    // StatementExpressionList
	}
	@Override public void exitForUpdate(@NotNull Java8Parser.ForUpdateContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterEmptyStatement(@NotNull Java8Parser.EmptyStatementContext ctx) {
	    // ; 
	}
	@Override public void exitEmptyStatement(@NotNull Java8Parser.EmptyStatementContext ctx) { 
	    if (";".equals(stack.peek())) {
	        String semi = stack.pop() + '\n';
	        stack.push(semi);
	    }
	}

	@Override public void enterPrimaryNoNewArray_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) { 
        // :	classInstanceCreationExpression_lf_primary
        // |	fieldAccess_lf_primary
        // |	arrayAccess_lf_primary
        // |	methodInvocation_lf_primary
        // |	methodReference_lf_primary
	
	}
	@Override public void exitPrimaryNoNewArray_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.	
	}

	@Override public void enterAnnotationTypeElementModifier(@NotNull Java8Parser.AnnotationTypeElementModifierContext ctx) {
	    // :	annotation
        // |	'public'
        // |	'abstract'
	}
	@Override public void exitAnnotationTypeElementModifier(@NotNull Java8Parser.AnnotationTypeElementModifierContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterShiftExpression(@NotNull Java8Parser.ShiftExpressionContext ctx) { 
	    // AdditiveExpression 
	    // ShiftExpression shiftOperator AdditiveExpression 
	    // ShiftExpression shiftOperator AdditiveExpression 
	    // ShiftExpression shiftOperator AdditiveExpression	
	}
	@Override public void exitShiftExpression(@NotNull Java8Parser.ShiftExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String additiveExpression = stack.pop();
	    if (ctx.shiftExpression() != null) {
            // 2 or 3 shift operator symbols on the stack, either "<" "<", or ">" ">" or ">" ">" ">"
            StringBuilder shiftOperator = new StringBuilder(stack.pop()).append(stack.pop());
            String so = "";
            String rangle = stack.pop();
            String shiftExpression;
            if (">".equals(rangle)) {
                shiftOperator.append(rangle);
                shiftExpression = stack.pop();
            }
            else {
                shiftExpression = rangle;    
            }
            so = padOperator(shiftOperator.toString());
            sb.append(shiftExpression).append(so);
	    }
	    sb.append(additiveExpression);
	    stack.push(sb.toString());
	}
	
	@Override public void enterFieldAccess_lf_primary(@NotNull Java8Parser.FieldAccess_lf_primaryContext ctx) { 
	    // '.' Identifier
	}
	@Override public void exitFieldAccess_lf_primary(@NotNull Java8Parser.FieldAccess_lf_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    String dot = stack.pop();
	    sb.append(dot).append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterInstanceInitializer(@NotNull Java8Parser.InstanceInitializerContext ctx) { 
	    // Block
	}
	@Override public void exitInstanceInitializer(@NotNull Java8Parser.InstanceInitializerContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnannType(@NotNull Java8Parser.UnannTypeContext ctx) {
	    // UnannPrimitiveType
	    // UnannReferenceType
	}
	@Override public void exitUnannType(@NotNull Java8Parser.UnannTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterIntegralType(@NotNull Java8Parser.IntegralTypeContext ctx) {
	    // (one of) byte short int long char
	}
	@Override public void exitIntegralType(@NotNull Java8Parser.IntegralTypeContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterPostIncrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) { 
	    // '++'
	}
	@Override public void exitPostIncrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx) {
	    // (	classType_lfno_classOrInterfaceType
	    // |	interfaceType_lfno_classOrInterfaceType
	    // )
	    // (	classType_lf_classOrInterfaceType
	    // |	interfaceType_lf_classOrInterfaceType
	    // )*                                               
	}
	@Override public void exitClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    // second half
	    String sh = "";
	    if (ctx.classType_lf_classOrInterfaceType() != null || ctx.interfaceType_lf_classOrInterfaceType() != null) {
	        int howMany = ctx.classType_lf_classOrInterfaceType() == null ? 0 : ctx.classType_lf_classOrInterfaceType().size();
	        howMany += ctx.interfaceType_lf_classOrInterfaceType() == null ? 0 : ctx.interfaceType_lf_classOrInterfaceType().size();
	        sh = howMany > 0 ? reverse(howMany, "") : "" ;
	    }
	    sb.append(stack.pop());        // first half
	    if (!sh.isEmpty()) {
	        sb.append(sh);
	    }
	    stack.push(sb.toString());
		
	}

	@Override public void enterEqualityExpression(@NotNull Java8Parser.EqualityExpressionContext ctx) {
	    // RelationalExpression 
	    // EqualityExpression == RelationalExpression 
	    // EqualityExpression != RelationalExpression	
	}
	@Override public void exitEqualityExpression(@NotNull Java8Parser.EqualityExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String re = stack.pop();
	    if (ctx.equalityExpression() != null) {
	        String operator = stack.pop();
	        operator = padOperator(operator);
	        String ee = stack.pop();
	        sb.append(ee).append(operator);
	    }
	    sb.append(re);
	    stack.push(sb.toString());
	}

	@Override public void enterNormalAnnotation(@NotNull Java8Parser.NormalAnnotationContext ctx) { 
	    // @ TypeName ( [ElementValuePairList] )
	}
	@Override public void exitNormalAnnotation(@NotNull Java8Parser.NormalAnnotationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rparen = stack.pop();
	    String list = ctx.elementValuePairList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String name = stack.pop();
	    String at = stack.pop();
	    sb.append(at).append(name).append(padParen(lparen, list)).append(list).append(padParen(rparen, list));
        stack.push(sb.toString());
	}

	@Override public void enterTypeBound(@NotNull Java8Parser.TypeBoundContext ctx) { 
	    // extends TypeVariable
	    // extends ClassOrInterfaceType {AdditionalBound}
	}
	@Override public void exitTypeBound(@NotNull Java8Parser.TypeBoundContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.typeVariable() != null) {
	        sb.append(stack.pop()); 
	        sb.insert(0, stack.pop() + ' ');
	    }
	    else {
	        String bounds = "";
	        if (ctx.additionalBound() != null && ctx.additionalBound().size() > 0) {
	            bounds = reverse(ctx.additionalBound().size(), " ");
	        }
	        sb.append(stack.pop());
	        String type = stack.pop();
	        String ext = stack.pop();
	        sb.append(ext).append(' ').append(type);
	        sb.append(bounds.isEmpty() ? "" : " " + bounds);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterPrimary(@NotNull Java8Parser.PrimaryContext ctx) {
        // 	   (	primaryNoNewArray_lfno_primary
        //     |	arrayCreationExpression
        //     )
        //     (	primaryNoNewArray_lf_primary
        //     )*
	}
	@Override public void exitPrimary(@NotNull Java8Parser.PrimaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    StringBuilder lf_primary = new StringBuilder();
	    if (ctx.primaryNoNewArray_lf_primary() != null && ctx.primaryNoNewArray_lf_primary().size() > 0) {
	        lf_primary.append(reverse(ctx.primaryNoNewArray_lf_primary().size(), ""));
	    }
	    if (ctx.primaryNoNewArray_lfno_primary() != null || ctx.arrayCreationExpression() != null) {
	        sb.append(stack.pop());    
	    }
	    sb.append(lf_primary.toString());
	    
	    stack.push(sb.toString());
	}

	@Override public void enterClassModifiers(@NotNull Java8Parser.ClassModifiersContext ctx) { 
	    // classmodifier*
	}
	@Override public void exitClassModifiers(@NotNull Java8Parser.ClassModifiersContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.classModifier() != null && ctx.classModifier().size() > 0) {
	        sb.append(formatModifiers(ctx.classModifier().size()));
	    }
	    if (sb.length() > 0) {
            trimEnd(sb);
            stack.push(sb.toString());
        }
	}
	@Override public void enterClassModifier(@NotNull Java8Parser.ClassModifierContext ctx) { 
	    // (one of) Annotation public protected private
	    // abstract static final strictfp
	}
	@Override public void exitClassModifier(@NotNull Java8Parser.ClassModifierContext ctx) {
	    // nothing to do here, either an annotation or one of the terminals will be on the stack
	}

	@Override public void enterFieldAccess(@NotNull Java8Parser.FieldAccessContext ctx) { 
	    // Primary . Identifier
	    // super . Identifier
	    // TypeName . super . Identifier
	}
	@Override public void exitFieldAccess(@NotNull Java8Parser.FieldAccessContext ctx) { 
	    StringBuilder sb = new StringBuilder();
        String identifier = stack.pop();
        String dot = stack.pop();
	    if (ctx.primary() != null) {
	        String primary = stack.pop();
	        primary = trimEnd(primary);
	        sb.append(primary);
	    }
	    else if (ctx.typeName() != null) {
	        String sup = stack.pop();
	        String dot1 = stack.pop();
	        sb.append(stack.pop()).append(dot1).append(sup);    
	    }
	    else {
	        sb.append(stack.pop());    // super   
	    }
	    sb.append(dot).append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeName(@NotNull Java8Parser.TypeNameContext ctx) {
	    // Identifier
	    // PackageOrTypeName . Identifier 
	}
	@Override public void exitTypeName(@NotNull Java8Parser.TypeNameContext ctx) {
	    StringBuilder sb = new StringBuilder();                                           
	    String identifier = stack.pop();
	    if (ctx.packageOrTypeName() != null) {
	        String dot = stack.pop();
	        String typename = stack.pop();
	        sb.append(typename).append(dot);
	    }
	    sb.append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx) {
	    // '{' explicitConstructorInvocation? blockStatements? '}'
	    ++tabCount;
	}
	@Override public void exitConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rbracket = stack.pop().trim();
	    String bs = ctx.blockStatements() == null ? "" : stack.pop().trim();
	    bs = bs.isEmpty() ? "" : indent(bs);
	    String eci = ctx.explicitConstructorInvocation() == null ? "" : stack.pop();
	    eci = eci.isEmpty() ? "" : indent(eci);
	    eci = trimEnd(eci);
	    String lbracket = stack.pop().trim();
	    sb.append(brokenBracket ? "\n" : "").append(lbracket).append(eci).append('\n').append(bs);
	    --tabCount;
	    String end = indent(rbracket);
	    sb.append(end);
	    stack.push(sb.toString());
	}

	@Override public void enterCatchClause(@NotNull Java8Parser.CatchClauseContext ctx) {
	    // catch ( CatchFormalParameter ) Block	
	}
	@Override public void exitCatchClause(@NotNull Java8Parser.CatchClauseContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String block = stack.pop();
	    block = trimFront(block);
        if (bracketStyle == BROKEN) {
            block = new StringBuilder("\n").append(block).toString();
        }
	    String rparen = stack.pop();
	    String cfp = stack.pop();
	    String lparen = stack.pop();
	    String cat = stack.pop();
	    sb.append(cat).append(' ').append(padParen(lparen)).append(cfp).append(padParen(rparen)).append(' ').append(block);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) { 
	    // do nothing here
	}
	@Override public void exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) { 
	    // do nothing here
	}

	@Override public void enterLabeledStatement(@NotNull Java8Parser.LabeledStatementContext ctx) {
	    // Identifier : Statement
	}
	@Override public void exitLabeledStatement(@NotNull Java8Parser.LabeledStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    String colon = stack.pop();
	    String identifier = stack.pop();
	    sb.append(identifier).append(colon).append(' ').append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterSwitchLabels(@NotNull Java8Parser.SwitchLabelsContext ctx) { 
	    // SwitchLabel {SwitchLabel}
	}
	@Override public void exitSwitchLabels(@NotNull Java8Parser.SwitchLabelsContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.switchLabel() != null && ctx.switchLabel().size() > 0) {
	        sb.append(reverse(ctx.switchLabel().size(), ""));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx) {
	    // {VariableModifier} UnannType VariableDeclaratorId
	}
	@Override public void exitFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String vdi = stack.pop();
	    String unannType = stack.pop();
	    if (ctx.variableModifier() != null && !ctx.variableModifier().isEmpty()) {
	        sb.append(reverse(ctx.variableModifier().size(), " ")).append(' ');
	    }
	    sb.append(unannType).append(' ').append(vdi);
	    stack.push(sb.toString());
	}
	@Override public void enterInterfaceType_lf_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) { 
	    // classType_lf_classOrInterfaceType
	}
	@Override public void exitInterfaceType_lf_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterSingleTypeImportDeclaration(@NotNull Java8Parser.SingleTypeImportDeclarationContext ctx) { 
	    // import TypeName ;
	}
	@Override public void exitSingleTypeImportDeclaration(@NotNull Java8Parser.SingleTypeImportDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String name = stack.pop();
	    String imp = stack.pop();
	    sb.append(imp).append(' ').append(name).append(semi).append('\n');
	    stack.push(sb.toString());
	}

	@Override public void enterSingleElementAnnotation(@NotNull Java8Parser.SingleElementAnnotationContext ctx) { 
	    // @ TypeName ( ElementValue )
	}
	@Override public void exitSingleElementAnnotation(@NotNull Java8Parser.SingleElementAnnotationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rparen = stack.pop();
	    String ev = stack.pop();
	    String lparen = stack.pop();
	    String name = stack.pop();
	    String at = stack.pop();
	    sb.append(at).append(name).append(padParen(lparen)).append(ev).append(padParen(rparen));
	    stack.push(sb.toString());
	}

	@Override public void enterElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx) {
	    // '{' elementValueList? COMMA? '}'	
	}
	@Override public void exitElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rb = stack.pop();
	    String comma = ctx.COMMA() == null ? "" : stack.pop();    // TODO: keep this? there is no need for it.
	    String evl = ctx.elementValueList() == null ? "" : stack.pop();
	    String lb = stack.pop();
	    sb.append(lb).append(evl).append(comma).append(rb);
	    stack.push(sb.toString());
	}

	@Override public void enterConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx) { 
	    // Expression
	}
	@Override public void exitConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx) { 
	    // nothing to do here, expression should already be on the stack.
	}

	@Override public void enterForStatement(@NotNull Java8Parser.ForStatementContext ctx) {
	    // BasicForStatement
	    // EnhancedForStatement
	}
	@Override public void exitForStatement(@NotNull Java8Parser.ForStatementContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterTypeArgumentList(@NotNull Java8Parser.TypeArgumentListContext ctx) { 
	    // TypeArgument {, TypeArgument}
	}
	@Override public void exitTypeArgumentList(@NotNull Java8Parser.TypeArgumentListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.typeArgument() != null) {
	        sb.append(reverse(ctx.typeArgument().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterPostDecrementExpression(@NotNull Java8Parser.PostDecrementExpressionContext ctx) { 
	    // PostfixExpression --	
	}
	@Override public void exitPostDecrementExpression(@NotNull Java8Parser.PostDecrementExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String minus = stack.pop();
	    String expr = stack.pop();
	    sb.append(expr).append(minus);
	    stack.push(sb.toString());
	}

	@Override public void enterReceiverParameter(@NotNull Java8Parser.ReceiverParameterContext ctx) { 
	    // {Annotation} UnannType [Identifier .] this
	}
	@Override public void exitReceiverParameter(@NotNull Java8Parser.ReceiverParameterContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String t = stack.pop();
	    String dot = "";
	    String identifier = "";
	    if (ctx.Identifier() != null) {
	        dot = stack.pop();
	        identifier = " " + stack.pop();
	    }
	    String type = stack.pop();
	    String annotations = "";
	    if (ctx.annotation() != null) {
	        annotations = reverse(ctx.annotation().size(), " ");
	    }
	    sb.append(annotations).append(type).append(identifier).append(dot).append(t);
	    stack.push(sb.toString());
	}

	@Override public void enterWhileStatementNoShortIf(@NotNull Java8Parser.WhileStatementNoShortIfContext ctx) { 
	    // while ( Expression ) StatementNoShortIf
	}
	@Override public void exitWhileStatementNoShortIf(@NotNull Java8Parser.WhileStatementNoShortIfContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    stmt = trimEnd(stmt);
	    if (!stmt.startsWith("{")) {
	        ++tabCount;
	        stmt = "{\n" + indent(stmt) + "}";
	        --tabCount;
	    }
	    String rparen = stack.pop();
	    String expr = stack.pop();
	    String lparen = stack.pop();
	    String w = stack.pop();
	    sb.append(w).append(' ').append(padParen(lparen)).append(expr).append(padParen(rparen));
	    if (!";".equals(stmt)) {
	        sb.append(' ');
	    }
	    sb.append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterEnumConstantList(@NotNull Java8Parser.EnumConstantListContext ctx) {
	    // EnumConstant {, EnumConstant}
	}
	@Override public void exitEnumConstantList(@NotNull Java8Parser.EnumConstantListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.enumConstant() != null) {
	        sb.append(reverse(ctx.enumConstant().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterDoStatement(@NotNull Java8Parser.DoStatementContext ctx) { 
	    // 'do' statement 'while' '(' expression ')' ';'
	}
	@Override public void exitDoStatement(@NotNull Java8Parser.DoStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String rparen = stack.pop();
	    String expr = stack.pop();
	    String lparen = stack.pop();
	    String while_ = stack.pop();
	    String statement = stack.pop();
	    statement = trimEnd(statement);
	    if (!statement.startsWith("{")) {
	        ++tabCount;
	        statement = (brokenBracket ? "\n" : "") + "{\n" + indent(statement) + "}";
	        --tabCount;
	    }
	    String do_ = stack.pop();
	    sb.append(do_).append(' ').append(statement).append(breakElse ? '\n' : ' ').append(while_).append(' ').append(padParen(lparen)).append(expr).append(padParen(rparen)).append(semi);
	    stack.push(sb.toString());
	}

	@Override public void enterCatchType(@NotNull Java8Parser.CatchTypeContext ctx) {
	    // unannClassType ('|' classType)*
	}
	@Override public void exitCatchType(@NotNull Java8Parser.CatchTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String ct = "";
	    if (ctx.classType() != null && ctx.classType().size() > 0) {
	        ct = reverse(ctx.classType().size() * 2 - 1, " ");
	    }
	    String type = stack.pop();
	    sb.append(type);
	    if (!ct.isEmpty()) {
	        sb.append(' ').append(ct);
	    }
	    stack.push(sb.toString()); 
	}

	@Override public void enterForStatementNoShortIf(@NotNull Java8Parser.ForStatementNoShortIfContext ctx) { 
	    // BasicForStatementNoShortIf
	    // EnhancedForStatementNoShortIf
	}
	@Override public void exitForStatementNoShortIf(@NotNull Java8Parser.ForStatementNoShortIfContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) { 
	    // if ( Expression ) statementNoShortIf else Statement
	}
	@Override public void exitIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop().trim();
	    if (!stmt.startsWith("{") && !stmt.startsWith("if")) {
	        ++tabCount;
	        stmt = "{\n" + indent(stmt) + "}";
	        --tabCount;
	    }
	    String else_ = stack.pop().trim();
	    String sn = stack.pop().trim();
	    if (!sn.startsWith("{")) {
	        ++tabCount;
	        sn = "{\n" + indent(sn) + "}";
	        --tabCount;
	    }
	    String rparen = stack.pop();
	    String expr = stack.pop();
	    String lparen = stack.pop();
	    String if_ = stack.pop();
	    sb.append(if_).append(' ').append(padParen(lparen)).append(expr).append(padParen(rparen)).append(brokenBracket ? '\n' : ' ').append(sn);
	    trimEnd(sb);
	    sb.append(breakElse ? '\n' : ' ').append(else_).append(brokenBracket ? '\n' : ' ').append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterAssignmentOperator(@NotNull Java8Parser.AssignmentOperatorContext ctx) {
	    // (one of) =  *=  /=  %=  +=  -=  <<=  >>=  >>>=  &=  ^=  |=
	}
	@Override public void exitAssignmentOperator(@NotNull Java8Parser.AssignmentOperatorContext ctx) {
	    stack.pop();
	    stack.push(padOperator(ctx.getText()));
	}

	@Override public void enterLabeledStatementNoShortIf(@NotNull Java8Parser.LabeledStatementNoShortIfContext ctx) { 
	    // Identifier ':' statementNoShortIf
	}
	@Override public void exitLabeledStatementNoShortIf(@NotNull Java8Parser.LabeledStatementNoShortIfContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    String colon = stack.pop();
	    String identifier = stack.pop();
	    sb.append(identifier).append(colon).append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterCatches(@NotNull Java8Parser.CatchesContext ctx) { 
	    // CatchClause {CatchClause}	
	}
	@Override public void exitCatches(@NotNull Java8Parser.CatchesContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.catchClause() != null) {
	        sb.append(reverse(ctx.catchClause().size(), " "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterMethodReference_lfno_primary(@NotNull Java8Parser.MethodReference_lfno_primaryContext ctx) {
	    // :	expressionName '::' typeArguments? Identifier
	    // |	referenceType '::' typeArguments? Identifier
	    // |	'super' '::' typeArguments? Identifier
	    // |	typeName '.' 'super' '::' typeArguments? Identifier
	    // |	classType '::' typeArguments? 'new'
	    // |	arrayType '::' 'new'
	
	}
	@Override public void exitMethodReference_lfno_primary(@NotNull Java8Parser.MethodReference_lfno_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.expressionName() != null || ctx.referenceType() != null) {
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String colon = stack.pop();
	        String expr = stack.pop();
	        sb.append(expr).append(padOperator(colon)).append(typeArgs).append(identifier);
	    }
	    else if (ctx.typeName() != null) {
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String colon = stack.pop();
	        String sup = stack.pop();
	        String dot = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot).append(sup).append(padOperator(colon)).append(typeArgs).append(identifier);
	    }
	    else if (ctx.classType() != null) {
	        String n = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String colon = stack.pop();
	        String type = stack.pop();
	        sb.append(type).append(padOperator(colon)).append(typeArgs).append(n);
	    }
	    else if (ctx.arrayType() != null) {
	        String n = stack.pop();
	        String colon = stack.pop();
	        String type = stack.pop();
	        sb.append(type).append(padOperator(colon)).append(n);
	    }
	    else {
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String colon = stack.pop();
	        String sup = stack.pop();
	        sb.append(sup).append(padOperator(colon)).append(typeArgs).append(identifier);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterVariableDeclaratorList(@NotNull Java8Parser.VariableDeclaratorListContext ctx) { 
	    // variableDeclarator (',' variableDeclarator)*
	}
	@Override public void exitVariableDeclaratorList(@NotNull Java8Parser.VariableDeclaratorListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.variableDeclarator() != null && ctx.variableDeclarator().size() > 0) {
	        sb.append(reverse(ctx.variableDeclarator().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx) {
	    // (one of) Annotation final
	}
	@Override public void exitVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterCastExpression(@NotNull Java8Parser.CastExpressionContext ctx) { 
	    // '(' primitiveType ')' unaryExpression
	    // '(' referenceType additionalBound* ')' unaryExpressionNotPlusMinus
	    // '(' referenceType additionalBound* ')' lambdaExpression                
	}
	@Override public void exitCastExpression(@NotNull Java8Parser.CastExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.unaryExpression() != null) {
	        // first choice
	        String expr = stack.pop().trim();
	        String rparen = stack.pop();
	        String type = stack.pop();
	        String lparen = stack.pop();
	        sb.append(padParen(lparen)).append(type).append(padParen(rparen)).append(expr);
	    }
	    else {
	        // 2nd and 3rd choices can be handled the same
	        String expr = stack.pop().trim();
	        String rparen = stack.pop();
	        String bounds = "";
	        if (ctx.additionalBound() != null && ctx.additionalBound().size() > 0) {
	            bounds = reverse(ctx.additionalBound().size(), " ");
	        }
	        String type = stack.pop();
	        String lparen = stack.pop();
	        sb.append(padParen(lparen)).append(type);
	        if (!bounds.isEmpty()) {
	            sb.append(' ').append(bounds);
	        }
	        sb.append(padParen(rparen));
	        sb.append(expr);
	    }
	    
	    stack.push(sb.toString());
	}

	@Override public void enterConditionalOrExpression(@NotNull Java8Parser.ConditionalOrExpressionContext ctx) { 
	    // ConditionalAndExpression 
	    // ConditionalOrExpression || ConditionalAndExpression	
	}
	@Override public void exitConditionalOrExpression(@NotNull Java8Parser.ConditionalOrExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
	    if (ctx.conditionalOrExpression() != null) {
	        String or = stack.pop();
	        sb.append(stack.pop()).append(padOperator(or));    
	    }
	    sb.append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeParameterModifier(@NotNull Java8Parser.TypeParameterModifierContext ctx) { 
	    // Annotation
	}
	@Override public void exitTypeParameterModifier(@NotNull Java8Parser.TypeParameterModifierContext ctx) { 
	    // if there is an annotation, it will already be on the stack
	}

	@Override public void enterElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx) {
	    // Identifier = ElementValue	
	}
	@Override public void exitElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String value = stack.pop();
	    String eq = stack.pop();
	    String identifier = stack.pop();
	    sb.append(identifier).append(padOperator(eq)).append(value);
	    stack.push(sb.toString());
	}

	@Override public void enterFloatingPointType(@NotNull Java8Parser.FloatingPointTypeContext ctx) { 
	    // (one of) float double
	}
	@Override public void exitFloatingPointType(@NotNull Java8Parser.FloatingPointTypeContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterDimExpr(@NotNull Java8Parser.DimExprContext ctx) {
	    // {Annotation} [ Expression ]	
	}
	@Override public void exitDimExpr(@NotNull Java8Parser.DimExprContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rs = stack.pop();
	    String expression = ctx.expression() == null ? "" : stack.pop();
	    String ls = stack.pop();
	    if (ctx.annotation() != null && ctx.annotation().size() > 0) {
	        sb.append(reverse(ctx.annotation().size(), " ")).append(' ');
	    }
	    sb.append(ls).append(expression).append(rs);
	    stack.push(sb.toString());
	}

	@Override public void enterUnannInterfaceType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) {
	    // unannClassType_lf_unannClassOrInterfaceType
	}
	@Override public void exitUnannInterfaceType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterResource(@NotNull Java8Parser.ResourceContext ctx) {
	    // {VariableModifier} UnannType VariableDeclaratorId = Expression	
	}
	@Override public void exitResource(@NotNull Java8Parser.ResourceContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop().trim();
	    String eq = stack.pop().trim();
	    String vdi = stack.pop().trim();
	    String type = stack.pop().trim();
	    if (ctx.variableModifier() != null && ctx.variableModifier().size() > 0) {
	        sb.append(reverse(ctx.variableModifier().size(), " ")).append(' ');
	    }
	    sb.append(type).append(' ').append(vdi).append(' ').append(eq).append(' ').append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterInclusiveOrExpression(@NotNull Java8Parser.InclusiveOrExpressionContext ctx) {
	    // ExclusiveOrExpression 
	    // InclusiveOrExpression | ExclusiveOrExpression	
	}
	@Override public void exitInclusiveOrExpression(@NotNull Java8Parser.InclusiveOrExpressionContext ctx) { 
	    
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
        if (ctx.inclusiveOrExpression() != null) {
            String or = stack.pop();
            sb.append(stack.pop()).append(padOperator(or));            
        }
        sb.append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceMethodModifier(@NotNull Java8Parser.InterfaceMethodModifierContext ctx) { 
	    // (one of) Annotation public 
	    // abstract default static strictfp	
	}
	@Override public void exitInterfaceMethodModifier(@NotNull Java8Parser.InterfaceMethodModifierContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx) {
	    // '(' resourceList SEMI? ')'	
	}
	@Override public void exitResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rparen = stack.pop();
	    String semi = ctx.SEMI() == null ? "" : stack.pop();    
	    String resources = stack.pop();
	    String lparen = stack.pop();
	    if (resources.indexOf(';') > -1) {
            resources = resources.replaceAll(";", ";\n");
            String[] lines = resources.split("\n");
	        StringBuilder r = new StringBuilder();
	        for (String line : lines) {
	            r.append(line.trim()).append('\n');    
	        }
	        resources = r.toString();
	        ++tabCount;
	        resources = trimEnd(indent(resources.trim()));
	        --tabCount;
    	    sb.append(lparen).append('\n').append(resources).append('\n').append(rparen);            
	    }
	    else {
	        sb.append(padParen(lparen)).append(resources).append(semi).append(padParen(rparen));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceTypeList(@NotNull Java8Parser.InterfaceTypeListContext ctx) {
	    // InterfaceType {, InterfaceType}
	}
	@Override public void exitInterfaceTypeList(@NotNull Java8Parser.InterfaceTypeListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.interfaceType() != null) {
	        sb.append(reverse(ctx.interfaceType().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) { 
	    // if ( Expression ) StatementNoShortIf else StatementNoShortIf    
	}
	@Override public void exitIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String elseStmt = stack.pop().trim();
	    if (!elseStmt.startsWith("{") && !elseStmt.startsWith("if")) {
	        ++tabCount;
	        elseStmt = (brokenBracket ? "\n" : "") + "{\n" + indent(elseStmt) + "}";
	        --tabCount;
	    }
	    String else_ = stack.pop();
	    String ifStmt = stack.pop().trim();
	    if (!ifStmt.startsWith("{")) {
	        ++tabCount;
	        ifStmt = (brokenBracket ? "\n" : "") + "{\n" + indent(ifStmt) + "}";
	        ifStmt = trimEnd(ifStmt);
	        --tabCount;
	    }
	    String rparen = stack.pop();
	    String expr = stack.pop();
	    String lparen = stack.pop();
	    String if_ = stack.pop();
	    sb.append(if_).append(' ').append(padParen(lparen)).append(expr).append(padParen(rparen));
	    sb.append(brokenBracket ? '\n' : ' ').append(ifStmt);
	    sb.append(breakElse ? '\n' : ' ').append(else_).append(brokenBracket ? '\n' : ' ').append(elseStmt).append(elseStmt.endsWith("\n") ? "" : "\n");
	    stack.push(sb.toString());
	}

	@Override public void enterUnannInterfaceType(@NotNull Java8Parser.UnannInterfaceTypeContext ctx) { 
	    // UnannClassType
	}
	@Override public void exitUnannInterfaceType(@NotNull Java8Parser.UnannInterfaceTypeContext ctx) { 
	    // nothing to do here, class type should already be on the stack.
	}

	@Override public void enterInterfaceModifiers(@NotNull Java8Parser.InterfaceModifiersContext ctx) { 
	    // interfacemodifier*
	}
	
	@Override public void exitInterfaceModifiers(@NotNull Java8Parser.InterfaceModifiersContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.interfaceModifier() != null && ctx.interfaceModifier().size() > 0) {
	        sb.append(formatModifiers(ctx.interfaceModifier().size()));
	    }
	    if (sb.length() > 0) {
            trimEnd(sb);
            stack.push(sb.toString());
        }
	}
	
	@Override public void enterInterfaceModifier(@NotNull Java8Parser.InterfaceModifierContext ctx) {
	    // (one of) Annotation public protected private 
	    // abstract static strictfp	
	}
	@Override public void exitInterfaceModifier(@NotNull Java8Parser.InterfaceModifierContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterExclusiveOrExpression(@NotNull Java8Parser.ExclusiveOrExpressionContext ctx) {
	    // AndExpression 
	    // ExclusiveOrExpression ^ AndExpression
	}
	@Override public void exitExclusiveOrExpression(@NotNull Java8Parser.ExclusiveOrExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
        if (ctx.exclusiveOrExpression() != null) {
            String hat = stack.pop();
            sb.append(stack.pop()).append(padOperator(hat));            
        }
        sb.append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterClassType(@NotNull Java8Parser.ClassTypeContext ctx) { 
	    //                          annotationIdentifier typeArguments?
	    // classOrInterfaceType '.' annotationIdentifier typeArguments?
	}
	@Override public void exitClassType(@NotNull Java8Parser.ClassTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    // same ending
        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop();
        String identifier = stack.pop();
	    
        // 2nd choice
	    if (ctx.classOrInterfaceType() != null) {
	        String dot = stack.pop();
	        String type = stack.pop();
	        sb.append(type).append(dot);
	    }
	    
	    // append ending
        sb.append(identifier);
        if (!typeArguments.isEmpty()) {
            sb.append(' ').append(typeArguments);    
        }
	    stack.push(sb.toString());
	}

	@Override public void enterPostIncrementExpression(@NotNull Java8Parser.PostIncrementExpressionContext ctx) {
	    // PostfixExpression ++	
	}
	@Override public void exitPostIncrementExpression(@NotNull Java8Parser.PostIncrementExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String plus = stack.pop().trim();
	    sb.append(stack.pop());    // postfix expression
	    sb.append(plus);
	    stack.push(sb.toString());
	}

	@Override public void enterTryStatement(@NotNull Java8Parser.TryStatementContext ctx) {
	    // try Block Catches 
	    // try Block [Catches] Finally 
	    // TryWithResourcesStatement	
	}
	@Override public void exitTryStatement(@NotNull Java8Parser.TryStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.tryWithResourcesStatement() != null) {
	        // third option   
	        sb.append(stack.pop());
	    }
	    else if (ctx.finally_() != null) {
	        // second option
	        String finally_ = stack.pop();
	        finally_ = trimFront(finally_);
            String catches = "";
	        if (ctx.catches() != null) {
	            catches = stack.pop().trim();
	        }
	        String block = stack.pop().trim();
            if (bracketStyle == BROKEN) {
                block = new StringBuilder("\n").append(block).toString();
            }
	        String try_ = stack.pop();
	        if (!try_.startsWith("\n")) {
	            try_ = new StringBuilder("\n").append(try_).toString();
	        }
	        sb.append(try_).append(' ').append(block).append(breakElse ? '\n' : ' ').append(catches).append(breakElse ? '\n' : ' ').append(finally_);
	    }
	    else {
	        // first option
	        String catches = stack.pop();
	        catches = trimFront(catches);
	        String block = stack.pop().trim();
            if (bracketStyle == BROKEN) {
                block = new StringBuilder("\n").append(block).toString();
            }
	        String try_ = stack.pop();
	        if (!try_.startsWith("\n")) {
	            try_ = new StringBuilder("\n").append(try_).toString();
	        }
	        sb.append(try_).append(' ').append(block).append(breakElse ? '\n' : ' ').append(catches);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterAnnotationIdentifier(@NotNull Java8Parser.AnnotationIdentifierContext ctx) { 
	    // annotation* Identifier
	}
	@Override public void exitAnnotationIdentifier(@NotNull Java8Parser.AnnotationIdentifierContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    String annotations = "";
	    if (ctx.annotation() != null && ctx.annotation().size() > 0) {
	        annotations = reverse(ctx.annotation().size(), " ");
	    }
	    sb.append(annotations).append(identifier);
	    stack.push(sb.toString());
	}
	
	
	@Override public void enterElementValueList(@NotNull Java8Parser.ElementValueListContext ctx) {
	    // ElementValue {, ElementValue}
	}
	@Override public void exitElementValueList(@NotNull Java8Parser.ElementValueListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.elementValue() != null) {
	        sb.append(reverse(ctx.elementValue().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterBasicForStatementNoShortIf(@NotNull Java8Parser.BasicForStatementNoShortIfContext ctx) { 
	    // for ( [ForInit] ; [Expression] ; [ForUpdate] ) StatementNoShortIf
	}
	@Override public void exitBasicForStatementNoShortIf(@NotNull Java8Parser.BasicForStatementNoShortIfContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String statement = stack.pop();
	    statement = trimEnd(statement);
	    statement = trimEnd(statement);
	    if (!statement.startsWith("{")) {
	        ++tabCount;
	        statement = "{\n" + indent(statement) + "}";
	        --tabCount;
	    }
	    if (brokenBracket) {
	        statement = new StringBuilder("\n").append(statement).toString();   
	    }
	    String rparen = stack.pop();
	    String forUpdate = ctx.forUpdate() == null ? "" : stack.pop();
	    String semi2 = stack.pop();
	    String expression = ctx.expression() == null ? "" : stack.pop();
	    String semi1 = stack.pop();
	    String forInit = ctx.forInit() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String for_ = stack.pop();
	    sb.append(for_).append(' ').append(padParen(lparen)).append(forInit).append(semi1);
	    sb.append(' ').append(expression).append(semi2).append(' ').append(forUpdate).append(padParen(rparen)).append(' ').append(statement);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx) {
	    // ClassDeclaration
	    // InterfaceDeclaration
	    // ;
	}
	@Override public void exitTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx) { 
	    if (";".equals(stack.peek())) {
	        String semi = stack.pop() + '\n';
	        stack.push(semi);
	    }
    }

	@Override public void enterSquareBrackets(@NotNull Java8Parser.SquareBracketsContext ctx) { 
	    //  '[]'    
	}

	@Override public void exitSquareBrackets(@NotNull Java8Parser.SquareBracketsContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}
	
	@Override public void enterSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) {
	    // 'switch' '(' expression ')' switchBlock
	}
	@Override public void exitSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String switchBlock = stack.pop();
	    String rparen = stack.pop().trim();
	    String expression = stack.pop();
	    String lparen = stack.pop();
	    String switch_ = stack.pop();
	    sb.append(switch_).append(' ').append(padParen(lparen)).append(expression).append(padParen(rparen)).append(' ').append(switchBlock);
	    stack.push(sb.toString());
	}

	@Override public void enterWildcard(@NotNull Java8Parser.WildcardContext ctx) { 
	    // {Annotation} ? [WildcardBounds]
	}
	@Override public void exitWildcard(@NotNull Java8Parser.WildcardContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String wildcardBounds = ctx.wildcardBounds() == null ? "" : ' ' + stack.pop();
	    String q = stack.pop();
	    String annotations = "";
	    if (ctx.annotation() != null) {
	        annotations = reverse(ctx.annotation().size(), " ");
	    }
	    sb.append(annotations).append(padOperator(q)).append(wildcardBounds);
	    stack.push(sb.toString());
	}

	@Override public void enterClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx) { 
	    // NormalClassDeclaration
	    // EnumDeclaration
	}
	@Override public void exitClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnaryExpressionNotPlusMinus(@NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx) {
	    // PostfixExpression 
	    // ~ UnaryExpression 
	    // ! UnaryExpression 
	    // CastExpression	
	}
	@Override public void exitUnaryExpressionNotPlusMinus(@NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String expression = stack.pop();
	    if (ctx.unaryExpression() != null) {
	        sb.append(stack.pop());
	    }
	    sb.append(expression);
	    stack.push(sb.toString());
	}

	@Override public void enterUnannReferenceType(@NotNull Java8Parser.UnannReferenceTypeContext ctx) {
	    // UnannClassOrInterfaceType
	    // UnannTypeVariable
	    // UnannArrayType
	}
	@Override public void exitUnannReferenceType(@NotNull Java8Parser.UnannReferenceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterMethodHeader(@NotNull Java8Parser.MethodHeaderContext ctx) { 
	    //                             Result MethodDeclarator [Throws]
	    // TypeParameters {Annotation} Result MethodDeclarator [throws]
	}
	@Override public void exitMethodHeader(@NotNull Java8Parser.MethodHeaderContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    // common ending
        String throws_ = ctx.throws_() == null ? "" : stack.pop();
        String methodDeclarator = stack.pop();
        String result = stack.pop();
        
        // second option 
        if (ctx.typeParameters() != null) {
            String annotations = "";
            if (ctx.annotation() != null) {
                annotations = reverse(ctx.annotation().size(), " ");
            }
            String typeParameters = stack.pop().trim();
            sb.append(typeParameters).append(' ');
            sb.append(annotations);
        }
        sb.append(result).append(' ').append(methodDeclarator);
        if (!throws_.isEmpty()) {
            sb.append(' ').append(throws_);    
        }
	    stack.push(sb.toString());
	}

	@Override public void enterCatchFormalParameter(@NotNull Java8Parser.CatchFormalParameterContext ctx) {
	    // {VariableModifier} CatchType VariableDeclaratorId	
	}
	@Override public void exitCatchFormalParameter(@NotNull Java8Parser.CatchFormalParameterContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String vdi = stack.pop();
	    String type = stack.pop();
	    String vm = "";
	    if (ctx.variableModifier() != null && ctx.variableModifier().size() > 0) {
	        vm = reverse(ctx.variableModifier().size(), " ");
	    }
	    if (!vm.isEmpty()) {
	        sb.append(vm).append(' ');    
	    }
	    sb.append(type).append(' ').append(vdi);
	    stack.push(sb.toString());
	}

	@Override public void enterEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx) { 
	    // {EnumConstantModifier} Identifier [( [ArgumentList] )] [ClassBody]
	}
	@Override public void exitEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String body = ctx.classBody() == null ? "" : stack.pop();
	    StringBuilder args = new StringBuilder();
	    if (ctx.argumentList() != null) {
	        String rparen = stack.pop();
	        String list = stack.pop();
	        String lparen = stack.pop();
	        args.append(padParen(lparen, list)).append(list).append(padParen(rparen, list));
	    }
	    String identifier = stack.pop();
	    if (ctx.enumConstantModifier() != null) {
	        sb.append(reverse(ctx.enumConstantModifier().size(), " ")).append(' ');
	    }
	    sb.append(identifier).append(args);
	    if (!body.isEmpty()) {
	        sb.append(' ').append(body);    
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
	    // :	methodName '(' argumentList? ')'
	    // |	typeName '.' typeArguments? Identifier '(' argumentList? ')'
	    // |	expressionName '.' typeArguments? Identifier '(' argumentList? ')'
	    // |	'super' '.' typeArguments? Identifier '(' argumentList? ')'
	    // |	typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
	}
	@Override public void exitMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    // common ending
	    String rparen = stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    if (argumentList.indexOf('\n') > -1) {
	        // double indent if argumentList spans more than one line
	        argumentList = trimEnd(indentAgain(indentAgain(argumentList)));
	    }
	    String lparen = stack.pop();
	    
	    // method name
	    if (ctx.methodName() != null) {
	        sb.append(stack.pop());    
	    }
	    else if (ctx.expressionName() != null) {
	        // expression name
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String dot = stack.pop();
	        String exprName = stack.pop();
	        sb.append(exprName).append(dot).append(typeArgs).append(identifier);
	    }
	    else if (ctx.typeName() != null) {
	        // 2 type names
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String dot1 = stack.pop();
	        String dot2 = "";
	        String super_ = "";
	        if (ctx.SUPER() != null) {
	            super_ = stack.pop();
	            dot2 = stack.pop();
	        }
	        String typeName = stack.pop();
	        sb.append(typeName).append(dot2).append(super_).append(dot1).append(typeArgs).append(identifier);
	    }
	    else {
	        // super
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null || ctx.typeArguments().isEmpty() ? "" : stack.pop() + ' ';
	        String dot = stack.pop();
	        String super_ = stack.pop();
	        sb.append(super_).append(dot).append(typeArgs).append(identifier);
	    }
	    
	    // common ending
	    sb.append(padParen(lparen, argumentList)).append(argumentList).append(padParen(rparen, argumentList));
	    stack.push(sb.toString());
	}

	@Override public void enterPackageName(@NotNull Java8Parser.PackageNameContext ctx) { 
	    // Identifier
	    // PackageName . Identifier
	}
	@Override public void exitPackageName(@NotNull Java8Parser.PackageNameContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    if (ctx.packageName() != null) {
	        String dot = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot);
	    }
	    sb.append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx) {
	    // SingleTypeImportDeclaration
	    // TypeImportOnDemandDeclaration
	    // SingleStaticImportDeclaration
	    // StaticImportOnDemandDeclaration
	}
	@Override public void exitImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx) {
	    // {Annotation} NumericType
	    // {Annotation} boolean
	}
	@Override public void exitPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String type = stack.pop();
        String annotations = "";
        if (ctx.annotation() != null) {
            annotations = reverse(ctx.annotation().size(), " ");
        }
        sb.append(annotations).append(type);
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx) { 
	    // NormalInterfaceDeclaration 
	    // AnnotationTypeDeclaration	
	}
	@Override public void exitInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx) {
	    // LocalVariableDeclaration ;
	}
	@Override public void exitLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String decl = stack.pop();
	    if (!decl.isEmpty()) {
	        sb.append(decl);
	        trimEnd(sb);
	        sb.append(semi);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx) {
	    // LocalVariableDeclarationStatement
	    // ClassDeclaration
	    // Statement
	}
	@Override public void exitBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx) {
	}

	@Override public void enterClassType_lf_classOrInterfaceType(@NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) { 
	    // '.' annotationIdentifier typeArguments?
	}
	@Override public void exitClassType_lf_classOrInterfaceType(@NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop();
	    String ai = stack.pop();
	    String dot = stack.pop();
	    sb.append(dot).append(ai);
	    if (!typeArgs.isEmpty()) {
	        sb.append(' ').append(typeArgs);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterPackageOrTypeName(@NotNull Java8Parser.PackageOrTypeNameContext ctx) {
        // Identifier 
        // PackageOrTypeName . Identifier	    
	}
	@Override public void exitPackageOrTypeName(@NotNull Java8Parser.PackageOrTypeNameContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    if (ctx.packageOrTypeName() != null) {
	        String dot = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot);
	    }
	    sb.append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterAssignment(@NotNull Java8Parser.AssignmentContext ctx) {
	    // LeftHandSide AssignmentOperator Expression	
	}
	@Override public void exitAssignment(@NotNull Java8Parser.AssignmentContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expression = stack.pop();
	    String operator = stack.pop();
	    String lhs = stack.pop();
	    sb.append(lhs).append(operator).append(expression);
	    stack.push(sb.toString());
	}

	@Override public void enterMethodName(@NotNull Java8Parser.MethodNameContext ctx) {
	    // Identifier
	}
	@Override public void exitMethodName(@NotNull Java8Parser.MethodNameContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx) {
        // Assignment 
        // PreIncrementExpression 
        // PreDecrementExpression 
        // PostIncrementExpression 
        // PostDecrementExpression 
        // MethodInvocation 
        // ClassInstanceCreationExpression	    
	}
	@Override public void exitStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterBreakStatement(@NotNull Java8Parser.BreakStatementContext ctx) {
	    // break [Identifier] ;
	}
	@Override public void exitBreakStatement(@NotNull Java8Parser.BreakStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String identifier = ctx.Identifier() == null ? "" : ' ' + stack.pop();
	    String break_ = stack.pop();
	    sb.append(break_).append(identifier).append(semi).append('\n');
	    stack.push(sb.toString());
	}

	@Override public void enterAmbiguousName(@NotNull Java8Parser.AmbiguousNameContext ctx) { 
	    // :	Identifier
	    // |	ambiguousName '.' Identifier
	}
	@Override public void exitAmbiguousName(@NotNull Java8Parser.AmbiguousNameContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    if (ctx.ambiguousName() != null) {
	        String dot = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot);
	    }
	    sb.append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterStatementExpressionList(@NotNull Java8Parser.StatementExpressionListContext ctx) { 
	    // StatementExpression {, StatementExpression}
	}
	@Override public void exitStatementExpressionList(@NotNull Java8Parser.StatementExpressionListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.statementExpression() != null) {
	        sb.append(reverse(ctx.statementExpression().size() * 2 - 1, " ", 2));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx) {
	    // interfaceMethodModifier* methodHeader methodBody	
	}
	@Override public void exitInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String methodBody = stack.pop();
	    String methodHeader = stack.pop().trim();
	    if (ctx.interfaceMethodModifier() != null && ctx.interfaceMethodModifier().size() > 0) {
	        sb.append(formatModifiers(ctx.interfaceMethodModifier().size()));
	        trimEnd(sb);
	        sb.append(' ');
	    }
	    sb.append(methodHeader).append(methodBody);
	    stack.push(sb.toString());
	}

	@Override public void enterThrows_(@NotNull Java8Parser.Throws_Context ctx) { 
	    // 'throws' exceptionTypeList
	}
	@Override public void exitThrows_(@NotNull Java8Parser.Throws_Context ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String list = stack.pop();
	    String throws_ = stack.pop();
	    sb.append(throws_).append(' ').append(list);
	    stack.push(sb.toString());
	}

	@Override public void enterSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx) { 
        // 'case' constantExpression ':'
        // 'case' enumConstantName ':'
        // 'default' ':' 
	}
	@Override public void exitSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String colon = stack.pop();
	    if (ctx.constantExpression() != null || ctx.enumConstantName() != null) {
	        String expr = stack.pop();
	        String case_ = stack.pop();
	        sb.append(case_.trim()).append(' ').append(expr);
	    }
	    else {
	        String default_ = stack.pop().trim();
	        sb.append(default_);   
	    }
	    sb.append(colon).append('\n');
	    stack.push(sb.toString());
	}

	@Override public void enterMethodReference(@NotNull Java8Parser.MethodReferenceContext ctx) {
        // :	expressionName '::' typeArguments? Identifier
        // |	referenceType '::' typeArguments? Identifier
        // |	primary '::' typeArguments? Identifier
        // |	SUPER '::' typeArguments? Identifier
        // |	typeName '.' SUPER '::' typeArguments? Identifier
        // |	classType '::' typeArguments? 'new'
        // |	arrayType '::' 'new'                               	    
	}
	@Override public void exitMethodReference(@NotNull Java8Parser.MethodReferenceContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.expressionName() != null || ctx.referenceType() != null || ctx.primary() != null ) {
	        String identifier = stack.pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String colon = stack.pop();
	        String expressionName = stack.pop();
	        sb.append(expressionName).append(padOperator(colon)).append(typeArguments).append(identifier);
	    }
	    else if (ctx.typeName() != null) {
	        String identifier = stack.pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String colon = stack.pop();
            String super_ = stack.pop();	        
	        String typeName = stack.pop();
	        sb.append(typeName).append(super_).append(padOperator(colon)).append(typeArguments).append(' ').append(identifier);    
	    }
	    else if (ctx.classType() != null) {
	        String new_ = stack.pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String colon = stack.pop();
	        String type = stack.pop();
	        sb.append(type).append(padOperator(colon)).append(typeArguments).append(' ').append(new_);    
	    }
	    else if (ctx.arrayType() != null) {
	        String new_ = stack.pop();
	        String colon = stack.pop();
	        String type = stack.pop();
	        sb.append(type).append(padOperator(colon)).append(new_);    
	    }
	    else {
	        String identifier = stack.pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String colon = stack.pop();
            String super_ = stack.pop();	        
	        sb.append(super_).append(padOperator(colon)).append(typeArguments).append(' ').append(identifier);    
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray(@NotNull Java8Parser.PrimaryNoNewArrayContext ctx) { 
        // :	literal
        // |	typeName (squareBrackets)* '.' 'class'
        // |	'void' '.' 'class'
        // |	'this'
        // |	typeName '.' 'this'
        // |	'(' expression ')'
        // |	classInstanceCreationExpression
        // |	fieldAccess
        // |	arrayAccess
        // |	methodInvocation
        // |	methodReference
	}
	@Override public void exitPrimaryNoNewArray(@NotNull Java8Parser.PrimaryNoNewArrayContext ctx) { 
	    if (ctx.literal() != null || ctx.classInstanceCreationExpression() != null ||
	        ctx.fieldAccess() != null || ctx.arrayAccess() != null || 
	        ctx.methodInvocation() != null || ctx.methodReference() != null ) {
	        // one of these choices will already be on the stack
	        return;
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    
	    // expression
	    if (ctx.expression() != null) {
	        String rparen = stack.pop();
	        String expression = stack.pop();
	        String lparen = stack.pop();
	        sb.append(padParen(lparen)).append(expression).append(padParen(rparen));
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 2 typeName choices
	    String raw = ctx.getText();
	    if (ctx.typeName() != null) {
	        if (raw.indexOf("class") > -1) {
	            // first typeName choice
	            String class_ = stack.pop();
	            String dot = stack.pop();
	            StringBuilder brackets = new StringBuilder();
	            if (ctx.squareBrackets() != null && ctx.squareBrackets().size() > 0) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append(stack.pop());
	                }
	            }
	            String name = stack.pop();
	            sb.append(name).append(brackets).append(dot).append(class_);
	        }
	        else {
	            // second typeName choice
	            String this_ = stack.pop();
	            String dot = stack.pop();
	            String name = stack.pop();
	            sb.append(name).append(dot).append(this_);
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 'void' and 'this' choice
	    if (raw.indexOf("void") > -1) {
	        String class_ = stack.pop();
	        String dot = stack.pop();
	        String void_ = stack.pop();
	        sb.append(void_).append(dot).append(class_);
	    }
	    else {
	        sb.append(stack.pop());    // 'this' choice   
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterNumericType(@NotNull Java8Parser.NumericTypeContext ctx) {
	    // IntegerType
	    // FloatingPointType
	}
	@Override public void exitNumericType(@NotNull Java8Parser.NumericTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterContinueStatement(@NotNull Java8Parser.ContinueStatementContext ctx) { 
	    // continue [Identifier] ;
	}
	@Override public void exitContinueStatement(@NotNull Java8Parser.ContinueStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String identifier = ctx.Identifier() == null ? "" : stack.pop();
	    String continue_ = stack.pop();
	    sb.append(continue_).append(identifier).append(semi).append('\n');
	    stack.push(sb.toString());
	}


	@Override public void enterEveryRule(@NotNull ParserRuleContext ctx) {
	}
	
	@Override public void exitEveryRule(@NotNull ParserRuleContext ctx) { 
	}

	@Override public void visitTerminal(@NotNull TerminalNode node) { 
        String terminalText = node.getText();
        stack.push(terminalText);
        processComments(node);
        processWhitespace(node);
	}
	
	@Override public void visitErrorNode(@NotNull ErrorNode node) { 
	}
//}}}	
}