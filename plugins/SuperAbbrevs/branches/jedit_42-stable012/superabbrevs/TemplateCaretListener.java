package superabbrevs;
import javax.swing.event.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;

public class TemplateCaretListener implements CaretListener {
	public void caretUpdate(CaretEvent e){
		JEditTextArea textArea = (JEditTextArea)e.getSource();
		Buffer buffer = textArea.getBuffer();
		Handler handler = SuperAbbrevs.getHandler(buffer);
		
		if (handler != null){	
			
			if(!handler.isDisabled()){
				Template template = handler.getTemplate();
				
				if(handler.justEdited()){
					handler.postEdit();
				}
				
				int caret = textArea.getCaretPosition();
				
				if (!template.inCurrentField(caret)){
					SuperAbbrevs.removeHandler(buffer);
					textArea.removeCaretListener(this);
				}
			} 
		} else {
			textArea.removeCaretListener(this);
		}
	}
}
