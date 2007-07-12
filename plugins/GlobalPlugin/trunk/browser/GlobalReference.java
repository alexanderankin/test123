package browser;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;


public class GlobalReference {
	GlobalRecord rec;
	public GlobalReference(GlobalRecord rec) {
		this.rec = rec;
	}
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		String file = rec.getFile();
		int line = rec.getLine();
		if (file != null)
			s.append("[" + file + ":" + line + "] ");
		s.append(rec.getText());
		return s.toString();
	}
	public void jump(View view)
	{
		String file = rec.getFile();
		if (file == null)
			return;
		int line = rec.getLine();
		Buffer buffer = jEdit.openFile(view, file);
		if(buffer == null) {
			view.getStatus().setMessage("Unable to open: " + file);
			return;
		}
		JEditTextArea ta = view.getTextArea();
		ta.setCaretPosition(ta.getLineStartOffset(line - 1));
	}
}
