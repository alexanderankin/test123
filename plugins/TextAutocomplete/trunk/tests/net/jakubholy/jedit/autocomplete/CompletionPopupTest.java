/**
 *
 */
package net.jakubholy.jedit.autocomplete;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

/**
 * Test the CompletionPopup window - how it handles key/mouse events etc.
 * @author Jakub Holý
 *
 */
public class CompletionPopupTest extends AbstractJEditBufferTest
{

	/**
	 * The popup window under test.
	 * <p>
	 * Usage notes:
	 * <pre>
	 * thePopup.setWord( thePrefix );
	 * thePopup.display( location , Completion[] );
	 * If already visible: thePopup.setCompletions( getCompletions( thePrefix ) );
	 * </pre></p>
	 */
	CompletionPopup thePopup;

	/** Location of the popup. */
	Point location;

	/** A list of completions to feed into the popup. */
	Completion[] completions;

	PreferencesManager prefMngr;

	public CompletionPopupTest(String arg0)
	{ super(arg0); }

	protected void setUp() throws Exception
	{
		super.setUp();
		thePopup = new CompletionPopup(view, buffer);
		location = new Point();
		SwingUtilities.convertPointToScreen(location,
				view.getTextArea().getPainter());

		completions = new Completion[]{
				new Completion("wordFirst"),
				new Completion("wordSecond")
		};

		prefMngr = PreferencesManager.getPreferencesManager();
	}

	///////////////////////////////////////////////////////////////////////////


	/**
	 * A particular completion on the completion list may be
	 * inserted by typing its number.
	 * @see CompletionPopup.KeyHandler
	 */
	@SuppressWarnings("deprecation")
	public void testSelectCompletionByNumber() {
		KeyEvent numTwoEvt = new KeyEvent(
				thePopup,
				KeyEvent.KEY_TYPED,
				EventQueue.getMostRecentEventTime(),
				0,
				KeyEvent.VK_UNDEFINED, '2');

		// I. SELECT. BY NUMBER ENABLED
		// Display the popup
		displayPopup();

		assertEquals("The buffer shall be empty prior to the test",
				0, buffer.getLength());
		assertTrue("Selection by number should be enabled before this test",
				prefMngr.isSelectionByNumberEnabled());

		// Simulate key press
		thePopup.dispatchEvent(numTwoEvt);

		assertEquals("I. [sel by num enabled] The second word (without the prefix) should have been inserted",
				"Second", view.getTextArea().getText() );

		// II. SELECTION BY NUMBER + MODIFIER KEY ONLY -------------------------
		final int modifier = InputEvent.CTRL_MASK;
		prefMngr.setIntegerProperty(
				"selectionByNumberModifierMask",
				modifier);
		assertEquals("Selection by number should now require the modifier Ctrl",
				prefMngr.getSelectionByNumberModifier(), modifier);

		// II.a Without the modifier key
		view.getTextArea().setText("");
		assertEquals("The buffer shall be now empty",
				0, buffer.getLength());

		// test
		displayPopup();
		thePopup.dispatchEvent(numTwoEvt);

		assertEquals("II.a [num w/o modif] The number 2 should have been inserted",
				"2", buffer.getText(0, buffer.getLength()) );

		// II.b With the modifier key pressed
		view.getTextArea().setText("");
		assertEquals("The buffer shall be now empty",
				0, buffer.getLength());

		// test
		displayPopup();
		numTwoEvt.setModifiers(modifier); // TODO: test all possible modifiers (ctrl, alt, altGr, alt+ctrl)
		thePopup.dispatchEvent(numTwoEvt);

		assertEquals("II.b [num+modifier] The second word (without the prefix) should have been inserted",
				"Second", view.getTextArea().getText() );

		// Reset
		numTwoEvt.setModifiers(0);
		prefMngr.setIntegerProperty("selectionByNumberModifierMask", 0);

		// III. SELECTION BY NUMBER DISABLED -----------------------------------
		// Disable selection by number
		prefMngr.setBooleanProperty("isSelectionByNumberEnabled", false);
		assertFalse("Selection by number should be now disabled",
				prefMngr.isSelectionByNumberEnabled());

		// Clear the buffer
		view.getTextArea().setText("");
		assertEquals("The buffer shall be now empty",
				0, buffer.getLength());

		// test
		displayPopup();
		thePopup.dispatchEvent(numTwoEvt);

		assertEquals("III. [sel by num disabled] The number 2 should have been inserted",
				"2", buffer.getText(0, buffer.getLength()) );

		// Re-enable
		prefMngr.setBooleanProperty("isSelectionByNumberEnabled", true);
	}

	///////////////////////////////////////////////////////////////////////////
	/** Display the completion popup window with {@link #completions}.  */
	protected void displayPopup()
	{
		thePopup.setWord( "word" );
		if (! thePopup.isVisible())
		{ thePopup.display( location , completions ); }
		else
		{ thePopup.setCompletions(completions); }
	}


}
