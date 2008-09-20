package context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.Buffer;

public class FunctionArgContextFinder extends FunctionContextProvider implements IContextFinder {

	@Override
	public String getContext(String identifier, Buffer buffer, int line,
			int pos)
	{
		String signature = getFunctionContextSignature(buffer.getPath(), line);
		if (signature == null)
			return null;
		Pattern pat = CaretContext.getVarDeclPattern(identifier);
		Matcher m = pat.matcher(signature);
		if (m.matches())
			return m.group(1);
		return null;
	}

}
