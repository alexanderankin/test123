package options;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

import ctags.CtagsInterfacePlugin;
import ctags.QueryAction;

@SuppressWarnings("serial")
public class ActionsOptionPane extends AbstractOptionPane {

	static public final String OPTION = CtagsInterfacePlugin.OPTION;
	static public final String MESSAGE = CtagsInterfacePlugin.MESSAGE;
	static public final String ACTIONS = OPTION + "actions.";
	JList actions;
	DefaultListModel actionsModel;
	
	public ActionsOptionPane() {
		super("CtagsInterface-Actions");
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		actionsModel = new DefaultListModel(
				);
		QueryAction[] queries = CtagsInterfacePlugin.getActions();
		for (int i = 0; i < queries.length; i++)
			actionsModel.addElement(queries[i]);
		actions = new JList(actionsModel);
		JScrollPane scroller = new JScrollPane(actions);
		scroller.setBorder(BorderFactory.createTitledBorder(
				jEdit.getProperty(MESSAGE + "actions")));
		addComponent(scroller, GridBagConstraints.HORIZONTAL);
		JPanel buttons = new JPanel();
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		buttons.add(add);
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		buttons.add(remove);
		addComponent(buttons);

		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				EditAction action = new ActionEditor().getAction();
				actionsModel.addElement(action);
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int i = actions.getSelectedIndex();
				if (i >= 0)
					actionsModel.removeElementAt(i);
			}
		});
	}

	public void save() {
		jEdit.setIntegerProperty(ACTIONS + "size", actionsModel.size());
		for (int i = 0; i < actionsModel.size(); i++) {
			QueryAction qa = (QueryAction) actionsModel.getElementAt(i);
			qa.save(i);
		}
		CtagsInterfacePlugin.reloadActions();
	}

	public class ActionEditor extends JDialog {
		
		EditAction action;
		JTextField query;
		JTextField name;
		JButton ok;
		JButton cancel;
		
		public ActionEditor() {
			super(jEdit.getActiveView(), jEdit.getProperty(MESSAGE + "actionEditorTitle"),
				true);
			setLayout(new BorderLayout());
			JPanel p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p, BorderLayout.NORTH);
			p.add(new JLabel(jEdit.getProperty(MESSAGE + "actionName")));
			name = new JTextField(40);
			p.add(name);
			p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p, BorderLayout.CENTER);
			p.add(new JLabel(jEdit.getProperty(MESSAGE + "sqlQuery")));
			query = new JTextField(80);
			p.add(query);
			p = new JPanel();
			p.setAlignmentX(LEFT_ALIGNMENT);
			add(p, BorderLayout.SOUTH);
			JButton ok = new JButton("Ok");
			p.add(ok);
			JButton cancel = new JButton("Cancel");
			p.add(cancel);
			
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					action = new QueryAction(name.getText(), query.getText());
					dispose();
				}
			});
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dispose();
				}
			});
			action = null;
			
			pack();
			setVisible(true);
		}

		public EditAction getAction() {
			return action;
		}
	}

}
