/**
 * ResultSetWindowPopup.java - Sql Plugin
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

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import org.gjt.sp.jedit.*;

/**
 * A popup menu for BufferList.
 *
 * @author     svu
 * @created    3 Сентябрь 2001 г.
 */
public class ResultSetWindowPopup extends JPopupMenu
{
  protected View view;
  protected JTable table;


  /**
   *Constructor for the ResultSetWindowPopup object
   *
   * @param  view   Description of Parameter
   * @param  table  Description of Parameter
   * @since
   */
  public ResultSetWindowPopup( View view, JTable table )
  {
    this.view = view;
    this.table = table;
    add( createCopyMenuItem( "copy_all_csv", ", " ) );
    add( createCopyMenuItem( "copy_all_tab", "\t" ) );
  }


  private JMenuItem createCopyMenuItem( String name, String delimiter )
  {
    final String label = jEdit.getProperty( "sql.resultSet.popup." + name + ".label" );
    final JMenuItem mi = new JMenuItem( label );
    mi.setActionCommand( name );
    mi.addActionListener( new CopyActionHandler( delimiter ) );
    return mi;
  }


  /**
   *Description of the Method
   *
   * @param  s  Description of Parameter
   * @return    Description of the Returned Value
   * @since
   */
  public static String csvize( String s )
  {
    if ( s.indexOf( ' ' ) == -1 )
      return s;
    return "\"" + s + "\"";
  }


  class CopyActionHandler implements ActionListener
  {
    protected String delimiter;


    /**
     *Constructor for the CopyActionHandler object
     *
     * @param  delimiter  Description of Parameter
     * @since
     */
    public CopyActionHandler( String delimiter )
    {
      this.delimiter = delimiter;
    }


    public void actionPerformed( ActionEvent evt )
    {
      final String actionCommand = evt.getActionCommand();

      final Registers.Register reg = Registers.getRegister( '$' );// clipboard
      if ( reg == null )
      {
        view.getToolkit().beep();
        return;
      }

      final TableModel model = table.getModel();
      final StringBuffer sb = new StringBuffer();

      final int maxR = model.getRowCount();
      final int maxC = model.getColumnCount();

      for ( int c = maxC; --c >= 0;  )
      {
        sb.insert( 0, csvize( model.getColumnName( c ) ) );
        if ( c != 0 )
          sb.insert( 0, ", " );
      }

      for ( int r = 0; r < maxR; r++ )
      {
        sb.append( '\n' );

        final StringBuffer rowb = new StringBuffer();
        for ( int c = maxC; --c >= 0;  )
        {
          String val = model.getValueAt( r, c ).toString();
          rowb.insert( 0, csvize( val ) );
          if ( c != 0 )
            rowb.insert( 0, delimiter );
        }

        sb.append( new String( rowb ) );
      }

      Registers.setRegister( '$', new String( sb ) );

    }
  }
}

