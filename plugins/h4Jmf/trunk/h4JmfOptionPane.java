/*
h4Jmf.java
program is free software GNU General Public License
author Herman Vierendeels,Ternat,Belgium
*/
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.*;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.FontSelector;

public class h4JmfOptionPane
extends AbstractOptionPane
{
 private static final Logger logger=Logger.getLogger(h4JmfOptionPane.class.getName());
 private JTextField seconds;
 //
 public h4JmfOptionPane()
 {
  super(h4JmfPlugin.NAME);
 }

 public void _init()
 {
  seconds=new JTextField(jEdit.getProperty(h4JmfPlugin.OPTION_PREFIX+"seconds"));
  addComponent(h4JmfPlugin.OPTION_PREFIX+"seconds",seconds);
 }

 public void _save()
 {
  String sseconds=seconds.getText();
  int iseconds=-1;
  try
  {
   iseconds=Integer.parseInt(sseconds);
   jEdit.setProperty(h4JmfPlugin.OPTION_PREFIX+"seconds",seconds.getText());
  }
  catch(Exception excptn)
  {
   logger.severe(excptn.toString());
  }
 }
 //end AbstractOptionPane implementation
}
