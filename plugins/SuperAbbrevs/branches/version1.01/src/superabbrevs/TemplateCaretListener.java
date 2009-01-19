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
	public void caretUpdate(CaretEvent e){
		JEditTextArea textArea = (JEditTextArea)e.getSource();
		View view = textArea.getView();
		Buffer buffer = view.getBuffer();
		Handler handler = Handler.getHandler(buffer);
		
		if (handler != null){	
			
			if(!handler.isDisabled()){
				Template template = handler.getTemplate();
				
				if(handler.justEdited()){
					handler.postEdit();
				}
				
				int caret = textArea.getCaretPosition();
				
				if (!template.inCurrentField(caret)){
					Handler.removeHandler(buffer);
					removeCaretListener(textArea);
				}
			} 
		} else {
			removeCaretListener(textArea);
		}
	}
	
	//{{{ Caret listener management
	
	private static Map<JEditTextArea,TemplateCaretListener> caretListeners = 
                new HashMap<JEditTextArea, TemplateCaretListener>();
		
	public static void putCaretListener(JEditTextArea textArea, TemplateCaretListener l){
		textArea.removeCaretListener(getCaretListener(textArea));
		caretListeners.put(textArea,l);
		textArea.addCaretListener(l);
	}
	
	public static TemplateCaretListener getCaretListener(JEditTextArea textArea){
		return (TemplateCaretListener)caretListeners.get(textArea);
	}
	
	public static TemplateCaretListener removeCaretListener(JEditTextArea textArea){
		TemplateCaretListener l = getCaretListener(textArea);
		textArea.removeCaretListener(l);
		caretListeners.remove(textArea);
		return l;
	}
	
	//}}}
}
