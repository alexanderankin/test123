/*
 * JSort.java - a class to sort sets
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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


//{{{ Imports
import java.util.*;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.util.Log;
//}}}

/**
 * Main sort routines
 *
 * @author <A HREF="mailto:carmine.lucarelli@rogers.com">Carmine Lucarelli</A>
 */
public class JSort implements Comparator
{
	
	//{{{ JSort constructors
	/**
	 * Constructor initializes Vector of SortBy instances and
	 * sets properties to false;
	 *
	 */
	public JSort()
	{
		this(false, false);
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
		this(deleteDuplicates, false);
	}
	
	/**
	 * Constructor initializes Vector of SortBy instances and
	 * sets properties to values passed in;
	 *
	 * @param deleteDuplicates  Should we delete duplicate lines while we sort?
	 * @param dontSort          Should we skip sorting when deleting duplicates?
	 *
	 */
	public JSort(boolean deleteDuplicates, boolean dontSort)
	{
		options = new Vector();
		this.deleteDuplicates = deleteDuplicates;
		this.dontSort = dontSort;
	}  //}}}
	
	//{{{ getSortBy() method
	/**
	 * convenience method to return all current options for this sort
	 */
	public Vector getSortBy()
	{
		return options;
	}  //}}}
	
	//{{{ addSortBy() method
	public void addSortBy(JSort.SortBy sortBy)
	{
		options.add(sortBy);
	}  //}}}
	
	//{{{ clearSort() method
	/**
	 * remove all current sort options and set delete duplicates flag to false
	 */
	public void clearSort()
	{
		options = new Vector();
		deleteDuplicates = false;
		dontSort = false;
	}  //}}}
	
	//{{{ addSortConstraint() method
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
		addSortConstraint(startColumn, endColumn, true, false, false, false, false);
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
		addSortConstraint(startColumn, endColumn, ascending, false, false, false, false);
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
		addSortConstraint(startColumn, endColumn, ascending, ignoreCase, false, false, false);
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
		addSortConstraint(startColumn, endColumn, ascending, ignoreCase, numeric, false, false);
	}   //}}}
	
	//{{{ addSortConstraint() method
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
	public void addSortConstraint(int startColumn, int endColumn, boolean ascending, boolean ignoreCase, boolean numeric, boolean trimWhitespace, boolean delDupRange)
	{
		// column choices are 1 indexed...
		addSortBy((new JSort.SortBy(--startColumn, endColumn, ascending, ignoreCase, numeric, trimWhitespace, delDupRange)));
	} //}}}
	
	//{{{ setDeleteDuplicates() method
	public void setDeleteDuplicates(boolean deleteDuplicates)
	{
		this.deleteDuplicates = deleteDuplicates;
	} //}}}
	
	//{{{ getDeleteDuplicates() method
	public boolean getDeleteDuplicates()
	{
		return deleteDuplicates;
	} //}}}
	
	//{{{ setDontSort() method
	public void setDontSort(boolean dontSort)
	{
		this.dontSort = dontSort;
	} //}}}
	
	//{{{ getDontSort() method
	public boolean getDontSort()
	{
		return dontSort;
	} //}}}
	
	//{{{ Public Sort Methods
	
	//{{{ shuffle() method
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
	} //}}}

	//{{{ sort() method
	/**
	 * Sort the given collection, based on current sort options.
	 *
	 * @param list  The data to sort
	 */
	public void sort(List list)
	{
		if (deleteDuplicates && dontSort)
		{
			// Plugin Bugs item #1277177: delete duplicated is not covered by TreeSet
			// do an explicit implementation (ruwi)
			ArrayList newList = new ArrayList(list.size());
			for (int i=0; i<list.size(); i++)
			{
				boolean duplicatedFound = false;
				for (int j=0; j<i && !duplicatedFound; j++)
				{
					// Log.log(Log.DEBUG, BeanShell.class,"+++ deldup.8: list.get(i) = "+list.get(i)+", list.get(j) = "+list.get(j));
					if (list.get(i).toString().equals(list.get(j).toString()))
						duplicatedFound = true;
				}
				if (!duplicatedFound)
					newList.add(list.get(i));
			}
			list.clear();
			list.addAll(newList);
		}
		else
		{
			if(options.size() == 0)
			{
				addSortConstraint(0, 10000);
			}
	
			TreeSet ts = new TreeSet(this);
			ts.addAll(list);
			list.clear();
			list.addAll(ts);
		}
	}  //}}}
	
	//}}}
	
	//{{{ Compare methods
	
	//{{{ compare() method
	/**
	 * Implemenation of java.util.Comparator used in creating a sorted 
	 * collection (TreeMap implementation).  Will also delete duplicates
	 * if option is set to true.
	 *
	 * @return int  if o1 > o2, positive, 0 if same and delete duplicates option set, else negative
	 */
