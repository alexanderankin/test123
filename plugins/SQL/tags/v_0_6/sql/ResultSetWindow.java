/**
 * ResultSetWindow.java - Sql Plugin
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
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.Element;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;

import SqlPlugin;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ?????? 2001 ?.
 */
public class ResultSetWindow extends JPanel implements DockableWindow
{

  protected View view;
  protected JLabel info;
  protected JLabel status;

  protected JComponent dataView = null;


  /**
   *  Constructor for the ResultSetWindow object
   *
   * @param  view  Description of Parameter
   * @since
   */
  public ResultSetWindow( View view )
  {
    this.view = view;

    setLayout( new BorderLayout() );
    add( BorderLayout.NORTH, info = new JLabel() );
    add( BorderLayout.SOUTH, status = new JLabel() );

    updateByModel( null );

    status.setText( jEdit.getProperty( "sql.resultSet.status",
        new Object[]{new Integer( SqlUtils.getThreadGroup().getNumberOfRequest() )} ) );

    SqlUtils.getThreadGroup().addListener(
      new SqlThreadGroup.Listener()
      {
        public void groupChanged( final int numberOfActiveThreads )
        {
          SwingUtilities.invokeLater(
            new Runnable()
            {
              public void run()
              {
                final Object[] args = {new Integer( numberOfActiveThreads )};
                status.setText( jEdit.getProperty( "sql.resultSet.status", args ) );
              }
            } );
        }
      } );
  }


  /**
   *  Gets the Name attribute of the ResultSetWindow object
   *
   * @return    The Name value
   * @since
   */
  public String getName()
  {
    return SqlPlugin.resultSetWinName;
  }


  /**
   *  Gets the Component attribute of the ResultSetWindow object
   *
   * @return    The Component value
   * @since
   */
  public Component getComponent()
  {
    return this;
  }


  /**
   *  Description of the Method
   *
   * @param  model  Description of Parameter
   * @since
   */
  public void updateByModel( Object model )
  {
    if ( dataView != null )
      remove( dataView );

    add( BorderLayout.CENTER, dataView = createDataView( model ) );

    updateStatus( model );

    revalidate();
  }


  /**
   *  Description of the Method
   *
   * @param  model  Description of Parameter
   * @since
   */
  protected void updateStatus( Object model )
  {
    if ( !validPreparedData( model ) )
      return;

    final Object[] datar = (Object[]) model;
    final Integer recCount = (Integer) datar[2];
    final Object[] args = {recCount};
    final int maxRecs = getMaxRecordsToShow();
    if ( recCount.intValue() > maxRecs )
      args[0] = new String( " > " + maxRecs );

    info.setText( jEdit.getProperty( "sql.resultSet.info", args ) );
  }


  /**
   *  Description of the Method
   *
   * @param  model  Description of Parameter
   * @return        Description of the Returned Value
   * @since
   */
  protected JComponent createDataView( Object model )
  {
    if ( model == null )
      return new JLabel( "No Data" );

    if ( model instanceof String )
      return new JLabel( (String) model );

    if ( !validPreparedData( model ) )
      return new JLabel( "What is " + model + "?" );

    final Object[] datar = (Object[]) model;

    final JTable tbl = new JTable( new TableModel( (Vector) datar[0],
        (Vector) datar[1] ) );

    tbl.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

    final JScrollPane scroller = new JScrollPane( tbl );

    return scroller;
  }


  /**
   *  Sets the MaxRecordsToShow attribute of the ResultSetWindow class
   *
   * @param  maxRecs  The new MaxRecordsToShow value
   * @since
   */
  public final static void setMaxRecordsToShow( int maxRecs )
  {
    SqlPlugin.setProperty( "sql.maxRecordsToShow", "" + maxRecs );
  }


  /**
   *  Gets the MaxRecordsToShow attribute of the ResultSetWindow class
   *
   * @return    The MaxRecordsToShow value
   * @since
   */
  public final static int getMaxRecordsToShow()
  {
    try
    {
      return new Integer( SqlPlugin.getProperty( "sql.maxRecordsToShow" ) ).intValue();
    } catch ( NumberFormatException ex )
    {
      return 10;
    } catch ( NullPointerException ex )
    {
      return 10;
    }
  }


  /**
   *Description of the Method
   *
   * @param  rs                Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   * @since
   */
  public static Object prepareModel( ResultSet rs )
       throws SQLException
  {
    int recCount = 0;

    if ( rs == null )
      return "No Data";

    final ResultSetMetaData rsmd = rs.getMetaData();
    final int colNumber = rsmd.getColumnCount();
    final Vector columnNames = new Vector( colNumber );
    for ( int i = colNumber + 1; --i > 0;  )
      columnNames.insertElementAt( rsmd.getColumnName( i ), 0 );

    final Vector rowData = new Vector();
    final int maxRecs = getMaxRecordsToShow();

    while ( rs.next() )
    {
      if ( ++recCount > maxRecs )
        break;

      final Vector aRow = new Vector( colNumber );
      for ( int i = colNumber + 1; --i > 0;  )
        aRow.insertElementAt( rs.getString( i ), 0 );

      rowData.addElement( aRow );
    }

    return new Object[]
        {rowData,
        columnNames,
        new Integer( recCount )};
  }


  /**
   *Description of the Method
   *
   * @param  data  Description of Parameter
   * @return       Description of the Returned Value
   * @since
   */
  protected static boolean validPreparedData( Object data )
  {
    return ( data instanceof Object[] &&
        ( (Object[]) data ).length == 3 );
  }


  protected class TableModel extends AbstractTableModel
  {
    private Vector rowData;
    private Vector columnHeaders;


    /**
     *  Constructor for the TableModel object
     *
     * @param  rowData        Description of Parameter
     * @param  columnHeaders  Description of Parameter
     * @since
     */
    public TableModel( Vector rowData, Vector columnHeaders )
    {
      this.rowData = rowData;
      this.columnHeaders = columnHeaders;
    }


    public int getRowCount()
    {
      return rowData.size();
    }


    public int getColumnCount()
    {
      return columnHeaders.size();
    }


    public Object getValueAt( int r, int c )
    {
      return ( (Vector) rowData.elementAt( r ) ).elementAt( c );
    }


    public String getColumnName( int c )
    {
      return columnHeaders.elementAt( c ).toString();
    }


    public boolean isCellEditable( int r, int c )
    {
      return false;
    }
  }

}

