package superabbrevs;

import java.util.List;
import java.util.SortedSet;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public interface JEditInterface extends ModeService {

	public abstract Buffer getBuffer();

	public abstract JEditTextArea getTextArea();

	public abstract View getView();

	public abstract Mode getCurrentMode();

	public abstract String getCurrentModeName();

	public abstract List<Mode> getModes();

	public abstract SortedSet<String> getModesNames();

}