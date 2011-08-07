/*
 * SearchFormatter.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Shlomy Reinstein
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
package gatchan.jedit.lucene;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;
import org.gjt.sp.util.IntegerArray;

public class SearchFormatter implements Formatter
{
	private final IntegerArray positions;
	private final int max;

	public SearchFormatter(IntegerArray positions, int max)
	{
		this.positions = positions;
		this.max = max;
	}
	public String highlightTerm(String originalText,
		TokenGroup tokenGroup)
	{
        if ((positions.getSize() < max) &&
        	(tokenGroup.getTotalScore() > 0))
        {
        	positions.add(tokenGroup.getStartOffset());
        	positions.add(tokenGroup.getEndOffset());
        }
		return originalText;
	}
}