/**
 * SqlOptionPane.java - Sql Plugin
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package sql.options;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import common.gui.pathbuilder.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;

import sql.*;
import sql.preprocessors.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ������ 2001 �.
 */
public class GeneralOptionPane extends SqlOptionPane
{
  private JTextField maxRecsField;
  private JCheckBox showToolBar;
  private JCheckBox showTitle;


  /**
   *  Constructor for the SqlOptionPane object
   *
   * @since
   */
  public GeneralOptionPane()
  {
    super( "sql.general" );
  }


  /**
   *Description of the Method
   *
   * @since
   */
  public void _init()
  {
    super._init();

    final Box vbox = Box.createVerticalBox();

    JPanel panel = new JPanel();
    {
      panel.setLayout( new BorderLayout( 10, 10 ) );
      panel.setBorder( createTitledBorder( "sql.options.recordSetView.label" ) );

      panel.add( new JLabel( jEdit.getProperty( "sql.options.maxRecs2Show.label" ) ), BorderLayout.WEST );
      panel.add( maxRecsField = new JTextField( "" + ResultSetWindow.getMaxRecordsToShow() ), BorderLayout.CENTER );
    }
    vbox.add( panel );
    vbox.add( vbox.createVerticalStrut( 5 ) );

    panel = new JPanel();
    {
      panel.setLayout( new BorderLayout( 10, 10 ) );
      panel.setBorder( createTitledBorder( "sql.options.toolbar.label" ) );
      final boolean stb = SqlToolBar.showToolBar();
      Log.log( Log.DEBUG, GeneralOptionPane.class, "stb4btn: " + stb );
      showToolBar = new JCheckBox(
          jEdit.getProperty( "sql.options.showToolBar.label" ),
          stb );
      panel.add( showToolBar, BorderLayout.NORTH );
      showToolBar.addChangeListener(
        new ChangeListener()
        {
          public void stateChanged( ChangeEvent evt )
          {
            showTitle.setEnabled( showToolBar.isSelected() );
          }
        } );

      showTitle = new JCheckBox(
          jEdit.getProperty( "sql.options.showTitle.label" ),
          SqlToolBar.showTitle() );
      showTitle.setEnabled( showToolBar.isSelected() );
      panel.add( showTitle, BorderLayout.SOUTH );
    }
    vbox.add( panel );
    vbox.add( vbox.createVerticalStrut( 5 ) );

    add( vbox, BorderLayout.NORTH );
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public void _save()
  {
    int mr = ResultSetWindow.getMaxRecordsToShow();
    try
    {
      mr = Integer.parseInt( maxRecsField.getText() );
    } catch ( NumberFormatException ex )
    {
    }
    ResultSetWindow.setMaxRecordsToShow( mr );
    SqlToolBar.showToolBar( showToolBar.isSelected() );
    SqlToolBar.showTitle( showTitle.isSelected() );

    SqlPlugin.commitProperties();
  }

}

