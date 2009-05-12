//keywords:display messages,errors,warnings
package be.dekamer.programs.allerlei;

import java.awt.*;
import java.io.*;
import java.lang.*;
import java.lang.Math;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import javax.swing.*;


public class Console
 extends JTextArea
{
 /** private final static variables*/
 private final static String $CLSSNM="Console";
 private final static String $CLSSNM_VERSION_STATIC="0.0.0";

 private final static String $LINE_SEPARATOR=System.getProperty("line.separator");

 double maxheigth=-1;

 public Console(int maxheigth)
 {
  super();
  this.maxheigth=maxheigth;
  setLineWrap(true);
  setEditable(false);
  setFocusable(false);
  //setSize(new Dimension(400,maxheigth));
  setPreferredSize(new Dimension(400,maxheigth));
 }
 public Console()
 {
  super();
  setLineWrap(true);
  setEditable(false);
  setFocusable(false);
  setSize(new Dimension(400,50));
 }
 public void setText(java.sql.SQLException sqle)
 {
  Toolkit.getDefaultToolkit().beep();
  String s_sqle=sqle.toString();
  //nog doen test of sqlcode nog niet voorkomt in s_sqle!!!!
  int sqlcode=sqle.getErrorCode();
  super.setText(s_sqle+" sqlcode="+sqlcode);
 }//
 public void setText(Exception excptn)
 {
  Toolkit.getDefaultToolkit().beep();
  super.setText(excptn.toString());
  Throwable thrwbl=excptn.getCause();
  if(thrwbl!=null) super.setText(thrwbl.toString());  
  super.setText(excptn.toString());
 }//
 public void setError(String s)
 {
  Toolkit.getDefaultToolkit().beep();
  super.setText(s);
 }//

 public void append(Throwable thrwbl)
 {
  if(thrwbl!=null)
  {	  
   Toolkit.getDefaultToolkit().beep();  
   super.append($LINE_SEPARATOR+thrwbl.toString());
  }
 }//
 public void append(String strng)
 {
  //int cp0=-1;	 
  //nog doen JTextArea and color !!! only possible in JTextPane??	 
  //http://www.java2s.com/Code/Java/Swing-JFC/ExtensionofJTextPanethatallowstheusertoeasilyappendcoloredtexttothedocument.htm
  //int ep=strng.indexOf("SEVERE: java.lang.Error:");
  //int ep=strng.indexOf("SEVERE:");
  //if(ep>-1)
  //{
   //super.setCaretColor(Color.RED);
   //System.out.println("ep="+ep);
   //ep=65
   //super.setForeground(Color.RED); all lines in window become red
   //cp0=getCaretPosition();
   //System.out.println("cp0="+cp0);
  //}
  super.append($LINE_SEPARATOR+strng);
  //if(cp0>-1)
  //{
  // int cp1=getCaretPosition();	  
  // System.out.println("cp1="+cp1);
  // setSelectedTextColor(Color.RED);
  // select(cp0,cp1);
  //}	  
 }//public void append(String strng)
 public void clear()
 {
  super.setText("");
 }	 
 /**************
 public Dimension getPreferredSize()
 {
  Dimension rv=null;
  System.out.println($CLSSNM+" getPreferredSize()");
  rv=super.getPreferredSize();
  if(maxheigth!=-1)
  {
   //System.out.println($CLSSNM+" rv w="+rv.getWidth()+" h="+rv.getHeight());
   Component parent=getParent();
   Dimension p_dmnsn=parent.getSize(null);
   //System.out.println($CLSSNM+" p_dmns w="+p_dmnsn.getWidth()+" h="+p_dmnsn.getHeight());
   rv.setSize(p_dmnsn.getWidth(),maxheigth);
  }
  return(rv);
 }//public Dimension getPreferredSize()


 public class ColorPane extends JTextPane {


  public void append(Color c, String s) { // better implementation--uses
                      // StyleContext
    StyleContext sc = StyleContext.getDefaultStyleContext();
    AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
        StyleConstants.Foreground, c);

    int len = getDocument().getLength(); // same value as
                       // getText().length();
    setCaretPosition(len); // place caret at the end (with no selection)
    setCharacterAttributes(aset, false);
    replaceSelection(s); // there is no selection, so inserts at caret
  }
 ******/
}//end Console
