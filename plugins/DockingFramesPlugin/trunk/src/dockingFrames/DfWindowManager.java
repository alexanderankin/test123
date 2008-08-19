package dockingFrames;

import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.gjt.sp.jedit.PerspectiveManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.View.ViewConfig;
import org.gjt.sp.jedit.gui.DockableWindowFactory;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.DefaultEclipseThemeConnector;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.dockable.DefaultDockableFactory;
import bibliothek.gui.dock.facile.action.CloseAction;
import bibliothek.gui.dock.layout.PredefinedDockSituation;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

@SuppressWarnings("serial")
public class DfWindowManager extends DockableWindowManager {

	public static enum Side {
		LEFT, RIGHT, TOP, BOTTOM
	}
	
	private static final String CENTER = "center";
	private static final String MAIN = "main";

	private DockController controller;
	private SplitDockStation center;
	private Map<String, DockStation> stations;
	private Factory factory;
	private Dockable mainPanel;
	private PredefinedDockSituation situation;
	private String theme;
	private Map<String, Dockable> created = new HashMap<String, Dockable>();
	private Map<String, Side> sides = new HashMap<String, Side>();
	private CloseAction closeAction;
	
	public DfWindowManager(View view, DockableWindowFactory instance,
			ViewConfig config) {
		super(view, instance, config);
		setLayout(new BorderLayout());
		situation = new PredefinedDockSituation();
        stations = new HashMap<String, DockStation>();
		controller = new DockController();
		setTheme(DfOptionPane.getThemeName());
		closeAction = new CloseAction(controller);
        center = new SplitDockStation();
        stations.put(CENTER, center);
        add(center.getComponent(), BorderLayout.CENTER);
        controller.add(center);
        controller.getProperties().set(EclipseTheme.THEME_CONNECTOR,
        	new DefaultEclipseThemeConnector() {
                @Override
                public TitleBar getTitleBarKind(Dockable dockable) {
                    if (dockable == mainPanel)
                        return TitleBar.NONE_BORDERED;
                    return super.getTitleBarKind(dockable);
                }
            });
		situation.put(CENTER, center);
        factory = new Factory();
        situation.add(factory);
        PerspectiveManager.setPerspectiveDirty(true);

        sides.put(TOP, Side.TOP);
        sides.put(BOTTOM, Side.BOTTOM);
        sides.put(RIGHT, Side.RIGHT);
        sides.put(LEFT, Side.LEFT);
	}

	private void setTheme(String name) {
        ThemeFactory[] themes = DockUI.getDefaultDockUI().getThemes();
        for (ThemeFactory t: themes) {
        	if (t.getName().equals(name)) {
        		controller.setTheme(t.create());
        		theme = name;
        		break;
        	}
        }
	}
	public PredefinedDockSituation getDockSituation() {
		return situation;
	}
	
	public Factory getDockFactory() {
		return factory;
	}
	
