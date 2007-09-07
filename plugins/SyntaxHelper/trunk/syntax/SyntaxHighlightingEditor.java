package syntax;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.print.Doc;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.gui.StyleEditor;
import org.gjt.sp.jedit.options.SyntaxHiliteOptionPane;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class SyntaxHighlightingEditor extends JPanel {
	
	private final class TokenChangeListener implements CaretListener {
		public void caretUpdate(CaretEvent arg0) {
			update();
		}
		public void update() {
			JEditTextArea textArea = view.getTextArea();
			int lineNum = textArea.getCaretLine();
			Buffer buffer = view.getBuffer();
			int start = buffer.getLineStartOffset(lineNum);
			int position = textArea.getCaretPosition();
			
			DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
			buffer.markTokens(lineNum,tokenHandler);
			Token token = tokenHandler.getTokens();
		
			while(token.id != Token.END)
			{
				int next = start + token.length;
				if (start <= position && next > position)
					break;
				start = next;
				token = token.next;
			}
			if (token.id == Token.END || token.id == Token.NULL)
			{
				table.getSelectionModel().clearSelection();
				return;
			}
			int index = 3 + token.id;
			table.getSelectionModel().setSelectionInterval(index, index);
			table.scrollRectToVisible(table.getCellRect(index, 0, true));
		}
	}
	private final class StyleEditorCloseHandler extends WindowAdapter {
		public void windowClosed(WindowEvent arg0) {
			Window w = arg0.getWindow();
			if (w instanceof StyleEditor) {
				optionPane.save();
				jEdit.propertiesChanged();
				w.removeWindowListener(this);
			}
		}
	}
	View view;
	SyntaxHiliteOptionPane optionPane = null;
	Map<String, String> styles;
	private StyleEditorCloseHandler styleEditorCloseHandler;
	private JCheckBox followCaret;
	private TokenChangeListener caretListener;
	private JTable table;
	
	public SyntaxHighlightingEditor(final View view, String position) {
		super(new BorderLayout());
		
		this.view = view;
		styles = new HashMap<String, String>();
		apply();
		initOptionPane();
		JButton apply = new JButton(jEdit.getProperty("messages.SyntaxHelper.apply"));
		JButton cancel = new JButton(jEdit.getProperty("messages.SyntaxHelper.cancel"));
		if (table != null)
		{
			followCaret = new JCheckBox(jEdit.getProperty("messages.SyntaxHelper.followCaret"));
			caretListener = new TokenChangeListener();
			followCaret.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					if (followCaret.isSelected())
					{
						view.getTextArea().addCaretListener(caretListener);
						caretListener.update();
					}
					else
						view.getTextArea().removeCaretListener(caretListener);
				}
			});
		}
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.add(apply);
		buttons.add(cancel);
		JPanel controls = new JPanel(new BorderLayout());
		boolean vertLayout = position.equals(DockableWindowManager.LEFT) ||
			position.equals(DockableWindowManager.RIGHT) ||
			position.equals(DockableWindowManager.FLOATING);
		if (vertLayout)
		{
			controls.add(buttons, BorderLayout.NORTH);
			controls.add(followCaret, BorderLayout.SOUTH);
		}
		else
		{
			controls.add(buttons, BorderLayout.WEST);
			controls.add(followCaret, BorderLayout.CENTER);
		}
		add(controls, BorderLayout.SOUTH);

		styleEditorCloseHandler = new StyleEditorCloseHandler();
		view.addWindowListener(new WindowAdapter() {
			public void windowDeactivated(WindowEvent arg0) {
				Window active = arg0.getOppositeWindow();
				if (active instanceof StyleEditor) {
					active.addWindowListener(styleEditorCloseHandler);
				}
			}
		});
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				apply();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				revert();
			}
		});
	}
	private void revert() {
		for (int i = 0; i < 4; i++)
		{
			String propName = "view.style.foldLine." + i;
			jEdit.setProperty(propName, styles.get(propName));
		}
		for(int i = 1; i < Token.ID_COUNT; i++)
		{
			String tokenName = Token.tokenToString((byte)i);
			String propName = "view.style." + tokenName.toLowerCase();
			jEdit.setProperty(propName, styles.get(propName));
		}
		jEdit.propertiesChanged();
		initOptionPane();
	}
	private void initOptionPane() {
		if (optionPane != null)
			remove(optionPane);
		optionPane = new SyntaxHiliteOptionPane();
		add(optionPane, BorderLayout.CENTER);
		optionPane.init();
		table = findTable(optionPane);
		if (table != null)
				table.setRowSelectionAllowed(true);
	}
	private JTable findTable(Container c) {
		for (int i = 0; i < c.getComponentCount(); i++) {
			Component comp = c.getComponent(i); 
			if (comp instanceof JTable)
				return (JTable) comp;
			else if (comp instanceof Container) {
				JTable t = findTable((Container) comp);
				if (t != null)
					return t;
			}
		}
		return null;
	}
	private void apply() {
		for (int i = 0; i < 4; i++)
		{
			String propName = "view.style.foldLine." + i;
			styles.put(propName, jEdit.getProperty(propName));
		}
		for(int i = 1; i < Token.ID_COUNT; i++)
		{
			String tokenName = Token.tokenToString((byte)i);
			String propName = "view.style." + tokenName.toLowerCase();
			styles.put(propName, jEdit.getProperty(propName));
		}
	}
}
