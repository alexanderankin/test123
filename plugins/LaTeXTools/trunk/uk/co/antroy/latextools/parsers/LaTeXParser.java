// :folding=explicit:
package uk.co.antroy.latextools.parsers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.TextUtilities;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;

import sidekick.SideKickCompletion;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import uk.co.antroy.latextools.LaTeXCompletion;
import uk.co.antroy.latextools.LaTeXDockable;
import errorlist.DefaultErrorSource;
import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;


public class LaTeXParser
    extends SideKickParser {

	/** String that identifies the start of LaTeX commands. */
    public static final String COMMAND_START = "\\";
	//~ Instance/static variables .............................................

    private String text;
    private SideKickParsedData data;
    private Buffer buffer;
//    private static final LaTeXDockable controls = new LaTeXDockable();
    private Set navItems = new TreeSet();
    private DefaultMutableTreeNode root;
    // private int lowlev;
    /** End of the buffer. */
    private Position bufferEndPosition;

    //~ Constructors ..........................................................

    /**
     * Creates a new LaTeXParser object.
     * 
     * @param name �
     */
    public LaTeXParser(String name) {
        super(name);
    }

    //~ Methods ...............................................................

    /**
     * �
     * 
     * @return � 
     */
    public static LaTeXDockable getControls() {

        return LaTeXDockable.getInstance();  //controls;
    }

    /**
     * Parse the buffer to find structure elements for the Structure browser.
     * @param buffer 
     * @param e 
     * @return Tree representation of the buffer's structure 
     */
    public SideKickParsedData parse(Buffer buffer, DefaultErrorSource e) {//{{{ 
    
        this.buffer = buffer;
        data = new SideKickParsedData(buffer.getName());
        text = buffer.getText(0, buffer.getLength());
        bufferEndPosition = buffer.createPosition(text.length());
        root = data.root;
        NavigationList nl = (NavigationList)NavigationList.getNavigationData().first();
        // See if something is selected in the combobox
        try {
        	Object o = getControls().getComboBox().getSelectedItem();
        	nl = (NavigationList) o;
        }
        catch (Exception ex) {}
        // Discover all structure elements covered by the NavigationList
        // and build a tree representing the structure
        parseNavigationData(nl);
        return data;	// Data having this.root as its root element
    } //}}}

    /**
     * Go through the list of discovered structure elements 
     * and build a tree from them based on their hierarchical 
     * relationships.
     * The tree is built under this.root.
     */
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

    /**
     * Find a correct parent for thisNode based on its nesting level 
     * compared to the nesting level of the last node. 
     * @param thisNode
     * @param lastNode
     * @return The correct parent for thisNode
     */
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
        // Find all structure elements matched by the NavigationList
        searchBuffer(navList);
        // Turn the list of structure elements into a tree reflecting 
        // their hierarchical relationships (based on the nesting level)
        buildTree();
    } //}}}


    /**
     * Search the buffer for structure LaTeX elements matched by tags 
     * on the navigation list, set their properties (start, end 
     * positions) and add them to the list of structure elements 
     * for the structure browser.
     * 
     * @param navList A list of {@link TagPair}s that define what 
     * elements to look for.
     */
    private void searchBuffer(NavigationList navList) {//{{{
        String text = buffer.getText(0, buffer.getLength());
        // LinkedList stack = new LinkedList();

        // For all elements matched by the Navigation list
        for (Iterator it = navList.iterator(); it.hasNext();) {

            TagPair srch = (TagPair)it.next();
            String replace = srch.getReplace();
            // String endTag = srch.getEndTag();
            boolean default_replace = true;

            if (!(replace.equals(" "))) { // TODO: NOTE: This code probably belongs in TagPair.java

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

                // For all find occurences of the given element
                for (int m = 0; m < match.length; m++) {
                    int posn = match[m].getStartIndex();
                    int line = buffer.getLineOfOffset(posn);
                    String preText = buffer.getLineText(line).substring(0,(posn - buffer.getLineStartOffset(line))+1);
                    
                    if (preText.indexOf("%") >= 0) continue;	// ignore lines commented out
                    
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
    
    /**
     * Update the end of elements that may span until the end of the buffer
     * - such elements (chapter, section...) are ended: 
     * either by the end of the document
     * or by a following element on the same or lesser nesting level 
     * <br>
     * Example: A section is ended by the start of a next section or 
     * of a new chapter.
     */
    private void updateAssetEnd(){//{{{ 
    
        LinkedList stack = new LinkedList();
        
        for (Iterator it = navItems.iterator(); it.hasNext(); ){
           LaTeXAsset currentAsset = (LaTeXAsset) it.next();
           // Ignore elements that certainly end before the end of buffer
           if (!currentAsset.getEnd().equals(bufferEndPosition)){ continue;}
           
           // Initialize the stack if yet empty
           if (stack.size()==0){
              stack.addLast(currentAsset);
              break;
           }
           
           // Does the current asset mark the end of the previous asset?
           // Example: A new section ends the previous, as well as a new chapter.
           LaTeXAsset top = (LaTeXAsset) stack.getLast();
           if (currentAsset.getLevel() > top.getLevel()){
              stack.addLast(currentAsset);
              break;
           }
           else{
              top.setEnd(buffer.createPosition(currentAsset.getStart().getOffset()-1));
              stack.removeLast();
           }
        } // for all Assets
        if (stack.size()>0){
           int endOfText = text.length();
           for (Iterator it = stack.iterator(); it.hasNext(); ){
              LaTeXAsset next = (LaTeXAsset) it.next();
              next.setEnd(buffer.createPosition(endOfText));
           }
        }

    } //}}}
    
    

    // TODO: The following should be implemented for code completion.{{{
     public boolean supportsCompletion(){
		return true;
	}
    
	public String getInstantCompletionTriggers(){
		return COMMAND_START;
	}
    /*
	public String getParseTriggers(){
		return null;
	}*/

     /*
      * @see SideKickParser#complete(org.gjt.sp.jedit.EditPane, int) 
      */
	public SideKickCompletion complete(EditPane editPane, int caret){
		
		// Get the current prefix - the "word" typed so far
		Buffer buffer = editPane.getBuffer();
	    JEditTextArea textArea = editPane.getTextArea();
	    int caretLine = textArea.getCaretLine();
	    int caretInLine = caret - buffer.getLineStartOffset(caretLine);
	    if (caretInLine == 0) return null;

	    String line = buffer.getLineText(caretLine);
	    int wordStart = TextUtilities.findWordStart(line, caretInLine - 1, COMMAND_START);
	    String currentWord = line.substring(wordStart, caretInLine);	// word before the caret
	    
	    //Log.log(Log.DEBUG, this, "currentWord:"+currentWord); // TODO: delme
	    
	    // Create and return the list of completions		
	    if(currentWord != null && currentWord.startsWith(COMMAND_START))
	    {
	    	// remove the leading '\' from the word typed so far
	    	return new LaTeXCompletion(editPane.getView(), 
	    			currentWord.substring(1,currentWord.length()));
	    }
	    else
	    { return null; }
	} 
    //}}}
}
