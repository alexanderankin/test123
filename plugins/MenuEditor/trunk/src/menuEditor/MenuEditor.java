package menuEditor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.*;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class MenuEditor extends JDialog
{
	private static final String viewMenuBar = "view.mbar";
	private static final String spaceSeparator = "\\s+";
	private static final String menuSeparator = "-";
	private static final String subMenu = "%";
	private JComboBox menu, actionSet;
	private JList items, unusedItems;
	private JButton add, remove, up, down;
	private JButton ok, apply, cancel, restoreDefault;
	private DefaultComboBoxModel menuModel, actionSetModel;
	private DefaultListModel itemModel, unusedModel;
	private ArrayList<MenuElement> menus = new ArrayList<MenuElement>();
	private HashSet<String> used = new HashSet<String>();
	private HashMap<String, ArrayList<MenuElement>> unused =
		new HashMap<String, ArrayList<MenuElement>>();

	public MenuEditor(View view)
	{
		super(view, "Menu Editor");
		initData();
		JPanel contentPanel = new JPanel(new BorderLayout());
		JPanel center = new JPanel(new BorderLayout(5,5));
		JPanel from = createMenuPanel();
		JPanel to = createActionPanel();
		JPanel movePanel = new JPanel(new GridLayout(0, 1));
		movePanel.setLayout(new BoxLayout(movePanel, BoxLayout.Y_AXIS));
		add = new JButton("Add");
		remove = new JButton("Remove");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				removeSelected();
			}
		});
		up = new JButton("Up");
		down = new JButton("Down");
		movePanel.add(add);
		movePanel.add(remove);
		movePanel.add(up);
		movePanel.add(down);
		center.add(from, BorderLayout.WEST);
		center.add(movePanel);
		center.add(to, BorderLayout.EAST);
		contentPanel.add(center, BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				apply();
				dispose();
			}
		});
		apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				apply();
			}
		});
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		restoreDefault = new JButton("Restore default");
		restoreDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				restoreDefault();
			}
		});
		bottom.add(ok);
		bottom.add(apply);
		bottom.add(cancel);
		bottom.add(restoreDefault);
		contentPanel.add(bottom, BorderLayout.SOUTH);
		setContentPane(contentPanel);
		pack();
		setVisible(true);

	}
	private void initData()
	{
		initMenuData();
		initUnusedActions();
	}
	private void removeSelected()
	{
		int [] selected = items.getSelectedIndices();
		MenuElement parentMenu = (MenuElement) menu.getSelectedItem();
		parentMenu.removeChildren(selected);
		updateItems(parentMenu);
	}
	private JPanel createActionPanel()
	{
		JPanel p = new JPanel(new BorderLayout());
		JPanel actionSetPanel = new JPanel();
		actionSetPanel.add(new JLabel("Action set:"));
		actionSetModel = new DefaultComboBoxModel();
		ArrayList<String> actionSetNames = new ArrayList<String>();
		for (String actionSet: unused.keySet())
			actionSetNames.add(actionSet);
		Collections.sort(actionSetNames);
		for (String actionSet: actionSetNames)
			actionSetModel.addElement(actionSet);
		actionSet = new JComboBox(actionSetModel);
		actionSetPanel.add(actionSet);
		p.add(actionSetPanel, BorderLayout.NORTH);
		JPanel actionPanel = new JPanel(new BorderLayout());
		actionPanel.add(new JLabel("Available items:"), BorderLayout.NORTH);
		unusedModel = new DefaultListModel();
		updateActions((String)actionSetModel.getElementAt(0));
		unusedItems = new JList(unusedModel);
		actionPanel.add(new JScrollPane(unusedItems), BorderLayout.CENTER);
		p.add(actionPanel, BorderLayout.CENTER);
		actionSet.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				updateActions((String)e.getItem());
			}
		});
		return p;
	}
	private void updateActions(String actionSet)
	{
		unusedModel.clear();
		for (MenuElement child: unused.get(actionSet))
			unusedModel.addElement(child);
	}
	private void applyMenu(MenuElement menuElem)
	{
		ArrayList<String> children = new ArrayList<String>();
		if (menuElem.children != null)
		{
			for (MenuElement childElem: menuElem.children)
			{
				if (childElem.children != null)
				{
					applyMenu(childElem);
					children.add(subMenu + childElem.menu);
				}
				else
					children.add(childElem.menu);
			}
		}
		StringBuilder menuString = new StringBuilder();
		for (String child: children)
		{
			if (menuString.length() > 0)
				menuString.append(" ");
			menuString.append(child);
		}
		jEdit.setProperty(menuElem.menu, menuString.toString());
		jEdit.saveSettings();
	}
	private void apply()
	{
		for (MenuElement menuElem: menus)
			applyMenu(menuElem);
	}
	private void resetMenu(MenuElement menuElem)
	{
		jEdit.resetProperty(menuElem.menu);
		if (menuElem.children == null)
			return;
		for (MenuElement childElem: menuElem.children)
			resetMenu(childElem);
	}
	private void restoreDefault()
	{
		for (MenuElement menuElem: menus)
			resetMenu(menuElem);
		initData();
		updateItems((MenuElement) menu.getSelectedItem());
	}
	private JPanel createMenuPanel()
	{
		menuModel = new DefaultComboBoxModel();
		for (MenuElement menuElem: menus)
			menuModel.addElement(menuElem);
		menu = new JComboBox(menuModel);
		itemModel = new DefaultListModel();
		updateItems(menus.get(0));
		items = new JList(itemModel);
		JPanel p = new JPanel(new BorderLayout());
		JPanel menuPanel = new JPanel();
		menuPanel.add(new JLabel("menu:"));
		menuPanel.add(menu);
		JPanel itemPanel = new JPanel(new BorderLayout());
		itemPanel.add(new JLabel("Items:"), BorderLayout.NORTH);
		itemPanel.add(new JScrollPane(items), BorderLayout.CENTER);
		p.add(menuPanel, BorderLayout.NORTH);
		p.add(itemPanel, BorderLayout.CENTER);
		menu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				updateItems((MenuElement)e.getItem());
			}
		});
		return p;
	}
	private void updateItems(MenuElement menuElem)
	{
		itemModel.clear();
		for (MenuElement child: menuElem.children)
			itemModel.addElement(child);
	}
	private String [] getMenus()
	{
		String s = jEdit.getProperty(viewMenuBar);
		return s.split(spaceSeparator);
	}
	private String [] getMenuItems(String menu)
	{
		if (menu.equals(menuSeparator))
			return null;
		String prop = jEdit.getProperty(menu);
		if (prop == null)
			return null;
		return prop.split(spaceSeparator);
	}
	private static String getLabel(String menuItem)
	{
		if (menuItem.equals(menuSeparator))
			return menuSeparator;
		String label = jEdit.getProperty(menuItem + ".label", "");
		return label.replace("$", "");
	}
	private void initMenu(MenuElement parent)
	{
		String [] menuItems = getMenuItems(parent.menu);
		if (menuItems == null)
			return;
		for (String item: menuItems)
		{
			boolean isMenu = item.startsWith(subMenu);
			if (isMenu)
				item = item.substring(1);
			used.add(item);
			MenuElement child = parent.addChild(item);
			if (isMenu)
				initMenu(child);
		}
	}
	private void initMenuData()
	{
		used.clear();
		menus.clear();
		String [] menuIds = getMenus();
		for (String menuId: menuIds)
		{
			used.add(menuId);
			MenuElement menuElem = new MenuElement(menuId);
			menus.add(menuElem);
			initMenu(menuElem);
		}
	}
	private void initUnusedActions()
	{
		unused.clear();
		ActionSet [] actionSets = jEdit.getActionSets();
		for (ActionSet set: actionSets)
		{
			ArrayList<MenuElement> children = new ArrayList<MenuElement>();
			EditAction [] actions = set.getActions();
			for (EditAction action: actions)
			{
				String name = action.getName();
				if (used.contains(name))
					continue;
				children.add(new MenuElement(name));
			}
			if (! children.isEmpty())
			{
				Collections.sort(children, new Comparator<MenuElement>() {
					public int compare(MenuElement o1, MenuElement o2)
					{
						return o1.label.compareTo(o2.label);
					}
				});
				String actionSetName = set.getLabel();
				unused.put(actionSetName, children);
			}
		}
	}

	private static class MenuElement
	{
		String menu, label;
		ArrayList<MenuElement> children;
		public MenuElement(String menu)
		{
			this.menu = menu;
			label = getLabel(menu);
		}
		public String toString()
		{
			return label;
		}
		public MenuElement addChild(String child)
		{
			if (children == null)
				children = new ArrayList<MenuElement>();
			MenuElement childElem = new MenuElement(child); 
			children.add(childElem);
			return childElem;
		}
		public void removeChildren(int [] indices)
		{
			Arrays.sort(indices);
			for (int i = indices.length - 1; i >= 0; i--)
				children.remove(indices[i]);
		}
	}
}
