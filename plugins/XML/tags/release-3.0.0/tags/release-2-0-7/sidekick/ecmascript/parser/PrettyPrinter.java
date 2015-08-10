/*
 Copyright (c) 2004-2005, The Dojo Foundation
 All Rights Reserved.

 Licensed under the Academic Free License version 2.1 or above OR the
 modified BSD license. For more information on Dojo licensing, see:

 http://dojotoolkit.org/community/licensing.shtml <http://dojotoolkit.org/community/licensing.shtml>

 Code donated to the Dojo Foundation by AOL LLC under the terms of
 the Dojo CCLA (http://dojotoolkit.org/ccla.txt).

 */
package sidekick.ecmascript.parser;

import java.util.*;
import java.io.*;

import sidekick.ecmascript.parser.ASTAllocationExpression;
import sidekick.ecmascript.parser.ASTArrayLiteral;
import sidekick.ecmascript.parser.ASTBreakStatement;
import sidekick.ecmascript.parser.ASTCaseGroups;
import sidekick.ecmascript.parser.ASTCaseGuard;
import sidekick.ecmascript.parser.ASTCatchClause;
import sidekick.ecmascript.parser.ASTConditionalExpression;
import sidekick.ecmascript.parser.ASTContinueStatement;
import sidekick.ecmascript.parser.ASTDoStatement;
import sidekick.ecmascript.parser.ASTEmptyStatement;
import sidekick.ecmascript.parser.ASTExpressionList;
import sidekick.ecmascript.parser.ASTExpressionStatement;
import sidekick.ecmascript.parser.ASTFinallyClause;
import sidekick.ecmascript.parser.ASTForInStatement;
import sidekick.ecmascript.parser.ASTForStatement;
import sidekick.ecmascript.parser.ASTForVarInStatement;
import sidekick.ecmascript.parser.ASTForVarStatement;
import sidekick.ecmascript.parser.ASTFormalParameterList;
import sidekick.ecmascript.parser.ASTFunctionCallParameters;
import sidekick.ecmascript.parser.ASTIfStatement;
import sidekick.ecmascript.parser.ASTLiteralField;
import sidekick.ecmascript.parser.ASTObjectLiteral;
import sidekick.ecmascript.parser.ASTParenExpression;
import sidekick.ecmascript.parser.ASTPropertyIdentifierReference;
import sidekick.ecmascript.parser.ASTPropertyValueReference;
import sidekick.ecmascript.parser.ASTReturnStatement;
import sidekick.ecmascript.parser.ASTSwitchStatement;
import sidekick.ecmascript.parser.ASTThisReference;
import sidekick.ecmascript.parser.ASTThrowStatement;
import sidekick.ecmascript.parser.ASTTryStatement;
import sidekick.ecmascript.parser.ASTVariableDeclaration;
import sidekick.ecmascript.parser.ASTVariableDeclarationList;
import sidekick.ecmascript.parser.ASTVariableStatement;
import sidekick.ecmascript.parser.ASTWhileStatement;
import sidekick.ecmascript.parser.ASTWithStatement;
import sidekick.ecmascript.parser.EcmaScriptConstants;
import sidekick.ecmascript.parser.Token;

/**
 *
 *
 * @since JDK 1.4
 */
public class PrettyPrinter extends EcmaScriptVisitorAdapter {

    static public final int PRETTY_PRINT = 0;

    static public final int PRESERVE_FORMATTING = 1;

    static public final int STRIP_COMMENTS = 2;

    static public final int STRIP_WHITESPACE = 4;

    static public final int STRIP_NEWLINES = 8;

    static public final int STRIP_ALL = STRIP_COMMENTS | STRIP_WHITESPACE
            | STRIP_NEWLINES;

    private Writer writer;

    private int style;

    // kept updated while writing out to keep track of indentation
    private int line;

    private int column;

    private int level;

    private boolean separator;

