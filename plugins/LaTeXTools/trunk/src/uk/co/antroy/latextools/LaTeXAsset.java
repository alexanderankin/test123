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
    
    public LaTeXAsset(String name){
      super(name);
    }
    
    public Icon getIcon(){
      
      StringBuffer filename = new StringBuffer("/");
      
      switch (iconType){
        case SECTION_ICON: filename.append("images/sections.png"); break;
        case GRAPHIC_ICON: filename.append("images/graphics.png"); break;
        case THEOREM_ICON: filename.append("images/theorem.png"); break;
        default: filename.append("images/default.png"); break;
      }
      
      Icon icon;
      try{
        icon = new ImageIcon(LaTeXAsset.class.getResource(filename.toString()));
        Log.log(Log.DEBUG,this,filename.toString() + "Loaded");
      }catch (Exception e){
        Log.log(Log.DEBUG,this,filename.toString() + "Not loaded");
        icon = null;
      }
      return icon;
//      return null;
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
    
    public static LaTeXAsset createAsset(String label, int line, int level, int icon){
      Buffer buffer = jEdit.getActiveView().getBuffer();
      Position pstart = buffer.createPosition(buffer.getLineStartOffset(line)),
               pend   =  buffer.createPosition(buffer.getLineStartOffset(line+1));
      LaTeXAsset asset = LaTeXAsset.createAsset(label, pstart, pend, icon);
      asset.setLevel(level);
      return asset;
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
    
    
  public String toString(){
    return getShortString();
  }
}
