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

import common.gui.pathbuilder.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;

import sql.preprocessors.*;

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
  private JList allSpecialCommentsLst;

  private JButton addServerBtn;
  private JButton editServerBtn;
  private JButton delServerBtn;
  private JButton addSpecialCommentBtn;
  private JButton editSpecialCommentBtn;
  private JButton delSpecialCommentBtn;
  private Map allServers;
  private java.util.List allSpecialComments;

  private PathBuilder pathBuilder;
  private JTextField maxRecsField;
  private JTextField specialCommentField;

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
    allSpecialCommentsLst = new JList();

    Box vp = Box.createVerticalBox();
    {
      vp.add( vp.createVerticalStrut( 10 ) );

      Box hp = Box.createHorizontalBox();
      {
        hp.add( hp.createHorizontalStrut( 40 ) );

        hp.add( new JLabel( SqlPlugin.Icon ) );

        hp.add( hp.createHorizontalStrut( 40 ) );
        vp.add( hp );
      }
      vp.add( vp.createVerticalStrut( 10 ) );
    }
    addComponent( vp );

    JPanel panel = new JPanel();
    {
      panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
      panel.setBorder( createTitledBorder( jEdit.getProperty( "sql.options.servers.label" ) ) );

      Box hp = Box.createHorizontalBox();
      {
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
      }
      panel.add( hp );

      panel.add( Box.createVerticalStrut( 10 ) );
    }
    addComponent( panel );

    panel = new JPanel();
    {
      panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
      panel.setBorder( createTitledBorder( jEdit.getProperty( "sql.options.jdbcClassPath.label" ) ) );
      pathBuilder = new PathBuilder();
      {
        pathBuilder.setMoveButtonsEnabled( false );
        pathBuilder.setFileFilter( new ClasspathFilter() );
        pathBuilder.setFileSelectionMode( JFileChooser.FILES_ONLY );
      }
      panel.add( pathBuilder );
    }
    addComponent( panel );

    panel = new JPanel();
    {
      panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
      panel.setBorder( createTitledBorder( jEdit.getProperty( "sql.options.recordSetView.label" ) ) );

      Box hp = Box.createHorizontalBox();
      {
        hp.add( new JLabel( jEdit.getProperty( "sql.options.maxRecs2Show.label" ) ) );

        hp.add( vp.createHorizontalStrut( 10 ) );

        hp.add( maxRecsField = new JTextField( "" + ResultSetWindow.getMaxRecordsToShow() ) );

        hp.add( hp.createHorizontalGlue() );
      }
      panel.add( hp );
    }
    addComponent( panel );

    panel = new JPanel();
    {
      panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
      panel.setBorder( createTitledBorder( jEdit.getProperty( "sql.options.specialCommentTitle.label" ) ) );

      Box hp = Box.createHorizontalBox();
      {

        hp.add( vp.createHorizontalStrut( 10 ) );

        hp.add( hp.createHorizontalGlue() );

        hp.add( allSpecialCommentsLst );

        hp.add( hp.createHorizontalGlue() );

        panel.add( hp );

        hp = Box.createHorizontalBox();

        hp.add( vp.createHorizontalStrut( 10 ) );

        hp.add( new JLabel( jEdit.getProperty( "sql.options.specialComment.label" ) ) );

        hp.add( specialCommentField = new JTextField( "" ) );

        hp.add( hp.createHorizontalGlue() );

        panel.add( hp );

        panel.add( Box.createVerticalStrut( 10 ) );

        hp = Box.createHorizontalBox();
        hp.add( hp.createHorizontalStrut( 10 ) );

        hp.add( hp.createHorizontalGlue() );

        addSpecialCommentBtn = new JButton( jEdit.getProperty( "sql.options.addSpecialCommentBtn.label" ) );
        hp.add( addSpecialCommentBtn );

        hp.add( hp.createHorizontalStrut( 10 ) );

        delSpecialCommentBtn = new JButton( jEdit.getProperty( "sql.options.delSpecialCommentBtn.label" ) );
        hp.add( delSpecialCommentBtn );

        hp.add( hp.createHorizontalGlue() );

        hp.add( hp.createHorizontalStrut( 10 ) );

      }
      panel.add( hp );
    }
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

    specialCommentField.addKeyListener(
      new KeyAdapter()
      {
        public void keyReleased( KeyEvent e )
        {
          updateSpecialCommentsButtons();
        }
      } );

    allSpecialCommentsLst.addListSelectionListener(
      new ListSelectionListener()
      {
        public void valueChanged( ListSelectionEvent evt )
        {
          updateSpecialCommentsButtons();
        }
      } );

    addSpecialCommentBtn.addActionListener(
      new ActionListener()
      {
        public void actionPerformed( ActionEvent evt )
        {
          final String text = specialCommentField.getText();
          allSpecialComments.add( text );
          MiscUtilities.quicksort( allSpecialComments, new MiscUtilities.StringCompare() );

          updateSpecialCommentList();
        }
      } );

    delSpecialCommentBtn.addActionListener(
      new ActionListener()
      {
        public void actionPerformed( ActionEvent evt )
        {
          final String text = (String) allSpecialCommentsLst.getSelectedValue();
          if ( text == null )
            return;
          allSpecialComments.remove( text );
          updateSpecialCommentList();
        }
      } );

    allSpecialComments = SpecialCommentRemover.getAllSpecialComments();

    updateSpecialCommentList();

    final String paths[] = SqlPlugin.getJdbcClassPath();
    jdbcClassPath = new TreeMap();
    for ( int i = paths.length; --i >= 0;  )
      jdbcClassPath.put( paths[i], paths[i] );

    pathBuilder.setPathArray( (String[]) jdbcClassPath.values().toArray( new String[0] ) );

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
    for ( Iterator e = allServers.values().iterator(); e.hasNext();  )
    {
      final SqlServerRecord rec = (SqlServerRecord) e.next();
      rec.save();
    }

    final SqlServerRecord selrec = (SqlServerRecord) allServersLst.getSelectedValue();
    if ( selrec != null )
      SqlUtils.setSelectedServerName( selrec.getName() );
    else
      SqlUtils.setSelectedServerName( null );

    int mr = ResultSetWindow.getMaxRecordsToShow();
    try
    {
      mr = Integer.parseInt( maxRecsField.getText() );
    } catch ( NumberFormatException ex )
    {
    }
    ResultSetWindow.setMaxRecordsToShow( mr );

    SqlPlugin.setJdbcClassPath( pathBuilder.getPathArray() );

    SqlPlugin.commitProperties();

    SqlPlugin.registerJdbcClassPath();

    SpecialCommentRemover.save( allSpecialComments );
  }


  /**
   *  Description of the Method
   *
   * @param  comp  The feature to be added to the Component attribute
   * @since
   */
  public void addComponent( Component comp )
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
   *  Description of the Method
   *
   * @since
   */
  protected void updateServerList()
  {
    allServers = SqlServerRecord.getAllRecords();

    final java.util.List allServersV = new ArrayList();
    for ( Iterator e = allServers.values().iterator(); e.hasNext();  )
    {
      final SqlServerRecord sr = (SqlServerRecord) e.next();
      allServersV.add( sr );
    }

    MiscUtilities.quicksort( allServersV, new MiscUtilities.StringCompare() );

    allServersLst.setListData( allServersV.toArray() );

    final String srv2select = SqlUtils.getSelectedServerName();

    final Object selSrv = srv2select == null ? null : allServers.get( srv2select );
    allServersLst.setSelectedValue( selSrv, true );

    updateServerListButtons();
  }


  /**
   *  Description of the Method
   */
  protected void updateSpecialCommentList()
  {
    allSpecialCommentsLst.setListData( allSpecialComments.toArray() );

    updateSpecialCommentsButtons();
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


  private void updateSpecialCommentsButtons()
  {
    final boolean isAny = allSpecialCommentsLst.getSelectedIndex() != -1;
    boolean isText = false;
    try
    {

      isText = specialCommentField.getText().indexOf( "?" ) == -1 ? true : false;
    } catch ( NullPointerException ex )
    {
      Log.log( Log.ERROR, SqlOptionPane.class, ex );
    }
    delSpecialCommentBtn.setEnabled( isAny );
    addSpecialCommentBtn.setEnabled( isText );

  }
}

