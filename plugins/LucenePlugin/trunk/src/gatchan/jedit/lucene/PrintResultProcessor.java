/*
 * PrintResultProcessor.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
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

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.document.Document;
import org.gjt.sp.util.Log;

/**
 * @author Matthieu Casanova
 */
public class PrintResultProcessor implements ResultProcessor
{
	public boolean process(ScoreDoc scoreDoc, Document document)
	{
		Log.log(Log.MESSAGE, this, document.getField("path").stringValue() + " score:" + scoreDoc.score);
		return true;
	}
}
