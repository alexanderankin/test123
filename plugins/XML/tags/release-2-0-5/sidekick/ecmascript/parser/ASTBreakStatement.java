/* Generated By:JJTree: Do not edit this line. ASTBreakStatement.java */

package sidekick.ecmascript.parser;

public class ASTBreakStatement extends SimpleNode {
  public ASTBreakStatement(int id) {
    super(id);
  }

  public ASTBreakStatement(EcmaScript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(EcmaScriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
