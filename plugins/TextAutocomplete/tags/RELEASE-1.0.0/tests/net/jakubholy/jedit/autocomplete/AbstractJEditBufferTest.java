/**
 * 
 */
package net.jakubholy.jedit.autocomplete;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;

/**
 * Parent of all jEdit test cases that need running jEdit and a buffer.
 * @author Jakub Holy
 */
public class AbstractJEditBufferTest extends AbstractJEditTest implements EBComponent
{
	/** Hold the last buffer update message received from the jEdit edit bus. */
	BufferUpdate[] editBus = new BufferUpdate[]{null};
	Buffer buffer = null;
	View view = null;
	
	/** Wait at max. [ms]. */
	static final int TIMEOUT = 500;

	/**
	 * @param arg0
	 */
	public AbstractJEditBufferTest(String arg0)
	{
		super(arg0);
	}

	/* (non-Javadoc)
	 * Notify when a buffer get's loaded.
	 * @see org.gjt.sp.jedit.EBComponent#handleMessage(org.gjt.sp.jedit.EBMessage)
	 */
	public void handleMessage(EBMessage message)
	{
		if (message instanceof BufferUpdate) 
		{
			final BufferUpdate bufferUpdateMsg = (BufferUpdate) message;
			
			if(bufferUpdateMsg.getWhat() == BufferUpdate.LOADED)
			{
				new Thread(new Runnable()
				{
					public void run()
					{
						synchronized (editBus)
						{
							editBus[0] = bufferUpdateMsg;
							editBus.notify();
						}
					} // run
				}, "Test-WaitUntilBufferLoaded").start();
			}
			/*else if(bufferUpdateMsg.getWhat() == BufferUpdate.CLOSED)
			{}*/
			
		}
	} // handleMessage
	
	/** Create a buffer. */
	protected void setUp() throws Exception
	{
		// Start jEdit
		super.setUp();
		
		// Add self to the EditBus listener if not there
		EditBus.removeFromBus(this);
		EditBus.addToBus(this);
		
		//  The buffer
		view = jEdit.getFirstView(); 
		if(view == null)
		{ view = jEdit.newView(null); }
		
		// Create the buffer and wait until it's fully loaded
		synchronized (editBus)
		{
			buffer = jEdit.newFile( view ); // new 'Untitled' buffer
			//buffer = jEdit.openTemporary( view, null, "TestBuffer", true ); // new 'Untitled' buffer
			try
			{
				boolean timeout = false;
				while (!buffer.isLoaded() && !timeout)
				{
					editBus.wait(TIMEOUT);
					// here => either timeout or some buffer - 
					// not necessarily the ours - got loaded
					if(editBus[0] == null)
					{
						timeout = true;
						throw new RuntimeException("Waiting for the buffer to " +
								"get loaded took too long, aborting.");
					}
					editBus[0] = null;
				}
			}
			catch (InterruptedException e)
			{}
		}// synchr editBus
	} // setUp
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		if(buffer != null && !buffer.isClosed())
		{ jEdit._closeBuffer(view, buffer); /*forced close*/ }
	}

}
