package outline;

import java.awt.event.*;
import javax.swing.JTree;
import javax.swing.tree.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.Log;

import sidekick.Asset;
import sidekick.SideKickTree;

/**
 *  The dockable outline tree. It's 99% duplicated code from SideKickTree.
 *  Something should really be done about that.
 *
 * @author     Brad Mace
 * @version    $Revision: 1.10 $ $Date: 2006/07/10 20:07:26 $
 * @see sidekick.SideKickTree
 * @deprecated - this class is not used anymore
 */
public class OutlineTree extends SideKickTree implements EBComponent {
	private OutlineParser parser = null;
	
	public OutlineTree(View view, boolean docked) {
		super(view,docked);
		//remove(0);
		//getComponentAt(1,1).setVisible(false);
	}

	//{{{ update() method
	protected void update() {
		if (parser == null)
			parser = new OutlineParser();
		
		data = parser.parse(view.getBuffer(), OutlinePlugin.errorSource);
		tree.setModel(data.tree);
		repaint();
		//if (treeFollowsCaret)
			expandTreeAt(view.getTextArea().getCaretPosition());
	}//}}}

	protected JTree buildTree(DefaultTreeModel model) {
		return new CustomOutlineTree(model);
	}
	
	protected ActionListener buildActionListener() {
		return new OutlineActionHandler();
	}
	
	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof BufferUpdate)
		{
			update();
		}
		
		if (msg instanceof EditPaneUpdate)
		{
			update();
		}

	}
	
	//{{{ handleMessage() method
	public void handleMessage2(EBMessage msg) {
		Buffer buffer = view.getBuffer();
		//{{{ BufferUpdate
		if (msg instanceof BufferChanging)
		{
			BufferChanging bc = (BufferChanging) msg;
			Log.log(Log.DEBUG, this, "Parsing "+buffer);
			update();
		}
		
		if (msg instanceof BufferUpdate) {
			BufferUpdate bmsg = (BufferUpdate) msg;
			if (bmsg.getBuffer() != buffer)
				return;

			if (bmsg.getWhat() == BufferUpdate.SAVED) {
				if (buffer.getBooleanProperty("sidekick.buffer-change-parse") || buffer.getBooleanProperty("sidekick.keystroke-parse"))
				{
					update();
				}
			}
			else if (bmsg.getWhat() == BufferUpdate.PROPERTIES_CHANGED) {
				if (buffer.getBooleanProperty("sidekick.buffer-change-parse") || buffer.getBooleanProperty("sidekick.keystroke-parse"))
					update();
			}
		}//}}}
		
		//{{{ EditPaneUpdate
		if (msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate epu = (EditPaneUpdate) msg;
			EditPane editPane = epu.getEditPane();
			if (editPane.getView() != view)
				return;

			if (epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
			{
				// check if this is the currently focused edit pane
				if (editPane == view.getEditPane())
				{
					if (buffer.getBooleanProperty("sidekick.buffer-change-parse") 
						|| buffer.getBooleanProperty("sidekick.keystroke-parse")) {
						update();
					}
				}
			}
		}//}}}
		
	}//}}}

	//{{{ addNotify() method
	public void addNotify() {
		super.addNotify();
		EditBus.addToBus(this);
	}//}}}

	//{{{ removeNotify() method
	public void removeNotify() {
		super.removeNotify();
		EditBus.removeFromBus(this);
	}//}}}

	//+{{{ Inner classes

	//-{{{ CustomOutlineTree class
	/**
	 *  Description of the Class
	 *
	 * @author     Brad Mace
	 * @version    $Revision: 1.10 $ $Date: 2006/07/10 20:07:26 $
	 */
	class CustomOutlineTree extends CustomTree {
		CustomOutlineTree(TreeModel model) {
			super(model);
		}
		
		protected void doubleClicked(View view, Asset asset, TreePath path) {
			JEditTextArea textArea = view.getTextArea();
			int startLine = textArea.getLineOfOffset(asset.getStart().getOffset());
			DisplayManager mgr = textArea.getDisplayManager();
			if (mgr.isLineVisible(startLine + 1)) {
				mgr.collapseFold(startLine);
				collapsePath(path);
			} else {
				mgr.expandFold(startLine, false);
				expandPath(path);
			}
			return;
		}
		
	}//}}}

	//{{{ ActionHandler class
	/**
	 *  Description of the Class
	 *
	 * @author     Brad Mace
	 * @version    $Revision: 1.10 $ $Date: 2006/07/10 20:07:26 $
	 */
	class OutlineActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			update();
		}
	}//}}}

	//}}}

}

