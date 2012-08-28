/*
Copyright (c) 2009, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package filesync;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.*;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.browser.*;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.ThreadUtilities;

import projectviewer.event.ProjectUpdate;
import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;

public class FileSyncPlugin extends EBPlugin {
    public static final String NAME = "filesync";

    // cache for properties
    private static HashMap<String, Properties> cache = new HashMap<String, Properties>();

    // git file filter
    private static final IOFileFilter gitFilter = FileFilterUtils.notFileFilter( FileFilterUtils.and( FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter( ".git" ) ) );

    // handle file sync on file save or files added/removed from a project
    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof BufferUpdate ) {
            BufferUpdate bu = ( BufferUpdate ) msg;
            if ( BufferUpdate.SAVED.equals( bu.getWhat() ) ) {
                FileSyncPlugin.syncFile( null, bu.getBuffer().getPath() );
            }
        } else if ( msg instanceof ProjectUpdate ) {
            ProjectUpdate pu = ( ProjectUpdate ) msg;
            if ( pu.getType().equals( ProjectUpdate.Type.FILES_CHANGED ) ) {
                String projectName = pu.getProject().getName();
                if ( pu.getAddedFiles() != null ) {
                    for ( VPTFile vptFile : pu.getAddedFiles() ) {
                        if ( vptFile != null ) {
                            FileSyncPlugin.syncFile( projectName, vptFile.getNodePath() );
                        }
                    }
                }
                if ( pu.getRemovedFiles() != null ) {
                    for ( VPTFile vptFile : pu.getRemovedFiles() ) {
                        if ( vptFile != null ) {
                            FileSyncPlugin.removeFile( projectName, vptFile.getNodePath() );
                        }
                    }
                }
            }
        }
    }

    /**
     * sync a single file
     * @param projectName The name of the project that the file belongs to.
     * @ param filename The name of the file to sync.
     */
    protected static void syncFile( String projectName, String filename ) {
        FileSyncPlugin.syncFile( projectName, filename, false );
    }

    /**
     * remove a single file from the sync destination
     * @param projectName The name of the project that the file belongs to.
     * @ param filename The name of the file to remove.
     */
    protected static void removeFile( String projectName, String filename ) {
        FileSyncPlugin.syncFile( projectName, filename, true );
    }

    /**
     * Deletes all files in the given directory.
     * Assumes 'target' is a directory, however, will still delete a single file.
     * @param target the file or directory to delete.
     */
    protected static void removeFiles( File target ) {
        FileUtils.deleteQuietly( target );
    }

    /**
     * Deletes all files from all defined targets for the given project.
     * @param projectName The name of the project to delete files for.
     */
    protected static void removeAllFiles( String projectName ) {
        if ( projectName == null ) {
            return;
        }
        Properties props = FileSyncPlugin.getSyncProperties( projectName );
        if ( props.size() == 0 ) {
            return;
        }

        int i = 0;
        while ( true ) {
            String sourceFolder = props.getProperty( "sourcefolder." + i );
            if ( sourceFolder == null ) {
                break;
            }
            String target = props.getProperty( "target." + i );
            if ( target != null && !target.isEmpty() ) {
                FileSyncPlugin.removeFiles( new File( target ) );
            }
            ++i;
        }
    }

    /**
     * sync a single file or remove a single file from the sync destination
     */
    private static void syncFile( String projectName, String filename, boolean delete ) {
        if ( projectName == null ) {
            VPTProject project = PVHelper.getProjectForFile( filename );
            if ( project != null ) {
                projectName = project.getName();
            }
        }
        if ( projectName == null ) {
            return;
        }
        Properties props = FileSyncPlugin.getSyncProperties( projectName );
        if ( props.size() == 0 ) {
            return;
        }

        if ( "false".equals( props.getProperty( "enableSync", "false" ) ) ) {
            return;
        }

        boolean noCvs = "true".equals( props.getProperty( "noCvs", "true" ) );
        boolean noSvn = "true".equals( props.getProperty( "noSvn", "true" ) );
        boolean noGit = "true".equals( props.getProperty( "noGit", "true" ) );

        int i = 0;
        while ( true ) {
            String sourceFolder = props.getProperty( "sourcefolder." + i );
            if ( sourceFolder == null ) {
                break;
            }
            String include = props.getProperty( "include." + i, "" );
            String exclude = props.getProperty( "exclude." + i, "" );
            String target = props.getProperty( "target." + i, "" );

            // create filters
            IOFileFilter includeFilter = null;
            String[] includes = include.split( "," );
            for ( String in : includes ) {
                if ( includeFilter == null ) {
                    includeFilter = new GlobFileFilter( in.trim() );
                } else {
                    includeFilter = FileFilterUtils.or( includeFilter, new GlobFileFilter( in.trim() ) );
                }
            }

            IOFileFilter excludeFilter = null;
            String[] excludes = exclude.split( "," );
            for ( String in : excludes ) {
                if ( excludeFilter == null ) {
                    excludeFilter = new GlobFileFilter( in.trim() );
                } else {
                    excludeFilter = FileFilterUtils.or( excludeFilter, new GlobFileFilter( in.trim() ) );
                }
            }

            if ( includeFilter == null && excludeFilter == null ) {
                return;
            }

            IOFileFilter filter = null;
            if ( excludeFilter == null ) {
                filter = includeFilter;
            } else if ( includeFilter == null ) {
                filter = FileFilterUtils.notFileFilter( excludeFilter );
            } else {
                filter = FileFilterUtils.and( includeFilter, FileFilterUtils.notFileFilter( excludeFilter ) );
            }

            if ( noCvs ) {
                filter = FileFilterUtils.makeCVSAware( filter );
            }
            if ( noSvn ) {
                filter = FileFilterUtils.makeSVNAware( filter );
            }
            if ( noGit ) {
                filter = FileFilterUtils.and( filter, gitFilter );
            }

            // apply filters
            File sourceFile = new File( filename );
            if ( filter.accept( sourceFile ) ) {
                try {
                    filename = filename.substring( sourceFolder.length() );
                    File file = FileUtils.getFile( new File( target ), filename.split( File.separator ) );
                    if ( delete ) {
                        FileUtils.deleteQuietly( file );
                    } else {
                        FileUtils.copyFile( sourceFile, file );
                    }
                    break;
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
            ++i;
        }
    }

    protected static void syncAllFiles( String projectName ) {
        final Properties props = FileSyncPlugin.getSyncProperties( projectName );
        if ( props.size() == 0 ) {
            return;
        }

        Runnable runnable = new Runnable() {
            public void run() {
                boolean noCvs = "true".equals( props.getProperty( "noCvs", "true" ) );
                boolean noSvn = "true".equals( props.getProperty( "noSvn", "true" ) );
                boolean noGit = "true".equals( props.getProperty( "noGit", "true" ) );

                int i = 0;
                while ( true ) {
                    String sourceFolder = props.getProperty( "sourcefolder." + i );
                    if ( sourceFolder == null ) {
                        break;
                    }
                    String include = props.getProperty( "include." + i, "" );
                    String exclude = props.getProperty( "exclude." + i, "" );
                    String target = props.getProperty( "target." + i, "" );

                    // create filters
                    IOFileFilter includeFilter = null;
                    String[] includes = include.split( "," );
                    for ( String in : includes ) {
                        if ( in == null || in.isEmpty() ) {
                            continue;
                        }
                        if ( includeFilter == null ) {
                            includeFilter = new GlobFileFilter( in.trim() );
                        } else {
                            includeFilter = FileFilterUtils.or( includeFilter, new GlobFileFilter( in.trim() ) );
                        }
                    }

                    IOFileFilter excludeFilter = null;
                    String[] excludes = exclude.split( "," );
                    for ( String in : excludes ) {
                        if ( in == null || in.isEmpty() ) {
                            continue;
                        }
                        if ( excludeFilter == null ) {
                            excludeFilter = new GlobFileFilter( in.trim() );
                        } else {
                            excludeFilter = FileFilterUtils.or( excludeFilter, new GlobFileFilter( in.trim() ) );
                        }
                    }

                    if ( includeFilter == null && excludeFilter == null ) {
                        return;
                    }

                    IOFileFilter filter = null;
                    if ( excludeFilter == null ) {
                        filter = includeFilter;
                    } else if ( includeFilter == null ) {
                        filter = FileFilterUtils.notFileFilter( excludeFilter );
                    } else {
                        filter = FileFilterUtils.and( includeFilter, FileFilterUtils.notFileFilter( excludeFilter ) );
                    }

                    if ( noCvs ) {
                        filter = FileFilterUtils.makeCVSAware( filter );
                    }
                    if ( noSvn ) {
                        filter = FileFilterUtils.makeSVNAware( filter );
                    }
                    if ( noGit ) {
                        filter = FileFilterUtils.makeGITAware( filter );
                    }

                    // apply the filters
                    try {
                        FileUtils.copyDirectory( new File( sourceFolder ), new File( target ), filter, true );
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }

                    ++i;
                }
            }
        };
        
        ThreadUtilities.runInBackground(runnable);
    }

    /**
     * @return A Properties. It may be empty, but it won't be null.
     */
    protected static Properties getSyncProperties( String projectName ) {
        Properties props = cache.get( projectName );
        if ( props != null ) {
            return props;
        }

        props = new Properties();

        try {
            EditPlugin plugin = jEdit.getPlugin( "filesync.FileSyncPlugin", false );
            File pluginHome = plugin.getPluginHome();
            if ( !pluginHome.exists() ) {
                pluginHome.mkdirs();
            }
            File propsFile = new File( pluginHome, projectName );
            if ( !propsFile.exists() ) {
                return props;
            }
            Reader reader = new BufferedReader( new FileReader( propsFile ) );
            props.load( reader );
            reader.close();
        } catch ( Exception e ) {            // NOPMD
        }
        cache.put( projectName, props );
        return props;
    }

    /**
     * Stores the given properties for a project.
     * @param projectName The name of the project to store the properties for.
     * @param props The properties to store.
     */
    protected static void saveSyncProperties( String projectName, Properties props ) {
        cache.put( projectName, props );
        try {
            EditPlugin plugin = jEdit.getPlugin( "filesync.FileSyncPlugin", false );
            File pluginHome = plugin.getPluginHome();
            if ( !pluginHome.exists() ) {
                pluginHome.mkdirs();
            }
            File propsFile = new File( pluginHome, projectName );
            Writer writer = new BufferedWriter( new FileWriter( propsFile ) );
            props.store( writer, "MACHINE GENERATED, DO NOT MODIFY" );
            writer.close();
        } catch ( Exception e ) {            // NOPMD
        }
    }
}