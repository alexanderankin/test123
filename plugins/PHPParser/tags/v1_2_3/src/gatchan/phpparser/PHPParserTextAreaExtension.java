package gatchan.phpparser;

import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import net.sourceforge.phpdt.internal.compiler.ast.PHPDocument;
import net.sourceforge.phpdt.internal.compiler.ast.Statement;
import net.sourceforge.phpdt.internal.compiler.ast.Expression;

/**
 * @author Matthieu Casanova
 */
public class PHPParserTextAreaExtension extends TextAreaExtension {
  private final JEditTextArea textArea;

  public PHPParserTextAreaExtension(JEditTextArea textArea) {
    this.textArea = textArea;
  }

  public String getToolTipText(int x, int y) {
    PHPDocument phpDocument = (PHPDocument) textArea.getBuffer().getProperty("PHPDocument");
    if (phpDocument == null) {
      return null;
    }
    int offset = textArea.xyToOffset(x, y);
    if (offset == -1) {
      return null;
    }
    int line = textArea.getLineOfOffset(offset);
    int column = offset - textArea.getLineStartOffset(line);
    Statement statement = phpDocument.getStatementAt(line+1, column);
    if (statement != null) {
      Expression expression = statement.expressionAt(line+1, column);
      if (expression != null) {
        return expression.getType().toString();
      }
    }
    return null;
  }
}
