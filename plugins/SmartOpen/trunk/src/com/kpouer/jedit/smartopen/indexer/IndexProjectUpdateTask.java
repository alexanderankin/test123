/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.kpouer.jedit.smartopen.indexer;

import java.util.Collection;

import com.kpouer.jedit.smartopen.SmartOpenPlugin;
import org.gjt.sp.util.Task;
import projectviewer.vpt.VPTFile;

/**
 * @author Matthieu Casanova
 */
public class IndexProjectUpdateTask extends Task
{
	private final Collection<VPTFile> addedFiles;
	private final Collection<VPTFile> removedFiles;

	public IndexProjectUpdateTask(Collection<VPTFile> addedFiles, Collection<VPTFile> removedFiles)
	{
		this.addedFiles = addedFiles;
		this.removedFiles = removedFiles;
	}

	@Override
	public void _run()
	{
		FileProvider addedFileProvider =
			new VPTFileProvider(addedFiles.toArray(new VPTFile[addedFiles.size()]));
		SmartOpenPlugin.itemFinder.addFiles(addedFileProvider, this, false);
		FileProvider removedFileProvider =
			new VPTFileProvider(removedFiles.toArray(new VPTFile[removedFiles.size()]));
		SmartOpenPlugin.itemFinder.removeFiles(removedFileProvider, this);
		SmartOpenPlugin.itemFinder.optimize();
	}
}
