package gatchan.phpparser.parser;

import junit.framework.TestCase;
import net.sourceforge.phpdt.internal.compiler.ast.ForeachStatement;
import net.sourceforge.phpdt.internal.compiler.ast.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * The AST Tester
 *
 * @author Matthieu Casanova
 */
public class ASTTester extends TestCase {

  public void testForeachStatement() {
    //Test with null expressions, variables and statement
    ForeachStatement foreachStatement = new ForeachStatement(null, null, null, 0, 0, 0, 0, 0, 0);
    tryStatement(foreachStatement);
  }

  private void tryStatement(Statement stmt) {
    List l = new ArrayList();
    stmt.getOutsideVariable(l);
    stmt.getModifiedVariable(l);
    stmt.getUsedVariable(l);
  }
}
