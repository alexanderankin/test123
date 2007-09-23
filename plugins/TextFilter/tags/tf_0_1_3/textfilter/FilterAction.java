/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package textfilter;

//{{{ Imports
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
//}}}

/**
 *  Defines an edit action to filter text through an external program.
 *
 *	@author		<A HREF="mailto:vanzin@ece.utexas.edu">Marcelo Vanzin</A>
 *  @version	0.1
 */
public final class FilterAction extends EditAction {

	private static final String ACTION_NAME_PREFIX = "textfilter.actions.";

	//{{{ Private members
	private String program;
	private int source;
	private int howToSend;
	private int returnType;
	//}}}

	//{{{ +FilterAction(String, String, int, int, int) : <init>
	public FilterAction(String name, String program, int howToSend, int source, int returnType) {
		super(ACTION_NAME_PREFIX + name);
		jEdit.setTemporaryProperty(ACTION_NAME_PREFIX + name + ".label", name);
		this.program = program;
		this.source = source;
		this.howToSend = howToSend;
		this.returnType = returnType;
	} //}}}

	//{{{ +invoke(View) : void
	public void invoke(View view) {
		ApplicationRunner.runApp(view, program, source, howToSend, returnType);
	} //}}}

	//{{{ +getCode() : String
	public String getCode() {
		return null;
	} //}}}

	//{{{ +getDataSourceType() : int
	public int getDataSourceType() {
		return howToSend;
	} //}}}

	//{{{ +getTextSource() : int
	public int getTextSource() {
		return source;
	} //}}}

	//{{{ +getDestination() : int
	public int getDestination() {
		return returnType;
	} //}}}

	//{{{ +getCommand() : String
	public String getCommand() {
		return program;
	} //}}}

}

