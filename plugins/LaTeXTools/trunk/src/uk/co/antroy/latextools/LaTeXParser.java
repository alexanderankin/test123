package uk.co.antroy.latextools; 

import sidekick.*;
import javax.swing.tree.DefaultMutableTreeNode;
import org.gjt.sp.jedit.*;
import errorlist.DefaultErrorSource;

public class LaTeXParser extends SideKickParser{
  
  private String text;
  private SideKickParsedData data;
  private Buffer buffer;
  private static final LaTeXDockable controls = new LaTeXDockable();
  private ArrayList navItems = new ArrayList();
  private TreeNode root;
  
  public LaTeXParser(String name){
    super(name);
  }
  
  public SideKickParsedData parse(Buffer buffer, DefaultErrorSource e){
    this.buffer = buffer;
    data = new SideKickParsedData(buffer.getName());
    text = buffer.getText(0,buffer.getLength());

    root = data.root;
    
    loadNavigationData();
    parseNavigationData();
//    parseReferences();        

    return data;
  }


  private void parseReferences(){
      int refStart = text.indexOf("\\label{");
      while (refStart >= 0) {
        int refEnd = text.indexOf("}", refStart);
        String ref = text.substring(refStart + 7, refEnd);
        root.add(new DefaultMutableTreeNode(LaTeXAsset.createAsset(ref.trim(), 
                                          buffer.createPosition(refStart+7),
                                          buffer.createPosition(refEnd))));
        refStart = text.indexOf("\\label{", refEnd);
      }
  }

  private void parseNavigationData(){
    
    navItems.clear();
		
		NavigationList nlist = (NavigationList) controls.getComboBox.getSelectedItem();
		File main = new File(LaTeXMacros.getMainFile());
		
		if (main.exists()) {
			searchInput(main, nlist);
		}else {
			searchBuffer(buffer, nlist);
		}
		
    buildTree();
    
//    Collections.sort(navItems);
  }

	private void searchInput(File input, NavigationList nlist){
		Buffer[] buffs = jEdit.getBuffers();

		// Call appropriate search method.
		for (int i=0; i<buffs.length; i++){
			
			File bfile = new File(buffs[i].getPath());			
			if (bfile.equals(input)){
				searchBuffer(buffs[i], nlist);
				return;
			}
		}
				searchFile(input, nlist);
	}
	
	private void searchFile(File input, NavigationList nlist){
		// Search a file for items in the NavigationList.
		try{
			
			BufferedReader in = new BufferedReader(new FileReader(input));
      String nextLine = in.readLine().trim();
			int index = 0;

      while (nextLine != null) {
				lowlev = nlist.getLowestLevel();
				searchLine(nextLine, index, nlist);
				index++;
        nextLine = in.readLine().trim();
			}
		}
		catch(Exception e){
			Log.log(Log.ERROR, this, "Reading file: " + input.toString() + "caused error:" + e);
		}
	}
	
	private void searchBuffer(Buffer input, NavigationList nlist){
		// Search a buffer for items in the NavigationList.
		  
			int index = 0;

			while (index < input.getLineCount() - 1) {
				lowlev = nlist.getLowestLevel();
				String line = input.getLineText(index);
				searchLine(line, index, nlist);
				index++;
			}

	}

	private void searchLine(String line, int index, NavigationList nl){
		// Search a string for items in the NavigationList.
		    
				Iterator it = nl.iterator();

	      while (it.hasNext()) {

        TagPair srch = (TagPair) it.next();
        RE search = null;

        try {
          search = new RE(srch.getTag());
        } catch (REException e) {

          search = null;
        }

        if (search == null) {

          int refStart = line.indexOf(srch.getTag());

          if (refStart >= 0) {

            TagPair tp = new TagPair(line, index, srch.getLine());
            navItems.add(tp);

            break;
          }
        } else {

          REMatch match = search.getMatch(line);
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
              result = line;
            }

            TagPair tp = new TagPair(result, index, srch.getLine());
            navItems.add(tp);

            break;
          }
        }
      }
	
	}
  
  private void buildTree() {

/*     navTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) navTree.getLastSelectedPathComponent();

        if (node == null)

          return;

        TagPair t = (TagPair) node.getUserObject();
        visitLabel(t.getLine());
      }
    });
 */
    Iterator it = navItems.iterator();
    DefaultMutableTreeNode lastNode = root;
//*****************************************************************************
    while (it.hasNext()) {
//        root.add(new DefaultMutableTreeNode(LaTeXAsset.createAsset(ref.trim(), 
//                                          buffer.createPosition(refStart+7),
//                                          buffer.createPosition(refEnd))));
//
      TagPair t = (TagPair) it.next();
      DefaultMutableTreeNode n = new DefaultMutableTreeNode(t);
      DefaultMutableTreeNode correctNode = findCorrectNode(n, lastNode);
      lastNode = n;
      correctNode.add(n);
    }
  }
  
  private DefaultMutableTreeNode findCorrectNode(DefaultMutableTreeNode thisNode, 
                                                 DefaultMutableTreeNode lastNode) {

    TagPair thisTp = (TagPair) thisNode.getUserObject();
    int thisLevel = thisTp.getLevel();
    TagPair lastTp = (TagPair) lastNode.getUserObject();
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
