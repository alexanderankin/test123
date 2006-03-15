/**
 * 
 */
package net.jakubholy.jedit.autocomplete;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

/**
 * OptionPane for the TextAutocomplete lugin.
 * @author <a href="mailto:jakubholy@jakubholy.net">Jakub Holy</a>
 *
 */
public class TextAutocompletePane extends AbstractOptionPane 
{
	/*
	 * # Is a character a part of a word or is it a word separator?
		#  options.isWordElement.code
		# Only offer completions when >= minPrefixLength characters has been typed:
		options.minPrefixLength=3
		# Keys used to accept a selected completion:
		# options.isAcceptKey=KeyEvent.VK_TAB KeyEvent.VK_ENTER
		# Keys used to dispose (hide) the popup window:
		# options.isDisposeKey=KeyEvent.VK_ESCAPE
		# Keys used to move up in the popup list of completions
		# options.isSelectionUpKey=KeyEvent.VK_UP
		# Keys used to move down in the popup list of completions
		# options.isSelectionDownKey=KeyEvent.VK_DOWN
		# Shall the word be rememberd i.e. added to the completion word list?
		# options.isWordToRemember.code
	 */
	private JTextField isWordCode;
	private JTextField minPrefixLength;
	private JTextField acceptKeys;
	private JTextField disposeKeys;
	private JTextField selectionUpKeys;
	private JTextField selectionDownKeys;
	private JTextField isWordToRememberCode;
	private JTextField minWordToRememberLength;
	private JButton resetButton;
	
	public TextAutocompletePane() {
		super("TextAutocomplete");
	}
	
	/* Build the GUI */
	protected void _init() {
		
		addComponent(new JLabel("<html><h2>TextAutocomplete Global Options</h2></html>"));
		
		minPrefixLength = new JTextField();
		minPrefixLength.setToolTipText("Only offer completions when this number of characters have been typed. [2]");
		addComponent("Minimal prefix length       ", minPrefixLength);
		
		minWordToRememberLength = new JTextField();
		minWordToRememberLength.setToolTipText("Don't remeber words of shorter length. [5]");
		addComponent("Remember words long at least", minWordToRememberLength);
		
		isWordToRememberCode = new JTextField();
		isWordToRememberCode.setToolTipText("Should I remember the typed word? Use the variable 'word' (String). [true]");
		addComponent("Is word to remember? [code] ", isWordToRememberCode);
		
		isWordCode = new JTextField();
		isWordCode.setToolTipText("<html>Does the insertion mark the end of the word being typed? " +
				"<br>Use the variables 'insertion' (char) and 'prefix' (String). [only letters]</html>");
		/*JPanel panel = new JPanel();
		panel.add( new JLabel("Is word element? [beanshell code]") );
		panel.add( isWordElementCode );*/
		addComponent("Belongs to word? [code]     ", isWordCode);
		
		addSeparator("Control keys");
		
		//JEditorPane pane = new  JEditorPane();
		JTextArea pane = new JTextArea();
		pane.setLineWrap(true);
		pane.setWrapStyleWord(true);
		// pane.setFont(new java.awt.Font("Dialog", 0, 10));
		pane.setFont( this.getFont() ); 
		//pane.setEditorKit( pane.getEditorKitForContentType("text/html") );	// switch to html
		pane.setEditable(false);
		pane.setBackground(this.getBackground());
		/*pane.setText("<html><p>Keys to control the completion pop-up window:<br>" +
				"A list of names of constants of the class java.awt.event.KeyEvent separated by a space or comma. " +
				"Only special, non-displayable keys such as arrows, Esc and Enter work.</p>" +
				"Example: <code>VK_SPACE VK_ENTER</code></html>");*/
		pane.setText("Keys to control the completion pop-up window:\n" +
				"A list of names of constants of the class java.awt.event.KeyEvent separated by spaces or commas. " +
				"Only special, non-displayable keys such as arrows, Esc and Enter work.\n" +
				"Example: VK_SPACE VK_ENTER");
		addComponent(pane, GridBagConstraints.BOTH);
		
		acceptKeys = new JTextField();
		acceptKeys.setToolTipText("Key to insert selected completion. (Only non-displayable keys such as Enter.) [VK_ENTER VK_TAB]");
		addComponent("Accept with key             ", acceptKeys);
		
		disposeKeys = new JTextField();
		disposeKeys.setToolTipText("Key to hide the completions popup. (Only non-displayable keys such as Escape.) [VK_ESCAPE]");
		addComponent("Dispose with key            ", disposeKeys);
		
		selectionUpKeys = new JTextField();
		selectionUpKeys.setToolTipText("Key to select the completion above. (Only non-displayable keys such as Up arrow.) [VK_UP]");
		addComponent("Up in completions key       ", selectionUpKeys);
		
		selectionDownKeys = new JTextField();
		selectionDownKeys.setToolTipText("Key to select the completion below. (Only non-displayable keys such as Down arrow.) [VK_DOWN]");
		addComponent("Down in completions key ", selectionDownKeys);
		
		addSeparator();
		
		resetButton = new javax.swing.JButton();
		resetButton.setText("Reset options");
		resetButton.addActionListener( new java.awt.event.ActionListener() 
			{
            	public void actionPerformed(java.awt.event.ActionEvent evt)
            	{ resetToDefault(); }
        });
		addComponent(resetButton);
		
		// Display the values
		redisplayValues();
	}

