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

import org.apache.xmlrpc.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.WorkRequest;
import java.util.Vector;
import java.util.List;

public class ChannelListRequest extends WorkRequest
{
	//{{{ ChannelListRequest constructor
	public ChannelListRequest(View view)
	{
		this.view = view;
	} //}}}
	
	//{{{ run() method
	public void run()
	{
		setStatus(jEdit.getProperty("channel-list-request.status"));
		
		String url = jEdit.getProperty("lisp-paste-request.url");

		try
		{
			XmlRpcClient client = new XmlRpcClient(url);
			list = (List)client.execute("listchannels",new Vector());
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,e);
			VFSManager.error(view,url,"lisp-paste-request.error",
				new String[] { e.toString() });
		}
	} //}}}
	
	//{{{ getChannelList() method
	public List getChannelList()
	{
		return list;
	} //}}}

	//{{{ Private members
	private View view;
	private List list;
	//}}}
}
