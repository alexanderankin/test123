/*
 * SideKickModeOptionsPane.java - 
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Alan Ezust
 * Copyright (c) 2007 Shlomy Reinstein
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


package sidekick;


import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.gjt.sp.jedit.jEdit;

import sidekick.ModeOptionPaneController.ModeOptionPaneDelegate;

// {{{ SideKickModeOptionsPane
/**
 * 
 * Mode-Specific options for SideKick - a custom ModeOptionPane which 
 * includes 3 questions for the user, and provides an example of
 * how to extend ModeOptionPane and set/reset the mode options.
 * 
 * @author Alan Ezust
 *
 */
@SuppressWarnings("serial")
public class SideKickModeOptionsPane extends AbstractModeOptionPane implements ModeOptionPaneDelegate
{
	ModeOptionPaneController controller;
	
	JCheckBox showStatusWindow;
	JCheckBox treeFollowsCaret;
	JComboBox autoExpandTreeDepth;
	JComboBox defaultParser;

	// {{{ SideKickModeOptionsPane ctor
	public SideKickModeOptionsPane() 
	{
		super("sidekick.mode");
		controller = new ModeOptionPaneController(this);
		
		showStatusWindow = new JCheckBox(jEdit.getProperty("options." + SideKick.SHOW_STATUS));
		addComponent(showStatusWindow);
		
		treeFollowsCaret = new JCheckBox(jEdit.getProperty("options.sidekick.tree-follows-caret"));
		addComponent(treeFollowsCaret);

		autoExpandTreeDepth = new JComboBox();
		addComponent(jEdit.getProperty("options.sidekick.auto-expand-tree-depth"), autoExpandTreeDepth);
//		autoExpandTreeDepth.addActionListener(new ActionHandler());
		autoExpandTreeDepth.addItem(ModeOptionsDialog.ALL);
		for (int i = 0; i <= 10; i++)
			autoExpandTreeDepth.addItem(String.valueOf(i));
		addComponent(autoExpandTreeDepth);
		defaultParser = new JComboBox();
		defaultParser.setModel(new DefaultComboBoxModel(SideKickTree.parserList().toArray()));
		
		addComponent(jEdit.getProperty("options.sidekick.parser.parser"), defaultParser);
		
	} // }}}
	

	// {{{ init()
	protected void _init() {
	} // }}}
	
	class Props {
		boolean treeFollowsCaret;
		boolean showStatusWindow;
		int autoExpandTreeDepth;
		String parser;
	}
	
	public Object createModeProps(String mode) {
		Props p = new Props();
		p.treeFollowsCaret = getBooleanProperty(mode, SideKick.FOLLOW_CARET);
		p.showStatusWindow = getBooleanProperty(mode, SideKick.SHOW_STATUS);
		p.autoExpandTreeDepth = getIntegerProperty(mode, SideKick.AUTO_EXPAND_DEPTH, 1);
		p.parser = getProperty(mode, SideKickPlugin.PARSER_PROPERTY);
		return p;
	}

	public void resetModeProps(String mode) {
		clearModeProperty(mode, SideKick.FOLLOW_CARET);
		clearModeProperty(mode, SideKick.AUTO_EXPAND_DEPTH);
		clearModeProperty(mode, SideKick.SHOW_STATUS);
		clearModeProperty(mode, SideKickPlugin.PARSER_PROPERTY);
	}

	public void saveModeProps(String mode, Object props) {
		Props p = (Props) props;
		setBooleanProperty(mode, SideKick.FOLLOW_CARET, p.treeFollowsCaret);
		setBooleanProperty(mode, SideKick.SHOW_STATUS, p.showStatusWindow);
		setIntegerProperty(mode, SideKick.AUTO_EXPAND_DEPTH, p.autoExpandTreeDepth);
		if (p.parser == SideKickPlugin.DEFAULT)
			clearModeProperty(mode, SideKickPlugin.PARSER_PROPERTY);
		else
			setProperty(mode, SideKickPlugin.PARSER_PROPERTY, p.parser);
	}

	public void updatePropsFromUI(Object props) {
		Props p = (Props) props;
		p.treeFollowsCaret = treeFollowsCaret.isSelected();
		p.showStatusWindow = showStatusWindow.isSelected();
		String value = (String)autoExpandTreeDepth.getSelectedItem();
		String depth = value.equals(ModeOptionsDialog.ALL) ? "-1" : value;
		p.autoExpandTreeDepth = Integer.valueOf(depth); 
		Object parser = defaultParser.getSelectedItem();
		p.parser = (parser == null) ? null : parser.toString();
	}

	public void updateUIFromProps(Object props) {
		Props p = (Props) props;
		treeFollowsCaret.setSelected(p.treeFollowsCaret);
		showStatusWindow.setSelected(p.showStatusWindow);
		autoExpandTreeDepth.setSelectedIndex(p.autoExpandTreeDepth + 1);
		defaultParser.setSelectedItem(p.parser);
	}

	public void modeSelected(String mode) {
		controller.modeSelected(mode);
	}

	public void cancel() {
		controller.cancel();
	}
	
	public void setUseDefaults(boolean b) {
		controller.setUseDefaults(b);
	}

	public boolean getUseDefaults(String mode) {
		return controller.getUseDefaults(mode);
	}

	public JComponent getUIComponent() {
		return this;
	}


	public boolean hasModeProps(String mode) {
		return modePropertyExists(mode, SideKick.FOLLOW_CARET) ||
			modePropertyExists(mode, SideKick.AUTO_EXPAND_DEPTH) ||
			modePropertyExists(mode, SideKick.SHOW_STATUS) ||
			modePropertyExists(mode, SideKickPlugin.PARSER_PROPERTY);
	}

	@Override
	public void _save() {
		controller.save();
	}


} // }}}

