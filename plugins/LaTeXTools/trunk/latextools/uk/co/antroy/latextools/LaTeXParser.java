// :folding=explicit:
package uk.co.antroy.latextools;

import errorlist.DefaultErrorSource;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.util.Set;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.text.Position;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;


public class LaTeXParser
    extends SideKickParser {

    //~ Instance/static variables .............................................

    private String text;
    private SideKickParsedData data;
    private Buffer buffer;
//    private static final LaTeXDockable controls = new LaTeXDockable();
    private Set navItems = new TreeSet();
    private DefaultMutableTreeNode root;
    private int lowlev;
    private Position bufferEndPosition;

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

        return LaTeXDockable.instance;//controls;
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
        bufferEndPosition = buffer.createPosition(text.length());
        root = data.root;

        switch (getControls().getSelectedButton()) {

            case LaTeXDockable.LABELS:
                parseReferences();

                break;

            default:

                Object o = getControls().getComboBox().getSelectedItem();
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

        LaTeXAsset thisTp = (LaTeXAsset) thisNode.getUserObject();
        int thisLevel = thisTp.getLevel();
        LaTeXAsset lastTp = (LaTeXAsset) lastNode.getUserObject();
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
        searchBuffer2(navList);
        buildTree();
    }

    private void parseReferences() {//{{{ 
    

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
    } //}}}

    private void searchBuffer(NavigationList navList) {//{{{  old version - DO NOT CHANGE!
    

        String text = buffer.getText(0, buffer.getLength());
        LinkedList stack = new LinkedList();

        for (Iterator it = navList.iterator(); it.hasNext();) {

            TagPair srch = (TagPair)it.next();
            String replace = srch.getReplace();
            String endTag = srch.getEndTag();
            boolean default_replace = true;

            if (!(replace.equals(" "))) { // NOTE: This code probably belongs in TagPair.java

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
                    }

                    LaTeXAsset asset = LaTeXAsset.createAsset(result, 
                                                              buffer.createPosition(match[m].getStartIndex()),
                                                              bufferEndPosition, 
                                                              srch.getIcon(), 
                                                              srch.getLevel());
                    navItems.add(asset);
                }
            }
        }
        updateAssetEnd();
    } //}}}
    
    private void searchBuffer2(NavigationList navList) {//{{{
        String text = buffer.getText(0, buffer.getLength());
        LinkedList stack = new LinkedList();

        for (Iterator it = navList.iterator(); it.hasNext();) {

            TagPair srch = (TagPair)it.next();
            String replace = srch.getReplace();
            String endTag = srch.getEndTag();
            boolean default_replace = true;

            if (!(replace.equals(" "))) { // NOTE: This code probably belongs in TagPair.java

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

                    Position endPosition = bufferEndPosition;
                    
                    if (srch.getType() == 1){
                        endPosition = buffer.createPosition(match[m].getEndIndex());
                    } else if (srch.getType() == 2){
                        findEndPosition(srch.getTag(), srch.getEndTag(), match[m].getEndIndex());
                    }
                    
                    LaTeXAsset asset = LaTeXAsset.createAsset(result, 
                                                              buffer.createPosition(match[m].getStartIndex()), 
                                                              endPosition, 
                                                              srch.getIcon(), 
                                                              srch.getLevel());
                    navItems.add(asset);
                }
            }
        }
        //updateAssetEnd();
    } //}}}
    
    private Position findEndPosition(String startRegExp, String endRegExp, int startIndex){//{{{ 
    
        if (true) return bufferEndPosition;
        String bufferText = buffer.getText(startIndex, buffer.getLength());
        StringBuffer sb = new StringBuffer("(");
        sb.append(startRegExp).append(")|(").append(endRegExp).append(")");
        RE regex = null;
        try{
            regex = new RE(sb.toString());
        } catch (REException e){
            e.printStackTrace();
            Log.log(Log.ERROR,this,e);
        }
        
        int offset = 0;
        int stack = 0;        
        
        do{
            REMatch match = regex.getMatch(bufferText, offset);
            
            if (match == null){break;}
            
            offset = match.getEndIndex();
            if (match.toString(1)==""){
                if(stack == 0){
                    return buffer.createPosition(startIndex + offset);
                } else {
                    stack--;
                    continue;
                }
            } else {
                stack++;
            }
            
        } while(true);
        
        
        return bufferEndPosition;
    } //}}}
    
    private void updateAssetEnd2(){ //{{{ 
    
       // NOTE: This method assumes that Sidekick cannot handle nested Assets.
       LaTeXAsset previousAsset = null;
       for (Iterator it = navItems.iterator(); it.hasNext(); ){
           if (previousAsset == null){
               previousAsset = (LaTeXAsset) it.next();
               break;
           }
           LaTeXAsset currentAsset = (LaTeXAsset) it.next();
           previousAsset.setEnd(currentAsset.getStart());
           previousAsset = currentAsset;
       }
       if (previousAsset != null){
           int endOfText = text.length();
           previousAsset.setEnd(buffer.createPosition(endOfText));
       }
    } //}}}
    
    private void updateAssetEnd(){//{{{ 
    
        // NOTE: This method assumes that Sidekick can handle nested Assets.
        LinkedList stack = new LinkedList();
        
        for (Iterator it = navItems.iterator(); it.hasNext(); ){
           LaTeXAsset currentAsset = (LaTeXAsset) it.next();
           //if (currentAsset.getEnd() = 
           if (stack.size()==0){
              stack.addLast(currentAsset);
              break;
           }
           LaTeXAsset top = (LaTeXAsset) stack.getLast();
           if (currentAsset.getLevel() > top.getLevel()){
              stack.addLast(currentAsset);
              break;
           }
           else{
              top.setEnd(buffer.createPosition(currentAsset.getStart().getOffset()-1));
              stack.removeLast();
           }
        }
        if (stack.size()>0){
           int endOfText = text.length();
           for (Iterator it = stack.iterator(); it.hasNext(); ){
              LaTeXAsset next = (LaTeXAsset) it.next();
              next.setEnd(buffer.createPosition(endOfText));
           }
        }

    } //}}}

    // TODO: The following should be implemented for code completion.
/*     public boolean supportsCompletion(){
		return false;
	}
    
	public String getInstantCompletionTriggers(){
		return null;
	}
    
	public String getParseTriggers(){
		return null;
	}

	public SideKickCompletion complete(EditPane editPane, int caret){
		return null;
	}  */

}
