package gatchan.phpparser.project.itemfinder;

import org.gjt.sp.util.Log;

import java.util.*;

/** @author Matthieu Casanova */
public final class QuickAccessItemFinder {

  private final Map items;

  private final int indexLength = 2;

  public QuickAccessItemFinder() {
    items = new HashMap();
  }

  public void addToIndex(PHPItem o) {
    final String name = o.getName().toLowerCase();
    addItem(name.substring(0, 1), o);
    for (int i = 0; i < name.length() - indexLength; i++) {
      final String sub = name.substring(i, i + indexLength);
      addItem(sub, o);
    }
  }

  private void addItem(String sub, PHPItem o) {
    List list = (List) items.get(sub);
    if (list == null) {
      list = new ArrayList();
      items.put(sub, list);
    }
    list.add(o);
  }

  public void purgePath(String path) {
    final long start = System.currentTimeMillis();
    final Iterator iterator = items.keySet().iterator();
    while (iterator.hasNext()) {
      final String sub = (String) iterator.next();
      final List list = (List) items.get(sub);
      final ListIterator listIterator = list.listIterator();
      while (listIterator.hasNext()) {
        final PHPItem phpItem = (PHPItem) listIterator.next();
        if (path.equals(phpItem.getPath())) {
          listIterator.remove();
        }
      }
      if (list.size() == 0) {
        iterator.remove();
      }
    }
    final long end = System.currentTimeMillis();
    Log.log(Log.DEBUG,this,(end-start)+"ms");
  }

  public List getItemContaining(String s) {
    if (s.length() > indexLength) {
      s = s.substring(0, indexLength);
    }
    final List list = (List) items.get(s);
    if (list == null) {
      return new ArrayList(1);
    }
    return list;
  }
}
