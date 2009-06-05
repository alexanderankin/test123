package tags.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

import tags.AttributeValueCollisionResolver;
import tags.TagsPlugin;

@SuppressWarnings("serial")
public class ActionOptionPane extends AbstractOptionPane implements ActionListener {

	private JList actions;
	private RolloverButton add;
	private RolloverButton remove;
	private DefaultListModel actionModel;
	
	//{{{ ActionOptionPane constructor
	public ActionOptionPane()
	{
		super("tags.actions");
	} //}}}

	@Override
	protected void _init() {
		addComponent(new JLabel("Actions for resolving tag collisions:"));
		actionModel = new DefaultListModel();
		actions = new JList(actionModel);
		addComponent(new JScrollPane(actions));
		JPanel buttons = new JPanel();
		add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.addActionListener(this);
		remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.addActionListener(this);
		buttons.add(add);
		buttons.add(remove);
		addComponent(buttons);
		AttributeValueCollisionResolver[] actions = TagsPlugin.getAllActions();
		for (int i = 0; i < actions.length; i++)
			actionModel.addElement(actions[i]);
	}

	@Override
	protected void _save() {
		jEdit.setBooleanProperty(TagsPlugin.OPTION_HAS_DYNAMIC_ACTIONS,
			(! actionModel.isEmpty()));
		jEdit.setIntegerProperty(TagsPlugin.OPTION_NUM_DYNAMIC_ACTIONS, actionModel.size());
		for (int i = 0; i < actionModel.size(); i++) {
			AttributeValueCollisionResolver resolver =
				(AttributeValueCollisionResolver) actionModel.get(i);
			resolver.save(i);
		}
		TagsPlugin.reloadActions();
	}
	
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == add) {
			AttributeValueCollisionResolver resolver =
				AttributeValueCollisionResolver.getNewResolver(
						GUIUtilities.getParentDialog(this));
			if (resolver == null)
				return;
			actionModel.addElement(resolver);
		}
		else if (event.getSource() == remove) {
			int i = actions.getSelectedIndex();
			if (i >= 0) {
				actionModel.remove(i);
				if (i >= actionModel.size())
					i = actionModel.size() - 1;
				actions.setSelectedIndex(i);
			}
		}
	}
	
}
