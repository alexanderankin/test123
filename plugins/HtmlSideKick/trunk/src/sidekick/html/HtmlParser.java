/*
 Copyright (c) 2006, Dale Anson
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
 this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 this list of conditions and the following disclaimer in the documentation 
 and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors 
 may be used to endorse or promote products derived from this software without 
 specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package sidekick.html;

import java.io.StringReader;
import java.util.Enumeration;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import sidekick.IAsset;
import sidekick.SideKickParsedData;
import sidekick.html.parser.html.HtmlCollector;
import sidekick.html.parser.html.HtmlDocument;
import sidekick.html.parser.html.HtmlScrubber;
import sidekick.html.parser.html.HtmlTreeBuilder;
import xml.parser.XmlParser;
import errorlist.DefaultErrorSource;

public class HtmlParser extends XmlParser implements EBComponent
{

	private View currentView = null;

	public static boolean showAll = true;

	public HtmlParser()
	{
		super("html");
	}

	public void activate(EditPane editPane)
	{
		super.activate(editPane);
		currentView = editPane.getView();
		EditBus.addToBus(this);
	}

	public void deactivate(EditPane editPane)
	{
		super.deactivate(editPane);
		EditBus.removeFromBus(this);
	}

	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof PropertiesChanged)
		{
			parse();
		}
	}

	public void parse()
	{
		if (currentView != null)
		{
			parse(currentView.getBuffer(), null);
		}
	}

	/**
	 * Parse the contents of the given buffer.
	 * 
	 * @param buffer
	 *                the buffer to parse
	 */
	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource)
	{
		
		String filename = buffer.getPath();
		SideKickParsedData parsedData = new HtmlSideKickParsedData(filename, buffer);
		DefaultMutableTreeNode root = parsedData.root;

		StringReader reader = new StringReader(buffer.getText(0, buffer.getLength()));
		HtmlTreeBuilder builder = null;
		try
		{
			// parse
			sidekick.html.parser.html.HtmlParser parser = new sidekick.html.parser.html.HtmlParser(
				reader);
			HtmlDocument document = parser.HtmlDocument();
			document.setShowBrackets(jEdit.getBooleanProperty(
				"options.sidekick.html.showBrackets", true));
			document.setShowTagAttributes(jEdit.getBooleanProperty(
				"options.sidekick.html.showTagAttributes", true));
			document.setShowCoreAttributes(jEdit.getBooleanProperty(
				"options.sidekick.html.showCoreAttributes", true));
			document.setShowLangAttributes(jEdit.getBooleanProperty(
				"options.sidekick.html.showLangAttributes", true));
			document.setShowScriptAttributes(jEdit.getBooleanProperty(
				"options.sidekick.html.showScriptAttributes", true));
			document.setShowJspTags(jEdit.getBooleanProperty(
				"options.sidekick.html.showJspElements", true));

			// collect and clean
			document.accept(new HtmlCollector());
			document.accept(new HtmlScrubber(HtmlScrubber.DEFAULT_OPTIONS
				| HtmlScrubber.TRIM_SPACES));

			// make a tree
			builder = new HtmlTreeBuilder(root);
			builder.setShowAll(jEdit.getBooleanProperty(
				"options.sidekick.html.showAllElements", true));
			builder.setShowAll(showAll);
			document.accept(builder);

			/*
			 * need to convert the HtmlDocument.HtmlElements that
			 * are currently the user objects in the tree nodes to
			 * SideKick Assets
			 */
			convert(buffer, root);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			reader.close();
		}
		return parsedData;
	}

	private void convert(Buffer buffer, DefaultMutableTreeNode node)
	{
		// convert the children of the node
		Enumeration children = node.children();
		while (children.hasMoreElements())
		{
			convert(buffer, (DefaultMutableTreeNode) children.nextElement());
		}

		// convert the node itself
		if (!(node.getUserObject() instanceof IAsset))
		{
			HtmlDocument.HtmlElement userObject = (HtmlDocument.HtmlElement) node
				.getUserObject();
			Position start_position = null;
			Position end_position = null;
			if (userObject instanceof HtmlDocument.TagBlock)
			{
				HtmlDocument.TagBlock block = (HtmlDocument.TagBlock) userObject;
				HtmlDocument.Tag start_tag = block.startTag;
				HtmlDocument.EndTag end_tag = block.endTag;
				start_position = createStartPosition(buffer, start_tag);
				createEndPosition(buffer, start_tag);
				createStartPosition(buffer, end_tag);
				end_position = createEndPosition(buffer, end_tag);
			}
			else
			{
				start_position = createStartPosition(buffer, userObject);
				end_position = createEndPosition(buffer, userObject);
			}
			HtmlAsset asset = new HtmlAsset(userObject);
			asset.setLongString(userObject.toLongString());
			asset.setStart(start_position);
			asset.setEnd(end_position);
			node.setUserObject(asset);
		}
	}

	/**
	 * Need to create Positions for each node. The javacc parser finds line
	 * and column location, need to convert this to a Position in the
	 * buffer. The HtmlElement contains a column offset based on the current
	 * tab size as set in the Buffer, need to use getOffsetOfVirtualColumn
	 * to account for soft and hard tab handling.
	 */
	public static Position createStartPosition(Buffer buffer, HtmlDocument.HtmlElement child)
	{
		final int line_offset = buffer.getLineStartOffset(Math.max(
			child.getStartLocation().line - 1, 0));
		final int col_offset = buffer.getOffsetOfVirtualColumn(Math.max(child
			.getStartLocation().line - 1, 0), Math.max(
			child.getStartLocation().column - 1, 0), null);
		Position p = new Position()
		{
			public int getOffset()
			{
				return line_offset + col_offset;
			}
		};
		child.setStartPosition(p);
		return p;
	}

	/**
	 * Need to create Positions for each node. The javacc parser finds line
	 * and column location, need to convert this to a Position in the
	 * buffer. The HtmlElement contains a column offset based on the current
	 * tab size as set in the Buffer, need to use getOffsetOfVirtualColumn
	 * to account for soft and hard tab handling.
	 */
	public static Position createEndPosition(Buffer buffer, HtmlDocument.HtmlElement child)
	{
		final int line_offset = buffer.getLineStartOffset(Math.max(
			child.getEndLocation().line - 1, 0));
		final int col_offset = buffer.getOffsetOfVirtualColumn(Math.max(child
			.getEndLocation().line - 1, 0), Math.max(child.getEndLocation().column - 1,
			0), null);
		Position p = new Position()
		{
			public int getOffset()
			{
				return line_offset + col_offset;
			}
		};
		child.setEndPosition(p);
		return p;
	}

}
