/*
 * UnicodeData.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Mike Dillon
 * Copyright (C) 2012 Max Funk
 * Generated Portions Copyright (C) Unicode, Inc.
 *                    (see charactermap/unicode/LICENSE)
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

package charactermap.unicode;


import java.util.*;
import org.gjt.sp.util.StandardUtilities;


/**
 * A utility class providing access to data structures generated from the
 * Unicode Character Database (UCD). This class provides access to a list of
 * Unicode Blocks and provides a mapping between code points and Unicode
 * character descriptions.
 */
public final class UnicodeData
{
//{{{ Block handling
		/**
	 * A simple data type representing a Unicode Block.
	 */
	public static class Block
	{
		private String name;
		private int first;
		private int last;
		private int length;
		private boolean highBlock;

		/**
		 * Construct a new block for the internal block list.
		 *
		 * @param name the Unicode description for the block.
		 * @param first the first code point in the block.
		 * @param last the last code point in the block.
		 */
		protected Block(String name, int first, int last)
		{
			this.name = name;
			this.first = first;
			this.last = last;

			length = 1 + last - first;
			highBlock = last > 0xFFFF;
		}

		/**
		 * Returns the Unicode description for the block.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Returns the first code point in this block as a Java char.
		 * This method is unsuitable for blocks outside of the Basic
		 * Multilingual Plane (Plane 0), i.e. for blocks where the code
		 * points are above the range of a Java char. If you might be
		 * working with such blocks, use <code>isHighBlock()</code> to
		 * determine if this method can be called safely.
		 *
		 * @see #isHighBlock()
		 * @throws java.lang.UnsupportedOperationException if the first
		 * code point is above the range of a Java char.
		 */
		public char getFirstChar()
		{
			if (highBlock)
			{
				throw new UnsupportedOperationException(
					"This block is above the range of a " +
					"Java char; use getFirstPoint()");
			}

			return (char)first;
		}

		/**
		 * Returns the first code point in this block.
		 */
		public int getFirstPoint()
		{
			return first;
		}

		/**
		 * Returns the last code point in this block as a Java char.
		 * This method is unsuitable for blocks outside of the Basic
		 * Multilingual Plane (Plane 0), i.e. for blocks where the code
		 * points are above the range of a Java char. If you might be
		 * working with such blocks, use <code>isHighBlock()</code> to
		 * determine if this method can be called safely.
		 *
		 * @see #isHighBlock()
		 * @throws java.lang.UnsupportedOperationException if the first
		 * code point is above the range of a Java char.
		 */
		public char getLastChar()
		{
			if (highBlock)
			{
				throw new UnsupportedOperationException(
					"This block is above the range of a " +
					"Java char; use getFirstPoint()");
			}

			return (char)last;
		}

		/**
		 * Returns the last code point in this block.
		 */
		public int getLastPoint()
		{
			return last;
		}

		/**
		 * Returns the number of code points in this block.
		 */
		public int length()
		{
			return length;
		}

		/**
		 * Returns whether this block is outside the Unicode Basic
		 * Multilingual Plane (Plane 0).
		 */
		public boolean isHighBlock()
		{
			return highBlock;
		}

		/**
		 * Returns the name of this block as its string representation.
		 */
		@Override
		public String toString()
		{
			return name;
		}
	}
//}}}

//{{{ Main class

	/**
	 * Returns the list of all defined Blocks from the Unicode Character
	 * Database.
	 */
	public static List<Block> getBlocks()
	{
		return Collections.unmodifiableList(UnicodeBlocks.getBlocks());
	}

	/**
	 * Returns the list of low defined Blocks from the Unicode Character
	 * Database.
	 */
	public static List<Block> getLowBlocks()
	{
		List<Block> lowBlockList = new ArrayList<Block>();
		for(Block I : getBlocks())
		{
			if (! I.isHighBlock())
				lowBlockList.add(I);
		}
		return Collections.unmodifiableList(lowBlockList);
	}

	/**
	 * Returns the list of all defined Blocks from the Unicode Character
	 * Database in alphabetic order.
	 */
	public static List<Block> getBlocksABC()
	{
		Block[] blockArrayABC  = new Block[getBlocks().size()];
		blockArrayABC = getBlocks().toArray(blockArrayABC);
		Arrays.sort(blockArrayABC,new StandardUtilities.StringCompare<Block>(true));
		return Collections.unmodifiableList(Arrays.asList(blockArrayABC));
	}

	/**
	 * Returns the list of low defined Blocks from the Unicode Character
	 * Database in alphabetic order.
	 */
	public static List<Block> getLowBlocksABC()
	{
		Block[] blockArrayABC  = new Block[getLowBlocks().size()];
		blockArrayABC = getLowBlocks().toArray(blockArrayABC);
		Arrays.sort(blockArrayABC,new StandardUtilities.StringCompare<Block>(true));
		return Collections.unmodifiableList(Arrays.asList(blockArrayABC));
	}

