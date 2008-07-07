package ise.plugin.svn.pv;

import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.io.File;

import projectviewer.vpt.IconComposer;

import ise.plugin.svn.data.StatusData;
import ise.plugin.svn.data.SVNData;
import ise.plugin.svn.command.Status;

public class SVNProvider implements IconComposer.VCProvider {

    public final static Icon NORMAL_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/normal.png" ) );
    public final static Icon ADDED_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/added.png" ) );
    public final static Icon CONFLICT_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/conflict.png" ) );
    public final static Icon DELETED_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/deleted.png" ) );
    public final static Icon IGNORED_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/ignored.png" ) );
    public final static Icon LOCKED_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/locked.png" ) );
    public final static Icon MODIFIED_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/modified.png" ) );
    public final static Icon OUTOFDATE_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/outofdate.png" ) );
    public final static Icon READONLY_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/readonly.png" ) );
    public final static Icon UNVERSIONED_ICON =
        new ImageIcon( SVNProvider.class.getClassLoader().getResource( "ise/plugin/svn/gui/icons/unversioned.png" ) );


    public int getFileState( File f, String path ) {
        //return IconComposer.VC_STATE_NONE;
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
        }
        if ( status == null ) {
            return IconComposer.VC_STATE_NONE;
        }

        if ( status.getAdded() != null ) {
            return IconComposer.VC_STATE_LOCAL_ADD;
        }
        else if ( status.getConflicted() != null ) {
            return IconComposer.VC_STATE_CONFLICT;
        }
        else if ( status.getDeleted() != null ) {
            return IconComposer.VC_STATE_DELETED;
        }
        else if ( status.getLocked() != null ) {
            return IconComposer.VC_STATE_LOCKED;
        }
        else if ( status.getMissing() != null ) {
            return IconComposer.VC_STATE_LOCAL_RM;
        }
        else if ( status.getModified() != null ) {
            return IconComposer.VC_STATE_LOCAL_MOD;
        }
        else if ( status.getOutOfDate() != null ) {
            return IconComposer.VC_STATE_NEED_UPDATE;
        }
        else if ( status.getUnversioned() != null ) {
            return IconComposer.VC_STATE_UNVERSIONED;
        }
        else {
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