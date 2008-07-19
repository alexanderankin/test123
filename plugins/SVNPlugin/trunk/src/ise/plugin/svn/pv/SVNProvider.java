package ise.plugin.svn.pv;

import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.io.File;

import projectviewer.vpt.IconComposer;

import ise.plugin.svn.data.StatusData;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.command.Status;

/**
 * This class lets ProjectViewer display an "overlay" icon in the PV tree
 * to show the status of a file like TortoiseSVN.
 */
public class SVNProvider implements IconComposer.VCProvider {

    public final static Icon NORMAL_ICON = getIcon( "ise/plugin/svn/gui/icons/normal.png" );
    public final static Icon ADDED_ICON = getIcon( "ise/plugin/svn/gui/icons/added.png" );
    public final static Icon CONFLICT_ICON = getIcon( "ise/plugin/svn/gui/icons/conflict.png" );
    public final static Icon DELETED_ICON = getIcon( "ise/plugin/svn/gui/icons/deleted.png" );
    public final static Icon IGNORED_ICON = getIcon( "ise/plugin/svn/gui/icons/ignored.png" );
    public final static Icon LOCKED_ICON = getIcon( "ise/plugin/svn/gui/icons/locked.png" );
    public final static Icon MODIFIED_ICON = getIcon( "ise/plugin/svn/gui/icons/modified.png" );
    public final static Icon OUTOFDATE_ICON = getIcon( "ise/plugin/svn/gui/icons/outofdate.png" );
    public final static Icon READONLY_ICON = getIcon( "ise/plugin/svn/gui/icons/readonly.png" );
    public final static Icon UNVERSIONED_ICON = getIcon( "ise/plugin/svn/gui/icons/unversioned.png" );

    private static Icon getIcon( String name ) {
        return new ImageIcon( SVNProvider.class.getClassLoader().getResource( name ) );
    }

    // <String for path, CacheItem for last time path status was pulled from SVN and state at that time>
    private static HashMap<String, CacheItem> cache = new HashMap<String, CacheItem>();

    class CacheItem {
        int state;
        long lastUpdate = System.currentTimeMillis();
        public CacheItem( int state ) {
            this.state = state;
        }
    }
    private long refreshTime = 60 * 1000;   // one minute

    public int getFileState( File f, String path ) {
        //return IconComposer.VC_STATE_NONE;
        CacheItem item = cache.get( path );
        if ( item != null && item.lastUpdate > System.currentTimeMillis() - refreshTime ) {
            return item.state;
        }
        SVNData data = new SVNData();
        List<String> paths = new ArrayList<String>();
        paths.add( path );
        data.setPaths( paths );
        data.setRecursive( false );

        // do NOT check if working copy is different than remote copy
        data.setRemote( false );

        data.setOut( null );
        Status command = new Status();
        StatusData status = null;
        try {
            status = command.getStatus( data );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            status = null;
        }
        if ( status == null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_NONE ) );
            return IconComposer.VC_STATE_NONE;
        }

        if ( status.getAdded() != null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_LOCAL_ADD ) );
            return IconComposer.VC_STATE_LOCAL_ADD;
        }
        else if ( status.getConflicted() != null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_CONFLICT ) );
            return IconComposer.VC_STATE_CONFLICT;
        }
        else if ( status.getDeleted() != null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_DELETED ) );
            return IconComposer.VC_STATE_DELETED;
        }
        else if ( status.getLocked() != null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_LOCKED ) );
            return IconComposer.VC_STATE_LOCKED;
        }
        else if ( status.getMissing() != null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_LOCAL_RM ) );
            return IconComposer.VC_STATE_LOCAL_RM;
        }
        else if ( status.getModified() != null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_LOCAL_MOD ) );
            return IconComposer.VC_STATE_LOCAL_MOD;
        }
        else if ( status.getOutOfDate() != null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_NEED_UPDATE ) );
            return IconComposer.VC_STATE_NEED_UPDATE;
        }
        else if ( status.getUnversioned() != null ) {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_UNVERSIONED ) );
            return IconComposer.VC_STATE_UNVERSIONED;
        }
        else {
            cache.put( path, new CacheItem( IconComposer.VC_STATE_NORMAL ) );
            return IconComposer.VC_STATE_NORMAL;
        }
    }

    public Icon getIcon( int state ) {
        switch ( state ) {
            case IconComposer.VC_STATE_LOCAL_MOD:
                return MODIFIED_ICON;
            case IconComposer.VC_STATE_LOCAL_ADD:
                return ADDED_ICON;
            case IconComposer.VC_STATE_LOCAL_RM:
                return DELETED_ICON;
            case IconComposer.VC_STATE_NEED_UPDATE:
                return OUTOFDATE_ICON;
            case IconComposer.VC_STATE_CONFLICT:
                return CONFLICT_ICON;
            case IconComposer.VC_STATE_NONE:
                return null;
            case IconComposer.VC_STATE_DELETED:
                return DELETED_ICON;
            case IconComposer.VC_STATE_LOCKED:
                return LOCKED_ICON;
            case IconComposer.VC_STATE_UNVERSIONED:
                return UNVERSIONED_ICON;
            case IconComposer.VC_STATE_NORMAL:
            default:
                return NORMAL_ICON;
        }
    }

}