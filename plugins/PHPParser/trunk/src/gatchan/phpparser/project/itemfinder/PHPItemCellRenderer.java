package gatchan.phpparser.project.itemfinder;

import javax.swing.*;
import java.awt.*;

/**
 * @author Matthieu Casanova
 */
public final class PHPItemCellRenderer implements ListCellRenderer {

  private final PHPItemCellPanel comp = new PHPItemCellPanel();

  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus) {
    comp.setItem((PHPItem) value);
    if (isSelected) {
      comp.setBackground(list.getSelectionBackground());
    } else {
      comp.setBackground(list.getBackground());
    }
    return comp;
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

    private void setItem(PHPItem phpItem) {
      label1.setIcon(phpItem.getIcon());
      label1.setText(phpItem.getName());
      label2.setText(phpItem.getPath());
    }
  }
}
