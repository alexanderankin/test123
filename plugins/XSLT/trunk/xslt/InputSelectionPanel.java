/*
 * Created on 19-dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package xslt;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * @author Pitje
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class InputSelectionPanel extends JPanel {

	private static final String LAST_SOURCE = "xslt.last-source";
	
	private View view;
	private XSLTProcessor processor;
	private JButton browseButton;
	private JRadioButton bufferRadio;
	private JRadioButton fileRadio;
	private JTextField sourceField = new JTextField();
	
	private XsltAction radioSelectAction = new RadioSelectAction();
	private XsltAction sourceSelectAction = new SourceSelectAction();

	InputSelectionPanel(View view, XSLTProcessor processor, MouseListener mouseListener) {
		super (new BorderLayout());
		
		this.view = view;
		this.processor = processor;
		
		JPanel radioPanel = new JPanel(new BorderLayout());
		radioPanel.add(new JLabel(jEdit.getProperty("xslt.source.label")), BorderLayout.NORTH);

		createRadioButtons(new ButtonGroup());		
		radioPanel.add(bufferRadio, BorderLayout.CENTER);
		radioPanel.add(fileRadio, BorderLayout.SOUTH);	
		
		JLabel sourceLabel = new JLabel(jEdit.getProperty("xslt.source.browse.label"));

		JPanel sourceFieldPanel = new JPanel(new GridLayout(2,1));
		sourceFieldPanel.add(sourceLabel);
		
		sourceField.addMouseListener(mouseListener);
		
		if(jEdit.getProperty(LAST_SOURCE) == null) {
			sourceField.setText(jEdit.getProperty("xquery.selectInput.prompt"));
		} else {
			sourceField.setText(jEdit.getProperty(LAST_SOURCE));
		}
		
		sourceField.setEnabled(false); //because buffer is primarily selected
		sourceFieldPanel.add(sourceField);
		
		JPanel browseButtonPanel = new JPanel(new BorderLayout());
		browseButton = sourceSelectAction.getButton();
		browseButton.setEnabled(false); //because buffer is primarily selected
		browseButtonPanel.add(browseButton, BorderLayout.SOUTH);

		JPanel browsePanel = new JPanel(new BorderLayout(3, 0));
		browsePanel.add(sourceFieldPanel);
		browsePanel.add(browseButtonPanel, BorderLayout.EAST);
	
		add(radioPanel, BorderLayout.NORTH);
		add(browsePanel, BorderLayout.SOUTH);
	}
	
	private void createRadioButtons(ButtonGroup radioGroup) {
		bufferRadio = radioSelectAction.getRadioButton("xslt.source.buffer");
		fileRadio = radioSelectAction.getRadioButton("xslt.source.file");
		
		bufferRadio.setActionCommand("buffer");
		bufferRadio.setSelected(true);
		
		fileRadio.setActionCommand("file");

		radioGroup.add(bufferRadio);
		radioGroup.add(fileRadio);	
	};
	
	private void chooseFile() {
		String path = null;

		if(getSourceFile() != null && !getSourceFile().equals(""))
			path = MiscUtilities.getParentOfPath(getSourceFile());

		String[] selections = GUIUtilities.showVFSFileDialog(view, path, JFileChooser.OPEN_DIALOG, false);

		if(selections != null) {
				setSourceFile(selections[0]);
		}

		Container topLevelAncestor = processor.getTopLevelAncestor();

		if(topLevelAncestor instanceof JFrame) {
			((JFrame)topLevelAncestor).toFront();
		}
	}
	
	public String getSourceFile() {
		return sourceField.getText();
	}
	
	public boolean isFileSelected() {
		return fileRadio.isSelected();
	}

	void setSourceFile(String sourceFile) {
		sourceField.setText(sourceFile);
		jEdit.setProperty(LAST_SOURCE, sourceFile);
	}

	JTextField getTextField() {
		return sourceField;
	}
	
	public boolean isSourceFileDefined() {
		return !getSourceFile().equals(jEdit.getProperty("xslt.source.browse.prompt"));
	}
	
	JPopupMenu initSelectionPanelMenu(XsltAction fileOpenAction) {
		XsltAction[] actions = new XsltAction[] {fileOpenAction, null, sourceSelectAction,};

		return XsltAction.initMenu(actions);
	
	}
	
	boolean isSourceFieldEventSource(Object eventSource) {
		return this.sourceField == eventSource;
	}
	
	private class RadioSelectAction extends XsltAction {

		public void actionPerformed(ActionEvent e) {
			Log.log(Log.MESSAGE, this, "select input action performed");
			if ("buffer".equals(e.getActionCommand())) {
				sourceField.setEnabled(false);
				browseButton.setEnabled(false);
			}else if ("file".equals(e.getActionCommand())) { 
				sourceField.setEnabled(true);
				browseButton.setEnabled(true);	
			}
		}

		protected String getActionName() {
			return "source.radioselect";
		}
	}

	private class SourceSelectAction extends XsltAction {

		public void actionPerformed(ActionEvent e) {
			Log.log(Log.MESSAGE, this, "select source action performed");
			chooseFile();
		}


		protected String getActionName() {
			return "source.select";
		}

	}
	
}
