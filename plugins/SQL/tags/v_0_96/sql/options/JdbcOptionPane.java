/**
 * JdbcOptionPane.java - Sql Plugin
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
 * @created    26 á×ÇÕÓÔ 2001 Ç.
 */
public class JdbcOptionPane extends SqlOptionPane
{
  private TreeMap jdbcClassPath;

  private PathBuilder pathBuilder;


  /**
   *  Constructor for the SqlOptionPane object
   *
   * @since
   */
  public JdbcOptionPane()
  {
    super( "sql.jdbc" );
  }


  /**
   *Description of the Method
   *
   * @since
   */
  public void _init()
  {
    super._init();

    JPanel panel = new JPanel();
    {
      panel.setLayout( new BorderLayout( 5, 5 ) );
      panel.setBorder( createTitledBorder( "sql.options.jdbc.classpath.label" ) );
      pathBuilder = new PathBuilder();
      {
        pathBuilder.setMoveButtonsEnabled( false );
        pathBuilder.setFileFilter( new ClasspathFilter() );
        pathBuilder.setFileSelectionMode( JFileChooser.FILES_ONLY );
      }
      panel.add( pathBuilder, BorderLayout.CENTER );
    }
    add( panel, BorderLayout.NORTH );

    final String paths[] = SqlPlugin.getJdbcClassPath();
    jdbcClassPath = new TreeMap();
    for ( int i = paths.length; --i >= 0;  )
      jdbcClassPath.put( paths[i], paths[i] );

    pathBuilder.setPathArray( (String[]) jdbcClassPath.values().toArray( new String[0] ) );
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public void _save()
  {
    SqlPlugin.setJdbcClassPath( pathBuilder.getPathArray() );

    SqlPlugin.registerJdbcClassPath();
  }
}

