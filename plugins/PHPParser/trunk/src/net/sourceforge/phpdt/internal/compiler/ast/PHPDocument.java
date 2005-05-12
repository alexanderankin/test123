package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;

/**
 * It's a php document. This class is an outlineable object It will contains html and php
 *
 * @author Matthieu Casanova
 */
public final class PHPDocument implements OutlineableWithChildren {
  /** The nodes. It will include html nodes or php nodes */
  private AstNode[] nodes;

  private final String name;

  /** The outlineable children (those will be in the node array too. */
  private final List children = new ArrayList();

  /**
   * Create the PHPDocument.
   *
   * @param name the nale f the document
   */
  public PHPDocument(String name) {
    this.name = name;
  }

  /**
   * Return the php document as String.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    StringBuffer buff = new StringBuffer();
    AstNode node;
    if (nodes != null) {
      int i;
      for (i = 0; i < nodes.length; i++) {
        node = nodes[i];
        if (node == null) {
          break;
        }
        buff.append(node.toString(0));
        if (node instanceof HTMLCode) {
          buff.append('\n');//$NON-NLS-1$
        } else {
          buff.append(";\n");//$NON-NLS-1$
        }
      }
    }
    return buff.toString();
  }

  /**
   * Add an outlineable object.
   *
   * @param o the new outlineable
   *
   * @return does the addition worked ?
   */
  public boolean add(Outlineable o) {
    return children.add(o);
  }

  /**
   * Return the outlineable at the index.
   *
   * @param index the index
   *
   * @return an outlineable object
   */
  public Outlineable get(int index) {
    return (Outlineable) children.get(index);
  }

  /**
   * The number of outlineable children.
   *
   * @return the number of children that are outlineable
   */
  public int size() {
    return children.size();
  }


  /**
   * Get the parent of the object.
   *
   * @return null
   */
  public OutlineableWithChildren getParent() {
    return null;
  }

  /**
   * Analyze the code of a php document.
   * <p/>
   * //todo : work on this
   */
  public void analyzeCode() {
    if (nodes != null) {
    }
  }

  public String getName() {
    return name;
  }

  public ClassDeclaration insideWichClassIsThisOffset(int line, int column) {
    for (int i = 0; i < children.size(); i++) {
      Outlineable outlineable = (Outlineable) children.get(i);
      if (outlineable instanceof ClassDeclaration) {
        ClassDeclaration classDeclaration = (ClassDeclaration) outlineable;
        if (line == classDeclaration.getBodyLineStart() && column > classDeclaration.getBodyColumnStart()) return classDeclaration;
        if (line == classDeclaration.getBodyLineEnd() && column < classDeclaration.getBodyColumnEnd()) return classDeclaration;
        if (line > classDeclaration.getBodyLineStart() && line < classDeclaration.getBodyLineEnd()) return classDeclaration;
      }
    }
    return null;
  }

  public MethodDeclaration insideWichMethodIsThisOffset(int line, int column) {
    for (int i = 0; i < children.size(); i++) {
      Outlineable outlineable = (Outlineable) children.get(i);
      if (outlineable instanceof MethodDeclaration) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) outlineable;
        if (line == methodDeclaration.getBodyLineStart() && column > methodDeclaration.getBodyColumnStart()) return methodDeclaration;
        if (line == methodDeclaration.getBodyLineEnd() && column < methodDeclaration.getBodyColumnEnd()) return methodDeclaration;
        if (line > methodDeclaration.getBodyLineStart() && line < methodDeclaration.getBodyLineEnd()) return methodDeclaration;
      }
    }
    return null;
  }

  public void setNodes(AstNode[] nodes) {
    this.nodes = nodes;
  }
}