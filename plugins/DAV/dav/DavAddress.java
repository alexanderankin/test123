/**
 * DavAdress.java - a DAV directory entry
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * A sort of hack from FTP.FtpAddress
 * Might not be necessary, since our URLs don't contain as much information
 * as Ftp addresses
 * @author James Glaubiger
 * @version $$
*/
package dav;

import org.gjt.sp.jedit.jEdit;

public class DavAddress
{
   	//{{{ members
	public String host;
	public String path;
	//}}}
	
	//{{{ DavAddress() constructor
	public DavAddress(String url)
	{
		int trimAt;
		if(url.startsWith(DavVFS.PROTOCOL + ":"))
		{
			trimAt = 5;
			for(int i = 5; i < url.length(); i++)
			{
				if(url.charAt(i) != '/')
				{
					trimAt = i;
					break;
				}
			}
		
			url = url.substring(trimAt);
		}

		int index = url.indexOf('/');
		if(index == -1)
			index = url.length();

		host = url.substring(0,index);
		path = url.substring(index);
		
		//host = path = url;
		//if( !path.endsWith("/") && path.length() != 0)
		//	path = path + "/";
	} //}}}

	//{{{ DavAddress() constructor
	public DavAddress(String host, String path)
	{
		this.host = host;
		this.path = path;
	} //}}}

	//{{{ toString() method
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(DavVFS.PROTOCOL);
		buf.append("://");
		buf.append(host);
		buf.append(path);

		return buf.toString();
	} //}}}
}
