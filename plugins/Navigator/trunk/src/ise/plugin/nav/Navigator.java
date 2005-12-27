package ise.plugin.nav;

import java.awt.event.*;
import java.util.Stack;

import javax.swing.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;


/**
 * NavigatorPlugin for keeping track of where we were and where.
 * @author Dale Anson
 * @author Alan Ezust
 * 
 * @version $Id$
 */
public class Navigator implements ActionListener 
{
	   private class DontBother extends Exception {}

	   /** Action command to go to the previous item. */
	   public final static String BACK = "back";
	   /** Action command to go to the next item. */
	   public final static String FORWARD = "forward";
	   /** Action command to indicate that it is okay to go back. */
	   public final static String CAN_GO_BACK = "canGoBack";
	   /** Action command to indicate that it is not okay to go back. */
	   public final static String CANNOT_GO_BACK = "cannotGoBack";
	   /** Action command to indicate that it is okay to go forward. */
	   public final static String CAN_GO_FORWARD = "canGoForward";
	   /** Action command to indicate that it is not okay to go forward. */
	   public final static String CANNOT_GO_FORWARD = "cannotGoForward";

	
	private Stack backStack;

	private Stack forwardStack;

	private DefaultButtonModel backButtonModel;
	
	private DefaultButtonModel forwardButtonModel;

	private Object currentNode = null;

	private int maxStackSize = 512;

	private View view;

	private boolean ignoreOpen;


	public Navigator(View view)
	{
		this(view, "");
	}
	
