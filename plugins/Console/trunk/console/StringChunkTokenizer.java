/*
 * StringChunkTokenizer.java - Split long strings in small chunks
 * Copyright (C) 2001 Dirk Moebius
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

package console;


import java.util.Enumeration;
import java.util.NoSuchElementException;


/**
 * The string chunk tokenizer allows an application to break a string into
 * small chunks of a specified size.
 * <p>
 * In its simplest form, the tokenizer cuts the string into chunks of a
 * fixed size (the default is 1000), with the last chunk being smaller
 * as necessary.
 * <p>
 * In its four-argument form, the tokenizer tries to cut the string at
 * one of the specified delimiter characters. In each chunk the tokenizer
 * looks back from the end for occurrences of one of the delimiters. If found,
 * the string is cut at the delimiter. Note that only a certain amount
 * of lookback is performed, as specified with the <code>lookBack</code>
 * argument.
 *
 * @author Dirk Moebius
 * @see java.util.StringTokenizer
 */
public class StringChunkTokenizer implements Enumeration
{

	/** The default chunk size, which is 1000. */
	public static final int DEFAULT_CHUNK_SIZE = 1000;


	/**
	 * Constructs a string chunk tokenizer for the specified string.
	 * All characters in the <code>delim</code> argument are the
	 * delimiters for separating chunks. When searching for delimiters
	 * no more than <code>lookBack</code> lookups are performed.
	 *
	 * @param  str  the string to be parsed.
	 * @param  chunkSize  the chunk size; must be positive.
	 * @param  delim  the delimiters.
	 * @param  lookBack  the amount of lookback to be performed when
	 *         searching for delimiters. Must be non-negative and
	 *         smaller than <code>chunkSize</code>.
	 */
	public StringChunkTokenizer(String str, int chunkSize, String delim, int lookBack) {
		if (chunkSize <= 0)
			throw new IllegalArgumentException("chunkSize must be positive");
		if (lookBack < 0)
			throw new IllegalArgumentException("lookBack must be non-negative");
		if (chunkSize <= lookBack)
			throw new IllegalArgumentException("chunkSize must be greater than lookBack");

		this.str = str;
		this.chunkSize = chunkSize;
		this.delim = delim;
		this.lookBack = lookBack;

		this.maxPos = str.length();
		this.currPos = 0;
	}


	/**
	 * Constructs a string chunk tokenizer for the specified string.
	 * The tokenizer uses the fixed size for the chunks specified by
	 * the <code>chunkSize</code> argument. No lookup for delimiters
	 * is performed.
	 *
	 * @param  str  the string to be parsed.
	 * @param  chunkSize  the chunk size; must be positive.
	 */
	public StringChunkTokenizer(String str, int chunkSize) {
		this(str, chunkSize, null, 0);
	}


	/**
	 * Constructs a string chunk tokenizer for the specified string.
	 * The tokenizer uses the fixed DEFAULT_CHUNK_SIZE, which is 1000,
	 *
	 * @param  str  the string to be parsed.
	 */
	public StringChunkTokenizer(String str) {
		this(str, DEFAULT_CHUNK_SIZE, null, 0);
	}



	/**
	 * Returns the next chunk from this string chunk tokenizer.
	 * If delimiters have been set, they are always returned at the
	 * end of the current chunk, not at the start of the next chunk.
	 *
	 * @return  the next chunk from this tokenizer.
	 * @exception  NoSuchElementException  if there are no more chunks
	 *             in this tokenizer's string.
	 */
	public String nextToken() {
		if (currPos >= maxPos)
			throw new NoSuchElementException();

		int nextPos = findNext();
		String token = str.substring(currPos, nextPos);
		currPos = nextPos;

		return token;
	}


	/**
	 * Tests if there are more tokens available from this tokenizer's
	 * string. If this method returns <code>true</code>, then a subsequent
	 * call to <code>nextToken</code> will successfully return a token.
	 *
	 * @return  <code>true</code> if and only if there is at least one
	 *          token in the string after the current position;
	 *          <code>false</code> otherwise.
	 */
	public boolean hasMoreTokens() {
		int nextPos = findNext();
		return nextPos >= 0 && nextPos <= maxPos;
	}


	private int findNext() {
		if (currPos >= maxPos)
			return -1;

		int nextPos = currPos + chunkSize;

		if (nextPos > maxPos)
			return maxPos;  // reached the end

		if (delim == null || delim.length() == 0)
			return nextPos;  // don't search for delimiters

		// search from the end of the chunk for delemiters:
		for (int i = nextPos - 1; i >= nextPos - lookBack && i > currPos; --i)
			if (delim.indexOf(str.charAt(i)) >= 0)
				return i + 1;

		// no delimiter found in lookBack range:
		return nextPos;
	}


	// Enumeration interface:

	/**
	 * Returns the same value as the <code>nextToken</code> method,
	 * except that its declared return value is <code>Object</code>
	 * rather than <code>String</code>.
	 * It exists so that this class can implement the
	 * <code>Enumeration</code> interface.
	 *
	 * @return     the next token in the string.
	 * @exception  NoSuchElementException  if there are no more tokens
	 *             in this tokenizer's string.
	 * @see        Enumeration
	 * @see        #nextToken()
	 */
	public Object nextElement() {
		return nextToken();
	}


	/**
	 * Returns the same value as the <code>hasMoreTokens</code> method.
	 * It exists so that this class can implement the
	 * <code>Enumeration</code> interface.
	 *
	 * @return  <code>true</code> if there are more tokens;
	 *          <code>false</code> otherwise.
	 * @see     Enumeration
	 * @see     #hasMoreTokens()
	 */
	public boolean hasMoreElements() {
		return hasMoreTokens();
	}


	private String str;
	private int chunkSize;
	private String delim;
	private int lookBack;
	private int currPos;
	private int maxPos;

}

