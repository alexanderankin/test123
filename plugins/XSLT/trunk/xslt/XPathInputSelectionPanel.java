/*
 * Created on 23-dec-2003
 *
 */
package xslt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

/**
 * @author Pitje
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * Panel housing the soure selection options
 */
class XPathInputSelectionPanel extends JPanel implements ActionListener {

	private static final String LAST_SOURCE = "xpath.last-source";
	
	private View view;
	private JRadioButton bufferRadio = new JRadioButton(jEdit.getProperty("xpath.select.buffer"));
	private JRadioButton fileRadio = new JRadioButton(jEdit.getProperty("xpath.select.file"));
	private JButton browseButton;
	private final JTextField sourceField = new JTextField();
	
	XPathInputSelectionPanel(View view) {
		super(new BorderLayout());
		
		this.view = view;
		createRadioButtons(new ButtonGroup());
		
		JPanel radioPanel = new JPanel(new BorderLayout());
		radioPanel.add(new JLabel(jEdit.getProperty("xpath.select.label")), BorderLayout.NORTH);
		radioPanel.add(bufferRadio, BorderLayout.CENTER);
		radioPanel.add(fileRadio, BorderLayout.SOUTH);

		JPanel sourceFieldPanel = new JPanel(new GridLayout(2,1));
		sourceFieldPanel.add(new JLabel(jEdit.getProperty("xpath.browse.label")));
		
		if(jEdit.getProperty(LAST_SOURCE) == null) {
			sourceField.setText(jEdit.getProperty("xpath.browse.prompt"));
		} else {
			sourceField.setText(jEdit.getProperty(LAST_SOURCE));
		}
		sourceField.setEnabled(false);
		sourceFieldPanel.add(sourceField);

		createBrowseButton();
		JPanel browseButtonPanel = new JPanel(new BorderLayout());		
		browseButtonPanel.add(browseButton, BorderLayout.SOUTH);
		
		JPanel fileSelectionPanel = new JPanel(new BorderLayout(3, 0));
		fileSelectionPanel.add(sourceFieldPanel);
		fileSelectionPanel.add(browseButtonPanel, BorderLayout.EAST);
		
		add(radioPanel, BorderLayout.NORTH);
		add(fileSelectionPanel, BorderLayout.SOUTH);
	}
	
	private void createRadioButtons(ButtonGroup radioGroup) {
		ActionListener bufferSelected = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setFileSelectionEnabled(false);
			}};
		bufferRadio.addActionListener(bufferSelected);
		bufferRadio.setSelected(true);
		
		ActionListener fileSelected = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setFileSelectionEnabled(true);
			}};
		fileRadio.addActionListener(fileSelected);
		
		radioGroup.add(bufferRadio);
		radioGroup.add(fileRadio);		
	}
	
	private void createBrowseButton() {
		String iconName = jEdit.getProperty("xpath.browse.button.icon");
		String toolTipText = jEdit.getProperty("xpath.browse.button.tooltip");

		String shortcut = jEdit.getProperty("xpath.select.shortcut");
		
		if(shortcut != null) {
			toolTipText += " (" + shortcut + ")";
		}
		URL url = XSLTProcessor.class.getResource(iconName);
		
		browseButton = new JButton(new ImageIcon(url));
		browseButton.setToolTipText(toolTipText);
		browseButton.addActionListener(this);
		
		Dimension dimension = new Dimension(30, 30);
		browseButton.setMinimumSize(dimension);
		browseButton.setMaximumSize(dimension);
		browseButton.setPreferredSize(dimension);
		browseButton.setEnabled(false);
	}
	
	public String getSourceFieldText() {
		return sourceField.getText();
	};		
	
	public void setFileSelectionEnabled(boolean b) {
		sourceField.setEnabled(b);
		browseButton.setEnabled(b);
	};
	
	public boolean isFileSelected() {
		return fileRadio.isSelected();
	};
	
	public boolean isBufferSelected() {
		return bufferRadio.isSelected();
	};

	public void actionPerformed(ActionEvent event) {	
		String[] selections = GUIUtilities.showVFSFileDialog(view, "", JFileChooser.OPEN_DIALOG, false);
		if(selections != null) {
			sourceField.setText(selections[0]);
			jEdit.setProperty(LAST_SOURCE, selections[0]);
		}
	}
}
