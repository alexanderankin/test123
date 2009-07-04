package superabbrevs;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import superabbrevs.template.Template;

public class TemplateCaretListener implements CaretListener {
	private final JEditInterface jedit;
	private final Handler handler;

	public TemplateCaretListener(JEditInterface jedit, Handler handler) {
		this.jedit = jedit;
		this.handler = handler;
	}
	
	public void caretUpdate(CaretEvent e){
		if(!handler.isDisabled()){
			if(handler.justEdited()){
				handler.postEdit();
			}
			
			int caret = jedit.getCaretPosition();
			Template template = handler.getTemplate();
			if (!template.inCurrentField(caret)){
				jedit.removeCaretListener(this);
				Handler.removeHandler(jedit);
			}
		}
	}
}
