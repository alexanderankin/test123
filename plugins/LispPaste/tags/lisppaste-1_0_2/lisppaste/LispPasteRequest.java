/* :folding=explicit:collapseFolds=1: */

/*
 * $Id$
 *
 * Copyright (C) 2004 Slava Pestov.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * DEVELOPERS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package lisppaste;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.WorkRequest;
import javax.swing.SwingUtilities;
import java.util.Vector;
import java.net.URL;

public class LispPasteRequest extends WorkRequest
{
	//{{{ LispPasteRequest constructor
	public LispPasteRequest(View view, String channel, String user,
		String title, String contents)
	{
		this.view = view;
		this.channel = channel;
		this.user = user;
		this.title = title;
		this.contents = contents;
	} //}}}
	
	//{{{ run() method
	public void run()
	{
		setStatus(jEdit.getProperty("lisp-paste-request.status"));
		
		String url = jEdit.getProperty("lisp-paste-request.url");

		Vector params = new Vector();
		params.add(channel);
		params.add(user);
		params.add(title);
		params.add(contents);

		try
		{
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(url));
			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);
			final Object response = client.execute("newpaste",params);

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					GUIUtilities.message(view,"lisp-paste-response",
						new String[] { String.valueOf(response) });
				}
			});
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,e);
			VFSManager.error(view,url,"lisp-paste-request.error",
				new String[] { e.toString() });
		}
	} //}}}
	
	//{{{ Private members
	private View view;
	private String channel;
	private String user;
	private String title;
	private String contents;
	//}}}
}
