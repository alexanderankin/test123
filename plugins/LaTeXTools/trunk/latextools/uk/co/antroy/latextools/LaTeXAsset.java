package uk.co.antroy.latextools;

import sidekick.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.Position;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;


//This must implement features of TagPair.
 public class LaTeXAsset extends Asset implements Comparable{

    private int level = 0;
    private int iconType = 0;
    public static final int DEFAULT_ICON = 0;
    public static final int SECTION_ICON = 1;
    public static final int GRAPHIC_ICON = 2;
    public static final int THEOREM_ICON = 3;
    public static final int TABLE_ICON = 4;
    public static final int LIST_ICON = 5;
    public static final int VERBATIM_ICON = 6;

    public LaTeXAsset(String name){
      super(name);
    }

    public Icon getIcon(){

      StringBuffer filename = new StringBuffer("/");

      switch (iconType){
        case DEFAULT_ICON: filename.append("images/default.png"); break;
        case SECTION_ICON: filename.append("images/sections.png"); break;
        case GRAPHIC_ICON: filename.append("images/graphics.png"); break;
        case THEOREM_ICON: filename.append("images/theorem.png"); break;
        case TABLE_ICON: filename.append("images/table.png"); break;
        case LIST_ICON: filename.append("images/list.png"); break;
        case VERBATIM_ICON: filename.append("images/verbatim.png"); break;
        default: filename = null;
      }

      Icon icon;
      if (filename == null) {
        icon = null;
      }else{
        try{
          icon = new ImageIcon(LaTeXAsset.class.getResource(filename.toString()));
        }catch (Exception e){
          Log.log(Log.DEBUG,this,filename.toString() + "Not found");
          icon = null;
        }
      }
      return icon;
    }

    public String getShortString(){
      return name;
    }

    public String getLongString(){
      return name;
    }

    public void setLevel(int lev){
     level = lev;
    }

    public int getLevel(){
      return level;
    }

    public void setIconType(int type){
      iconType = type;
    }

    public int getIconType(){
      return iconType;
    }

    public static LaTeXAsset createAsset(String name, Position start, Position end, int icon_type, int lev){
      LaTeXAsset asset = createAsset(name, start, end, icon_type);
      asset.setLevel(lev);
      return asset;
    }

    public static LaTeXAsset createAsset(String name, Position start, Position end, int icon_type){
      LaTeXAsset asset = createAsset(name, start, end);
      asset.setIconType(icon_type);
      return asset;
    }

    public static LaTeXAsset createAsset(String name, Position start, Position end){

      LaTeXAsset la = new LaTeXAsset(name);
      la.start = start;
      la.end = end;

      return la;
    }

    public int compareTo(LaTeXAsset asset) {

      int offset = start.getOffset();
      int assetOffset = asset.start.getOffset();

      if (offset == assetOffset){
        String comp = offset + getLongString();
        return comp.compareTo(assetOffset + asset.getLongString());
      } else{
        return offset - assetOffset;
      }
   }

  public int compareTo(Object o) {
    return compareTo((LaTeXAsset) o);
  }

  public boolean equals(Object o){
    if (o instanceof LaTeXAsset){
      return equals((LaTeXAsset) o);
    }else{
      return false;
    }
  }

  public boolean equals(LaTeXAsset o){
    boolean out = true;
    out = out && (name.equals(o.name));
    out = out && (start.getOffset() == o.start.getOffset());
    out = out && (end.getOffset() == o.end.getOffset());
    return out;
  }

  public int hashCode(){
    int out = 13;
    out = out *37;
    out += name.hashCode();
    out = out * 37;
    out += start.getOffset();
    out = out * 37;
    out += end.getOffset();
    return out;
  }

  public String toString(){
    return getShortString();
  }
}
