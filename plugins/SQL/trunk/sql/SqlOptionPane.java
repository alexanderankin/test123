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
package sql;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;

import SqlPlugin;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 á×ÇÕÓÔ 2001 Ç.
 */
public class SqlOptionPane extends AbstractOptionPane
{
  private JList allServersLst;
  private Hashtable allServers;
  private JTextField maxRecsField;
  private JButton addBtn;
  private JButton editBtn;
  private JButton delBtn;

  private JFrame parentFrame = null;


  /**
   *  Constructor for the SqlOptionPane object
   *
   * @since
   */
  public SqlOptionPane()
  {
    super( "sql" );
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public void _init()
  {
    allServersLst = new JList();

    Box hp = Box.createHorizontalBox();
    hp.add( hp.createHorizontalStrut( 40 ) );
    hp.add( new JLabel( SqlPlugin.Icon ) );
    hp.add( hp.createHorizontalStrut( 40 ) );

    Box vp = Box.createVerticalBox();
    vp.add( vp.createVerticalStrut( 10 ) );
    vp.add( hp );
    vp.add( vp.createVerticalStrut( 10 ) );
    addComponent( vp );

    addComponent( jEdit.getProperty( "sql.options.allServers.label" ),
        allServersLst );

    hp = Box.createHorizontalBox();
    hp.add( hp.createHorizontalStrut( 10 ) );
    hp.add( hp.createHorizontalGlue() );
    addBtn = new JButton( jEdit.getProperty( "sql.options.addBtn.label" ) );
    hp.add( addBtn );
    hp.add( hp.createHorizontalStrut( 10 ) );

    editBtn = new JButton( jEdit.getProperty( "sql.options.editBtn.label" ) );
    hp.add( editBtn );
    hp.add( hp.createHorizontalStrut( 10 ) );

    delBtn = new JButton( jEdit.getProperty( "sql.options.delBtn.label" ) );
    hp.add( delBtn );
    hp.add( hp.createHorizontalGlue() );
    hp.add( hp.createHorizontalStrut( 10 ) );

    vp = Box.createVerticalBox();
    vp.add( vp.createVerticalStrut( 10 ) );
    vp.add( hp );
    vp.add( vp.createVerticalStrut( 10 ) );

    hp = Box.createHorizontalBox();
    hp.add( new JLabel( jEdit.getProperty( "sql.options.maxRecs2Show.label" ) ) );
    hp.add( vp.createHorizontalStrut( 10 ) );
    hp.add( maxRecsField = new JTextField( "" + ResultSetWindow.getMaxRecordsToShow() ) );
    vp.add( hp );

    vp.add( vp.createVerticalStrut( 10 ) );
    addComponent( vp );

    addBtn.addActionListener(
      new ActionListener()
      {
        public void actionPerformed( ActionEvent evt )
        {
          final SqlServerDialog dlg = new SqlServerDialog( parentFrame,
              null,
              SqlServerDialog.ADD_MODE );
          dlg.setVisible( true );
          final SqlServerRecord rec = dlg.getResult();
          if ( rec == null )
            return;

          rec.save();
          updateList();
        }
      }
         );

    editBtn.addActionListener(
      new ActionListener()
      {
        public void actionPerformed( ActionEvent evt )
        {
          final SqlServerRecord rec = (SqlServerRecord) allServersLst.getSelectedValue();
          if ( rec == null )
            return;

          final SqlServerDialog dlg = new SqlServerDialog( parentFrame,
              rec,
              SqlServerDialog.EDIT_MODE );
          rec.save();
          dlg.setVisible( true );
        }
      }
         );

    delBtn.addActionListener(
      new ActionListener()
      {
        public void actionPerformed( ActionEvent evt )
        {
          final SqlServerRecord rec = (SqlServerRecord) allServersLst.getSelectedValue();
          if ( rec == null )
            return;

          final SqlServerDialog dlg = new SqlServerDialog( parentFrame,
              rec,
              SqlServerDialog.DEL_MODE );
          dlg.setVisible( true );

          if ( dlg.getResult() != null )
          {
            rec.delete();
            updateList();
          }
        }
      }
         );

    updateList();

    Component cp = this;
    while ( cp != null )
    {
      cp = cp.getParent();
      if ( cp instanceof JFrame )
      {
        parentFrame = (JFrame) cp;
        break;
      }
    }
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public void updateList()
  {
    allServers = SqlServerRecord.getAllRecords();

    final Vector allServersV = new Vector();
    for ( Enumeration e = allServers.elements(); e.hasMoreElements();  )
    {
      final SqlServerRecord sr = (SqlServerRecord) e.nextElement();
      allServersV.addElement( sr );
    }

    MiscUtilities.quicksort( allServersV, new MiscUtilities.StringCompare() );

    allServersLst.setListData( allServersV );

    final String srv2select = SqlUtils.getSelectedServerName();

    final Object selSrv = srv2select == null ? null : allServers.get( srv2select );
    allServersLst.setSelectedValue( selSrv, true );

    updateButtons();
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public void _save()
  {
    for ( Enumeration e = allServers.elements(); e.hasMoreElements();  )
    {
      final SqlServerRecord rec = (SqlServerRecord) e.nextElement();
      rec.save();
    }

    final SqlServerRecord selrec = (SqlServerRecord) allServersLst.getSelectedValue();
    if ( selrec != null )
      SqlUtils.setSelectedServerName( selrec.getName() );

    int mr = ResultSetWindow.getMaxRecordsToShow();
    try
    {
      mr = Integer.parseInt( maxRecsField.getText() );
    } catch ( NumberFormatException ex )
    {
    }
    ResultSetWindow.setMaxRecordsToShow( mr );

    SqlPlugin.commitProperties();
  }


  private void updateButtons()
  {
    final boolean isAny = allServersLst.getSelectedIndex() != -1;

    delBtn.setEnabled( isAny );
    editBtn.setEnabled( isAny );
  }
}

