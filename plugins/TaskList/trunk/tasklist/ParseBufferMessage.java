package tasklist;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.BufferUpdate;

public class ParseBufferMessage extends BufferUpdate {

    public static Object DO_PARSE = "do-parse";
    public static Object DO_PARSE_ALL = "do-parse-all";

    public ParseBufferMessage( View view, Buffer buffer, Object what ) {
        super( buffer, view, what );
    }

}