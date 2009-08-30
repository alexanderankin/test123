package treebufferswitcher.model;

import org.gjt.sp.jedit.Buffer;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import treebufferswitcher.util.PathUtils;

public class OneLevelGroupedModelBuilder implements ModelBuilder {

    public BufferSwitcherModel createModel(Buffer[] buffers) {
        // prepare groups
        Map<String, List<Buffer>> groups = new TreeMap<String, List<Buffer>>();
        for (Buffer buffer : buffers) {
            String parentPath = PathUtils.getParentOfPath(buffer.getPath());
            List<Buffer> parent = groups.get(parentPath);
            if (parent == null) {
                parent = new ArrayList<Buffer>();
                groups.put(parentPath, parent);
            }
            parent.add(buffer);
        }
        // compose flat array
        List<Object> items = new ArrayList<Object>(groups.size() + buffers.length);
        int keyIndex = -1;
        for (Map.Entry<String, List<Buffer>> group : groups.entrySet()) {
            items.add(new PathItem(group.getKey(), 0));
            for (Buffer buffer : group.getValue()) {
                items.add(new BufferItem(buffer, buffer.getName(), 1, ++keyIndex));
            }
        }
        return new BufferSwitcherModel(items.toArray());
    }

    public void updateBuffer(BufferItem bufferItem) {
        bufferItem.title = bufferItem.buffer.getName();
    }

}