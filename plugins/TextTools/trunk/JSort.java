/*
 * :folding=explicit:collapseFolds=1:
 *
 * JSort.java - a class to sort sets
 * Copyright (c) 2002 Carmine Lucarelli (carmine.lucarelli@rogers.com)
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


//{{{ imports
import java.util.*;
import org.gjt.sp.util.Log;
//}}}

/**
 * Main sort routines
 *
 * @author <A HREF="mailto:carmine.lucarelli@rogers.com">Carmine Lucarelli</A>
 */
public class JSort implements Comparator
{
	//{{{ private members
	/**
	  * A collection of SortBy objects (sort options)
	  */
	private Vector options;

	/**
	  * Flag to control whether we will delete duplicate entries while we sort
	  */
	private boolean deleteDuplicates;

	/**
	  *  The sorted data
	  */
	private TreeSet data;
	//}}}

	//{{{ JSort constructors
	/**
	  * Constructor initializes Vector of SortBy instances and
	  * sets deleteDuplicates property to false;
	  *
	  */
	public JSort()
	{
		this(false);
	}
	
	/**
	  * Constructor initializes Vector of SortBy instances and
	  * sets deleteDuplicates property to value passed in;
	  *
	  * @param deleteDuplicates  Should we delete duplicate lines while we sort?
	  *
	  */
	public JSort(boolean deleteDuplicates)
	{
		options = new Vector();
		this.deleteDuplicates = deleteDuplicates;
	}  //}}}

	//{{{ methods to modify sort

	//{{{ getSortBy()
	/**
	  * convenience method to return all current options for this sort
	  */
	public Vector getSortBy()
	{
		return options;
	}  //}}}

	//{{{ addSortBy(JSort.SortBy sortBy)
	public void addSortBy(JSort.SortBy sortBy)
	{
		options.add(sortBy);
	}  //}}}

	//{{{ clearSort
	/**
	  * remove all current sort options and set delete duplicates flag to false
	  */
	public void clearSort()
	{
		options = new Vector();
		deleteDuplicates = false;
	}  //}}}

	//{{{ JSort addSortConstraint
	/**
	  * Add a 'field' to sort by.  Identical to calling addSortConstraint(startColumn, endColumn, true, false, false, false)
	  *
	  * @param startColumn  The start position of the field, 1 indexed.  If less than one, will be
	  *                     modified to be one.
	  * @param endColumn  The end position of the field, inclusive and 1 indexed.  If greater than
	  *                   the length of any of the entries, will effectively be startColumn to the
	  *                   end of the entry.
	  */
	public void addSortConstraint(int startColumn, int endColumn)
	{
		addSortConstraint(startColumn, endColumn, true, false, false, false);
	}
	
	
	/**
	  * Add a 'field' to sort by.  Identical to calling addSortConstraint(startColumn, endColumn, ascending, false, false, false)
	  *
	  * @param startColumn  The start position of the field, 1 indexed.  If less than one, will be
	  *                     modified to be one.
	  * @param endColumn  The end position of the field, inclusive and 1 indexed.  If greater than
	  *                   the length of any of the entries, will effectively be startColumn to the
	  *                   end of the entry.
	  * @param ascending  True for an ascending sort, false for a descending
	  */
	public void addSortConstraint(int startColumn, int endColumn, boolean ascending)
	{
		addSortConstraint(startColumn, endColumn, ascending, false, false, false);
	}
	
	
	/**
	  * Add a 'field' to sort by.  Identical to calling addSortConstraint(startColumn, endColumn, ascending, ignoreCase, false, false)
	  *
	  * @param startColumn  The start position of the field, 1 indexed.  If less than one, will be
	  *                     modified to be one.
	  * @param endColumn  The end position of the field, inclusive and 1 indexed.  If greater than
	  *                   the length of any of the entries, will effectively be startColumn to the
	  *                   end of the entry.
	  * @param ascending  True for an ascending sort, false for a descending
	  * @param ignoreCase  True will treat 'a' and 'A' identically.
	  */
	public void addSortConstraint(int startColumn, int endColumn, boolean ascending, boolean ignoreCase)
	{
		addSortConstraint(startColumn, endColumn, ascending, ignoreCase, false);
	}
	
	
	/**
	  * Add a 'field' to sort by.  Identical to calling addSortConstraint(startColumn, endColumn, ascending, ignoreCase, numeric, false)
	  *
	  * @param startColumn  The start position of the field, 1 indexed.  If less than one, will be
	  *                     modified to be one.
	  * @param endColumn  The end position of the field, inclusive and 1 indexed.  If greater than
	  *                   the length of any of the entries, will effectively be startColumn to the
	  *                   end of the entry.
	  * @param ascending  True for an ascending sort, false for a descending
	  * @param ignoreCase  True will treat 'a' and 'A' identically.
	  * @param numeric  True will place SomeName2 before SomeName10 in the sort.
	  */
	public void addSortConstraint(int startColumn, int endColumn, boolean ascending, boolean ignoreCase, boolean numeric)
	{
		addSortConstraint(startColumn, endColumn, ascending, ignoreCase, numeric, false);
	}   //}}}

