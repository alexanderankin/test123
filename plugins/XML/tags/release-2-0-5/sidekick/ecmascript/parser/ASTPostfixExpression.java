/* Generated By:JJTree: Do not edit this line. ASTPostfixExpression.java */

package sidekick.ecmascript.parser;

public class ASTPostfixExpression extends SimpleNode {
  public ASTPostfixExpression(int id) {
    super(id);
  }

  public ASTPostfixExpression(EcmaScript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(EcmaScriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
