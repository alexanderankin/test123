/*
 * SessionFile.java - encapsulates information about a single file which is 
 *                    being managed as part of a jEdit session.
 * Copyright (c) 2007 Steve Jakob
 *
 * :tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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

package sessions;

import java.util.Hashtable;
import org.gjt.sp.jedit.*;

public class SessionFile
{
	protected String path;
	protected String encoding;
	protected Integer carat;
	
	/**
	 * Create a <code>SessionFile</code> object with the supplied file path
	 * and the default character encoding.
	 */
	public SessionFile(String path)
	{
		super();
		this.path = path;
	}
	
	/**
	 * Create a <code>SessionFile</code> object with the supplied file path and 
	 * character encoding. Note that the supplied encoding is only used to set 
	 * the buffer's character encoding at the time the file is opened. The 
	 * <code>SessionFile</code>'s encoding is not currently kept synchronized 
	 * with the buffer encoding.
	 */
	public SessionFile(String path, String encoding)
	{
		this(path);
		// TODO: validate the supplied encoding
		this.encoding = encoding;
	}
	
	/**
	 * Create a <code>SessionFile</code> object with the supplied file path, 
	 * character encoding, and carat position. Note that the supplied encoding 
	 * is only used to set the buffer's character encoding at the time the 
	 * file is opened. The <code>SessionFile</code>'s encoding is not 
	 * currently kept synchronized with the buffer encoding.
	 */
	public SessionFile(String path, String encoding, Integer carat)
	{
		this(path, encoding);
		this.carat = carat;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getEncoding()
	{
		return encoding;
	}
	
	public Integer getCarat()
	{
		if (carat == null)
			return new Integer(0);
		return carat;
	}
	
	/**
	 * A convenience method which returns a <code>Hashtable</code> containing 
	 * properties suitable for passing to the <code>openFile</code> method in 
	 * the <code>org.gjt.sp.jedit.jEdit</code> class.
	 */
	public Hashtable getBufferProperties()
	{
		Hashtable props = new Hashtable();
		if (this.getEncoding() != null)
			props.put(Buffer.ENCODING, this.getEncoding());
		props.put(Buffer.CARET, this.getCarat());
		return props;
	}
	
}
