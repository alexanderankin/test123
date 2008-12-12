package context;

import org.gjt.sp.jedit.View;

import ctags.Tag;

public class CaretContext {
	
	public static Tag getContext(View view)
	{
		AbstractContextFinder finder = new CppContextFinder(
			view.getBuffer(), view.getTextArea().getCaretPosition());
		return finder.getContext();
	}
	
}
