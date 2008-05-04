package ise.plugin.svn.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.jEdit;
import ise.library.KappaLayout;

public class SubversionOptions implements OptionPane {
   private JPanel panel = null;

   public SubversionOptions( ) {
   }

   public void init() {
      if ( panel != null )
         return ;
      panel = new JPanel( new KappaLayout() );


      final JCheckBox use_tsvn_template = new JCheckBox("Use tsvn:logtemplate property for commit template");
      use_tsvn_template.setSelected(jEdit.getBooleanProperty("ise.plugin.svn.useTsvnTemplate", false));

      panel.add( use_tsvn_template, "0, 0, 1, 1, 0, wh, 5" );

      use_tsvn_template.addActionListener( new ActionListener() {
               public void actionPerformed( ActionEvent ae ) {
                   jEdit.setBooleanProperty("ise.plugin.svn.useTsvnTemplate", use_tsvn_template.isSelected());
               }
            }
                                  );
   }

   public void save() {}

   public Component getComponent() {
      if ( panel == null )
         init();
      return panel;
   }

   public String getName() {
      return "subversion";
   }

}
