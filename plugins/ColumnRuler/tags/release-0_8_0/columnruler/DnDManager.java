package columnruler;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;

/**
 * Description of the Class
 *
 * @author     mace
 * @created    June 8, 2003
 * @modified   $Date: 2003-06-09 17:54:10 $ by $Author: bemace $
 * @version    $Revision: 1.1 $
 */
public class DnDManager implements DropTargetListener, DragGestureListener {
	private ColumnRuler _ruler;

	public DnDManager(ColumnRuler r) {
		_ruler = r;
		DropTarget target = new DropTarget(_ruler, this);
		target.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);
		DragSource source = DragSource.getDefaultDragSource();
		source.createDefaultDragGestureRecognizer(_ruler, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	//{{{ DropTargetListener implementation
	private boolean isDragAcceptable(DropTargetDragEvent e) {
		return e.isDataFlavorSupported(Mark.MARK_FLAVOR);
	}

	private boolean isDropAcceptable(DropTargetDropEvent e) {
		return e.isDataFlavorSupported(Mark.MARK_FLAVOR);
	}

	/**
	 * Called when an object is drug into the ruler
	 *
	 * @param e  Description of the Parameter
	 */
	public void dragEnter(DropTargetDragEvent e) {
		_ruler.getTempMarker().setVisible(true);
		_ruler.repaint();
		if (!isDragAcceptable(e)) {
			e.rejectDrag();
		}
	}

	/**
	 * Called when an object is drug out of the ruler
	 *
	 * @param e  Description of the Parameter
	 */
	public void dragExit(DropTargetEvent e) {
		_ruler.getTempMarker().setVisible(false);
		_ruler.repaint();
	}

	/**
	 * Called when an object is over the ruler
	 *
	 * @param e  Description of the Parameter
	 */
	public void dragOver(DropTargetDragEvent e) {
		Mark temp = _ruler.getTempMarker();
		temp.setColumn(_ruler.getColumnAtPoint(e.getLocation()));
		temp.setVisible(true);
		_ruler.repaint();
	}

	public void dropActionChanged(DropTargetDragEvent e) {
		if (!isDragAcceptable(e)) {
			e.rejectDrag();
		}
	}

	/**
	 * Called when something is dropped on the ruler
	 *
	 * @param e  Description of the Parameter
	 */
	public void drop(DropTargetDropEvent e) {
		_ruler.getTempMarker().setVisible(false);
		if (!isDropAcceptable(e)) {
			e.rejectDrop();
		}
		e.acceptDrop(DnDConstants.ACTION_COPY);
		try {
			Mark m = (Mark) e.getTransferable().getTransferData(Mark.MARK_FLAVOR);
			if (m != null) {
				m.setColumn(_ruler.getColumnAtPoint(e.getLocation()));
			}
		} catch (UnsupportedFlavorException ufe) {
		} catch (IOException ioe) {}
		e.dropComplete(true);
		_ruler.repaint();
	}

	//}}}

	//{{{ DragGestureListener implementation
	public void dragGestureRecognized(DragGestureEvent e) {
		Mark m = _ruler.getMarkAtPoint(e.getDragOrigin());
		if (m != null) {
			e.startDrag(null, m);
		}
	}
	//}}}
}