    // conveniences for indentation
    static private char[] BLANKS = new char[128];

    static private char[] FEEDS = new char[8];

    static {
        for (int i = 0; i < FEEDS.length; i++) {
            FEEDS[i] = '\n';
        }
        for (int i = 0; i < BLANKS.length; i++) {
            BLANKS[i] = ' ';
        }
    }

    public PrettyPrinter(Writer writer) {
        this(writer, PRESERVE_FORMATTING);
    }

    public PrettyPrinter(Writer writer, int style) {
        super();

        this.writer = writer;
        this.style = style;
    }

    /**
     * Convenience method to write indentation chars.
     */
    protected void printIndentation(int lf, int blanks) {
        if (lf > 0) {
            do {
                int n = Math.min(lf, FEEDS.length);
                this.print(FEEDS, 0, n);
                lf -= n;
            } while (lf > 0);
        }
        while (blanks > 0) {
            int n = Math.min(blanks, BLANKS.length);
            this.print(BLANKS, 0, n);
            blanks -= n;
        }
    }

    /**
     * Write a single character.
     *
     * @param c
     *            an int value.
     */
    protected void print(int c) {
        if (c == '\n') {
            column = 0;
            line += 1;
        } else {
            column += 1;
        }
        try {
            writer.write(c);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Write a sequence of characters.
     *
     * @param cbuf
     *            an array of char.
     * @param off
     *            an int value.
     * @param len
     *            an int value.
     */
    protected void print(char[] cbuf, int off, int len) {
        boolean col = false;

        for (int i = off + len - 1; i >= off; i -= 1) {
            if (cbuf[i] == '\n') {
                line += 1;

                if (!col) {
                    column = off + len - 1 - i;
                    col = true;
                }
            }
        }
        if (!col) {
            column += len;
        }
        try {
            writer.write(cbuf, off, len);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Writes a string.
     *
     * @param str
     *            a string.
     */
    protected void print(String str) {
        int i = str.lastIndexOf('\n');

        if (i >= 0) {
            column = str.length() - i + 1;
            do {
                line += 1;
                i = str.lastIndexOf('\n', i - 1);
            } while (i >= 0);
        } else {
            column += str.length();
        }
        try {
            writer.write(str);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private Token invertSpecialTokenList(Token token) {
        Token y = null;
        Token x = token;
        Token dummy;

        while (x != null) {
            dummy = x;
            x = dummy.specialToken;
            dummy.specialToken = y;
            y = dummy;
        }

        return y;
    }

    // this is getting called when at least some of the old formatting needs to
    // be preserved
    protected void printToken(Token token) {

        // invert the special tokens (javacc gives them to us in inverted order)
        token.specialToken = invertSpecialTokenList(token.specialToken);

        Token wsToken = token.specialToken;
        boolean hadSeparator = (wsToken != null);

        if ((style & STRIP_ALL) == STRIP_ALL) {
            // ignore all special tokens (comments and white space)
            if ((!separator) && hadSeparator) {
                this.print(' ');
            }
            this.print(token.image);
            return;
        }

        // write out filtered list of special tokens

        boolean wroteSeparator = false;

        while (wsToken != null) {

            if ((wsToken.kind == EcmaScriptConstants.MULTI_LINE_COMMENT)
                    || (wsToken.kind == EcmaScriptConstants.SINGLE_LINE_COMMENT)) {
                if ((style & STRIP_COMMENTS) == 0) {
                    this.print(wsToken.image);
                    wroteSeparator = true;
                }
            } else if (wsToken.kind == EcmaScriptConstants.WHITE_SPACE) {
                if ((style & STRIP_WHITESPACE) == 0) {
                    this.print(wsToken.image);
                    wroteSeparator = true;
                }
            } else { // wsToken.kind == EOL
                if ((style & STRIP_NEWLINES) == 0) {
                    this.print(wsToken.image);
                    wroteSeparator = true;
                }
            }
            wsToken = wsToken.specialToken;
        }

        if ((!separator) && (!wroteSeparator) && (style != PRESERVE_FORMATTING)
                && hadSeparator) {
            // we need some kind of separator
            this.print(' ');
        }

        // write out token itself
        this.print(token.image);

        // invert the special tokens back
        token.specialToken = invertSpecialTokenList(token.specialToken);
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        throw new IllegalStateException(
                "the visitor has been called with a simple node");
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        // if it's an object literal or array literal we let the children nodes
        // do the work
        Object literalValue = node.getValue();

        if ((literalValue instanceof Map) || (literalValue instanceof List)) {
            return super.visit(node, data);
        }

        // begin and end token for this node are the same
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!separator) {
                this.print(' ');
            }
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        return data;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        // begin and end token for this node are the same
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!separator) {
                this.print(' ');
            }
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        return data;
    }

    @Override
    public Object visit(ASTObjectLiteral node, Object data) {
        // write out "{", the commas and "}" and let the children write out rest

        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        level++;

        int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            SimpleNode literalFieldNode = (SimpleNode) node.jjtGetChild(i);

            if ((style == PRETTY_PRINT) || node.inserted()) {
                printIndentation(1, level * 4);
                separator = true;
            }

            data = literalFieldNode.jjtAccept(this, data);

            // write out the comma
            if (i < n - 1) {
                Token commaToken = literalFieldNode.getEndToken().next;
                if (commaToken == null) {
                    // this might happen if the literalFieldNode is newly
                    // inserted
                    this.print(',');
                } else {
                    if ((style == PRETTY_PRINT) || node.inserted()) {
                        this.print(commaToken.image);
                    } else {
                        // preserve some or all of the special tokens
                        printToken(commaToken);
                    }
                }

                separator = false;
            }
        }

        level--;

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTLiteralField node, Object data) {
        // write out ":" and let children write out rest

        // print out first child
        SimpleNode keyNode = (SimpleNode) node.jjtGetChild(0);

        data = keyNode.jjtAccept(this, data);

        Token colonToken = keyNode.getEndToken().next;

        if (colonToken == null) {
            this.print(" :");
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(colonToken.image);
            } else {
                // preserve some or all of the special tokens
                printToken(colonToken);
            }
        }

        separator = false;

        SimpleNode valueNode = (SimpleNode) node.jjtGetChild(1);

        return valueNode.jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTArrayLiteral node, Object data) {
        // write out "[", the commas and "]" and let the children write out rest

        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;
        level++;

        int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            SimpleNode arrayElementNode = (SimpleNode) node.jjtGetChild(i);

            if ((style == PRETTY_PRINT) || node.inserted()) {
                printIndentation(1, level * 4);
                separator = true;
            }

            data = arrayElementNode.jjtAccept(this, data);

            // write out the comma
            if (i < n - 1) {
                Token commaToken = arrayElementNode.getEndToken().next;

                if (commaToken == null) {
                    this.print(',');
                } else {
                    if ((style == PRETTY_PRINT) || node.inserted()) {
                        this.print(commaToken.image);
                    } else {
                        // preserve some or all of the special tokens
                        printToken(commaToken);
                    }
                }
                separator = false;
            }
        }

        level--;

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }
        separator = true;
        return data;
    }

