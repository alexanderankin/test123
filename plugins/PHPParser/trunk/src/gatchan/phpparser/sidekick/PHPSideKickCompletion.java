package gatchan.phpparser.sidekick;

import gatchan.phpparser.project.itemfinder.PHPItemCellRenderer;
import net.sourceforge.phpdt.internal.compiler.ast.ClassDeclaration;
import net.sourceforge.phpdt.internal.compiler.ast.ClassHeader;
import net.sourceforge.phpdt.internal.compiler.ast.MethodDeclaration;
import net.sourceforge.phpdt.internal.compiler.ast.MethodHeader;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import sidekick.SideKickCompletion;

import javax.swing.*;
import java.util.List;

/** @author Matthieu Casanova */
public class PHPSideKickCompletion extends SideKickCompletion {

  private JEditTextArea textArea;

  private String word, lastWord;

  public PHPSideKickCompletion(JEditTextArea textArea, String word, String lastWord) {
    this.textArea = textArea;
    this.word = word;
    this.lastWord = lastWord;
  }

  public void addItem(Object item, String word) {
    boolean caseSensitive = !(item instanceof MethodDeclaration);
    if (item.toString().regionMatches(caseSensitive, 0, word, 0, word.length())) {
      items.add(item);
    }
  }

  public ListCellRenderer getRenderer() {
    return new PHPItemCellRenderer();
  }

  public int getItemsCount() {
    return items.size();
  }

  public void addOutlineableList(List items, String word) {
    for (int i = 0; i < items.size(); i++) {
      Object o = items.get(i);
      addItem(o, word);
    }
  }

  public void insert(int index) {
    final Object object = items.get(index);
    String insertText;
    int caret = textArea.getCaretPosition();
    if (word.length() != 0) {
      Selection selection = textArea.getSelectionAtOffset(caret);
      if (selection == null) {
        selection = new Selection.Range(caret-word.length(),caret);
      } else {
        int start = selection.getStart();
        int end = selection.getEnd();
        selection = new Selection.Range(start - word.length(), end);
      }
      textArea.setSelection(selection);
    }
    if (object instanceof Outlineable) {
      insertText = ((Outlineable) object).getName();
      if (object instanceof MethodDeclaration || (object instanceof ClassDeclaration && "new".equals(lastWord))) {
        insertText += "()";
        caret--; //to go between the parenthesis
      }
    } else if (object instanceof ClassHeader) {
      insertText = ((ClassHeader) object).getName();
      if ("new".equals(lastWord)) {
        insertText += "()";
        caret--; //to go between the parenthesis
      }
    }else if (object instanceof MethodHeader) {
      insertText = ((MethodHeader) object).getName();
    } else {
      insertText = (String) object;
    }
    caret += insertText.length();
    textArea.setSelectedText(insertText);
   // textArea.setCaretPosition(caret);
  }

  public int getTokenLength() {
    return word.length();
  }

  public boolean handleKeystroke(int selectedIndex, char keyChar) {
    if (keyChar == '\n' || keyChar == ' ' || keyChar == '\t') {
      insert(selectedIndex);
      if (keyChar == ' ') {
        //inserting the space after the insertion
        textArea.userInput(' ');
      } else if (keyChar == '\t') {
        //removing the end of the word
        textArea.deleteWord();
      }
      return false;
    } else {
      textArea.userInput(keyChar);
      return true;
    }
  }
}
