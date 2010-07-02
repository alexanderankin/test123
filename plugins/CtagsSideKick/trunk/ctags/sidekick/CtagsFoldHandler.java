package ctags.sidekick;

import javax.swing.text.Segment;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.buffer.JEditBuffer;

import sidekick.SideKickParsedData;
import sidekick.SideKickPlugin;

public class CtagsFoldHandler extends org.gjt.sp.jedit.buffer.FoldHandler {

	public static final String CTAGS_SIDE_KICK_FOLD_HANDLER = "CtagsSideKick";

	protected CtagsFoldHandler() {
		super(CTAGS_SIDE_KICK_FOLD_HANDLER);
	}

	@Override
	public int getFoldLevel(JEditBuffer buffer, int lineIndex, Segment seg) {
		if(lineIndex == 0 || lineIndex == buffer.getLineCount() - 1)
			return 0;

		SideKickParsedData data = (SideKickParsedData)buffer.getProperty(
			SideKickPlugin.PARSED_DATA_PROPERTY);
		if(data == null)
			return 0;
		int offset = buffer.getLineStartOffset(lineIndex);
		TreePath path = data.getTreePathForPosition(offset);
		if(path == null)
			return 0;
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode)path.getLastPathComponent();
		Object obj = node.getUserObject();
		if (obj instanceof Tag) {
			Tag tag = (Tag)(node.getUserObject());
			if (lineIndex == tag.getLine())
				return 0;
			return 1;
		}
		return 0;
	}

}
