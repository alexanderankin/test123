package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.project.itemfinder.PHPItem;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import org.gjt.sp.jedit.GUIUtilities;

/**
 * The ClassHeader is that : class ClassName [extends SuperClassName].
 *
 * @author Matthieu Casanova
 */
public class ClassHeader extends AstNode implements PHPItem, Serializable {
  /** The path of the file containing this class. */
  private String path;

  /** The name of the class. */
  private String className;

  private String nameLowerCase;

  /** The name of the superclass. */
  private String superClassName;

  /** The methodsHeaders of the class. */
  private final List methodsHeaders = new ArrayList();

  /** The fields of the class. */
  private final List fields = new ArrayList();

  private static transient Icon icon;

  private transient String cachedToString;

  public ClassHeader() {
  }

  public ClassHeader(String path,
                     String className,
                     String superClassName,
                     int sourceStart,
                     int sourceEnd,
                     int beginLine,
                     int endLine,
                     int beginColumn,
                     int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.path = path;
    this.className = className;
    this.superClassName = superClassName;
  }

  public ClassHeader(String path,
                     String className,
                     int sourceStart,
                     int sourceEnd,
                     int beginLine,
                     int endLine,
                     int beginColumn,
                     int endColumn) {
    this(path, className, null, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
  }

  public String toString(int tab) {
    StringBuffer buff = new StringBuffer(200);
    buff.append(tabString(tab));
    buff.append("class ");
    buff.append(className);
    if (superClassName != null) {
      buff.append(" extends ");
      buff.append(superClassName);
    }
    return buff.toString();
  }

  public String toString() {
    if (cachedToString == null) {
      StringBuffer buff = new StringBuffer(200);
      buff.append(className);
      if (superClassName != null) {
        buff.append(':');
        buff.append(superClassName);
      }
      cachedToString = buff.toString();
    }
    return cachedToString;
  }

  public void getOutsideVariable(List list) {
  }

  public void getModifiedVariable(List list) {
  }

  public void getUsedVariable(List list) {
  }

  /**
   * Returns the name of the class.
   *
   * @return the name of the class
   */
  public String getName() {
    return className;
  }

  public String getNameLowerCase() {
    if (nameLowerCase == null) {
      nameLowerCase = className.toLowerCase();
    }
    return nameLowerCase;
  }

  /**
   * Returns the name of the superclass.
   *
   * @return the name of the superclass
   */
  public String getSuperClassName() {
    return superClassName;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof ClassHeader)) return false;
    return ((ClassHeader) obj).getName().equals(className);
  }

  public String getPath() {
    return path;
  }

  public Icon getIcon() {
    if (icon == null) {
      icon = GUIUtilities.loadIcon(ClassHeader.class.getResource("/gatchan/phpparser/icons/class.png").toString());
    }
    return icon;
  }

  /**
   * Add a method to the class.
   *
   * @param method the method declaration
   */
  public void addMethod(MethodHeader method) {
    methodsHeaders.add(method);
  }

  /**
   * Add a method to the class.
   *
   * @param field the method declaration
   */
  public void addField(FieldDeclaration field) {
    fields.add(field);
  }

  public List getMethodsHeaders() {
    return methodsHeaders;
  }

  public List getFields() {
    return fields;
  }

  public int getItemType() {
    return CLASS;
  }
}
