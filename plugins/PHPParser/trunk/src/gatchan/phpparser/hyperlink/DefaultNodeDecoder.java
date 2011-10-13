/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
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

package gatchan.phpparser.hyperlink;

import java.util.ArrayList;
import java.util.List;

import gatchan.jedit.hyperlinks.Hyperlink;
import net.sourceforge.phpdt.internal.compiler.ast.AstNode;
import org.gjt.sp.jedit.Buffer;

/**
 * @author Matthieu Casanova
 */
public class DefaultNodeDecoder implements HyperlinkDecoder<AstNode>
{
	private List<HyperlinkDecoder> decoders;

	public DefaultNodeDecoder()
	{
		decoders = new ArrayList<HyperlinkDecoder>();
		decoders.add(new FunctionCallSource(this));
		decoders.add(new ClassAccessSource(this));
		decoders.add(new ClassInstantiationSource());
		decoders.add(new ClassHeaderSource());
	}

	@Override
	public boolean accept(AstNode node)
	{
		return true;
	}

	@Override
	public Hyperlink getHyperlink(AstNode node, Buffer buffer, int line, int lineOffset)
	{
		while (true)
		{
			for (HyperlinkDecoder decoder : decoders)
			{
				if (decoder.accept(node))
				{
					return decoder.getHyperlink(node, buffer, line, lineOffset);
				}
			}
			AstNode newNode = node.subNodeAt(line + 1, lineOffset);
			if (newNode == null || newNode == node)
				return null;
			node = newNode;
		}
	}
}