	/**
	 * Constructor for Navigator
	 * 
	 * @param vw
	 * @param position
	 */
	public Navigator(View vw, String position)
	{
		this.view = vw;

		backButtonModel = new DefaultButtonModel();
		forwardButtonModel = new DefaultButtonModel();
		ignoreOpen= false;
		
		backButtonModel.setActionCommand( Navigator.BACK );
		forwardButtonModel.setActionCommand( Navigator.FORWARD );
		backButtonModel.addActionListener( this );
		forwardButtonModel.addActionListener( this );
		
		// set up the history stacks
		backStack = new Stack();
		forwardStack = new Stack();
		clearStacks();
		update();

		// create a Nav and make sure the plugin knows about it
		// NavigatorPlugin.addNavigator( view, this );

		// add a mouse listener to the view. Each mouse click on a text
		// area in
		// the view is stored
		view.getTextArea().getPainter().addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent ce)
			{
				update();

				// setPosition(new NavPosition(b, cp));
			}
		});
	      
	}

	public ButtonModel getBackModel() {
		return backButtonModel;
	}
	
	NavPosition currentPosition() throws DontBother {
		Buffer b = view.getBuffer();
		
		int cp = view.getTextArea().getCaretPosition();
		if ((cp == 0) && b.getName().startsWith("Untitled"))
			throw new DontBother();
		NavPosition retval = new NavPosition(b, cp);
		return retval;
	}
	
	/** push current position onto the forward stack */
	public void pushForward() 
	{
		try {
			forwardStack.push(currentPosition());
		} catch (DontBother e) {}
	}
	
	public void update() {
		if (ignoreOpen) {
			return;
		}
		try {
			update(currentPosition());
		} 
		catch (DontBother db) {}
	}
	
	public ButtonModel getForwardModel() {
		return forwardButtonModel;
	}
	
	/**
	 * Updates the stacks and button state based on the given node. Pushes
	 * the node on to the "back" history, clears the "forward" history.
	 * 
	 * @param node
	 *                an instance of NavPosition.
	 */
	private void update(NavPosition node)
	{
		if (node != currentNode)
		{
			if (currentNode != null)
			{
				backStack.push(currentNode);
				if (backStack.size() > maxStackSize)
					backStack.removeElementAt(0);
			}
			currentNode = node;
			forwardStack.clear();
		}
		setButtonState();
	}

	   /**
		 * The action handler for this class. Actions can be invoked by
		 * calling this method and passing an ActionEvent with one of
		 * the action commands defined in this class (BACK, FORWARD,
		 * etc).
		 * 
		 * @param ae
		 *                the action event to kick a response.
		 */
	   public void actionPerformed( ActionEvent ae ) {
	      if ( ae.getActionCommand().equals( BACK ) ) {
	         goBack();
	      }
	      else if ( ae.getActionCommand().equals( FORWARD ) ) {
	         goForward();
	      }
	      else if ( ae.getActionCommand().equals( CAN_GO_BACK ) ) {
	         backButtonModel.setEnabled( true );
	      }
	      else if ( ae.getActionCommand().equals( CANNOT_GO_BACK ) ) {
	         backButtonModel.setEnabled( false );
	      }
	      else if ( ae.getActionCommand().equals( CAN_GO_FORWARD ) ) {
	         forwardButtonModel.setEnabled( true );
	      }
	      else if ( ae.getActionCommand().equals( CANNOT_GO_FORWARD ) ) {
	         forwardButtonModel.setEnabled( false );
	      }
	   }

	
	/**
	 * Removes an invalid node from the navigation history.
	 * 
	 * @param node
	 *                an invalid node
	 */
	public void remove(Object node)
	{
		backStack.remove(node);
		forwardStack.remove(node);
	}

	public void setMaxHistorySize(int size)
	{
		maxStackSize = size;
	}

	public int getMaxHistorySize()
	{
		return maxStackSize;
	}

	public void clearStacks()
	{
		backStack.clear();
		forwardStack.clear();
		setButtonState();
	}

	/** Sets the state of the navigation buttons. */
	private void setButtonState()
	{
		backButtonModel.setEnabled(!backStack.empty());
		forwardButtonModel.setEnabled(!forwardStack.empty());
	}

	/**
	 * Sets the position of the cursor to the given NavPosition.
	 * 
	 * @param o
	 *                The new NavPosition value
	 */
	public void setPosition(Object o)
	{
		
		NavPosition np = (NavPosition) o;
		Buffer buffer = np.buffer;
		// JEditBuffer buffer = np.buffer; // for jEdit 4.3
		int caret = np.caret;

		if (buffer.equals(view.getBuffer()))
		{
			// nav in current buffer, just set cursor position
			view.getTextArea().setCaretPosition(caret, true);
			return;
		}

		// check if buffer is open
		Buffer[] buffers = jEdit.getBuffers();
		for (int i = 0; i < buffers.length; i++)
		{
			if (buffers[i].equals(buffer))
			{
				// found it
				view.goToBuffer(buffer);
				view.getTextArea().setCaretPosition(caret, true);
				return;
			}
		}

		// buffer isn't open
		String path = buffer.getPath();
		ignoreOpen = true;
		buffer = jEdit.openFile(view, path);
		ignoreOpen = false;

		if (buffer == null)
		{
			remove(np);
			return; // nowhere to go, maybe the file got deleted?
		}

		if (caret >= buffer.getLength())
		{
			caret = buffer.getLength() - 1;
		}
		if (caret < 0)
		{
			caret = 0;
		}
		try
		{
			view.getTextArea().setCaretPosition(caret, true);
		}
		catch (NullPointerException npe)
		{
			// sometimes Buffer.markTokens throws a NPE here, catch
			// it
			// and silently ignore it.
		}
		view.getTextArea().requestFocus();
	}


	   /** Moves to the previous item in the "back" history. */
	   public void goBack() {
		   if ( !backStack.empty() )  {
			   try { 
				   forwardStack.push( currentPosition() );
			   }
			   catch (DontBother db) {}
			   
			   if (forwardStack.size() > maxStackSize)
		                      forwardStack.removeElementAt(0);
			   currentNode = backStack.pop();
			         setPosition( currentNode );
		   	         setButtonState();
		   }
		   
	   }

	   /** Moves to the next item in the "forward" history. */
	   public void goForward() {
	      
	      if ( !forwardStack.empty() ) {
		      try {
			      currentNode = currentPosition();
			      backStack.push( currentNode );
		      }
		      catch (DontBother db) {}

		      if (backStack.size() > maxStackSize)
			      backStack.removeElementAt(0);
		      currentNode = forwardStack.pop();
	              setPosition ( currentNode );
		      setButtonState();
	      }
	   }
}

