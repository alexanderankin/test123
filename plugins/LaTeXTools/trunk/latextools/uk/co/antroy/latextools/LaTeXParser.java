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
    public SideKickParsedData parse(Buffer buffer, DefaultErrorSource e) {//{{{ 
    
        this.buffer = buffer;
        data = new SideKickParsedData(buffer.getName());
        text = buffer.getText(0, buffer.getLength());
        bufferEndPosition = buffer.createPosition(text.length());
        root = data.root;

                Object o = getControls().getComboBox().getSelectedItem();
                parseNavigationData((NavigationList)o);
        return data;
    } //}}}

    private void buildTree() {//{{{ 

        Iterator it = navItems.iterator();
        DefaultMutableTreeNode lastNode = root;

        while (it.hasNext()) {

            LaTeXAsset asset = (LaTeXAsset)it.next();
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(asset);
            DefaultMutableTreeNode correctNode = findCorrectNode(n, lastNode);
            lastNode = n;
            correctNode.add(n);
        }
    } //}}}

    private DefaultMutableTreeNode findCorrectNode(DefaultMutableTreeNode thisNode, 
                                                   DefaultMutableTreeNode lastNode) {//{{{ 
                                                   

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
    } //}}}

    private void parseNavigationData(NavigationList navList) {//{{{ 
    
        navItems.clear();
        searchBuffer(navList);
        buildTree();
    } //}}}


    private void searchBuffer(NavigationList navList) {//{{{
        String text = buffer.getText(0, buffer.getLength());
        LinkedList stack = new LinkedList();

        for (Iterator it = navList.iterator(); it.hasNext();) {

            TagPair srch = (TagPair)it.next();
            String replace = srch.getReplace();
            String endTag = srch.getEndTag();
            boolean default_replace = true;

            if (!(replace.equals(" "))) { // NOTE: This code probably belongs in TagPair.java

                try {
                    RE colon = new RE("\\\\u003[aA]");
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
                    int posn = match[m].getStartIndex();
                    int line = buffer.getLineOfOffset(posn);
                    String preText = buffer.getLineText(line).substring(0,(posn - buffer.getLineStartOffset(line))+1);
                    
                    if (preText.indexOf("%") >= 0) continue;
                    
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

                    Position endPosition = bufferEndPosition;
                    
                    if (srch.getType() == 1){
                        endPosition = buffer.createPosition(match[m].getEndIndex());
                    } else if (srch.getType() == 2){
                        endPosition = findEndPosition(srch.getTag(), srch.getEndTag(), match[m].getEndIndex());
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
        updateAssetEnd();
    } //}}}
    
    private Position findEndPosition(String startRegExp, String endRegExp, int startIndex){//{{{ 
        String bufferText = buffer.getText(startIndex, buffer.getLength()-startIndex);
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
                    Position out = buffer.createPosition(startIndex + offset);
                   return out;
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
    
    private void updateAssetEnd(){//{{{ 
    
        LinkedList stack = new LinkedList();
        
        for (Iterator it = navItems.iterator(); it.hasNext(); ){
           LaTeXAsset currentAsset = (LaTeXAsset) it.next();
           if (!currentAsset.getEnd().equals(bufferEndPosition)){ continue;}
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

    // TODO: The following should be implemented for code completion.{{{
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
    //}}}
}
