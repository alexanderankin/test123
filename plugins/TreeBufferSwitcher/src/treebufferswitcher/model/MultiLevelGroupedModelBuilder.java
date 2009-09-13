package treebufferswitcher.model;

import org.gjt.sp.jedit.Buffer;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.TreeMap;

import treebufferswitcher.util.PathUtils;

// TODO: refactor CompactMultiLevelGroupedModelBuilder out of this
public class MultiLevelGroupedModelBuilder implements ModelBuilder {

    private final boolean compact;
    private Map<String, List<String>> groups;
    private Map<String, List<Buffer>> elements;
    private int keyIndex;

    public MultiLevelGroupedModelBuilder(boolean compact) {
        this.compact = compact;
    }

    public BufferSwitcherModel createModel(Buffer[] buffers) {
        // prepare groups
        final List<String> topLevelGroups = new ArrayList<String>();
        groups = new TreeMap<String, List<String>>();
        elements = new TreeMap<String, List<Buffer>>();
        for (Buffer buffer : buffers) {
            String path = buffer.getPath();
            boolean isBuffer = true;
            while (!path.isEmpty()) {
                String parentPath = PathUtils.getParentOfPath(path);
                if (isBuffer) {
                    List<Buffer> parent = elements.get(parentPath);
                    if (parent == null) {
                        parent = new ArrayList<Buffer>();
                        elements.put(parentPath, parent);
                    }
                    parent.add(buffer);
                } else {
                    List<String> parent = groups.get(parentPath);
                    if (parent == null) {
                        parent = new ArrayList<String>();
                        groups.put(parentPath, parent);
                    }
                    if (!parent.contains(path)) {
                        parent.add(path);
                    }
                }
                isBuffer = false;
                if (parentPath.isEmpty() && !topLevelGroups.contains(path)) {
                    topLevelGroups.add(path);
                }
                path = parentPath;
            }
        }
        // compose flat array
        keyIndex = -1;
        List<Object> items = new ArrayList<Object>(groups.size() + buffers.length);
        for (String topLevelGroup : topLevelGroups) {
            if (compact) {
                appendTreeCompact(items, topLevelGroup, topLevelGroup, 0);
            } else {
                appendTree(items, topLevelGroup, 0);
            }
        }
        return new BufferSwitcherModel(items.toArray());
    }

    public void updateBuffer(BufferItem bufferItem) {
        bufferItem.title = bufferItem.buffer.getName();
    }

    private void appendTree(List<Object> items, String parent, int level) {
        items.add(new PathItem(PathUtils.getLastPathComponent(parent), level));
        List<String> subgroups = groups.get(parent);
        if (subgroups != null) {
            for (String subgroup : subgroups) {
                appendTree(items, subgroup, level + 1);
            }
        }
        List<Buffer> subelements = elements.get(parent);
        if (subelements != null) {
            for (Buffer subelement : subelements) {
                items.add(new BufferItem(subelement, subelement.getName(), level + 1, ++keyIndex));
            }
        }
    }

    private void appendTreeCompact(List<Object> items, String parent, String title, int level) {
        List<String> subgroups = groups.get(parent);
        List<Buffer> subelements = elements.get(parent);
        if (subgroups != null && subgroups.size() == 1
                && (subelements == null || subelements.isEmpty())) {
            appendTreeCompact(items, subgroups.get(0), title + "/" + PathUtils.getLastPathComponent(subgroups.get(0)), level);
        } else {
            items.add(new PathItem(title, level));
            if (subgroups != null) {
                for (String subgroup : subgroups) {
                    appendTreeCompact(items, subgroup, PathUtils.getLastPathComponent(subgroup), level + 1);
                }
            }
        }
        if (subelements != null) {
            for (Buffer subelement : subelements) {
                items.add(new BufferItem(subelement, subelement.getName(), level + 1, ++keyIndex));
            }
        }
    }

}