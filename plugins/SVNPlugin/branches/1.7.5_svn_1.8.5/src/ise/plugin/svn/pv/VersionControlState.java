
package ise.plugin.svn.pv;

import projectviewer.config.VersionControlService;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;
import projectviewer.importer.ImporterFileFilter;

import java.io.*;
import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.io.VFSFile;

import ise.plugin.svn.command.Info;
import ise.plugin.svn.command.Status;
import ise.plugin.svn.io.*;
import ise.plugin.svn.gui.PVSVNOptionPane;

import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

/**
 * Provide version control icons for file status to ProjectViewer.
 */
public class VersionControlState implements VersionControlService {

    // version control states for Subversion
    public static final int NONE = 0;    // no state
    public static final int LOCAL_MOD = 1;    // modified
    public static final int LOCAL_ADD = 2;    // added
    public static final int LOCAL_RM = 3;    // missing
    public static final int NEED_UPDATE = 4;    // out of date
    public static final int CONFLICT = 5;    // conflicted
    public static final int DELETED = 6;    // deleted
    public static final int IGNORED = 7;    // ignored
    public static final int LOCKED = 8;    // locked
    public static final int UNVERSIONED = 9;    // unversioned
    public static final int NORMAL = 10;    // normal

    public static final int UNKNOWN = -1;

    // icon definitions for the various states
    public static final Icon NORMAL_ICON = createIcon( "ise/plugin/svn/gui/icons/normal.png" );
    public static final Icon ADDED_ICON = createIcon( "ise/plugin/svn/gui/icons/added.png" );
    public static final Icon CONFLICT_ICON = createIcon( "ise/plugin/svn/gui/icons/conflict.png" );
    public static final Icon DELETED_ICON = createIcon( "ise/plugin/svn/gui/icons/deleted.png" );
    public static final Icon IGNORED_ICON = createIcon( "ise/plugin/svn/gui/icons/ignored.png" );
    public static final Icon LOCKED_ICON = createIcon( "ise/plugin/svn/gui/icons/locked.png" );
    public static final Icon MODIFIED_ICON = createIcon( "ise/plugin/svn/gui/icons/modified.png" );
    public static final Icon OUTOFDATE_ICON = createIcon( "ise/plugin/svn/gui/icons/outofdate.png" );
    public static final Icon READONLY_ICON = createIcon( "ise/plugin/svn/gui/icons/readonly.png" );
    public static final Icon UNVERSIONED_ICON = createIcon( "ise/plugin/svn/gui/icons/unversioned.png" );

    private static Icon createIcon( String name ) {
        return new ImageIcon( VersionControlState.class.getClassLoader().getResource( name ) );
    }

    private static Status command = new Status();

    private static class SingletonHolder {
        public static final VersionControlState instance = new VersionControlState();
    }

    private VersionControlState() {
        EditBus.addToBus( this );
    }

    public static VersionControlState getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * This method should return an integer identifying the current
     * state of the given file.
     *
     * This method will be called every time the file's tree node needs
     * to be repainted, so it shouldn't take long to return. It's
     * extremely encouraged that implementations do some sort of caching
     * to make this method return quickly.
     *
     * @param f  The file.
     *
     * @return A service-specific identifier for the file state.
     */
    public int getNodeState( VPTNode f ) {
        if ( f == null ) {
            return UNKNOWN;
        }
        return getNodeState( f.getNodePath() );
    }

