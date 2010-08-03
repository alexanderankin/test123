package menuEditor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class MenuEditor extends JPanel
{
	private static final String viewMenuBar = "view.mbar";
	private static final String spaceSeparator = "\\s+";
	private static final String menuSeparator = "-";
	private View view;
	private JComboBox [] menu = new JComboBox[2];
	private JList [] items = new JList[2];
	private JButton add, remove, up, down;
	private JButton ok, apply, cancel, restoreDefault;
	private DefaultComboBoxModel [] menuModel = new DefaultComboBoxModel[2];
	private DefaultListModel [] itemModel = new DefaultListModel[2];
	private ArrayList<MenuElement> menus = new ArrayList<MenuElement>();

	public MenuEditor(View view)
	{
		this.view = view;
		initMenuData();
		setLayout(new BorderLayout());
		JPanel center = new JPanel();
		JPanel from = createPanel(0);
		JPanel to = createPanel(1);
		JPanel movePanel = new JPanel(new GridLayout(0, 1));
		add = new JButton("Add");
		remove = new JButton("Remove");
		up = new JButton("Up");
		down = new JButton("Down");
		movePanel.add(add);
		movePanel.add(remove);
		movePanel.add(up);
		movePanel.add(down);
		center.add(from);
		center.add(movePanel);
		center.add(to);
		add(center, BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		ok = new JButton("Ok");
		apply = new JButton("Apply");
		cancel = new JButton("Cancel");
		restoreDefault = new JButton("Restore default");
		restoreDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				restoreDefault();
			}
		});
		bottom.add(ok);
		bottom.add(apply);
		bottom.add(cancel);
		bottom.add(restoreDefault);
		add(bottom, BorderLayout.SOUTH);
	}
	private void restoreDefault()
	{
		for (MenuElement menuElem: menus)
		{
			
		}
	}
	private JPanel createPanel(final int index)
	{
		menuModel[index] = new DefaultComboBoxModel();
		for (MenuElement menuElem: menus)
			menuModel[index].addElement(menuElem);
		menu[index] = new JComboBox(menuModel[index]);
		itemModel[index] = new DefaultListModel();
		updateItems(index, menus.get(0));
		items[index] = new JList(itemModel[index]);
		JPanel p = new JPanel(new BorderLayout());
		JPanel menuPanel = new JPanel();
		menuPanel.add(new JLabel("menu:"));
		menuPanel.add(menu[index]);
		JPanel itemPanel = new JPanel(new BorderLayout());
		itemPanel.add(new JLabel("Items:"), BorderLayout.NORTH);
		itemPanel.add(new JScrollPane(items[index]), BorderLayout.CENTER);
		p.add(menuPanel, BorderLayout.NORTH);
		p.add(itemPanel, BorderLayout.CENTER);
		menu[index].addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				updateItems(index, (MenuElement)e.getItem());
			}
		});
		return p;
	}
	private void updateItems(int index, MenuElement menuElem)
	{
		itemModel[index].clear();
		for (MenuElement child: menuElem.children)
			itemModel[index].addElement(child);
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
			boolean isMenu = item.startsWith("%");
			if (isMenu)
				item = item.substring(1);
			MenuElement child = parent.addChild(item);
			if (isMenu)
				initMenu(child);
		}
	}
	private void initMenuData()
	{
		String [] menuIds = getMenus();
		for (String menuId: menuIds)
		{
			MenuElement menuElem = new MenuElement(menuId);
			menus.add(menuElem);
			initMenu(menuElem);
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
	}
}
