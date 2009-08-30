package treebufferswitcher.model;

import org.gjt.sp.jedit.Buffer;

import javax.swing.*;

public class FlatModelBuilder implements ModelBuilder {

    public BufferSwitcherModel createModel(Buffer[] buffers) {
        BufferItem[] items = new BufferItem[buffers.length];
        for (int i=0; i<buffers.length; ++i) {
            // TODO: add an option to format title as jEdit does ("filename (title)")
            items[i] = new BufferItem(buffers[i], buffers[i].getPath(), 0, i);
        }
        return new BufferSwitcherModel(items);
    }

    public void updateBuffer(BufferItem bufferItem) {
        bufferItem.title = bufferItem.buffer.getPath();
    }

}