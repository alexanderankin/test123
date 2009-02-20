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

import com.google.inject.Inject;

public class JEditInterfaceImpl implements JEditInterface {
    private View view;
    private JEditTextArea textArea;
    private Buffer buffer;

    @Inject
    public JEditInterfaceImpl(View view, JEditTextArea textArea, Buffer buffer) {
        this.view = view;
        this.textArea = textArea;
        this.buffer = buffer;
    }

    /* (non-Javadoc)
	 * @see superabbrevs.JEditInterface#getBuffer()
	 */
    public Buffer getBuffer() {
        return buffer;
    }

    /* (non-Javadoc)
	 * @see superabbrevs.JEditInterface#getTextArea()
	 */
    public JEditTextArea getTextArea() {
        return textArea;
    }

    /* (non-Javadoc)
	 * @see superabbrevs.JEditInterface#getView()
	 */
    public View getView() {
        return view;
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.JEditInterface#getCurrentMode()
	 */
    public Mode getCurrentMode() {
        return buffer.getMode();
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.JEditInterface#getCurrentModeName()
	 */
    public String getCurrentModeName() {
        return buffer.getMode().getName();
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.JEditInterface#getModes()
	 */
    public List<Mode> getModes() {
        return Arrays.asList(jEdit.getModes());
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.JEditInterface#getModesNames()
	 */
    public SortedSet<String> getModesNames() {
    	SortedSet<String> modeNames = new TreeSet<String>();
    	for (Mode mode : jEdit.getModes()) {
			modeNames.add(mode.getName());
		}
        return modeNames;
    }
        
}
