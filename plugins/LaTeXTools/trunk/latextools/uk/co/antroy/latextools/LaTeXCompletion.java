package uk.co.antroy.latextools;

import errorlist.DefaultErrorSource;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;

import sidekick.SideKickParsedData;
import sidekick.SideKickCompletion;


public class LaTeXCompletion extends SideKickCompletion {

   public LaTeXCompletion(){
      items.add("Bertrand");
      items.add("Gem Gem");
   }
   
	public void insert(int index){
      
   }

	/**
	 * The length of the text being completed (popup will be positioned there).
	 */
    public int getTokenLength(){
       return 1;
    }

	/**
	 * @param selectedIndex -1 if the popup is empty, otherwise the index of
	 * the selected completion.
	 * @param keyChar the character typed by the user.
	 */
    public boolean handleKeystroke(int selectedIndex, char keyChar){
       return false;
    }

}
