/*
 * HtmlCssExporterProvider.java - part of the code2html jedit plugin
 * Copyright (C) 2009 Romain Francois
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

package code2html.services ;

import code2html.generic.GenericExporter ;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.textarea.Selection;

import code2html.impl.htmlcss.HtmlCssExporter ;

public class HtmlCssExporterProvider extends ExporterProvider {
	
	@Override
	public GenericExporter getExporter( Buffer buffer, SyntaxStyle[] syntaxStyles, Selection[] selection ){
		return new HtmlCssExporter( buffer, syntaxStyles, selection) ;
	}
	
}
