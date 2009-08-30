package treebufferswitcher.model;

import org.gjt.sp.jedit.Buffer;

public class BufferItem {

    public final Buffer buffer;
    public String title;
    public final int level;
    public final int keyIndex;

    BufferItem(Buffer buffer, String title, int level, int keyIndex) {
        this.buffer = buffer;
        this.title = title;
        this.level = level;
        this.keyIndex = keyIndex;
    }

}