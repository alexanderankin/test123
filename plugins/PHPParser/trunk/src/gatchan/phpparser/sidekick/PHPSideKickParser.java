package gatchan.phpparser.sidekick;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.PHPErrorSource;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.ParseException;
import gatchan.phpparser.parser.ParsingAbortedError;
//import gatchan.phpparser.parser.ParsingAbortedError;
import net.sourceforge.phpdt.internal.compiler.ast.AstNode;
import net.sourceforge.phpdt.internal.compiler.ast.PHPDocument;
import net.sourceforge.phpdt.internal.compiler.ast.ClassDeclaration;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.SideKickCompletion;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * My sidekick implementation of the sidekick parser.
 *
 * @author Matthieu Casanova
 */
public final class PHPSideKickParser extends SideKickParser {
  private PHPParser parser;

  /**
   * Instantiate the PHPSideKickParser.
   *
   * @param name the name of the parser
   */
  public PHPSideKickParser(final String name) {
    super(name);
  }

  public SideKickParsedData parse(final Buffer buffer, final DefaultErrorSource errorSource) {
    final String path = buffer.getPath();
    try {
      if (parser != null && !parser.isStopped()) {
        Log.log(Log.ERROR, PHPSideKickParser.class, "The parser had not been stopped before asking a new parse !");
        stop();
      }
      parser = new PHPParser();
      parser.setPath(path);
      final PHPErrorSource phpErrorSource = new PHPErrorSource(errorSource);
      errorSource.removeFileErrors(path);
      parser.addParserListener(phpErrorSource);
      try {
        parser.parseInfo(null, buffer.getText(0, buffer.getLength()));
      } catch (ParsingAbortedError parsingAbortedError) {
        Log.log(Log.MESSAGE, PHPSideKickParser.class, "The parser was aborted");
        return null;
      }
      final PHPDocument phpDocument = parser.getPHPDocument();
      parser = null;
      final SideKickParsedData data = new SideKickParsedData(buffer.getName());

      buildChildNodes(data.root, phpDocument, buffer);
      buffer.setProperty("PHPDocument",phpDocument);
      return data;


    } catch (ParseException e) {
      Log.log(Log.ERROR, this, e);
      errorSource.addError(ErrorSource.ERROR,
                           path,
                           e.currentToken.beginLine - 1,
                           e.currentToken.beginColumn,
                           e.currentToken.endColumn,
                           "Unhandled error please report the bug (with the trace in the activity log");
    }
    return null;
  }

  public void stop() {
    if (parser != null) {
      Log.log(Log.MESSAGE, PHPSideKickParser.class, "Stopping parser");
      parser.stop();
    }
  }


  private void buildChildNodes(final DefaultMutableTreeNode parent,
                               final OutlineableWithChildren phpDocument,
                               final Buffer buffer) {
    final List list = phpDocument.getList();
    for (int i = 0; i < list.size(); i++) {
      final Outlineable o = (Outlineable) list.get(i);
      buildNode(parent, o, buffer);
    }
  }


  private void buildNode(final DefaultMutableTreeNode parent,
                         final Outlineable sourceNode,
                         final Buffer buffer) {
    final AstNode astNode = (AstNode) sourceNode;
    final Position position = buffer.createPosition(astNode.sourceStart);
    final PHPAsset asset = new PHPAsset("php", astNode.toString(), position, buffer.createPosition(astNode.sourceEnd));
    final DefaultMutableTreeNode node = new DefaultMutableTreeNode(asset, true);
    if (sourceNode instanceof OutlineableWithChildren) {
      buildChildNodes(node, (OutlineableWithChildren) sourceNode, buffer);
    }
    parent.add(node);
  }

  public boolean supportsCompletion() {
    return true;
  }

  public SideKickCompletion complete(EditPane editPane, int caret) {
    final Buffer buffer = editPane.getBuffer();
    final PHPDocument phpDocument = (PHPDocument) buffer.getProperty("PHPDocument");
    if (phpDocument == null) {
      return null;
    }
    int i;
    for (i = caret-1;i>0;i--) {
      String text = buffer.getText(i, 1);
      if (!" ".equals(text) && !"\n".equals(text) && !"\t".equals(text)) break;
    }
    i++;
    int j;
    for (j = i-1;j>0;j--) {
      String text = buffer.getText(j, 1);
      if (" ".equals(text) || "\n".equals(text) || "\t".equals(text)) break;
    }
    String word = buffer.getText(j+1,i-j-1);
   // Log.log(Log.DEBUG, PHPSideKickParser.class, "Word found : '"+word+"'");
    if ("new".equals(word)) {
       return completeClassDeclaration(editPane.getTextArea(), phpDocument);
    }
    if ("$this->".equals(word)) {
      return completeClassMembers(editPane.getTextArea(), phpDocument,caret);
    }

    return null;
  }

  private SideKickCompletion completeClassDeclaration(JEditTextArea textArea, final PHPDocument phpDocument) {
    PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea);
    final List children = phpDocument.getList();
    for (int k = 0; k < children.size(); k++) {
      Object o = children.get(k);
      if (o instanceof ClassDeclaration) {
        phpSideKickCompletion.addItem((Outlineable) o);
      }
    }
    return phpSideKickCompletion;
  }

  private SideKickCompletion completeClassMembers(JEditTextArea textArea, final PHPDocument phpDocument, int caret) {
    ClassDeclaration classDeclaration = phpDocument.insideWichClassIsThisOffset(caret);
    if (classDeclaration == null) return null;
    PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea);
    List methods = classDeclaration.getMethods();
    List fields = classDeclaration.getFields();
    phpSideKickCompletion.addOutlineableList(methods);
    phpSideKickCompletion.addOutlineableList(fields);
    return phpSideKickCompletion;
  }
}
