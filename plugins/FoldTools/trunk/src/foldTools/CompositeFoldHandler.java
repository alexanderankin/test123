package foldTools;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.buffer.FoldHandler;
import org.gjt.sp.jedit.buffer.JEditBuffer;

public class CompositeFoldHandler extends FoldHandler {

	private FoldHandler [] handlers;
	private boolean [] isFixedHandler;

	public CompositeFoldHandler(FoldHandler [] handlers)
	{
		super(createName(handlers));
		this.handlers = handlers.clone();
		isFixedHandler = new boolean[handlers.length];
		for (int i = 0; i < handlers.length; i++)
			isFixedHandler[i] = isFixedLevelHandler(handlers[i]);
	}
	private static String createName(FoldHandler [] handlers) {
		StringBuilder sb = new StringBuilder("composite(");
		for (int i = 0; i < handlers.length; i++)
		{
			if (i > 0)
				sb.append(",");
			sb.append(handlers[i].getName());
		}
		sb.append(")");
		return sb.toString();
	}
	private boolean isFixedLevelHandler(FoldHandler h)
	{
		String name = h.getName();
		int type = FoldingModeTypes.getModeType(name);
		return (type == FoldingModeTypes.Fixed);
	}
	@Override
	public int getFoldLevel(JEditBuffer buffer, int lineIndex, Segment seg) {
		int prev = (lineIndex > 0) ? buffer.getFoldLevel(lineIndex - 1) : 0;
		int level = prev;
		for (int i = 0; i < handlers.length; i++)
		{
			FoldHandler h = handlers[i];
			level += h.getFoldLevel(buffer, lineIndex, seg);
			int hprev;
			if (isFixedHandler[i])
				hprev = ((lineIndex > 0) ? h.getFoldLevel(buffer, lineIndex - 1, seg) : 0);
			else
				hprev = prev;
			level -= hprev;
		}
		return level;
	}

}
