/**
 * DbCodeObject.java - Sql Plugin
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
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.swing.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 á×ÇÕÓÔ 2001 Ç.
 */
public class DbCodeObject
{
  /**
   *  Description of the Field
   *
   * @since
   */
  public String name;
  /**
   *  Description of the Field
   *
   * @since
   */
  public String type;
  /**
   *  Description of the Field
   *
   * @since
   */
  public boolean valid;


  /**
   *  Constructor for the DbCodeObject object
   *
   * @param  name   Description of Parameter
   * @param  type   Description of Parameter
   * @param  valid  Description of Parameter
   * @since
   */
  public DbCodeObject( String name, String type, String valid )
  {
    this.name = name;
    this.type = type;
    this.valid = "VALID".equals( valid );
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   * @since
   */
  public int hashCode()
  {
    return name.hashCode() + type.hashCode() + ( valid ? 1 : 0 );
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   * @since
   */
  public String toString()
  {
    return name + ":" + type + ":" + valid;
  }



  public static class CellRenderer extends JLabel
       implements ListCellRenderer
  {

    /**
     *  Constructor for the CellRenderer object
     *
     * @since
     */
    public CellRenderer() { }


    public Component getListCellRendererComponent( JList list,
        Object obj,
        int index,
        boolean isSelected,
        boolean cellHasFocus )
    {
      DbCodeObject dbobj = (DbCodeObject) obj;
      setText( dbobj.type + " " + dbobj.name );
      setForeground( dbobj.valid ? Color.black : Color.red );
      return this;
    }
  }
}


