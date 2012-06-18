/*
 * escMatcher.java - central class
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012, Artem Bryantsev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
 
package jcfunc;

import java.util.regex.*;
import java.util.Map;
import java.util.EnumMap;
import java.util.TreeMap;
import java.util.ArrayList;

/**
   Central class. Its functions are:
   <ul>
   <li>&nbsp; matching control function in a text</li>
   <li>&nbsp; removing control function from the text</li>
   <li>&nbsp; parsing control functions</li>
   </ul>
   Typical invocation sequence is thus:
   <ol>
   <li>create: new escMatcher</li>
   <li>set up working set of control functions (what functions are parsing?): setPatterns()</li>
   <li>matches() and parse()</li>
   </ol>
 */
public class escMatcher
{
	//{{{ data members
	private int MATCH_MODE;
	private int COMPILE_FLAG;
	private Pattern basePattern;   // CF.CSI
	private Pattern commonPattern; // any CF
	private Pattern searchPattern; // for matches() method
	private EnumMap<CF, Pattern> workPatterns = new EnumMap<CF, Pattern>(CF.class); // all except CF.CSI
	private String DIVIDER = ";";
	private Pattern replace4spliting = Pattern.compile("[^0-9"+DIVIDER+"]");
	private CF notFound;
	//}}}
	
	//{{{ returnPattern() method
	private Pattern returnPattern(String str)
	{
		return Pattern.compile(str, COMPILE_FLAG);
	} //}}}
	
	//{{{ constructor
	/**
	 * Constructor.
	 * @param mode - parsing mode, 7-bit or 8-bit
	 * @param compileFlag - control flags for 'Pattern.compile()' method
	 */
	public escMatcher(int mode, int compileFlag)
	{
		MATCH_MODE   = mode;
		COMPILE_FLAG = compileFlag;
		
		// init generic patterns
		basePattern   = returnPattern(        Sequences.getCFPattern(CF.CSI, MATCH_MODE)     );
		commonPattern = returnPattern(        Sequences.getCommonCFPattern(MATCH_MODE, true) );
		searchPattern = returnPattern(".*?" + Sequences.getCFPattern(CF.CSI, MATCH_MODE)     );
		
		notFound = CF.NUL;
	} //}}}
	
	//{{{ setPatterns() method
	/**
	 * Creates working set of patterns
	 * @param cmds - array of control functions for parsing; if it is absent then all functions are set up
	 */
	public void setPatterns(CF... cmds)
	{
		EnumMap<CF, Sequences.Record> newMap;
		
		if (cmds == null || cmds.length == 0) {
			newMap = Sequences.generateFullWorkingSet();
			
		} else {
			newMap = Sequences.generateWorkingSet(cmds);
			
		}
		
		workPatterns.clear();
		
		boolean is8bit = (MATCH_MODE == Sequences.MODE_8BIT);
		
		for ( Map.Entry<CF, Sequences.Record> entry: newMap.entrySet() ) {
			// CF.CSI ? => ignor
			if (entry.getKey() == CF.CSI) continue;
			
			String str = is8bit ?
					entry.getValue().pattern8 :
					entry.getValue().pattern7 ;
			
			// 7bit: CF.LS0 == CF.LS1 == null
			// 8bit: CF.SI  == CF.SO  == null
			if (str != null)
				workPatterns.put(entry.getKey(), Pattern.compile(str));
			
		}
	} //}}}
	
	//{{{ matches() method
	/**
	 * This method matches input string with patterns from Working set
	 * @param inputString string for matching
	 * @return true if input string matches this matcher's pattern
	 */
	public boolean matches(String inputString)
	{
		return searchPattern.matcher(inputString).lookingAt();
	} //}}}
	
	//{{{ remove() method
	/**
	 * Remove from input string control function's sequence only.
	 * Substring which followed control function immediately is kept.
	 * @param inputString input string
	 * @param func removed control function
	 * @return string without stated control function
	 */
	public String remove(String inputString, CF func)
	{
		if ( !workPatterns.isEmpty() && workPatterns.containsKey(func) ) {
			return workPatterns.get(func).matcher(inputString).replaceAll("");
		} else {
			return inputString;
		}
	} //}}}
	