	@Override
	public void applyDockingLayout(DockingLayout docking) {
		if (docking != null) {
			DfDockingLayout layout = (DfDockingLayout) docking;
			String filename = layout.getPersistenceFilename();
			if (filename != null) {
				XElement root = null;
				try {
					root = XIO.readUTF(new FileInputStream(filename));
					situation.readXML(root);
					return;
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		}		
		super.applyDockingLayout(docking);
	}

	public Map<String, DockStation> getStationMap() {
		return stations;
	}
	
	@Override
	protected void dockingPositionChanged(String dockableName,
			String oldPosition, String newPosition) {
		showDockableWindow(dockableName);
	}

	@Override
	public void closeCurrentArea() {
		// TODO Auto-generated method stub

	}

	@Override
	public JComponent floatDockableWindow(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingArea getBottomDockingArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingLayout getDockingLayout(ViewConfig config) {
		DfDockingLayout layout = new DfDockingLayout(this);
		return layout;
	}

	@Override
	public DockingArea getLeftDockingArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingArea getRightDockingArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DockingArea getTopDockingArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hideDockableWindow(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDockableWindowDocked(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDockableWindowVisible(String name) {
		JComponent c = getDockable(name);
		if (c == null)
			return false;
		return c.isVisible();
	}

	@Override
	protected void propertiesChanged() {
		super.propertiesChanged();
		String selectedTheme = DfOptionPane.getThemeName();
		if (! selectedTheme.equals(theme))
			setTheme(selectedTheme);
	}

	@Override
	public void setMainPanel(JPanel panel) {
		mainPanel = new MainDockable(panel);
		center.drop(mainPanel);
		situation.put(MAIN, mainPanel);
	}

	@Override
	public void showDockableWindow(String name) {
		Dockable d = created.get(name);
		if (d != null) {
			if (d.getController() != null) {
				DockStation station = d.getDockParent();
				if (station != null)
					station.setFrontDockable(d);
				else
					d.getComponent().setVisible(true);
				focusDockable(name);
				return;
			} else {
				// Windows has been closed
				created.remove(d);
			}
		}
		d = createDefaultDockable(name);
		if (d == null)
			return;
		String position = getDockablePosition(name); 
		drop(d, sides.get(position));
		focusDockable(name);
	}

    private void drop(Dockable dockable, Side side) {
        Leaf leaf = find(center.getRoot(), side);
       
        if( leaf != null ){
            if( !aside( leaf, side ))
                leaf = null;
        }
       
        if( leaf == null ){
            switch( side ){
                case BOTTOM:
                    drop( dockable, SplitDockProperty.SOUTH );
                    break;
                case TOP:
                    drop( dockable, SplitDockProperty.NORTH );
                    break;
                case LEFT:
                    drop( dockable, SplitDockProperty.WEST );
                    break;
                case RIGHT:
                    drop( dockable, SplitDockProperty.EAST );
                    break;
            }
        }
        else{
            DockStation stack = leaf.getDockable().asDockStation();
            if( stack == null ){
                center.drop( dockable, center.getDockableProperty( leaf.getDockable() ) );
            }
            else{
                stack.drop( dockable );
            }
        }
    }
   
    private void drop(Dockable dockable, SplitDockProperty property) {
        if(! center.drop(dockable, property))
            center.drop(dockable);
    }
 
    // tells whether the leaf is aside the center dockable on the given side
    private boolean aside( Leaf leaf, Side side ){
        Leaf centerLeaf = center.getRoot().getLeaf( mainPanel );
       
        switch( side ){
            case TOP:
                return leaf.getY() + leaf.getHeight() <= centerLeaf.getY();
            case BOTTOM:
                return leaf.getY() >= centerLeaf.getY() + centerLeaf.getHeight();
            case LEFT:
                return leaf.getX() + leaf.getWidth() <= centerLeaf.getX();
            case RIGHT:
                return leaf.getX() >= centerLeaf.getX() + centerLeaf.getHeight();
        }
        return false;
    }
   
    private Leaf find(SplitNode parent, Side side) {
        if( parent instanceof Leaf ){
            Leaf leaf = (Leaf)parent;
            if( leaf.getDockable() != center ){
                return leaf;
            }
        }
        if( parent instanceof Node ){
            Node node = (Node)parent;
           
            Leaf left = find( node.getLeft(), side );
            Leaf right = find( node.getRight(), side );
           
            if( left == null )
                return right;
            if( right == null )
                return left;
           
            switch( side ){
                case LEFT:
                    if( left.getX() + left.getWidth()/2 < right.getX() + right.getWidth()/2 )
                        return left;
                    else
                        return right;
                case RIGHT:
                    if( left.getX() + left.getWidth()/2 > right.getX() + right.getWidth()/2 )
                        return left;
                    else
                        return right;
                case TOP:
                    if( left.getY() + left.getHeight()/2 < right.getY() + right.getHeight()/2 )
                        return left;
                    else
                        return right;
                case BOTTOM:
                    if( left.getY() + left.getHeight()/2 > right.getY() + right.getHeight()/2 )
                        return left;
                    else
                        return right;
            }
        }
        if( parent instanceof Root ){
            return find( ((Root)parent).getChild(), side );
        }
        return null;
    }
	private JEditDockable createDefaultDockable(String name) {
		JComponent window = getDockable(name);
		if (window == null)
			window = createDockable(name);
		if (window == null)
			return null;
		String title = getDockableTitle(name);
		JEditDockable d = new JEditDockable(name, title, window);
		created.put(name, d);
		DefaultDockActionSource source = new DefaultDockActionSource();
		source.setHint(new LocationHint(LocationHint.DOCKABLE, LocationHint.RIGHT_OF_ALL));
		d.setActionOffers(source);
		source.add(closeAction);
		return d;
	}

	private static class MainDockable extends DefaultDockable {
		public MainDockable(JPanel panel) {
			super(panel, (String)null);
		}

		@Override
		public DockTitle getDockTitle(DockTitleVersion version) {
			return null;
		}
	}
	private static class JEditDockable extends DefaultDockable {
		String name;
		public JEditDockable(String name, String title, JComponent window) {
			super(window, title);
			this.name = name;
			setFactoryID("jEdit");
		}
		public String getName() {
			return name;
		}
	}
	private class Factory extends DefaultDockableFactory {

		@Override
		public String getID() {
			return "jEdit";
		}

		@Override
		public Object getLayout(DefaultDockable element,
				Map<Dockable, Integer> children)
		{
			if (element instanceof JEditDockable) {
				JEditDockable d = (JEditDockable) element;
				return d.getName();
			}
			return super.getLayout(element, children);
		}

		@Override
		public DefaultDockable layout(Object layout, Map<Integer, Dockable> children)
		{
			String name = (String) layout;
			return createDefaultDockable(name);
		}

		@Override
		public DefaultDockable layout(Object layout) {
			return layout(layout, null);
		}

		@Override
		public Object read(XElement element) {
			XElement child = element.getElement("Dockable");
			if (child == null)
				return super.read(element);
			return child.getString("name");
		}

		@Override
		public void write(Object layout, XElement element) {
			XElement child = new XElement("Dockable");
			child.addString("name", (String)layout);
			element.addElement(child);
		}
		
	}
}
