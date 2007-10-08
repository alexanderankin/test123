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

import sidekick.ecmascript.parser.ASTAllocationExpression;
import sidekick.ecmascript.parser.ASTAndExpressionSequence;
import sidekick.ecmascript.parser.ASTArrayLiteral;
import sidekick.ecmascript.parser.ASTAssignmentExpression;
import sidekick.ecmascript.parser.ASTBinaryExpressionSequence;
import sidekick.ecmascript.parser.ASTBreakStatement;
import sidekick.ecmascript.parser.ASTCaseGroup;
import sidekick.ecmascript.parser.ASTCaseGroups;
import sidekick.ecmascript.parser.ASTCaseGuard;
import sidekick.ecmascript.parser.ASTCatchClause;
import sidekick.ecmascript.parser.ASTConditionalExpression;
import sidekick.ecmascript.parser.ASTContinueStatement;
import sidekick.ecmascript.parser.ASTDoStatement;
import sidekick.ecmascript.parser.ASTEmptyExpression;
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
import sidekick.ecmascript.parser.ASTFunctionExpression;
import sidekick.ecmascript.parser.ASTIfStatement;
import sidekick.ecmascript.parser.ASTLiteralField;
import sidekick.ecmascript.parser.ASTObjectLiteral;
import sidekick.ecmascript.parser.ASTOrExpressionSequence;
import sidekick.ecmascript.parser.ASTParenExpression;
import sidekick.ecmascript.parser.ASTPostfixExpression;
import sidekick.ecmascript.parser.ASTPropertyIdentifierReference;
import sidekick.ecmascript.parser.ASTPropertyValueReference;
import sidekick.ecmascript.parser.ASTReturnStatement;
import sidekick.ecmascript.parser.ASTStatementList;
import sidekick.ecmascript.parser.ASTSwitchStatement;
import sidekick.ecmascript.parser.ASTThisReference;
import sidekick.ecmascript.parser.ASTThrowStatement;
import sidekick.ecmascript.parser.ASTTryStatement;
import sidekick.ecmascript.parser.ASTUnaryExpression;
import sidekick.ecmascript.parser.ASTVariableDeclaration;
import sidekick.ecmascript.parser.ASTVariableDeclarationList;
import sidekick.ecmascript.parser.ASTVariableStatement;
import sidekick.ecmascript.parser.ASTWhileStatement;
import sidekick.ecmascript.parser.ASTWithStatement;
import sidekick.ecmascript.parser.EcmaScriptVisitor;

public class EcmaScriptVisitorAdapter extends Object implements
        EcmaScriptVisitor {

    protected LinkedList scopes;

    protected ASTFunctionDeclaration enteringFunction;

    protected EcmaScriptVisitorDelegate delegate;

    protected EcmaScriptVisitorAdapter() {
        this(null);
    }

    public EcmaScriptVisitorAdapter(EcmaScriptVisitorDelegate delegate) {
        super();
        this.delegate = delegate;
        scopes = new LinkedList();
    }

    public SimpleNode getScope() {
        return scopes.size() == 0 ? null : (SimpleNode) scopes.getLast();
    }

    protected void pre(SimpleNode node, Object data) {
        if (delegate != null) {
            delegate.willVisit(node, data);
        }
    }

    protected void post(SimpleNode node, Object data) {
        if (delegate != null) {
            delegate.didVisit(node, data);
        }
    }

    protected Object visitImpl(SimpleNode node, Object data) {
        pre(node, data);
        data = node.childrenAccept(this, data);
        post(node, data);
        return data;
    }

    public Object visit(SimpleNode node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTLiteral node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTIdentifier node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTObjectLiteral node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTLiteralField node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTArrayLiteral node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTThisReference node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTCompositeReference node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTFunctionCallParameters node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTFunctionExpression node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTPropertyValueReference node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTPropertyIdentifierReference node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTParenExpression node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTOperator node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTPostfixExpression node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTUnaryExpression node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTBinaryExpressionSequence node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTAndExpressionSequence node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTOrExpressionSequence node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTAssignmentExpression node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTExpressionList node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTStatementList node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTVariableDeclaration node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTExpressionStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTIfStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTWhileStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTForStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTEmptyExpression node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTForVarStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTForInStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTForVarInStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTContinueStatement node, Object data) {
        return visitImpl(node, data);
    }

    /*
     * public Object visit(ASTDebuggerStatement node, Object data) { return
     * visitImpl(node, data); }
     */

    public Object visit(ASTBreakStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTReturnStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTWithStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTFunctionDeclaration node, Object data) {
        pre(node, data);
        enteringFunction = node;
        data = node.childrenAccept(this, data);
        post(node, data);
        return data;
    }

    public Object visit(ASTFormalParameterList node, Object data) {
        return visitImpl(node, data);
    }

    /*
     * public Object visit(ASTActiveXReference node, Object data) { return
     * visitImpl(node, data); }
     */

    public Object visit(ASTBlock node, Object data) {
        boolean addedScope = false;
        if ((enteringFunction != null)
                && (node.jjtGetParent() == enteringFunction)) {
            scopes.add(node);
            addedScope = true;
            enteringFunction = null;
        }
        pre(node, data);
        data = node.childrenAccept(this, data);
        post(node, data);
        if (addedScope) {
            scopes.removeLast();
        }
        return data;
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTCaseGroups node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTCaseGroup node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTCaseGuard node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTTryStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTCatchClause node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTFinallyClause node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTThrowStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTDoStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTVariableStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTVariableDeclarationList node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTEmptyStatement node, Object data) {
        return visitImpl(node, data);
    }

    public Object visit(ASTProgram node, Object data) {
        scopes.add(node);
        pre(node, data);
        data = node.childrenAccept(this, data);
        post(node, data);
        scopes.removeLast();
        return data;
    }

}
