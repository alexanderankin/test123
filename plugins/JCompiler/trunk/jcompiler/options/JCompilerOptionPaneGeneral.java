/*
 * JCompilerOptionPaneGeneral.java - plugin options pane for JCompiler - general options
 * Copyright (c) 2001 Dirk Moebius
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


package jcompiler.options;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;


/**
 * This is the option pane that jEdit displays for JCompiler's General
 * plugin options.
 */
public class JCompilerOptionPaneGeneral extends AbstractOptionPane
{
	private JCheckBox showCommandLine;
	private JCheckBox parseAccentChar;
	private JRadioButton saveCurrentOnCompile;
	private JRadioButton saveNotOnCompile;
	private JRadioButton saveAllOnCompile;
	private JRadioButton saveAskOnCompile;
	private JRadioButton saveCurrentOnPkgCompile;
	private JRadioButton saveNotOnPkgCompile;
	private JRadioButton saveAllOnPkgCompile;
	private JRadioButton saveAskOnPkgCompile;
	private JTextField regexp;
	private JTextField regexpFilename;
	private JTextField regexpLineNo;
	private JTextField regexpMessage;
	private JTextField regexpWarning;


	public JCompilerOptionPaneGeneral() {
		super("jcompiler.general");
	}


	public void _init() {
		// "Show command line"
		showCommandLine = new JCheckBox(jEdit.getProperty("options.jcompiler.showcommandline"));
		showCommandLine.setSelected(jEdit.getBooleanProperty("jcompiler.showcommandline", false));
		addComponent(showCommandLine);
		addComponent(Box.createVerticalStrut(15));

		// ========== Autosave options ==========

		// "When compiling file..."
		JLabel whenCompilingFile = new JLabel(jEdit.getProperty("options.jcompiler.autosave.compile"));

		// Radio buttons: "Save current buffer/Save all buffers/Don't save/Ask"
		saveCurrentOnCompile = new JRadioButton(jEdit.getProperty("options.jcompiler.autosave.current"));
		saveAllOnCompile = new JRadioButton(jEdit.getProperty("options.jcompiler.autosave.all"));
		saveNotOnCompile = new JRadioButton(jEdit.getProperty("options.jcompiler.autosave.not"));
		saveAskOnCompile = new JRadioButton(jEdit.getProperty("options.jcompiler.autosave.ask"));

		ButtonGroup group1 = new ButtonGroup();
		group1.add(saveCurrentOnCompile);
		group1.add(saveAllOnCompile);
		group1.add(saveNotOnCompile);
		group1.add(saveAskOnCompile);

		String s = jEdit.getProperty("jcompiler.javacompile.autosave", "ask");
		if (s.equals("ask"))
			saveAskOnCompile.setSelected(true);
		else if (s.equals("current"))
			saveCurrentOnCompile.setSelected(true);
		else if (s.equals("all"))
			saveAllOnCompile.setSelected(true);
		else // s.equals("no")
			saveNotOnCompile.setSelected(true);

		// "When building a package..."
		JLabel whenBuildingPackage = new JLabel(jEdit.getProperty("options.jcompiler.autosave.compilepkg"));

		// Radio buttons: "Save current buffer/Save all buffers/Don't save/Ask"
		saveCurrentOnPkgCompile = new JRadioButton(jEdit.getProperty("options.jcompiler.autosave.current"));
		saveAllOnPkgCompile = new JRadioButton(jEdit.getProperty("options.jcompiler.autosave.all"));
		saveNotOnPkgCompile = new JRadioButton(jEdit.getProperty("options.jcompiler.autosave.not"));
		saveAskOnPkgCompile = new JRadioButton(jEdit.getProperty("options.jcompiler.autosave.ask"));

		ButtonGroup group2 = new ButtonGroup();
		group2.add(saveCurrentOnPkgCompile);
		group2.add(saveAllOnPkgCompile);
		group2.add(saveNotOnPkgCompile);
		group2.add(saveAskOnPkgCompile);

		s = jEdit.getProperty("jcompiler.javapkgcompile.autosave", "ask");
		if (s.equals("ask"))
			saveAskOnPkgCompile.setSelected(true);
		else if (s.equals("current"))
			saveCurrentOnPkgCompile.setSelected(true);
		else if (s.equals("all"))
			saveAllOnPkgCompile.setSelected(true);
		else // s.equals("no")
			saveNotOnPkgCompile.setSelected(true);

		JPanel saveOptions = new JPanel(new VariableGridLayout(VariableGridLayout.FIXED_NUM_COLUMNS, 2, 30, 0));
		saveOptions.add(whenCompilingFile);
		saveOptions.add(whenBuildingPackage);
		saveOptions.add(saveCurrentOnCompile);
		saveOptions.add(saveCurrentOnPkgCompile);
		saveOptions.add(saveAllOnCompile);
		saveOptions.add(saveAllOnPkgCompile);
		saveOptions.add(saveNotOnCompile);
		saveOptions.add(saveNotOnPkgCompile);
		saveOptions.add(saveAskOnCompile);
		saveOptions.add(saveAskOnPkgCompile);
		addComponent(saveOptions);
		addComponent(Box.createVerticalStrut(15));

		// ========== Error parsing options ==========
		addSeparator("options.jcompiler.sep.errorparsing");

		// "Regexp for errors:"
		regexp = new JTextField(jEdit.getProperty("jcompiler.regexp"));
		addComponent(jEdit.getProperty("options.jcompiler.regexp"), regexp);

		// "Filename at:"
		regexpFilename = new JTextField(jEdit.getProperty("jcompiler.regexp.filename"));
		addComponent(jEdit.getProperty("options.jcompiler.regexp.filename"), regexpFilename);

		// "Line number at:"
		regexpLineNo = new JTextField(jEdit.getProperty("jcompiler.regexp.lineno"));
		addComponent(jEdit.getProperty("options.jcompiler.regexp.lineno"), regexpLineNo);

		// "Message at:"
		regexpMessage = new JTextField(jEdit.getProperty("jcompiler.regexp.message"));
		addComponent(jEdit.getProperty("options.jcompiler.regexp.message"), regexpMessage);

		// "Regexp for warnings:"
		regexpWarning = new JTextField(jEdit.getProperty("jcompiler.regexp.warning"));
		addComponent(jEdit.getProperty("options.jcompiler.regexp.warning"), regexpWarning);

		// "Parse errors for '^' column indicator"
		parseAccentChar = new JCheckBox(jEdit.getProperty("options.jcompiler.parseaccentchar"));
		parseAccentChar.setSelected(jEdit.getBooleanProperty("jcompiler.parseaccentchar", true));
		addComponent(parseAccentChar);
	}


