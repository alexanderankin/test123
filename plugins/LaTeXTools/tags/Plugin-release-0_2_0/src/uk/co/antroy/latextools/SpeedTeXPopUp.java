package uk.co.antroy.latextools;

import java.awt.event.*;

import java.util.*;


//import java.awt.*;
//import java.awt.geom.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.util.*;


//import java.text.*;
class SpeedTeXPopUp
  extends JWindow {

  //~ Instance/static variables ...............................................

  private JList list;
  private DefaultListModel model;
  private SpeedTeX sptex;

  //~ Constructors ............................................................

  /**
   * Creates a new SpeedTeXPopUp object.
   * 
   * @param spt ¤
   */
  public SpeedTeXPopUp(SpeedTeX spt) {
    super(jEdit.getActiveView());
    this.sptex = spt;
    init();
  }

  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @param input ¤
   */
  public void add(String input) {
    model.addElement(input);
  }

  /**
   * ¤
   * 
   * @param input ¤
   */
  public void add(String[] input) {

    for (int i = 0; i < input.length; i++) {
      add(input[i]);
    }
  }

  /**
   * ¤
   */
  public void show() {
    this.pack();
    this.show();
  }

  private void init() {
    model = new DefaultListModel();
    list = new JList();
    this.getContentPane().add(list);
  }
}
