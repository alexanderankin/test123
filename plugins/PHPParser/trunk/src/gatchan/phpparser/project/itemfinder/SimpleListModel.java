package gatchan.phpparser.project.itemfinder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/** @author Matthieu Casanova */
public final class SimpleListModel extends AbstractListModel {
  private List list = new ArrayList(1);

  public SimpleListModel() {
  }

  public int getSize() {
    return Math.min(list.size(), 25);
  }

  public Object getElementAt(int index) {
    return list.get(index);
  }

  public void setList(List list) {
    final int oldSize = this.list.size();
    final int size = list.size();
    this.list.clear();
    if (oldSize != 0) {
      fireIntervalRemoved(this, 0, Math.min(oldSize - 1, 25));
    }
    this.list = list;
    if (size != 0) {
      fireIntervalRemoved(this, 0, Math.min(size - 1, 25));
    }
  }

  public void clear() {
    list.clear();
  }
}
