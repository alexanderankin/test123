/*
 * save_ftp.java
 * Copyright (C) 2000 Slava Pestov
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

import java.awt.event.ActionEvent;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.*;

public class save_ftp extends EditAction
{
	public save_ftp()
	{
		super("save-ftp");
	}

	public void actionPerformed(ActionEvent evt)
	{
		View view = getView(evt);

		String path = ((FtpVFS)VFSManager.getVFSForProtocol("ftp"))
			.showBrowseDialog(new Object[1],getView(evt));
		if(path != null)
		{
			String[] files = GUIUtilities.showVFSFileDialog(
				view,path,VFSBrowser.SAVE_DIALOG,false);
			if(files == null)
				return;

			view.getBuffer().save(view,files[0],true);
		}
	}
}
