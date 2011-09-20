/*
 *  CharacterMapPlugin.java
 *  Copyright (C) 2000, 2001, 2002 Slava Pestov
 *  Copyright (C) 2003 Mark Wickens
 *  Copyright (C) 2011 Max Funk
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
 
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.gjt.sp.jedit.*;

import charactermap.unicode.UnicodeData;
import charactermap.unicode.UnicodeData.Block;

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
public class CharacterMap extends JPanel
{
	/** JEdit view */
	private View view;
	/** Combox box for font encoding information */
	private JComboBox encodingCombo;
	/** Selected encoding */
	private String encoding;
	/** Table containing character glyphs */
	private JTable table;
	/** Status information */
	private JTextArea status;
	/** Font size used to draw the large glyph image */
	private float largeSize;
	/** Font size used to draw the super-glyph */
	private float superSize;
	/** Component displaying large glyph */
	private JLabel largeChar;
	/** Large display of selected glyph in table */
	private boolean showLarge;
	/** Display all glyphs anti-aliased */
	private boolean antiAlias;
	/** Show the super-glyph */
	private boolean showSuper;
	/** Offset the super-glyph rendering from the selected glyph */
	private boolean offsetSuper;
	/** Use fractional font-metrics in glyph display */
	private boolean fracFontMetrics;
	/** Component displaying super glyph */
	private JLabel superChar;
	/** Current display graphics configuration */
	private GraphicsConfiguration graphConfig;
        /** Current System Fonts */
        private Font[] systemFonts;
	/** Reference to the character map panel */
	private JPanel thisPanel;
	/** Number of columns in the glyph table */
	private int tableColumns;
	/** Display the selected unicode page/glyph information */
	private boolean showStatus;
	/** Display the encoding combo box */
	private boolean showEncoding;
	/** Display the unicode pager */
	private boolean showBlocks;
	/** Encodings contained within the combo box */
	private DefaultComboBoxModel encodings;
	/** Lock anti-aliasing on */
	private boolean alwaysAntiAlias;
	/** Table model for the character map */
	private CharacterMapModel tableModel;
	/** Combo-box for displaying/moving between Unicode pages */
	private JComboBox blocks;
	
	/**
	 * Construct a character map panel using the contents of the current buffer of
	 * the given view as the default encoding, and the font of the given
	 * view to render the character table. Aliasing options set on the
	 * view are honoured within the character map. Selectable options are set from
	 * properties as defined within the options pane.
	 *
	 * @param  view  jEdit view
	 * @see          CharacterMapOptions
	 */
	public CharacterMap(View view)
	{
		super(new BorderLayout(12, 12));

		alwaysAntiAlias = jEdit.getBooleanProperty("options.character-map.anti-alias");
		determineAntiAliasRequirements();

		this.view = view;
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		// Upper Menu Line (Encoding Selector, Unicode Block Selector, Char)
		
		JPanel encodingBox = new JPanel();
		encodingBox.setLayout(gridbag);
		
		JLabel caption = new JLabel(jEdit.getProperty("character-map.encoding-caption"));

		c.weighty = 0.0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		if (isDockedLeftRight()) c.insets = new Insets(5,4,5,4);
		else c.insets = new Insets(0, 4, 0, 4);
		gridbag.setConstraints(caption, c);

		encodings = new DefaultComboBoxModel();
		StringTokenizer st = new StringTokenizer(jEdit.getProperty("encodings"));
		while (st.hasMoreTokens()) {
			encodings.addElement(st.nextToken());
		}

		encodingCombo = new JComboBox(encodings);
		encodingCombo.setEditable(true);
		encodingCombo.setSelectedItem(view.getBuffer().getStringProperty(Buffer.ENCODING));
		encodingCombo.addActionListener(new ActionHandler());

		if (isDockedLeftRight()) c.gridwidth = GridBagConstraints.REMAINDER;		
		gridbag.setConstraints(encodingCombo, c);
		
		showEncoding = jEdit.getBooleanProperty("options.character-map.encoding");
		if (showEncoding)
		{
			encodingBox.add(caption);
			encodingBox.add(encodingCombo);
		}

		if (!isDockedLeftRight()) encodingBox.add(Box.createHorizontalStrut(12));
                                                                                                              
		caption = new JLabel(jEdit.getProperty("character-map.blocks-caption"));

		c.gridwidth = 1;
		gridbag.setConstraints(caption,c);

		blocks = new JComboBox(UnicodeData.getBlocks().toArray());
		blocks.addItemListener(new ItemHandler());

		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		if (isDockedLeftRight()) c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(blocks, c);
		
		showBlocks = jEdit.getBooleanProperty("options.character-map.blocks");
		if (showBlocks)
		{
			encodingBox.add(caption);
			encodingBox.add(blocks);
		}
		
		JLabel charCaption = new JLabel(jEdit.getProperty("character-map.char-caption"));
		c.gridwidth=1;
		c.weightx = 0.0;
		if (isDockedLeftRight()) c.anchor = GridBagConstraints.WEST;
		else c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(charCaption, c);

		largeSize = (float) jEdit.getIntegerProperty("options.character-map.large-size", 36);
		largeChar = new AntiAliasingLabel(largeFont(" "), " ");
		Dimension largeCharSz = largeChar.getPreferredSize();
		largeCharSz.width *= 3;
		largeChar.setMinimumSize(largeCharSz);
		largeChar.setPreferredSize(largeCharSz);
		gridbag.setConstraints(largeChar, c);

		showLarge = jEdit.getBooleanProperty("options.character-map.large");
		if (showLarge) {
			encodingBox.add(charCaption);
			encodingBox.add(largeChar);
		}

		superSize = (float) jEdit.getIntegerProperty("options.character-map.super-size", 128);
		superChar = new AntiAliasingLabel(superFont(" "), " ");
		superChar.setBorder(BorderFactory.createLineBorder(Color.black));
		showSuper = jEdit.getBooleanProperty("options.character-map.super");
		offsetSuper = jEdit.getBooleanProperty("options.character-map.super-offset");

		add(BorderLayout.NORTH, encodingBox);

		encoding = (String) encodingCombo.getSelectedItem();
		blocks.setEnabled(isEncodingUnicode());

		// Middle Part (Character Table)
		
		if (isDockedLeftRight()) {
			tableColumns = jEdit.getIntegerProperty("options.character-map.columns-dock-lr", 8); 
		}
		else if (isDockedTopBottom()) {
			tableColumns = jEdit.getIntegerProperty("options.character-map.columns-dock-tb", 32); 
		}	
		else {
			tableColumns = jEdit.getIntegerProperty("options.character-map.columns", 16);
		}

		tableModel = new CharacterMapModel();
		
		table = new JTable(tableModel);
		table.setFont(normalFont());
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		MouseHandler mouseHandler = new MouseHandler(this, superChar);
		table.addMouseListener(mouseHandler);
		table.addMouseMotionListener(mouseHandler);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setDefaultRenderer(Object.class, new AntiAliasingRenderer());

		add(BorderLayout.CENTER, new JScrollPane(table));

		// Lower Part (Status Line)
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(gridbag);

		status = new JTextArea(" ");
		status.setEditable(false);
		status.setOpaque(false);
		status.setFont(new Font("Monospaced", Font.PLAIN, status.getFont().getSize()));

		/* status = new AntiAliasingLabel(" ");
		status.setOpaque(false);
		status.setHorizontalAlignment(SwingConstants.LEFT);
		status.setHorizontalTextPosition(SwingConstants.LEFT);
		status.setVerticalAlignment(SwingConstants.CENTER);
		status.setVerticalTextPosition(SwingConstants.CENTER);
		status.setFont(font); */
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(status, c);

		showStatus = jEdit.getBooleanProperty("options.character-map.status");
		if (showStatus) {
			southPanel.add(status);
			add(BorderLayout.SOUTH, southPanel);
		}
		
		table.repaint();
		graphConfig = table.getGraphicsConfiguration();
	}

	/**
	 * Set the value of the status string, based on the selected
	 * character and table position. If the encoding is not unicode
	 * or utf8 then the position is reported as a character and the
	 * position of the character in the 256 character table. If unicode
	 * or utf8, the position includes a page indicator. If the given
	 * character is null, there is no selected character and therefore
	 * only page information is displayed.
	 *
	 * @param  ch      The selected character, or null if none
	 * @param  row     The table row of the selected character
	 * @param  column  The table column of the selected character
	 */
	private void setStatusText(String ch, int row, int column)
	{
		int num;

		int n = row * tableColumns + column + getBlockOffset();
		
		if (ch == null) {
			status.setText(" ");
		}
		else {
			StringBuilder buf = new StringBuilder();
						
			// buf.append(" Char: ").append(ch);
			
			if (!isEncodingUnicode())
			{
				buf.append(toDecString(n,3," Dec: "));
				buf.append(toHexString(n,2,true," Hex: 0x"));
			}
			else
			{
				buf.append(toDecString(n,5," Dec: "));
				buf.append(toHexString(n,4,true," Hex: 0x"));
			}

			if (encoding.toUpperCase().startsWith("UTF")
			 || encoding.toUpperCase().startsWith("X-UTF"))
			{
				if (isDockedLeftRight()) buf.append("\n");

				if (encoding.contains("8"))
				{
					buf.append(" UTF-8: ");
					if (n < 128)
						buf.append(toHexString(n, 2, true, "0x"));
					else if (n < 2048)
					{
						int b1 = 192;
						int b2 = 128;
						b1 += n >> 6;
						b2 += n & 63;
						buf.append(toHexString(b1, 2, true, "0x"));
						buf.append(toHexString(b2, 2, true, " 0x"));
					}
					else
					{
						int b1 = 224;
						int b2 = 128;
						int b3 = 128;				
						b1 += n >> 12;
						b2 += (n >> 6) & 63;
						b3 += n & 63;
						buf.append(toHexString(b1, 2, true, "0x"));
						buf.append(toHexString(b2, 2, true, " 0x"));
						buf.append(toHexString(b3, 2, true, " 0x"));
					}

				}
				else if (encoding.contains("16"))
				{
					if (encoding.contains("LE"))
					{
						buf.append(" UTF-16LE: ");
						int b1 = n & 0xFF;
						int b2 = (n & 0xFF00) >> 8; 
						buf.append(toHexString(b1, 2, true, "0x"));
						buf.append(toHexString(b2, 2, true, " 0x"));
					}
					else // Big endian
					{
						buf.append(" UTF-16BE: ");
						int b1 = (n & 0xFF00) >> 8;
						int b2 = n & 0xFF; 
						buf.append(toHexString(b1, 2, true, "0x"));
						buf.append(toHexString(b2, 2, true, " 0x"));
					}
				}
				else if (encoding.contains("32"))
				{
					if (encoding.contains("LE"))
					{
						buf.append(" UTF-32LE: ");
						int b1 = n & 0xFF;
						int b2 = (n & 0xFF00) >> 8; 
						buf.append(toHexString(b1, 2, true, "0x"));
						buf.append(toHexString(b2, 2, true, " 0x"));
						buf.append(" 0x00 0x00");
					}
					else // Big endian
					{
						buf.append(" UTF-32BE: ");
						int b1 = (n & 0xFF00) >> 8;
						int b2 = n & 0xFF; 
						buf.append("0x00 0x00");
						buf.append(toHexString(b1, 2, true, " 0x"));
						buf.append(toHexString(b2, 2, true, " 0x"));
					}
				}
			}
			if (isEncodingUnicode())
			{
				if (isDockedLeftRight()) buf.append("\n");
				String name =
					UnicodeData.getCharacterName(n);
				if (name != null)
				{
					buf.append(" Name: ");
					buf.append(name);
				}
			}

			status.setText(buf.toString());
		}
	}
	
	
	/** Formatted output of int in hexadecimal form
	 *  @param i       Integer to be converted
	 *  @param digits  Minimum number of digits 
	 *                 (fill with leading zeros)
	 *  @param upper   true: Uppercase digits; else: Lowercase
	 *  @param prefix  Prefix string
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
	 *  @param digits  Minimum number of digits 
	 *                 (fill with leading zeros)
	 *  @param prefix  Prefix string
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

	/** Font used to draw character glyphs in table */
	private Font normalFont()
	{
		return view.getTextArea().getPainter().getFont();
	}
	
	/** Font used to draw character glyphs in table.
	 *  By default, normalFont() is returned.
	 *  Automatic font substitution for missing characters, 
	 *  if this feature is selected in jEdit Options.
	 *  First, user-defined substitution fonts are checked,
	 *  then system fonts (as in jEdit).
	 *  @param text Text string to be drawn
	 *  @see org.gjt.sp.jedit.syntax.Chunk
	 */
	private Font autoFont(String text)
	{
		Font f = normalFont();

		// If normal font sufficient or no font substitution
		// -> return normal font
		if ( (f.canDisplayUpTo(text) == -1)
		   || !jEdit.getBooleanProperty("view.enableFontSubst") )
	    	{
	    		return f;
	    	}
		

		// search user defined substitution fonts
		int i = 0;
		String family;
		Font candidate;
		while ((family = jEdit.getProperty("view.fontSubstList." + i)) != null)	
		{ 
			candidate = new Font(family, Font.PLAIN, f.getSize());
			if (candidate.canDisplayUpTo(text) == -1) 
			{
				return candidate;
			}
			i++;
		}
		
		// search system fonts

		// Disabled due to following reasons: 
		// - there are differences of the shown characters
		//   jEdit 4.4.1 bug: Not always switching back to first font
		//   in order
		// - perhaps the user should know, which font he is using 
		//   if he inserts characters from the CharacterMap
		
		/* if (systemFonts == null)
		{
			systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		}		
		for (Font k : systemFonts)
		{
			candidate = k.deriveFont(Font.PLAIN, f.getSize());
			if (candidate.canDisplayUpTo(text) == -1)
			{
				return candidate;
			}
		} */
		
                // if nothing found
		return normalFont();
	}
	
	/** Font used to draw the large glyph image.
	 * Otherwise same as autoFont function
	 * @param text Text string to be drawn
	 */
	private Font largeFont(String text)
	{
	   return autoFont(text).deriveFont(largeSize);
	}


	/** Font used to draw the super large glyph image. 
	 * Otherwise same as autoFont function
	 * @param text Text string to be drawn
	 */
	private Font superFont(String text)
	{
	   return autoFont(text).deriveFont(superSize);
	}
	

	/**
	 * Set member variables regarding anti-aliasing and
	 * fractional font-metrics requirements from the defined
	 * values in the jEdit properties
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

	private boolean isDockedLeftRight()
	{
		String position = jEdit.getProperty("character-map.dock-position","float");
		return "left".equalsIgnoreCase(position)
		    || "right".equalsIgnoreCase(position);
	}

	private boolean isDockedTopBottom()
	{
		String position = jEdit.getProperty("character-map.dock-position","float");
		return "top".equalsIgnoreCase(position)
		    || "bottom".equalsIgnoreCase(position);
	}

	private boolean isEncodingUnicode()
	{
		return encoding.toUpperCase().startsWith("UNICODE") 
                    || encoding.toUpperCase().startsWith("UTF")
                    || encoding.toUpperCase().startsWith("X-UTF");
	}

	private int getBlockSize()
	{
		if (isEncodingUnicode())
		{
			Block block = (Block)blocks.getSelectedItem();
			return block.length();
		}
		else
		{
			return 256;
		}
	}

	private int getBlockOffset()
	{
		if (isEncodingUnicode())
		{
			Block block = (Block)blocks.getSelectedItem();
			return block.getFirstPoint();
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Model of character data contained within the
	 * glyph table.
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 */
	class CharacterMapModel extends AbstractTableModel
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
		 * Generates a string containing the character stored within
		 * the table at the given row and column. If the encoding is
		 * unicode or utf8 this depends on the current page value
		 *
		 * @param  row  Row of table containing required character
		 * @param  col  Column of table containing required character
		 * @return      String containing character representation of
		 * glyph stored within glyph table at given row
		 */
		public Object getValueAt(int row, int col)
		{
			int cell = row * tableColumns + col;
			
			if (isEncodingUnicode()) 
			{
				int offset = getBlockOffset();
				return String.valueOf((char) (offset + cell));
			}
			else {
				try {
					return new String(new byte[]{
						(byte) cell }, encoding);
				}
				catch (UnsupportedEncodingException ue) {
					return "?";
				}
			}
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
	 *  Handles actions performed on the encoding combo-box
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 */
	class ActionHandler implements ActionListener
	{
		/**
		 * Called when the encoding value in the encoding combo-box
		 * is changed. The encoding is stored, the page slider is
		 * enabled if the encoding set is unicode or utf8 and the
		 * table is repainted. If the encoding has been entered 
		 * manually it is stored in the combo box list for later
		 * reference in this session.
		 *
		 * @param  evt  The event representing the action performed
		 */
		public void actionPerformed(ActionEvent evt)
		{
			encoding = (String) encodingCombo.getSelectedItem();
			blocks.setEnabled(isEncodingUnicode());
			if (encodings.getIndexOf(encoding) < 0)
			{
				encodings.addElement(encoding);
			}
			tableModel.fireTableDataChanged();
			table.repaint();
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
		 * @param  evt  The event representing the state change
		 */
		public void stateChanged(ChangeEvent evt)
		{
			setStatusText(null, 0, 0);
			table.repaint();
		}
	}


	class ItemHandler implements ItemListener
	{
		public void itemStateChanged(final ItemEvent evt)
		{
			tableModel.fireTableDataChanged();
			table.repaint();
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
				if (row == -1 || column == -1) {
					setStatusText(null, 0, 0);
					largeChar.setText(" ");
				}
				else {
					String ch = getChar(row, column);
					view.getTextArea().setSelectedText(ch);
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
				Point p = evt.getPoint();
				row = table.rowAtPoint(p);
				column = table.columnAtPoint(p);
				if (row != -1 && column != -1) {
					if (showSuper) {
						//Log.log(Log.MESSAGE, this, "Got mousePressed at x = " + p.x + ", y = " + p.y);
						String ch = getChar(row, column);
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
					setStatusText(null, 0, 0);
					largeChar.setText(" ");
				}
				else {
					String ch = getChar(row, column);
					superChar.setFont(superFont(ch));
					superChar.setText(ch);
					largeChar.setFont(largeFont(ch));
					largeChar.setText(ch);
					setStatusText(ch, row, column);
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
					setStatusText(null, 0, 0);
					largeChar.setText(" ");
				}
				else {
					String ch = getChar(row, column);
					largeChar.setFont(largeFont(ch));
					largeChar.setText(ch);
					setStatusText(ch, row, column);
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
			setStatusText(null, 0, 0);
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
				/*
				 *  + tcm.getColumnMargin()
				 */
					;
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
			int rtn = 0;
			TableColumnModel tcm = table.getColumnModel();
			TableColumn tc = tcm.getColumn(column);
			rtn = tc.getWidth();
			return rtn;
		}

		/**
		 * Get the character of the glyph in the given table position
		 *
		 * @param  row     Row of the glyph table containing the character
		 * @param  column  Column of the glyph table containing the character
		 * @return         If the table model does not contain and data for the
		 *                 given row and column, return a space, otherwise
		 *                 return the glyph at the given position as a
		 *                 one-character string.
		 */
		private String getChar(int row, int column)
		{
			String rtn = (String) table.getModel().getValueAt(row, column);
			if (rtn == null || rtn.equals("")) {
				rtn = " ";
			}
			return rtn;
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
			//Log.log(Log.MESSAGE, this, "ownerLoc.x = " + ownerLoc.x + ", ownerLoc.y = " + ownerLoc.y);
			int displayX = ownerLoc.x + x;
			int displayY = ownerLoc.y + y;

			//Log.log(Log.MESSAGE, this, "before displayX = " + displayX + ", displayY = " + displayY);

			int popupWidth = contents.getWidth();
			int popupHeight = contents.getHeight();

			//Log.log(Log.MESSAGE, this, "popupWidth = " + popupWidth + ", popupHeight = " + popupHeight);

			displayX -= (popupWidth / 2);
			//displayY += (popupHeight / 2);

			//Log.log(Log.MESSAGE, this, "after displayX = " + displayX + ", displayY = " + displayY);

			if (offsetSuper) {
				int rowHeight = table.getRowHeight();
				int columnWidth = getColumnWidth(column);
				displayX += (columnWidth / 2) + (popupWidth / 2); 
				displayY -= (rowHeight / 2) + (popupHeight / 2);

				GraphicsConfiguration gf = CharacterMap.this.getGraphicsConfiguration();
				Rectangle bounds = gf.getBounds();
				int screenWidth = bounds.x + bounds.width;
				//Log.log(Log.MESSAGE, this, "bounds = " + bounds + ", displayX + popupWidth = " + (displayX + popupWidth));
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

	/**
	 * JLabel with anti-aliasing rendering turned on if required
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 */
	class AntiAliasingLabel extends JLabel
	{
		/** Map containing hints for the renderer (eg, render anti-aliased) */
		private Map<RenderingHints.Key,Object> renderingHints;
	 		 
		/**
		 * Construct a label with the given font and text
		 *
		 * @param  font  The font to render label value in
		 * @param  text  The text of the label
		 */
		public AntiAliasingLabel(Font font, String text)
		{
			super();
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
			if (font != null) {
				setFont(font);
			}
			if (text != null) {
				setText(text);
			}
			else {
				setText("");
			}
			setBackground(Color.white);
			setOpaque(true);
			setForeground(Color.black);
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
	}

	/**
	 * Renderer for table cells based on the AntiAliasingLabel
	 * that uses anti-aliasing if required
	 *
	 * @author     mawic
	 * @created    June 11, 2003
	 * @see AntiAliasingLabel
	 */
	class AntiAliasingRenderer extends AntiAliasingLabel implements TableCellRenderer
	{
		/**
		 * Default constructor, use default font 
		 */
		public AntiAliasingRenderer()
		{
			super(null, null);
		}

		/**
		 * Method called by the table renderer to determine the 
		 * component to use to render the selected table cell.
		 * Sets up the foreground and background colours, sets the 
		 * text to render then returns the instance of the renderer
		 * (which is a super-class of JLabel).
		 * @param table Glyph table
		 * @param text Text contained within cell at given location
		 * @param isSelected Indicates whether cell is selected
		 * @param hasFocus Indicates whether cell has input focus
		 * @param row Row of selected cell in glyph table
		 * @param column Column of selected cell in glyph table
		 */
		public Component getTableCellRendererComponent(
			JTable table, Object text,
			boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			setForeground(Color.black);
			setBackground(Color.white);
			setFont(autoFont((String) text));
			setText((String) text);
			return this;
		}
	}
}

