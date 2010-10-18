package foldTools;

import java.util.ArrayList;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.buffer.JEditBuffer;

public class CompositeFoldHandler extends FoldHandler {

	private ArrayList<FoldHandler> handlers;

	public CompositeFoldHandler(ArrayList<FoldHandler> handlers)
	{
		super(createName(handlers));
		this.handlers = handlers;
	}
	private static String createName(ArrayList<FoldHandler> handlers) {
		StringBuilder sb = new StringBuilder("composite(");
		boolean first = true;
		for (FoldHandler h: handlers)
		{
			if (! first)
				sb.append(",");
			sb.append(h.getName());
		}
		sb.append(")");
		return sb.toString();
	}
	private boolean isFixedLevelHandler(FoldHandler h)
	{
		String name = h.getName();
		return (name.equals("comment") || name.equals("indent"));
	}
	@Override
	public int getFoldLevel(JEditBuffer buffer, int lineIndex, Segment seg) {
		int prev = (lineIndex > 0) ? buffer.getFoldLevel(lineIndex - 1) : 0;
		int level = prev;
		for (FoldHandler h: handlers)
		{
			level += h.getFoldLevel(buffer, lineIndex, seg);
			int hprev;
			if (isFixedLevelHandler(h))
				hprev = ((lineIndex > 0) ? h.getFoldLevel(buffer, lineIndex - 1, seg) : 0);
			else
				hprev = prev;
			level -= hprev;
		}
		return level;
	}

}