	/**
	  * Add a 'field' to sort by.  The entries are used in order.  So the first
	  * constraint is the primary sort field, the second becomes the secondary, etc.
	  *
	  * @param startColumn  The start position of the field, 1 indexed.  If less than one, will be
	  *                     modified to be one.
	  * @param endColumn  The end position of the field, inclusive and 1 indexed.  If greater than
	  *                   the length of any of the entries, will effectively be startColumn to the
	  *                   end of the entry.
	  * @param ascending  True for an ascending sort, false for a descending
	  * @param ignoreCase  True will treat 'a' and 'A' identically.
	  * @param numeric  True will place SomeName2 before SomeName10 in the sort.
	  * @param trimWhitespace  True will do a String.trim() previous to sorting (this will not trim the original
	  *                        value in the List to be sorted
	  */
	public void addSortConstraint(int startColumn, int endColumn, boolean ascending, boolean ignoreCase, boolean numeric, boolean trimWhitespace)
	{
		// column choices are 1 indexed...
		addSortBy((new JSort.SortBy(--startColumn, endColumn, ascending, ignoreCase, numeric, trimWhitespace)));
	}   //}}}

	//{{{ setDeleteDuplicates(boolean)
	public void setDeleteDuplicates(boolean deleteDuplicates)
	{
		this.deleteDuplicates = deleteDuplicates;
	}  //}}}

	//{{{ getDeleteDuplicates()
	public boolean getDeleteDuplicates()
	{
		return deleteDuplicates;
	}  //}}}

	//}}}

	//{{{ public sort methods
	
	//{{{ shuffle(List)
	/**
	  * Randomize the entries in the given collection
	  *
	  * @param list  The data to 'shuffle'
	  */
	public static void shuffle(List list) 
	{
		for(int lastPlace = list.size() - 1; lastPlace > 0; lastPlace--)
		{
			// Choose a random location from among 0,1,...,lastPlace.
			int randLoc = (int)(Math.random() * (lastPlace + 1));
			// Swap items in locations randLoc and lastPlace.
			Object o = list.set(lastPlace, list.get(randLoc));
			list.set(randLoc, o);
		}
	}  //}}}

	//{{{ sort(List)
	/**
	  * Sort the given collection, based on current sort options.
	  *
	  * @param list  The data to sort
	  */
	public void sort(List list)
	{
		if(options.size() == 0)
		{
			addSortConstraint(0, 10000);
		}

		TreeSet ts = new TreeSet(this);
		ts.addAll(list);
		list.clear();
		list.addAll(ts);
	}  //}}}
	
	//}}}

	//{{{ compare methods
	
	//{{{ compare(Object, Object)
	/**
	  * Implemenation of java.util.Comparator used in creating a sorted 
	  * collection (TreeMap implementation).  Will also delete duplicates
	  * if option is set to true.
	  *
	  * @return int  if o1 > o2, positive, 0 if same and delete duplicates option set, else negative
	  */
	public int compare(Object o1, Object o2)
	{
		// if they don't pass in strings, sort 'em anyway
		String s1 = o1.toString(), s2 = o2.toString();

		if(deleteDuplicates && s1.equals(s2))
		{
			return 0;
		}

		int retVal = 0;

		for(int i = 0; i < options.size(); i++)
		{
			SortBy sb = (SortBy)options.elementAt(i);

			if(sb.startColumn < 0)
			{
				sb.startColumn = 0;
			}
			String sub1 = null;
			String sub2 = null;
			if(sb.endColumn > s1.length())
			{
				sub1 = s1.substring(sb.startColumn, s1.length());
			}
			else
			{
				sub1 = s1.substring(sb.startColumn, sb.endColumn);
			}
			if(sb.endColumn > s2.length())
			{
				sub2 = s2.substring(sb.startColumn, s2.length());
			}
			else
			{
				sub2 = s2.substring(sb.startColumn, sb.endColumn);
			}
			if(sb.trimWhitespace)
			{
				sub1 = sub1.trim();
				sub2 = sub2.trim();
			}
			
			retVal = compare(sub1, sub2, sb);

			if(retVal == 0)
			{
				continue;
			}

			break;
		}

		if(retVal == 0)
		{
			// if we returned zero, the item wouldn't be added to the TreeSet, so
			// in an attempt to retain original order, make 'bigger'
			return 1;
		}

		return retVal;
	}  //}}}


