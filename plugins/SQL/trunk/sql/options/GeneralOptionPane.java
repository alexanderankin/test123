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
  private JCheckBox autoresizeResult;
  private JCheckBox closeWithBuffer;
  private JCheckBox popupSuccessfulEmptyUpdateMessages;


  /**
   *  Constructor for the SqlOptionPane object
   *
   * @since
   */
  public GeneralOptionPane()
  {
    super( "sql_general" );
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
      panel.setLayout( new BorderLayout( 5, 5 ) );
      panel.setBorder( createTitledBorder( "sql.options.common.label" ) );
      panel.add( popupSuccessfulEmptyUpdateMessages = new JCheckBox( jEdit.getProperty( "sql.options.popupSuccessfulEmptyUpdateMessages.label" ) ), BorderLayout.CENTER );
      popupSuccessfulEmptyUpdateMessages.setSelected( SqlTextPublisher.getPopupSuccessfulEmptyUpdateMessages() );
    }
    vbox.add( panel );
    vbox.add( vbox.createVerticalStrut( 5 ) );

    panel = new JPanel();
    {
      panel.setLayout( new GridLayout( 0, 1, 5, 5 ) );
      panel.setBorder( createTitledBorder( "sql.options.recordSetView.label" ) );

      JPanel panel1 = new JPanel();
      {
        panel1.setLayout( new BorderLayout( 5, 5 ) );
        panel1.add( new JLabel( jEdit.getProperty( "sql.options.maxRecs2Show.label" ) ), BorderLayout.WEST );
        panel1.add( maxRecsField = new JTextField( "" + ResultSetWindow.getMaxRecordsToShow() ), BorderLayout.CENTER );
      }
      panel.add( panel1 );

      panel1 = new JPanel();
      {
        panel1.setLayout( new BorderLayout( 5, 5 ) );
        panel1.add( autoresizeResult = new JCheckBox( jEdit.getProperty( "sql.options.autoresizeResult.label" ) ), BorderLayout.CENTER );
        autoresizeResult.setSelected( ResultSetWindow.getAutoResize() );
      }

      panel.add( panel1 );
      panel1 = new JPanel();
      {
        panel1.setLayout( new BorderLayout( 5, 5 ) );
        panel1.add( closeWithBuffer = new JCheckBox( jEdit.getProperty( "sql.options.closeWithBuffer.label" ) ), BorderLayout.CENTER );
        closeWithBuffer.setSelected( ResultSetWindow.getCloseWithBuffer() );
      }
      panel.add( panel1 );
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

    panel = new JPanel();
    {
      panel.setLayout( new BorderLayout( 10, 10 ) );
      panel.setBorder( createTitledBorder( "sql.options.infoPanel.label" ) );
      panel.add( new JLabel( jEdit.getProperty( "sql.options.infoPanel.text" ) ) );
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
    SqlTextPublisher.setPopupSuccessfulEmptyUpdateMessages( popupSuccessfulEmptyUpdateMessages.isSelected() );
    try
    {
      ResultSetWindow.setMaxRecordsToShow( Integer.parseInt( maxRecsField.getText() ) );
    } catch ( NumberFormatException ex )
    {
    }
    ResultSetWindow.setAutoResize( autoresizeResult.getSelectedObjects() != null );
    ResultSetWindow.setCloseWithBuffer( closeWithBuffer.getSelectedObjects() != null );

    SqlToolBar.showToolBar( showToolBar.isSelected() );
    SqlToolBar.showTitle( showTitle.isSelected() );
  }

}

