package tasklist;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;

public class ParseBufferMessage extends BufferUpdate {

    public static Object DO_PARSE = new Object();

    public ParseBufferMessage( View view, Buffer buffer ) {
        super( buffer, view, DO_PARSE );
    }

}