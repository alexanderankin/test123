/*
 *  CharacterMapPlugin.java
 * :folding=explicit:collapseFolds=1:
 *
 *  Copyright (C) 2000, 2001, 2002 Slava Pestov
 *  Copyright (C) 2003 Mark Wickens
 *  Copyright (C) 2011, 2012 Max Funk
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package charactermap;

//{{{ Imports
import charactermap.unicode.UnicodeData;
import charactermap.unicode.UnicodeData.Block;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.StandardUtilities;
//import java.util.StringTokenizer;
//import org.gjt.sp.util.Log;
//}}}

/**
 * A character map is a way of viewing and inserting characters contained
 * within the character set of the character encoding method used by the
 * current buffer.
 *
 * @author     Slava Pestov
 * @author     Mark Wickens
 * @author     Max Funk
 * @version    1.3
 */

@SuppressWarnings({"rawtypes", "unchecked"})
// Alternatively (compiles only in Java 7, not in Java 6):
//	JComboBox -> JComboBox<String>, JComboBox<Block>
//	JComboBoxModel ->  JComboBoxModel<String>, JComboBoxModel<Block>

public class CharacterMap extends JPanel
	implements EBComponent
{
//{{{ Variables

	//{{{ General
	/** Shortcut to CharacterMapPlugin.NAME_PREFIX */
	private final String NAME_PREFIX = CharacterMapPlugin.NAME_PREFIX;
	/** Shortcut to CharacterMapPlugin.OPTION_PREFIX */
	private final String OPTION_PREFIX = CharacterMapPlugin.OPTION_PREFIX;
	/** Replacement for invalid characters */
	private final String REPLACEMENT_CHAR = "\uFFFD";

	/** JEdit view to which character map instance is attached */
	private View view;
	/** Docking position of this character map instance */
	private String position;
	/** Current fonts for font substitution */
	private ArrayList<Font> substitutionFonts;
	/** Current display graphics configuration */
	private GraphicsConfiguration graphConfig;
	//}}}

	//{{{ Display components
	/** Combo box for font encoding information */
	private JComboBox encodingCombo;
	/** Listener for encoding combo settings */
	private ActionListener encodingComboListener;
	/** Encodings selected in jEdit settings */
	private String[] selectedEncodings;
	/** Encodings contained within the encoding combo box */
	private DefaultComboBoxModel encodings;
	/** Selected encoding */
	private String encoding;
	/** Combo-box for unicode blocks */
	private JComboBox blocksCombo;
	/** Blocks contained within the blocks combo box */
	private DefaultComboBoxModel blocks;
	/** Blocks in alphabetic order */
	private boolean blocksAlphabetic;
	/** Block navigation left */
	private JButton blockLeft;
	/** Block navigation right */
	private JButton blockRight;
	/** Listener for the Block navigation actions */
	private ActionListener blockNavigationListener;
	/** Panel taking the block navigation buttons */
	private JPanel blockNavigation;
	/** Component displaying large glyph */
	private CharLabel largeChar;
	/** Table containing character glyphs */
	private JTable table;
	/** Table model for the character map */
	private CharTableModel tableModel;
	/** Number of columns in the table */
	private int tableColumns;
	/** Status information */
	private JTextArea status;
	/** Component displaying super glyph */
	private CharLabel superChar;
	//}}}

	//{{{ Show the following components
	/** Show the encoding combo / information */
	private boolean showEncoding;
	/** Show the unicode blocks */
	private boolean showBlocks;
	/** Show the large glyph */
	private boolean showLarge;
	/** Show the status information */
	private boolean showStatus;
	/** Show the super glyph */
	private boolean showSuper;
	/** Show the Characters with Codepoints above 0xFFFF or 65535 */
	private boolean showHigherPlanes;
	//}}}

	//{{{ Font and Graphics settings
	/** Font size used to draw the large glyph image */
	private float largeSize;
	/** Font size used to draw the super-glyph */
	private float superSize;
	/** Use fractional font-metrics in glyph display */
	private boolean fracFontMetrics;
	/** Offset the super-glyph rendering from the selected glyph */
	private boolean offsetSuper;
	/** Lock anti-aliasing on */
	private boolean alwaysAntiAlias;
	/** Display all glyphs anti-aliased */
	private boolean antiAlias;
	//}}}

//}}}


//{{{ Main Program
	/**
	 * Construct a character map panel using the contents of the current buffer of
	 * the given view as the default encoding, and the font of the given
	 * view to render the character table. Aliasing options set on the
	 * view are honoured within the character map. Selectable options are set from
	 * properties as defined within the options pane.
	 *
	 * @param  view  jEdit view
	 * @see          CharacterMapOptionPane
	 */
	public CharacterMap(View view, String position)
	{
		//{{{ Initial settings
		super(new BorderLayout(12, 12));

		this.view = view;
		this.position = new String(position);

		substitutionFonts = new ArrayList<Font>();

		alwaysAntiAlias = jEdit.getBooleanProperty(OPTION_PREFIX + "anti-alias");
		determineAntiAliasRequirements();

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		//}}}

		//{{{ North Panel
		JPanel northPanel = new JPanel();
		northPanel.setLayout(gridbag);

		JLabel caption = new JLabel(jEdit.getProperty(NAME_PREFIX + "encoding.label"));

		c.weighty = 0.0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		if (isDockedLeftRight()) c.insets = new Insets(5,4,5,4);
		else c.insets = new Insets(0, 4, 0, 4);
		gridbag.setConstraints(caption, c);

		encoding = view.getBuffer().getStringProperty(Buffer.ENCODING);

		// A small, fixed encoding set from jEdit properties
		//encodings = new DefaultComboBoxModel();
		//StringTokenizer st = new StringTokenizer(jEdit.getProperty("encodings"));
		//while (st.hasMoreTokens())
		//	encodings.addElement(st.nextToken());

		// Encoding set from jEdit global options
		selectedEncodings = MiscUtilities.getEncodings(true);
		Arrays.sort(selectedEncodings,new StandardUtilities.StringCompare<String>(true));

		encodings = new DefaultComboBoxModel(selectedEncodings);
		if (encodings.getIndexOf(encoding) < 0) encodings.addElement(encoding);

		encodingCombo = new JComboBox(encodings);
		encodingCombo.setEditable(true);
		encodingCombo.setSelectedItem(encoding);
		encodingComboListener = new ActionHandler();
		encodingCombo.addActionListener(encodingComboListener);

		if (isDockedLeftRight()) c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(encodingCombo, c);

		showEncoding = jEdit.getBooleanProperty(OPTION_PREFIX + "encoding");
		if (showEncoding)
		{
			northPanel.add(caption);
			northPanel.add(encodingCombo);
		}

		if (!isDockedLeftRight()) northPanel.add(Box.createHorizontalStrut(12));

		caption = new JLabel(jEdit.getProperty(NAME_PREFIX + "blocks.label"));

		c.gridwidth = 1;
		gridbag.setConstraints(caption,c);

		showHigherPlanes = jEdit.getBooleanProperty(OPTION_PREFIX + "higher-planes");
		blocksAlphabetic = jEdit.getBooleanProperty(OPTION_PREFIX + "blocks-abc");

		Iterator<Block> blocks_iterator;
		blocks = new DefaultComboBoxModel();
		if(showHigherPlanes && blocksAlphabetic) {
			blocks_iterator = UnicodeData.getBlocksABC().iterator();
			while (blocks_iterator.hasNext()) blocks.addElement(blocks_iterator.next());
		}
		else if(showHigherPlanes && !blocksAlphabetic) {
			blocks_iterator = UnicodeData.getBlocks().iterator();
			while (blocks_iterator.hasNext()) blocks.addElement(blocks_iterator.next());
		}
		else if(!showHigherPlanes && blocksAlphabetic) {
			blocks_iterator = UnicodeData.getLowBlocksABC().iterator();
			while (blocks_iterator.hasNext()) blocks.addElement(blocks_iterator.next());
		}
		else {
			blocks_iterator = UnicodeData.getLowBlocks().iterator();
			while (blocks_iterator.hasNext()) blocks.addElement(blocks_iterator.next());
		}
		blocksCombo = new JComboBox(blocks);
		blocksCombo.setSelectedItem(UnicodeData.getBlock("Basic Latin"));
		blocksCombo.addItemListener(new ItemHandler());
		blocksCombo.setEnabled(isUnicode(encoding));

		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		if (isDockedLeftRight()) c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(blocksCombo, c);

		blockLeft = new JButton("<");
		blockRight = new JButton(">");
		blockLeft.setMargin(new Insets(1,1,1,1));
		blockRight.setMargin(new Insets(1,1,1,1));

		blockNavigationListener = new ActionHandler();
		blockLeft.setActionCommand("block-left");
		blockRight.setActionCommand("block-right");
		blockLeft.addActionListener(blockNavigationListener);
		blockRight.addActionListener(blockNavigationListener);

		blockNavigation = new JPanel();
		blockNavigation.add(blockLeft);
		blockNavigation.add(blockRight);
		blockLeft.setEnabled(isUnicode(encoding));
		blockRight.setEnabled(isUnicode(encoding));

		c.weightx = 0.0;
		c.gridwidth=1;
		if (isDockedLeftRight()) c.anchor = GridBagConstraints.NORTHEAST;
		else c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(blockNavigation,c);

		showBlocks = jEdit.getBooleanProperty(OPTION_PREFIX + "blocks");
		if (showBlocks)
		{
			northPanel.add(caption);
			northPanel.add(blocksCombo);
		}

		caption = new JLabel(jEdit.getProperty(NAME_PREFIX + "char.label"));
		c.gridwidth=1;
		c.weightx = 0.0;
		if (isDockedLeftRight()) c.anchor = GridBagConstraints.WEST;
		else c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(caption, c);

		largeSize = (float) jEdit.getIntegerProperty(OPTION_PREFIX + "large-size");
		largeChar = new CharLabel(largeFont(), " ");
		Dimension largeCharSz = new Dimension();
		largeCharSz.height = largeChar.getPreferredSize().height * 5 / 4;
		largeCharSz.width = largeCharSz.height * 3 / 2;
		largeChar.setMinimumSize(largeCharSz);
		largeChar.setPreferredSize(largeCharSz);
		gridbag.setConstraints(largeChar, c);

		if (showBlocks && ! isDockedLeftRight())
			northPanel.add(blockNavigation);

		showLarge = jEdit.getBooleanProperty(OPTION_PREFIX + "large");
		if (showLarge) {
			northPanel.add(caption);
			northPanel.add(largeChar);
		}

		if (showBlocks && isDockedLeftRight())
			northPanel.add(blockNavigation);

		superSize = (float) jEdit.getIntegerProperty(OPTION_PREFIX + "super-size");
		superChar = new CharLabel(superFont(), " ");
		superChar.setBorder(BorderFactory.createLineBorder(Color.black));
		showSuper = jEdit.getBooleanProperty(OPTION_PREFIX + "super");
		offsetSuper = jEdit.getBooleanProperty(OPTION_PREFIX + "super-offset");

		add(BorderLayout.NORTH, northPanel);
		//}}}

		//{{{ Center Table
		setTableColumns();
		tableModel = new CharTableModel();

		table = new JTable(tableModel);
		table.setFont(normalFont());
		table.setRowHeight(tableRowHeight());
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		MouseHandler mouseHandler = new MouseHandler(this, superChar);
		table.addMouseListener(mouseHandler);
		table.addMouseMotionListener(mouseHandler);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setDefaultRenderer(Object.class, new CharTableCellRenderer());

		add(BorderLayout.CENTER, new JScrollPane(table));
		//}}}

		//{{{ South Panel (Status Line)
		JPanel southPanel = new JPanel();
		southPanel.setLayout(gridbag);

		status = new JTextArea(" ");
		status.setRows(isDockedLeftRight() ? 3 : 1);
		status.setEditable(false);
		status.setOpaque(false);
		status.setFont(new Font(Font.MONOSPACED, Font.PLAIN,
			UIManager.getFont("Label.font").getSize()));

		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(status, c);

		showStatus = jEdit.getBooleanProperty(OPTION_PREFIX + "status");
		if (showStatus) {
			southPanel.add(status);
			add(BorderLayout.SOUTH, southPanel);
		}
		//}}}

		//{{{ Final settings
		table.repaint();
		graphConfig = table.getGraphicsConfiguration();
		//}}}
	}
//}}}


//{{{ Auxiliary functions

	//{{{ General
	/** Formatted output of int in hexadecimal form
	 *  @param i       Integer to be converted
	 *  @param digits  Minimum number of digits (fill with leading zeros)
	 *  @param upper   true: Uppercase digits; false: Lowercase
	 *  @param prefix  Prefix string
	 *  @return        Formatted string
	 */
	public static String toHexString(int i, int digits, boolean upper, String prefix)
	{
		String str = Integer.toHexString(i);
		if (upper) str = str.toUpperCase();
		else str = str.toLowerCase();
		int nzeros = digits - str.length();
		if (nzeros < 0) nzeros = 0;
		while (nzeros > 0) {
			str = "0" + str;
			--nzeros;
		}
		str = prefix + str;
		return str;
	}

	/** Formatted output of int in decimal form
	 *  @param i       Integer to be converted
	 *  @param digits  Minimum number of digits (fill with leading zeros)
	 *  @param prefix  Prefix string
	 *  @return        Formatted string
	 */
	public static String toDecString(int i, int digits, String prefix)
	{
		String pattern = prefix;
		int k = digits;
		while (k > 0) {
			pattern += "0";
			--k;
		}
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		return myFormatter.format(i);
	}

	/** Font used to draw character glyphs in table
	 *  (normal, without font substitution)
	 */
	private Font normalFont()
	{
		return view.getTextArea().getPainter().getFont();
	}

	/** Font used to draw character glyphs in table.
	 *  Use font substitution for missing characters,
	 *  if this feature is selected in jEdit options.
	 *  @param codepoint Codepoint of character glyph to be drawn
	 *  @return          Best font, which can show the glyph.
	 *                   If no font is found, return normalFont()
	 *  @see org.gjt.sp.jedit.syntax.Chunk
	 */
	private Font autoFont(int codepoint)
	{
		Font f = normalFont();

		// If normal font sufficient or no font substitution
		// -> return normal font
		if (f.canDisplay(codepoint)
		|| !jEdit.getBooleanProperty("view.enableFontSubst"))
		{
			return f;
		}

		// If the string contains control characters
		// -> return normal font
		if (Character.isISOControl(codepoint))
			return f;

		// If empty, determine substitution font list
		if (substitutionFonts.isEmpty())
		{
			// add preferred fonts
			String family;
			Font candidate;
			int i = 0;
			while ((family = jEdit.getProperty("view.fontSubstList." + i)) != null)
			{
				candidate = new Font(family, Font.PLAIN, f.getSize());
				// if the specified font does not exist,
				// java falls back to a font with family "Dialog".
				// Don't take that one.
				if (candidate.getFamily().equalsIgnoreCase(family))
					substitutionFonts.add(candidate);
				i++;
			}
			// add system fonts
			if (jEdit.getBooleanProperty("view.enableFontSubstSystemFonts",true))
			{
				Font[] sysFonts = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getAllFonts();
				for(i = 0; i < sysFonts.length; i++)
				{
					candidate = sysFonts[i].deriveFont(Font.PLAIN, f.getSize());
					if (candidate.getFamily()
						.equalsIgnoreCase(sysFonts[i].getFamily()))
						substitutionFonts.add(candidate);
				}
			}
		}

		// return substitution font
		Iterator<Font> I = substitutionFonts.iterator();
		Font candidate;
		while (I.hasNext())
		{
			candidate = I.next();
			if (candidate.canDisplay(codepoint))
			{
				return candidate;
			}
		}

		// if nothing found
		return f;
	}

	/** Font used to draw the large glyph image.
	 *  The font family is determined by normalFont().
	 */
	private Font largeFont()
	{
		return normalFont().deriveFont(largeSize);
	}

	/** Font used to draw the large glyph image.
	 *  The font family is determined by autoFont().
	 *  @param codepoint Codepoint of glyph to be drawn
	 */
	private Font largeFont(int codepoint)
	{
		return autoFont(codepoint).deriveFont(largeSize);
	}

	/** Font used to draw the super large glyph image.
	 *  The font family is determined by normalFont().
	 */
	private Font superFont()
	{
		return normalFont().deriveFont(superSize);
	}

	/** Font used to draw the super large glyph image.
	 *  The font family is determined by autoFont().
	 *  @param codepoint Codepoint of glyph to be drawn
	 */
	private Font superFont(int codepoint)
	{
		return autoFont(codepoint).deriveFont(superSize);
	}

	/** Height of tableRows depending on the normal font size */
	private int tableRowHeight()
	{
		return normalFont().getSize() * 4/3;
	}

	/** Set member variables regarding anti-aliasing and
	 *  fractional font-metrics requirements from the defined
	 *  values in the jEdit properties
	 */
	private void determineAntiAliasRequirements()
	{
		if (alwaysAntiAlias)
		{
			antiAlias = true;
			fracFontMetrics = true;
		}
		else
		{
			antiAlias = jEdit.getBooleanProperty("view.antiAlias");
			fracFontMetrics = jEdit.getBooleanProperty("view.fracFontMetrics");
		}
	}

	/** Character map window is docked left or right */
	private boolean isDockedLeftRight()
	{
		return position.equalsIgnoreCase(DockableWindowManager.LEFT)
		    || position.equalsIgnoreCase(DockableWindowManager.RIGHT);
	}

	/** Character map window is docked top or bottom */
	private boolean isDockedTopBottom()
	{
		return position.equalsIgnoreCase(DockableWindowManager.TOP)
		    || position.equalsIgnoreCase(DockableWindowManager.BOTTOM);
	}

	/** Encoding is Unicode
	 *  @param enc Encoding name
	 */
	private boolean isUnicode(String enc)
	{
		return enc.toUpperCase().startsWith("UNICODE")
		|| enc.toUpperCase().startsWith("UTF")
		|| enc.toUpperCase().startsWith("X-UTF");
	}

	/** Determine the size of the currently selected block.
	 *  If the current encoding is not Unicode, fixed sizes are returned.
	 */
	private int getBlockSize()
	{
		if (isUnicode(encoding))
		{
			Block block = (Block)blocksCombo.getSelectedItem();
			return block.length();
		}
		else if (encoding.toUpperCase().contains("ASCII"))
		{
			return 128;
		}
		else
		{
			return 256;
		}
	}


	/** Determine the offset of the currently selected block.
	 *  If the current encoding is not Unicode, zero is returned.
	 */
	private int getBlockOffset()
	{
		if (isUnicode(encoding))
		{
			Block block = (Block)blocksCombo.getSelectedItem();
			return block.getFirstPoint();
		}
		else
		{
			return 0;
		}
	}
	//}}}

	//{{{ Encoding
	/**
	 * Set the charmap encoding and the encoding combo box to the input value.
	 * If the value does not exist in the encodings list, it is stored
	 * for later reference in this session.
	 * If the encoding really changed, the charmap table is redrawn.
	 * @param enc New encoding.
	 * @see   encoding
	 * @see   encodings
	 * @see   encodingCombo
	 */
	private void changeEncodingCharMap(String enc)
	{
		boolean encodingChanged = false;

		if (!enc.equals(encoding))
		{
			encoding = enc;
			encodingChanged=true;
		}
		if (!encodings.getSelectedItem().equals(enc))
		{
			encodingCombo.setSelectedItem(enc);
			encodingChanged=true;
		}
		if (encodings.getIndexOf(enc) < 0)
		{
			encodings.addElement(enc);
		}
		if (encodingChanged)
		{
			blocksCombo.setEnabled(isUnicode(encoding));
			blockLeft.setEnabled(isUnicode(encoding));
			blockRight.setEnabled(isUnicode(encoding));
			tableModel.fireTableDataChanged();
			table.repaint();
			clearStatusText();
		}
	}

	/**
	 * Set the encoding of the current jEdit Buffer to the input value.
	 * @param enc New encoding.
	 * @see   org.gjt.sp.jedit.Buffer
	 */
	private void changeEncodingBuffer(String enc)
	{
		if (!view.getBuffer().getStringProperty(JEditBuffer.ENCODING).equals(enc))
		{
			view.getBuffer().setStringProperty(JEditBuffer.ENCODING, encoding);
			view.getBuffer().setDirty(true);
			view.getBuffer().propertiesChanged();
		}
	}
	//}}}

	//{{{ Table
	/** Set tableColumns depending on character map options
	 *  and window docking state.
	 *  @see tableColumns
	 */
	private void setTableColumns()
	{
		if (isDockedLeftRight()) {
			tableColumns = jEdit.getIntegerProperty(OPTION_PREFIX + "columns-dock-lr");
		}
		else if (isDockedTopBottom()) {
			tableColumns = jEdit.getIntegerProperty(OPTION_PREFIX + "columns-dock-tb");
		}
		else {
			tableColumns = jEdit.getIntegerProperty(OPTION_PREFIX + "columns");
		}
	}
	//}}}

	//{{{ Status line
	/**
	 * Set the value of the status string, for the given table entry.
	 * For invalid codepoints / characters the status string is cleared.
	 *
	 * @param cp  Codepoint (unicode) of the selected character, or -1 if none
	 * @param row Table row of the selected character
	 * @param col Table column of the selected character
	 * @see   Character.isValidCodePoint
	 * @see   CharTableModel.isValidChar
	 */
	private void setStatusText(int row, int col)
	{

		if (!tableModel.isValidChar(row, col)) {
			clearStatusText();
			return;
		}


		int index;    // Table index = codepoint for current encoding
		int cp;       // Unicode codepoint for the table index
		String ch;    // Character as unicode string
		byte[] bytes; // Character bytecode for current encoding
		String name;  // Character unicode name

		// Determine status line parameters

		index = tableModel.getIndexAt(row,col);
		ch = (String) tableModel.getValueAt(row,col);
		cp = ch.codePointAt(0);

		try {
			// switch to encodings without explicit encoding marks
			String enc = encoding.toUpperCase();
			if (enc.contains("UTF") && enc.contains("8"))
				enc = "UTF-8";
			if (enc.contains("UTF") && enc.contains("16")) {
				enc = enc.contains("LE") ?
				"UTF-16LE" : "UTF-16BE";
			}
			if (enc.contains("UTF") && enc.contains("32")) {
				enc = enc.contains("LE") ?
				"UTF-32LE" : "UTF-32BE";
			}

			bytes = ch.getBytes(enc);
		}
		catch (Exception e) {
			clearStatusText();
			return;
		}

		//name = Character.getName(cp);
		name = UnicodeData.getCharacterName(cp);

		// Write the status line

		StringBuilder buf = new StringBuilder();

		buf.append(toDecString(index, 3, " Dec: "));
		buf.append(toHexString(index, 2, true, " Hex: 0x"));

		if (isDockedLeftRight()) buf.append("\n");

		buf.append(" Bytes:");
		for (int i = 0; i < bytes.length; i++)
			buf.append(toHexString(bytes[i] & 0xFF, 2, true, " 0x"));

		if (isDockedLeftRight()) buf.append("\n");

		if (name != null)
			buf.append(" Name: ").append(name);
		else
			buf.append(" Name: ").append(" - ");

		status.setText(buf.toString());
	}

	/** Clear the status line */
	private void clearStatusText()
	{
		status.setText(" ");
		status.setRows(isDockedLeftRight() ? 3 : 1);
	}
	//}}}

	//{{{ Interaction with jEdit
	/**
	 * Implementation of EBComponent.
	 * - Changes charmap encoding, if buffer encoding has changed in jEdit.
	 * - Changes table row height, if text area font size has changed in jEdit.
	 * - Change charmap encodings list if selected encodings have changed in jEdit.
	 * - Reset Substitution fonts. (Initialisation is done in the autoFont() function)
	 */
	public void handleMessage(EBMessage message)
	{
		if (((message instanceof BufferUpdate)
			&& (((BufferUpdate) message).getWhat().equals(BufferUpdate.CREATED)
			|| ((BufferUpdate) message).getWhat().equals(BufferUpdate.LOADED)
			|| ((BufferUpdate) message).getWhat().equals(BufferUpdate.PROPERTIES_CHANGED)))
		|| ((message instanceof EditPaneUpdate)
			&& ((EditPaneUpdate) message).getWhat().equals(EditPaneUpdate.BUFFER_CHANGED)))
		{
			changeEncodingCharMap(view.getBuffer().getStringProperty(JEditBuffer.ENCODING));
		}
		if (message instanceof PropertiesChanged)
		{
			if (table.getRowHeight() != tableRowHeight())
			{
				table.setRowHeight(tableRowHeight());
				table.repaint();
			}

			// Reinitialise encoding combo list
			String[] selectedEncodingsNew = MiscUtilities.getEncodings(true);
			Arrays.sort(selectedEncodingsNew,new StandardUtilities.StringCompare<String>(true));
			if (!Arrays.equals(selectedEncodingsNew, selectedEncodings))
			{
				encodingCombo.removeActionListener(encodingComboListener);
				encodingCombo.setSelectedIndex(-1);
				encodings.removeAllElements();
				for (int i = 0; i < selectedEncodingsNew.length; i++)
					encodings.addElement(selectedEncodingsNew[i]);
				if (encodings.getIndexOf(encoding) < 0) encodings.addElement(encoding);
				encodingCombo.setSelectedItem(encoding);
				encodingCombo.addActionListener(encodingComboListener);
				selectedEncodings = selectedEncodingsNew;
			}

			// Reinitialise Substitution Fonts
			substitutionFonts.clear();
		}
	}

	/** Charmap Communication with EditBus: 'Remove' */
	@Override
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);
	}

	/** Charmap Communication with EditBus: 'Add' */
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);
	}

	/** Write character into active jEdit Buffer.
	 *  Don't write and issue a warning message, if the buffer is non-Unicode
	 *  and a different encoding is chosen in character map.
	 */
	private void setCharInBuffer(String ch)
	{
		String bufferEncoding = view.getBuffer()
			.getStringProperty(JEditBuffer.ENCODING);
		if (bufferEncoding.toUpperCase().equals(encoding.toUpperCase())
			|| isUnicode(bufferEncoding)) {
			view.getTextArea().setSelectedText(ch);
		}
		else {
			JOptionPane.showMessageDialog(view,
				jEdit.getProperty(NAME_PREFIX + "no-insert-message-1")
				  + bufferEncoding
				  + jEdit.getProperty(NAME_PREFIX + "no-insert-message-2")
				  + encoding
				  + jEdit.getProperty(NAME_PREFIX + "no-insert-message-3"),
				jEdit.getProperty(NAME_PREFIX + "no-insert-message.label"),
				JOptionPane.WARNING_MESSAGE);
		}
	}
	//}}}

