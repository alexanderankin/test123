/**
 * SqlServerType.java - Sql Plugin
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

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.*;

import SqlPlugin;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    26 ������ 2001 �.
 */
public class SqlServerType extends Properties
{

  protected Hashtable connectionParameters = new Hashtable();

  protected Hashtable statements = new Properties();

  protected SqlSubVFS vfs = null;

  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String NAME_PROPERTY = "name";
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String DRIVER_PROPERTY = "driver";
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String VFS_PROPERTY = "vfs";
  /**
   *  Description of the Field
   *
   * @since
   */
  public final static String MODE_NAME_PROPERTY = "editMode";

  protected static Hashtable allTypes = new Hashtable();

  protected static DocumentBuilder docBuilder = null;

  private final static String[] codebaseFiles =
      {
      "/sql/serverTypes/oracle.xml",
      "/sql/serverTypes/mysql.xml",
      "/sql/serverTypes/db2Local.xml",
      "/sql/serverTypes/db2Remote.xml",
      "/sql/serverTypes/pgsql.xml",
      "/sql/serverTypes/ASA.xml"
      };


  /**
   *  Constructor for the SqlServerType object
   *
   * @since
   */
  protected SqlServerType() { }


  /**
   *  Gets the EditModeName attribute of the SqlServerType object
   *
   * @return    The EditModeName value
   * @since
   */
  public String getEditModeName()
  {
    final String name = getProperty( MODE_NAME_PROPERTY );

    if ( name == null ||
        name.length() == 0 )
      return SqlPlugin.DEFAULT_EDIT_MODE_NAME;

    return name;
  }


  /**
   *  Gets the SubVFS attribute of the SqlServerType object
   *
   * @return    The SubVFS value
   * @since
   */
  public SqlSubVFS getSubVFS()
  {
    if ( vfs != null )
      return vfs;

    final String className = getProperty( VFS_PROPERTY );

    try
    {
      vfs = (SqlSubVFS) SqlPlugin.class.getClassLoader().loadClass( className ).newInstance();
    } catch ( Exception ex )
    {
      Log.log( Log.ERROR, SqlServerType.class,
          "Error instantiating " + className + ", using default" );
      vfs = new SqlSubVFS();
    }
    return vfs;
  }


  /**
   *  Gets the ConnectionParameters attribute of the SqlServerType object
   *
   * @return    The ConnectionParameters value
   * @since
   */
  public Hashtable getConnectionParameters()
  {
    return connectionParameters;
  }


  /**
   *  Gets the Name attribute of the SqlServerType object
   *
   * @return    The Name value
   * @since
   */
  public String getName()
  {
    return getProperty( NAME_PROPERTY );
  }


