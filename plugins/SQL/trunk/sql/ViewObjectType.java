/**
 * ViewObjectType.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
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

import org.gjt.sp.util.*;

import sql.*;
import sql.actions.*;

/**
 *  Description of the Class
 *
 *  @author     svu
 */
public class ViewObjectType extends CodeObjectType
{

	protected final MessageFormat prefixFmt;

	/**
	 *  Constructor for the ViewObjectType object
	 *
	 *  @param  typeString  Description of Parameter
	 *  @since
	 */
	public ViewObjectType(String type, String prefix)
	{
		this(type, null, prefix);
	}


	/**
	 *Constructor for the ViewObjectType object
	 *
	 * @param  type             Description of Parameter
	 * @param  statementPurpose  Description of Parameter
	 */
	public ViewObjectType(String type, String statementPurpose4List, String prefix)
	{
		this(type, statementPurpose4List, null, prefix);
	}


	/**
	 *Constructor for the ViewObjectType object
	 *
	 * @param  typeString             Description of Parameter
	 * @param  statementPurpose4Text  Description of Parameter
	 * @param  statementPurpose4List  Description of Parameter
	 */
	public ViewObjectType(String type, String statementPurpose4List, String statementPurpose4Text, String prefix)
	{
		super(type, statementPurpose4List, statementPurpose4Text);
		
		prefixFmt = prefix == null ? null : new MessageFormat(prefix);

		// overriding
		objectActions.put("Source Code",
		                  new SourceCodeAction(type, this.statementPurpose4Text)
				  {
				  	public String getSource(String path,
								SqlServerRecord rec,
								String userName,
								String objName)
					{
						String src = super.getSource(path, rec, userName, objName);
						if (prefixFmt != null)
							src = prefixFmt.format(new Object[] { userName, objName }) + src;
						return src;

					}
		                  });


		objectActions.put("Data",
		                  new DataAction());

		objectActions.put("Columns",
		                  new SchemaAction());
	}
}

