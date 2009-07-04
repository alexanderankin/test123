package superabbrevs;

import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import com.google.inject.Inject;

import superabbrevs.template.Template;
import superabbrevs.template.WriteOutsideTemplateException;

public class TemplateBufferListenerImpl extends BufferAdapter implements TemplateBufferListener {

    private boolean justEdited = false;
    private int caret;
    private int oldTemplateLength;
    private Template template;
	private final JEditInterface jedit;
	private boolean listening;

    /* (non-Javadoc)
	 * @see superabbrevs.TemplateBufferListener#getTemplate()
	 */
    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
		this.template = template;
	}
    
    @Inject
    public TemplateBufferListenerImpl(JEditInterface jedit) {
		this.jedit = jedit;
    }

    /* (non-Javadoc)
	 * @see superabbrevs.TemplateBufferListener#justEdited()
	 */
    public boolean justEdited() {
        return justEdited;
    }
    
    /* (non-Javadoc)
	 * @see superabbrevs.TemplateBufferListener#isListening()
	 */
    public boolean isListening() {
    	return listening;
    }

    @Override
    public void contentInserted(JEditBuffer buffer, int startLine, int offset,
            int numLines, int length) {

        String insertedText = buffer.getText(offset, length);

        try {

            if (justEdited) {
                offset = caret;
                oldTemplateLength += length;
            } else {
                oldTemplateLength = getTemplate().getLength() + length;
            }

            int fieldOffset = getTemplate().getCurrentField().getOffset();

            getTemplate().insert(offset, insertedText);

            int offsetChanged = getTemplate().getCurrentField().getOffset() - fieldOffset;
            caret = offset + offsetChanged + length;

            justEdited = true;
        } catch (WriteOutsideTemplateException e) {
            stopListening();
        }
    }

    @Override
    public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
            int numLines, int length) {

        try {
            oldTemplateLength = getTemplate().getLength() - length;

            int fieldOffset = getTemplate().getCurrentField().getOffset();
            getTemplate().delete(offset, length);
            int offsetChanged = getTemplate().getCurrentField().getOffset() - fieldOffset;
            caret = offset + offsetChanged;

            justEdited = true;

            jedit.setCaretPosition(caret);
        } catch (WriteOutsideTemplateException e) {
        	stopListening();
        }
    }

    /* (non-Javadoc)
	 * @see superabbrevs.TemplateBufferListener#postEdit()
	 */
    public void postEdit() {
        stopListening();

        jedit.writeLock();
        
        //remove the old template
        jedit.remove(getTemplate().getOffset(), oldTemplateLength);

        //insert the new template
        jedit.insert(getTemplate().getOffset(), getTemplate().toString());

        jedit.writeUnlock();

        jedit.setCaretPosition(caret);

        startListening();

        justEdited = false;
    }

	/* (non-Javadoc)
	 * @see superabbrevs.TemplateBufferListener#stopListening()
	 */
	public void stopListening() {
		jedit.removeBufferListener(this);
		listening = false;
	}

	/* (non-Javadoc)
	 * @see superabbrevs.TemplateBufferListener#startListening()
	 */
	public void startListening() {
		jedit.addBufferListener(this);
		listening = true;
	}
} 
