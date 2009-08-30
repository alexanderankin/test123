package treebufferswitcher.model;

import org.gjt.sp.jedit.Buffer;

import javax.swing.*;

public class BufferSwitcherModel extends DefaultComboBoxModel {

    public final int buffersCount;
    public boolean adjusting;
    public final Object[] items;

    public BufferSwitcherModel(Object items[]) {
        super(items);
        this.items = items;
        int buffersCountLocal = 0;
        for (Object item : items) {
            if (item instanceof BufferItem) {
                ++buffersCountLocal;
            }
        }
        buffersCount = buffersCountLocal;
    }

    public BufferItem itemByKeyIndex(int keyIndex) {
        for (Object item : items) {
            if (item instanceof BufferItem) {
                if (((BufferItem)item).keyIndex == keyIndex) {
                    return (BufferItem)item;
                }
            }
        }
        return null;
    }

    public BufferItem itemByBuffer(Buffer buffer) {
        for (Object item : items) {
            if (item instanceof BufferItem) {
                if (((BufferItem)item).buffer == buffer) {
                    return (BufferItem)item;
                }
            }
        }
        return null;
    }

    public void fireItemChanged(BufferItem bufferItem) {
        int index = getIndexOf(bufferItem);
        fireContentsChanged(this, index, index);
    }
    
}