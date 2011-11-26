/*
 * PHPErrorSource.java - The PHP Parser
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2011 Matthieu Casanova
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
package gatchan.phpparser;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import gatchan.phpparser.parser.PHPParseErrorEvent;
import gatchan.phpparser.parser.PHPParseMessageEvent;
import gatchan.phpparser.parser.PHPParserListener;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * The PHP Error source that will receive the errors from the parser and give them to the ErrorList api.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class PHPErrorSource implements PHPParserListener
{
	private DefaultErrorSource errorSource;

	/**
	 * Instantiate the PHP error source.
	 */
	public PHPErrorSource()
	{
		Log.log(Log.DEBUG, PHPErrorSource.class, "New PHPErrorSource");
	}

	public void setErrorSource(DefaultErrorSource errorSource)
	{
		this.errorSource = errorSource;
	}

	@Override
	public void parseError(PHPParseErrorEvent e)
	{
		if (e.getBeginLine() == e.getEndLine())
		{
			errorSource.addError(ErrorSource.ERROR,
				e.getPath(),
				e.getBeginLine() - 1,
				e.getBeginColumn() - 1,
				e.getEndColumn(),
				e.getMessage());
		}
		else
		{
			errorSource.addError(ErrorSource.ERROR,
				e.getPath(),
				e.getBeginLine() - 1,
				e.getBeginColumn() - 1,
				e.getBeginColumn(),
				e.getMessage());
		}
	}

	@Override
	public void parseMessage(PHPParseMessageEvent e)
	{
		if (!jEdit.getBooleanProperty("gatchan.phpparser.warnings."+e.getMessageClass()))
			return;
		errorSource.addError(ErrorSource.WARNING,
			e.getPath(),
			e.getBeginLine() - 1,
			e.getBeginColumn() - 1,
			e.getEndColumn(),
			e.getMessage());
	}
}
