package gatchan.phpparser.project.itemfinder;

import org.gjt.sp.util.Log;

import java.util.*;

/** @author Matthieu Casanova */
public final class QuickAccessItemFinder {
  private final Map items;

  private static final int indexLength = 2;

  public QuickAccessItemFinder() {
    items = new HashMap();
  }

  public void addToIndex(PHPItem phpItem) {
    String name = phpItem.getNameLowerCase();
    for (int i = 0; i < name.length() - 1; i++) {
      String sub = name.substring(i, i + 1);
      addItem(sub, phpItem);
    }
    for (int i = 0; i < name.length() - indexLength; i++) {
      String sub = name.substring(i, i + indexLength);
      addItem(sub, phpItem);
    }
  }

  private void addItem(String sub, PHPItem o) {
    List list = (List) items.get(sub);
    if (list == null) {
      list = new ArrayList();
      list.add(o);
      items.put(sub, list);
    } else if (!list.contains(o)) {
      list.add(o);
    }
  }

  public void purgePath(String path) {
    long start = System.currentTimeMillis();
    Iterator iterator = items.keySet().iterator();
    while (iterator.hasNext()) {
      String sub = (String) iterator.next();
      List list = (List) items.get(sub);
      ListIterator listIterator = list.listIterator();
      while (listIterator.hasNext()) {
        PHPItem phpItem = (PHPItem) listIterator.next();
        if (path.equals(phpItem.getPath())) {
          listIterator.remove();
        }
      }
      if (list.size() == 0) {
        iterator.remove();
      }
    }
    long end = System.currentTimeMillis();
    Log.log(Log.DEBUG, this, "Purge path "+(end - start) + "ms");
  }

  /**
   * Get the list of items containing the first characters (limited to the index length.
   *
   * @param s the searched string
   *
   * @return the list
   */
  public List getItemContaining(String s) {
    if (s.length() > indexLength) {
      s = s.substring(0, indexLength);
    }
    List list = (List) items.get(s);
    if (list == null) {
      return new ArrayList(1);
    }
    return list;
  }

  public static int getIndexLength() {
    return indexLength;
  }
}