	//{{{  compare(String, String, SortBy)
	/**
	 *********** originally copied from Slava Pestov's MiscUtilities.compareStrings(...) method ***********
	 * A more intelligent version of String.compareTo() that handles
	 * numbers specially. For example, it places "My file 2" before
	 * "My file 10".  Will also sort ascending or descending.
	 *
	 * @param str1 The first string
	 * @param str2 The second string
	 * @param sortBy The options for this sort (ascending/descending, numeric sort, ignore case)
	 * @return negative If str1 &lt; str2, 0 if both are the same,
	 * positive if str1 &gt; str2
	 */
	public int compare(String str1, String str2, SortBy sortBy)
	{
		char[] char1 = str1.toCharArray();
		char[] char2 = str2.toCharArray();

		int len = Math.min(char1.length,char2.length);

		for(int i = 0, j = 0; i < len && j < len; i++, j++)
		{
			char ch1 = char1[i];
			char ch2 = char2[j];

			if(sortBy.numeric && Character.isDigit(ch1) && Character.isDigit(ch2)
			        && ch1 != '0' && ch2 != '0')
			{
				int _i = i + 1;
				int _j = j + 1;

				for(; _i < char1.length; _i++)
				{
					if(!Character.isDigit(char1[_i]))
					{
						//_i--;
						break;
					}
				}

				for(; _j < char2.length; _j++)
				{
					if(!Character.isDigit(char2[_j]))
					{
						//_j--;
						break;
					}
				}

				int len1 = _i - i;
				int len2 = _j - j;

				if(len1 > len2)
				{
					if(sortBy.ascending)
					{
						return 1;
					}
					else
					{
						return -1;
					}
				}
				else if(len1 < len2)
				{
					if(sortBy.ascending)
					{
						return -1;
					}
					else
					{
						return 1;
					}
				}
				else
				{
					for(int k = 0; k < len1; k++)
					{
						ch1 = char1[i + k];
						ch2 = char2[j + k];

						if(ch1 != ch2)
						{
							if(sortBy.ascending)
							{
								return (ch1 - ch2);
							}
							else
							{
								return ((ch1 - ch2) * -1);
							}
						}
					}
				}

				i = _i - 1;
				j = _j - 1;
			}
			else
			{
				if(sortBy.ignoreCase)
				{
					ch1 = Character.toLowerCase(ch1);
					ch2 = Character.toLowerCase(ch2);
				}

				if(ch1 != ch2)
				{
					if(sortBy.ascending)
					{
						return (ch1 - ch2);
					}
					else
					{
						return ((ch1 - ch2) * -1);
					}
				}
			}
		}

		if(sortBy.ascending)
		{
			return (char1.length - char2.length);
		}
		else
		{
			return ((char1.length - char2.length) * -1);
		}
	}  //}}}
	
	//}}}

	//{{{ SortBy class def.
	public class SortBy
	{
		public int startColumn;
		public int endColumn;
		public boolean ascending;
		public boolean ignoreCase;
		public boolean numeric;
		public boolean trimWhitespace;

		public SortBy(int startColumn, int endColumn, boolean ascending, boolean
		              ignoreCase, boolean numeric, boolean trimWhitespace)
		{
			this.startColumn = startColumn;
			this.endColumn = endColumn;
			this.ascending = ascending;
			this.ignoreCase = ignoreCase;
			this.numeric = numeric;
			this.trimWhitespace = trimWhitespace;
		}
		
		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			sb.append("startColumn = ").append(startColumn).append(" endColumn = ").append(endColumn)
				.append(" ascending = ").append(ascending).append(" ignoreCase = ")
				.append(ignoreCase).append(" numeric = ").append(numeric).append(" trimWhitespace = ")
				.append(trimWhitespace);
			return sb.toString();
		}
	}  //}}}
}

