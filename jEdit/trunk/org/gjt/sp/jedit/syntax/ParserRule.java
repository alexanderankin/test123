/*
 * ParserRule.java - Parser rule for the token marker
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999 mike dillon
 * Portions copyright (C) 2002 Slava Pestov
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

package org.gjt.sp.jedit.syntax;

import gnu.regexp.*;
import org.gjt.sp.jedit.search.RESearchMatcher;

/**
 * A parser rule.
 * @author mike dillon, Slava Pestov
 * @version $Id$
 */
public class ParserRule
{
	//{{{ Major actions
	public static final int MAJOR_ACTIONS = 0x000000FF;
	public static final int SEQ = 0;
	public static final int SPAN = 1 << 1;
	public static final int MARK_PREVIOUS = 1 << 2;
	public static final int MARK_FOLLOWING = 1 << 3;
	public static final int EOL_SPAN = 1 << 4;
	//}}}

	//{{{ Action hints
	public static final int ACTION_HINTS = 0x0000FF00;
	public static final int EXCLUDE_MATCH = 1 << 8;
	public static final int NO_LINE_BREAK = 1 << 9;
	public static final int NO_WORD_BREAK = 1 << 10;
	public static final int IS_ESCAPE = 1 << 11;
	public static final int REGEXP = 1 << 12;
	//}}}

	//{{{ Position match hints
	public static final int AT_LINE_START = 1 << 1;
	public static final int AT_WHITESPACE_END = 1 << 2;
	public static final int AT_WORD_START = 1 << 3;
	//}}}

	//{{{ Instance variables
	public final char hashChar;
	public final int startPosMatch;
	public final char[] start;
	public final RE startRegexp;

	public final int endPosMatch;
	public final char[] end;

	public final int action;
	public final byte token;

	public ParserRule next;
	//}}}

	//{{{ getDelegateRuleSet() method
	/**
	 * Returns the parser rule set used to highlight text matched by this
	 * rule. Only applicable for <code>SEQ</code>, <code>SPAN</code>,
	 * <code>EOL_SPAN</code>, and <code>MARK_FOLLOWING</code> rules.
	 *
	 * @param tokenMarker The token marker
	 */
	public ParserRuleSet getDelegateRuleSet(TokenMarker tokenMarker)
	{
		// don't worry
		if(delegate == null)
		{
			if((action & MAJOR_ACTIONS) == SEQ)
				return null;
			else
				return ParserRuleSet.getStandardRuleSet(token);
		}
		else
		{
			ParserRuleSet delegateSet = tokenMarker.getRuleSet(delegate);
			if(delegateSet == null)
			{
				return ParserRuleSet.getStandardRuleSet(
					Token.NULL);
			}
			else
				return delegateSet;
		}
	} //}}}

	//{{{ createSequenceRule() method
	public static final ParserRule createSequenceRule(
		int posMatch, String seq, String delegate, byte id)
	{
		int ruleAction = SEQ;

		return new ParserRule(SEQ, seq.charAt(0),
			posMatch, seq.toCharArray(), null,
			0, null, delegate, id);
	} //}}}

	//{{{ createRegexpSequenceRule() method
	public static final ParserRule createRegexpSequenceRule(
		char hashChar, int posMatch, String seq,
		String delegate, byte id, boolean ignoreCase)
		throws REException
	{
		return new ParserRule(SEQ | REGEXP, hashChar, posMatch,
			null, new RE("\\A" + seq,(ignoreCase ? RE.REG_ICASE : 0),
			RESearchMatcher.RE_SYNTAX_JEDIT), 0,
			null, delegate, id);
	} //}}}

	//{{{ createSpanRule() method
	public static final ParserRule createSpanRule(
		int startPosMatch, String start, int endPosMatch, String end,
		String delegate, byte id, boolean excludeMatch, boolean noLineBreak,
		boolean noWordBreak)
	{
		int ruleAction = SPAN |
			((noLineBreak) ? NO_LINE_BREAK : 0) |
			((excludeMatch) ? EXCLUDE_MATCH : 0) |
			((noWordBreak) ? NO_WORD_BREAK : 0);

		return new ParserRule(ruleAction, start.charAt(0), startPosMatch,
			start.toCharArray(), null,
			endPosMatch, end.toCharArray(),
			delegate, id);
	} //}}}