	/* Save & aspply the changes to the properties. */
	protected void _save() {
		String propertyValue = null;
		
		propertyValue = isWordCode.getText();
		if(isValueSet(propertyValue))
		{ propertyValue = PreferencesManager.sanitizeCode(propertyValue); }
		setJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWord-code", propertyValue);
		
		propertyValue = minPrefixLength.getText();
		if( isInteger("minPrefixLength", propertyValue) )
		{ setJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "minPrefixLength", propertyValue); }
		
		propertyValue = acceptKeys.getText();
		setJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "acceptKey", propertyValue);
				
		propertyValue = disposeKeys.getText();
		setJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "disposeKey", propertyValue);
		
		propertyValue = selectionUpKeys.getText();
		setJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "selectionUpKey", propertyValue);
		
		propertyValue = selectionDownKeys.getText();
		setJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "selectionDownKey", propertyValue);
				
		propertyValue = isWordToRememberCode.getText();
		if(isValueSet(propertyValue))
		{ propertyValue = PreferencesManager.sanitizeCode(propertyValue); }
		setJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWordToRemember-code", propertyValue);
		
		propertyValue = minWordToRememberLength.getText();
		if(isInteger("minWordToRememberLength", propertyValue))
		{ setJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "minWordToRememberLength", propertyValue); }
		
		// Notify the PreferencesManager that options have changed
		PreferencesManager.getPreferencesManager().optionsChanged();
	}
	
	/** 
	 * Return true if the propertyValue is convertable to an integer.
	 * @param propertyName Property name to display in an error dialog
	 * @param propertyValue the value that should be an integer
	 */
	private boolean isInteger(final String propertyName, final String propertyValue) {
		if(isValueSet(propertyValue))
		{
			try
			{ Integer.parseInt(propertyValue.trim()); }
			catch(NumberFormatException nf)
			{ 
				GUIUtilities.error(null, TextAutocompletePlugin.PROPS_PREFIX + "errorMessage", 
					new Object[]{ propertyName + " must be an integer but is '"+propertyValue+"'" });
				return false;
			}
		}
		return true;
	}
	
	private void redisplayValues()
	{
		isWordCode.setText( 
				getJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWord-code") );
		minPrefixLength.setText( 
				getJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "minPrefixLength") 
				);
		acceptKeys.setText( 
				getJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "acceptKey") 
				);
		disposeKeys.setText( 
				getJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "disposeKey") 
				);
		selectionUpKeys.setText( 
				getJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "selectionUpKey") 
				);
		selectionDownKeys.setText( 
				getJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "selectionDownKey") 
				);
		isWordToRememberCode.setText( 
				getJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "isWordToRemember-code") 
				);
		minWordToRememberLength.setText( 
				getJEditProperty(TextAutocompletePlugin.PROPS_PREFIX + "minWordToRememberLength") 
				);
	}
	
	/** Reset all options to the default values. */
	public void resetToDefault()	// TODO: Add a button to run this
	{
		String[] properties = new String[] {
				"isWord-code",
				"minPrefixLength",
				"acceptKey",
				"disposeKey",
				"selectionUpKey",
				"selectionDownKey",
				"isWordToRemember-code",
				"minWordToRememberLength"
		};
		
		for (int i = 0; i < properties.length; i++) {
			jEdit.unsetProperty(TextAutocompletePlugin.PROPS_PREFIX + properties[i]);
		}
		
		// Display the new properties
		redisplayValues();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** What to display as a value for a property that is not set. */
	private static String UNSET_PROP = "<default - see documentation>"; 
	
	/** Return jEdit property or, if it isn't set, a default string. */
	private String getJEditProperty(String property)
	{ return jEdit.getProperty(property, UNSET_PROP); }
	
	/** Set jEdit property if the value has been set. */
	private boolean setJEditProperty(String property, String value)
	{
		if( isValueSet(value) )
		{
			jEdit.setProperty(property, value);
			return true;
		}
		else
		{ return false; }
	}
	
	/** True if the display value != the "property unset" value. */
	final private boolean isValueSet(String value)
	{ return (value != null) && (! value.equals(UNSET_PROP)); }
	
	/*
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	
            	TextAutocompletePane pane = new TextAutocompletePane();
            	pane._init();
            	pane.setSize(600, 600);
        		
            	javax.swing.JFrame frame = new javax.swing.JFrame("PAutocomplPaneTest");
            	javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
                frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
                frame.getContentPane().add( pane );
            	frame.add( pane );
            	frame.pack();
        		frame.setVisible(true);                
            }
		});
		System.out.println("Done!");
	}
	*/

}
