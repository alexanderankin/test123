package org.gjt.sp.jedit;

import java.util.Map;
import java.util.HashMap;

public class BufferMock extends Buffer {

    private static Map EMPTY_MAP = new HashMap();
    
    public BufferMock(String path) {
        super(path, false, false, EMPTY_MAP);
    }

}