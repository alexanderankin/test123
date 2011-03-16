
package beauty.beautifiers;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;
import beauty.BeautyPlugin;
import beauty.PrivilegedAccessor;
import static beauty.beautifiers.Constants.*;

/**
 * This is a default beautifier to use when there is no specific beautifier
 * available.  This can make for a pretty decent beautifier if configured
 * correctly.
 */
public class DefaultBeautifier extends Beautifier {
 
    private String modeName = null;
 
    private boolean prePadOperator = false;
    private boolean prePadFunction = false;
    private boolean prePadDigit = false;
    private boolean prePadKeyword1 = false;
    private boolean prePadKeyword2 = false;
    private boolean prePadKeyword3 = false;
    private boolean prePadKeyword4 = false;
    private boolean postPadOperator = false;
    private boolean postPadFunction = false;
    private boolean postPadDigit = false;
    private boolean postPadKeyword1 = false;
    private boolean postPadKeyword2 = false;
    private boolean postPadKeyword3 = false;
    private boolean postPadKeyword4 = false;
 
    private boolean labelOnSeparateLine = true;
 
    private String prePadCharacters = "";
    private String postPadCharacters = "";
    private String dontPrePadCharacters = "";
    private String dontPostPadCharacters = "";
 
    // these are comma separated strings now, not a list of characters
    private String preInsertLineCharacters = "";
    private String postInsertLineCharacters = "";
 
    private boolean collapseBlankLines = false;
 
    private boolean indentLines = false;
    private String indentOpenBrackets = "";
    private String indentCloseBrackets = "";
    private String unalignedOpenBrackets = "";
    private String unalignedCloseBrackets = ""; 
    private String indentNextLine = "";
    private String unindentThisLine = "";
    private String electricKeys = "";
 
    // this constructor is used for testing
    public DefaultBeautifier() {
 
    }
 
    public DefaultBeautifier(String modeName) {
        if (modeName == null || modeName.isEmpty()) {
            throw new IllegalArgumentException("mode name was null"); 
        }
        this.modeName = modeName;
 
        Properties props = BeautyPlugin.getCustomModeProperties(modeName);
        prePadOperator = "true".equals(props.getProperty(PRE_PAD_OPERATORS)) ? true : false;
        prePadFunction = "true".equals(props.getProperty(PRE_PAD_FUNCTIONS)) ? true : false;
        prePadDigit = "true".equals(props.getProperty(PRE_PAD_DIGITS)) ? true : false;
        prePadKeyword1 = "true".equals(props.getProperty(PRE_PAD_KEYWORDS1)) ? true : false;
        prePadKeyword2 = "true".equals(props.getProperty(PRE_PAD_KEYWORDS2)) ? true : false;
        prePadKeyword3 = "true".equals(props.getProperty(PRE_PAD_KEYWORDS3)) ? true : false;
        prePadKeyword4 = "true".equals(props.getProperty(PRE_PAD_KEYWORDS4)) ? true : false;
        postPadOperator = "true".equals(props.getProperty(POST_PAD_OPERATORS)) ? true : false;
        postPadFunction = "true".equals(props.getProperty(POST_PAD_FUNCTIONS)) ? true : false;
        postPadDigit = "true".equals(props.getProperty(POST_PAD_DIGITS)) ? true : false;
        postPadKeyword1 = "true".equals(props.getProperty(POST_PAD_KEYWORDS1)) ? true : false;
        postPadKeyword2 = "true".equals(props.getProperty(POST_PAD_KEYWORDS2)) ? true : false;
        postPadKeyword3 = "true".equals(props.getProperty(POST_PAD_KEYWORDS3)) ? true : false;
        postPadKeyword4 = "true".equals(props.getProperty(POST_PAD_KEYWORDS4)) ? true : false;
        labelOnSeparateLine = "true".equals(props.getProperty(LABEL_ON_SEPARATE_LINE)) ? true : false;
        prePadCharacters = props.getProperty(PRE_PAD_CHARACTERS) == null ? "" : props.getProperty(PRE_PAD_CHARACTERS);
        postPadCharacters = props.getProperty(POST_PAD_CHARACTERS) == null ? "" : props.getProperty(POST_PAD_CHARACTERS);
        dontPrePadCharacters = props.getProperty(DONT_PRE_PAD_CHARACTERS) == null ? "" : props.getProperty(DONT_PRE_PAD_CHARACTERS);
        dontPostPadCharacters = props.getProperty(DONT_POST_PAD_CHARACTERS) == null ? "" : props.getProperty(DONT_POST_PAD_CHARACTERS);
        preInsertLineCharacters = props.getProperty(PRE_INSERT_LINE_CHARACTERS) == null ? "" : props.getProperty(PRE_INSERT_LINE_CHARACTERS);
        postInsertLineCharacters = props.getProperty(POST_INSERT_LINE_CHARACTERS) == null ? "" : props.getProperty(POST_INSERT_LINE_CHARACTERS);
        collapseBlankLines = "true".equals(props.getProperty(COLLAPSE_BLANK_LINES)) ? true : false;
 
        indentLines = "true".equals(props.getProperty(USE_JEDIT_INDENTER)) ? true : false;
        indentOpenBrackets = props.getProperty(INDENT_OPEN_BRACKETS) == null ? "" : props.getProperty(INDENT_OPEN_BRACKETS);
        indentCloseBrackets = props.getProperty(INDENT_CLOSE_BRACKETS) == null ? "" : props.getProperty(INDENT_CLOSE_BRACKETS);
        unalignedOpenBrackets = props.getProperty(UNALIGNED_OPEN_BRACKETS) == null ? "" : props.getProperty(UNALIGNED_OPEN_BRACKETS);
        unalignedCloseBrackets = props.getProperty(UNALIGNED_CLOSE_BRACKETS) == null ? "" : props.getProperty(UNALIGNED_CLOSE_BRACKETS);
        indentNextLine = props.getProperty(INDENT_NEXT_LINE) == null ? "" : props.getProperty(INDENT_NEXT_LINE);
        unindentThisLine = props.getProperty(UNINDENT_THIS_LINE) == null ? "" : props.getProperty(UNINDENT_THIS_LINE);
        electricKeys = props.getProperty(ELECTRIC_KEYS) == null ? "" : props.getProperty(ELECTRIC_KEYS);
 
    }
 
