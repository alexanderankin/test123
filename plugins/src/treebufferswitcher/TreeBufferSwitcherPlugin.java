package treebufferswitcher;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import treebufferswitcher.model.*;
import treebufferswitcher.model.MultiLevelGroupedModelBuilder;

import java.util.Map;
import java.util.HashMap;

// !!! add configuration dialog and load/save properties
public class TreeBufferSwitcherPlugin extends EBPlugin {

    // settings
    // !!! encapsulate and read from props
    public boolean enabled = true;
    public GroupingMode groupingMode = GroupingMode.COMPACT_MULTI_LEVEL_GROUPING;
    public boolean useShortcuts = true;
    public String shortcuts = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public int visibleRowCount = 24;
    public int treeLevelOffset = 16;
    public int deleteDelay = 60;
    // state
    private static TreeBufferSwitcherPlugin instance;
    public String shortcutsUppercased;
    public String shortcutsLowercased;
    private Map<EditPane, BufferSwitcherEnhanced> bufferSwitcherMap = new HashMap<EditPane, BufferSwitcherEnhanced>();

    public ModelBuilder modelBuilder;

    public TreeBufferSwitcherPlugin() {
        instance = this;
    }

    @Override
    public void start() {
        propertiesChanged();
    }

    @Override
    public void stop() {
        ensureInstalled(false);
    }

    public static void focusBufferSwitcher(EditPane editPane) {
        BufferSwitcherEnhanced bufferSwitcher = instance.bufferSwitcherMap.get(editPane);
        if (bufferSwitcher != null) {
            bufferSwitcher.requestFocus();
            bufferSwitcher.showPopup();
        }
    }

    @Override
    public void handleMessage(EBMessage message) {
        if (enabled && message instanceof EditPaneUpdate) {
            EditPaneUpdate editPaneUpdate = (EditPaneUpdate)message;
            if (editPaneUpdate.getWhat() == EditPaneUpdate.CREATED) {
                editPaneCreated(editPaneUpdate.getEditPane());
            } else if (editPaneUpdate.getWhat() == EditPaneUpdate.DESTROYED) {
                editPaneDestroyed(editPaneUpdate.getEditPane());
            }
        }
        if (message instanceof PropertiesChanged) {
            propertiesChanged();
        }
    }

    private void ensureInstalled(final boolean install) {
        jEdit.visit(new JEditVisitorAdapter() {
            @Override
            public void visit(EditPane editPane) {
                ensureInstalled(install, editPane);
            }
        });
    }

    private void ensureInstalled(boolean install, EditPane editPane) {
        if (install) {
            if (!bufferSwitcherMap.containsKey(editPane)) {
                editPaneCreated(editPane);
            }
        } else {
            if (bufferSwitcherMap.containsKey(editPane)) {
                editPaneDestroyed(editPane);
            }
        }
    }

    private void editPaneCreated(EditPane editPane) {
        BufferSwitcherEnhanced bufferSwitcher = new BufferSwitcherEnhanced(this, editPane);
        bufferSwitcherMap.put(editPane, bufferSwitcher);
    }

    private void editPaneDestroyed(EditPane editPane) {
        BufferSwitcherEnhanced bufferSwitcher = bufferSwitcherMap.get(editPane);
        if (bufferSwitcher != null) {
            bufferSwitcher.dispose();
            bufferSwitcherMap.remove(editPane);
        }
    }

    private void propertiesChanged() {
        // !!! read properties
        switch (groupingMode) {
            case NO_GROUPING: modelBuilder = new FlatModelBuilder(); break;
            case ONE_LEVEL_GROUPING: modelBuilder = new OneLevelGroupedModelBuilder(); break;
            case MULTI_LEVEL_GROUPING: modelBuilder = new MultiLevelGroupedModelBuilder(false); break;
            case COMPACT_MULTI_LEVEL_GROUPING: modelBuilder = new MultiLevelGroupedModelBuilder(true); break;
        }
        shortcutsUppercased = shortcuts.toUpperCase();
        shortcutsLowercased = shortcuts.toLowerCase();
        ensureInstalled(enabled);
        for (BufferSwitcherEnhanced bufferSwitcher : bufferSwitcherMap.values()) {
            bufferSwitcher.propertiesChanged();
        }
    }

    public enum GroupingMode {
        NO_GROUPING,
        ONE_LEVEL_GROUPING,
        MULTI_LEVEL_GROUPING,
        COMPACT_MULTI_LEVEL_GROUPING
    }

}