// $Id$
/*
 * TemplateAction.java - Extends BeanShellAction which itself is a jEdit
 * EditAction to respond to requests for a specific template.
 * Copyright (C) 2002 Steve Jakob
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
package templates;

import org.gjt.sp.jedit.BeanShellAction;
import org.gjt.sp.util.Log;
/**
 * This class is a specialized version of BeanShellAction for use with the
 * Templates plugin. By using this class to encapsulate a template selection,
 * it becomes possible to recored that selection in a macro.
 */
public class TemplateAction extends BeanShellAction
{
	private static String actionLabel = "Templates.process-template";
	private static String code1 = "templates.TemplatesPlugin.processTemplate(\"";
	private static String code2 = "\", textArea);";

	//Constructors
	public TemplateAction(String label, String filepath) {
		super(actionLabel,
				code1 + filepath.replace('\\','/') + code2,
				null,
				false,
				false);
	}
	
	public TemplateAction(TemplateFile file) {
		this(file.getLabel(), file.getPath());
	}
	
	/**
	 * Over-ride EditAction.getLabel() which requires a property
	 */
	public String getLabel() {
		return actionLabel;
	}

}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.1  2002/04/30 19:26:10  sjakob
	 * Integrated Calvin Yu's Velocity plugin into Templates to support dynamic templates.
	 *
	 * Revision 1.2  2002/03/10 02:04:32  sjakob
	 * BUGFIX: Template selection resulted in a BeanShell exception on Windows
	 * platforms, as BeanShell objected to the backslash character.
	 *
	 * Revision 1.1  2002/02/22 02:31:41  sjakob
	 * Added TemplateAction, a subclass of BeanShellAction, to handle menu
	 * selection, and allow these selections to be recorded in macros.
	 *
	 */

