package context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.Buffer;

public class FunctionLocalVarContextFinder extends FunctionContextProvider implements IContextFinder {

	@Override
	public String getContext(String identifier, Buffer buffer, int line, int pos) {
		int functionLine = getFunctionContextLine(buffer.getPath(), line);
		if (functionLine == 0)
			return null;
		Pattern pat = CaretContext.getVarDeclPattern(identifier);
		// Go back from current line to function definition, since there may
		// be several declarations of the variable in nested blocks of code.
		for (int i = line; i >= functionLine; i--) {
			String l = buffer.getLineText(i - 1);
			Matcher m = pat.matcher(l);
			if (m.matches())
				return m.group(1);
		}
		return null;
	}
}
