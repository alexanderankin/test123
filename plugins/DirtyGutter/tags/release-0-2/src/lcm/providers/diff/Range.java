/*
 * Range - A range of changed lines.
 *
 * Copyright (C) 2009 Shlomy Reinstein
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

package lcm.providers.diff;

@SuppressWarnings("unchecked")
public class Range implements Comparable
{
	public int first, last;
	enum ChangeType { ADDED, REMOVED, CHANGED, NONE };
	ChangeType type;

	public Range(int first, int num)
	{
		this(first, num, ChangeType.ADDED);
	}
	public Range(int first, int num, ChangeType type)
	{
		this.first = first;
		last = first + num - 1;
		this.type = type;
	}
	public int compareTo(Object arg0)
	{
		Range other = (Range) arg0;
		/* This check enables using TreeSet<Range>.contains() to find if:
		 * - a 1-line range is in the set (any overlapping range contains it).
		 * - a multi-line range overlaps with a range in the set.
		 */
		if (overlaps(other))
			return 0;
		if (first < other.first)
			return (-1);
		if (first > other.first)
			return 1;
		return 0;
	}
	// Returns true if this range overlaps with 'other'.
	public boolean overlaps(Range other)
	{
		/* There are three overlap cases:
		 * 1. First line of this range is inside 'other'.
		 * 2. Last line of this range is inside 'other'.
		 * 3. 'other' is an absolute sub-range of this range.
		 * Case (1) is covered by the first line below.
		 * Cases (2) & (3) are identified by the first line of this range
		 * preceding 'other', and the last line inside 'other' or
		 * following it. 
		 */
		return  (((other.first >= first) && (other.first <= last)) ||
			 ((other.first < first) && (other.last >= first)));
	}
}