	public void _save() {
		jEdit.setBooleanProperty("jcompiler.showcommandline", showCommandLine.isSelected());
		jEdit.setBooleanProperty("jcompiler.parseaccentchar", parseAccentChar.isSelected());
		jEdit.setProperty("jcompiler.regexp", regexp.getText());
		jEdit.setProperty("jcompiler.regexp.filename", regexpFilename.getText());
		jEdit.setProperty("jcompiler.regexp.lineno", regexpLineNo.getText());
		jEdit.setProperty("jcompiler.regexp.message", regexpMessage.getText());
		jEdit.setProperty("jcompiler.regexp.warning", regexpWarning.getText());

		String s1 = "no";
		if (saveAskOnCompile.isSelected()) s1 = "ask";
		else if (saveCurrentOnCompile.isSelected()) s1 = "current";
		else if (saveAllOnCompile.isSelected()) s1 = "all";
		jEdit.setProperty("jcompiler.javacompile.autosave", s1);

		String s2 = "no";
		if (saveAskOnPkgCompile.isSelected()) s2 = "ask";
		else if (saveCurrentOnPkgCompile.isSelected()) s2 = "current";
		else if (saveAllOnPkgCompile.isSelected()) s2 = "all";
		jEdit.setProperty("jcompiler.javapkgcompile.autosave", s2);
	}

}

