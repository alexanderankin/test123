package uk.co.antroy.latextools; 

import sidekick.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import org.gjt.sp.jedit.*;
import errorlist.DefaultErrorSource;
import java.util.*;
import gnu.regexp.*;
import java.io.*;
import org.gjt.sp.util.Log;

public class LaTeXParser extends SideKickParser{
  
  private String text;
  private SideKickParsedData data;
  private Buffer buffer;
  private static final LaTeXDockable controls = new LaTeXDockable();
  private ArrayList navItems = new ArrayList();
  private DefaultMutableTreeNode root;
  private int lowlev;
  
  public LaTeXParser(String name){
    super(name);
  }
  
  public SideKickParsedData parse(Buffer buffer, DefaultErrorSource e){
    this.buffer = buffer;
    data = new SideKickParsedData(buffer.getName());
    text = buffer.getText(0,buffer.getLength());

    root = data.root;
    
    switch(controls.getSelectedButton()){
      case LaTeXDockable.LABELS: parseReferences(); break;
      default: Object o = controls.getComboBox().getSelectedItem();
               parseNavigationData((NavigationList) o);
    }
    
    
    return data;
  }

  private void parseReferences(){
      int refStart = text.indexOf("\\label{");
      while (refStart >= 0) {
        int refEnd = text.indexOf("}", refStart);
        String ref = text.substring(refStart + 7, refEnd);
        root.add(new DefaultMutableTreeNode(LaTeXAsset.createAsset(ref.trim(), 
                                          buffer.createPosition(refStart+7),
                                          buffer.createPosition(refEnd),
                                          LaTeXAsset.DEFAULT_ICON)));
        refStart = text.indexOf("\\label{", refEnd);
      }
      
      
  }

  private void parseNavigationData(NavigationList navList){
    navItems.clear();
		searchBuffer(navList);
    buildTree();
  }


	private void searchBuffer(NavigationList navList){
			for (int index = 0; index < buffer.getLineCount() - 1;index++) {
				lowlev = navList.getLowestLevel();
				searchLine(index, navList);
			}

	}

	private void searchLine(int lineNumber, NavigationList nl){
		// Search a string for items in the NavigationList.
			String lineText = buffer.getLineText(lineNumber);
      for (Iterator it = nl.iterator(); it.hasNext(); ) {
      TagPair srch = (TagPair) it.next();
      RE search = null;

      try {
        search = new RE(srch.getTag());
      } catch (REException e) {
        search = null;
      }

      if (search == null) {
        int refStart = lineText.indexOf(srch.getTag());
        if (refStart >= 0) {
          LaTeXAsset asset = LaTeXAsset.createAsset(lineText, lineNumber, srch.getLevel(), srch.getIcon());
          navItems.add(asset);
          break;
        }
      } else {
        REMatch match = search.getMatch(lineText);
        String result = "";
        if (match != null) {
          int i;
          if ((i = search.getNumSubs()) > 0) {
            StringBuffer sb = new StringBuffer();
            for (int j = 1; j <= i; j++) {
              sb.append(match.toString(j));
            }
            result = sb.toString();
          } else {
            result = lineText;
          }
          LaTeXAsset asset = LaTeXAsset.createAsset(result, lineNumber, srch.getLevel(), srch.getIcon());
          navItems.add(asset);
          break;
        }
      }
    }
	}
  
  private LaTeXAsset makeAsset(String lable, int line, int level, int icon){
    Position pstart = buffer.createPosition(buffer.getLineStartOffset(line)),
             pend   =  buffer.createPosition(buffer.getLineStartOffset(line+1));
    LaTeXAsset asset = LaTeXAsset.createAsset(lable, pstart, pend, icon);
    asset.setLevel(level);
    return asset;
  }
  
  
  private void buildTree() {
    Iterator it = navItems.iterator();
    DefaultMutableTreeNode lastNode = root;
    while (it.hasNext()) {
      LaTeXAsset asset = (LaTeXAsset) it.next();
      DefaultMutableTreeNode n = new DefaultMutableTreeNode(asset);
      DefaultMutableTreeNode correctNode = findCorrectNode(n, lastNode);
      lastNode = n;
      correctNode.add(n);
    }
  }
  
  private DefaultMutableTreeNode findCorrectNode(DefaultMutableTreeNode thisNode, 
                                                 DefaultMutableTreeNode lastNode) {
    if (lastNode == root) return lastNode;
    
    LaTeXAsset thisTp = (LaTeXAsset) thisNode.getUserObject();
    int thisLevel = thisTp.getLevel();
    LaTeXAsset lastTp = (LaTeXAsset) lastNode.getUserObject();
    int lastLevel = lastTp.getLevel();

    if (thisLevel > lastLevel) {

      return lastNode;
    } else if (thisLevel == lastLevel) {

      return (DefaultMutableTreeNode) lastNode.getParent();
    } else {

      return findCorrectNode(thisNode, 
                             (DefaultMutableTreeNode) lastNode.getParent());
    }
  }


  
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  
  public static LaTeXDockable getControls(){
    return controls;
  }
  
}
