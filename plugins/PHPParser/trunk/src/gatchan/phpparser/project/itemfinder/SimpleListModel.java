package gatchan.phpparser.project.itemfinder;

import net.sourceforge.phpdt.internal.compiler.ast.ClassHeader;
import net.sourceforge.phpdt.internal.compiler.ast.MethodHeader;

import javax.swing.*;
import java.util.List;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Iterator;

/** @author Matthieu Casanova */
public final class SimpleListModel extends AbstractListModel {
  private TreeSet list = new TreeSet(new SimpleComparator());

  private int mode;

  private Object[] items;

  public SimpleListModel() {
  }

  public int getSize() {
    if (items == null) return 0;
    return Math.min(items.length, 25);
  }

  public Object getElementAt(int index) {
    return items[index];
  }

  public void setList(List list, String searchString) {
    final int oldSize = this.list.size();
    final int size = list.size();
    if (oldSize != 0) {
      fireIntervalRemoved(this, 0, Math.min(oldSize - 1, 25));
    }
    this.list.clear();
    for (int i = 0; i < list.size(); i++) {
      PHPItem phpItem = (PHPItem) list.get(i);
      if (accept(phpItem, searchString)) {
        this.list.add(phpItem);
      }
    }
    items = this.list.toArray();
    if (size != 0) {
      fireIntervalRemoved(this, 0, Math.min(size - 1, 25));
    }
  }

  public void filter(String searchString) {
    if (getSize() != 0) {
      boolean modified = false;
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        PHPItem phpItem = (PHPItem) iterator.next();
        if (!accept(phpItem, searchString)) {
          modified = true;
          iterator.remove();
        }
      }
      if (modified) {
        int oldSize = items.length;
        items = list.toArray();
        fireIntervalRemoved(this, items.length, oldSize);
      }
      fireContentsChanged(this, 0, items.length);
    }
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  private boolean accept(PHPItem phpItem, String searchText) {
    return phpItem.getName().toLowerCase().indexOf(searchText) != -1 && (mode == FrameFindItem.ALL_MODE ||
                                                                                                        (mode == FrameFindItem.CLASS_MODE && phpItem instanceof ClassHeader) ||
                                                                                                                                                                             (mode == FrameFindItem.METHOD_MODE && phpItem instanceof MethodHeader));
  }


  public void clear() {
    list.clear();
    items = null;
  }

  private class SimpleComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      return ((PHPItem) o1).getName().compareTo(((PHPItem) o2).getName());
    }
  }
}
