package menuEditor;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.*;

import jdiff.util.Diff;
import jdiff.util.DiffNormalOutput;
import jdiff.util.DiffOutput;
import jdiff.util.Diff.Change;
import jdiff.util.patch.normal.Patch;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

@SuppressWarnings("serial")
public class MenuEditor extends JDialog
{
	private static final String viewMenuBar = "view.mbar";
	private static final String spaceSeparator = "\\s+";
	private static final String menuSeparator = "-";
	private static final String subMenu = "%";
	private static String [] unchangedMenus;
	private static HashMap<String, String[]> unchangedMenuItems =
		new HashMap<String, String[]>();
	private JComboBox menu, actionSet;
	private JList items, allActions;
	private RolloverButton add, remove, up, down;
	private JButton ok, apply, cancel, restoreDefault;
	private DefaultComboBoxModel menuModel, actionSetModel;
	private DefaultListModel itemModel, allActionsModel;
	private ArrayList<MenuElement> menus = new ArrayList<MenuElement>();
	private HashMap<String, ArrayList<MenuElement>> actionSetMap =
		new HashMap<String, ArrayList<MenuElement>>();
	private boolean itemListInitialized = false;
	private static File home;

	public static void start()
	{
		home = jEdit.getPlugin("menuEditor.MenuEditorPlugin").getPluginHome();
		if (! home.isDirectory())
			home.mkdir();
		resetAllMenus();
		unchangedMenus = getMenus();
		for (String mi: unchangedMenus)
		{
			String [] items = getMenuItems(mi);
			if (items != null)
				unchangedMenuItems.put(mi, items);
		}
		applyDiff();
	}
	public static void stop()
	{
	}
	private static String readFile(String path)
	{
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(path));
			StringBuilder contents = new StringBuilder();
			String line = null;
			while ((line = input.readLine()) != null)
				contents.append(line + "\n");
			input.close();
			return contents.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	private static void writeFile(String path, String content)
	{
		try
		{
			BufferedWriter output = new BufferedWriter(new FileWriter(path));
			output.write(content);
			output.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private static String join(String [] lines, char separator)
	{
		StringBuilder sb = new StringBuilder();
		for (String line: lines)
			sb.append(line + separator);
		return sb.toString();
	}
	private static void applyDiff()
	{
		String diffPath = home.getAbsolutePath() + File.separator;
		for (String menu: unchangedMenus)
		{
			String diffFile = diffPath + menu + ".diff";
			if (! new File(diffFile).exists())
				continue;
			String [] items = unchangedMenuItems.get(menu);
			if (items == null)
				continue;
			String diff = readFile(diffFile);
			String patched;
			try
			{
				patched = Patch.patchNormal(diff, join(items, '\n'));
			} catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
			String newMenu = join(patched.split("\n"), ' ');
			jEdit.setProperty(menu, newMenu);
		}
		jEdit.propertiesChanged();
	}
	private void createDiff(MenuElement menuElem)
	{
		String menu = menuElem.menu;
		jEdit.resetProperty(getMenuPropName(menu));
		String [] orig = unchangedMenuItems.get(menu);
		String [] modified = menuElem.getChildren();
		Diff diff = new Diff(orig, modified);
		Change edit = diff.diff_2();
        StringWriter sw = new StringWriter();
        DiffOutput diffOutput = new DiffNormalOutput(orig, modified);
        diffOutput.setOut(new BufferedWriter(sw));
        diffOutput.setLineSeparator("\n");
        try
        {
            diffOutput.writeScript(edit);
    		String diffPath = home.getAbsolutePath() + File.separator;
    		String diffFile = diffPath + menu + ".diff";
            writeFile(diffFile, sw.toString());
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	return;
        }
	}
	private static void resetAllMenus()
	{
		jEdit.resetProperty(viewMenuBar);
		String [] m = getMenus();
		for (String mi: m)
			jEdit.resetProperty(getMenuPropName(mi));
		jEdit.propertiesChanged();
	}
	private static String getMenuPropName(String submenu)
	{
		if (submenu.startsWith(subMenu))
			return submenu.substring(1);
		return submenu;
	}
	private static String getProp(String propName)
	{
		return jEdit.getProperty(propName);
	}

	public MenuEditor(View view)
	{
		super(view, getProp("menu-editor.dialog.title"));
		JPanel contentPanel = new JPanel(new BorderLayout(5,5));
		contentPanel.add(new JLabel(jEdit.getProperty("menu-editor.help")),
			BorderLayout.NORTH);
		JPanel from = createMenuPanel();
		JPanel to = createActionPanel();
		JPanel center = new JPanel(new BorderLayout(5, 5));
		center.add(from, BorderLayout.WEST);
		center.add(to, BorderLayout.EAST);
		contentPanel.add(center, BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		ok = new JButton(getProp("menu-editor.ok"));
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				apply();
				dispose();
			}
		});
		apply = new JButton(getProp("menu-editor.apply"));
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				apply();
			}
		});
		cancel = new JButton(getProp("menu-editor.cancel"));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		restoreDefault = new JButton(getProp("menu-editor.restoreDefaults"));
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
		initData();
		setContentPane(contentPanel);
		pack();
		setLocationRelativeTo(view);
		setVisible(true);
		itemListInitialized = true;
	}
	private void initData()
	{
		initMenuData();
		initAllActions();
	}
	private void moveSelected(boolean up)
	{
		int [] selected = items.getSelectedIndices();
		Arrays.sort(selected);
		int [] moved = new int[selected.length];
		int movedIndex = 0;
		int selCount = selected.length;
		int itemCount = itemModel.getSize();
		int from, to, diff, move, limit; 
		if (up)
		{
			from = 0;
			to = selCount;
			diff = 1;
			move = -1;
			limit = 0;
		}
		else
		{
			from = selCount - 1;
			to = -1;
			diff = -1;
			move = 1;
			limit = itemCount - 1;
		}
		// Skip the items that can't move (items at list boundary)
		while ((from != to) && (selected[from] == limit))
		{
			moved[movedIndex++] = selected[from];
			from += diff;
			limit += diff;
		}
		// Move the items that can move
		int firstMoved = -1;
		for (; from != to; from += diff)
		{
			int i1 = selected[from];
			int i2 = i1 + move;
			if (firstMoved == -1)
				firstMoved = i2;
			MenuElement tmp = (MenuElement) itemModel.get(i1);
			itemModel.set(i1, itemModel.get(i2));
			itemModel.set(i2, tmp);
			moved[movedIndex++] = i2;
		}
		items.setSelectedIndices(moved);
		items.ensureIndexIsVisible(firstMoved);
	}
	private void addSelected()
	{
		MenuElement parentMenu = (MenuElement) menu.getSelectedItem();
		int [] selected = allActions.getSelectedIndices();
		if (selected.length == 0)
			return;
		int [] indices = new int[selected.length];
		int count = 0;
		for (int i: selected)
		{
			MenuElement element =
				(MenuElement) allActions.getModel().getElementAt(i);
			indices[count++] = parentMenu.addChild(element);
		}
		updateItems(parentMenu);
		items.setSelectedIndices(indices);
		items.ensureIndexIsVisible(indices[0]);
	}
	private void removeSelected()
	{
		int [] selected = items.getSelectedIndices();
		Arrays.sort(selected);
		for (int i = selected.length - 1; i >= 0; i--)
			itemModel.remove(selected[i]);
	}
	private JPanel createActionPanel()
	{
		JPanel p = new JPanel(new BorderLayout());
		JPanel actionSetPanel = new JPanel(new BorderLayout());
		actionSetPanel.add(new JLabel(getProp("menu-editor.actionSet")),
			BorderLayout.WEST);
		actionSetModel = new DefaultComboBoxModel();
		actionSet = new JComboBox(actionSetModel);
		JPanel actionSetComboPanel = new JPanel(new BorderLayout());
		actionSetComboPanel.add(actionSet, BorderLayout.WEST);
		actionSetPanel.add(actionSetComboPanel, BorderLayout.CENTER);
		p.add(actionSetPanel, BorderLayout.NORTH);
		JPanel actionPanel = new JPanel(new BorderLayout());
		actionPanel.add(new JLabel(getProp("menu-editor.actionSetItems")),
			BorderLayout.NORTH);
		allActionsModel = new DefaultListModel();
		allActions = new JList(allActionsModel);
		allActions.setDragEnabled(true);
		allActions.setTransferHandler(new ListTransferHandler());
		actionPanel.add(new JScrollPane(allActions), BorderLayout.CENTER);
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
		allActionsModel.clear();
		for (MenuElement child: actionSetMap.get(actionSet))
			allActionsModel.addElement(child);
	}
	private void createMenuDiff(MenuElement menuElem)
	{
		ArrayList<String> children = new ArrayList<String>();
		if (menuElem.children != null)
		{
			for (MenuElement childElem: menuElem.children)
			{
				if (childElem.children != null)
				{
					createMenuDiff(childElem);
					children.add(subMenu + childElem.menu);
				}
				else
					children.add(childElem.menu);
			}
		}
		createDiff(menuElem);
	}
	private void apply()
	{
		updateMenuElement((MenuElement) menu.getSelectedItem());
		for (MenuElement menuElem: menus)
			createMenuDiff(menuElem);
		applyDiff();
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
	}
	private JPanel createMenuPanel()
	{
		menuModel = new DefaultComboBoxModel();
		menu = new JComboBox(menuModel);
		itemModel = new DefaultListModel();
		items = new JList(itemModel);
		items.setDragEnabled(true);
		items.setDropMode(DropMode.INSERT);
		items.setTransferHandler(new ListTransferHandler());
		JPanel p = new JPanel(new BorderLayout());
		JPanel menuPanel = new JPanel(new BorderLayout());
		menuPanel.add(new JLabel(getProp("menu-editor.menu")),
			BorderLayout.WEST);
		JPanel menuComboPanel = new JPanel(new BorderLayout());
		menuComboPanel.add(menu, BorderLayout.WEST);
		menuPanel.add(menuComboPanel, BorderLayout.CENTER);
		JPanel itemPanel = new JPanel(new BorderLayout());
		itemPanel.add(new JLabel(getProp("menu-editor.items")),
			BorderLayout.NORTH);
		itemPanel.add(new JScrollPane(items), BorderLayout.CENTER);
		JPanel movePanel = new JPanel(new GridLayout(0, 1));
		movePanel.setLayout(new BoxLayout(movePanel, BoxLayout.Y_AXIS));
		itemPanel.add(movePanel, BorderLayout.EAST);
		add = new RolloverButton(GUIUtilities.loadIcon("ArrowL.png"));
		remove = new RolloverButton(GUIUtilities.loadIcon("ArrowR.png"));
		up = new RolloverButton(GUIUtilities.loadIcon("ArrowU.png"));
		down = new RolloverButton(GUIUtilities.loadIcon("ArrowD.png"));
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				addSelected();
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				removeSelected();
			}
		});
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				moveSelected(true);
			}
		});
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				moveSelected(false);
			}
		});
		movePanel.add(add);
		movePanel.add(remove);
		movePanel.add(up);
		movePanel.add(down);
		p.add(menuPanel, BorderLayout.NORTH);
		p.add(itemPanel, BorderLayout.CENTER);
		menu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e)
			{
				switch (e.getStateChange())
				{
				case ItemEvent.SELECTED:
					updateItems((MenuElement)e.getItem());
					break;
				case ItemEvent.DESELECTED:
					updateMenuElement((MenuElement)e.getItem());
					break;
				}
			}
		});
		return p;
	}
	private void updateMenuElement(MenuElement menuElem)
	{
		if (! itemListInitialized)
			return;
		menuElem.removeAllChildren();
		for (int i = 0; i < itemModel.size(); i++)
			menuElem.addChild((MenuElement) itemModel.elementAt(i));
	}
	private void updateItems(MenuElement menuElem)
	{
		itemModel.clear();
		for (MenuElement child: menuElem.children)
			itemModel.addElement(child);
	}
	private static String [] getMenus()
	{
		String s = jEdit.getProperty(viewMenuBar);
		return s.split(spaceSeparator);
	}
	private static String [] getMenuItems(String menu)
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
			MenuElement child = parent.addChild(item);
			if (item.startsWith(subMenu))
				initMenu(child);
		}
	}
	private void initMenuData()
	{
		menus.clear();
		String [] menuIds = getMenus();
		for (String menuId: menuIds)
		{
			MenuElement menuElem = new MenuElement(menuId);
			menus.add(menuElem);
			initMenu(menuElem);
		}
		menuModel.removeAllElements();
		for (MenuElement menuElem: menus)
			menuModel.addElement(menuElem);
		updateItems(menus.get(0));
	}
	private void initAllActions()
	{
		actionSetMap.clear();
		ActionSet [] actionSets = jEdit.getActionSets();
		for (ActionSet set: actionSets)
		{
			ArrayList<MenuElement> children = new ArrayList<MenuElement>();
			EditAction [] actions = set.getActions();
			for (EditAction action: actions)
			{
				String name = action.getName();
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
				actionSetMap.put(actionSetName, children);
			}
		}
		ArrayList<String> actionSetNames = new ArrayList<String>();
		for (String actionSet: actionSetMap.keySet())
			actionSetNames.add(actionSet);
		Collections.sort(actionSetNames);
		actionSetModel.removeAllElements();
		for (String actionSet: actionSetNames)
			actionSetModel.addElement(actionSet);
		updateActions((String)actionSetModel.getElementAt(0));
	}

	private static class MenuElement implements Serializable
	{
		String menu, label;
		ArrayList<MenuElement> children;
		public MenuElement(String menu)
		{
			this.menu = menu;
			String prop = menu.startsWith(subMenu) ? menu.substring(1) : menu;
			label = getLabel(prop);
		}
		public String toString()
		{
			return label;
		}
		public MenuElement addChild(String child)
		{
			MenuElement childElem = new MenuElement(child);
			addChild(childElem);
			return childElem;
		}
		public int addChild(MenuElement child)
		{
			if (children == null)
				children = new ArrayList<MenuElement>();
			children.add(child);
			return children.size() - 1;
		}
		public void removeAllChildren()
		{
			children = null;
		}
		public String [] getChildren()
		{
			if (children == null)
				return null;
			String [] copy = new String[children.size()];
			for (int i = 0; i < children.size(); i++)
				copy[i] = children.get(i).menu;
			return copy;
		}
	}
	public class ListTransferHandler extends TransferHandler
	{
		public class MenuElementTransferable implements Transferable
		{
			ArrayList<MenuElement> elements = new ArrayList<MenuElement>();
			public DataFlavor[] getTransferDataFlavors()
			{
				return new DataFlavor[]{ flavor };
			}
			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return (ListTransferHandler.this.flavor == flavor);
			}
			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException
			{
				return elements;
			}
			public void add(Object elem)
			{
				elements.add((MenuElement) elem);
			}
		}

		DataFlavor flavor = new DataFlavor(this.getClass(), "MenuElementFlavor");  
		int [] indices;

		@Override
		public boolean importData(TransferSupport support)
		{
			if (! support.isDrop())
				return false;
			JList.DropLocation dl =
				(JList.DropLocation) support.getDropLocation();
			int index = dl.getIndex();
			Object data;
			try
			{
				data = support.getTransferable().getTransferData(flavor);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			ArrayList<MenuElement> elements = (ArrayList<MenuElement>) data;
			for (MenuElement element: elements)
				itemModel.insertElementAt(element, index++);
			if (indices != null)
			{
				for (int i = 0; i < indices.length; i++)
					if (indices[i] >= index)
						indices[i] += elements.size();
			}
			return true;
		}

		@Override
		public boolean canImport(TransferSupport support)
		{
			return (support.getComponent() == items) &&
				(support.isDataFlavorSupported(flavor));
		}

		@Override
		public int getSourceActions(JComponent c)
		{
			return (c == items) ? TransferHandler.MOVE : TransferHandler.COPY;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			MenuElementTransferable t = new MenuElementTransferable();
			JList l = (JList) c;
			indices = l.getSelectedIndices();
			for (int index: indices)
				t.add(l.getModel().getElementAt(index));
			return t;
		}

		@Override
		protected void exportDone(JComponent source, Transferable data,
				int action)
		{
			if (source != items)
				return;
			Arrays.sort(indices);
			for (int i = indices.length - 1; i >= 0; i--)
				itemModel.remove(indices[i]);
		}
	}
}
