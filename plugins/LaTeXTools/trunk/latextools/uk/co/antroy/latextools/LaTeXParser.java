package uk.co.antroy.latextools;

import errorlist.DefaultErrorSource;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;


public class LaTeXParser
    extends SideKickParser {

    //~ Instance/static variables .............................................

    private String text;
    private SideKickParsedData data;
    private Buffer buffer;
    private static final LaTeXDockable controls = new LaTeXDockable();
    private Collection navItems = new TreeSet();
    private DefaultMutableTreeNode root;
    private int lowlev;

    //~ Constructors ..........................................................

    /**
     * Creates a new LaTeXParser object.
     * 
     * @param name ¤
     */
    public LaTeXParser(String name) {
        super(name);
    }

    //~ Methods ...............................................................

    /**
     * ¤
     * 
     * @return ¤ 
     */
    public static LaTeXDockable getControls() {

        return controls;
    }

    /**
     * ¤
     * 
     * @param buffer ¤
     * @param e ¤
     * @return ¤ 
     */
    public SideKickParsedData parse(Buffer buffer, DefaultErrorSource e) {
        this.buffer = buffer;
        data = new SideKickParsedData(buffer.getName());
        text = buffer.getText(0, buffer.getLength());
        root = data.root;

        switch (controls.getSelectedButton()) {

            case LaTeXDockable.LABELS:
                parseReferences();

                break;

            default:

                Object o = controls.getComboBox().getSelectedItem();
                parseNavigationData((NavigationList)o);
        }

        return data;
    }

    private void buildTree() {

        Iterator it = navItems.iterator();
        DefaultMutableTreeNode lastNode = root;

        while (it.hasNext()) {

            LaTeXAsset asset = (LaTeXAsset)it.next();
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(asset);
            DefaultMutableTreeNode correctNode = findCorrectNode(n, lastNode);
            lastNode = n;
            correctNode.add(n);
        }
    }

    private DefaultMutableTreeNode findCorrectNode(DefaultMutableTreeNode thisNode, 
                                                   DefaultMutableTreeNode lastNode) {

        if (lastNode == root) {

            return lastNode;
        }

        LaTeXAsset thisTp = (LaTeXAsset)thisNode.getUserObject();
        int thisLevel = thisTp.getLevel();
        LaTeXAsset lastTp = (LaTeXAsset)lastNode.getUserObject();
        int lastLevel = lastTp.getLevel();

        if (thisLevel > lastLevel) {

            return lastNode;
        } else if (thisLevel == lastLevel) {

            return (DefaultMutableTreeNode)lastNode.getParent();
        } else {

            return findCorrectNode(thisNode, 
                                   (DefaultMutableTreeNode)lastNode.getParent());
        }
    }

    private void parseNavigationData(NavigationList navList) {
        navItems.clear();
        searchBuffer(navList);
        buildTree();
    }

    private void parseReferences() {

        int refStart = text.indexOf("\\label{");

        while (refStart >= 0) {

            int refEnd = text.indexOf("}", refStart);
            String ref = text.substring(refStart + 7, refEnd);
            root.add(new DefaultMutableTreeNode(LaTeXAsset.createAsset(ref.trim(), 
                                                                       buffer.createPosition(
                                                                                   refStart + 7), 
                                                                       buffer.createPosition(
                                                                                   refEnd), 
                                                                       LaTeXAsset.DEFAULT_ICON)));
            refStart = text.indexOf("\\label{", refEnd);
        }
    }

    private void searchBuffer(NavigationList navList) {

        String text = buffer.getText(0, buffer.getLength());

        for (Iterator it = navList.iterator(); it.hasNext();) {

            TagPair srch = (TagPair)it.next();
            String replace = srch.getReplace();
            boolean default_replace = true;

            if (!(replace.equals(" "))) { // TODO: This code belongs in TagPair.java

                try {

                    //          Log.log(Log.MESSAGE,this,"replace: "+replace);
                    RE colon = new RE("\\\\u003[aA]");

                    //          Log.log(Log.MESSAGE,this,"found: "+colon.getMatch(replace).toString());
                    replace = colon.substituteAll(replace, ":");
                    default_replace = false;
                } catch (REException e) {
                    default_replace = true;
                }
            }

            RE search = null;

            try {
                search = new RE(srch.getTag());
            } catch (REException e) {
                search = null;
            }

            if (search == null) {

                continue;
            } else {

                REMatch[] match = search.getAllMatches(text);

                for (int m = 0; m < match.length; m++) {

                    String result = "";
                    int i;

                    if (default_replace) {

                        if ((i = search.getNumSubs()) > 0) {

                            StringBuffer sb = new StringBuffer();

                            for (int j = 1; j <= i; j++) {
                                sb.append(match[m].toString(j));
                            }

                            result = sb.toString();
                        } else {
                            result = match[m].toString();
                        }
                    } else {
                        result = match[m].substituteInto(replace);

                        //            Log.log(Log.DEBUG,this,"SUB: "+replace);
                        //            Log.log(Log.DEBUG,this,"RES: "+result);
                    }

                    LaTeXAsset asset = LaTeXAsset.createAsset(result, 
                                                              buffer.createPosition(match[m].getStartIndex()), 
                                                              buffer.createPosition(match[m].getEndIndex()), 
                                                              srch.getIcon(), 
                                                              srch.getLevel());
                    navItems.add(asset);
                }
            }
        }
        updateAssetEnd();
    }
    
    private void updateAssetEnd(){
        // TODO: Run through the navItems TreeSet. 
        // Create a stack. Add each item to the stack in turn.
        // If the next items nesting level == top of stacks nesting level
        // then take the item off of the stack, and set its end to the next items level -1.
        // else add next item to stack. If there are no more items, 
        // set the end of each remaining item in the stack to the end of the buffer.
        
    }
    
}
