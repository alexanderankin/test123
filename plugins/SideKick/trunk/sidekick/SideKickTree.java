/*
 * SideKickTree.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package sidekick;

//{{{ Imports
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
//}}}

/**
 * The Structure Browser dockable.  One instance is created for each View.
 */
public class SideKickTree extends JPanel implements EBComponent,
DefaultFocusComponent
{
        //{{{ SideKickTree constructor
        public SideKickTree(View view, boolean docked)
        {
                super(new BorderLayout());

                this.view = view;

                // create toolbar with parse button
                JToolBar buttonBox = new JToolBar();
                buttonBox.setFloatable(false);

                parseBtn = new RolloverButton(GUIUtilities.loadIcon("Parse.png"));
                parseBtn.setToolTipText(jEdit.getProperty("sidekick-tree.parse"));
                parseBtn.setMargin(new Insets(0,0,0,0));
                parseBtn.setRequestFocusEnabled(false);
                parseBtn.addActionListener(buildActionListener());
                buttonBox.add(parseBtn);
                
                
                String[] serviceNames = ServiceManager.getServiceNames(SideKickParser.SERVICE);
                parserCombo = new JComboBox(serviceNames);
                SideKickParser currentParser = SideKickPlugin.getParserForBuffer(view.getBuffer());
                if (currentParser != null) {
                	parserCombo.setSelectedItem(currentParser.getName());
                }
                buttonBox.add(parserCombo);
                parserCombo.addActionListener(buildActionListener());
                
                
                buttonBox.add(Box.createGlue());

                add(BorderLayout.NORTH,buttonBox);

                // create a faux model that will do until a real one arrives
                DefaultTreeModel emptyModel = new DefaultTreeModel(
                        new DefaultMutableTreeNode(null));
                tree = buildTree(emptyModel);
                tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                tree.addKeyListener(new KeyHandler());
                if(docked)
                        tree.addMouseMotionListener(new MouseHandler());

                // looks bad with the OS X L&F, apparently...
                if(!OperatingSystem.isMacOSLF())
                        tree.putClientProperty("JTree.lineStyle", "Angled");

                tree.setVisibleRowCount(10);
                tree.setCellRenderer(new Renderer());

                add(BorderLayout.CENTER,new JScrollPane(tree));

                propertiesChanged();

                CaretHandler caretListener = new CaretHandler();

                EditPane[] editPanes = view.getEditPanes();
                for(int i = 0; i < editPanes.length; i++)
                {
                        editPanes[i].getTextArea().addCaretListener(
                                caretListener);
                }

                update();
        } //}}}

        //{{{ focusOnDefaultComponent() method
        public void focusOnDefaultComponent()
        {
                tree.requestFocus();
        } //}}}

        //{{{ addNotify() method
        public void addNotify()
        {
                super.addNotify();
                EditBus.addToBus(this);
        } //}}}

        //{{{ removeNotify() method
        public void removeNotify()
        {
                super.removeNotify();
                EditBus.removeFromBus(this);
        } //}}}

        //{{{ handleMessage() method
        public void handleMessage(EBMessage msg)
        {
        	
        	
                if(msg instanceof EditPaneUpdate)
                {
                        EditPaneUpdate epu = (EditPaneUpdate)msg;
                        EditPane editPane = epu.getEditPane();

                        if(epu.getWhat() == EditPaneUpdate.CREATED)
                                editPane.getTextArea().addCaretListener(new CaretHandler());
                }
                else if(msg instanceof PropertiesChanged)
                        propertiesChanged();
                else if(msg instanceof SideKickUpdate)
                {
                        if(((SideKickUpdate)msg).getView() == view)
                                update();
                }
        } //}}}

        //{{{ update() method
        protected void update()
        {
        	SideKickParser parser =  SideKickPlugin.getParserForBuffer(view.getBuffer());
        	if (parser != null) parserCombo.setSelectedItem(parser.getName());
                data = SideKickParsedData.getParsedData(view);
                if(SideKickPlugin.getParserForBuffer(view.getBuffer()) == null
                        || data == null)
                {
                        DefaultMutableTreeNode root = new DefaultMutableTreeNode(view.getBuffer().getName());
                        root.insert(new DefaultMutableTreeNode(
                                jEdit.getProperty("sidekick-tree.not-parsed")),0);

                        tree.setModel(new DefaultTreeModel(root));
                }
                else
                {
                        tree.setModel(data.tree);
                        if(treeFollowsCaret)
                                expandTreeAt(view.getTextArea().getCaretPosition());
                }
		
		if (autoExpandTree == -1)
			expandAll(true);
		else if (autoExpandTree == 0)
			expandAll(false);
		else if (autoExpandTree > 0) {
			tree.expandRow( 0 );
			for (int i = 1; i < autoExpandTree; i++) { 
				for ( int j = tree.getRowCount() - 1; j > 0; j-- )
				    tree.expandRow( j );
			}
		}
        } //}}}
	
        //{{{ expandAll() method
        /**
         * Expand or collapse all nodes in a tree.
         * @param tree the tree
         * @param expand if true, expand all nodes, if false, collapse all nodes
         */
        public void expandAll( boolean expand ) {
		TreeNode root = ( TreeNode ) tree.getModel().getRoot();
		expandAll( new TreePath( root ), expand );
        }//}}}
	
        //{{{ expandAll() method
	// recursive method to traverse children
        private void expandAll( TreePath parent, boolean expand ) {
		TreeNode node = ( TreeNode ) parent.getLastPathComponent();
		if ( node.getChildCount() >= 0 ) {
		    for ( Enumeration e = node.children(); e.hasMoreElements(); ) {
			TreeNode n = ( TreeNode ) e.nextElement();
			TreePath path = parent.pathByAddingChild( n );
			expandAll( path, expand );
		    }
		}
		
		// expansion or collapse must be done from the bottom up
		if ( expand ) {
		    tree.expandPath( parent );
		}
		else {
		    tree.collapsePath( parent );
		}
        }//}}}


        //{{{ buildTree() method
        protected JTree buildTree(DefaultTreeModel model)
        {
                return new CustomTree(model);
        }//}}}
        
        //{{{ buildActionListener() method
        /**
         * Creates an action listener for the parse button.
         */
        protected ActionListener buildActionListener()
        {
                return new ActionHandler();
        }//}}}
        
        //{{{ Private members

        //{{{ Instance variables
        private RolloverButton parseBtn;
        private JComboBox parserCombo;
        protected JTree tree;

        protected boolean treeFollowsCaret;

        protected View view;
        private Timer caretTimer;

        protected SideKickParsedData data;
	
	private int autoExpandTree = 0;
        //}}}

        //{{{ propertiesChanged() method
        private void propertiesChanged()
        {
                treeFollowsCaret = jEdit.getBooleanProperty("sidekick-tree.follows-caret");
		autoExpandTree = jEdit.getIntegerProperty("sidekick-tree.auto-expand-tree-depth", 1);
        } //}}}

        //{{{ expandTreeWithDelay() method
        /**
         * Expands the tree after a delay.  
         * The delay timer is restarted each time this method is called.
         */
        protected void expandTreeWithDelay()
        {
                // if keystroke parse timer is running, do nothing
                // if(keystrokeTimer != null && keystrokeTimer.isRunning())
                        // return;

                if(caretTimer != null)
                        caretTimer.stop();

                caretTimer = new Timer(0,new ActionListener()
                {
                        public void actionPerformed(ActionEvent evt)
                        {
                                JEditTextArea textArea = view.getTextArea();
                                int caret = textArea.getCaretPosition();
                                Selection s = textArea.getSelectionAtOffset(caret);
                                expandTreeAt(s == null ? caret : s.getStart());
                        }
                });

                caretTimer.setInitialDelay(500);
                caretTimer.setRepeats(false);
                caretTimer.start();
        } //}}}

        //{{{ expandTreeAt() method
        protected void expandTreeAt(int dot)
        {
                if(data == null)
                        return;

                TreePath treePath = data.getTreePathForPosition(dot);
                if(treePath != null)
                {
                        tree.expandPath(treePath);
                        tree.setSelectionPath(treePath);
                        tree.scrollPathToVisible(treePath);
                }
        } //}}}

        //}}}

        //{{{ Inner classes

        //{{{ CustomTree class
        /**
         * A JTree with added mouse handling.  Other plugins providing similar trees 
         * can extend CustomTree and override the mouse methods.
         */
        protected class CustomTree extends JTree
        {
                protected CustomTree(TreeModel model)
                {
                        super(model);
                }

                protected void processMouseEvent(MouseEvent evt)
                {
                        switch(evt.getID())
                        {
                        //{{{ MOUSE_PRESSED...
                        case MouseEvent.MOUSE_PRESSED:
                                TreePath path = getPathForLocation(
                                        evt.getX(),evt.getY());
                                if(path != null)
                                {
                                        Object value = ((DefaultMutableTreeNode)path
                                                .getLastPathComponent()).getUserObject();

                                        if(value instanceof IAsset)
                                        {
                                                IAsset asset = (IAsset)value;

                                                JEditTextArea textArea = view.getTextArea();

                                                if(evt.getClickCount() == 2)
                                                {
                                                        doubleClicked(view,asset,path);
                                                }
                                                else if(evt.isShiftDown())
                                                {
                                                        shiftClick(view,asset,path);
                                                }
                                                else if(evt.isControlDown())
                                                {
                                                        controlClick(view,asset,path);
                                                }
                                                else
                                                        textArea.setCaretPosition(asset.getStart().getOffset());
                                        }
                                }

                                super.processMouseEvent(evt);
                                break; //}}}
                        //{{{ MOUSE_EXITED...
                        case MouseEvent.MOUSE_EXITED:
                                view.getStatus().setMessage(null);
                                super.processMouseEvent(evt);
                                break; //}}}
                        default:
                                super.processMouseEvent(evt);
                                break;
                        }
                }
                
                protected void doubleClicked(View view, IAsset asset, TreePath path)
                {
                }
                
                protected void shiftClick(View view, IAsset asset, TreePath path)
                {
                        JEditTextArea textArea = view.getTextArea();
                        textArea.setCaretPosition(asset.getEnd().getOffset());
                        textArea.addToSelection(
                                new Selection.Range(
                                        asset.getStart().getOffset(),
                                        asset.getEnd().getOffset()));
                }
                
                protected void controlClick(View view, IAsset asset, TreePath path)
                {
                        JEditTextArea textArea = view.getTextArea();
                        textArea.getDisplayManager().narrow(
                                textArea.getLineOfOffset(asset.getStart().getOffset()),
                                textArea.getLineOfOffset(asset.getEnd().getOffset()));
                }
        } //}}}

        //{{{ ActionHandler class
        class ActionHandler implements ActionListener
        {
                public void actionPerformed(ActionEvent evt)
                {
                	String parserName = parserCombo.getSelectedItem().toString();
                	SideKickPlugin.setParserForBuffer(view.getBuffer(), parserName);;
                        SideKickPlugin.parse(view,true);
                }
        } //}}}

        //{{{ CaretHandler class
        class CaretHandler implements CaretListener
        {
                public void caretUpdate(CaretEvent evt)
                {
                        if(evt.getSource() == view.getTextArea() && treeFollowsCaret) {
                                expandTreeWithDelay();
			}
                }
        } //}}}

        //{{{ KeyHandler class
        class KeyHandler extends KeyAdapter
        {
                public void keyPressed(KeyEvent evt)
                {
                        if(caretTimer != null)
                                caretTimer.stop();

                        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
                        {
                                evt.consume();

                                TreePath path = tree.getSelectionPath();

                                if(path != null)
                                {
                                        Object value = ((DefaultMutableTreeNode)path
                                                .getLastPathComponent()).getUserObject();

                                        if(value instanceof IAsset)
                                        {
                                                IAsset asset = (IAsset)value;

                                                JEditTextArea textArea = view.getTextArea();

                                                if(evt.isShiftDown())
                                                {
                                                        textArea.setCaretPosition(asset.getEnd().getOffset());
                                                        textArea.addToSelection(
                                                                new Selection.Range(
                                                                        asset.getStart().getOffset(),
                                                                        asset.getEnd().getOffset()));
                                                }
                                                else
                                                        textArea.setCaretPosition(asset.getStart().getOffset());
                                        }
                                }
                        }
                }
        } //}}}

        //{{{ MouseHandler class
        class MouseHandler extends MouseMotionAdapter
        {
                public void mouseMoved(MouseEvent evt)
                {
                        TreePath path = tree.getPathForLocation(
                                evt.getX(),evt.getY());
                        if(path == null)
                                view.getStatus().setMessage(null);
                        else
                        {
                                Object value = ((DefaultMutableTreeNode)path
                                        .getLastPathComponent()).getUserObject();

                                if(value instanceof IAsset)
                                {
                                	String info = ((IAsset)value).getShortString();
                                        view.getStatus().setMessage(info);
                                }
                        }
                }
        } //}}}

        //{{{ Renderer class
        class Renderer extends DefaultTreeCellRenderer
        {
                public Component getTreeCellRendererComponent(JTree tree,
                        Object value, boolean sel, boolean expanded,
                        boolean leaf, int row, boolean hasFocus)
                {
                        super.getTreeCellRendererComponent(tree,value,sel,
                                expanded,leaf,row,hasFocus);

                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
                        Object nodeValue = node.getUserObject();
                        if(nodeValue instanceof IAsset)
                        {
                                IAsset asset = (IAsset)node.getUserObject();

                                setText(asset.getShortString());
                                setIcon(asset.getIcon());
                        }
                        // is root?
                        else if(node.getParent() == null)
                        {
                                setIcon(org.gjt.sp.jedit.browser.FileCellRenderer
                                        .fileIcon);
                        }
                        else
                                setIcon(null);

                        return this;
                }
        } //}}}

        //}}}
}