    public int getNodeState( String path ) {
        if ( path == null ) {
            return UNKNOWN;
        }
        File file = new File( path );
        SVNStatus status = command.getStatus( file );
        if ( status == null ) {
            return NONE;
        }
        SVNStatusType type = status.getContentsStatus();
        if ( SVNStatusType.STATUS_ADDED.equals( type ) ) {
            return LOCAL_ADD;
        } else if ( SVNStatusType.STATUS_CONFLICTED.equals( type ) ) {
            return CONFLICT;
        } else if ( SVNStatusType.STATUS_DELETED.equals( type ) ) {
            return DELETED;
        } else if ( SVNStatusType.STATUS_IGNORED.equals( type ) ) {
            return DELETED;
        } else if ( status.isLocked() ) {
            return LOCKED;
        } else if ( SVNStatusType.STATUS_MISSING.equals( type ) ) {
            return LOCAL_RM;
        } else if ( SVNStatusType.STATUS_MODIFIED.equals( type ) ) {
            return LOCAL_MOD;
        } else if ( SVNStatusType.STATUS_UNVERSIONED.equals( type ) ) {
            return UNVERSIONED;
        } else if ( SVNStatusType.STATUS_NORMAL.equals( type ) ) {
            return NORMAL;
        } 
        return NONE;
    }

    /**
     * This should return the status icon to be used to represent the
     * given state.
     *
     * @param state The value retrieved from {@link #getFileState(VPTFile)}.
     *
     * @return The icon for the given state, or null for no icon.
     */
    public Icon getIcon( int state ) {
        switch ( state ) {
            case LOCAL_MOD:
                return MODIFIED_ICON;
            case NEED_UPDATE:
                return OUTOFDATE_ICON;
            case CONFLICT:
                return CONFLICT_ICON;
            case DELETED:
            case LOCAL_RM:
                return DELETED_ICON;
            case LOCKED:
                return LOCKED_ICON;
            case IGNORED:
                return IGNORED_ICON;
            case NORMAL:
                return NORMAL_ICON;
            case LOCAL_ADD:
                return ADDED_ICON;
            case UNVERSIONED:
            case NONE:
                return UNVERSIONED_ICON;
            default:
                return null;
        }
    }

    /**
     * Returns the class identifying the plugin. This is used to check
     * whether there are version control-specific option panes / groups
     * to be added to a project's option dialog.
     *
     * @return The main plugin class for this service.
     */
    public Class getPlugin() {
        return ise.plugin.svn.SVNPlugin.class;
    }

    /**
     * Called when a user removes the version control association with a
     * project (either by not choosing a version control service or a
     * different one). This allows the service to clean up any
     * configuration data associated with the service from the project's
     * properties.
     *
     * @param proj The project.
     */
    public void dissociate( VPTProject project ) {
    }

    /**
     * This method should return the option pane to be shown. As with
     * regular jEdit option panes, the label to be shown in the dialog
     * should be defined by the "option.[pane_name].label" property.
     *
     * @param project The project that will be edited.
     *
     * @return An OptionPane instance, or null for no option pane.
     */
    public OptionPane getOptionPane( VPTProject project ) {
        return new PVSVNOptionPane( project.getName() );
    }

    /**
     * This should return an OptionGroup to be shown. As with regular
     * jEdit option groups, the label to be shown in the dialog
     * should be defined by the "option.[group_name].label" property.
     *
     * @param project The project that will be edited.
     *
     * @return null
     */
    public OptionGroup getOptionGroup( VPTProject project ) {
        return null;
    }

    /**
     * Returns a file filter to be shown as an option when the user
     * imports files into a project backed by this version control
     * service.
     *
     * @return An ImporterFileFilter for SVN.
     */
    public ImporterFileFilter getFilter() {
        return new ImporterFileFilter() {
            public String getRecurseDescription() {
                return jEdit.getProperty( "ips.Use_SVN_entries", "Use SVN entries" );
            }

            @Override
            public boolean accept( String path ) {
                if ( path == null ) {
                    return false;
                }
                File file = new File( path );
                if ( !file.exists() ) {
                    return false;
                }
                return Info.isWorkingCopy( file );
            }

            public boolean accept( VFSFile file ) {
                if ( file == null) {
                    return false;   
                }
                return accept( file.getPath() );
            }

            public String getDescription() {
                return jEdit.getProperty( "ips.Import_files_in_under_SVN_version_control_only.", "Import files in under SVN version control only." );
            }
        };
    }
}
