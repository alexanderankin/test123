package superabbrevs;

import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.Buffer;
import java.util.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import superabbrevs.template.Template;
import superabbrevs.template.WriteOutsideTemplateException;

public class Handler extends BufferAdapter {

    private boolean disabled;
    private JEditTextArea textArea;
    private boolean justEdited = false;
    private int caret;
    private int oldTemplateLength;
    private Buffer buffer;
    private boolean inCompoundEdit = false;
    private Template template;

    public Template getTemplate() {
        return template;
    }

    public Handler(Template template, JEditTextArea textArea, Buffer buffer) {
        this.textArea = textArea;
        //buffer = textArea.getBuffer();
        this.buffer = buffer;
        this.template = template;
    }

    public boolean justEdited() {
        return justEdited;
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void contentInserted(JEditBuffer buffer, int startLine, int offset,
            int numLines, int length) {

        if (!disabled) {
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
                removeHandler(buffer);
            //System.out.println("Handler removed "+e.getMessage());
            }
        }
    }

    @Override
    public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
            int numLines, int length) {

        if (!disabled) {
            try {
                oldTemplateLength = template.getLength() - length;

                int fieldOffset = template.getCurrentField().getOffset();
                template.delete(offset, length);
                int offsetChanged = template.getCurrentField().getOffset() - fieldOffset;
                caret = offset + offsetChanged;

                justEdited = true;

                textArea.setCaretPosition(caret);
            } catch (WriteOutsideTemplateException e) {
                removeHandler(buffer);
            //System.out.println("Handler removed "+e.getMessage());
            }
        }
    }

    /**
     * Method postEdit()
     */
    public void postEdit() {
        disabled = true;

        buffer.writeLock();

        TemplateCaretListener listener = TemplateCaretListener.removeCaretListener(textArea);
        Handler handler = removeHandler(buffer);

        //remove the old template
        buffer.remove(template.getOffset(), oldTemplateLength);

        //insert the new template
        buffer.insert(template.getOffset(), template.toString());

        putHandler(buffer, handler);
        TemplateCaretListener.putCaretListener(textArea, listener);

        buffer.writeUnlock();

        textArea.setCaretPosition(caret);

        disabled = false;

        justEdited = false;
    }
    private static Map<JEditBuffer, Handler> handlers = new HashMap<JEditBuffer, Handler>();

    public static void putHandler(JEditBuffer buffer, Handler t) {
        buffer.removeBufferListener(handlers.get(buffer));
        buffer.addBufferListener(t);
        handlers.put(buffer, t);
    }

    public static Handler removeHandler(JEditBuffer buffer) {
        Handler h = handlers.get(buffer);
        buffer.removeBufferListener(h);
        handlers.remove(buffer);
        return h;
    }

    public static Handler gethandler(JEditBuffer buffer) {
        return handlers.get(buffer);
    }

    public static Handler getHandler(Buffer buffer) {
        return handlers.get(buffer);
    }

    public static boolean enabled(JEditBuffer buffer) {
        return null != handlers.get(buffer);
    }
} 
