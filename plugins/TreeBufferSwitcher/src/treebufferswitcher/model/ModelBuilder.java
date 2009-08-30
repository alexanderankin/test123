package treebufferswitcher.model;

import org.gjt.sp.jedit.Buffer;

import javax.swing.*;

public interface ModelBuilder {

    public BufferSwitcherModel createModel(Buffer[] buffers);

    public void updateBuffer(BufferItem bufferItem);

}