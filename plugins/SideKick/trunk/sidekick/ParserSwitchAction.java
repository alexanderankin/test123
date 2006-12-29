package sidekick;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;

public class ParserSwitchAction extends EditAction
{

	String parserName ;
	public ParserSwitchAction(String parser) 
	{
		super("sidekick.parser." + parser + "-switch");
		parserName = parser;
	}
	public String getLabel() 
	{
		return parserName;
	}
	public String getCode()
	{
		return "new sidekick.ParserSwitchAction(\"" + parserName + "\").invoke(view)";
	}
	
	public void invoke(View view)
	{
		DockableWindowManager wm = view.getDockableWindowManager();
		wm.showDockableWindow("sidekick-tree");
		SideKick sk = SideKickPlugin.getSideKick(view);
		Buffer b = view.getBuffer();
		SideKickPlugin.setParserForBuffer(b, parserName);
		sk.parse(true);
		
	}

}
