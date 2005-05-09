package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * Any expression that have an operator.
 *
 * @author Matthieu Casanova
 */
public abstract class OperatorExpression
        extends Expression
        implements OperatorIds {

  private final int operator;

  protected OperatorExpression(Type type,
                               int operator,
                               int sourceStart,
                               int sourceEnd,
                               int beginLine,
                               int endLine,
                               int beginColumn,
                               int endColumn) {
    super(type, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
    this.operator = operator;
  }

  public final String operatorToString() {
    switch (operator) {
      case EQUAL_EQUAL:
        return "=="; //$NON-NLS-1$
      case LESS_EQUAL:
        return "<="; //$NON-NLS-1$
      case GREATER_EQUAL:
        return ">="; //$NON-NLS-1$
      case NOT_EQUAL:
        return "!="; //$NON-NLS-1$
      case LEFT_SHIFT:
        return "<<"; //$NON-NLS-1$
      case RIGHT_SHIFT:
        return ">>"; //$NON-NLS-1$
      case UNSIGNED_RIGHT_SHIFT:
        return ">>>"; //$NON-NLS-1$
      case OR_OR:
        return "||"; //$NON-NLS-1$
      case AND_AND:
        return "&&"; //$NON-NLS-1$
      case PLUS:
        return "+"; //$NON-NLS-1$
      case MINUS:
        return "-"; //$NON-NLS-1$
      case NOT:
        return "!"; //$NON-NLS-1$
      case REMAINDER:
        return "%"; //$NON-NLS-1$
      case BIT_XOR:
        return "^"; //$NON-NLS-1$
      case AND:
        return "&"; //$NON-NLS-1$
      case MULTIPLY:
        return "*"; //$NON-NLS-1$
      case OR:
        return "|"; //$NON-NLS-1$
      case TWIDDLE:
        return "~"; //$NON-NLS-1$
      case DIVIDE:
        return "/"; //$NON-NLS-1$
      case GREATER:
        return ">"; //$NON-NLS-1$
      case LESS:
        return "<"; //$NON-NLS-1$
      case ORL:
        return "OR"; //$NON-NLS-1$
      case XOR:
        return "XOR"; //$NON-NLS-1$
      case ANDL:
        return "AND"; //$NON-NLS-1$
      case DOT:
        return "."; //$NON-NLS-1$
      case DIF:
        return "<>"; //$NON-NLS-1$
      case BANG_EQUAL_EQUAL:
        return "!=="; //$NON-NLS-1$
      case EQUAL_EQUAL_EQUAL:
        return "==="; //$NON-NLS-1$
      case EQUAL:
        return "="; //$NON-NLS-1$
      case AT:
        return "@"; //$NON-NLS-1$
      case PLUS_PLUS:
        return "++"; //$NON-NLS-1$
      case MINUS_MINUS:
        return "--"; //$NON-NLS-1$
      case NEW:
        return "new "; //$NON-NLS-1$
    }
    return "unknown operator " + operator; //$NON-NLS-1$
  }
}