    @Override
    public Object visit(ASTVariableStatement node, Object data) {
        // take care of "var" token and ";" token, let children do rest,
        // but write commas between children if more than one

        Token varToken = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(varToken.image);
        } else {
            // preserve some or all of the special tokens
            printToken(varToken);
        }

        separator = false;

        int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            SimpleNode varDeclNode = (SimpleNode) node.jjtGetChild(i);

            data = varDeclNode.jjtAccept(this, data);

            // write out the comma
            if (i < n - 1) {
                Token commaToken = varDeclNode.getEndToken().next;

                if (commaToken == null) {
                    this.print(',');
                } else {
                    if ((style == PRETTY_PRINT) || node.inserted()) {
                        this.print(commaToken.image);
                    } else {
                        // preserve some or all of the special tokens
                        printToken(commaToken);
                    }
                }

                separator = false;
            }
        }

        Token scToken = node.getEndToken();

        if ((scToken != null)
                && (scToken.kind == EcmaScriptConstants.SEMICOLON)) {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(scToken.image);
            } else {
                // preserve some or all of the special tokens
                printToken(scToken);
            }

            separator = true;
        }

        return data;
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        // for the optional semicolon

        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (getScope() instanceof ASTProgram) {
                printIndentation(2, level * 4);
            } else {
                printIndentation(1, level * 4);
            }
            separator = true;
        }

        data = super.visit(node, data);

        Token scToken = node.getEndToken();

        if ((scToken != null)
                && (scToken.kind == EcmaScriptConstants.SEMICOLON)) {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(scToken.image);
            } else {
                // preserve some or all of the special tokens
                printToken(scToken);
            }
            separator = true;
        }

        return data;
    }

    @Override
    public Object visit(ASTThisReference node, Object data) {
        // begin and end token for this node are the same
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;
        return data;
    }

    @Override
    public Object visit(ASTFunctionCallParameters node, Object data) {
        // write out "(", the commas and ")" and let the children write out rest
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            SimpleNode paramNode = (SimpleNode) node.jjtGetChild(i);

            data = paramNode.jjtAccept(this, data);

            // write out the comma
            if (i < n - 1) {
                Token commaToken = paramNode.getEndToken().next;

                if (commaToken == null) {
                    this.print(',');
                } else {
                    if ((style == PRETTY_PRINT) || node.inserted()) {
                        this.print(commaToken.image);
                    } else {
                        // preserve some or all of the special tokens
                        printToken(commaToken);
                    }
                }

                separator = false;
            }
        }

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTPropertyValueReference node, Object data) {
        // write out "[" and "]" and let child do rest
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        data = super.visit(node, data);

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTPropertyIdentifierReference node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        data = super.visit(node, data);

        return data;
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!separator) {
                this.print(' ');
            }
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        data = super.visit(node, data);

        return data;
    }

    @Override
    public Object visit(ASTOperator node, Object data) {
        // begin and end token for this node are the same
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!(token.image.equals("++") || token.image.equals("--"))) {
                this.print(' ');
            }
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }
        separator = (token.image.equals("++") || token.image.equals("--"));

        return data;
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        // take care of hook and colon
        SimpleNode condNode = (SimpleNode) node.jjtGetChild(0);
        data = condNode.jjtAccept(this, data);

        Token hookToken = condNode.getEndToken().next;

        if (hookToken == null) {
            this.print(' ');
            this.print('?');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(' ');
                this.print(hookToken.image);
            } else {
                // preserve some or all of the special tokens
                printToken(hookToken);
            }
        }
        separator = false;

        SimpleNode trueBranchNode = (SimpleNode) node.jjtGetChild(1);
        data = trueBranchNode.jjtAccept(this, data);

        Token colonToken = trueBranchNode.getEndToken().next;

        if (colonToken == null) {
            this.print(' ');
            this.print('?');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(' ');
                this.print(colonToken.image);
            } else {
                // preserve some or all of the special tokens
                printToken(colonToken);
            }
        }

        separator = false;

        SimpleNode falseBranchNode = (SimpleNode) node.jjtGetChild(2);
        data = falseBranchNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTExpressionList node, Object data) {
        // take care of comma

        int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            SimpleNode paramNode = (SimpleNode) node.jjtGetChild(i);

            data = paramNode.jjtAccept(this, data);

            // write out the comma
            if (i < n - 1) {
                Token commaToken = paramNode.getEndToken().next;

                if (commaToken == null) {
                    this.print(',');
                } else {
                    if ((style == PRETTY_PRINT) || node.inserted()) {
                        this.print(commaToken.image);
                    } else {
                        // preserve some or all of the special tokens
                        printToken(commaToken);
                    }
                }

                separator = false;
            }
        }

        return data;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        // take care of "{" and "}"

        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(' ');
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        level++;

        data = super.visit(node, data);

        level--;

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        // write out first child
        SimpleNode identifierNode = (SimpleNode) node.jjtGetChild(0);

        data = identifierNode.jjtAccept(this, data);

        if (node.jjtGetNumChildren() == 2) {
            // write out "=" token

            Token assignToken = identifierNode.getEndToken().next;

            if (assignToken == null) {
                this.print(" =");
            } else {
                if ((style == PRETTY_PRINT) || node.inserted()) {
                    this.print(' ');
                    this.print(assignToken.image);
                } else {
                    // preserve some or all of the special tokens
                    printToken(assignToken);
                }
            }

            separator = false;

            SimpleNode initializerNode = (SimpleNode) node.jjtGetChild(1);

            data = initializerNode.jjtAccept(this, data);
        }

        return data;
    }

    @Override
    public Object visit(ASTVariableDeclarationList node, Object data) {
        // take care of the commas

        int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            SimpleNode paramNode = (SimpleNode) node.jjtGetChild(i);

            data = paramNode.jjtAccept(this, data);

            // write out the comma
            if (i < n - 1) {
                Token commaToken = paramNode.getEndToken().next;

                if (commaToken == null) {
                    this.print(',');
                } else {
                    if ((style == PRETTY_PRINT) || node.inserted()) {
                        this.print(commaToken.image);
                    } else {
                        // preserve some or all of the special tokens
                        printToken(commaToken);
                    }
                }

                separator = false;
            }
        }

        return data;

    }

    @Override
    public Object visit(ASTEmptyStatement node, Object data) {
        // begin and end token for this node are the same
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTFunctionDeclaration node, Object data) {
        // take care of "function"

        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!separator) {
                this.print(' ');
            }
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        return super.visit(node, data);
    }

    /*
     * @Override public Object visit(ASTActiveXReference node, Object data) { //
     * take care of "::" SimpleNode objNode = (SimpleNode) node.jjtGetChild(0);
     *
     * data = objNode.jjtAccept(this, data);
     *
     * Token doubleColonToken = objNode.getEndToken().next;
     *
     * if (doubleColonToken == null) { this.print("::"); } else { if ((style ==
     * PRETTY_PRINT) || node.inserted()) { this.print(doubleColonToken.image); }
     * else { // preserve some or all of the special tokens
     * printToken(doubleColonToken); } }
     *
     * SimpleNode nameNode = (SimpleNode) node.jjtGetChild(1);
     *
     * separator = false; return nameNode.jjtAccept(this, data); }
     */

    @Override
    public Object visit(ASTFormalParameterList node, Object data) {
        // take care of "(", commas and ")"

        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        int n = node.jjtGetNumChildren();
        for (int i = 0; i < n; i++) {
            SimpleNode paramNode = (SimpleNode) node.jjtGetChild(i);

            data = paramNode.jjtAccept(this, data);

            // write out the comma
            if (i < n - 1) {
                Token commaToken = paramNode.getEndToken().next;

                if (commaToken == null) {
                    this.print(',');
                } else {
                    if ((style == PRETTY_PRINT) || node.inserted()) {
                        this.print(commaToken.image);
                    } else {
                        // preserve some or all of the special tokens
                        printToken(commaToken);
                    }
                }

                separator = false;
            }
        }

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        SimpleNode condNode = (SimpleNode) node.jjtGetChild(0);

        data = condNode.jjtAccept(this, data);

        token = condNode.getEndToken().next;

        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode trueBranchNode = (SimpleNode) node.jjtGetChild(1);
        boolean single = false;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!(trueBranchNode instanceof ASTBlock)) {
                single = true;

                level++;
            }
        }

        data = trueBranchNode.jjtAccept(this, data);

        if (node.jjtGetNumChildren() == 3) {
            token = trueBranchNode.getEndToken().next;

            if (token == null) {
                if (single) {
                    level--;
                } else {
                    this.print(' ');
                }
                this.print("else");
            } else {
                if ((style == PRETTY_PRINT) || node.inserted()) {
                    if (single) {
                        level--;
                    } else {
                        this.print(' ');
                    }
                    this.print(token.image);
                } else {
                    // preserve some or all of the special tokens
                    printToken(token);
                }
            }

            separator = false;

            SimpleNode falseBranchNode = (SimpleNode) node.jjtGetChild(2);

            boolean singleElse = false;

            if ((style == PRETTY_PRINT) || node.inserted()) {
                if (!(falseBranchNode instanceof ASTBlock)) {
                    singleElse = true;
                    level++;
                }
            }

            data = falseBranchNode.jjtAccept(this, data);

            if (singleElse) {
                level--;
            }

        } else {
            if (single) {
                level--;
            }
        }

        return data;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        SimpleNode condNode = (SimpleNode) node.jjtGetChild(0);

        data = condNode.jjtAccept(this, data);

        token = condNode.getEndToken().next;

        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode groupsNode = (SimpleNode) node.jjtGetChild(1);

        data = groupsNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTCaseGroups node, Object data) {
        // take care of "{" and "}"

        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        level++;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
        }
        data = super.visit(node, data);

        level--;

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTParenExpression node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        data = super.visit(node, data);

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTCaseGuard node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!separator) {
                this.print(' ');
            }
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        if (node.jjtGetNumChildren() > 0) {
            data = super.visit(node, data);
        }

        token = node.getEndToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(' ');
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        Token scToken = node.getEndToken();

        if ((scToken != null)
                && (scToken.kind == EcmaScriptConstants.SEMICOLON)) {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(scToken.image);
            } else {
                // preserve some or all of the special tokens
                printToken(scToken);
            }

            separator = true;
        }

        return data;
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        data = super.visit(node, data);

        Token scToken = node.getEndToken();

        if ((scToken != null)
                && (scToken.kind == EcmaScriptConstants.SEMICOLON)) {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(scToken.image);
            } else {
                // preserve some or all of the special tokens
                printToken(scToken);
            }

            separator = true;
        }

        return data;
    }

    @Override
    public Object visit(ASTWithStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        SimpleNode exprNode = (SimpleNode) node.jjtGetChild(0);

        data = exprNode.jjtAccept(this, data);

        token = exprNode.getEndToken().next;

        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode stmtNode = (SimpleNode) node.jjtGetChild(1);

        data = stmtNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCatchClause node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!separator) {
                this.print(' ');
            }
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;
        SimpleNode identifierNode = (SimpleNode) node.jjtGetChild(0);

        data = identifierNode.jjtAccept(this, data);

        token = identifierNode.getEndToken().next;

        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode blockNode = (SimpleNode) node.jjtGetChild(1);

        data = blockNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTFinallyClause node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            if (!separator) {
                this.print(' ');
            }
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(1, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        Token scToken = node.getEndToken();

        if ((scToken != null)
                && (scToken.kind == EcmaScriptConstants.SEMICOLON)) {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(scToken.image);
            } else {
                // preserve some or all of the special tokens
                printToken(scToken);
            }

            separator = true;
        }

        return data;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        SimpleNode codeNode = (SimpleNode) node.jjtGetChild(0);

        data = codeNode.jjtAccept(this, data);

        token = codeNode.getEndToken().next;

        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode stmtNode = (SimpleNode) node.jjtGetChild(1);

        data = stmtNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        SimpleNode stmtNode = (SimpleNode) node.jjtGetChild(0);

        data = stmtNode.jjtAccept(this, data);

        token = stmtNode.getEndToken().next;

        if (token == null) {
            this.print(" while");
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(' ');
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = false;

        if (token != null) {
            token = token.next;
        }

        if (token == null) {
            this.print('(');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode codeNode = (SimpleNode) node.jjtGetChild(1);

        data = codeNode.jjtAccept(this, data);

        token = codeNode.getEndToken().next;
        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        return data;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        SimpleNode initNode = (SimpleNode) node.jjtGetChild(0);

        data = initNode.jjtAccept(this, data);

        token = initNode.getEndToken().next;
        if (token == null) {
            this.print(';');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = false;

        SimpleNode condNode = (SimpleNode) node.jjtGetChild(1);

        data = condNode.jjtAccept(this, data);

        token = condNode.getEndToken().next;
        if (token == null) {
            this.print(';');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = false;

        SimpleNode incrNode = (SimpleNode) node.jjtGetChild(2);

        data = incrNode.jjtAccept(this, data);

        token = incrNode.getEndToken().next;
        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode stmtNode = (SimpleNode) node.jjtGetChild(3);

        data = stmtNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTForVarStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        SimpleNode initNode = (SimpleNode) node.jjtGetChild(0);

        data = initNode.jjtAccept(this, data);

        token = initNode.getEndToken().next;
        if (token == null) {
            this.print(';');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = false;

        SimpleNode condNode = (SimpleNode) node.jjtGetChild(1);

        data = condNode.jjtAccept(this, data);

        token = condNode.getEndToken().next;
        if (token == null) {
            this.print(';');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = false;

        SimpleNode incrNode = (SimpleNode) node.jjtGetChild(2);

        data = incrNode.jjtAccept(this, data);

        token = incrNode.getEndToken().next;
        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode stmtNode = (SimpleNode) node.jjtGetChild(3);

        data = stmtNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTForInStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = true;

        SimpleNode exprNode = (SimpleNode) node.jjtGetChild(0);

        data = exprNode.jjtAccept(this, data);

        token = exprNode.getEndToken().next;
        if (token == null) {
            this.print(" in");
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(' ');
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = false;

        exprNode = (SimpleNode) node.jjtGetChild(1);

        data = exprNode.jjtAccept(this, data);

        token = exprNode.getEndToken().next;
        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode stmtNode = (SimpleNode) node.jjtGetChild(2);

        data = stmtNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTForVarInStatement node, Object data) {
        Token token = node.getBeginToken();
        if ((style == PRETTY_PRINT) || node.inserted()) {
            printIndentation(2, level * 4);
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        token = token.next;

        if ((style == PRETTY_PRINT) || node.inserted()) {
            this.print(token.image);
        } else {
            // preserve some or all of the special tokens
            printToken(token);
        }

        separator = false;

        SimpleNode identifierNode = (SimpleNode) node.jjtGetChild(0);

        data = identifierNode.jjtAccept(this, data);

        SimpleNode initializerNode = (SimpleNode) node.jjtGetChild(1);

        data = initializerNode.jjtAccept(this, data);

        token = initializerNode.getEndToken().next;
        if (token == null) {
            this.print(" in");
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(' ');
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = false;

        SimpleNode exprNode = (SimpleNode) node.jjtGetChild(2);

        data = exprNode.jjtAccept(this, data);

        token = exprNode.getEndToken().next;
        if (token == null) {
            this.print(')');
        } else {
            if ((style == PRETTY_PRINT) || node.inserted()) {
                this.print(token.image);
            } else {
                // preserve some or all of the special tokens
                printToken(token);
            }
        }

        separator = true;

        SimpleNode stmtNode = (SimpleNode) node.jjtGetChild(3);

        data = stmtNode.jjtAccept(this, data);

        return data;
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        data = super.visit(node, data);
        return data;
    }

}
