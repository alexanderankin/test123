package superabbrevs.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.View;
import java.awt.event.*;
import superabbrevs.SuperAbbrevs;

/*
 * I modified Slava Pestov code
 * @author Sune Simonsen
 */ 
public class AddAbbrevDialog extends JDialog {
	public AddAbbrevDialog(View view, String abbrev){
		super(view,jEdit.getProperty("add-abbrev.title"),true);

		this.view = view;

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		editor = new AbbrevEditor();
		editor.setAbbrev(abbrev);
		editor.setBorder(new EmptyBorder(6,0,12,0));
		content.add(BorderLayout.CENTER,editor);

		Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createGlue());
		global = new JButton(jEdit.getProperty("add-abbrev.global"));
		global.addActionListener(new ActionHandler());
		box.add(global);
		box.add(Box.createHorizontalStrut(6));
		modeSpecific = new JButton(jEdit.getProperty("add-abbrev.mode"));
		modeSpecific.addActionListener(new ActionHandler());
		box.add(modeSpecific);
		box.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(new ActionHandler());
		box.add(cancel);
		box.add(Box.createGlue());
		content.add(BorderLayout.SOUTH,box);

		KeyListener listener = new KeyHandler();
		addKeyListener(listener);
		editor.getTemplateTextArea().addKeyListener(listener);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		if(abbrev == null)
			GUIUtilities.requestFocus(this,editor.getAbbrevField());
		else
			GUIUtilities.requestFocus(this,editor.getTemplateTextArea());

		pack();
		setLocationRelativeTo(view);
		setVisible(true);
	}

	// private members
	private View view;
	private AbbrevEditor editor;
	private JButton global;
	private JButton modeSpecific;
	private JButton cancel;

	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Object source = evt.getSource();
			if(source == global) {
				String _abbrev = editor.getAbbrev();
				if(_abbrev == null || _abbrev.length() == 0) {
					getToolkit().beep();
					return;
				}
				SuperAbbrevs.addGlobalAbbrev(_abbrev,editor.getExpansion());
				SuperAbbrevs.expandAbbrev(view,false);
			} else if(source == modeSpecific) {
				String _abbrev = editor.getAbbrev();
				if(_abbrev == null || _abbrev.length() == 0) {
					getToolkit().beep();
					return;
				}
				SuperAbbrevs.addModeAbbrev(view.getBuffer().getMode().getName(),
					_abbrev,editor.getExpansion());
				SuperAbbrevs.expandAbbrev(view,false);
			}

			dispose();
		}
	}

	class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent evt) {
			if(evt.getKeyCode() == KeyEvent.VK_ESCAPE)
				dispose();
		}
	}
}
