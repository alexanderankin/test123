package gatchan.phpparser.sidekick;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.PHPErrorSource;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.ParseException;
import gatchan.phpparser.parser.ParsingAbortedError;
import net.sourceforge.phpdt.internal.compiler.ast.AstNode;
import net.sourceforge.phpdt.internal.compiler.ast.ClassDeclaration;
import net.sourceforge.phpdt.internal.compiler.ast.MethodDeclaration;
import net.sourceforge.phpdt.internal.compiler.ast.PHPDocument;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import sidekick.SideKickCompletion;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

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
    final String word = getCurrentWord(caret, buffer);
    final String lastWord = getPreviousWord(caret, buffer);
    final ClassDeclaration currentClass = phpDocument.insideWichClassIsThisOffset(caret);
    final MethodDeclaration currentMethod;
    if (currentClass == null) {
      currentMethod = phpDocument.insideWichMethodIsThisOffset(caret);
    } else {
      currentMethod = currentClass.insideWichMethodIsThisOffset(caret);
      if (currentMethod != null) {
        if (lastWord.startsWith("$this->")) {
          return completeClassMembers(editPane.getTextArea(), phpDocument, caret, word,lastWord);
        }
      } else {
        if (lastWord.endsWith(";") || lastWord.endsWith("{") || lastWord.endsWith("}")) {
          PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(editPane.getTextArea(),
                                                                                  word,
                                                                                  lastWord);
          phpSideKickCompletion.addItem("var", word);
          phpSideKickCompletion.addItem("function", word);
          return phpSideKickCompletion;
        }
      }
    }

    // Log.log(Log.DEBUG, PHPSideKickParser.class, "Word found : '"+lastWord+"'");
    if ("new".equals(lastWord) || "extends".equals(lastWord)) {
      return completeClassDeclaration(editPane.getTextArea(), phpDocument, word,lastWord);
    }

    return null;
  }

  /**
   * Returns the current word in a buffer.
   *
   * @param caret  the caret position
   * @param buffer the buffer
   *
   * @return the current word or ""
   */
  private String getCurrentWord(final int caret, final Buffer buffer) {
    int i;
    char c = buffer.getText(caret - 1, 1).charAt(0);
    if (Character.isLetterOrDigit(c)) {
      int beginLine = buffer.getLineStartOffset(buffer.getLineOfOffset(caret));
      String line = buffer.getText(beginLine, caret - beginLine);
      for (i = caret - 1; i >= beginLine; i--) {
        c = line.charAt(i - beginLine);
        if (!Character.isLetterOrDigit(c)) break;
      }
      final String word = line.substring(i - beginLine + 1, caret - beginLine);
      return word;
    }
    return "";
  }

  /**
   * Returns the previous word in a buffer.
   *
   * @param caret  the caret position
   * @param buffer the buffer
   *
   * @return the previous word or ""
   */
  private static String getPreviousWord(final int caret, final Buffer buffer) {
    int i;
    for (i = caret - 1; i > 0; i--) {
      char c = buffer.getText(i, 1).charAt(0);
      if (!Character.isWhitespace(c)) break;
    }
    int j;
    for (j = i - 1; j > 0; j--) {
      char c = buffer.getText(j, 1).charAt(0);
      if (Character.isWhitespace(c)) break;
    }
    final String word = buffer.getText(j + 1, i - j);
    return word;
  }

  /**
   * Build the completion list to follow a 'new'. It will contains classes name
   *
   * @param textArea    the current textArea
   * @param phpDocument the phpDocument
   * @param word
   *
   * @return a completion list
   */
  private static SideKickCompletion completeClassDeclaration(final JEditTextArea textArea,
                                                             final PHPDocument phpDocument,
                                                             String word,
                                                             String lastWord) {
    final PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea, word, lastWord);
    final List children = phpDocument.getList();
    for (int k = 0; k < children.size(); k++) {
      final Object o = children.get(k);
      if (o instanceof ClassDeclaration) {
        phpSideKickCompletion.addItem(o, word);
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
   * @param word
   *
   * @return a completion list or null if we aren't in a class
   */
  private static SideKickCompletion completeClassMembers(final JEditTextArea textArea,
                                                         final PHPDocument phpDocument,
                                                         final int caret, String word,
                                                         String lastWord) {
    final ClassDeclaration classDeclaration = phpDocument.insideWichClassIsThisOffset(caret);
    if (classDeclaration == null) return null;
    final PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea, word, lastWord);
    final List methods = classDeclaration.getMethods();
    final List fields = classDeclaration.getFields();
    phpSideKickCompletion.addOutlineableList(methods, word);
    phpSideKickCompletion.addOutlineableList(fields, word);
    return phpSideKickCompletion;
  }
}
