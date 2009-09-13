package treebufferswitcher;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.visitors.JEditVisitorAdapter;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import treebufferswitcher.model.*;
import treebufferswitcher.model.MultiLevelGroupedModelBuilder;

import java.util.Map;
import java.util.HashMap;

// TODO: lookup model providers through jEdit service framework
public class TreeBufferSwitcherPlugin extends EBPlugin {

    // settings
    private boolean enabled = true;
    private GroupingMode groupingMode = GroupingMode.COMPACT_MULTI_LEVEL_GROUPING;
    private boolean useShortcuts = true;
    private String shortcuts = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int visibleRowCount = 24;
    private int treeLevelOffset = 16;
    private int deleteDelay = 60;
    // state
    private String shortcutsUppercased;
    private String shortcutsLowercased;
    private Map<EditPane, BufferSwitcherEnhanced> bufferSwitcherMap = new HashMap<EditPane, BufferSwitcherEnhanced>();

    public ModelBuilder modelBuilder;

    @Override
    public void start() {
        propertiesChanged();
    }

    @Override
    public void stop() {
        ensureInstalled(false);
    }

    public static TreeBufferSwitcherPlugin instance() {
        return (TreeBufferSwitcherPlugin)jEdit.getPlugin("treebufferswitcher.TreeBufferSwitcherPlugin");
    }

    public static void focusBufferSwitcher(EditPane editPane) {
        BufferSwitcherEnhanced bufferSwitcher = instance().bufferSwitcherMap.get(editPane);
        if (bufferSwitcher != null) {
            bufferSwitcher.requestFocus();
            bufferSwitcher.showPopup();
        }
    }

    @Override
    public void handleMessage(EBMessage message) {
        if (isEnabled() && message instanceof EditPaneUpdate) {
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
        enabled = jEdit.getBooleanProperty("treebufferswitcher.enabled");
        /*try */{
            groupingMode = GroupingMode.valueOf(jEdit.getProperty("treebufferswitcher.provider"));
        }/* catch (IllegalArgumentException ignore) {
            groupingMode = GroupingMode.NO_GROUPING;
        }*/
        useShortcuts = jEdit.getBooleanProperty("treebufferswitcher.use-shortcuts");
        shortcuts = jEdit.getProperty("treebufferswitcher.shortcuts");
        visibleRowCount = jEdit.getIntegerProperty("treebufferswitcher.visible-row-count");
        treeLevelOffset = jEdit.getIntegerProperty("treebufferswitcher.tree-level-offset");
        switch (getGroupingMode()) {
            case NO_GROUPING: modelBuilder = new FlatModelBuilder(); break;
            case ONE_LEVEL_GROUPING: modelBuilder = new OneLevelGroupedModelBuilder(); break;
            case MULTI_LEVEL_GROUPING: modelBuilder = new MultiLevelGroupedModelBuilder(false); break;
            case COMPACT_MULTI_LEVEL_GROUPING: modelBuilder = new MultiLevelGroupedModelBuilder(true); break;
        }
        shortcutsUppercased = getShortcuts().toUpperCase();
        shortcutsLowercased = getShortcuts().toLowerCase();
        ensureInstalled(isEnabled());
        for (BufferSwitcherEnhanced bufferSwitcher : bufferSwitcherMap.values()) {
            bufferSwitcher.propertiesChanged();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public GroupingMode getGroupingMode() {
        return groupingMode;
    }

    public boolean isUseShortcuts() {
        return useShortcuts;
    }

    public String getShortcuts() {
        return shortcuts;
    }

    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    public int getTreeLevelOffset() {
        return treeLevelOffset;
    }

    public int getDeleteDelay() {
        return deleteDelay;
    }

    public String getShortcutsUppercased() {
        return shortcutsUppercased;
    }

    public String getShortcutsLowercased() {
        return shortcutsLowercased;
    }

    public enum GroupingMode {
        NO_GROUPING,
        ONE_LEVEL_GROUPING,
        MULTI_LEVEL_GROUPING,
        COMPACT_MULTI_LEVEL_GROUPING
    }

}