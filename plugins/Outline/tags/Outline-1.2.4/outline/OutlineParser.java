/* :folding=none: */
package outline;

import javax.swing.text.*;
import javax.swing.tree.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.util.*;

import errorlist.*;

import sidekick.*;

/**
 * 'Parses' buffers using their FoldHandler to create an outline.
 * @see org.gjt.sp.jedit.buffer.FoldHandler
 */
public class OutlineParser extends SideKickParser {

	public OutlineParser() {
		super("outline");
	}

	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
		SideKickParsedData data = new SideKickParsedData(buffer.getName());
		FoldHandler handler = buffer.getFoldHandler();
		Stack<Integer> levels = new Stack<Integer>();
		levels.push(-1);
		Stack<DefaultMutableTreeNode> nodes = new Stack<DefaultMutableTreeNode>();
		nodes.push(data.root);
		Segment seg = new Segment();
		int lastFoldLevel = 0;

		for (int i = 0; i < buffer.getLineCount()-1; i++) {
			String line = buffer.getLineText(i).trim();
			buffer.getLineText(i,seg);
			int level = handler.getFoldLevel(buffer,i+1,seg);
			if (level > lastFoldLevel) {
				int startIndex = buffer.getLineStartOffset(i);
				OutlineAsset asset;
				if (line.indexOf("{{{") >= 0) {
					asset = new OutlineAsset(line.substring(line.indexOf("{{{")+3),startIndex);
				} else if (line.equals("/**")) {
					asset = new OutlineAsset(buffer.getLineText(i+1),startIndex);
				} else {
					asset = new OutlineAsset(line,startIndex);
				}
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(asset);
				DefaultMutableTreeNode parent = nodes.peek();
				parent.add(node);
				nodes.push(node);
				levels.push(lastFoldLevel);
			}
			if (level < lastFoldLevel) {
				while (levels.peek() >= level) {
					levels.pop();
					DefaultMutableTreeNode node = nodes.pop();
					try {
						OutlineAsset asset = (OutlineAsset) node.getUserObject();
						asset.setEnd(buffer.getLineEndOffset(i));
						int startLine = buffer.getLineOfOffset(asset.getStart().getOffset());
						asset.setDescription((i-startLine)+" Lines ("+(startLine+1)+" to "+i+')');
					} catch (ClassCastException e) {
						Log.log(Log.DEBUG,this,node.getUserObject());
					}
				}
			}
			lastFoldLevel = level;
		}
		while (nodes.peek() != data.root) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.pop();
			OutlineAsset asset = (OutlineAsset) node.getUserObject();
			asset.setEnd(buffer.getLength());
		}
		return data;
	}
}
