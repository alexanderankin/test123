package gatchan.phpparser.sidekick;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import sidekick.SideKickCompletion;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class PHPSideKickCompletion extends SideKickCompletion {

  private JEditTextArea textArea;

  public PHPSideKickCompletion(JEditTextArea textArea) {
    this.textArea = textArea;
  }

  public void addItem(String item) {
    items.add(item);
  }

  public void addItem(Outlineable item) {
    items.add(item);
  }

  public void addOutlineableList(List items) {
    this.items.addAll(items);
  }

  public void insert(int index) {
    //todo change that !!!
    final Object object = items.get(index);
    String insertText;
    if (object instanceof Outlineable) {
      insertText =((Outlineable)object).getName();
    } else {
      insertText = (String) object;
    }
    textArea.setSelectedText(insertText);
  }

  public int getTokenLength() {
    return 0;
  }

  public boolean handleKeystroke(int selectedIndex, char keyChar) {
    if (keyChar == '\n' || keyChar == ' '  || keyChar == '\t') {
      insert(selectedIndex);
      return false;
    }
    return false;
  }
}
