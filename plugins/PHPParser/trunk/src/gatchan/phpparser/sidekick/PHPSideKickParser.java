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
import net.sourceforge.phpdt.internal.compiler.ast.MethodDeclaration;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
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
      buffer.setProperty("PHPDocument", phpDocument);
      return data;


    } catch (ParseException e) {
      parser = null;
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

  public SideKickCompletion complete(final EditPane editPane, final int caret) {
    final Buffer buffer = editPane.getBuffer();
    final PHPDocument phpDocument = (PHPDocument) buffer.getProperty("PHPDocument");
    if (phpDocument == null) {
      return null;
    }
    final String word = getPreviousWord(caret, buffer);
    final ClassDeclaration currentClass = phpDocument.insideWichClassIsThisOffset(caret);
    final MethodDeclaration currentMethod;
    if (currentClass == null) {
      currentMethod = phpDocument.insideWichMethodIsThisOffset(caret);
    } else {
      currentMethod = currentClass.insideWichMethodIsThisOffset(caret);
    }
    if (currentClass != null) {
      if (currentMethod != null) {
        if ("$this->".equals(word)) {
          return completeClassMembers(editPane.getTextArea(), phpDocument, caret);
        }
      } else {
        if (word.endsWith(";") || word.endsWith("{") || word.endsWith("}")) {
          PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(editPane.getTextArea());
          phpSideKickCompletion.addItem("var");
          phpSideKickCompletion.addItem("function");
          return phpSideKickCompletion;
        }
      }

    }
    // Log.log(Log.DEBUG, PHPSideKickParser.class, "Word found : '"+word+"'");
    if ("new".equals(word)) {
      return completeClassDeclaration(editPane.getTextArea(), phpDocument);
    }

    return null;
  }

  private static String getPreviousWord(final int caret, final Buffer buffer) {
    int i;
    for (i = caret - 1; i > 0; i--) {
      final String text = buffer.getText(i, 1);
      if (!" ".equals(text) && !"\n".equals(text) && !"\t".equals(text)) break;
    }
    i++;
    int j;
    for (j = i - 1; j > 0; j--) {
      final String text = buffer.getText(j, 1);
      if (" ".equals(text) || "\n".equals(text) || "\t".equals(text)) break;
    }
    final String word = buffer.getText(j + 1, i - j - 1);
    return word;
  }

  /**
   * Build the completion list to follow a 'new'. It will contains classes name
   *
   * @param textArea    the current textArea
   * @param phpDocument the phpDocument
   *
   * @return a completion list
   */
  private static SideKickCompletion completeClassDeclaration(final JEditTextArea textArea,
                                                             final PHPDocument phpDocument) {
    final PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea);
    final List children = phpDocument.getList();
    for (int k = 0; k < children.size(); k++) {
      final Object o = children.get(k);
      if (o instanceof ClassDeclaration) {
        phpSideKickCompletion.addItem((Outlineable) o);
      }
    }
    return phpSideKickCompletion;
  }

  /**
   * Build the completion list to follow a '$this->'. It will contains fields and methods
   *
   * @param textArea    the current textArea
   * @param phpDocument the phpDocument
   * @param caret       the caret position
   *
   * @return a completion list or null if we aren't in a class
   */
  private static SideKickCompletion completeClassMembers(final JEditTextArea textArea,
                                                         final PHPDocument phpDocument,
                                                         final int caret) {
    final ClassDeclaration classDeclaration = phpDocument.insideWichClassIsThisOffset(caret);
    if (classDeclaration == null) return null;
    final PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea);
    final List methods = classDeclaration.getMethods();
    final List fields = classDeclaration.getFields();
    phpSideKickCompletion.addOutlineableList(methods);
    phpSideKickCompletion.addOutlineableList(fields);
    return phpSideKickCompletion;
  }
}
