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
import org.gjt.sp.jedit.TextUtilities;
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

  /**
   * Parses the given text and returns a tree model. This method is called by the sidekick plugin
   *
   * @param buffer      The buffer to parse.
   * @param errorSource An error source to add errors to.
   *
   * @return A new instance of the <code>SideKickParsedData</code> class.
   */
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
        parser.parse(buffer.getText(0, buffer.getLength()));
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

  public void parse(String path, Reader reader) {
    final PHPParser parser = new PHPParser();
    parser.setPath(path);
    try {
      parser.parse(reader);
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
    final JEditTextArea textArea = editPane.getTextArea();
    final int caretLine = textArea.getCaretLine();
    final String line = buffer.getLineText(caretLine);
    final int caretInLine = caret - buffer.getLineStartOffset(caretLine);
    final int wordStart = TextUtilities.findWordStart(line, caretInLine - 1, "");
    final String currentWord = line.substring(wordStart, caretInLine);

    final AbstractProject project = projectManager.getProject();

    //Static class access
    PHPSideKickCompletion phpSideKickCompletion = null;

    phpSideKickCompletion = completeStaticClassAccess(currentWord, line, wordStart, project, textArea);
    if (phpSideKickCompletion != null) return phpSideKickCompletion;

    final ClassDeclaration currentClass = phpDocument.insideWichClassIsThisOffset(caret);
    final MethodDeclaration currentMethod;


    final String lastWord2 = getPreviousWord(caret, buffer);


    if (currentClass == null) {
      //We are not inside a class
      currentMethod = phpDocument.insideWichMethodIsThisOffset(caret);
    } else {
      //We are inside a class
      currentMethod = currentClass.insideWichMethodIsThisOffset(caret);


      if (currentMethod == null) {
        //We are inside a class but not inside a method
        if (lastWord2.endsWith(";") || lastWord2.endsWith("{") || lastWord2.endsWith("}")) {
          phpSideKickCompletion = new PHPSideKickCompletion(textArea, currentWord, lastWord2);
          phpSideKickCompletion.addItem("var", currentWord);
          phpSideKickCompletion.addItem("function", currentWord);
          return phpSideKickCompletion;
        }
      } else {
        String lastWord = null;
        if (wordStart != 0) {
          int previousWordStart = TextUtilities.findWordStart(line, wordStart - 1, "$_->");
          lastWord = line.substring(previousWordStart, wordStart);
        }
        //We are inside a method of a class
        if ("$this->".equals(lastWord) || ("->".equals(currentWord) && "$this".equals(lastWord))) {
          Log.log(Log.DEBUG, this, "Completing $this->");
          if ("->".equals(currentWord)) {
            phpSideKickCompletion = completeClassMembers(textArea, currentClass.getClassHeader(), "", "$this->");
          } else {
            phpSideKickCompletion = completeClassMembers(textArea,
                                                         currentClass.getClassHeader(),
                                                         currentWord,
                                                         "$this->");
          }
          return phpSideKickCompletion;
        }
      }
    }

    // Log.log(Log.DEBUG, PHPSideKickParser.class, "Word found : '"+lastWord2+"'");
    if ("new".equals(lastWord2) || "extends".equals(lastWord2)) {
      Log.log(Log.DEBUG, this, "Completing class name");
      return completeClassDeclaration(textArea, currentWord, lastWord2);
    }

    return null;
  }

  private PHPSideKickCompletion completeStaticClassAccess(String currentWord,
                                                          String line,
                                                          int wordStart,
                                                          AbstractProject project,
                                                          JEditTextArea textArea) {
    PHPSideKickCompletion phpSideKickCompletion = null;
    if ("::".equals(currentWord)) {
      final int previousWordStart = TextUtilities.findWordStart(line, wordStart - 1, "_");
      final String classAccessed = line.substring(previousWordStart, wordStart);
      final ClassHeader classHeader = project.getClass(classAccessed);
      phpSideKickCompletion = completeClassMembers(textArea, classHeader, "", classAccessed + "::");
    } else if (wordStart > 2 && (line.charAt(wordStart - 1) == ':' || line.charAt(wordStart - 2) == ':')) {
      final int previousWordStart = TextUtilities.findWordStart(line, wordStart - 3, "_");
      final String classAccessed = line.substring(previousWordStart, wordStart - 2);
      final ClassHeader classHeader = project.getClass(classAccessed);
      phpSideKickCompletion = completeClassMembers(textArea, classHeader, currentWord, classAccessed + "::");
    }
    return phpSideKickCompletion;
  }

  /* private void addKeywords(Buffer buffer,
                           int caret,
                           PHPSideKickCompletion sideKickCompletion,
                           String currentWord) {
    String[] keywords = buffer.getKeywordMapAtOffset(caret).getKeywords();
    for (int i = 0; i < keywords.length; i++) {
      String keyword = keywords[i];
      sideKickCompletion.addItem(keyword,currentWord);
    }
  }  */

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
      final char c = buffer.getText(i, 1).charAt(0);
      if (!Character.isWhitespace(c)) break;
    }
    int j;
    for (j = i - 1; j > 0; j--) {
      final char c = buffer.getText(j, 1).charAt(0);
      if (Character.isWhitespace(c)) break;
    }
    final String word = buffer.getText(j + 1, i - j);
    return word;
  }

  /**
   * Build the completion list to follow a 'new'. It will contains classes name
   *
   * @param textArea the current textArea
   * @param word
   *
   * @return a completion list
   */
  private SideKickCompletion completeClassDeclaration(JEditTextArea textArea, String word, String lastWord) {
    final PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea, word, lastWord);
    final AbstractProject project = projectManager.getProject();
    final Map classes = project.getClasses();
    final Collection collection = classes.values();
    for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
      final Object o = iterator.next();
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
    final AbstractProject project = projectManager.getProject();
    if (project != null) {
      if (project.acceptFile(path)) {
        for (int i = 0; i < phpDocument.getList().size(); i++) {
          final Object o = phpDocument.getList().get(i);
          if (o instanceof ClassDeclaration) {
            final ClassDeclaration classDeclaration = (ClassDeclaration) o;
            project.addClass(classDeclaration.getClassHeader());
          } else if (o instanceof MethodDeclaration) {
            final MethodDeclaration methodDeclaration = (MethodDeclaration) o;
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
   * @param classHeader classHeader
   * @param currentWord the current word
   * @param lastWord    the previous word
   *
   * @return a completion list or null if we aren't in a class
   */
  private static PHPSideKickCompletion completeClassMembers(JEditTextArea textArea,
                                                            ClassHeader classHeader,
                                                            String currentWord,
                                                            String lastWord) {
    if (classHeader == null) return null;
    final PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(textArea, currentWord, lastWord);
    final List methods = classHeader.getMethodsHeaders();
    final List fields = classHeader.getFields();
    phpSideKickCompletion.addOutlineableList(methods, currentWord);
    phpSideKickCompletion.addOutlineableList(fields, currentWord);
    Log.log(Log.DEBUG, PHPSideKickParser.class, "Items in list : " + phpSideKickCompletion.getItemsCount());
    return phpSideKickCompletion;
  }
}
