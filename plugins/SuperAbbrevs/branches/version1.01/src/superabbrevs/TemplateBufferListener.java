package superabbrevs;

import org.gjt.sp.jedit.buffer.BufferListener;

import superabbrevs.template.Template;

public interface TemplateBufferListener extends BufferListener {

	public abstract Template getTemplate();

	public abstract boolean justEdited();

	public abstract boolean isListening();

	public abstract void postEdit();

	public abstract void stopListening();

	public abstract void startListening();

	public abstract void setTemplate(Template template);
}