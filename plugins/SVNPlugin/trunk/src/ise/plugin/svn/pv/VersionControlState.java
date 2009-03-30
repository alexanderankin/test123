
package ise.plugin.svn.pv;

import projectviewer.config.VersionControlService;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.*;

import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;

import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.data.StatusData;
import ise.plugin.svn.command.Status;
import ise.plugin.svn.io.*;
import ise.plugin.svn.gui.PVSVNOptionPane;

/**
 * Provide version control icons for file status to ProjectViewer.
 */
public class VersionControlState implements VersionControlService {

    // version control states for Subversion
    public final static int NONE = 0; // no state
    public final static int LOCAL_MOD = 1; // modified
    public final static int LOCAL_ADD = 2; // added
    public final static int LOCAL_RM = 3; // missing
    public final static int NEED_UPDATE = 4; // out of date
    public final static int CONFLICT = 5; // conflicted
    public final static int DELETED = 6; // deleted
    public final static int LOCKED = 7; // locked
    public final static int UNVERSIONED = 8;  // unversioned
    public final static int NORMAL = 9; // normal


    // icon definitions for the various states
    public final static Icon NORMAL_ICON = createIcon( "ise/plugin/svn/gui/icons/normal.png" );
    public final static Icon ADDED_ICON = createIcon( "ise/plugin/svn/gui/icons/added.png" );
    public final static Icon CONFLICT_ICON = createIcon( "ise/plugin/svn/gui/icons/conflict.png" );
    public final static Icon DELETED_ICON = createIcon( "ise/plugin/svn/gui/icons/deleted.png" );
    public final static Icon IGNORED_ICON = createIcon( "ise/plugin/svn/gui/icons/ignored.png" );
    public final static Icon LOCKED_ICON = createIcon( "ise/plugin/svn/gui/icons/locked.png" );
    public final static Icon MODIFIED_ICON = createIcon( "ise/plugin/svn/gui/icons/modified.png" );
    public final static Icon OUTOFDATE_ICON = createIcon( "ise/plugin/svn/gui/icons/outofdate.png" );
    public final static Icon READONLY_ICON = createIcon( "ise/plugin/svn/gui/icons/readonly.png" );
    public final static Icon UNVERSIONED_ICON = createIcon( "ise/plugin/svn/gui/icons/unversioned.png" );

    private static Icon createIcon( String name ) {
        return new ImageIcon( VersionControlState.class.getClassLoader().getResource( name ) );
    }

    // Caching mechanism is a hold over from working with a previous version of PV,
    // it doesn't seem to be necessary now, but I'm keeping the code here in case
    // I discover it's necessary.
    // <String for path, CacheItem for last time path status was pulled from SVN and state at that time>
    /*
    private static HashMap<String, CacheItem> cache = new HashMap<String, CacheItem>();

    class CacheItem {
        int state;
        long lastUpdate = System.currentTimeMillis();
        public CacheItem( int state ) {
            this.state = state;
        }
    }
    private long refreshTime = 60 * 1000;   // one minute, may need to adjust this
    */

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
    public int getFileState( VPTFile f ) {
        String path = f.getNodePath();
        /* not using the caching for now
        CacheItem item = cache.get( path );
        if ( item != null && item.lastUpdate > System.currentTimeMillis() - refreshTime ) {
            return item.state;
        }
        */
        SVNData data = new SVNData();
        List<String> paths = new ArrayList<String>();
        paths.add( path );
        data.setPaths( paths );
        data.setRecursive( false );

        // do NOT check if working copy is different than remote copy, that is
        // way too expensive, time-wise.
        data.setRemote( false );

        data.setOut( new ConsolePrintStream(new NullOutputStream()) );
        Status command = new Status();
        StatusData status = null;
        try {
            status = command.getStatus( data );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            status = null;
        }

        //CacheItem cacheItem = null;
        if ( status == null ) {
        //    cacheItem = new CacheItem(NONE ));
            return NONE;
        }

        if ( status.getAdded() != null ) {
        //    cacheItem = new CacheItem(LOCAL_ADD ));
            return LOCAL_ADD;
        }
        else if ( status.getConflicted() != null ) {
        //    cacheItem = new CacheItem(CONFLICT ));
            return CONFLICT;
        }
        else if ( status.getDeleted() != null ) {
        //    cacheItem = new CacheItem(DELETED ));
            return DELETED;
        }
        else if ( status.getLocked() != null ) {
        //    cacheItem = new CacheItem(LOCKED ));
            return LOCKED;
        }
        else if ( status.getMissing() != null ) {
        //    cacheItem = new CacheItem(LOCAL_RM ));
            return LOCAL_RM;
        }
        else if ( status.getModified() != null ) {
        //    cacheItem = new CacheItem(LOCAL_MOD ));
            return LOCAL_MOD;
        }
        else if ( status.getOutOfDate() != null ) {
        //    cacheItem = new CacheItem(NEED_UPDATE ));
            return NEED_UPDATE;
        }
        else if ( status.getUnversioned() != null ) {
        //    cacheItem = new CacheItem(UNVERSIONED ));
            return UNVERSIONED;
        }
        else {
        //    cacheItem = new CacheItem(NORMAL ));
            return NORMAL;
        }
        //if (cacheItem != null) {
        //    cache.put( path, cacheItem );
        //}
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
            case NORMAL:
                return NORMAL_ICON;
            case LOCAL_ADD:
                return ADDED_ICON;
            case UNVERSIONED:
            case NONE:
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
    public void dissociate( VPTProject proj ) {}

	/**
	 * This method should return the option pane to be shown. As with
	 * regular jEdit option panes, the label to be shown in the dialog
	 * should be defined by the "option.[pane_name].label" property.
	 *
	 * @param	project	The project that will be edited.
	 *
	 * @return An OptionPane instance, or null for no option pane.
	 */
     public OptionPane getOptionPane(VPTProject project) {
        return new PVSVNOptionPane(project.getName());   
     }


	/**
	 * This should return an OptionGroup to be shown. As with regular
	 * jEdit option groups, the label to be shown in the dialog
	 * should be defined by the "option.[group_name].label" property.
	 *
	 * @param	project	The project that will be edited.
	 *
	 * @return null
	 */
     public OptionGroup getOptionGroup(VPTProject project) {
        return null;   
     }

}