	//{{{ createRegexpSpanRule() method
	public static final ParserRule createRegexpSpanRule(
		char hashChar, int startPosMatch, String start,
		int endPosMatch, String end, String delegate, byte id,
		boolean excludeMatch, boolean noLineBreak, boolean noWordBreak,
		boolean ignoreCase)
		throws REException
	{
		int ruleAction = SPAN | REGEXP |
			((noLineBreak) ? NO_LINE_BREAK : 0) |
			((excludeMatch) ? EXCLUDE_MATCH : 0) |
			((noWordBreak) ? NO_WORD_BREAK : 0);

		return new ParserRule(ruleAction, hashChar, startPosMatch, null,
			new RE("\\A" + start,(ignoreCase ? RE.REG_ICASE : 0),
			RESearchMatcher.RE_SYNTAX_JEDIT), endPosMatch,
			end.toCharArray(), delegate, id);
	} //}}}

	//{{{ createEOLSpanRule() method
	public static final ParserRule createEOLSpanRule(
		int posMatch, String seq, String delegate, byte id,
		boolean excludeMatch)
	{
		int ruleAction = EOL_SPAN |
			((excludeMatch) ? EXCLUDE_MATCH : 0)
			| NO_LINE_BREAK;

		return new ParserRule(ruleAction, seq.charAt(0), posMatch,
			seq.toCharArray(), null, 0, null,
			delegate, id);
	} //}}}

	//{{{ createRegexpEOLSpanRule() method
	public static final ParserRule createRegexpEOLSpanRule(
		char hashChar, int posMatch, String seq, String delegate,
		byte id, boolean excludeMatch, boolean ignoreCase)
		throws REException
	{
		int ruleAction = EOL_SPAN | REGEXP |
			((excludeMatch) ? EXCLUDE_MATCH : 0)
			| NO_LINE_BREAK;

		return new ParserRule(ruleAction, hashChar, posMatch,
			null, new RE("\\A" + seq,(ignoreCase ? RE.REG_ICASE : 0),
			RESearchMatcher.RE_SYNTAX_JEDIT), 0, null,
			delegate, id);
	} //}}}

	//{{{ createMarkFollowingRule() method
	public static final ParserRule createMarkFollowingRule(
		int posMatch, String seq, byte id, boolean excludeMatch)
	{
		int ruleAction = MARK_FOLLOWING |
			((excludeMatch) ? EXCLUDE_MATCH : 0);

		return new ParserRule(ruleAction, seq.charAt(0), posMatch,
			seq.toCharArray(), null, 0, null, null, id);
	} //}}}

	//{{{ createMarkPreviousRule() method
	public static final ParserRule createMarkPreviousRule(
		int posMatch, String seq, byte id, boolean excludeMatch)
	{
		int ruleAction = MARK_PREVIOUS |
			((excludeMatch) ? EXCLUDE_MATCH : 0);

		return new ParserRule(ruleAction, seq.charAt(0), posMatch,
			seq.toCharArray(), null, 0, null, null, id);
	} //}}}

	//{{{ createEscapeRule() method
	public static final ParserRule createEscapeRule(String seq)
	{
		int ruleAction = IS_ESCAPE;

		return new ParserRule(ruleAction, seq.charAt(0),
			0, seq.toCharArray(), null, 0, null,
			null, Token.NULL);
	} //}}}

	//{{{ Private members
	private String delegate;

	private ParserRule(int action, char hashChar,
		int startPosMatch, char[] start, RE startRegexp,
		int endPosMatch, char[] end,
		String delegate, byte token)
	{
		this.action = action;
		this.hashChar = hashChar;
		this.startPosMatch = startPosMatch;
		this.start = start;
		this.startRegexp = startRegexp;
		this.endPosMatch = endPosMatch;
		this.end = end;
		this.delegate = delegate;
		this.token = token;
	} //}}}
}
