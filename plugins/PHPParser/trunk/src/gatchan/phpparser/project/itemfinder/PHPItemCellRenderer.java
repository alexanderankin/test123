package gatchan.phpparser.project.itemfinder;

import javax.swing.*;
import java.awt.*;

/**
 * @author Matthieu Casanova
 */
public final class PHPItemCellRenderer implements ListCellRenderer {

  private final PHPItemCellPanel comp = new PHPItemCellPanel();

  private String searchString;

  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus) {
    comp.setItem((PHPItem) value, searchString);
    if (isSelected) {
      comp.setBackground(list.getSelectionBackground());
    } else {
      comp.setBackground(list.getBackground());
    }
    return comp;
  }

  public void setSearchString(String searchString) {
    this.searchString = searchString.toLowerCase();
  }

  private static final class PHPItemCellPanel extends JPanel {

    private final JLabel label1 = new JLabel();
    private final JLabel label2 = new JLabel();

    private PHPItemCellPanel() {
      super(new FlowLayout(FlowLayout.LEFT));

      label2.setForeground(Color.gray);
      add(label1);
      add(label2);
    }

    private void setItem(PHPItem phpItem, String searchString) {
      String text;
      final String name = phpItem.getName();
      if (searchString == null) {
        text = name;
      } else {
        int i = name.toLowerCase().indexOf(searchString);
        final int searchStringLength = searchString.length();
        if (i == 0) {
          text = "<html><b>" + name.substring(0, searchStringLength) + "</b>" + name.substring(searchStringLength) + "</html>";
        } else if (i == -1) {
          text = name;
        } else {
          final String s = name.substring(0, i);
          final String s2 = name.substring(i, i + searchStringLength);
          final String s3 = name.substring(i + searchStringLength);
          text = "<html>" + s + "<b>" + s2 + "</b>" + s3 + "</html>";
        }
      }
      label1.setIcon(phpItem.getIcon());
      label1.setText(text);
      label2.setText(phpItem.getPath());
    }
  }
}
