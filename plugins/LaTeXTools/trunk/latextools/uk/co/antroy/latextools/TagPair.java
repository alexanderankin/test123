package uk.co.antroy.latextools; 

import org.gjt.sp.jedit.jEdit;

public class TagPair
//  implements Comparable {
{
  //~ Instance/static variables ...............................................

  int level;
  int line = 0;
  int icon;
  String tag;
  String replace = " ";
  String endTag = "";

  //~ Constructors ............................................................
  TagPair(String tag, String replace, String endTag, int level, int icon) {
      this(tag,replace,level,icon);
      this.endTag = endTag;
  }

  TagPair(String tag, String replace, int level, int icon) {
      this(tag,level,icon);
      this.replace = replace;
  }

  TagPair(String tag, int level, int icon) {
    this.tag = tag;
    this.level = level;
    this.icon = icon;
  }

  TagPair(String tag, int lev) {
    this(tag, 0, lev);
  }

  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @return ¤
   */
  public int getLevel() {

    return level;
  }

  /**
   * ¤
   * 
   * @return ¤
   */
/*   public int getLine() {

    return line;
  }
 */  
  public int getIcon() {

    return icon;
  }

  /**
   * ¤
   * 
   * @return ¤
   */
  public String getTag() {

    return tag;
  }

  /**
   * ¤
   * 
   * @return ¤
   */
  public String getReplace() {

    return replace;
  }
  /**
   * ¤
   * 
   * @return ¤
   */
  public String getEndTag() {

    return endTag;
  }
  /**
   * ¤
   * 
   * @return ¤
   */
  public void setEndTag(String et) {
      endTag = et;
  }

  /**
   * ¤
   * 
   * @param be ¤
   * @return ¤
   */
/*   public int compareTo(TagPair be) {

    if (be.getLine() == line) {

      String comp = line + tag;

      return comp.compareTo(be.getLine() + be.getTag());
    } else

      return line - be.getLine();
  }
 */
  /**
   * ¤
   * 
   * @param o
   * @return ¤
   */
/*   public int compareTo(Object o) {

    return compareTo((TagPair) o);
  }
 */
  /**
   * ¤
   * 
   * @return ¤
   */
  public String toString() {

    boolean NUMBERS_OFF = jEdit.getBooleanProperty("tagpair.linenumbers");

    if (line < 0 || NUMBERS_OFF)

      return tag;

    return line + "  : " + tag;
  }
}
