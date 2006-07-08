package csidekick.parser;

import org.gjt.sp.jedit.Buffer;

import errorlist.DefaultErrorSource;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;

public class CSideKickParser extends SideKickParser
{

	public CSideKickParser()
	{
		super("c");
		
	}

	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource)
	{
		return null;
	}

}