	/**
	 *  Returns the Unicode block for a given block name, or
	 *  <code>null</code> if there is no block with this name available.
	 */
	public static Block getBlock(String blockName)
	{
		Iterator<Block> I = getBlocks().iterator();
		Block B;
		while (I.hasNext()) {
			B = I.next();
			if (B.toString().equals(blockName))
				return B;
		}
		return null;
	}

	/** Remember last search result in getBlock(codepoint) */
	private static Block getBlockPrevious = UnicodeBlocks.get(0);

	/**
	 *  Returns the Unicode block of a character.
	 *  If no valid character is given, <code>null</code> is returned.
	 *
	 *  @param codePoint The codepoint of the character
	 */
	public static Block getBlock(int codePoint)
	{
		if (!Character.isValidCodePoint(codePoint)) return null;

		if ( (getBlockPrevious.getFirstPoint() <= codePoint)
			&& (codePoint <= getBlockPrevious.getLastPoint()) ) {
			return getBlockPrevious;
		}
		Iterator<Block> I = getBlocks().iterator();
		Block B;
		while (I.hasNext()) {
			B = I.next();
			if ( (B.getFirstPoint() <= codePoint)
				&& (codePoint <= B.getLastPoint()) ) {
				getBlockPrevious = B;
				return B;
			}
		}
		return null;
	}

	/** Remember last search result in getCharacterName(codePoint). **/
	private static int getCharacterNamePrevious = 0;

	/**
	 * Returns the Unicode character name for the specified code
	 * point, or <code>null</code> if there is no name available.
	 */
	public static String getCharacterName(int codePoint)
	{
		if (!Character.isValidCodePoint(codePoint)) return null;

		String name = UnicodeCharNames.get(Integer.valueOf(codePoint));

		boolean nameIsFirst = (name != null && name.endsWith("First>"));
		boolean nameIsLast = (name != null && name.endsWith("Last>"));

		// Return chars with standard name
		if (name != null && !nameIsFirst && !nameIsLast)
			return name;

		// Return chars named "..First>" and "..Last>".
		if (nameIsFirst)
			return (name.substring(1, name.trim().length() - 8)
				+ "-" + Integer.toHexString(codePoint).toUpperCase());
		if (nameIsLast)
			return (name.substring(1, name.trim().length() - 7)
				+ "-" + Integer.toHexString(codePoint).toUpperCase());

		// Return empty chars not from first-last block
		Block B = getBlock(codePoint);
		if (B == null) return null;
		name = UnicodeCharNames.get(Integer.valueOf(B.getFirstPoint()));
		if ((name == null) || (!name.endsWith("First>"))) return null;

		// Return chars between first and last
		int getCharLast;
		if ((B.getFirstPoint() <= getCharacterNamePrevious)
			&& (getCharacterNamePrevious <= B.getLastPoint())) {
			getCharLast = getCharacterNamePrevious;
		}
		else {
			getCharLast = B.getLastPoint();
			String tempName;
			while (getCharLast > B.getFirstPoint()) {
				tempName = UnicodeCharNames.get(Integer.valueOf(getCharLast));
				if ((tempName != null) && tempName.endsWith("Last>"))
					break;
				getCharLast--;
			}
			getCharacterNamePrevious = getCharLast;
		}
		if (codePoint < getCharLast)
			return (name.substring(1, name.trim().length() - 8)
				+ "-" + Integer.toHexString(codePoint).toUpperCase());

		// Char is behind last
		return null;
	}

	/** Remember previous search result of isDefined(codePoint) */
	private static int isDefinedPrevious = 0;

	/**
	 *  Character assignment in the data list.
	 *  A character is unassigned, if it has an empty name.
	 *  @param codePoint Codepoint of the Character
	 *  @return          Character unassigned
	 */
	public static boolean isDefined(int codePoint)
	{
		// The function is equivalent to getCharacterName == null
		// but rewritten to be faster

		if (!Character.isValidCodePoint(codePoint)) return false;

		String name = UnicodeCharNames.get(Integer.valueOf(codePoint));

		// names != null => Character is defined
		if (name != null) return true;

		// Empty chars not from first-last block => undefined
		Block B = getBlock(codePoint);
		if (B == null) return false;
		name = UnicodeCharNames.get(Integer.valueOf(B.getFirstPoint()));
		if ((name == null) || (!name.endsWith("First>"))) return false;

		// Chars between first and last => defined
		int getCharLast;
		if ((B.getFirstPoint() <= isDefinedPrevious)
			&& (isDefinedPrevious <= B.getLastPoint())) {
			getCharLast = isDefinedPrevious;
		}
		else {
			getCharLast = B.getLastPoint();
			String tempName;
			while (getCharLast > B.getFirstPoint()) {
				tempName = UnicodeCharNames.get(Integer.valueOf(getCharLast));
				if ((tempName != null) && tempName.endsWith("Last>"))
					break;
				getCharLast--;
			}
			isDefinedPrevious = getCharLast;
		}
		if (codePoint < getCharLast)
			return true;

		// Char is behind last => undefined
		return false;
	}

//}}}
}


