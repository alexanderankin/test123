package net.sourceforge.phpdt.internal.compiler.ast;

import java.io.Serializable;

/**
 * The php types.
 *
 * @author Matthieu Casanova
 */
public class Type implements Serializable {

  public static final int UNKNOWN_INT = 0;
  public static final int BOOLEAN_INT = 1;
  public static final int REAL_INT = 2;
  public static final int DOUBLE_INT = 3;
  public static final int FLOAT_INT = 4;
  public static final int INTEGER_INT = 5;
  public static final int OBJECT_INT = 6;
  public static final int NULL_INT = 7;
  public static final int STRING_INT = 8;
  public static final int ARRAY_INT = 8;

  private final int type;

  public static final Type UNKNOWN = new Type(UNKNOWN_INT);
  public static final Type BOOLEAN = new Type(BOOLEAN_INT);
  public static final Type REAL = new Type(REAL_INT);
  public static final Type DOUBLE = new Type(DOUBLE_INT);
  public static final Type FLOAT = new Type(FLOAT_INT);
  public static final Type INTEGER = new Type(INTEGER_INT);
  public static final Type OBJECT = new Type(OBJECT_INT);
  public static final Type NULL = new Type(NULL_INT);
  public static final Type STRING = new Type(STRING_INT);
  public static final Type ARRAY = new Type(ARRAY_INT);

  private final String className;

  public Type(int type) {
    this(type,null);
  }

  public Type(int type, String className) {
    this.type = type;
    this.className = className;
  }

  public int getType() {
    return type;
  }

  public String getClassName() {
    return className;
  }
}
