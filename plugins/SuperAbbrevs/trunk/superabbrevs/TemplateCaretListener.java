package superabbrevs;
import javax.swing.event.*;
import java.util.Hashtable;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.Buffer;
import superabbrevs.template.*;
import org.gjt.sp.jedit.View;

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
					SelectableField f = template.getCurrentField();
					Handler.removeHandler(buffer);
					
					removeCaretListener(textArea);
				}
			} 
		} else {
			removeCaretListener(textArea);
		}
	}
	
	//{{{ Caret listener management
	
	private static Hashtable caretListeners = new Hashtable();
		
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
