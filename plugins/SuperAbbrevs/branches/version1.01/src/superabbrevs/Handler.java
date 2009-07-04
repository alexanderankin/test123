package superabbrevs;

import java.util.HashMap;
import java.util.Map;

import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import superabbrevs.template.Template;
import superabbrevs.template.WriteOutsideTemplateException;

public class Handler extends BufferAdapter {

    private boolean disabled;
    private boolean justEdited = false;
    private int caret;
    private int oldTemplateLength;
    private Template template;
	private final JEditInterface jedit;

    public Template getTemplate() {
        return template;
    }

    public Handler(Template template, JEditInterface jedit) {
        this.template = template;
		this.jedit = jedit;
    }

    public boolean justEdited() {
        return justEdited;
    }
    
    public void disable() {
    	disabled = true;
    }
    
    public void enable() {
    	disabled = false;
    }

    public boolean isDisabled() {
        return disabled;
    }
    
    public boolean isEnabled() {
    	return !disabled;
    }

    @Override
    public void contentInserted(JEditBuffer buffer, int startLine, int offset,
            int numLines, int length) {

        if (isEnabled()) {
            String insertedText = buffer.getText(offset, length);

            try {

                if (justEdited) {
                    offset = caret;
                    oldTemplateLength += length;
                } else {
                    oldTemplateLength = template.getLength() + length;
                }

                int fieldOffset = template.getCurrentField().getOffset();

                template.insert(offset, insertedText);

                int offsetChanged = template.getCurrentField().getOffset() - fieldOffset;
                caret = offset + offsetChanged + length;

                justEdited = true;
            } catch (WriteOutsideTemplateException e) {
                removeHandler(jedit);
            //System.out.println("Handler removed "+e.getMessage());
            }
        }
    }

    @Override
    public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
            int numLines, int length) {

        if (isEnabled()) {
            try {
                oldTemplateLength = template.getLength() - length;

                int fieldOffset = template.getCurrentField().getOffset();
                template.delete(offset, length);
                int offsetChanged = template.getCurrentField().getOffset() - fieldOffset;
                caret = offset + offsetChanged;

                justEdited = true;

                jedit.setCaretPosition(caret);
            } catch (WriteOutsideTemplateException e) {
                removeHandler(jedit);
            //System.out.println("Handler removed "+e.getMessage());
            }
        }
    }

    /**
     * Method postEdit()
     */
    public void postEdit() {
        disable();

        jedit.writeLock();
        
        //remove the old template
        jedit.remove(template.getOffset(), oldTemplateLength);

        //insert the new template
        jedit.insert(template.getOffset(), template.toString());

        jedit.writeUnlock();

        jedit.setCaretPosition(caret);

        enable();

        justEdited = false;
    }
    private static Map<JEditBuffer, Handler> handlers = new HashMap<JEditBuffer, Handler>();

    public static void putHandler(JEditInterface jedit, Handler handler) {
        jedit.removeBufferListener(handlers.get(jedit.getBuffer()));
        jedit.addBufferListener(handler);
        handlers.put(jedit.getBuffer(), handler);
    }

    public static Handler getHandler(JEditInterface jedit) {
        return handlers.get(jedit.getBuffer());
    }

    public static boolean handleIsEnabled(JEditInterface jedit) {
        return null != handlers.get(jedit.getBuffer());
    }

	public static void removeHandler(JEditInterface jedit) {
		Handler h = handlers.get(jedit.getBuffer());
        jedit.removeBufferListener(h);
        handlers.remove(jedit.getBuffer());
	}
} 
