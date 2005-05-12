package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.parser.PHPParserConstants;
import gatchan.phpparser.sidekick.FieldAsset;

import javax.swing.*;
import javax.swing.text.Position;

import org.gjt.sp.jedit.GUIUtilities;
import sidekick.Asset;

/**
 * A Field declaration. This is a variable declaration for a php class In fact it's an array of VariableUsage, since a
 * field could contains several var : var $toto,$tata;
 *
 * @author Matthieu Casanova
 */
public final class FieldDeclaration extends Statement implements Outlineable, PHPItem {
  /** The path of the file containing this field. */
  private String path;

  /** The variables. */
  private final VariableDeclaration variable;

  private String nameLowerCase;
  /** The parent do not need to be serialized. */
  private final transient OutlineableWithChildren parent;

  private static transient Icon icon;

  private final int visibility;

  /**
     * Create a field. with public visibility
     *
     * @param path        the path
     * @param variable    the variable of the field
     * @param parent      the parent class
     * @param sourceStart the sourceStart
     * @param sourceEnd   source end
     * @param beginLine   begin line
     * @param endLine     end line
     * @param beginColumn begin column
     * @param endColumn   end column
     */
  public FieldDeclaration(String path,
                          VariableDeclaration variable,
                          OutlineableWithChildren parent,
                          int sourceStart,
                          int sourceEnd,
                          int beginLine,
                          int endLine,
                          int beginColumn,
                          int endColumn) {
    this(PHPParserConstants.PUBLIC,
         path,
         variable,
         parent,
         sourceStart,
         sourceEnd,
         beginLine,
         endLine,
         beginColumn,
         endColumn);
  }

  /**
     * Create a field.
     *
     * @param visibility  the visibility
     * @param path        the path
     * @param variable    the variable of the field
     * @param parent      the parent class
     * @param sourceStart the sourceStart
     * @param sourceEnd   source end
     * @param beginLine   begin line
     * @param endLine     end line
     * @param beginColumn begin column
     * @param endColumn   end column
     */
  public FieldDeclaration(int visibility,
                          String path,
                          VariableDeclaration variable,
                          OutlineableWithChildren parent,
                          int sourceStart,
                          int sourceEnd,
                          int beginLine,
                          int endLine,
                          int beginColumn,
                          int endColumn) {
    super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.path = path;
    this.variable = variable;
    this.parent = parent;
    this.visibility = visibility;
  }

  /**
   * Return the object into String.
   *
   * @param tab how many tabs (not used here
   *
   * @return a String
   */
  public String toString(int tab) {
    StringBuffer buff = new StringBuffer(tabString(tab));
    buff.append("var ");
    buff.append(variable.toStringExpression());
    return buff.toString();
  }


  public OutlineableWithChildren getParent() {
    return parent;
  }

  /**
   * Get the variables from outside (parameters, globals ...)
   *
   * @param list the list where we will put variables
   */
  public void getOutsideVariable(List list) {
  }

  /**
   * get the modified variables.
   *
   * @param list the list where we will put variables
   */
  public void getModifiedVariable(List list) {
  }

  /**
   * Get the variables used.
   *
   * @param list the list where we will put variables
   */
  public void getUsedVariable(List list) {
  }

  public String getName() {
    return variable.getName();
  }

  public String getNameLowerCase() {
    if (nameLowerCase == null) {
      nameLowerCase = variable.getName().toLowerCase();
    }
    return nameLowerCase;
  }

  public String toString() {
    return getName();
  }

  public int getItemType() {
    return FIELD;
  }

  public String getPath() {
    return path;
  }

  public Icon getIcon() {
    if (icon == null) {
      icon = GUIUtilities.loadIcon(ClassHeader.class.getResource("/gatchan/phpparser/icons/field.png").toString());
    }
    return icon;
  }

  /**
   * Returns the visibility of the field.
   *
   * @return the visibility ({@link PHPParserConstants#PUBLIC}, {@link PHPParserConstants#PROTECTED} or {@link PHPParserConstants#PRIVATE})
   */
  public int getVisibility() {
    return visibility;
  }

  public VariableDeclaration getVariable() {
    return variable;
  }

  public Asset getAsset(Position start, Position end) {
    return new FieldAsset(toString(),start, end);
  }
}
