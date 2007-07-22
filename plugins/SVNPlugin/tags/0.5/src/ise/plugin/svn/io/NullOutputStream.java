package ise.plugin.svn.io;

import java.io.OutputStream;
import java.io.IOException;

/**
 * A bottomless sink.
 */
public class NullOutputStream extends OutputStream {
    public void write(int b) throws IOException {
    }
}