  /**
   *  Gets the Statement attribute of the SqlServerType object
   *
   * @param  purpose  Description of Parameter
   * @return          The Statement value
   * @since
   */
  public Statement getStatement( String purpose )
  {
    return (Statement) statements.get( purpose );
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   * @since
   */
  public boolean register()
  {
    Log.log( Log.DEBUG, SqlServerType.class,
        "registering driver " + getName() );
    final String drName = getProperty( DRIVER_PROPERTY );
    Log.log( Log.DEBUG, SqlServerType.class,
        "  driver class: " + drName );

    // no driver required - OK, no problem
    if ( drName == null )
      return true;

    try
    {
      Class.forName( drName );
      Log.log( Log.DEBUG, SqlServerType.class,
          " registering done OK" );
      return true;
    } catch ( Exception ex )
    {
      Log.log( Log.ERROR, SqlServerType.class,
          "Error loading driver " + drName + ": " + ex );
      //Log.log( Log.ERROR, SqlServerType.class, ex );
      return false;
    }
  }


  /**
   *  Gets the ByName attribute of the SqlServerType class
   *
   * @param  name  Description of Parameter
   * @return       The ByName value
   * @since
   */
  public static SqlServerType getByName( String name )
  {
    return (SqlServerType) allTypes.get( name );
  }


  /**
   *  Gets the AllTypes attribute of the SqlServerType class
   *
   * @return    The AllTypes value
   * @since
   */
  public static Hashtable getAllTypes()
  {
    return allTypes;
  }


  /**
   *  Description of the Method
   *
   * @param  fileName  Description of Parameter
   * @return           Description of the Returned Value
   * @since
   */
  public static SqlServerType loadFromFile( String fileName )
  {
    Log.log( Log.DEBUG, SqlServerType.class,
        "  Loading SQL server type from " + fileName );
    try
    {
      return loadFromStream( new FileInputStream( fileName ) );
    } catch ( IOException ex )
    {
      Log.log( Log.ERROR, SqlServerType.class,
          "Could not create stream from file " + fileName );
      Log.log( Log.ERROR, SqlServerType.class,
          ex );
    }
    return null;
  }


  /**
   *  Description of the Method
   *
   * @param  path  Description of Parameter
   * @return       Description of the Returned Value
   * @since
   */
  public static SqlServerType loadFromCodebase( String path )
  {
    final InputStream is = SqlServerType.class.getResourceAsStream( path );
    Log.log( Log.DEBUG, SqlServerType.class,
        "Got system stream for " + path + ": " + is );
    return loadFromStream( is );
  }


  /**
   *  Description of the Method
   *
   * @param  istream  Description of Parameter
   * @return          Description of the Returned Value
   * @since
   */
  public static SqlServerType loadFromStream( InputStream istream )
  {
    //!! XML goes here
    try
    {
      if ( docBuilder == null )
      {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        docBuilder = dbf.newDocumentBuilder();
        docBuilder.setEntityResolver( new EResolver() );
      }

      final Document typeDoc = docBuilder.parse( new BufferedInputStream( istream ) );

      final Element rootElement = typeDoc.getDocumentElement();

      final NodeList children = rootElement.getChildNodes();

      SqlServerType rv = new SqlServerType();

      rv.setProperty( NAME_PROPERTY, rootElement.getAttribute( "name" ) );

      for ( int i = children.getLength(); --i >= 0;  )
      {
        final Node childNode = children.item( i );
        if ( childNode.getNodeType() != childNode.ELEMENT_NODE )
          continue;

        final Element childElement = (Element) childNode;
        if ( "connection".equals( childElement.getTagName() ) )
        {
          final NodeList paramNodes = childElement.getChildNodes();
          for ( int j = paramNodes.getLength(); --j >= 0;  )
          {
            final Node paramNode = paramNodes.item( j );
            if ( paramNode.getNodeType() != childNode.ELEMENT_NODE )
              continue;

            final Element paramElement = (Element) paramNode;
            if ( "parameter".equals( paramElement.getTagName() ) )
            {
              final String name = paramElement.getAttribute( "name" );
              final String defaultValue = paramElement.getAttribute( "default" );
              final String description = paramElement.getAttribute( "description" );

              final ConnectionParameter param = new ConnectionParameter( name,
                  description,
                  defaultValue );

              rv.connectionParameters.put( name, param );
            }
            else
            {
              final String name = paramElement.getTagName();
              final String value = paramElement.getFirstChild().getNodeValue();

              rv.setProperty( "connection." + name, value );
            }
          }

        }
        else
            if ( "statements".equals( childElement.getTagName() ) )
        {
          final NodeList stmtNodes = childElement.getChildNodes();
          for ( int j = stmtNodes.getLength(); --j >= 0;  )
          {
            final Node stmtNode = stmtNodes.item( j );
            if ( stmtNode.getNodeType() != stmtNode.ELEMENT_NODE )
              continue;

            final Element stmtElement = (Element) stmtNode;
            if ( "statement".equals( stmtElement.getTagName() ) )
            {
              final String purpose = stmtElement.getAttribute( "purpose" );
              final String text = stmtElement.getFirstChild().getNodeValue();
              final String substMethod = stmtElement.getAttribute( "substMethod" );

              rv.statements.put( purpose,
                  new Statement( purpose, substMethod, text ) );
            }
          }
        }
        {
          final String name = childElement.getTagName();
          final String value = childElement.getFirstChild().getNodeValue();

          rv.setProperty( name, value );
        }

      }
      Log.log( Log.DEBUG, SqlServerType.class,
          "    SQL server type " + rv.getName() + " is loaded OK" );

      if ( !rv.register() )
        return null;

      return rv;
    } catch ( ParserConfigurationException ex )
    {
      Log.log( Log.ERROR, SqlServerType.class,
          "ParserConfigurationException on getting the server type: " );
      Log.log( Log.ERROR, SqlServerType.class, ex );
    } catch ( SAXException ex )
    {
      Log.log( Log.ERROR, SqlServerType.class,
          "SAXException on getting the server type: " );
      Log.log( Log.ERROR, SqlServerType.class, ex );
    } catch ( IOException ex )
    {
      Log.log( Log.ERROR, SqlServerType.class,
          "IOException on getting the server type: " );
      Log.log( Log.ERROR, SqlServerType.class, ex );
    }
    return null;
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public static void loadAll()
  {
    Log.log( Log.DEBUG, SqlServerType.class,
        ">>Loading global settings!" );
    loadFromDirectory( MiscUtilities.constructPath( jEdit.getJEditHome(),
        "sql",
        "serverTypes" ) );
    // load user catalog
    if ( jEdit.getSettingsDirectory() != null )
    {
      Log.log( Log.DEBUG, SqlServerType.class,
          ">>Loading per-user settings!" );
      final File userTypesDir = new File( MiscUtilities.constructPath(
          jEdit.getSettingsDirectory(),
          "sql",
          "serverTypes" ) );
      if ( !userTypesDir.exists() )
        userTypesDir.mkdirs();

      loadFromDirectory( userTypesDir.getPath() );
    }
    else
      Log.log( Log.NOTICE, SqlServerType.class,
          "No user settings!" );

    loadAllFromCodebase();

    docBuilder = null;
    // just not to waste the memory
  }


  /**
   *Description of the Method
   *
   * @since
   */
  public static void dropAll()
  {
    Log.log( Log.NOTICE, SqlServerType.class,
        "All server types are dropped" );
    allTypes = new Hashtable();
  }


  /**
   *  Description of the Method
   *
   * @since
   */
  public static void loadAllFromCodebase()
  {
    Log.log( Log.DEBUG, SqlServerType.class,
        "Loading SQL server types from codebase" );
    for ( int i = codebaseFiles.length; --i >= 0;  )
    {
      final SqlServerType serverType = loadFromCodebase( codebaseFiles[i] );
      if ( serverType != null )
        allTypes.put( serverType.getName(), serverType );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  directory  Description of Parameter
   * @since
   */
  public static void loadFromDirectory( String directory )
  {
    Log.log( Log.DEBUG, SqlServerType.class,
        "Loading SQL server types from " + directory );
    final File file = new File( directory );

    if ( !( file.exists() && file.isDirectory() ) )
      return;

    final String[] dbTypes = file.list();
    if ( dbTypes == null )
      return;

    MiscUtilities.quicksort( dbTypes, new MiscUtilities.StringICaseCompare() );
    for ( int i = 0; i < dbTypes.length; i++ )
    {
      final String dbType = dbTypes[i];
      if ( !dbType.toLowerCase().endsWith( ".xml" ) )
        continue;

      final String typePath = MiscUtilities.constructPath( directory, dbType );
      final SqlServerType serverType = loadFromFile( typePath );
      if ( serverType != null )
        allTypes.put( serverType.getName(), serverType );
    }
  }


  /**
   *  Description of the Method
   *
   * @param  args  Description of Parameter
   * @since
   */
  public static void main( String args[] )
  {
    loadAll();
  }


  public static class Statement
  {
    private String purpose;
    private int substMethod;
    private String code;

    private MessageFormat fmt;

    public final static int SUBST_SQL = 0;
    public final static int SUBST_TEXT = 1;


    /**
     *  Constructor for the Statement object
     *
     * @param  purpose      Description of Parameter
     * @param  substMethod  Description of Parameter
     * @param  code         Description of Parameter
     * @since
     */
    public Statement( String purpose, int substMethod, String code )
    {
      this.purpose = purpose;
      this.substMethod = substMethod;
      this.code = code;
      switch ( substMethod )
      {
        case SUBST_TEXT:
          fmt = new MessageFormat( code );
          break;
        case SUBST_SQL:
          fmt = null;
      }
    }


    /**
     *  Constructor for the Statement object
     *
     * @param  purpose      Description of Parameter
     * @param  substMethod  Description of Parameter
     * @param  code         Description of Parameter
     * @since
     */
    public Statement( String purpose, String substMethod, String code )
    {
      this( purpose, "text".equals( substMethod ) ? SUBST_TEXT : SUBST_SQL, code );
    }


    public void setParams( PreparedStatement stmt, Object[] args )
         throws SQLException
    {
      if ( args == null )
        return;

      switch ( substMethod )
      {
        case SUBST_TEXT:
          break;
        case SUBST_SQL:
          for ( int i = args.length; --i >= 0;  )
          {
            if ( args[i] instanceof String )
              stmt.setString( i + 1, (String) args[i] );
            else
              stmt.setObject( i + 1, args[i] );
          }
      }
    }


    public String getStatementText( Object[] args )
    {
      if ( args == null )
        return code;

      switch ( substMethod )
      {
        case SUBST_TEXT:
          return fmt.format( args );
        case SUBST_SQL:
          return code;
      }
      return null;
    }
  }


  public static class ConnectionParameter
  {
    private String name;
    private String defaultValue;
    private String description;


    /**
     *  Constructor for the ConnectionParameter object
     *
     * @param  name          Description of Parameter
     * @param  description   Description of Parameter
     * @param  defaultValue  Description of Parameter
     * @since
     */
    public ConnectionParameter( String name,
        String description,
        String defaultValue )
    {
      this.name = name;
      this.description = description;
      this.defaultValue = defaultValue;
    }


    public String getName()
    {
      return name;
    }


    public String getDefaultValue()
    {
      return defaultValue;
    }


    public String getDescription()
    {
      return ( description == null || "".equals( description ) ) ?
          name : description;
    }
  }


  protected static class EResolver implements EntityResolver
  {
    public InputSource resolveEntity( String publicId, String systemId )
    {
      if ( systemId.endsWith( "sqlServerType.dtd" ) )
      {
        final InputStream ist = SqlServerType.class.getClassLoader().getResourceAsStream(
            "sqlServerType.dtd" );
        return new InputSource( ist );
      }
      return null;
    }
  }

}

