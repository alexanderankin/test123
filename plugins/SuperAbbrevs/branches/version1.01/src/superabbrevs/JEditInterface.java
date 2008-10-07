package superabbrevs;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class JEditInterface {
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
    
    public Mode getMode() {
        return buffer.getMode();
    }
}
