
/*
 * ErrorMatcherPanel.java - 
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2005 by Slava Pestov, Alan Ezust
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.Caret;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import console.ErrorMatcher;
import console.gui.Button;
import console.gui.CheckBox;
import console.gui.Label;
import console.utils.StringList;


/**
 * A view/editor for an ErrorMatcher
 * 
 * @author ezust
 *
 */
class ErrorMatcherPanel extends AbstractOptionPane 
{

	// {{{ Public Members
	// {{{ Constructor
	/**
	 * 
	 * @param optionPane -
	 *                an instance of ErrorsOptionPane - which is how we
	 *                created this dialog, presumably.
	 * @param matcher
	 *                the original matcher which was loaded.
	 */
	public ErrorMatcherPanel(String name, ErrorMatcher matcher)
	{
		super(name);
		this.matcher = matcher;
		testMatcher = (ErrorMatcher) matcher.clone();

		isEnabled = new CheckBox("options.console.errors.isenabled");
		test = new Button("options.console.errors.test");
		cancel = new Button("common.cancel");

		ActionHandler handler = new ActionHandler();
//		apply.addActionListener(handler);
		test.addActionListener(handler);
		cancel.addActionListener(handler);

		Box box = new Box(BoxLayout.X_AXIS);
		
//		box.add(isEnabled);
		box.add(test);
		box.add(cancel);
		
		addComponent( isEnabled, box);
		
		// addSeparator();

		JLabel label = new Label("options.console.errors.name", JLabel.RIGHT);
		errorName = new JTextField(20);
		addComponent(label, errorName);

		label = new Label("options.console.errors.match", JLabel.RIGHT);
		error = new JTextField(20);
		
		addComponent(label, error, GridBagConstraints.BOTH);

		label = new Label("options.console.errors.warning", JLabel.RIGHT);
		warning = new JTextField(20);
		
		addComponent(label, warning, GridBagConstraints.HORIZONTAL);

		label = new Label("options.console.errors.extra", JLabel.RIGHT);
		
		extra = new JTextField(20);
		
		addComponent(label, extra);

		label = new Label("options.console.errors.filename",
			JLabel.RIGHT);
		filename = new JTextField(20);
		
		addComponent(label, filename);
		
		label = new Label("options.console.errors.line", JLabel.RIGHT);
		line = new JTextField(20);
		
		addComponent(label, line);

		label = new Label("options.console.errors.message",
			JLabel.RIGHT);
		
		message = new JTextField(20);
		
		addComponent(label, message, GridBagConstraints.HORIZONTAL);

		label = new Label("options.console.errors.testarea.label",
			JLabel.RIGHT);
		testArea = new JTextArea();
		testArea.setFocusable(true);
		
		testArea.addKeyListener(new KeyHandler());
		
		JScrollPane scrollPane = new JScrollPane(testArea);
		scrollPane.setMinimumSize(new Dimension(300, 120));
		// scrollPane.setPreferredSize(new Dimension(400, 200));
		// scrollPane.setSize(new Dimension(400, 200));
		
		scrollPane.setBorder(new TitledBorder(label.getText()));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill=gbc.BOTH;
		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.gridheight = 10;
		gbc.gridwidth = 3;
		gridBag.setConstraints(scrollPane, gbc);
		add(scrollPane);
		
		
		

//		apply = new Button("common.apply");
		y += 6;
		gbc = new GridBagConstraints();
		gbc.fill=gbc.NONE;
		gbc.gridx = 1;
		gbc.gridy = ++y;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gridBag.setConstraints(box, gbc);
		add(box);
		
		validateTree();
		init();
		
		
	} // }}}

	
	@Override
	protected void _init()
	{
		initialized=true;
		updateTextFields(matcher);
	}

	protected void _save()
	{	
		commitTextFields(matcher);
		matcher.save();
	}


	
	// {{{ updateTextFields()
	/**
	 * Sets the text field values based on what is in the ErrorMatcher
	 */
	public void updateTextFields(ErrorMatcher m)
	{
		errorName.setText(m.name);
		error.setText(m.error);
		warning.setText(m.warning);
		extra.setText(m.extraPattern);
		filename.setText(m.fileBackref);
		line.setText(m.lineBackref);
		message.setText(m.messageBackref);
		testArea.setText(m.testText);
		isEnabled.setSelected(m.isEnabled());
	}

	// }}}

	// {{{ commitTextFields()
	/** Resets the matcher with values from the text fields */
	public void commitTextFields(ErrorMatcher m)
	{
		m.clear();
		m.user = true;
		m.name = errorName.getText();
		m.error = error.getText();
		m.warning = warning.getText();
		m.extraPattern = extra.getText();
		m.fileBackref = filename.getText();
		m.lineBackref = line.getText();
		m.messageBackref = message.getText();
		m.testText = testArea.getText();
		m.setEnabled (isEnabled.isSelected());
	}

	// }}}

	// {{{ void validateRegex()
	public void validateRegex()
	{
		commitTextFields(testMatcher);
		isOK = testMatcher.isValid();
		if (isOK)
			commitTextFields(matcher);
	}

	// }}}

	// {{{ void testRegex()
	public void testRegex()
	{
		validateRegex();
		String testString = testArea.getText();
		jEdit.setProperty("options.console.errors.testarea", testString);
		StringList matches = matcher.findMatches(testString);

		if (matches.size() == 0)
		{
			matches.add("No Matches");
		}
		StringList errors = testMatcher.errors;
		errors.addAll(matches);
		String errorString = errors.join("\n");
		// Log.log(Log.WARNING, ErrorMatcherPanel.class, errorString);
		GUIUtilities.error(JOptionPane.getFrameForComponent(this),
			"options.console.errors.checking", new String[] { errorString });
	}

	// }}}

	// {{{ ok() method
	public void apply()
	{
		validateRegex();
		if (!isOK)
		{
			String errorString = testMatcher.errors.join("\n");
			GUIUtilities.error(JOptionPane.getFrameForComponent(this),
				"options.console.errors.checking", new String[] { errorString });
		}
		else
		{
			testMatcher.user = true;
			matcher.user = true;
		}
	} // }}}

	// {{{ cancel() method
	public void cancel()
	{
		updateTextFields(matcher);
	} // }}}

	// {{{ isOK() method
	public boolean isOK()
	{
		return isOK;
	} // }}}

	// }}}

	// {{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
/*			if (evt.getSource() == apply) 
			{
				apply();
			}
			else */ if (evt.getSource() == test)
			{
				testRegex();
			}
			else if (evt.getSource() == cancel)
			{
				cancel();
			}
		}
	} // }}}

	// {{{ Private data members
	
	private ErrorMatcher matcher;

	private ErrorMatcher testMatcher;

	private JTextField errorName;

	private JTextField error;

	private JTextField warning;

	private JTextField extra;

	private JTextField filename;

	private JTextField line;

	private JTextField message;

	private JTextArea testArea;

//	private Button apply;
	
	private Button test;

	private Button cancel;
	
	JCheckBox isEnabled;

	private boolean isOK;
	// / }}}
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}


	public void keyPressed(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}


	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	class KeyHandler extends KeyAdapter 
	{
		public void keyPressed(KeyEvent evt)
		{
			if(evt.getKeyCode() == KeyEvent.VK_ENTER)
			{
				Caret caret = testArea.getCaret();
				testArea.insert("\n", caret.getDot());
				evt.consume();
			}
		}
	}
} // }}}
