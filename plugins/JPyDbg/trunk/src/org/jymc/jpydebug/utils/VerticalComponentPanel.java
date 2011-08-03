/*
 * VerticqalElementPanel.java
 *
 * Created on October 13, 2005, 12:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jymc.jpydebug.utils;

import javax.swing.* ; 
import java.awt.* ;

/**
 *
 * @author jean-yves
 */
public class VerticalComponentPanel
extends JPanel
{
  
  /**
     * Creates a new instance of VerticalComponentPanel
     */
  public VerticalComponentPanel( JComponent label , JComponent field )
  {
    setLayout ( new GridLayout(1,1) ) ; 	
    Box b = Box.createHorizontalBox() ;   
      b.add(label) ; 
      b.add( Box.createHorizontalStrut(10)) ;
      b.add( Box.createVerticalGlue() ) ;
      // prevent component to resize in ugly ways
      Dimension pref = new Dimension( 
	                              field.getMaximumSize().width ,
	                              field.getPreferredSize().height 
	                             ) ; 
      field.setMaximumSize(pref) ;
      b.add(field) ; 
      b.add( Box.createVerticalGlue() ) ;
      add(b) ; 
   }	
    
}
  
