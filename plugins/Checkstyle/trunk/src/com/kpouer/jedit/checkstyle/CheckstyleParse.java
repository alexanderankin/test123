/*
 * CheckstyleParse.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Matthieu Casanova
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
package com.kpouer.jedit.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.*;
import errorlist.DefaultErrorSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.io.FileVFS;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;

import javax.swing.text.Segment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class CheckstyleParse implements Runnable, AuditListener
{
	private List<File> file;

	private DefaultErrorSource errorSource;

	private Buffer buffer;

	private Segment segment;

	public CheckstyleParse(Buffer buffer, DefaultErrorSource errorSource)
	{
		this.errorSource = errorSource;
		file = new ArrayList<File>(1);
		String path = buffer.getPath();
		file.add(new File(path));
	}

	public CheckstyleParse(VFSFile[] files, DefaultErrorSource errorSource)
	{
		this.errorSource = errorSource;
		file = new ArrayList<File>();
		addFiles(files);
	}

	private void addFiles(VFSFile[] files)
	{
		for (VFSFile vfsFile : files)
		{
			VFS vfs = vfsFile.getVFS();
			if (vfs instanceof FileVFS)
			{
				String path = vfsFile.getPath();
				if (vfsFile.getType() == VFSFile.DIRECTORY)
				{
					try
					{
						VFSFile[] list = vfs._listFiles(null, path, jEdit.getActiveView());
						addFiles(list);
					}
					catch (IOException e)
					{
						Log.log(Log.ERROR,this, e);
					}
				}
				else
				{
					if (ModeProvider.instance.getModeForFile(path, "") == ModeProvider.instance.getMode("java"))
						file.add(new File(path));
				}
			}
		}
	}

	private Configuration getEmbeddedConfiguration(String path)
	{
		InputStream stream = null;
		try
		{
			stream = getClass().getResourceAsStream("/styles/"+path+".xml");
			return getConfiguration(stream);
		}
		catch (CheckstyleException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			IOUtilities.closeQuietly(stream);
		}
		return null;
	}

	private Configuration getConfiguration(InputStream stream) throws CheckstyleException
	{
		return ConfigurationLoader.loadConfiguration(stream, new PropertiesExpander(System.getProperties()), false);
	}

	private Configuration getConfiguration(String path)
	{
		Object session = null;
		InputStream inputStream = null;
		VFS vfs = VFSManager.getVFSForPath(path);
		try
		{
			session = vfs.createVFSSession(path, jEdit.getActiveView());
			inputStream = vfs._createInputStream(session, path, true, jEdit.getActiveView());
			return getConfiguration(inputStream);
		}
		catch (CheckstyleException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			if (session != null)
			{
				try
				{
					vfs._endVFSSession(session, jEdit.getActiveView());
				}
				catch (IOException e)
				{
				}
				IOUtilities.closeQuietly(inputStream);
			}
		}
		return null;
	}

	private Configuration getConfiguration()
	{
		if (jEdit.getBooleanProperty("checkstyle.defaultstyle.embedded"))
		{
			String style = jEdit.getProperty("checkstyle.defaultstyle.embedded.value");
			Log.log(Log.MESSAGE, this, "Using embedded configuration " + style);
			return getEmbeddedConfiguration(style);
		}
		String path = jEdit.getProperty("checkstyle.defaultstyle.file");
		Log.log(Log.MESSAGE, this, "Using external configuration " + path);
		return getConfiguration(path);
	}

	public void run()
	{
		Configuration conf = getConfiguration();
		try
		{
			errorSource.clear();
			Checker checker = new Checker();
			ClassLoader moduleClassLoader = Checker.class.getClassLoader();
			checker.setModuleClassLoader(moduleClassLoader);
			checker.configure(conf);
			checker.addListener(this);
			checker.process(file);
		}
		catch (CheckstyleException e)
		{
			Log.log(Log.ERROR, this, e);
		}
	}

	public void auditStarted(AuditEvent auditEvent)
	{
		segment = new Segment();
	}

	public void auditFinished(AuditEvent auditEvent)
	{
		segment = null;
	}

	public void fileStarted(AuditEvent auditEvent)
	{
		buffer = jEdit.openTemporary(jEdit.getActiveView(), null, auditEvent.getFileName(), false);
	}

	public void fileFinished(AuditEvent auditEvent)
	{
		buffer = null;
	}

	public void addError(AuditEvent auditEvent)
	{
		int column = auditEvent.getColumn();
		int line = auditEvent.getLine();
		SeverityLevel severityLevel = auditEvent.getSeverityLevel();
		int level;

		if (severityLevel.equals(SeverityLevel.ERROR))
		{
			level = DefaultErrorSource.ERROR;
		}
		else
		{
			level = DefaultErrorSource.WARNING;
		}

		int start = column == 0 ? 0 : column - 1;
		int reduce = 0;
		int lineIndex = line == 0 ? 0 : line - 1;
		if (buffer != null)
		{
			int tabSize = buffer.getTabSize();
			buffer.getLineText(lineIndex, segment);
			for (int i = 0;i<segment.length();i++)
			{
				if (segment.charAt(i) == '\t')
				{
					reduce = reduce + tabSize - 1;
				}
				else
					break;
			}
		}
		start -= reduce;
		errorSource.addError(level,
				     auditEvent.getFileName(),
				     lineIndex,
				     start,
				     start + 1,
				     auditEvent.getMessage());
	}

	public void addException(AuditEvent auditEvent, Throwable throwable)
	{
	}
}
