/*
 * SideKickParser.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
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

package sidekick;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import errorlist.DefaultErrorSource;

/**
 * An abstract base class for plugin-provided parser implementations.
 *
 * Note that each <code>SideKickParser</code> subclass has a name which is
 * used to key a property <code>sidekick.parser.<i>name</i>.label</code>.
 * The parser name can also be referenced in a <code>sidekick.parser</code>
 * buffer-local property.
 *
 * @version $Id$
 * @author Slava Pestov
 */
public abstract class SideKickParser
{
	//{{{ SideKickParser constructor
	/**
	 * The parser constructor.
	 *
	 */
	public SideKickParser(String name)
	{
		this.name = name;
	} //}}}

	//{{{ getName() method
	/**
	 * Returns the parser's name.
	 */
	public final String getName()
	{
		return name;
	} //}}}

	//{{{ parse() method
	/**
	 * Parses the given text and returns a tree model.
	 *
	 * @param buffer The buffer to parse.
	 * @param errorSource An error source to add errors to.
	 *
	 * @return A new instance of the <code>SideKickParsedData</code> class.
	 */
	public abstract SideKickParsedData parse(Buffer buffer,
		DefaultErrorSource errorSource);
	//}}}

	//{{{ supportsCompletion() method
	/**
	 * Returns if the parser supports code completion.
	 *
	 * Returns false by default.
	 */
	public boolean supportsCompletion()
	{
		return false;
	} //}}}

	//{{{ getDelayCompletionTriggers() method
	/**
	 * Returns a list of characters which trigger completion if they
	 * are followed by a short period of inactivity.
	 *
	 * Returns null by default.
	 *
	 */
	public String getDelayCompletionTriggers()
	{
		return null;
	} //}}}

	//{{{ getInstantCompletionTriggers() method
	/**
	 * Returns a list of characters which trigger completion immediately.
	 *
	 * Returns null by default.
	 *
	 */
	public String getInstantCompletionTriggers()
	{
		return null;
	} //}}}

	//{{{ complete() method
	/**
	 * Returns completions suitable for insertion at the specified position.
	 *
	 * Returns null by default.
	 *
	 * @param editPane The edit pane involved.
	 * @param caret The caret position.
	 */
	public SideKickCompletion complete(EditPane editPane, int caret)
	{
		return null;
	} //}}}

	private String name;
}
