/*
 * CompileRunOptionPane.java - Compile & run option pane
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Slava Pestov, 2010 Damien Radtke
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

package console.options;

//{{{ Imports
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import javax.swing.text.html.HTMLEditorKit;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;

import console.ConsolePlugin;
//}}}

//{{{ CompileRunOptionPane class
public class CompileRunOptionPane extends AbstractOptionPane implements ActionListener
{
	private static final long serialVersionUID = -1672963909425664168L;
	public static final String NONE = "none";

	//{{{ CompileRunOptionPane constructor
	public CompileRunOptionPane()
	{
		super("console.compile-run");
	} //}}}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == modeBox) {
			update(((Mode) modeBox.getSelectedItem()).getName());
		} else if (source == compilerCommando) {
			compilerCommandList.setEnabled(true);
			compilerCustomText.setEnabled(false);
		} else if (source == compilerCustom) {
			compilerCommandList.setEnabled(false);
			compilerCustomText.setEnabled(true);
		} else if (source == interpreterCommando) {
			interpreterCommandList.setEnabled(true);
			interpreterCustomText.setEnabled(false);
		} else if (source == interpreterCustom) {
			interpreterCommandList.setEnabled(false);
			interpreterCustomText.setEnabled(true);
		}
	}

	//{{{ Protected members

	//{{{ _init() method
	protected void _init()
	{
		EditAction[] commandos = ConsolePlugin.getCommandoCommands();
		String[] labels = new String[commandos.length + 1];
		for(int i = 0; i < commandos.length; i++)
		{
			labels[i + 1] = commandos[i].getLabel();
		}

		labels[0] = CompileRunOptionPane.NONE;
		
		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.X_AXIS));
		modePanel.add(new JLabel("Set compiler/interpreter for edit mode:"));
		modeBox = new JComboBox(jEdit.getModes());
		modeBox.setSelectedItem(jEdit.getActiveView().getBuffer().getMode());
		modePanel.add(modeBox);
		addComponent(modePanel);
		
		JPanel compilerPanel = new JPanel();
		compilerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
			"Compiler"));
		compilerPanel.setLayout(new BoxLayout(compilerPanel, BoxLayout.Y_AXIS));
		JPanel compilerCommandoPanel = new JPanel();
		compilerCommandoPanel.setLayout(new BoxLayout(compilerCommandoPanel, BoxLayout.X_AXIS));
		compilerCommandoPanel.add(compilerCommando = new JRadioButton("Use command"));
		compilerCommandoPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		compilerCommandoPanel.add(compilerCommandList = new JComboBox(labels));
		JPanel compilerCustomPanel = new JPanel();
		compilerCustomPanel.setLayout(new BoxLayout(compilerCustomPanel, BoxLayout.X_AXIS));
		compilerCustomPanel.add(compilerCustom = new JRadioButton("Use custom command"));
		compilerCustomPanel.add(compilerCustomText = new JTextField(30));
		compilerPanel.add(compilerCommandoPanel);
		compilerPanel.add(compilerCustomPanel);
		
		JPanel interpreterPanel = new JPanel();
		interpreterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
			"Interpreter"));
		interpreterPanel.setLayout(new BoxLayout(interpreterPanel, BoxLayout.Y_AXIS));
		JPanel interpreterCommandoPanel = new JPanel();
		interpreterCommandoPanel.setLayout(new BoxLayout(interpreterCommandoPanel, BoxLayout.X_AXIS));
		interpreterCommandoPanel.add(interpreterCommando = new JRadioButton("Use command"));
		interpreterCommandoPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		interpreterCommandoPanel.add(interpreterCommandList = new JComboBox(labels));
		JPanel interpreterCustomPanel = new JPanel();
		interpreterCustomPanel.setLayout(new BoxLayout(interpreterCustomPanel, BoxLayout.X_AXIS));
		interpreterCustomPanel.add(interpreterCustom = new JRadioButton("Use custom command"));
		interpreterCustomPanel.add(interpreterCustomText = new JTextField(30));
		interpreterPanel.add(interpreterCommandoPanel);
		interpreterPanel.add(interpreterCustomPanel);
		
		JLabel help = new JLabel();
		help.setText(jEdit.getProperty("options.console.compile-run.help"));
		
		addComponent(compilerPanel);
		addComponent(interpreterPanel);
		addComponent(Box.createRigidArea(new Dimension(0, 20)));
		addComponent(help);
		
		ButtonGroup compilerGroup = new ButtonGroup();
		ButtonGroup interpreterGroup = new ButtonGroup();
		compilerGroup.add(compilerCommando);
		compilerGroup.add(compilerCustom);
		interpreterGroup.add(interpreterCommando);
		interpreterGroup.add(interpreterCustom);
		
		modeBox.addActionListener(this);
		compilerCommando.addActionListener(this);
		compilerCustom.addActionListener(this);
		interpreterCommando.addActionListener(this);
		interpreterCustom.addActionListener(this);
		
		update((String) null);
		
		/*
		JLabel label = new JLabel(jEdit.getProperty(
			"options.console.compile-run.caption"));
		label.setBorder(new EmptyBorder(0,0,6,0));
		add(BorderLayout.NORTH,label);

		add(BorderLayout.CENTER,createModeTableScroller());
		*/
	} //}}}
	
	protected void update(String mode) {
		if (mode == null)
			mode = jEdit.getActiveView().getBuffer().getMode().getName();
		
		String currentCompileCommand = jEdit.getProperty("mode."+mode+".commando.compile");
		if (currentCompileCommand != null)
			compilerCommandList.setSelectedItem(currentCompileCommand);
		else
			compilerCommandList.setSelectedIndex(0);
		
		String currentInterpretCommand = jEdit.getProperty("mode."+mode+".commando.run");
		if (currentInterpretCommand != null)
			interpreterCommandList.setSelectedItem(currentInterpretCommand);
		else
			interpreterCommandList.setSelectedIndex(0);
		
		compilerCustomText.setText(jEdit.getProperty("mode."+mode+".compile.custom", ""));
		interpreterCustomText.setText(jEdit.getProperty("mode."+mode+".run.custom", ""));
		
		if (!jEdit.getBooleanProperty("mode."+mode+".compile.use-custom")) {
			compilerCommando.setSelected(true);
			compilerCommandList.setEnabled(true);
			compilerCustomText.setEnabled(false);
		} else {
			compilerCustom.setSelected(true);
			compilerCommandList.setEnabled(false);
			compilerCustomText.setEnabled(true);
		}
		
		if (!jEdit.getBooleanProperty("mode."+mode+".run.use-custom")) {
			interpreterCommando.setSelected(true);
			interpreterCommandList.setEnabled(true);
			interpreterCustomText.setEnabled(false);
		} else {
			interpreterCustom.setSelected(true);
			interpreterCommandList.setEnabled(false);
			interpreterCustomText.setEnabled(true);
		}
		
	}

	//{{{ _save() method
	protected void _save()
	{
		//model.save();
		String mode = modeBox.getSelectedItem().toString();
		jEdit.setBooleanProperty("mode."+mode+".compile.use-custom",
			compilerCustom.isSelected());
		jEdit.setBooleanProperty("mode."+mode+".run.use-custom",
			interpreterCustom.isSelected());
		
		if (compilerCommandList.getSelectedIndex() == 0)
		{
			jEdit.unsetProperty("mode."+mode+".commando.compile");
		}
		else
		{
			jEdit.setProperty("mode."+mode+".commando.compile",
				(String) compilerCommandList.getSelectedItem());
		}
		
		if (interpreterCommandList.getSelectedIndex() == 0)
		{
			jEdit.unsetProperty("mode."+mode+".commando.run");
		}
		else
		{
			jEdit.setProperty("mode."+mode+".commando.run",
				(String) interpreterCommandList.getSelectedItem());
		}
		
		jEdit.setProperty("mode."+mode+".compile.custom", compilerCustomText.getText());
		jEdit.setProperty("mode."+mode+".run.custom", interpreterCustomText.getText());
	} //}}}

	//}}}

	//{{{ Private members
	private JComboBox modeBox;
	
	private JRadioButton compilerCommando;
	private JComboBox compilerCommandList;
	private JTextField compilerCustomText;
	private JRadioButton compilerCustom;
	
	private JRadioButton interpreterCommando;
	private JComboBox interpreterCommandList;
	private JTextField interpreterCustomText;
	private JRadioButton interpreterCustom;
	
	//}}}

} //}}}
