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

//{{{ Imports
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import errorlist.DefaultErrorSource;
import org.gjt.sp.util.Log;
//}}}

/**
 * An abstract base class for plugin-provided parser implementations.<p>
 *
 * Plugins can provide SideKick parsers by defining entries in their
 * <code>services.xml</code> files like so:
 *
 * <pre>&lt;SERVICE CLASS="sidekick.SideKickParser" NAME="<i>name</i>"&gt;
 *    new <i>MyParser<i>();
 *&lt;/SERVICE&gt;</pre>
 *
 * See <code>org.gjt.sp.jedit.ServiceManager</code> for details.<p>
 *
 * Note that each <code>SideKickParser</code> subclass has a name which is
 * used to key a property <code>sidekick.parser.<i>name</i>.label</code>.<p>
 *
 * To associate a parser with some edit modes, define properties like this:
 * <pre>mode.scheme.sidekick.parser=lisp
 *mode.lisp.sidekick.parser=lisp</pre>
 *
 * @version $Id$
 * @author Slava Pestov
 */
public abstract class SideKickParser
{
	public static final String SERVICE = "sidekick.SideKickParser";

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

	//{{{ stop() method
	/**
	 * Stops the parse request currently in progress. It is up to the
	 * parser to implement this.
	 * @since SideKick 0.3
	 */
	public void stop()
	{
	} //}}}

	//{{{ activate() method
	/**
	 * This method is called when a buffer using this parser is selected
	 * in the specified view.
	 * @param view The view
	 * @since SideKick 0.2
	 */
	public void activate(View view)
	{
		Log.log(Log.DEBUG,this,getName() + ": activated for " + view.getBuffer());
	} //}}}

	//{{{ deactivate() method
	/**
	 * This method is called when a buffer using this parser is no longer
	 * selected in the specified view.
	 * @param view The view
	 * @since SideKick 0.2
	 */
	public void deactivate(View view)
	{
		Log.log(Log.DEBUG,this,getName() + ": deactivated");
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

	//{{{ getParseTriggers() method
	/**
	 * Returns a list of characters which trigger a buffer re-parse.
	 *
	 * Returns null by default.
	 * @since SideKick 0.3
	 *
	 */
	public String getParseTriggers()
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
