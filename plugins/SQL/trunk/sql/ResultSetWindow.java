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

import common.gui.*;

import sql.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ?????? 2001 ?.
 */
public class ResultSetWindow extends JPanel
     implements SqlUtils.SelectedServerListener
{

  protected View view;
  protected JLabel server;
  protected JLabel info;
  protected JLabel status;

  protected JComponent dataView = null;

  protected final static String MAX_RECS_TO_SHOW_PROP = "sql.maxRecordsToShow";


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
    final JPanel p = new JPanel( new BorderLayout() );
    server = new JLabel( "blahblahblah" );
    p.add( BorderLayout.NORTH, server );
    info = new JLabel();
    p.add( BorderLayout.SOUTH, info );

    add( BorderLayout.NORTH, p );
    add( BorderLayout.SOUTH, status = new JLabel() );

    SqlUtils.addSelectedServerListener( this );

    selectedServerChanged( SqlUtils.getSelectedServerName() );

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
   * @param  name  Description of Parameter
   */
  public void selectedServerChanged( String name )
  {
    server.setText(
        jEdit.getProperty( "sql.resultSet.server", new Object[]{name} ) );
  }


  /**
   *  Description of the Method
   *
   * @param  model  Description of Parameter
   * @since
   */
  protected void updateStatus( Object model )
  {
    if ( !( model instanceof Data ) )
      return;
    final int recCount = ( (Data) model ).recCount;
    final Object[] args = {new Integer( recCount )};
    final int maxRecs = getMaxRecordsToShow();
    if ( recCount > maxRecs )
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

    if ( !( model instanceof Data ) )
      return new JLabel( "What is " + model + "?" );

    final Data data = (Data) model;

    final HelpfulJTable tbl = new HelpfulJTable();
    //final JTable tbl = new JTable();

    tbl.setAutoResizeColumns( false );
    //!!tbl.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
    tbl.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

    tbl.setModel( new TableModel( data.rowData, data.columnNames ) );

    tbl.addMouseListener( new MouseHandler( tbl ) );

    tbl.setTableHeader( new TableHeader( tbl, data ) );

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
    SqlPlugin.setProperty( MAX_RECS_TO_SHOW_PROP, "" + maxRecs );
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
      return new Integer( SqlPlugin.getProperty( MAX_RECS_TO_SHOW_PROP ) ).intValue();
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
   * @since
   */
  public final static void clearProperties()
  {
    SqlPlugin.unsetProperty( MAX_RECS_TO_SHOW_PROP );
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
    final String[] columnNames = new String[colNumber];
    final String[] columnTypes = new String[colNumber];
    for ( int i = colNumber + 1; --i > 0;  )
    {
      columnNames[i - 1] = rsmd.getColumnName( i );

      String type = rsmd.getColumnTypeName( i );

      try
      {
        final int precision = rsmd.getPrecision( i );
        if ( precision != 0 )
        {
          final int scale = rsmd.getScale( i );
          type += "[" + precision + ( ( scale == 0 ) ? "" : ( "." + scale ) ) + "]";
        }
      } catch ( Exception ex )
      {
        //Log.log( Log.DEBUG, ResultSetWindow.class, ex );
        /*
         *  SQLException - not supported?
         *  Can also be NumberFormatException - for Oracle's CLOBs
         */
      }

      if ( rsmd.columnNoNulls == rsmd.isNullable( i ) )
        type += "/" + jEdit.getProperty( "sql.resultSet.colHeaders.notNullable" );

      columnTypes[i - 1] = type;
    }

    final java.util.List rowData = new ArrayList();
    final int maxRecs = getMaxRecordsToShow();

    while ( rs.next() )
    {
      if ( ++recCount > maxRecs )
        break;

      final String[] aRow = new String[colNumber];
      int j = 1;
      for ( int i = colNumber; --i >= 0; j++ )
        aRow[j - 1] = col2String( rsmd, rs, j );

      rowData.add( aRow );
    }

    Log.log( Log.DEBUG, ResultSetWindow.class,
        "Got " + rowData.size() + " records in " + columnNames.length + " columns" );
    return new Data
        ( (String[][]) rowData.toArray( new String[0][] ),
        columnNames,
        columnTypes,
        recCount );
  }


  /**
   *  Description of the Method
   *
   * @param  rsmd              Description of Parameter
   * @param  rs                Description of Parameter
   * @param  idx               Description of Parameter
   * @return                   Description of the Returned Value
   * @exception  SQLException  Description of Exception
   */
  protected static String col2String( ResultSetMetaData rsmd, ResultSet rs, int idx )
       throws SQLException
  {
    switch ( rsmd.getColumnType( idx ) )
    {
        case Types.CLOB:
          return "<<CLOB>>";
        case Types.BLOB:
          return "<<BLOB>>";
        default:
          return rs.getString( idx );
    }
  }


  protected class MouseHandler extends MouseAdapter
  {
    protected JTable table;


    /**
     *Constructor for the MouseHandler object
     *
     * @param  table  Description of Parameter
     * @since
     */
    public MouseHandler( JTable table )
    {
      this.table = table;
    }


    public void mousePressed( MouseEvent evt )
    {
      final Point p = evt.getPoint();

      if ( ( evt.getModifiers() & MouseEvent.BUTTON3_MASK ) != 0 )
      {
        final ResultSetWindowPopup rswp = new ResultSetWindowPopup( view, table );
        rswp.show( table, p.x + 1, p.y + 1 );
        evt.consume();
      }
    }
  }


  protected static class TableModel extends AbstractTableModel
  {
    private String rowData[][];
    private String columnHeaders[];


    /**
     *  Constructor for the TableModel object
     *
     * @param  rowData        Description of Parameter
     * @param  columnHeaders  Description of Parameter
     * @since
     */
    public TableModel( String rowData[][], String columnHeaders[] )
    {
      this.rowData = rowData;// can be 0 records ...
      this.columnHeaders = columnHeaders;
    }


    public int getRowCount()
    {
      return rowData.length;
    }


    public int getColumnCount()
    {
      return columnHeaders.length;
    }


    public Object getValueAt( int r, int c )
    {
      if ( r >= rowData.length || r < 0 )
        return null;
      if ( c >= columnHeaders.length || c < 0 )
        return null;

      return rowData[r][c];
    }


    public String getColumnName( int c )
    {
      return columnHeaders[c];
    }


    public boolean isCellEditable( int r, int c )
    {
      return false;
    }
  }


  protected static class TableHeader extends JTableHeader
  {
    protected String types[];


    /**
     *Constructor for the TableHeader object
     *
     * @param  data   Description of Parameter
     * @param  table  Description of Parameter
     * @since
     */
    public TableHeader( JTable table, Data data )
    {
      super( table.getColumnModel() );
      types = data.columnTypes;
    }


    public String getToolTipText( MouseEvent evt )
    {
      final Point p = evt.getPoint();
      if ( p == null )
        return null;
      final int colNo = columnAtPoint( p );
      if ( colNo == -1 )
        return null;
      return types[colNo];
    }
  }


  protected static class Data
  {
    public String rowData[][];
    public String columnNames[];
    public String columnTypes[];
    public int recCount;


    /**
     *Constructor for the Data object
     *
     * @param  rowData      Description of Parameter
     * @param  recCount     Description of Parameter
     * @param  columnNames  Description of Parameter
     * @param  columnTypes  Description of Parameter
     * @since
     */
    public Data( String rowData[][],
        String columnNames[],
        String columnTypes[],
        int recCount )
    {
      this.rowData = rowData;
      this.columnNames = columnNames;
      this.columnTypes = columnTypes;
      this.recCount = recCount;
    }
  }

}