//	public static int dontSort = 1;
	public int compare(Object o1, Object o2)
	{
		// if they don't pass in strings, sort 'em anyway
		String s1 = o1.toString(), s2 = o2.toString();

		if(deleteDuplicates && s1.equals(s2))
			return 0;
		int retVal = 0;

		for(int i = 0; i < options.size(); i++)
		{
			SortBy sb = (SortBy)options.elementAt(i);

			if(sb.startColumn < 0)
			{
				sb.startColumn = 0;
			}
			String sub1;
			String sub2;
			if (sb.endColumn == 0) {
				sub1 = s1;
				sub2 = s2;
			} else {
				sub1 = getCompareStringForSortby(sb, s1);
				sub2 = getCompareStringForSortby(sb, s2);
			}
			if(sb.trimWhitespace)
			{
				sub1 = sub1.trim();
				sub2 = sub2.trim();
			}
			
			retVal = compare(sub1, sub2, sb);
			if (TextToolsPlugin.debugTT) Log.log(Log.DEBUG, BeanShell.class,"JSort.298: retVal = "+retVal+", sub1 = "+sub1
			+", sub2 = "+sub2+", sb = "+sb);

			if(retVal == 0)
			{
				// rwadd: delete, if section equal and deldup selected
				if (sb.delDupRange)
					return 0;
				else {
					if (dontSort) return 1;  // if no sorting required, make 'bigger'
					continue;
				}
			}
			if (dontSort) return 1;  // if no sorting required, make 'bigger'

			break;
		}

		if(retVal == 0)
		{
			// if we returned zero, the item wouldn't be added to the TreeSet, so
			// in an attempt to retain original order, make 'bigger'
			return 1;
		}

		return retVal;
	} //}}}
	
	//{{{ compare(String, String, SortBy)
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
	
	//{{{ SortBy class
	public class SortBy
	{
		public int startColumn;
		public int endColumn;
		public boolean ascending;
		public boolean ignoreCase;
		public boolean numeric;
		public boolean trimWhitespace;
		public boolean delDupRange;

		public SortBy(int startColumn, int endColumn, boolean ascending, boolean
		              ignoreCase, boolean numeric, boolean trimWhitespace, boolean delDupRange)
		{
			this.startColumn = startColumn;
			this.endColumn = endColumn;
			this.ascending = ascending;
			this.ignoreCase = ignoreCase;
			this.numeric = numeric;
			this.trimWhitespace = trimWhitespace;
			this.delDupRange = delDupRange;
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("startColumn = ").append(startColumn)
				.append(" endColumn = ").append(endColumn)
				.append(" ascending = ").append(ascending)
				.append(" ignoreCase = ").append(ignoreCase)
				.append(" numeric = ").append(numeric)
				.append(" trimWhitespace = ").append(trimWhitespace)
				.append(" delDupRange = ").append(delDupRange);
			return sb.toString();
		}
	} //}}}
	
	//{{{ getCompareStringForSortby() method
	private static String getCompareStringForSortby(SortBy sb, String compStr)
	{
		if(sb.startColumn > compStr.length()) 
			return new String();
		else {
			if(sb.endColumn > compStr.length())
			{
				return compStr.substring(sb.startColumn, compStr.length());
			}
			else
			{
				return compStr.substring(sb.startColumn, sb.endColumn);
			}
		}
	}//}}}
	
	//{{{ Private members
	/**
	  * A collection of SortBy objects (sort options)
	  */
	private Vector options;

	/**
	  * Flags to control whether we will delete duplicate entries while we sort / skip sort
	  */
	private boolean deleteDuplicates;
	private boolean dontSort;

	/**
	  *  The sorted data
	  */
	private TreeSet data;
	//}}}
}

