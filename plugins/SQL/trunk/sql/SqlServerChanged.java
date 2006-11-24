/**
 * SqlServerChanged.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 */
package sql;

import org.gjt.sp.jedit.*;

/**
 *  Description of the Class
 *
 * @author     svu
 * @created    17 Февраль 2003 г.
 */
public class SqlServerChanged extends EBMessage
{
	private String newServer;


	/**
	 *Constructor for the SqlServerChanged object
	 *
	 * @param  source     Description of Parameter
	 * @param  newServer  Description of Parameter
	 */
	public SqlServerChanged(EBComponent source, String newServer)
	{
		super(source);
		this.newServer = newServer;
	}


	/**
	 *  Gets the NewServer attribute of the SqlServerChanged object
	 *
	 * @return    The NewServer value
	 */
	public final String getNewServer()
	{
		return newServer;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 */
	public String paramString()
	{
		return super.paramString()
		       + ",newServer=" + newServer;
	}

}

