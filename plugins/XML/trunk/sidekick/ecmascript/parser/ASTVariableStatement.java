/* Generated By:JJTree: Do not edit this line. ASTVariableStatement.java */

package sidekick.ecmascript.parser;

public class ASTVariableStatement extends SimpleNode {
  public ASTVariableStatement(int id) {
    super(id);
  }

  public ASTVariableStatement(EcmaScript p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(EcmaScriptVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public String toString() {
      return jjtGetChild(0).jjtGetChild(0).toString();
  }

  public boolean isVisible() {
      return true;
  }

}
