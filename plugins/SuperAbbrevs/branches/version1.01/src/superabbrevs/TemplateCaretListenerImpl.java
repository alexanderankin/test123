package superabbrevs;
import javax.swing.event.CaretEvent;

import superabbrevs.template.Template;

import com.google.inject.Inject;

public class TemplateCaretListenerImpl implements TemplateCaretListener {
	private final JEditInterface jedit;
	private final TemplateBufferListener handler;

	@Inject
	public TemplateCaretListenerImpl(JEditInterface jedit, TemplateBufferListener handler) {
		this.jedit = jedit;
		this.handler = handler;
	}
	
	/* (non-Javadoc)
	 * @see superabbrevs.TemplateCaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate(CaretEvent e){
		if(!handler.isListening()){
			if(handler.justEdited()){
				handler.postEdit();
			}
			
			int caret = jedit.getCaretPosition();
			Template template = handler.getTemplate();
			if (!template.inCurrentField(caret)){
				jedit.removeCaretListener(this);
				handler.stopListening();
			}
		}
	}

	/* (non-Javadoc)
	 * @see superabbrevs.TemplateCaretListener#startListening()
	 */
	public void startListening() {
		jedit.addCaretListener(this);
	}
	
	/* (non-Javadoc)
	 * @see superabbrevs.TemplateCaretListener#stopListening()
	 */
	public void stopListening() {
		jedit.removeCaretListener(this);
	}
}
