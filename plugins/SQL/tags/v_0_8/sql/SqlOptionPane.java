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
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

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
  private TreeMap jdbcClassPath;

  private JList allServersLst;
  private JButton addServerBtn;
  private JButton editServerBtn;
  private JButton delServerBtn;
  private Hashtable allServers;

  private JList jdbcClassPathLst;
  private JButton addClassPathBtn;
  private JButton delClassPathBtn;

  private JTextField maxRecsField;

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
   *Description of the Method
   *
   * @since
   */
  public void _init()
  {
    allServersLst = new JList();

    Box vp = Box.createVerticalBox();
    vp.add( vp.createVerticalStrut( 10 ) );

    Box hp = Box.createHorizontalBox();
    hp.add( hp.createHorizontalStrut( 40 ) );

    hp.add( new JLabel( SqlPlugin.Icon ) );

    hp.add( hp.createHorizontalStrut( 40 ) );
    vp.add( hp );
    vp.add( vp.createVerticalStrut( 10 ) );
    addComponent( vp );

    JPanel panel = new JPanel();
    panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
    panel.setBorder( createTitledBorder( jEdit.getProperty( "sql.options.servers.label" ) ) );

    hp = Box.createHorizontalBox();
    hp.add( Box.createHorizontalStrut( 10 ) );

    hp.add( hp.createHorizontalGlue() );

    hp.add( allServersLst );

    hp.add( hp.createHorizontalGlue() );
    panel.add( hp );

    panel.add( Box.createVerticalStrut( 10 ) );

    hp = Box.createHorizontalBox();
    hp.add( hp.createHorizontalStrut( 10 ) );

    hp.add( hp.createHorizontalGlue() );

    addServerBtn = new JButton( jEdit.getProperty( "sql.options.addServerBtn.label" ) );
    hp.add( addServerBtn );

    hp.add( hp.createHorizontalStrut( 10 ) );

    editServerBtn = new JButton( jEdit.getProperty( "sql.options.editServerBtn.label" ) );
    hp.add( editServerBtn );

    hp.add( hp.createHorizontalStrut( 10 ) );

    delServerBtn = new JButton( jEdit.getProperty( "sql.options.delServerBtn.label" ) );
    hp.add( delServerBtn );

    hp.add( hp.createHorizontalGlue() );

    hp.add( hp.createHorizontalStrut( 10 ) );
    panel.add( hp );

    panel.add( Box.createVerticalStrut( 10 ) );

    addComponent( panel );

    panel = new JPanel();
    panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
    panel.setBorder( createTitledBorder( jEdit.getProperty( "sql.options.jdbcClassPath.label" ) ) );

    hp = Box.createHorizontalBox();

    hp.add( Box.createHorizontalStrut( 10 ) );

    hp.add( hp.createHorizontalGlue() );

    hp.add( jdbcClassPathLst = new JList( SqlPlugin.getJdbcClassPath() ) );

    hp.add( hp.createHorizontalGlue() );
    panel.add( hp );

    panel.add( Box.createVerticalStrut( 10 ) );

    hp = Box.createHorizontalBox();
    hp.add( hp.createHorizontalStrut( 10 ) );

    hp.add( hp.createHorizontalGlue() );

    addClassPathBtn = new JButton( jEdit.getProperty( "sql.options.addClassPathBtn.label" ) );
    hp.add( addClassPathBtn );

    hp.add( hp.createHorizontalStrut( 10 ) );

    delClassPathBtn = new JButton( jEdit.getProperty( "sql.options.delClassPathBtn.label" ) );
    hp.add( delClassPathBtn );

    hp.add( hp.createHorizontalGlue() );

    hp.add( hp.createHorizontalStrut( 10 ) );
    panel.add( hp );

    panel.add( Box.createVerticalStrut( 10 ) );

    panel.add( Box.createVerticalStrut( 10 ) );

    addComponent( panel );

    panel = new JPanel();
    panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
    panel.setBorder( createTitledBorder( jEdit.getProperty( "sql.options.recordSetView.label" ) ) );

    hp = Box.createHorizontalBox();
    hp.add( new JLabel( jEdit.getProperty( "sql.options.maxRecs2Show.label" ) ) );

    hp.add( vp.createHorizontalStrut( 10 ) );

    hp.add( maxRecsField = new JTextField( "" + ResultSetWindow.getMaxRecordsToShow() ) );

    hp.add( hp.createHorizontalGlue() );
    panel.add( hp );

    addComponent( panel );

    allServersLst.addListSelectionListener(
      new ListSelectionListener()
      {
        public void valueChanged( ListSelectionEvent evt )
        {
          updateServerListButtons();
        }
      } );

    addServerBtn.addActionListener(
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
          updateServerList();
        }
      }
         );

    editServerBtn.addActionListener(
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

    delServerBtn.addActionListener(
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
            updateServerList();
          }
        }
      }
         );

    updateServerList();

    jdbcClassPathLst.addListSelectionListener(
      new ListSelectionListener()
      {
        public void valueChanged( ListSelectionEvent evt )
        {
          updateClassPathButtons();
        }
      } );

    addClassPathBtn.addActionListener(
      new ActionListener()
      {
        public void actionPerformed( ActionEvent evt )
        {
          final JFileChooser fc = new JFileChooser();

          fc.setFileFilter(
            new FileFilter()
            {
              public String getDescription()
              {
                return jEdit.getProperty( "sql.options.jdbcClassPath.filterDescr" );
              }


              public boolean accept( java.io.File file )
              {
                if ( file.isDirectory() )
                  return true;

                if ( file.isHidden() )
                  return false;

                final String path = file.getAbsolutePath().toLowerCase();
                return path.endsWith( ".zip" ) ||
                    path.endsWith( ".jar" );
              }
            } );
          final int res = fc.showOpenDialog( parentFrame );
          if ( res != fc.APPROVE_OPTION )
            return;
          final String fileName = fc.getSelectedFile().getAbsolutePath();
          jdbcClassPath.put( fileName, fileName );
          updateClassPathList();
        }
      } );

    delClassPathBtn.addActionListener(
      new ActionListener()
      {
        public void actionPerformed( ActionEvent evt )
        {
          final String fileName = (String) jdbcClassPathLst.getSelectedValue();
          jdbcClassPath.remove( fileName );
          updateClassPathList();
        }
      } );

    final String paths[] = SqlPlugin.getJdbcClassPath();
    jdbcClassPath = new TreeMap();
    for ( int i = paths.length; --i >= 0;  )
      jdbcClassPath.put( paths[i], paths[i] );

    updateClassPathList();

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

    SqlPlugin.setJdbcClassPath( (String[]) jdbcClassPath.values().toArray( new String[0] ) );

    SqlPlugin.commitProperties();

    SqlPlugin.registerJdbcClassPath();
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  protected void updateServerList()
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

    updateServerListButtons();
  }


  /**
   *Description of the Method
   *
   * @since
   */
  protected void updateClassPathList()
  {
    jdbcClassPathLst.setListData( jdbcClassPath.values().toArray( new String[0] ) );

    updateClassPathButtons();
  }


  /**
   *Description of the Method
   *
   * @since
   */
  protected void updateClassPathButtons()
  {
    final boolean isAny = jdbcClassPathLst.getSelectedIndex() != -1;

    delClassPathBtn.setEnabled( isAny );
  }


  /**
   *  Description of the Method
   *
   * @param  comp  The feature to be added to the Component attribute
   * @since
   */
  protected void addComponent( Component comp )
  {
    GridBagConstraints cons = new GridBagConstraints();
    cons.gridy = y++;
    cons.gridheight = 1;
    cons.gridwidth = cons.REMAINDER;
    cons.fill = GridBagConstraints.HORIZONTAL;
    cons.anchor = GridBagConstraints.WEST;
    cons.weightx = 1.0f;

    gridBag.setConstraints( comp, cons );
    add( comp );
  }


  /**
   *Description of the Method
   *
   * @param  title  Description of Parameter
   * @return        Description of the Returned Value
   * @since
   */
  protected Border createTitledBorder( String title )
  {
    return BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
        title );
  }


  private void updateServerListButtons()
  {
    final boolean isAny = allServersLst.getSelectedIndex() != -1;

    delServerBtn.setEnabled( isAny );
    editServerBtn.setEnabled( isAny );
  }
}

