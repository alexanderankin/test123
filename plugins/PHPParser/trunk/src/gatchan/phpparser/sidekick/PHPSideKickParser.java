package gatchan.phpparser.sidekick;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.PHPErrorSource;
import gatchan.phpparser.PHPParserOptionPane;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.ParseException;
import gatchan.phpparser.parser.ParsingAbortedError;
import gatchan.phpparser.project.PHPProjectChangedMessage;
import gatchan.phpparser.project.Project;
import gatchan.phpparser.project.ProjectManager;
import net.sourceforge.phpdt.internal.compiler.ast.*;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import org.gjt.sp.jedit.*;
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
    String path = buffer.getPath();
    Project project = projectManager.getProject();
    if (project != null) project.clearSourceFile(path);
    if (parser != null && !parser.isStopped()) {
      Log.log(Log.ERROR, PHPSideKickParser.class, "The parser had not been stopped before asking a new parse !");
      stop();
    }
    parser = new PHPParser();
    parser.setPhp5Enabled(jEdit.getBooleanProperty(PHPParserOptionPane.PROP_PHP5_SUPPORT));
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
      PHPDocument phpDocument = parser.getPHPDocument();
      updateProject(phpDocument, path);
      parser = null;
      SideKickParsedData data = new SideKickParsedData(buffer.getName());

      buildChildNodes(data.root, phpDocument, buffer);
      buffer.setProperty("PHPDocument", phpDocument);
      EditBus.send(new PHPProjectChangedMessage(this, projectManager.getProject(), PHPProjectChangedMessage.UPDATED));
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
    PHPParser parser = new PHPParser();
    parser.setPath(path);
    try {
      parser.parse(reader);
    } catch (ParsingAbortedError parsingAbortedError) {
      Log.log(Log.MESSAGE, PHPSideKickParser.class, "The parser was aborted");
    } catch (ParseException e) {
      Log.log(Log.MESSAGE, PHPSideKickParser.class, e.getMessage());
    }
    PHPDocument phpDocument = parser.getPHPDocument();
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
    for (int i = 0; i < phpDocument.size(); i++) {
      Outlineable o = phpDocument.get(i);
      buildNode(parent, o, buffer);
    }
  }


  private void buildNode(DefaultMutableTreeNode parent,
                         Outlineable sourceNode,
                         Buffer buffer) {
    AstNode astNode = (AstNode) sourceNode;
    Position startPosition = buffer.createPosition(buffer.getLineStartOffset(astNode.getBeginLine() - 1) + astNode.getBeginColumn());
    Position endPosition = buffer.createPosition(buffer.getLineStartOffset(astNode.getEndLine()) + astNode.getEndColumn());
    PHPAsset asset;
    if (astNode instanceof ClassDeclaration) {
      asset = new ClassAsset(astNode.toString(), startPosition, endPosition);
    } else if (astNode instanceof MethodDeclaration) {
      asset = new MethodAsset(astNode.toString(), startPosition, endPosition);
    } else if (astNode instanceof VariableDeclaration) {
      asset = new FieldAsset(astNode.toString(), startPosition, endPosition);
    } else if (astNode instanceof InclusionExpression) {
      asset = new IncludeAsset(astNode.toString(), startPosition, endPosition);
    } else {
      asset = new PHPAsset(astNode.toString(), startPosition, endPosition);
    }
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(asset, true);
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
    Buffer buffer = editPane.getBuffer();
    PHPDocument phpDocument = (PHPDocument) buffer.getProperty("PHPDocument");
    if (phpDocument == null) {
      Log.log(Log.DEBUG, this, "No php document for this buffer");
      return null;
    }
    JEditTextArea textArea = editPane.getTextArea();
    int caretLine = textArea.getCaretLine();
    int caretInLine = caret - buffer.getLineStartOffset(caretLine);
    if (caretInLine == 0) return null;
    String line = buffer.getLineText(caretLine);
    int wordStart = TextUtilities.findWordStart(line, caretInLine - 1, "");
    String currentWord = line.substring(wordStart, caretInLine);

    Project project = projectManager.getProject();

    //Static class access
    PHPSideKickCompletion phpSideKickCompletion = null;

    if (project != null) {
      phpSideKickCompletion = completeStaticClassAccess(currentWord, line, wordStart, project, textArea);
    }
    if (phpSideKickCompletion != null) return phpSideKickCompletion;

    ClassDeclaration currentClass = phpDocument.insideWichClassIsThisOffset(caretLine, caretInLine);
    MethodDeclaration currentMethod;


    String lastWord2 = getPreviousWord(caret, buffer);


    if (currentClass == null) {
      //We are not inside a class
      currentMethod = phpDocument.insideWichMethodIsThisOffset(caretLine, caretInLine);
    } else {
      //We are inside a class
      currentMethod = currentClass.insideWichMethodIsThisOffset(caretLine, caretInLine);


      if (currentMethod == null) {
        //We are inside a class but not inside a method
        if (lastWord2.endsWith(";") || lastWord2.endsWith("{") || lastWord2.endsWith("}")) {
          phpSideKickCompletion = new PHPSideKickCompletion(currentWord, lastWord2);
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
        if (lastWord != null && lastWord.charAt(0) == '$' && "->".equals(currentWord) && currentMethod != null) {
          VariableUsage variableUsage = currentMethod.getAssignedVariableInCode(lastWord.substring(1),
                                                                                caretLine,
                                                                                caretInLine);
          String className = variableUsage.getType().getClassName();
          if (className != null) {
            ClassHeader classHeader = project.getClass(className);
            if (classHeader == null) {
              for (int j = 0; j < phpDocument.size(); j++) {
                Outlineable outlineable = phpDocument.get(j);
                if (outlineable instanceof  ClassDeclaration && outlineable.getName().equals(className)) {
                  classHeader = ((ClassDeclaration) outlineable).getClassHeader();
                }
              }
            }
            if (classHeader != null) {
              phpSideKickCompletion = new PHPSideKickCompletion(currentWord, lastWord);
              List methodsHeaders = classHeader.getMethodsHeaders();
              for (int j = 0; j < methodsHeaders.size(); j++) {
                phpSideKickCompletion.addItem(methodsHeaders.get(j), "");
              }
              List fields = classHeader.getFields();
              for (int j = 0; j < fields.size(); j++) {
                phpSideKickCompletion.addItem(fields.get(j), "");
              }
              return phpSideKickCompletion;
            }
          }
        } else if ("$this->".equals(lastWord) || ("->".equals(currentWord) && "$this".equals(lastWord))) {
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
      return completeClassDeclaration(textArea, currentWord.trim(), lastWord2, phpDocument);
    }

    return null;
  }

  private static PHPSideKickCompletion completeStaticClassAccess
          (String
                  currentWord,
           String
                   line,
           int wordStart,
           Project project,
           JEditTextArea textArea) {
    PHPSideKickCompletion phpSideKickCompletion = null;
    if ("::".equals(currentWord)) {
      int previousWordStart = TextUtilities.findWordStart(line, wordStart - 1, "_");
      String classAccessed = line.substring(previousWordStart, wordStart);
      ClassHeader classHeader = project.getClass(classAccessed);
      phpSideKickCompletion = completeClassMembers(textArea, classHeader, "", classAccessed + "::");
    } else if (wordStart > 2 && (line.charAt(wordStart - 1) == ':' || line.charAt(wordStart - 2) == ':')) {
      int previousWordStart = TextUtilities.findWordStart(line, wordStart - 3, "_");
      String classAccessed = line.substring(previousWordStart, wordStart - 2);
      ClassHeader classHeader = project.getClass(classAccessed);
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
  private static String getPreviousWord
          (
                  int caret, Buffer
                          buffer) {
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
    return buffer.getText(j + 1, i - j);
  }

  /**
   * Build the completion list to follow a 'new'. It will contains classes name
   *
   * @param textArea    the current textArea
   * @param word
   * @param phpDocument
   *
   * @return a completion list
   */
  private SideKickCompletion completeClassDeclaration
          (JEditTextArea
                  textArea,
           String
                   word,
           String
                   lastWord,
           PHPDocument
                   phpDocument) {
    PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(word, lastWord);
    Project project = projectManager.getProject();
    if (project != null) {
      Map classes = project.getClasses();
      Collection collection = classes.values();
      Iterator iterator = collection.iterator();
      while (iterator.hasNext()) {
        Object o = iterator.next();
        phpSideKickCompletion.addItem(o, word);
      }
    }

    for (int i = 0; i < phpDocument.size(); i++) {
      Outlineable outlineable = phpDocument.get(i);
      if (outlineable instanceof ClassDeclaration) {
        phpSideKickCompletion.addItem(outlineable, word);
      }
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

  private void updateProject
          (PHPDocument
                  phpDocument, String
                  path) {
    Project project = projectManager.getProject();
    if (project != null) {
      if (project.acceptFile(path)) {
        for (int i = 0; i < phpDocument.size(); i++) {
          Outlineable o = phpDocument.get(i);
          if (o instanceof ClassDeclaration) {
            ClassDeclaration classDeclaration = (ClassDeclaration) o;
            project.addClass(classDeclaration.getClassHeader());
          } else if (o instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) o;
            project.addMethod(methodDeclaration.getMethodHeader());
          } else if (o instanceof InterfaceDeclaration) {
            project.addInterface((InterfaceDeclaration) o);
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
  private static PHPSideKickCompletion completeClassMembers
          (JEditTextArea
                  textArea,
           ClassHeader
                   classHeader,
           String
                   currentWord,
           String
                   lastWord) {
    if (classHeader == null) return null;
    PHPSideKickCompletion phpSideKickCompletion = new PHPSideKickCompletion(currentWord, lastWord);
    completeClassMembers(classHeader, phpSideKickCompletion, currentWord);
    Log.log(Log.DEBUG, PHPSideKickParser.class, "Items in list : " + phpSideKickCompletion.getItemsCount());
    return phpSideKickCompletion;
  }

  private static void completeClassMembers
          (ClassHeader
                  classHeader,
           PHPSideKickCompletion
                   phpSideKickCompletion,
           String
                   currentWord) {
    List methods = classHeader.getMethodsHeaders();
    List fields = classHeader.getFields();
    phpSideKickCompletion.addOutlineableList(methods, currentWord);
    phpSideKickCompletion.addOutlineableList(fields, currentWord);
    String superClassName = classHeader.getSuperClassName();
    if (superClassName != null) {
      Project project = ProjectManager.getInstance().getProject();
      if (project != null) {
        ClassHeader superClassHeader = project.getClass(superClassName);
        if (superClassHeader == null) {
          Log.log(Log.DEBUG, PHPSideKickParser.class, "Unknown superclass " + superClassHeader);
        } else {
          completeClassMembers(superClassHeader, phpSideKickCompletion, currentWord);
        }
      }
    }
  }
}
