/**
 *  SqlSubVFS.java - Sql Plugin Copyright (C) 2001 Sergey V. Udaltsov
 *  svu@users.sourceforge.net This program is free software; you can
 *  redistribute it and/or modify it under the terms of the GNU General Public
 *  License as published by the Free Software Foundation; either version 2 of
 *  the License, or any later version. This program is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details. You should have received a copy of
 *  the GNU General Public License along with this program; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
 */
package sql;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.*;

import sql.*;

/**
 *  Description of the Class
 *
 *@author     svu
 *@created    05 December 2003
 */
public class SqlSubVFS
{

	/**
	 *  Description of the Field
	 *
	 *@since
	 */
	public VFS sqlVFS;

	/**
	 *  Description of the Field
	 */
	protected final static int TABLEGROUP_LEVEL = 2;
	/**
	 *  Description of the Field
	 */
	protected final static int TABLE_LEVEL = 3;


	/**
	 *  Description of the Method
	 *
	 *@param  session          Description of Parameter
	 *@param  path             Description of Parameter
	 *@param  comp             Description of Parameter
	 *@param  rec              Description of Parameter
	 *@param  level            Description of Parameter
	 *@return                  Description of the Returned Value
	 *@exception  IOException  Description of Exception
	 *@since
	 */
	public VFS.DirectoryEntry[] _listDirectory( Object session,
	                                            String path,
	                                            Component comp,
	                                            SqlServerRecord rec,
	                                            int level )
		throws IOException
	{
		Log.log( Log.DEBUG, SqlSubVFS.class,
			"Listing " + path );
		VFS.DirectoryEntry[] retval = null;

		int i;

		switch ( level )
		{
				case SqlVFS.DB_LEVEL:

					retval = getEntriesFromDb( session,
						path,
						comp,
						rec,
						level,
						"selectTableGroups",
						null );

					break;
				case TABLEGROUP_LEVEL:

					final String tgName = SqlVFS.getPathComponent( path, TABLEGROUP_LEVEL );

					retval = getEntriesFromDb( session,
						path,
						comp,
						rec,
						level,
						"selectTablesInGroup",
						new Object[]{tgName} );

					break;
		}
		Log.log( Log.DEBUG, SqlSubVFS.class,
			"Listed total " + ( retval == null ? -1 : retval.length ) + " items" );
		return retval;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  session          Description of Parameter
	 *@param  comp             Description of Parameter
	 *@param  level            Description of Parameter
	 *@param  rec              Description of the Parameter
	 *@return                  Description of the Returned Value
	 *@exception  IOException  Description of Exception
	 *@since
	 */
	public VFS.DirectoryEntry _getDirectoryEntry( Object session, VFSObjectRec rec, Component comp, int level )
		throws IOException
	{
		Log.log( Log.DEBUG, SqlSubVFS.class, "Getting entry for [" + rec.path + "]/[" + rec.size + "]" );
		return
			new SqlDirectoryEntry( rec, level == TABLE_LEVEL ?
			VFS.DirectoryEntry.FILE :
			VFS.DirectoryEntry.DIRECTORY );
	}


	/**
	 *  Description of the Method
	 *
	 *@param  view    Description of Parameter
	 *@param  buffer  Description of Parameter
	 *@param  path    Description of Parameter
	 *@param  level   Description of Parameter
	 *@return         Description of the Returned Value
	 *@since
	 */
	public boolean afterLoad( final View view, final Buffer buffer, final String path, int level )
	{
		buffer.setBooleanProperty( SqlVFS.RUN_ON_LOAD_PROPERTY, true );

		return true;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  vfs              Description of Parameter
	 *@param  session          Description of Parameter
	 *@param  path             Description of Parameter
	 *@param  ignoreErrors     Description of Parameter
	 *@param  comp             Description of Parameter
	 *@param  level            Description of Parameter
	 *@return                  Description of the Returned Value
	 *@exception  IOException  Description of Exception
	 *@since
	 */
	public InputStream _createInputStream( VFS vfs, Object session, String path,
	                                       boolean ignoreErrors, Component comp, int level )
		throws IOException
	{
		return new ByteArrayInputStream( ( "SELECT * FROM " +
			vfs.getFileName( SqlVFS.normalize( vfs.getParentOfPath( path ) ) ) + "." +
			vfs.getFileName( path ) ).getBytes() );
	}


	/**
	 *  Gets the SqlVFS attribute of the SqlSubVFS object
	 *
	 *@return    The SqlVFS value
	 *@since
	 */
	protected VFS getSqlVFS()
	{
		if ( sqlVFS == null )
		{
			sqlVFS = VFSManager.getVFSForProtocol( SqlVFS.PROTOCOL );
		}
		return sqlVFS;
	}


	/**
	 *  Gets the EntriesFromDb attribute of the SqlSubVFS object
	 *
	 *@param  session          Description of Parameter
	 *@param  path             Description of Parameter
	 *@param  comp             Description of Parameter
	 *@param  rec              Description of Parameter
	 *@param  level            Description of Parameter
	 *@param  stmtPurpose      Description of Parameter
	 *@param  stmtParams       Description of Parameter
	 *@return                  The EntriesFromDb value
	 *@exception  IOException  Description of Exception
	 *@since
	 */
	protected VFS.DirectoryEntry[] getEntriesFromDb( Object session,
	                                                 String path,
	                                                 Component comp,
	                                                 SqlServerRecord rec,
	                                                 int level,
	                                                 String stmtPurpose,
	                                                 Object[] stmtParams )
		throws IOException
	{
		final java.util.List tableGroups = getVFSObjectsList( rec,
			stmtPurpose,
			stmtParams );

		if ( tableGroups == null )
			return null;

		final VFS.DirectoryEntry[] retval = new VFS.DirectoryEntry[tableGroups.size()];
		int i = 0;
		for ( Iterator e = tableGroups.iterator(); e.hasNext();  )
		{
			final VFSObjectRec r = (VFSObjectRec) e.next();
			r.setDir( path );
			retval[i++] =
				_getDirectoryEntry( session, r, comp, level + 1 );
		}
		return retval;
	}


	/**
	 *  Gets the VFSObjects attribute of the SqlSubVFS object
	 *
	 *@param  rec       Description of Parameter
	 *@param  stmtName  Description of Parameter
	 *@param  args      Description of Parameter
	 *@return           The VFSObjects value
	 *@since
	 */
	protected java.util.List getVFSObjectsList( SqlServerRecord rec, String stmtName, Object args[] )
	{
		Log.log( Log.DEBUG, SqlServerRecord.class,
			"Looking for vfs objects in:" );
		if ( args != null )
			for ( int i = args.length; --i >= 0;  )
				Log.log( Log.DEBUG, SqlServerRecord.class,
					">" + args[i] );

		final java.util.List rv = new ArrayList();
		Connection conn = null;
		try
		{
			conn = rec.allocConnection();
			final PreparedStatement pstmt = rec.prepareStatement( conn,
				stmtName,
				args );
			final ResultSet rs = SqlUtils.executeQuery( pstmt );
			final ResultSetMetaData rsmd = rs.getMetaData();
			final boolean hasSize = rsmd.getColumnCount() > 1;
			while ( rs.next() )
			{
				final String tgname = rs.getString( 1 );
				final String tgsize = hasSize ? rs.getString( 2 ) : null;
				final VFSObjectRec vor = new VFSObjectRec( tgname, tgsize );
				rv.add( vor );
			}
			rec.releaseStatement( pstmt );
		} catch ( SQLException ex )
		{
			Log.log( Log.ERROR, SqlServerRecord.class,
				"Error getting vfs objects in " + args );
			Log.log( Log.ERROR, SqlServerRecord.class,
				ex );
		} finally
		{
			rec.releaseConnection( conn );
		}
		return rv;
	}


	/**
	 *  Description of the Class
	 *
	 *@author     svu
	 *@created    05 December 2003
	 */
	protected final class SqlDirectoryEntry extends VFS.DirectoryEntry
	{
		/**
		 *  Description of the Field
		 */
		protected VFSObjectRec rec;


		/**
		 *  Constructor for the SqlDirectoryEntry object
		 *
		 *@param  rec   Description of the Parameter
		 *@param  type  Description of the Parameter
		 */
		public SqlDirectoryEntry( VFSObjectRec rec, int type )
		{
			super( getSqlVFS().getFileName( rec.path ), rec.path, rec.path,
				type, 0L, false );
			this.rec = rec;
		}


		/**
		 *  Gets the extendedAttribute attribute of the SqlDirectoryEntry object
		 *
		 *@param  name  Description of the Parameter
		 *@return       The extendedAttribute value
		 */
		public String getExtendedAttribute( String name )
		{
			return ( name == VFS.EA_SIZE && rec.size != null ) ?
				rec.size : super.getExtendedAttribute( name );
		}
	}


	/**
	 *  Description of the Class
	 *
	 *@author     svu
	 *@created    05 December 2003
	 */
	public static class VFSObjectRec
	{
		/**
		 *  Description of the Field
		 */
		public String path;
		/**
		 *  Description of the Field
		 */
		public String size;


		/**
		 *  Constructor for the VFSObjectRec object
		 *
		 *@param  size  Description of the Parameter
		 *@param  path  Description of the Parameter
		 */
		public VFSObjectRec( String path, String size )
		{
			this.path = path;
			this.size = size;
		}


		/**
		 *  Constructor for the VFSObjectRec object
		 *
		 *@param  path  Description of the Parameter
		 */
		public VFSObjectRec( String path )
		{
			this( path, null );
		}


		/**
		 *  Sets the dir attribute of the VFSObjectRec object
		 *
		 *@param  path  The new dir value
		 */
		public void setDir( String path )
		{
			this.path = path + SqlVFS.separatorString + this.path;
		}
	}
}