//{{{ Unicode Block Data
/**
 * Class containing the Unicode Blocks imported from the
 * Unicode Database and auxiliary functions
 */
final class UnicodeBlocks
{
	/*
	 * Below is a list of Unicode Blocks generated from the UCD file
	 * Blocks.txt using the parse_unicode_data.pl script. All blocks below
	 * a cutoff value defined in the script are included in the generated
	 * code.
	 *
	 * Do not edit these definitions by hand.
	 */


// BEGIN GENERATED CODE: Blocks.txt, cutoff=0x10FFFF
private static final List<UnicodeData.Block> blocks = Arrays.asList(new UnicodeData.Block[] {
	new UnicodeData.Block("Basic Latin", 0x0000, 0x007F),
	new UnicodeData.Block("Latin-1 Supplement", 0x0080, 0x00FF),
	new UnicodeData.Block("Latin Extended-A", 0x0100, 0x017F),
	new UnicodeData.Block("Latin Extended-B", 0x0180, 0x024F),
	new UnicodeData.Block("IPA Extensions", 0x0250, 0x02AF),
	new UnicodeData.Block("Spacing Modifier Letters", 0x02B0, 0x02FF),
	new UnicodeData.Block("Combining Diacritical Marks", 0x0300, 0x036F),
	new UnicodeData.Block("Greek and Coptic", 0x0370, 0x03FF),
	new UnicodeData.Block("Cyrillic", 0x0400, 0x04FF),
	new UnicodeData.Block("Cyrillic Supplement", 0x0500, 0x052F),
	new UnicodeData.Block("Armenian", 0x0530, 0x058F),
	new UnicodeData.Block("Hebrew", 0x0590, 0x05FF),
	new UnicodeData.Block("Arabic", 0x0600, 0x06FF),
	new UnicodeData.Block("Syriac", 0x0700, 0x074F),
	new UnicodeData.Block("Arabic Supplement", 0x0750, 0x077F),
	new UnicodeData.Block("Thaana", 0x0780, 0x07BF),
	new UnicodeData.Block("NKo", 0x07C0, 0x07FF),
	new UnicodeData.Block("Samaritan", 0x0800, 0x083F),
	new UnicodeData.Block("Mandaic", 0x0840, 0x085F),
	new UnicodeData.Block("Syriac Supplement", 0x0860, 0x086F),
	new UnicodeData.Block("Arabic Extended-A", 0x08A0, 0x08FF),
	new UnicodeData.Block("Devanagari", 0x0900, 0x097F),
	new UnicodeData.Block("Bengali", 0x0980, 0x09FF),
	new UnicodeData.Block("Gurmukhi", 0x0A00, 0x0A7F),
	new UnicodeData.Block("Gujarati", 0x0A80, 0x0AFF),
	new UnicodeData.Block("Oriya", 0x0B00, 0x0B7F),
	new UnicodeData.Block("Tamil", 0x0B80, 0x0BFF),
	new UnicodeData.Block("Telugu", 0x0C00, 0x0C7F),
	new UnicodeData.Block("Kannada", 0x0C80, 0x0CFF),
	new UnicodeData.Block("Malayalam", 0x0D00, 0x0D7F),
	new UnicodeData.Block("Sinhala", 0x0D80, 0x0DFF),
	new UnicodeData.Block("Thai", 0x0E00, 0x0E7F),
	new UnicodeData.Block("Lao", 0x0E80, 0x0EFF),
	new UnicodeData.Block("Tibetan", 0x0F00, 0x0FFF),
	new UnicodeData.Block("Myanmar", 0x1000, 0x109F),
	new UnicodeData.Block("Georgian", 0x10A0, 0x10FF),
	new UnicodeData.Block("Hangul Jamo", 0x1100, 0x11FF),
	new UnicodeData.Block("Ethiopic", 0x1200, 0x137F),
	new UnicodeData.Block("Ethiopic Supplement", 0x1380, 0x139F),
	new UnicodeData.Block("Cherokee", 0x13A0, 0x13FF),
	new UnicodeData.Block("Unified Canadian Aboriginal Syllabics", 0x1400, 0x167F),
	new UnicodeData.Block("Ogham", 0x1680, 0x169F),
	new UnicodeData.Block("Runic", 0x16A0, 0x16FF),
	new UnicodeData.Block("Tagalog", 0x1700, 0x171F),
	new UnicodeData.Block("Hanunoo", 0x1720, 0x173F),
	new UnicodeData.Block("Buhid", 0x1740, 0x175F),
	new UnicodeData.Block("Tagbanwa", 0x1760, 0x177F),
	new UnicodeData.Block("Khmer", 0x1780, 0x17FF),
	new UnicodeData.Block("Mongolian", 0x1800, 0x18AF),
	new UnicodeData.Block("Unified Canadian Aboriginal Syllabics Extended", 0x18B0, 0x18FF),
	new UnicodeData.Block("Limbu", 0x1900, 0x194F),
	new UnicodeData.Block("Tai Le", 0x1950, 0x197F),
	new UnicodeData.Block("New Tai Lue", 0x1980, 0x19DF),
	new UnicodeData.Block("Khmer Symbols", 0x19E0, 0x19FF),
	new UnicodeData.Block("Buginese", 0x1A00, 0x1A1F),
	new UnicodeData.Block("Tai Tham", 0x1A20, 0x1AAF),
	new UnicodeData.Block("Combining Diacritical Marks Extended", 0x1AB0, 0x1AFF),
	new UnicodeData.Block("Balinese", 0x1B00, 0x1B7F),
	new UnicodeData.Block("Sundanese", 0x1B80, 0x1BBF),
	new UnicodeData.Block("Batak", 0x1BC0, 0x1BFF),
	new UnicodeData.Block("Lepcha", 0x1C00, 0x1C4F),
	new UnicodeData.Block("Ol Chiki", 0x1C50, 0x1C7F),
	new UnicodeData.Block("Cyrillic Extended-C", 0x1C80, 0x1C8F),
	new UnicodeData.Block("Georgian Extended", 0x1C90, 0x1CBF),
	new UnicodeData.Block("Sundanese Supplement", 0x1CC0, 0x1CCF),
	new UnicodeData.Block("Vedic Extensions", 0x1CD0, 0x1CFF),
	new UnicodeData.Block("Phonetic Extensions", 0x1D00, 0x1D7F),
	new UnicodeData.Block("Phonetic Extensions Supplement", 0x1D80, 0x1DBF),
	new UnicodeData.Block("Combining Diacritical Marks Supplement", 0x1DC0, 0x1DFF),
	new UnicodeData.Block("Latin Extended Additional", 0x1E00, 0x1EFF),
	new UnicodeData.Block("Greek Extended", 0x1F00, 0x1FFF),
	new UnicodeData.Block("General Punctuation", 0x2000, 0x206F),
	new UnicodeData.Block("Superscripts and Subscripts", 0x2070, 0x209F),
	new UnicodeData.Block("Currency Symbols", 0x20A0, 0x20CF),
	new UnicodeData.Block("Combining Diacritical Marks for Symbols", 0x20D0, 0x20FF),
	new UnicodeData.Block("Letterlike Symbols", 0x2100, 0x214F),
	new UnicodeData.Block("Number Forms", 0x2150, 0x218F),
	new UnicodeData.Block("Arrows", 0x2190, 0x21FF),
	new UnicodeData.Block("Mathematical Operators", 0x2200, 0x22FF),
	new UnicodeData.Block("Miscellaneous Technical", 0x2300, 0x23FF),
	new UnicodeData.Block("Control Pictures", 0x2400, 0x243F),
	new UnicodeData.Block("Optical Character Recognition", 0x2440, 0x245F),
	new UnicodeData.Block("Enclosed Alphanumerics", 0x2460, 0x24FF),
	new UnicodeData.Block("Box Drawing", 0x2500, 0x257F),
	new UnicodeData.Block("Block Elements", 0x2580, 0x259F),
	new UnicodeData.Block("Geometric Shapes", 0x25A0, 0x25FF),
	new UnicodeData.Block("Miscellaneous Symbols", 0x2600, 0x26FF),
	new UnicodeData.Block("Dingbats", 0x2700, 0x27BF),
	new UnicodeData.Block("Miscellaneous Mathematical Symbols-A", 0x27C0, 0x27EF),
	new UnicodeData.Block("Supplemental Arrows-A", 0x27F0, 0x27FF),
	new UnicodeData.Block("Braille Patterns", 0x2800, 0x28FF),
	new UnicodeData.Block("Supplemental Arrows-B", 0x2900, 0x297F),
	new UnicodeData.Block("Miscellaneous Mathematical Symbols-B", 0x2980, 0x29FF),
	new UnicodeData.Block("Supplemental Mathematical Operators", 0x2A00, 0x2AFF),
	new UnicodeData.Block("Miscellaneous Symbols and Arrows", 0x2B00, 0x2BFF),
	new UnicodeData.Block("Glagolitic", 0x2C00, 0x2C5F),
	new UnicodeData.Block("Latin Extended-C", 0x2C60, 0x2C7F),
	new UnicodeData.Block("Coptic", 0x2C80, 0x2CFF),
	new UnicodeData.Block("Georgian Supplement", 0x2D00, 0x2D2F),
	new UnicodeData.Block("Tifinagh", 0x2D30, 0x2D7F),
	new UnicodeData.Block("Ethiopic Extended", 0x2D80, 0x2DDF),
	new UnicodeData.Block("Cyrillic Extended-A", 0x2DE0, 0x2DFF),
	new UnicodeData.Block("Supplemental Punctuation", 0x2E00, 0x2E7F),
	new UnicodeData.Block("CJK Radicals Supplement", 0x2E80, 0x2EFF),
	new UnicodeData.Block("Kangxi Radicals", 0x2F00, 0x2FDF),
	new UnicodeData.Block("Ideographic Description Characters", 0x2FF0, 0x2FFF),
	new UnicodeData.Block("CJK Symbols and Punctuation", 0x3000, 0x303F),
	new UnicodeData.Block("Hiragana", 0x3040, 0x309F),
	new UnicodeData.Block("Katakana", 0x30A0, 0x30FF),
	new UnicodeData.Block("Bopomofo", 0x3100, 0x312F),
	new UnicodeData.Block("Hangul Compatibility Jamo", 0x3130, 0x318F),
	new UnicodeData.Block("Kanbun", 0x3190, 0x319F),
	new UnicodeData.Block("Bopomofo Extended", 0x31A0, 0x31BF),
	new UnicodeData.Block("CJK Strokes", 0x31C0, 0x31EF),
	new UnicodeData.Block("Katakana Phonetic Extensions", 0x31F0, 0x31FF),
	new UnicodeData.Block("Enclosed CJK Letters and Months", 0x3200, 0x32FF),
	new UnicodeData.Block("CJK Compatibility", 0x3300, 0x33FF),
	new UnicodeData.Block("CJK Unified Ideographs Extension A", 0x3400, 0x4DBF),
	new UnicodeData.Block("Yijing Hexagram Symbols", 0x4DC0, 0x4DFF),
	new UnicodeData.Block("CJK Unified Ideographs", 0x4E00, 0x9FFF),
	new UnicodeData.Block("Yi Syllables", 0xA000, 0xA48F),
	new UnicodeData.Block("Yi Radicals", 0xA490, 0xA4CF),
	new UnicodeData.Block("Lisu", 0xA4D0, 0xA4FF),
	new UnicodeData.Block("Vai", 0xA500, 0xA63F),
	new UnicodeData.Block("Cyrillic Extended-B", 0xA640, 0xA69F),
	new UnicodeData.Block("Bamum", 0xA6A0, 0xA6FF),
	new UnicodeData.Block("Modifier Tone Letters", 0xA700, 0xA71F),
	new UnicodeData.Block("Latin Extended-D", 0xA720, 0xA7FF),
	new UnicodeData.Block("Syloti Nagri", 0xA800, 0xA82F),
	new UnicodeData.Block("Common Indic Number Forms", 0xA830, 0xA83F),
	new UnicodeData.Block("Phags-pa", 0xA840, 0xA87F),
	new UnicodeData.Block("Saurashtra", 0xA880, 0xA8DF),
	new UnicodeData.Block("Devanagari Extended", 0xA8E0, 0xA8FF),
	new UnicodeData.Block("Kayah Li", 0xA900, 0xA92F),
	new UnicodeData.Block("Rejang", 0xA930, 0xA95F),
	new UnicodeData.Block("Hangul Jamo Extended-A", 0xA960, 0xA97F),
	new UnicodeData.Block("Javanese", 0xA980, 0xA9DF),
	new UnicodeData.Block("Myanmar Extended-B", 0xA9E0, 0xA9FF),
	new UnicodeData.Block("Cham", 0xAA00, 0xAA5F),
	new UnicodeData.Block("Myanmar Extended-A", 0xAA60, 0xAA7F),
	new UnicodeData.Block("Tai Viet", 0xAA80, 0xAADF),
	new UnicodeData.Block("Meetei Mayek Extensions", 0xAAE0, 0xAAFF),
	new UnicodeData.Block("Ethiopic Extended-A", 0xAB00, 0xAB2F),
	new UnicodeData.Block("Latin Extended-E", 0xAB30, 0xAB6F),
	new UnicodeData.Block("Cherokee Supplement", 0xAB70, 0xABBF),
	new UnicodeData.Block("Meetei Mayek", 0xABC0, 0xABFF),
	new UnicodeData.Block("Hangul Syllables", 0xAC00, 0xD7AF),
	new UnicodeData.Block("Hangul Jamo Extended-B", 0xD7B0, 0xD7FF),
	new UnicodeData.Block("High Surrogates", 0xD800, 0xDB7F),
	new UnicodeData.Block("High Private Use Surrogates", 0xDB80, 0xDBFF),
	new UnicodeData.Block("Low Surrogates", 0xDC00, 0xDFFF),
	new UnicodeData.Block("Private Use Area", 0xE000, 0xF8FF),
	new UnicodeData.Block("CJK Compatibility Ideographs", 0xF900, 0xFAFF),
	new UnicodeData.Block("Alphabetic Presentation Forms", 0xFB00, 0xFB4F),
	new UnicodeData.Block("Arabic Presentation Forms-A", 0xFB50, 0xFDFF),
	new UnicodeData.Block("Variation Selectors", 0xFE00, 0xFE0F),
	new UnicodeData.Block("Vertical Forms", 0xFE10, 0xFE1F),
	new UnicodeData.Block("Combining Half Marks", 0xFE20, 0xFE2F),
	new UnicodeData.Block("CJK Compatibility Forms", 0xFE30, 0xFE4F),
	new UnicodeData.Block("Small Form Variants", 0xFE50, 0xFE6F),
	new UnicodeData.Block("Arabic Presentation Forms-B", 0xFE70, 0xFEFF),
	new UnicodeData.Block("Halfwidth and Fullwidth Forms", 0xFF00, 0xFFEF),
	new UnicodeData.Block("Specials", 0xFFF0, 0xFFFF),
	new UnicodeData.Block("Linear B Syllabary", 0x10000, 0x1007F),
	new UnicodeData.Block("Linear B Ideograms", 0x10080, 0x100FF),
	new UnicodeData.Block("Aegean Numbers", 0x10100, 0x1013F),
	new UnicodeData.Block("Ancient Greek Numbers", 0x10140, 0x1018F),
	new UnicodeData.Block("Ancient Symbols", 0x10190, 0x101CF),
	new UnicodeData.Block("Phaistos Disc", 0x101D0, 0x101FF),
	new UnicodeData.Block("Lycian", 0x10280, 0x1029F),
	new UnicodeData.Block("Carian", 0x102A0, 0x102DF),
	new UnicodeData.Block("Coptic Epact Numbers", 0x102E0, 0x102FF),
	new UnicodeData.Block("Old Italic", 0x10300, 0x1032F),
	new UnicodeData.Block("Gothic", 0x10330, 0x1034F),
	new UnicodeData.Block("Old Permic", 0x10350, 0x1037F),
	new UnicodeData.Block("Ugaritic", 0x10380, 0x1039F),
	new UnicodeData.Block("Old Persian", 0x103A0, 0x103DF),
	new UnicodeData.Block("Deseret", 0x10400, 0x1044F),
	new UnicodeData.Block("Shavian", 0x10450, 0x1047F),
	new UnicodeData.Block("Osmanya", 0x10480, 0x104AF),
	new UnicodeData.Block("Osage", 0x104B0, 0x104FF),
	new UnicodeData.Block("Elbasan", 0x10500, 0x1052F),
	new UnicodeData.Block("Caucasian Albanian", 0x10530, 0x1056F),
	new UnicodeData.Block("Linear A", 0x10600, 0x1077F),
	new UnicodeData.Block("Cypriot Syllabary", 0x10800, 0x1083F),
	new UnicodeData.Block("Imperial Aramaic", 0x10840, 0x1085F),
	new UnicodeData.Block("Palmyrene", 0x10860, 0x1087F),
	new UnicodeData.Block("Nabataean", 0x10880, 0x108AF),
	new UnicodeData.Block("Hatran", 0x108E0, 0x108FF),
	new UnicodeData.Block("Phoenician", 0x10900, 0x1091F),
	new UnicodeData.Block("Lydian", 0x10920, 0x1093F),
	new UnicodeData.Block("Meroitic Hieroglyphs", 0x10980, 0x1099F),
	new UnicodeData.Block("Meroitic Cursive", 0x109A0, 0x109FF),
	new UnicodeData.Block("Kharoshthi", 0x10A00, 0x10A5F),
	new UnicodeData.Block("Old South Arabian", 0x10A60, 0x10A7F),
	new UnicodeData.Block("Old North Arabian", 0x10A80, 0x10A9F),
	new UnicodeData.Block("Manichaean", 0x10AC0, 0x10AFF),
	new UnicodeData.Block("Avestan", 0x10B00, 0x10B3F),
	new UnicodeData.Block("Inscriptional Parthian", 0x10B40, 0x10B5F),
	new UnicodeData.Block("Inscriptional Pahlavi", 0x10B60, 0x10B7F),
	new UnicodeData.Block("Psalter Pahlavi", 0x10B80, 0x10BAF),
	new UnicodeData.Block("Old Turkic", 0x10C00, 0x10C4F),
	new UnicodeData.Block("Old Hungarian", 0x10C80, 0x10CFF),
	new UnicodeData.Block("Hanifi Rohingya", 0x10D00, 0x10D3F),
	new UnicodeData.Block("Rumi Numeral Symbols", 0x10E60, 0x10E7F),
	new UnicodeData.Block("Yezidi", 0x10E80, 0x10EBF),
	new UnicodeData.Block("Old Sogdian", 0x10F00, 0x10F2F),
	new UnicodeData.Block("Sogdian", 0x10F30, 0x10F6F),
	new UnicodeData.Block("Chorasmian", 0x10FB0, 0x10FDF),
	new UnicodeData.Block("Elymaic", 0x10FE0, 0x10FFF),
	new UnicodeData.Block("Brahmi", 0x11000, 0x1107F),
	new UnicodeData.Block("Kaithi", 0x11080, 0x110CF),
	new UnicodeData.Block("Sora Sompeng", 0x110D0, 0x110FF),
	new UnicodeData.Block("Chakma", 0x11100, 0x1114F),
	new UnicodeData.Block("Mahajani", 0x11150, 0x1117F),
	new UnicodeData.Block("Sharada", 0x11180, 0x111DF),
	new UnicodeData.Block("Sinhala Archaic Numbers", 0x111E0, 0x111FF),
	new UnicodeData.Block("Khojki", 0x11200, 0x1124F),
	new UnicodeData.Block("Multani", 0x11280, 0x112AF),
	new UnicodeData.Block("Khudawadi", 0x112B0, 0x112FF),
	new UnicodeData.Block("Grantha", 0x11300, 0x1137F),
	new UnicodeData.Block("Newa", 0x11400, 0x1147F),
	new UnicodeData.Block("Tirhuta", 0x11480, 0x114DF),
	new UnicodeData.Block("Siddham", 0x11580, 0x115FF),
	new UnicodeData.Block("Modi", 0x11600, 0x1165F),
	new UnicodeData.Block("Mongolian Supplement", 0x11660, 0x1167F),
	new UnicodeData.Block("Takri", 0x11680, 0x116CF),
	new UnicodeData.Block("Ahom", 0x11700, 0x1173F),
	new UnicodeData.Block("Dogra", 0x11800, 0x1184F),
	new UnicodeData.Block("Warang Citi", 0x118A0, 0x118FF),
	new UnicodeData.Block("Dives Akuru", 0x11900, 0x1195F),
	new UnicodeData.Block("Nandinagari", 0x119A0, 0x119FF),
	new UnicodeData.Block("Zanabazar Square", 0x11A00, 0x11A4F),
	new UnicodeData.Block("Soyombo", 0x11A50, 0x11AAF),
	new UnicodeData.Block("Pau Cin Hau", 0x11AC0, 0x11AFF),
	new UnicodeData.Block("Bhaiksuki", 0x11C00, 0x11C6F),
	new UnicodeData.Block("Marchen", 0x11C70, 0x11CBF),
	new UnicodeData.Block("Masaram Gondi", 0x11D00, 0x11D5F),
	new UnicodeData.Block("Gunjala Gondi", 0x11D60, 0x11DAF),
	new UnicodeData.Block("Makasar", 0x11EE0, 0x11EFF),
	new UnicodeData.Block("Lisu Supplement", 0x11FB0, 0x11FBF),
	new UnicodeData.Block("Tamil Supplement", 0x11FC0, 0x11FFF),
	new UnicodeData.Block("Cuneiform", 0x12000, 0x123FF),
	new UnicodeData.Block("Cuneiform Numbers and Punctuation", 0x12400, 0x1247F),
	new UnicodeData.Block("Early Dynastic Cuneiform", 0x12480, 0x1254F),
	new UnicodeData.Block("Egyptian Hieroglyphs", 0x13000, 0x1342F),
	new UnicodeData.Block("Egyptian Hieroglyph Format Controls", 0x13430, 0x1343F),
	new UnicodeData.Block("Anatolian Hieroglyphs", 0x14400, 0x1467F),
	new UnicodeData.Block("Bamum Supplement", 0x16800, 0x16A3F),
	new UnicodeData.Block("Mro", 0x16A40, 0x16A6F),
	new UnicodeData.Block("Bassa Vah", 0x16AD0, 0x16AFF),
	new UnicodeData.Block("Pahawh Hmong", 0x16B00, 0x16B8F),
	new UnicodeData.Block("Medefaidrin", 0x16E40, 0x16E9F),
	new UnicodeData.Block("Miao", 0x16F00, 0x16F9F),
	new UnicodeData.Block("Ideographic Symbols and Punctuation", 0x16FE0, 0x16FFF),
	new UnicodeData.Block("Tangut", 0x17000, 0x187FF),
	new UnicodeData.Block("Tangut Components", 0x18800, 0x18AFF),
	new UnicodeData.Block("Khitan Small Script", 0x18B00, 0x18CFF),
	new UnicodeData.Block("Tangut Supplement", 0x18D00, 0x18D8F),
	new UnicodeData.Block("Kana Supplement", 0x1B000, 0x1B0FF),
	new UnicodeData.Block("Kana Extended-A", 0x1B100, 0x1B12F),
	new UnicodeData.Block("Small Kana Extension", 0x1B130, 0x1B16F),
	new UnicodeData.Block("Nushu", 0x1B170, 0x1B2FF),
	new UnicodeData.Block("Duployan", 0x1BC00, 0x1BC9F),
	new UnicodeData.Block("Shorthand Format Controls", 0x1BCA0, 0x1BCAF),
	new UnicodeData.Block("Byzantine Musical Symbols", 0x1D000, 0x1D0FF),
	new UnicodeData.Block("Musical Symbols", 0x1D100, 0x1D1FF),
	new UnicodeData.Block("Ancient Greek Musical Notation", 0x1D200, 0x1D24F),
	new UnicodeData.Block("Mayan Numerals", 0x1D2E0, 0x1D2FF),
	new UnicodeData.Block("Tai Xuan Jing Symbols", 0x1D300, 0x1D35F),
	new UnicodeData.Block("Counting Rod Numerals", 0x1D360, 0x1D37F),
	new UnicodeData.Block("Mathematical Alphanumeric Symbols", 0x1D400, 0x1D7FF),
	new UnicodeData.Block("Sutton SignWriting", 0x1D800, 0x1DAAF),
	new UnicodeData.Block("Glagolitic Supplement", 0x1E000, 0x1E02F),
	new UnicodeData.Block("Nyiakeng Puachue Hmong", 0x1E100, 0x1E14F),
	new UnicodeData.Block("Wancho", 0x1E2C0, 0x1E2FF),
	new UnicodeData.Block("Mende Kikakui", 0x1E800, 0x1E8DF),
	new UnicodeData.Block("Adlam", 0x1E900, 0x1E95F),
	new UnicodeData.Block("Indic Siyaq Numbers", 0x1EC70, 0x1ECBF),
	new UnicodeData.Block("Ottoman Siyaq Numbers", 0x1ED00, 0x1ED4F),
	new UnicodeData.Block("Arabic Mathematical Alphabetic Symbols", 0x1EE00, 0x1EEFF),
	new UnicodeData.Block("Mahjong Tiles", 0x1F000, 0x1F02F),
	new UnicodeData.Block("Domino Tiles", 0x1F030, 0x1F09F),
	new UnicodeData.Block("Playing Cards", 0x1F0A0, 0x1F0FF),
	new UnicodeData.Block("Enclosed Alphanumeric Supplement", 0x1F100, 0x1F1FF),
	new UnicodeData.Block("Enclosed Ideographic Supplement", 0x1F200, 0x1F2FF),
	new UnicodeData.Block("Miscellaneous Symbols and Pictographs", 0x1F300, 0x1F5FF),
	new UnicodeData.Block("Emoticons", 0x1F600, 0x1F64F),
	new UnicodeData.Block("Ornamental Dingbats", 0x1F650, 0x1F67F),
	new UnicodeData.Block("Transport and Map Symbols", 0x1F680, 0x1F6FF),
	new UnicodeData.Block("Alchemical Symbols", 0x1F700, 0x1F77F),
	new UnicodeData.Block("Geometric Shapes Extended", 0x1F780, 0x1F7FF),
	new UnicodeData.Block("Supplemental Arrows-C", 0x1F800, 0x1F8FF),
	new UnicodeData.Block("Supplemental Symbols and Pictographs", 0x1F900, 0x1F9FF),
	new UnicodeData.Block("Chess Symbols", 0x1FA00, 0x1FA6F),
	new UnicodeData.Block("Symbols and Pictographs Extended-A", 0x1FA70, 0x1FAFF),
	new UnicodeData.Block("Symbols for Legacy Computing", 0x1FB00, 0x1FBFF),
	new UnicodeData.Block("CJK Unified Ideographs Extension B", 0x20000, 0x2A6DF),
	new UnicodeData.Block("CJK Unified Ideographs Extension C", 0x2A700, 0x2B73F),
	new UnicodeData.Block("CJK Unified Ideographs Extension D", 0x2B740, 0x2B81F),
	new UnicodeData.Block("CJK Unified Ideographs Extension E", 0x2B820, 0x2CEAF),
	new UnicodeData.Block("CJK Unified Ideographs Extension F", 0x2CEB0, 0x2EBEF),
	new UnicodeData.Block("CJK Compatibility Ideographs Supplement", 0x2F800, 0x2FA1F),
	new UnicodeData.Block("CJK Unified Ideographs Extension G", 0x30000, 0x3134F),
	new UnicodeData.Block("Tags", 0xE0000, 0xE007F),
	new UnicodeData.Block("Variation Selectors Supplement", 0xE0100, 0xE01EF),
	new UnicodeData.Block("Supplementary Private Use Area-A", 0xF0000, 0xFFFFF),
	new UnicodeData.Block("Supplementary Private Use Area-B", 0x100000, 0x10FFFF),
});
// END GENERATED CODE

	/**
	 * Empty default constructor. The class should be called only with its
	 * static methods.
	 */
	UnicodeBlocks()
	{
	}

	/**
	 Returns the Unicode Block List
	 */
	static List<UnicodeData.Block>  getBlocks()
	{
		return blocks;
	}

	/**
	 Returns an element from the List with the specified index
	 */
	static UnicodeData.Block get(int index)
	{
		return blocks.get(index);
	}
}
//}}}


//{{{ Unicode Name Data
/**
 * Wrapper class for the Unicode Character Names from Java builtin function
 */
final class UnicodeCharNames
{
	/**
	 * Empty default constructor. The class should be called only with its
	 * static methods.
	 */
	UnicodeCharNames()
	{
	}

	/**
	 * Returns the name for the specified code point. Returns
	 * <code>null</code> if the map contains no mapping for this key.
	 */
	static String get(int codePoint)
	{
		if ( Character.isValidCodePoint(codePoint) ) {
			return Character.getName(codePoint);
		}
		else {
			return null;
		}
	}
}
//}}}

