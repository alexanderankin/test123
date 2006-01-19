package superabbrevs;
import javax.swing.event.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.buffer.*;

public class TemplateCaretListener implements CaretListener {
	public void caretUpdate(CaretEvent e){
		JEditTextArea textArea = (JEditTextArea)e.getSource();
		JEditBuffer buffer = textArea.getBuffer();
		Handler handler = SuperAbbrevs.getHandler(buffer);
		
		if (handler != null){	
			
			if(!handler.isDisabled()){
				Template template = handler.getTemplate();
				
				if(handler.justEdited()){
					handler.postEdit();
				}
				
				int caret = textArea.getCaretPosition();
				
				if (!template.inCurrentField(caret)){
					SelectableField f = template.getCurrentField();
					System.out.println("Caretlistner removed "+caret+" in field: "+f+" "+f.getOffset()+"-"+f.getLength() );
					SuperAbbrevs.removeHandler(buffer);
					//textArea.removeCaretListener(this);
					SuperAbbrevs.removeCaretListener(textArea);
				}
			} 
		} else {
			SuperAbbrevs.removeCaretListener(textArea);
			
			System.out.println("Handler is null");
		}
	}
}
