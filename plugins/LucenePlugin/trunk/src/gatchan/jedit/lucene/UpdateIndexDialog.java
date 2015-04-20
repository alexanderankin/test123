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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

@SuppressWarnings("serial")
public class UpdateIndexDialog extends JDialog
{
	public static final String OPTIONS = "lucene.options.";
	public static final String MESSAGE = "lucene.message.";
	private static final String GEOMETRY = OPTIONS + "UpdateLuceneIndexDialog";
	private static final String CLEAN_FILENAME_REGEX = "CleanFileRegex";
	
	private final JComboBox<String> indexList;
	private final JTextArea displayArea;
	private boolean accepted;
	
	public UpdateIndexDialog(Frame frame)
	{
		super(frame, jEdit.getProperty(MESSAGE + "UpdateIndexDialogTitle"), true);
		Log.log(Log.DEBUG, this, OPTIONS + CLEAN_FILENAME_REGEX + " = " +
				jEdit.getProperty(OPTIONS + CLEAN_FILENAME_REGEX));

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				saveGeometry();
			}
		});
		setLayout(new BorderLayout());
				
		// Display panel
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
		add(p, BorderLayout.NORTH);
		p.add(new JLabel(jEdit.getProperty(MESSAGE + "IndexName")));
		
		indexList = new JComboBox<>(LucenePlugin.instance.getIndexes());
		p.add(indexList);
		
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(displayArea);
		add(scrollPane,BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		add(buttons, BorderLayout.SOUTH);
		JButton display = new JButton("Display");
		buttons.add(display);
		final JButton delete = new JButton("Delete");
		buttons.add(delete);
		display.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{				
				int index = indexList.getSelectedIndex();
				String indexName = indexList.getItemAt(index);
				
				StringBuilder fileList = new StringBuilder();
				try 
				{
					for (String fileName : LucenePlugin.CENTRAL.getAllDocuments(indexName)) {
						fileList.append(fileName);
						fileList.append("\n");
					}
				} 
				catch (IndexInterruptedException e1) 
				{
					Log.log(Log.WARNING, this, "Halting due to Interrupt");	
				}
				
				displayArea.setText(fileList.toString());
			}
		});

		delete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int index = indexList.getSelectedIndex();
				String indexName = indexList.getItemAt(index);
				try 
				{
					LucenePlugin.instance.removeIndex(indexName);
					
				    // getting exiting combo box model
			        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) indexList.getModel();
			        
			        // removing old data
			        model.removeAllElements();

			        for (String item : LucenePlugin.instance.getIndexes()) {
			            model.addElement(item);
			        }

			        // setting model with new data
			        indexList.setModel(model);
					displayArea.setText("");
				} 
				catch (IndexInterruptedException e1) 
				{
					Log.log(Log.WARNING, this, "Halting due to Interrupt");	
				}
			
			}
		});

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		ActionListener cancelListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				delete.doClick();
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

	public String getIndexAnalyzer()
	{
		return indexList.getSelectedItem().toString();
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
