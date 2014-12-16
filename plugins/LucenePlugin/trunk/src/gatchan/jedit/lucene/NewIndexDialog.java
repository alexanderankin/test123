/*
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009, 2011 Matthieu Casanova
 * Copyright (C) 2009, 2011 Shlomy Reinstein
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
package gatchan.jedit.lucene;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

@SuppressWarnings("serial")
public class NewIndexDialog extends JDialog
{
	public static final String OPTIONS = "lucene.options.";
	public static final String MESSAGE = "lucene.message.";
	private static final String GEOMETRY = OPTIONS + "NewLuceneIndexDialog";
	private static final String CLEAN_FILENAME_REGEX = "CleanFileRegex";
	
	private final JTextField name;
	private final JComboBox<String> analyzer;
	private boolean accepted;
	
	private final Pattern pattern;
	private Matcher matcher;

	public NewIndexDialog(Frame frame, String initialName)
	{
		super(frame, jEdit.getProperty(MESSAGE + "NewIndexDialogTitle"), true);
		Log.log(Log.DEBUG, this, OPTIONS + CLEAN_FILENAME_REGEX + " = " +
				jEdit.getProperty(OPTIONS + CLEAN_FILENAME_REGEX));

		pattern = Pattern.compile(jEdit.getProperty(OPTIONS + CLEAN_FILENAME_REGEX));
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				saveGeometry();
			}
		});
		setLayout(new GridLayout(0, 1));
		
		final JLabel fileNameError = new JLabel();
		
		// Name panel
		JPanel p = new JPanel();
		add(p);
		p.add(new JLabel(jEdit.getProperty(MESSAGE + "IndexName")));
		p.add(name = new JTextField(30));
		if (initialName != null)
		{
			name.setText(initialName);
			matcher = pattern.matcher(initialName);
			Log.log(Log.DEBUG, this, "Matching " + initialName + " : " + matcher
					+ " : " + matcher.matches());
			
			if (matcher.matches())
				fileNameError.setText(jEdit.getProperty(MESSAGE + "BadFileName"));
			else
				fileNameError.setText("");
		}

		// Analyzer panel
		p = new JPanel(new FlowLayout(FlowLayout.LEADING));
		add(p);
		p.add(new JLabel(jEdit.getProperty(MESSAGE + "Analyzer")));
		
		analyzer = new JComboBox<>(AnalyzerFactory.getAnalyzerNames());
		p.add(analyzer);
		
		String defaultAnalyzer = jEdit.getProperty(LucenePlugin.LUCENE_DEFAULT_ANALYZER);
		if (defaultAnalyzer != null)
			analyzer.setSelectedItem(defaultAnalyzer);
		// Button panel

		fileNameError.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		add(fileNameError);

		JPanel buttons = new JPanel();
		add(buttons);
		JButton ok = new JButton("Ok");
		buttons.add(ok);
		final JButton cancel = new JButton("Cancel");
		buttons.add(cancel);
		ok.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{				
				matcher = pattern.matcher(name.getText());
				Log.log(Log.DEBUG, this,"Matching " + name.getText() + " : " + matcher
						+ " : " + matcher.matches());

				if (matcher.matches())
					fileNameError.setText(jEdit.getProperty(MESSAGE + "BadFileName"));
				else
				{
					saveGeometry();
					save();
					dispose();
				}
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		ActionListener cancelListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				cancel.doClick();
			}
		};
		rootPane.registerKeyboardAction(cancelListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		pack();
		GUIUtilities.loadGeometry(this, GEOMETRY);
	}

	private void save()
	{
		accepted = true;
	}

	public String getIndexName()
	{
		return name.getText();
	}

	public String getIndexAnalyzer()
	{
		return analyzer.getSelectedItem().toString();
	}

	public boolean accepted()
	{
		return accepted;
	}

	private void saveGeometry()
	{
		GUIUtilities.saveGeometry(this, GEOMETRY);
	}
}
