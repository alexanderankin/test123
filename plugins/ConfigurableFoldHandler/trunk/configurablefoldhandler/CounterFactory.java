package configurablefoldhandler;

/*
 * CounterFactory.java
 * 
 * Copyright (c) 2002 C.J.Kent
 *
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=custom:collapseFolds=0:
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


/**
 * This class creates and returns a new implementation of {@link FoldCounter}.
 */
public class CounterFactory
{
	/**
	 * Returns a new {@link FoldCounter} instance.
	 */
	public FoldCounter getFoldCounter(String startStr, String endStr,
		boolean useRegex) throws FoldStringsException
	{
		if (useRegex)
			return new JavaRegexCounter(startStr, endStr);  
		else
			return new PlainCounter(startStr, endStr);
	}
}
