/*
 * ConfigurableHyperlink.java - The configurable Hyperlink
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 Patrick Eibl
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
package gatchan.jedit.hyperlinks.configurable;

import gatchan.jedit.hyperlinks.Hyperlink;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.BeanShell;
import java.util.regex.Matcher;
import org.gjt.sp.jedit.bsh.NameSpace;
import org.gjt.sp.util.Log;

/**
 * This hyperlink will execute beanshell code.
 *
 * @author Patrick Eibl
 */
public class ConfigurableHyperlink implements Hyperlink
{
	private final int start;

	private final int end;

	private final int startLine;

	private final int endLine;

	private final String tooltip;
	
	private final String code;
	private final Matcher matcher;
	
	public ConfigurableHyperlink(int start, int end, int line, Matcher matcher, String code, String tooltip)
	{
		this.start = start;
		this.end = end;
		this.startLine = line;
		this.endLine = line;
		this.matcher = matcher;
		this.code = code;
		this.tooltip = evalTooltip(tooltip);
	}

	@Override
	public void click(View view)
	{
		try
		{
			NameSpace ns = BeanShell.getNameSpace();
			ns.setVariable("matcher", matcher);
			for(int i=0; i <= matcher.groupCount(); i++) {
				ns.setVariable("_"+i, matcher.group(i));
			}
			BeanShell.eval(jEdit.getActiveView(), ns, code);
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, e, e);
		}
	}
	
	private String evalTooltip(String tooltip)
	{
		try
		{
			NameSpace ns = BeanShell.getNameSpace();
			ns.setVariable("matcher", matcher);
			for(int i=0; i <= matcher.groupCount(); i++) {
				ns.setVariable("_"+i, matcher.group(i));
			}
			return BeanShell.eval(jEdit.getActiveView(), ns, tooltip).toString();
		}
		catch (Exception e)
		{
			Log.log(Log.ERROR, e, e);
			return "";
		}
	}
	
	/**
	 * Returns the start offset.
	 */
	@Override
	public int getStartOffset()
	{
		return start;
	}

	/**
	 * Returns the end offset.
	 */
	@Override
	public int getEndOffset()
	{
		return end;
	}

	@Override
	public int getStartLine()
	{
		return startLine;
	}

	@Override
	public int getEndLine()
	{
		return endLine;
	}

	@Override
	public String getTooltip()
	{
		return tooltip;
	}
}
