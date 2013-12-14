package ise.plugin.bmp;

import java.util.Calendar;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;


/**
 * A data object to track buffers
 * @version   $Revision: 18720 $
 */
public class BufferReference {
    private final View view;
    private final Buffer buffer;
    private Calendar viewed;

    /**
     * Constructor for BufferReference
     *
     * @param view
     * @param buffer
     */
    public BufferReference(View view, Buffer buffer) {
        this.view = view;
        this.buffer = buffer;
        viewed = Calendar.getInstance();
    }

    /** Sets the viewed attribute of the BufferReference object */
    public void setViewed() {
        viewed = Calendar.getInstance();
    }

    /**
     * Gets the view attribute of the BufferReference object
     *
     * @return   The view value
     */
    public View getView() {
        return view;
    }

    /**
     * Gets the buffer attribute of the BufferReference object
     *
     * @return   The buffer value
     */
    public Buffer getBuffer() {
        return buffer;
    }

    /**
     * Gets the viewed attribute of the BufferReference object
     *
     * @return   The viewed value
     */
    public Calendar getViewed() {
        return viewed;
    }

    /**
     * @return   Description of the Returned Value
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(50);
        sb.append("BufferReference[");
        sb.append(buffer.getPath()).append(',');
        sb.append(viewed.getTime().toString()).append(']');
        return sb.toString();
    }
}