	//{{{ removeAll() method
	/**
	 * Remove from input string any control function's sequence only.
	 * Substring which followed control function immediately is kept.
	 * @param inputString input string
	 * @return string without any stated control functions
	 */
	public String removeAll(String inputString)
	{
		return commonPattern.matcher(inputString).replaceAll("");
	} //}}}
	
	//{{{ parse() method
	/**
	   Main method. It returns:
	   <ul>
	   <li>found control function (CF)</li>
	   <li>index of CF's first symbol (usually it's CF.CSI)</li>
	   <li>offset after the last character matched (after end of substring)</li>
	   <li>int-array of CF's parameters, if any</li>
	   </ul>
	   @param inputString parsed string
	   @return array list of Description's objects
	 */
	public ArrayList<Description> parse(String inputString)
	{
		return parse(inputString, false);
	} //}}}

	//{{{ parse() method
	/**
	   Main method.
	   If <code>ignorSequences</code> is set FALSE then final indecies describe substrings
	   WITH control functions and their parameters. Otherwise (TRUE) substrings
	   include substrings (data) only.
	   @param inputString parsed string
	   @param ignorSequences affects to returned indecies
	   @return array list of Description's objects
	 */
	public ArrayList<Description> parse(String inputString, boolean ignorSequences)
	{
		if (workPatterns == null || workPatterns.isEmpty() )
			return null;
		
		ArrayList<Description> returnedList = null;
		TreeMap<Integer, String> internalMap = new TreeMap<Integer, String>();
		
		/*  looks for any sequences  */
		Matcher matcher = basePattern.matcher(inputString);
		
		// result[0] - last entering in the input string
		// result[1] - first character of (sequence or data)
		// result[2] - index of character, which follows after LAST character of (sequence or data)
		int[] result = {0, 0, 0};
		int prevBegining = -1;
		while ( matcher.find() ) {
			int newBegining = matcher.start();
			
			if (prevBegining != -1) {
				internalMap.put( new Integer(prevBegining), new String( inputString.substring(prevBegining, newBegining) ) );
				
			} else {
				result[0] = newBegining;
			}
			
			prevBegining = newBegining;
		}
		if (prevBegining != -1)
			internalMap.put( new Integer(prevBegining), new String( inputString.substring(prevBegining) ) );
		
		/*  process found strings  */
		if (internalMap.size() > 0) {
			returnedList = new ArrayList<Description>( internalMap.size() );
			
			// go over strings
			for ( Map.Entry<Integer, String> keyValue: internalMap.entrySet() ) {
				
				String processingStr = keyValue.getValue();
				CF foundCF = null;
				matcher    = null;
				
				// go over patterns
				for ( Map.Entry<CF, Pattern> pattern: workPatterns.entrySet() ) {
					
					matcher = pattern.getValue().matcher(processingStr);
					
					if ( matcher.lookingAt() ) {
						foundCF = pattern.getKey();
						break;
					}
				}
				
				// it's unknowned sequence (it's absent in 'workPatterns')
				if (foundCF == null) {
					foundCF = notFound;
					matcher = null;
				}
				
				// parameters
				int[] params;
				if (matcher != null) {
					/* extracts parameters from the substring
					 *
					 *              ESC[01;34;45m_substring
					 *              ^            ^
					 *              |            |
					 *     matcher.start()  matcher.end()
					 */
					 
					// splits string to array
					int end = matcher.end();
					String[] strArr =
							replace4spliting.matcher(
								// removes CF.CSI    : "01;34;45m"
								basePattern.matcher(
									// extracts sequence : ^-- "ESC[01;34;45m"
									processingStr.substring( matcher.start(), end )
								).replaceAll("")
			                                ).replaceAll(DIVIDER).split(DIVIDER, 0);
					// converting
					params = new int[strArr.length];
					for (int i = 0; i < strArr.length; i++)
						params[i] = Integer.decode(strArr[i]);
					
				} else {
					params = new int[0];
					
				}
				
				// indexes
				result[2] = result[1] = result[0];
				if (ignorSequences) {
					result[2] += removeAll(processingStr).length();
				} else {
					result[2] += processingStr.length();
				}
				result[0] = result[2]; // shift and save last entering
				
				returnedList.add( new Description(result[1], result[2], foundCF, params) );
			}
		}
		
		return returnedList;
	} //}}}
}
