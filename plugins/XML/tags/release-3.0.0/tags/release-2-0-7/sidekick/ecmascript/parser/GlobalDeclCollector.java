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

import sidekick.ecmascript.parser.ASTAssignmentExpression;
import sidekick.ecmascript.parser.ASTExpressionStatement;
import sidekick.ecmascript.parser.ASTForVarInStatement;
import sidekick.ecmascript.parser.ASTForVarStatement;
import sidekick.ecmascript.parser.ASTFormalParameterList;
import sidekick.ecmascript.parser.ASTVariableDeclaration;
import sidekick.ecmascript.parser.EcmaScriptVisitor;

/**
 * Visitor that collects the global identifiers and string literals in the code
 * base and also decorates the function declaration nodes with symbol table
 * information of local variables
 *
 *
 * @since JDK 1.4
 */
public class GlobalDeclCollector extends EcmaScriptVisitorAdapter implements
        EcmaScriptVisitor {

    // globals
    private Set declarations;

    private LinkedList declarationNodes;

    private boolean collectForvarDeclarations;

    // stacks of local variables
    private LinkedList localDeclarations;

    private LinkedList loopDeclarations;

    public GlobalDeclCollector(Set declarations, LinkedList declarationNodes,
            EcmaScriptVisitorDelegate visitorDelegate) {
        super(visitorDelegate);
        this.declarations = declarations;
        this.declarationNodes = declarationNodes;

        localDeclarations = new LinkedList();
        loopDeclarations = new LinkedList();
    }

    public GlobalDeclCollector(Set declarations, LinkedList declarationNodes) {
        this(declarations, declarationNodes, null);
    }

    public GlobalDeclCollector(Set declarations) {
        this(declarations, null);
    }

    private boolean isLocal(String identifierName) {
        ListIterator iter = loopDeclarations.listIterator(loopDeclarations
                .size());

        while (iter.hasPrevious()) {
            Map decls = (Map) iter.previous();

            if (decls.containsKey(identifierName)) {
                return true;
            }
        }

        iter = localDeclarations.listIterator(localDeclarations.size());

        while (iter.hasPrevious()) {
            Map decls = (Map) iter.previous();

            if (decls.containsKey(identifierName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        String name = ((ASTIdentifier) node.jjtGetChild(0)).getName();

        if (collectForvarDeclarations) {
            Map forvarDeclarations = (Map) loopDeclarations.getLast();

            if (!forvarDeclarations.containsKey(name)) {
                forvarDeclarations.put(name, name);
            }
        } else if (localDeclarations.size() > 0) {
            Map functionDecls = (Map) localDeclarations.getLast();

            if (!functionDecls.containsKey(name)) {
                functionDecls.put(name, name);
            }
        } else {
            declarations.add(name);
            if (declarationNodes != null) {
                declarationNodes.add(node);
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        // see if we have an assignment expression with a composite reference on
        // the lhs
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode exprNode = (SimpleNode) node.jjtGetChild(0);

            if ((exprNode instanceof ASTAssignmentExpression)
                    && (exprNode.jjtGetNumChildren() > 0)) {
                SimpleNode lhsNode = (SimpleNode) exprNode.jjtGetChild(0);

                if (lhsNode instanceof ASTCompositeReference) {
                    // determine if it is a global declaration
                    String firstRefPart = null;
                    SimpleNode cNode = (SimpleNode) lhsNode.jjtGetChild(0);

                    if (cNode instanceof ASTIdentifier) {
                        firstRefPart = ((ASTIdentifier) cNode).getName();
                    }

                    if ((firstRefPart != null) && (!isLocal(firstRefPart))) {
                        String compositeName = ((ASTCompositeReference) lhsNode)
                                .getCompositeName();
                        if (compositeName != null) {
                            declarations.add(compositeName);
                            if (declarationNodes != null) {
                                declarationNodes.add(exprNode);
                            }
                        }
                    }
                } else if (lhsNode instanceof ASTIdentifier) {
                    String lhsName = ((ASTIdentifier) lhsNode).getName();

                    if ((lhsName != null) && (!isLocal(lhsName))) {
                        // PENDING(uwe): I cannot really have this as a global
                        // declaration
                        // because I cannot distinguish between an actual
                        // declaration
                        // and an assignment
                        // i.e. location = foo;
                        // in this case it's window.location which would be bad
                        // if I consider it
                        // a declaration (that is something we own and is a
                        // potential target for janitor/jabber)

                        // so I look for the @constructor tag in comment
                        // associated with this assignment
                        // if it exists it's one of ours

                        Comment comment = node.getComment();

                        if ((comment != null)
                                && comment.containsTag("constructor")) {
                            declarations.add(lhsName);
                            if (declarationNodes != null) {
                                declarationNodes.add(exprNode);
                            }
                        }
                    }
                }
            }
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTForVarStatement node, Object data) {
        HashMap forvarDeclarations = new HashMap();
        loopDeclarations.add(forvarDeclarations);

        pre(node, data);
        collectForvarDeclarations = true;
        data = node.jjtGetChild(0).jjtAccept(this, data);
        collectForvarDeclarations = false;

        // it's weird but in javascript loop variables are visible afterwards
        // outside the loop
        // so we dump them into the last local variables map
        if (localDeclarations.size() > 0) {
            Map functionDecls = (Map) localDeclarations.getLast();

            Iterator keyIter = forvarDeclarations.keySet().iterator();

            while (keyIter.hasNext()) {
                String name = (String) keyIter.next();
                if (!functionDecls.containsKey(name)) {
                    functionDecls.put(name, name);
                }
            }
        }

        int n = node.jjtGetNumChildren();
        for (int i = 1; i < n; i++) {
            data = node.jjtGetChild(i).jjtAccept(this, data);
        }
        loopDeclarations.removeLast();
        post(node, data);
        return data;
    }

    @Override
    public Object visit(ASTForVarInStatement node, Object data) {
        HashMap forvarInDecls = new HashMap();
        String forVarInName = ((ASTIdentifier) node.jjtGetChild(0)).getName();
        forvarInDecls.put(forVarInName, forVarInName);

        // it's weird but in javascript loop variables are visible afterwards
        // outside the loop
        // so we dump them into the last local variables map
        if (localDeclarations.size() > 0) {
            Map functionDecls = (Map) localDeclarations.getLast();

            if (!functionDecls.containsKey(forVarInName)) {
                functionDecls.put(forVarInName, forVarInName);
            }
        }

        loopDeclarations.add(forvarInDecls);
        data = super.visit(node, data);
        loopDeclarations.removeLast();
        return data;
    }

    @Override
    public Object visit(ASTFunctionDeclaration node, Object data) {
        int index = 0;
        pre(node, data);
        if (node.jjtGetNumChildren() == 3) {
            SimpleNode firstChild = (SimpleNode) node.jjtGetChild(0);
            String name = null;

            if (firstChild instanceof ASTIdentifier) {
                name = ((ASTIdentifier) firstChild).getName();
            } else {
                name = ((ASTIdentifier) firstChild.jjtGetChild(1)).getName();
            }
            // if scope is global record it as a global declaration
            if (getScope() instanceof ASTProgram) {
                declarations.add(name);
                if (declarationNodes != null) {
                    declarationNodes.add(node);
                }
            }

            // visit function name identifier
            data = node.jjtGetChild(0).jjtAccept(this, data);
            index = 1;
        }

        ASTFormalParameterList paramList = (ASTFormalParameterList) node
                .jjtGetChild(index);
        ASTBlock body = (ASTBlock) node.jjtGetChild(index + 1);

        HashMap functionVars = new HashMap();
        if (paramList.jjtGetNumChildren() > 0) {
            int n = paramList.jjtGetNumChildren();

            for (int i = 0; i < n; i++) {
                ASTIdentifier param = (ASTIdentifier) paramList.jjtGetChild(i);
                String paramName = param.getName();

                functionVars.put(paramName, paramName);
            }
        }

        localDeclarations.add(functionVars);
        data = body.jjtAccept(this, data);

        // here we decorate the function declaration node
        // with all the local variables visible to it
        node.setLocals(localDeclarations);

        localDeclarations.removeLast();
        post(node, data);
        return data;
    }

    public Object visit(ASTRequireStatement node, Object data) {
        return data;
    }
}
