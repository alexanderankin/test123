// $Id$
/*
 * TemplateMode.java - A custom jEdit mode, for use by the Templates plugin.
 * Copyright (C) 2001 Steve Jakob
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

import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.syntax.*;
/**
 * A custom jEdit mode, designed specifically for use by the Templates plugin.
 */
public class TemplateMode extends Mode
{
	private String templateVarStart = "/??";
	private String templateVarEnd = "??/";
	private String optionalSectStart = "<<?";
	private String optionalSectEnd = "?>>";
	private String repeatingSectStart = "<<*";
	private String repeatingSectEnd = "*>>";

	//Constructors
	public TemplateMode() {
		super("template");
		this.init();
	}

	//Implementors
	public void init() {
		TokenMarker marker = new TokenMarker();
		ParserRuleSet rules = new ParserRuleSet();
		rules.setIgnoreCase(true);
		rules.setHighlightDigits(false);
		rules.setEscape(null);
		rules.setDefault(Token.NULL);
		rules.addRule(ParserRuleFactory.createEOLSpanRule("#ctpragma", 
				Token.COMMENT1, true, false));
		rules.addRule(ParserRuleFactory.createSequenceRule(templateVarStart, 
				Token.OPERATOR, false));
		rules.addRule(ParserRuleFactory.createSequenceRule(templateVarEnd, 
				Token.OPERATOR, false));
		rules.addRule(ParserRuleFactory.createSpanRule(optionalSectStart, 
				optionalSectEnd, Token.LABEL, true, false, false, false));
		rules.addRule(ParserRuleFactory.createSpanRule(repeatingSectStart, 
				repeatingSectEnd, Token.LABEL, true, false, false, false));
		marker.addRuleSet("templateRules", rules);
		// rules.setKeywords(keywords);
		this.setTokenMarker(marker);
	}

}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.1  2001/02/26 05:47:37  sjakob
	 * Added "Save Template" function to Templates menu.
	 * Added TemplateMode (custom mode for Templates parsing).
	 *
	 */

