package ise.plugin.svn.command;

import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;


public class SVNKit {
    /**
     * Initializes the svnkit library to work with a repository via
     * different protocols.
     */
    public static void setupLibrary() {
        // For using over http:// and https://
        DAVRepositoryFactory.setup();

        // For using over svn:// and svn+xxx://
        SVNRepositoryFactoryImpl.setup();

        // For using over file:///
        FSRepositoryFactory.setup();
    }

}
