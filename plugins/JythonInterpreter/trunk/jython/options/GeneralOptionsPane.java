/*
 * GeneralOptionsPane.java - General Options for JythonInterpreter plugin.
 *
 * Copyright (C) 2003 Ollie Rutherfurd
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
 *
 * $Id: GeneralOptionsPane.java,v 1.3 2003/08/04 21:46:58 tibu Exp $
 */

package jython.options;

import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;

public class GeneralOptionsPane extends AbstractOptionPane
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	//{{{ constructor
	public GeneralOptionsPane()
	{
		super("jython.general");
	} //}}}

	//{{{ _init() method
	public void _init()
	{
		addComponent(autoload = new JCheckBox(jEdit.getProperty(
			"options.jython.autoloadTitle")));
		autoload.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.autoload"));
		addComponent(autoloadPlugins = new JCheckBox(jEdit.getProperty(
			"options.jython.autoloadPluginsTitle")));
		autoloadPlugins.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.autoloadPlugins"));
		addComponent(autosave = new JCheckBox(jEdit.getProperty(
			"options.jython.autosaveTitle")));
		autosave.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.autosave"));
		addComponent(saveask = new JCheckBox(jEdit.getProperty(
			"options.jython.saveaskTitle")));
		saveask.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.saveask"));
		addComponent(saveJythonPath = new JCheckBox(jEdit.getProperty(
			"options.jython.saveJythonPathTitle")));
		saveJythonPath.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.saveJythonPath"));
		addComponent(cleanDirtyFlag = new JCheckBox(jEdit.getProperty(
			"options.jython.cleanDirtyFlagTitle")));
		cleanDirtyFlag.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.cleanDirtyFlag"));
		addComponent(reuseOutputBuffer = new JCheckBox(jEdit.getProperty(
			"options.jython.reuseOutputBufferTitle")));
		reuseOutputBuffer.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.reuseOutputBuffer"));
		addComponent(append = new JCheckBox(jEdit.getProperty(
			"options.jython.appendTitle")));
		append.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.append"));
		addComponent(upDownFlag = new JCheckBox(jEdit.getProperty(
			"options.jython.upDownFlagTitle")));
		upDownFlag.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.upDownFlag"));
		addComponent(codeCompletionFlag = new JCheckBox(jEdit.getProperty(
			"options.jython.codeCompletionTitle")));
		codeCompletionFlag.getModel().setSelected(jEdit.getBooleanProperty(
			"options.jython.codeCompletionFlag"));
	} //}}}

	//{{{ _save() method
	public void _save()
	{
		jEdit.setBooleanProperty("options.jython.autoload",
			autoload.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.autoloadPlugins",
			autoloadPlugins.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.autosave",
			autosave.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.saveask",
			saveask.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.saveJythonPath",
			saveJythonPath.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.cleanDirtyFlag",
			cleanDirtyFlag.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.reuseOutputBuffer",
			reuseOutputBuffer.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.append",
			append.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.upDownFlag",
			upDownFlag.getModel().isSelected());
		jEdit.setBooleanProperty("options.jython.codeCompletionFlag",
			codeCompletionFlag.getModel().isSelected());
	}
	//}}}

	//{{{ instance variables
	private JCheckBox autoload;
	private JCheckBox autoloadPlugins;
	private JCheckBox autosave;
	private JCheckBox saveask;
	private JCheckBox saveJythonPath;
	private JCheckBox cleanDirtyFlag;
	private JCheckBox reuseOutputBuffer;
	private JCheckBox append;
	private JCheckBox upDownFlag;
	private JCheckBox codeCompletionFlag;
	//}}}
}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=1:
