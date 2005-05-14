package net.sourceforge.phpdt.internal.compiler.ast;

import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;

import java.util.List;
import java.util.ArrayList;

import gatchan.phpparser.project.itemfinder.PHPItem;

import javax.swing.*;
import javax.swing.text.Position;

import org.gjt.sp.jedit.GUIUtilities;
import sidekick.IAsset;

/** @author Matthieu Casanova */
public class InterfaceDeclaration extends Statement implements OutlineableWithChildren, PHPItem, IAsset {
  private final String path;
  private final OutlineableWithChildren parent;
  private final String name;

  private List children = new ArrayList();
  private static transient Icon icon;
  private String nameLowerCase;

  private Position start;
  private Position end;

  public InterfaceDeclaration(String path,
                              OutlineableWithChildren parent,
                              String name,
                              int sourceStart,
                              int beginLine,
                              int beginColumn) {
    this.path = path;
    this.parent = parent;
    this.name = name;
    this.sourceStart = sourceStart;
    this.beginLine = beginLine;
    this.beginColumn = beginColumn;
  }

  public String toString(int tab) {
    return null;
  }

  public String toString() {
    return "interface " + name;
  }

  public void getOutsideVariable(List list) {
  }

  public void getModifiedVariable(List list) {
  }

  public void getUsedVariable(List list) {
  }

  public boolean add(Outlineable o) {
    return children.add(o);
  }

  public Outlineable get(int index) {
    return (Outlineable) children.get(index);
  }

  public int size() {
    return children.size();
  }

  public OutlineableWithChildren getParent() {
    return parent;
  }

  public String getName() {
    return name;
  }

  public String getNameLowerCase() {
    if (nameLowerCase == null) {
      nameLowerCase = name.toLowerCase();
    }
    return nameLowerCase;
  }

  public int getItemType() {
    return INTERFACE;
  }

  public String getPath() {
    return path;
  }

  public Icon getIcon() {
    // todo an interface icon
    if (icon == null) {
      icon = GUIUtilities.loadIcon(ClassHeader.class.getResource("/gatchan/phpparser/icons/class.png").toString());
    }
    return icon;
  }

  public Position getStart() {
    return start;
  }

  public void setStart(Position start) {
    this.start = start;
  }

  public Position getEnd() {
    return end;
  }

  public void setEnd(Position end) {
    this.end = end;
  }

  public String getShortString() {
    return name;
  }

  public String getLongString() {
    return name;
  }

  public void setName(String name) {
  }

  public Expression expressionAt(int line, int column) {
    //todo : fix interface declaration
    return null;
  }
}
