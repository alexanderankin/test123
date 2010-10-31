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
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class RFCReaderPlugin extends EditPlugin
{
	Map<Integer, RFC> rfcList;
	private RFCIndex index;

	private String currentService;
	public static final String DEFAULT_INDEX = "title";

	@Override
	public void start()
	{
		EditBus.addToBus(this);
		RFCListParser parser = new RFCListParser();
		rfcList = parser.parse();
	}

	@Override
	public void stop()
	{
		rfcList = null;
		index.close();
		index = null;
		EditBus.removeFromBus(this);
	}

	public RFCIndex getIndex()
	{
		if (index == null)
		{
			try
			{
				String s = jEdit.getProperty("options.rfcreader.index", DEFAULT_INDEX);
				index = ServiceManager.getService(RFCIndex.class, s);
				if (index == null)
					index = ServiceManager.getService(RFCIndex.class, DEFAULT_INDEX);
				index.load();
				currentService = s;
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
			}
		}
		return index;
	}

	@EBHandler
	public void handlePropertiesChanged(PropertiesChanged msg)
	{
		String s = jEdit.getProperty("options.rfcreader.index", DEFAULT_INDEX);
		if (!s.equals(currentService))
		{
			currentService = s;
			index.close();
			index = null;
		}
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
