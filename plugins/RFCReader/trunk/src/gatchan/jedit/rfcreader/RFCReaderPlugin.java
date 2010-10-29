/*
 * RFCReaderPlugin.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2007 Matthieu Casanova
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
package gatchan.jedit.rfcreader;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class RFCReaderPlugin extends EBPlugin
{
	Map<Integer, RFC> rfcList;
	private RFCIndex index;

	private String currentService;

	@Override
	public void start()
	{
		RFCListParser parser = new RFCListParser();
		rfcList = parser.parse();
	}

	public RFCIndex getIndex()
	{
		if (index == null)
		{
			try
			{
				String s = jEdit.getProperty("rfc.indexservice", "numbers");
				index = ServiceManager.getService(RFCIndex.class, s);
				if (index == null)
					index = ServiceManager.getService(RFCIndex.class, "numbers");
				index.load();
				currentService = s;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return index;
	}

	@EditBus.EBHandler
	public void handlePropertiesChanged(PropertiesChanged pc)
	{
		String s = jEdit.getProperty("rfc.indexservice", "numbers");
		if (!s.equals(currentService))
		{
			currentService = s;
			index.close();
			index = null;
		}
	}

	@Override
	public void stop()
	{
		rfcList = null;
		index.close();
		index = null;
	}

	public static void openRFC(View view, int rfcNum)
	{
		String mirrorId = jEdit.getProperty(RFCHyperlink.MIRROR_PROPERTY);
		String pattern = jEdit.getProperty("options.rfcreader.rfcsources." + mirrorId + ".url");
		String url = MessageFormat.format(pattern, String.valueOf(rfcNum));
		jEdit.openFile(view, url);
	}

	public static void openRFC(View view)
	{
		ItemFinderWindow<RFC> window = new ItemFinderWindow<RFC>(new RFCItemFinder());
		window.setLocationRelativeTo(view);
		window.setVisible(true);
		EventQueue.invokeLater(window.requestFocusWorker);
	}

	public Map<Integer, RFC> getRfcList()
	{
		return rfcList;
	}
}