//}}}


//{{{ Auxiliary classes

	//{{{ Handlers
	/**
	 *  Handles actions performed on the encoding combo-box
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 */
	class ActionHandler implements ActionListener
	{
		/**
		 * Changes encoding after entering a value in the encoding
		 * combo-box.
		 *
		 * @param  evt  The event representing the action performed
		 */
		public void actionPerformed(ActionEvent evt)
		{
			if (evt.getSource() == encodingCombo)
			{
				changeEncodingCharMap((String) encodingCombo.getSelectedItem());
			}
			else if (evt.getActionCommand().equals("block-left"))
			{
				int index = blocksCombo.getSelectedIndex();
				if (index > 0)
					blocksCombo.setSelectedIndex(index - 1);
			}
			else if (evt.getActionCommand().equals("block-right"))
			{
				int index = blocksCombo.getSelectedIndex();
				if (index < (blocksCombo.getItemCount() - 1))
					blocksCombo.setSelectedIndex(index + 1);
			}
		}
	}


	/**
	 *  Catches changes to the slider value
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 */
	class SliderChangeHandler implements ChangeListener
	{
		/**
		 * Called when the slider value changes. Resets the
		 * status text to display the current page and repaints
		 * the table.
		 *
		 * @param  evt	The event representing the state change
		 */
		public void stateChanged(ChangeEvent evt)
		{
			table.repaint();
			clearStatusText();
		}
	}

	/**
	 *  Catches item events in the combo boxes
	 */
	class ItemHandler implements ItemListener
	{
		/**
		 * Called when the items in the block and encoding combos
		 * are changed. Changes the table content according to the
		 * new settings.
		 */
		public void itemStateChanged(final ItemEvent evt)
		{
			tableModel.fireTableDataChanged();
			table.repaint();
			clearStatusText();
		}
	}

	/**
	 * Handles mouse interaction (movement, dragging
	 * and button activity) with the glyph table
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 */
	class MouseHandler extends MouseInputAdapter
	{
		/** Row in glyph table of selected glyph, -1 if no glyph selected */
		private int row;
		/** Column in glyph table of selected glyph, -1 if no glyph selected */
		private int column;
		/** Popup containing super-character */
		private Popup popup;
		/** Owner of the popup (the character map panel) */
		private JPanel owner;
		/** Contents of the popup, displays the super-sized glyph rendering */
		private JLabel contents;
		/** Indicates that the super-sized glyph rendering is active */
		private boolean displayingSuperChar;

		/**
		 * Construct a mouse handler and maintain a reference
		 * to the panel containing the table for which this
		 * handler has been constructed, and the label that will
		 * be displayed within the super-character popup.
		 *
		 * @param  owner     Container of table whose mouse events are to be handled
		 * @param  contents  Popup contents
		 */
		public MouseHandler(JPanel owner, JLabel contents)
		{
			this.owner = owner;
			this.contents = contents;
			this.displayingSuperChar = false;
		}

		/**
		 * A mouse button has been clicked (pressed and released
		 * on the same spot. If this is button 1, determine the
		 * table cell within which the pointer is located and
		 * insert the corresponding character within the current
		 * buffer.
		 *
		 * @param  evt  Event containing button activity information
		 */
		 @Override
		public void mouseClicked(MouseEvent evt)
		{
			int button = evt.getButton();
			if (button == MouseEvent.BUTTON1) {
				Point p = evt.getPoint();
				row = table.rowAtPoint(p);
				column = table.columnAtPoint(p);
				if (tableModel.isValidChar(row, column) ) {
					String ch = getChar(row, column);
					setCharInBuffer(ch);
				}
			}
		}

		/**
		 * A mouse button has been pressed. If this is button 3
		 * the table cell within the pointer is located is calculated
		 * and the super character is displayed for the glyph contained
		 * within the selected table cell
		 *
		 * @param  evt  Event containing mouse button press information
		 */
		 @Override
		public void mousePressed(MouseEvent evt)
		{
			int button = evt.getButton();
			if (button == MouseEvent.BUTTON3) {

				if (row == -1 || column == -1) return;

				Point p = evt.getPoint();
				row = table.rowAtPoint(p);
				column = table.columnAtPoint(p);

				if (showSuper) {
					//Log.log(Log.MESSAGE, this, "Got mousePressed at x = " + p.x + ", y = " + p.y);
					String ch = getChar(row, column);
					int cp = ch.codePointAt(0);
					superChar.setFont(superFont(cp));

					if (popup == null) {
						// We need to do this because the popup contents will not have
						// been defined and the final position on screen depends
						// on the size of the contents.
						displaySuperChar(ch, row, column);
						popup.hide();
					}
					displaySuperChar(ch, row, column);
					displayingSuperChar = true;
				}
			}
		}

		/**
		 * Mouse has been dragged across the table. Determines
		 * if the cell within which the mouse pointer is now positioned
		 * is different from the last recorded cell. If it is then
		 * the status text and large character are updated. If we are
		 * displaying the super character (button 3 must be currently
		 * pressed) then we hide the popup and recreate it at new cell
		 * with new cell contents.
		 *
		 * @param  evt  Event containing mouse drag information
		 */
		 @Override
		public void mouseDragged(MouseEvent evt)
		{
			Point p = evt.getPoint();
			int newRow = table.rowAtPoint(p);
			int newColumn = table.columnAtPoint(p);
			boolean changed = (newRow != row) || (newColumn != column);

			if (changed) {
				row = newRow;
				column = newColumn;

				if (row == -1 || column == -1) {
					clearStatusText();
					largeChar.setText(" ");
				}
				else {
					String ch = getChar(row, column);
					int cp = ch.codePointAt(0);
					largeChar.setFont(largeFont(cp));
					superChar.setFont(superFont(cp));

					setStatusText(row, column);
					largeChar.setText(ch);
					if (displayingSuperChar) {
						//Log.log(Log.MESSAGE, this, "Got mouseDragged at x = " + p.x + ", y = " + p.y);
						popup.hide();
						// Snap click location to centre of cell in table
						displaySuperChar(ch, row, column);
					}
				}
			}
		}

		/**
		 * Mouse button has been released. If this is button 3
		 * then we hide the super character
		 *
		 * @param  evt  Description of the Parameter
		 * @param,      evt Event containing mouse button release information
		 */
		 @Override
		public void mouseReleased(MouseEvent evt)
		{
			int button = evt.getButton();
			if (button == MouseEvent.BUTTON3) {
				hideSuperChar();
				displayingSuperChar = false;
			}
		}

		/**
		 * Mouse has been moved when no mouse buttons are being
		 * pressed. The row and column in the table of the cell within
		 * which the mouse pointer falls are calculated, and if they
		 * are different from the last recorded cell position then the
		 * status text and large character are updated to reflect the
		 * selected glyph.
		 *
		 * @param  evt  Event containing new mouse position
		 */
		@Override
		public void mouseMoved(MouseEvent evt)
		{
			Point p = evt.getPoint();
			int newRow = table.rowAtPoint(p);
			int newColumn = table.columnAtPoint(p);
			boolean changed = (newRow != row) || (newColumn != column);

			if (changed) {
				row = newRow;
				column = newColumn;

				if (row == -1 || column == -1) {
					clearStatusText();
					largeChar.setText(" ");
				}
				else {
					String ch = getChar(row, column);
					int cp = ch.codePointAt(0);

					largeChar.setFont(largeFont(cp));
					largeChar.setText(ch);

					setStatusText(row, column);
				}
			}
		}

		/**
		 * Mouse was moved outside the table boundary.
		 * Reset the status text and the large character
		 * rendering
		 *
		 * @param  evt  Mouse event that caused exit
		 */
		@Override
		public void mouseExited(MouseEvent evt)
		{
			row = -1; column = -1;
			clearStatusText();
			largeChar.setText(" ");
		}

		/**
		 * Determine the centre of the selected cell in the table
		 * and return as an offset relative to the origin on the table
		 * rendering
		 *
		 * @param  row     Cell row
		 * @param  column  Cell column
		 * @return         Centre point of given table cell, relative to
		 *                 origin of table
		 */
		private Point getPointInTable(int row, int column)
		{
			TableColumnModel tcm = table.getColumnModel();
			int cellX = 0;
			int cellWidth = tcm.getColumn(0).getWidth();
			for (int i = 0; i < column; i++) {
				TableColumn tc = tcm.getColumn(i);
				cellWidth = tc.getWidth();
				cellX += cellWidth
				/* + tcm.getColumnMargin() */ ;
			}
			int cellHeight = table.getRowHeight();

			int px = table.getX() + cellX + (cellWidth / 2);
			int py = table.getY() + (cellHeight * row) + (cellHeight / 2);
			Point rtn = new Point(px, py);
			//Log.log(Log.MESSAGE, this, "Centre of cell in table is x = " + rtn.x + ", y = " + rtn.y);
			return rtn;
		}

		/**
		 * Determine the width (in pixels) of the given column of
		 * the glyph table.
		 *
		 * @param  column  The index of the table column
		 * @return         Width of given column in pixels
		 */
		private int getColumnWidth(int column)
		{
			TableColumnModel tcm = table.getColumnModel();
			TableColumn tc = tcm.getColumn(column);
			return tc.getWidth();
		}

		/**
		 * Get the character of the glyph in the given table position.
		 * Shortcut to getValueAt in CharTableModel.
		 */
		private String getChar(int row, int column)
		{
			return (String) tableModel.getValueAt(row, column);
		}

		/**
		 * Create a popup containing the text within the given
		 * string rendered with the 'super-character' options.
		 * The popup is displayed at the given glyph table location, unless
		 * the offset option is set in which case the popup is located
		 * above and to the right of the selected table location. If the
		 * calculated location of the popup would move it off the current
		 * display, the location is altered so it is always displayed
		 * fully
		 * @param ch The character glyph to display
		 * @param row The selected glyph table row
		 * @param column The selected glyph table column
		 */
		private void displaySuperChar(String ch, int row, int column)
		{
			Point p = getPointInTable(row, column);
			int x = p.x;
			int y = p.y;

			PopupFactory factory = PopupFactory.getSharedInstance();
			contents.setText(ch);
			// Position of popup should be relative to owner component
			// over the character in the table
			Point ownerLoc = owner.getLocationOnScreen();
			int displayX = ownerLoc.x + x;
			int displayY = ownerLoc.y + y;

			int popupWidth = contents.getWidth();
			int popupHeight = contents.getHeight();

			displayX -= (popupWidth / 2);
			//displayY += (popupHeight / 2);

			if (offsetSuper) {
				int rowHeight = table.getRowHeight();
				int columnWidth = getColumnWidth(column);
				displayX += (columnWidth / 2) + (popupWidth / 2);
				displayY -= (rowHeight / 2) + (popupHeight / 2);

				GraphicsConfiguration gf = CharacterMap.this.getGraphicsConfiguration();
				Rectangle bounds = gf.getBounds();
				int screenWidth = bounds.x + bounds.width;
				if (displayX + popupWidth > screenWidth) {
					displayX = screenWidth - popupWidth;
				}
				if (displayY < bounds.y) {
					displayY = bounds.y;
				}
			}

			popup = factory.getPopup(owner, contents, displayX, displayY);
			popup.show();
		}

		/** Hide the popup containing the super character  */
		private void hideSuperChar()
		{
			popup.hide();
		}
	}
	//}}}

	//{{{ GUI Component extensions
	/**
	 * JLabel with anti-aliasing rendering turned on if required
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 */
	class CharLabel extends JLabel
	{
		/** Map containing hints for the renderer (eg, render anti-aliased) */
		private Map<RenderingHints.Key,Object> renderingHints;

		/**
		 * Construct a label with the given font and text
		 *
		 * @param  font  The font to render label value in
		 * @param  text  The text of the label
		 */
		public CharLabel(Font font, String text)
		{
			// Initialise JLabel
			super();

			// Anti-Aliasing-Feature
			renderingHints = new HashMap<RenderingHints.Key,Object>();

			if (antiAlias) {
				//hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
			else {
				renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}

			renderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS,
				fracFontMetrics ?
				RenderingHints.VALUE_FRACTIONALMETRICS_ON
				 : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

			// Set font and text
			if (font == null)
				setFont(normalFont());
			else
				setFont(font);
			setText(text);

			// Default Layout
			setBackground(Color.WHITE);
			setOpaque(true);
			setForeground(Color.BLACK);
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);
		}

		/**
		 * Paint the contents of the label. Sets the selected rendering
		 * hints before painting the label via the super class pain
		 * method
		 *
		 * @param  gc  Graphics context within which to paint the component
		 */
		@Override
		public void paint(Graphics gc)
		{
			Graphics2D gc2d = (Graphics2D) gc;
			if (gc2d != null) {
				// Set anti-aliasing options
				gc2d.addRenderingHints(renderingHints);
			}
			super.paint(gc);
		}

		/**
		 * Set the text in the character labels
		 * Empty strings, undefined chars and surrogates are replaced
		 * by a single space.
		 * Strings consisting of a control character are converted
		 * to numbers with smaller size.
		 *
		 * @param  text Text string for label
		 */
		@Override
		public final void setText(String text)
		{
			// Empty text
			if ((text == null) || text.isEmpty()) {
				super.setText(" ");
				return;
			}

			int cp = text.codePointAt(0);

			// Control chars
			if ((text != null)
				&& (text.length() == 1)
				&& Character.isISOControl(cp)) {
				text = "#" + cp;
				setFont(this.getFont().deriveFont(
					(float) this.getFont().getSize() * 2/3));
			}
			// Undefined chars, Surrogates
			else if ((text == null) || text.isEmpty()
				//|| !Character.isDefined(text.codePointAt(0)) )
				|| !UnicodeData.isDefined(cp)
				|| ((cp <= 0xFFFF) && Character.isSurrogate((char) cp)))
				text = " ";

			super.setText(text);
		}
	}

	/**
	 * Model of character data contained within the
	 * glyph table.
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 */
	class CharTableModel extends AbstractTableModel
	{
		/**
		 * @return    Number of columns in the glyph table
		 */
		public int getColumnCount()
		{
			return tableColumns;
		}

		/**
		 * @return    Number of rows in the glyph table
		 */
		public int getRowCount()
		{
			int tableRows = (getBlockSize() - getBlockSize() % tableColumns)
				/ tableColumns;
			if ( getBlockSize() % tableColumns != 0 ) tableRows += 1;
			return tableRows;
		}

		/**
		 * Returns the index of a character in the glyph table.
		 * i.e. the codepoint for the given encoding.
		 * For Unicode blocks, the block offset is added.
		 * For entries outside the table (row == -1 or col == -1)
		 * return -1
		 *
		 * @param row Row of table containing character.
		 * @param col Column of table containing the character.
		 * @return Index of cell in the glyph table.
		 */
		public int getIndexAt(int row, int col)
		{
			int cell = row * tableColumns + col;

			if ((row == -1) || (col == -1))
				return -1;

			if (isUnicode(encoding))
				return cell + getBlockOffset();
			else
				return cell;
		}

		/**
		 * Generates a string containing the character stored within
		 * the table at the given row and column. If the encoding is
		 * Unicode, this depends on the current page value.
		 * For invalid table entries, the unicode replacement char
		 * is returned.
		 *
		 * @param  row  Row of table containing required character
		 * @param  col  Column of table containing required character
		 * @return      String containing character representation of
		 *              glyph stored within glyph table at given row
		 * @see         isValidChar
		 * @see         REPLACEMENT_CHAR
		 */
		public Object getValueAt(int row, int col)
		{
			String ch;

			// TODO: Make a better algorithm with the Encoder class
			//       instead of String(byte[])
			try {
				if (isUnicode(encoding)) {
					int cp = getIndexAt(row, col);
					if (! Character.isValidCodePoint(cp))
						return REPLACEMENT_CHAR;
					ch = new String(Character.toChars(cp));
				}
				else {
					byte[] indexBytes = new byte[]{
						(byte) getIndexAt(row, col) };
					ch = new String(indexBytes, encoding);

					// Check roundtrip conversion with Unicode:
					byte[] chBytes =  ch.getBytes(encoding);
					if (!Arrays.equals(indexBytes, chBytes))
						return REPLACEMENT_CHAR;
				}
			}
			catch (Exception e) {
				return REPLACEMENT_CHAR;
			}
			if ((ch == null) || (ch == "")) {
				return REPLACEMENT_CHAR;
			}

			return ch;
		}

		/**
		 * Determine, if the character at a given index in the current
		 * table is valid. A table index is invalid, if it does not
		 * correspond a valid unicode entry.
		 * This happens, if the table entry is outside the encoding range
		 * or if we are outside of the table (row == -1 or col == -1)
		 * or if it is a character from an encoding with no Unicode
		 * equivalent.
		 *
		 * @param row   Table row
		 * @param col   Table column
		 * @return      Table entry is valid Unicode entry
		 */
		public boolean isValidChar(int row, int col)
		{
			String ch = (String) getValueAt(row, col);

			if (! ch.equals(REPLACEMENT_CHAR)) return true;

			if (getIndexAt(row, col) == REPLACEMENT_CHAR.codePointAt(0))
				return true;
			else
				return false;
		}

		/**
		 * Determine name of column with given index
		 *
		 * @param  index  Column index
		 * @return        Name of column
		 */
		 @Override
		public String getColumnName(int index)
		{
			return null;
		}
	}

	/**
	 * Renderer for table cells based on the CharLabel
	 * that uses anti-aliasing if required
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 * @see CharLabel
	 */
	class CharTableCellRenderer extends CharLabel implements TableCellRenderer
	{
		/**
		 * Default constructor, use default font
		 */
		public CharTableCellRenderer()
		{
			super(null, null);
		}

		/**
		 * Method called by the table renderer to determine the
		 * component to use to render the selected table cell.
		 * Sets up the foreground and background colours,
		 * the font and the text.
		 * @param table Glyph table
		 * @param text Text contained within cell at given location
		 * @param isSelected Indicates whether cell is selected
		 * @param hasFocus Indicates whether cell has input focus
		 * @param row Row of selected cell in glyph table
		 * @param column Column of selected cell in glyph table
		 * @return Instance of the renderer (super-class of JLabel)
		 */
		public Component getTableCellRendererComponent(
			JTable table, Object text,
			boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			// Catch empty text
			if ((text == null) || ((String) text).isEmpty()) {
				setFont(normalFont());
				setBackground(jEdit.getColorProperty(
					OPTION_PREFIX + "color-normal"));
				setText(" ");
				return this;
			}

			int cp = ((String) text).codePointAt(0);

			// Set Font
			setFont(autoFont(cp));

			// Set Background
			if (Character.isISOControl(cp)) {
				setBackground(jEdit.getColorProperty(
					OPTION_PREFIX + "color-control"));
			}
			else if (Character.getType(cp) == Character.PRIVATE_USE) {
				setBackground(jEdit.getColorProperty(
					OPTION_PREFIX + "color-private"));
			}
			else if ( !((CharTableModel) table.getModel())
				.isValidChar(row, column)) {
				setBackground(jEdit.getColorProperty(
					OPTION_PREFIX + "color-invalid"));
			}
			else if (!UnicodeData.isDefined(cp)
			//else if (!Character.isDefined(cp)
				|| (Character.getType(cp) == Character.SURROGATE)) {
				setBackground(jEdit.getColorProperty(
					OPTION_PREFIX + "color-unassigned"));
			}
			else {
				setBackground(jEdit.getColorProperty(
					OPTION_PREFIX + "color-normal"));
			}

			// Set Text
			// (same for table cell, large char and super char)
			setText((String) text);

			return this;
		}
	}
	//}}}

//}}}
}

