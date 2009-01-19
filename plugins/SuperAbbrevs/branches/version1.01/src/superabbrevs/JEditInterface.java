package superabbrevs;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class JEditInterface implements ModeService {
    private View view;
    private JEditTextArea textArea;
    private Buffer buffer;

    public JEditInterface(View view, JEditTextArea textArea, Buffer buffer) {
        this.view = view;
        this.textArea = textArea;
        this.buffer = buffer;
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public JEditTextArea getTextArea() {
        return textArea;
    }

    public View getView() {
        return view;
    }
    
    public Mode getCurrentMode() {
        return buffer.getMode();
    }
    
    public String getCurrentModeName() {
        return buffer.getMode().getName();
    }
    
    public List<Mode> getModes() {
        return Arrays.asList(jEdit.getModes());
    }
    
    public SortedSet<String> getModesNames() {
    	SortedSet<String> modeNames = new TreeSet<String>();
    	for (Mode mode : jEdit.getModes()) {
			modeNames.add(mode.getName());
		}
        return modeNames;
    }
        
}
