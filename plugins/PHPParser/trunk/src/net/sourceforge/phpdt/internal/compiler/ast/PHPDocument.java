package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;

/**
 * It's a php document.
 * This class is an outlineable object
 * It will contains html and php
 * @author Matthieu Casanova
 */
public final class PHPDocument implements OutlineableWithChildren {

  /**
   * The nodes.
   * It will include html nodes or php nodes
   */
  public AstNode[] nodes;

  private final String name;

  /** The parent of the object. */
  private final Object parent;

  /** The outlineable children (those will be in the node array too. */
  private final ArrayList children = new ArrayList();

  /**
   * Create the PHPDocument.
   * @param parent the parent object (it should be null isn't it ?)
   * @param name the nale f the document
   */
  public PHPDocument(final Object parent,
                     final String name) {
    this.parent = parent;
    this.name = name;
  }

  /**
   * Return the php document as String.
   * @return  a string representation of the object.
   */
  public String toString() {
    final StringBuffer buff = new StringBuffer();
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
          buff.append("\n");//$NON-NLS-1$
        } else {
          buff.append(";\n");//$NON-NLS-1$
        }
      }
    }
    return buff.toString();
  }

  /**
   * Add an outlineable object.
   * @param o the new outlineable
   * @return does the addition worked ?
   */
  public boolean add(final Outlineable o) {
    return children.add(o);
  }

  /**
   * Return the outlineable at the index.
   * @param index the index
   * @return an outlineable object
   */
  public Outlineable get(final int index) {
    return (Outlineable) children.get(index);
  }

  /**
   * The number of outlineable children.
   * @return the number of children that are outlineable
   */
  public int size() {
    return children.size();
  }


  /**
   * Get the parent of the object.
   * @return the parent of the object
   */
  public Object getParent() {
    return parent;
  }

  public List getList() {
    return children;
  }

  /**
   * Analyze the code of a php document.
   *
   * //todo : work on this
   */
  public void analyzeCode() {
    if (nodes != null) {
    }
  }

  public String getName() {
    return name;
  }

  public ClassDeclaration insideWichClassIsThisOffset(int offset) {
    for (int i = 0; i < children.size(); i++) {
      Outlineable outlineable = (Outlineable) children.get(i);
      if (outlineable instanceof ClassDeclaration) {
        ClassDeclaration classDeclaration = (ClassDeclaration) outlineable;
        if (offset < classDeclaration.getBodyEnd() && offset> classDeclaration.getBodyStart())
          return classDeclaration;
      }
    }
    return null;
  }

  public MethodDeclaration insideWichMethodIsThisOffset(int offset) {
    for (int i = 0; i < children.size(); i++) {
      Outlineable outlineable = (Outlineable) children.get(i);
      if (outlineable instanceof MethodDeclaration) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) outlineable;
        if (offset < methodDeclaration.getBodyEnd() && offset> methodDeclaration.getBodyStart())
          return methodDeclaration;
      }
    }
    return null;
  }
}