    /**
     * @param text The text to beautify.
     */ 
    public String beautify(String text) {
        StringBuilder sb = new StringBuilder(text);
 
        // Token padding can work on the raw text
        sb = padTokens(sb);
 
        // the next few operations need to work only on text, not on comments,
        // so split out the buffer contents into text and comments.
        List<PToken> tokens = parseTokens(sb);
        for (PToken token : tokens) {
            if (token.isText) {
                String s = token.tokenText; 
                s = prePadCharacters(s);
                s = postPadCharacters(s);
                s = preInsertLineSeparators(s);
                s = postInsertLineSeparators(s);
                s = dontPrePadCharacters(s);
                s = dontPostPadCharacters(s);
                s = collapseBlankLines(s);
                token.tokenText = s;
            }
        }
 
        // convert the token list back to a string for padding keywords and indenting
        sb.setLength(0);
        for (PToken token : tokens) {
            sb.append(token.tokenText); 
        }
 
        sb = padKeywords(sb);
 
        if (indentLines) {
            sb = indentLines(sb);
        }
 
        return sb.toString();
    }
 
    /**
     * Pad the tokens found by the jEdit syntax highlighting engine. In
     * general, I found that this is pretty horrible since many of the
     * mode files do a poor job of identifying tokens.
     */ 
    StringBuilder padTokens(StringBuilder sb) {
        if (prePadFunction || postPadFunction || prePadOperator || postPadOperator || prePadDigit || postPadDigit || prePadKeyword1 || postPadKeyword1 || prePadKeyword2 || postPadKeyword2 || prePadKeyword3 || postPadKeyword3 || prePadKeyword4 || postPadKeyword4) {
            try {
                File tempFile = File.createTempFile("tmp", null);
                tempFile.deleteOnExit();
                Buffer tempBuffer = jEdit.openTemporary(jEdit.getActiveView(), null, tempFile.getAbsolutePath(), true);
                tempBuffer.setMode(jEdit.getMode(modeName));
                tempBuffer.insert(0, sb.toString());
                sb.setLength(0);
                int firstLine = 0;
                int lastLine = tempBuffer.getLineCount();
                DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
                for (int lineNum = firstLine; lineNum < lastLine; lineNum++) {
                    tokenHandler.init();
 
                    int lineStart = tempBuffer.getLineStartOffset(lineNum);
                    tempBuffer.markTokens(lineNum, tokenHandler);
                    Token token = tokenHandler.getTokens();
                    int tokenStart = lineStart;
 
                    String previousTokenText = "";
                    byte previousTokenId = Token.NULL;
                    String currentTokenText = tempBuffer.getText(tokenStart, token.length);
                    String nextTokenText = token.next != null ? tempBuffer.getText(tokenStart + token.length, token.next.length) : "";
 
                    while (token.id != Token.END) {
 
                        // maybe pad start
                        if (! previousTokenText.endsWith(" ")) {                            // NOPMD
                            if ((token.id == Token.OPERATOR && prePadOperator && previousTokenId != Token.OPERATOR) || (token.id == Token.FUNCTION && prePadFunction) || (token.id == Token.DIGIT && prePadDigit) || (token.id == Token.KEYWORD1 && prePadKeyword1) || (token.id == Token.KEYWORD2 && prePadKeyword2) || (token.id == Token.KEYWORD3 && prePadKeyword3) || (token.id == Token.KEYWORD4 && prePadKeyword4)) {                                // NOPMD
                                sb.append(' ');
                            }
                        }
 
                        // maybe add a line for a label
                        boolean onlyWhitespace = tempBuffer.getText(lineStart, tokenStart - lineStart).trim().length() > 0;
                        if (token.id == Token.LABEL && labelOnSeparateLine && onlyWhitespace) {
                            sb.append(getLineSeparator());
                        }
 
                        // definitely add text of current token
                        sb.append(currentTokenText);
 
                        // maybe pad after token
                        if (! nextTokenText.startsWith(" ")) {
                            if ((token.id == Token.OPERATOR && postPadOperator && token.next.id != Token.OPERATOR) || (token.id == Token.FUNCTION && postPadFunction) || (token.id == Token.DIGIT && postPadDigit) || (token.id == Token.KEYWORD1 && postPadKeyword1) || (token.id == Token.KEYWORD2 && postPadKeyword2) || (token.id == Token.KEYWORD3 && postPadKeyword3) || (token.id == Token.KEYWORD4 && postPadKeyword4)) {                                // NOPMD
                                sb.append(' ');
                                currentTokenText += " ";                                // NOPMD
                            }
                        }
 
                        previousTokenText = currentTokenText;
                        previousTokenId = token.id;
                        currentTokenText = nextTokenText;
                        tokenStart += token.length;
                        token = token.next;
                        if (token.next != null) {
                            nextTokenText = tempBuffer.getText(tokenStart + token.length, token.next.length);
                        }
                    }
                    if (lineNum <= lastLine - 2) {
                        sb.append(getLineSeparator());
                    }
                }
                if (sb.length() == 0) {
                    sb = new StringBuilder(tempBuffer.getText(0, tempBuffer.getLength())); 
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } 
        }
        return sb;
    }
 
    /**
     * The user may specify a list of characters to pad in front of.
     */ 
    String prePadCharacters(String s) {
        if (prePadCharacters.length() == 0) {
            return s;
        }
 
        for (int i = 0; i < prePadCharacters.length(); i++) {
            char c = prePadCharacters.charAt(i);
            s = s.replaceAll("(\\S)[" + c + "]", "$1 " + c);
        }
        return s;
    }
 
    /**
     * The user may specify a list of characters to pad after.
     */ 
    String postPadCharacters(String s) {
        if (postPadCharacters.length() == 0) {
            return s;
        }
 
        for (int i = 0; i < postPadCharacters.length(); i++) {
            char c = postPadCharacters.charAt(i);
            s = s.replaceAll("[" + (c == '[' || c == ']' ? "\\" : "") + c + "](\\S)", c + " $1");
        }
        return s;
    }
 
    /**
     * The user may specify a comma separated list of strings before which a
     * line separator will be inserted.  The strings are regular expressions.
     */ 
    String preInsertLineSeparators(String s) {
        if (preInsertLineCharacters.length() == 0) {
            return s;
        }
 
        // need to deal with commas that may be part of a regex in a comma-
        // separated list of regex's.  Find all escaped commas and replace with
        // c1f, then split on remaining commas, then revert the c1f's to normal
        // unescaped commas.
        String pilc = preInsertLineCharacters;
        pilc = pilc.replaceAll("\\\\,", "\\\\c1f");
        String[] chars = pilc.split(",");
        for (String c : chars) {
            c = c.replaceAll("\\\\c1f", ",");
            s = preInsertLineSeparators(s, c);
        }
        return s;
    }
 
    String preInsertLineSeparators(String s, String c) {
        try {
            String ls = getLineSeparator();
            String regex = "(?<!(" + getLSString() + "))(" + c + ")";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(s);
            s = m.replaceAll(ls + "$2");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
 
    /**
     * The user may specify a comma separated list of strings after which a
     * line separator will be inserted.
     */ 
    String postInsertLineSeparators(String s) {
        if (postInsertLineCharacters.length() == 0) {
            return s;
        }
 
        // need to deal with commas that may be part of a regex in a comma-
        // separated list of regex's.
        String pilc = postInsertLineCharacters;
        pilc = pilc.replaceAll("\\\\,", "\\\\c1f");
        String[] chars = pilc.split(",");
        for (String c : chars) {
            c = c.replaceAll("\\\\c1f", ",");
            s = postInsertLineSeparators(s, c);
        }
        return s;
    }
 
    String postInsertLineSeparators(String s, String c) {
        try {
            String ls = getLineSeparator();
            String regex = "(" + c + ")(?!(" + getLSString() + "))";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(s);
            s = m.replaceAll("$1" + ls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
 
    /**
     * Remove a single whitespace character from before a character.  Only
     * one whitespace character is removed.  The intent here is that a
     * character may be mis-identified as an operator token in the mode file,
     * so it gets padded as an operator.  The user can add this character to
     * the don't pad list and have that padding removed.  An example is the
     * javascript mode, where ; is defined as an operator.
     */ 
    String dontPrePadCharacters(String s) {
        if (dontPrePadCharacters.length() == 0) {
            return s;
        }
 
        if (dontPrePadCharacters.length() > 0) {
            for (int i = 0; i < dontPrePadCharacters.length(); i++) {
                char c = dontPrePadCharacters.charAt(i);
                s = s.replaceAll("\\s+[" + (c == '[' || c == ']' || c == '\\' ? "\\" : "") + c + "]", String.valueOf(c));
            }
        }
        return s;
    }
 
    /**
     * Remove a single whitespace character from after a character.  Only
     * one whitespace character is removed.  The intent here is that a
     * character may be mis-identified as an operator token in the mode file,
     * so it gets padded as an operator.  The user can add this character to
     * the don't pad list and have that padding removed.  An example is the
     * javascript mode, where ; is defined as an operator.
     */ 
    String dontPostPadCharacters(String s) {
        if (dontPostPadCharacters.length() == 0) {
            return s;
        }
 
        if (dontPostPadCharacters.length() > 0) {
            for (int i = 0; i < dontPostPadCharacters.length(); i++) {
                char c = dontPostPadCharacters.charAt(i);
                s = s.replaceAll("[" + (c == '[' || c == ']' || c == '\\' ? "\\" : "") + c + "]\\s+", String.valueOf(c));
            }
        }
        return s;
    }
 
    /**
     * Keywords get padded last rather than with padTokens.  Otherwise, the other
     * pad/dontPad methods may eliminate the keyword padding, for example,
     * the space between "for (" may be removed by some other rule even when
     * pad keywords is checked.
     */ 
    StringBuilder padKeywords(StringBuilder sb) {
        if (prePadKeyword1 || postPadKeyword1 || prePadKeyword2 || postPadKeyword2 || prePadKeyword3 || postPadKeyword3 || prePadKeyword4 || postPadKeyword4) {
            try {
                File tempFile = File.createTempFile("tmp", null);
                tempFile.deleteOnExit();
                Buffer tempBuffer = jEdit.openTemporary(jEdit.getActiveView(), null, tempFile.getAbsolutePath(), true);
                tempBuffer.setMode(jEdit.getMode(modeName));
                tempBuffer.insert(0, sb.toString());
                sb.setLength(0);
                int firstLine = 0;
                int lastLine = tempBuffer.getLineCount();
                DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
                for (int lineNum = firstLine; lineNum < lastLine; lineNum++) {
                    tokenHandler.init();
 
                    int lineStart = tempBuffer.getLineStartOffset(lineNum);
                    tempBuffer.markTokens(lineNum, tokenHandler);
                    Token token = tokenHandler.getTokens();
                    int tokenStart = lineStart;
 
                    String previousTokenText = "";
                    String currentTokenText = tempBuffer.getText(tokenStart, token.length);
                    String nextTokenText = token.next != null ? tempBuffer.getText(tokenStart + token.length, token.next.length) : "";
 
                    while (token.id != Token.END) {
 
                        // maybe pad start
                        if (! previousTokenText.endsWith(" ")) {                            // NOPMD
                            if ((token.id == Token.KEYWORD1 && prePadKeyword1) || (token.id == Token.KEYWORD2 && prePadKeyword2) || (token.id == Token.KEYWORD3 && prePadKeyword3) || (token.id == Token.KEYWORD4 && prePadKeyword4)) {                                // NOPMD
                                sb.append(' ');
                            }
                        }
 
                        // definitely add text of current token
                        sb.append(currentTokenText);
 
                        // maybe pad after token
                        if (! nextTokenText.startsWith(" ")) {
                            if ((token.id == Token.KEYWORD1 && postPadKeyword1) || (token.id == Token.KEYWORD2 && postPadKeyword2) || (token.id == Token.KEYWORD3 && postPadKeyword3) || (token.id == Token.KEYWORD4 && postPadKeyword4)) {                                // NOPMD
                                sb.append(' ');
                                currentTokenText += " ";                                // NOPMD
                            }
                        }
 
                        previousTokenText = currentTokenText;
                        currentTokenText = nextTokenText;
                        tokenStart += token.length;
                        token = token.next;
                        if (token.next != null) {
                            nextTokenText = tempBuffer.getText(tokenStart + token.length, token.next.length);
                        }
                    }
                    if (lineNum <= lastLine - 2) {
                        sb.append(getLineSeparator());
                    }
                }
                if (sb.length() == 0) {
                    sb = new StringBuilder(tempBuffer.getText(0, tempBuffer.getLength())); 
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } 
        }
        return sb;
    }
 
    /**
     * Collapse two or more blank lines to a single blank line.
     */ 
    String collapseBlankLines(String s) {
        if (! collapseBlankLines) {
            return s;
        }
        String regex = "(([ ]|[\\t])*(" + getLSString() + ")){2,}";
        s = s.replaceAll(regex, getLineSeparator());
        return s;
    }
 
    /**
     * Use the jEdit indenter to indent the lines.
     */ 
    StringBuilder indentLines(StringBuilder sb) {
        try {
            // unfortunate hack here -- the Mode class only loads the indenting rules once,
            // so need to set the rules to null so they get reloaded with the user defined
            // properties for this custom beautifier.
            Mode mode = jEdit.getMode(modeName);
            PrivilegedAccessor.setValue(mode, "indentRules", null);
 
            // now the indenting rules can be set and will be used by jEdit
            mode.setProperty(INDENT_OPEN_BRACKETS, indentOpenBrackets);
            mode.setProperty(INDENT_CLOSE_BRACKETS, indentCloseBrackets);
            mode.setProperty(UNALIGNED_OPEN_BRACKETS, unalignedOpenBrackets);
            mode.setProperty(UNALIGNED_CLOSE_BRACKETS, unalignedCloseBrackets);
            mode.setProperty(INDENT_NEXT_LINE, indentNextLine);
            mode.setProperty(UNINDENT_THIS_LINE, unindentThisLine);
            mode.setProperty(ELECTRIC_KEYS, electricKeys); 
 
            File tempFile = File.createTempFile("tmp", null);
            tempFile.deleteOnExit();
            Buffer tempBuffer = jEdit.openTemporary(jEdit.getActiveView(), null, tempFile.getAbsolutePath(), true);
            tempBuffer.setMode(mode);
            tempBuffer.insert(0, sb.toString());
            tempBuffer.indentLines(0, tempBuffer.getLineCount() - 1);
            sb = new StringBuilder(tempBuffer.getText(0, tempBuffer.getLength()));
 
            if (initialLevel > 0) {
                // do additional indenting, for example, this is the case when
                // javascript is inside a <script> block in an html or jsp file
                // and the entire block needs indented some more to fit with
                // the rest of the file.
                StringBuilder pad = new StringBuilder();
                for (int i = 0; i < initialLevel; i++) {
                    pad.append(indent);
                }
                String all = sb.toString();
                String ls = getLineSeparator();
                String[] lines = all.split(ls);
                sb.setLength(0);
                for (String line : lines) {
                    sb.append(pad).append(line).append(ls);
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } 
        return sb;
    }
 
    /**
     * @return A string representing the line separator escaped for using it
     * in a regular expression.
     */ 
    private String getLSString() {
        String ls = getLineSeparator();
        if ("\r".equals(ls)) {
            return "\\r";
        }
        if ("\r\n".equals(ls)) {
            return "\\r\\n";
        }
        return "\\n";
    }
 
    /**
     * This splits the input string into a list of tokens, where a token is either
     * some modifiable text, a comment, or a literal.  This allows the pad/don't pad and pre/post insert
     * methods to work only on the modifiable portions of the input and leave the comments and literals alone.
     * This uses the jEdit syntax highlighting engine to do the parsing.
     * @param sb Some text to parse.
     * @param A list of PTokens.  Modifiable PTokens have isText set to true.
     */ 
    public List<PToken> parseTokens(StringBuilder sb) {
        List<PToken> ptokens = new ArrayList<PToken>();
        try {
            StringBuilder textBuffer = new StringBuilder();
            StringBuilder commentBuffer = new StringBuilder();
            StringBuilder literalBuffer = new StringBuilder();
 
            File tempFile = File.createTempFile("tmp", null);
            tempFile.deleteOnExit();
            Buffer tempBuffer = jEdit.openTemporary(jEdit.getActiveView(), null, tempFile.getAbsolutePath(), true);
            tempBuffer.setMode(jEdit.getMode(modeName));
            tempBuffer.insert(0, sb.toString());
 
            int firstLine = 0;
            int lastLine = tempBuffer.getLineCount();
            DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
 
            for (int lineNum = firstLine; lineNum < lastLine; lineNum++) {
                tokenHandler.init();
 
                int lineStart = tempBuffer.getLineStartOffset(lineNum);
                tempBuffer.markTokens(lineNum, tokenHandler);
                Token token = tokenHandler.getTokens();
                int tokenStart = lineStart;
 
                String currentTokenText = tempBuffer.getText(tokenStart, token.length);
                String nextTokenText = token.next != null ? tempBuffer.getText(tokenStart + token.length, token.next.length) : "";
 
                while (token.id != Token.END) {
                    if (token.id == Token.COMMENT1 || token.id == Token.COMMENT2 || token.id == Token.COMMENT3 || token.id == Token.COMMENT4) {
                        // hit a comment token and there is text in the textBuffer, so
                        // create a ptoken for the text
                        if (textBuffer.length() > 0) {
                            PToken textToken = new PToken();
                            textToken.isText = true;
                            textToken.tokenText = textBuffer.toString();
                            ptokens.add(textToken);
                            textBuffer.setLength(0);
                        }
                        // hit a comment token and there is text in the literalBuffer, so
                        // create a ptoken for the literal
                        if (literalBuffer.length() > 0) {
                            PToken literalToken = new PToken();
                            literalToken.isText = false;
                            literalToken.tokenText = literalBuffer.toString();
                            ptokens.add(literalToken);
                            literalBuffer.setLength(0);
                        }
                        commentBuffer.append(currentTokenText);
                    } else if (token.id == Token.LITERAL1 || token.id == Token.LITERAL2 || token.id == Token.LITERAL3 || token.id == Token.LITERAL4) {
                        // hit a literal token and there is text in the textBuffer, so
                        // create a ptoken for the text
                        if (textBuffer.length() > 0) {
                            PToken textToken = new PToken();
                            textToken.isText = true;
                            textToken.tokenText = textBuffer.toString();
                            ptokens.add(textToken);
                            textBuffer.setLength(0);
                        }
                        // hit a literal token and there is text in the literalBuffer, so
                        // create a ptoken for the literal
                        if (commentBuffer.length() > 0) {
                            PToken commentToken = new PToken();
                            commentToken.isText = false;
                            commentToken.tokenText = commentBuffer.toString();
                            ptokens.add(commentToken);
                            commentBuffer.setLength(0);
                        }
                        literalBuffer.append(currentTokenText);
                    } else {
                        // in a regular, modifiable piece of code, close out the
                        // comment and literal buffers
                        if (commentBuffer.length() > 0) {
                            PToken commentToken = new PToken();
                            commentToken.isText = false;
                            commentToken.tokenText = commentBuffer.toString();
                            ptokens.add(commentToken);
                            commentBuffer.setLength(0);
                        }
                        if (literalBuffer.length() > 0) {
                            PToken literalToken = new PToken();
                            literalToken.isText = false;
                            literalToken.tokenText = literalBuffer.toString();
                            ptokens.add(literalToken);
                            literalBuffer.setLength(0);
                        }
                        textBuffer.append(currentTokenText);
                    }
 
                    currentTokenText = nextTokenText;
                    tokenStart += token.length;
                    token = token.next;
                    if (token.next != null) {
                        nextTokenText = tempBuffer.getText(tokenStart + token.length, token.next.length);
                    }
                }
                if (lineNum <= lastLine - 2 && textBuffer.length() > 0) {
                    textBuffer.append(getLineSeparator());
                }
                if (lineNum <= lastLine - 2 && commentBuffer.length() > 0) {
                    commentBuffer.append(getLineSeparator()); 
                }
            }
            if (textBuffer.length() > 0) {
                PToken textToken = new PToken();
                textToken.isText = true;
                textToken.tokenText = textBuffer.toString();
                ptokens.add(textToken);
            }
            if (commentBuffer.length() > 0) {
                PToken commentToken = new PToken();
                commentToken.isText = false;
                commentToken.tokenText = commentBuffer.toString();
                ptokens.add(commentToken);
            }
            if (literalBuffer.length() > 0) {
                PToken literalToken = new PToken();
                literalToken.isText = false;
                literalToken.tokenText = literalBuffer.toString();
                ptokens.add(literalToken);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } 
        return ptokens;
    }
 
    // A class to tell modifiable text from comments and literals.
    public class PToken {
        boolean isText = false;
        String tokenText;
    }
 
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    /// everything below is to support unit tests.
 
    /**
     * Sets the value of prePadOperator.
     * @param prePadOperator The value to assign prePadOperator.
     */ 
    public void setPrePadOperator(boolean prePadOperator) {
        this.prePadOperator = prePadOperator;
    }
 
    /**
     * Sets the value of prePadFunction.
     * @param prePadFunction The value to assign prePadFunction.
     */ 
    public void setPrePadFunction(boolean prePadFunction) {
        this.prePadFunction = prePadFunction;
    }
 
    /**
     * Sets the value of prePadDigit.
     * @param prePadDigit The value to assign prePadDigit.
     */ 
    public void setPrePadDigit(boolean prePadDigit) {
        this.prePadDigit = prePadDigit;
    }
 
    /**
     * Sets the value of prePadKeyword1.
     * @param prePadKeyword1 The value to assign prePadKeyword1.
     */ 
    public void setPrePadKeyword1(boolean prePadKeyword1) {
        this.prePadKeyword1 = prePadKeyword1;
    }
 
    /**
     * Sets the value of prePadKeyword2.
     * @param prePadKeyword2 The value to assign prePadKeyword2.
     */ 
    public void setPrePadKeyword2(boolean prePadKeyword2) {
        this.prePadKeyword2 = prePadKeyword2;
    }
 
    /**
     * Sets the value of prePadKeyword3.
     * @param prePadKeyword3 The value to assign prePadKeyword3.
     */ 
    public void setPrePadKeyword3(boolean prePadKeyword3) {
        this.prePadKeyword3 = prePadKeyword3;
    }
 
    /**
     * Sets the value of prePadKeyword4.
     * @param prePadKeyword4 The value to assign prePadKeyword4.
     */ 
    public void setPrePadKeyword4(boolean prePadKeyword4) {
        this.prePadKeyword4 = prePadKeyword4;
    }
 
    /**
     * Sets the value of postPadOperator.
     * @param postPadOperator The value to assign postPadOperator.
     */ 
    public void setPostPadOperator(boolean postPadOperator) {
        this.postPadOperator = postPadOperator;
    }
 
    /**
     * Sets the value of postPadFunction.
     * @param postPadFunction The value to assign postPadFunction.
     */ 
    public void setPostPadFunction(boolean postPadFunction) {
        this.postPadFunction = postPadFunction;
    }
 
    /**
     * Sets the value of postPadDigit.
     * @param postPadDigit The value to assign postPadDigit.
     */ 
    public void setPostPadDigit(boolean postPadDigit) {
        this.postPadDigit = postPadDigit;
    }
 
    /**
     * Sets the value of postPadKeyword1.
     * @param postPadKeyword1 The value to assign postPadKeyword1.
     */ 
    public void setPostPadKeyword1(boolean postPadKeyword1) {
        this.postPadKeyword1 = postPadKeyword1;
    }
 
    /**
     * Sets the value of postPadKeyword2.
     * @param postPadKeyword2 The value to assign postPadKeyword2.
     */ 
    public void setPostPadKeyword2(boolean postPadKeyword2) {
        this.postPadKeyword2 = postPadKeyword2;
    }
 
    /**
     * Sets the value of postPadKeyword3.
     * @param postPadKeyword3 The value to assign postPadKeyword3.
     */ 
    public void setPostPadKeyword3(boolean postPadKeyword3) {
        this.postPadKeyword3 = postPadKeyword3;
    }
 
    /**
     * Sets the value of postPadKeyword4.
     * @param postPadKeyword4 The value to assign postPadKeyword4.
     */ 
    public void setPostPadKeyword4(boolean postPadKeyword4) {
        this.postPadKeyword4 = postPadKeyword4;
    }
 
    /**
     * Sets the value of labelOnSeparateLine.
     * @param labelOnSeparateLine The value to assign labelOnSeparateLine.
     */ 
    public void setLabelOnSeparateLine(boolean labelOnSeparateLine) {
        this.labelOnSeparateLine = labelOnSeparateLine;
    }
 
    /**
     * Sets the value of prePadCharacters.
     * @param prePadCharacters The value to assign prePadCharacters.
     */ 
    public void setPrePadCharacters(String prePadCharacters) {
        this.prePadCharacters = prePadCharacters;
    }
 
    /**
     * Sets the value of postPadCharacters.
     * @param postPadCharacters The value to assign postPadCharacters.
     */ 
    public void setPostPadCharacters(String postPadCharacters) {
        this.postPadCharacters = postPadCharacters;
    }
 
    /**
     * Sets the value of dontPrePadCharacters.
     * @param dontPrePadCharacters The value to assign dontPrePadCharacters.
     */ 
    public void setDontPrePadCharacters(String dontPrePadCharacters) {
        this.dontPrePadCharacters = dontPrePadCharacters;
    }
 
    /**
     * Sets the value of dontPostPadCharacters.
     * @param dontPostPadCharacters The value to assign dontPostPadCharacters.
     */ 
    public void setDontPostPadCharacters(String dontPostPadCharacters) {
        this.dontPostPadCharacters = dontPostPadCharacters;
    }
 
    /**
     * Sets the value of preInsertLineCharacters.
     * @param preInsertLineCharacters The value to assign preInsertLineCharacters.
     */ 
    public void setPreInsertLineCharacters(String preInsertLineCharacters) {
        this.preInsertLineCharacters = preInsertLineCharacters;
    }
 
    /**
     * Sets the value of postInsertLineCharacters.
     * @param postInsertLineCharacters The value to assign postInsertLineCharacters.
     */ 
    public void setPostInsertLineCharacters(String postInsertLineCharacters) {
        this.postInsertLineCharacters = postInsertLineCharacters;
    }
 
    /**
     * Sets the value of collapseBlankLines.
     * @param collapseBlankLines The value to assign collapseBlankLines.
     */ 
    public void setCollapseBlankLines(boolean collapseBlankLines) {
        this.collapseBlankLines = collapseBlankLines;
    }
}