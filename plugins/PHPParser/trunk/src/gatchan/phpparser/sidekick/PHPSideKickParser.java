package gatchan.phpparser.sidekick;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.PHPErrorSource;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.ParseException;
import gatchan.phpparser.parser.ParsingAbortedError;
import gatchan.phpparser.project.AbstractProject;
import gatchan.phpparser.project.PHPProjectChangedMessage;
import gatchan.phpparser.project.ProjectManager;
import net.sourceforge.phpdt.internal.compiler.ast.*;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import sidekick.SideKickCompletion;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Reader;
import java.util.*;

/**
 * My sidekick implementation of the sidekick parser.
 *
 * @author Matthieu Casanova
 */
public final class PHPSideKickParser extends SideKickParser {
  private PHPParser parser;
  private final ProjectManager projectManager;
  private final PHPErrorSource phpErrorSource = new PHPErrorSource();

  /**
   * Instantiate the PHPSideKickParser.
   *
   * @param name the name of the parser
   */
  public PHPSideKickParser(String name) {
    super(name);
    projectManager = ProjectManager.getInstance();
  }

  public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
    phpErrorSource.setErrorSource(errorSource);
    final String path = buffer.getPath();
    projectManager.getProject().clearSourceFile(path);
    if (parser != null && !parser.isStopped()) {
      Log.log(Log.ERROR, PHPSideKickParser.class, "The parser had not been stopped before asking a new parse !");
      stop();
    }
    parser = new PHPParser();
    parser.setPath(path);
    try {
      errorSource.removeFileErrors(path);
      parser.addParserListener(phpErrorSource);
      try {
        parser.parseInfo(null, buffer.getText(0, buffer.getLength()));
      } catch (ParsingAbortedError parsingAbortedError) {
        Log.log(Log.MESSAGE, PHPSideKickParser.class, "The parser was aborted");
        return null;
      }
      final PHPDocument phpDocument = parser.getPHPDocument();
      updateProject(phpDocument, path);
      parser = null;
      final SideKickParsedData data = new SideKickParsedData(buffer.getName());

      buildChildNodes(data.root, phpDocument, buffer);
      buffer.setProperty("PHPDocument", phpDocument);
      EditBus.send(new PHPProjectChangedMessage(this, projectManager.getProject()));
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

  public void parse(String path, Reader data) {
    PHPParser parser = new PHPParser();
    parser.setPath(path);
    try {
      parser.parseInfo(null, data);
    } catch (ParsingAbortedError parsingAbortedError) {
      Log.log(Log.MESSAGE, PHPSideKickParser.class, "The parser was aborted");
    } catch (ParseException e) {
      Log.log(Log.MESSAGE, PHPSideKickParser.class, e.getMessage());
    }
    final PHPDocument phpDocument = parser.getPHPDocument();
    updateProject(phpDocument, path);
  }

  public void stop() {
    if (parser != null) {
      Log.log(Log.MESSAGE, PHPSideKickParser.class, "Stopping parser");
      parser.stop();
    }
  }


  private void buildChildNodes(DefaultMutableTreeNode parent,
                               OutlineableWithChildren phpDocument,
                               Buffer buffer) {
    final List list = phpDocument.getList();
    for (int i = 0; i < list.size(); i++) {
      final Outlineable o = (Outlineable) list.get(i);
      buildNode(parent, o, buffer);
    }
  }


  private void buildNode(DefaultMutableTreeNode parent,
                         Outlineable sourceNode,
                         Buffer buffer) {
    final AstNode astNode = (AstNode) sourceNode;
    final Position position = buffer.createPosition(astNode.sourceStart);
    final PHPAsset asset;
    if (astNode instanceof ClassDeclaration) {
      asset = new ClassAsset(astNode.toString(), position, buffer.createPosition(astNode.sourceEnd));
    } else if (astNode instanceof MethodDeclaration) {
      asset = new MethodAsset(astNode.toString(), position, buffer.createPosition(astNode.sourceEnd));
    } else if (astNode instanceof VariableDeclaration) {
      asset = new FieldAsset(astNode.toString(), position, buffer.createPosition(astNode.sourceEnd));
    } else if (astNode instanceof InclusionStatement) {
      asset = new IncludeAsset(astNode.toString(), position, buffer.createPosition(astNode.sourceEnd));
    } else {
      asset = new PHPAsset(astNode.toString(), position, buffer.createPosition(astNode.sourceEnd));
    }
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
    Log.log(Log.DEBUG, this, "Requesting sidekick complete");
    final Buffer buffer = editPane.getBuffer();
    final PHPDocument phpDocument = (PHPDocument) buffer.getProperty("PHPDocument");
    if (phpDocument == null) {
      Log.log(Log.DEBUG, this, "No php document for this buffer");
      return null;
    }
    JEditTextArea textArea = editPane.getTextArea();
    String line = buffer.getLineText(textArea.getCaretLine());
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
          Log.log(Log.DEBUG, this, "Completing $this->");
          return completeClassMembers(editPane.getTextArea(), phpDocument, caret, word, lastWord);
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
      Log.log(Log.DEBUG, this, "Completing class name");
      return completeClassDeclaration(editPane.getTextArea(), phpDocument, word, lastWord);
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
  private static String getCurrentWord(int caret, Buffer buffer) {
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
  private static String getPreviousWord(int caret, Buffer buffer) {
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
  private SideKickCompletion completeClassDeclaration(JEditTextArea textArea,
                                                      PHPDocument phpDocument,
                                                      String word,
                                                      String lastWord) {
    final PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea, word, lastWord);
    AbstractProject project = projectManager.getProject();

    final Map classes = project.getClasses();
    Collection collection = classes.values();
    for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
      Object o = iterator.next();
      phpSideKickCompletion.addItem(o, word);
    }
    Log.log(Log.DEBUG, this, "Items in list : " + phpSideKickCompletion.getItemsCount());
    /*final List children = phpDocument.getList();
    for (int k = 0; k < children.size(); k++) {
      final Object o = children.get(k);
      if (o instanceof ClassDeclaration) {
        phpSideKickCompletion.addItem(o, word);
      }
    } */
    return phpSideKickCompletion;
  }

  private void updateProject(PHPDocument phpDocument, String path) {
    AbstractProject project = projectManager.getProject();
    if (project != null) {
      if (project.acceptFile(path)) {
        for (int i = 0; i < phpDocument.getList().size(); i++) {
          Object o = phpDocument.getList().get(i);
          if (o instanceof ClassDeclaration) {
            ClassDeclaration classDeclaration = (ClassDeclaration) o;
            project.addClass(classDeclaration.getClassHeader());
          } else if (o instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) o;
            project.addMethod(methodDeclaration.getMethodHeader());
          }
        }
      }
    }
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
  private static SideKickCompletion completeClassMembers(JEditTextArea textArea,
                                                         PHPDocument phpDocument,
                                                         int caret, String word,
                                                         String lastWord) {
    final ClassDeclaration classDeclaration = phpDocument.insideWichClassIsThisOffset(caret);
    if (classDeclaration == null) return null;
    final PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea, word, lastWord);
    final List methods = classDeclaration.getClassHeader().getMethodsHeaders();
    final List fields = classDeclaration.getClassHeader().getFields();
    phpSideKickCompletion.addOutlineableList(methods, word);
    phpSideKickCompletion.addOutlineableList(fields, word);
    Log.log(Log.DEBUG, PHPSideKickParser.class, "Items in list : " + phpSideKickCompletion.getItemsCount());
    return phpSideKickCompletion;
  }
}
