/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

import java.io.*;
import java.util.*;
import javax.swing.*;


/**
 * Imports project files.
 */
class ProjectFileImporter {
  
  private ProjectViewer viewer;
  private Filter filter;
  
  /**
   * Import files from the current directory.
   */
  public ProjectFileImporter( ProjectViewer aViewer ) {
    viewer = aViewer;
  }
  
  /**
   * Import project files.  This method will ask the user to specify an import
   * directory using a file chooser.
   */
  public void doImport() {
    JFileChooser chooser = viewer.createFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (chooser.showOpenDialog(viewer) != JFileChooser.APPROVE_OPTION) return;
    doImport( chooser.getSelectedFile() );
  }
  
  /**
   * Import project files starting from the given directory and traversing into
   * subdirectories.
   */
  public void doImport( File directory ) {
    List files = new ArrayList();
    buildFileList( directory, files );
    
    if ( files.isEmpty() ) {
      JOptionPane.showMessageDialog( viewer, 
                                     "No files were found.", 
                                     "Note", 
                                     JOptionPane.ERROR_MESSAGE );
      return;
    }

    // TODO: Perform import in background.
    viewer.getCurrentProject().importFiles( files );
    
    JOptionPane.showMessageDialog( viewer, 
                                   "Imported " + files.size() + " file(s) into your project", 
                                   "Import Successful", 
                                   JOptionPane.INFORMATION_MESSAGE );
  }
  
  /**
   * Build the file list.
   */
  private void buildFileList( File directory, List files ) {
    File[] fileArray = directory.listFiles( getFilter() );
    
    for ( int i=0; i < fileArray.length; ++i ) {
      if ( fileArray[i].isDirectory() ) {
        buildFileList( fileArray[i], files );
      } else {
        String path = fileArray[i].getAbsolutePath();
        if ( !viewer.getCurrentProject().isProjectFile( path ) )
          files.add( new ProjectFile( path ) );
      }
    }
  }
  
  /**
   * Returns a filter.
   */
  private Filter getFilter() {
    if ( filter == null )
      filter = new Filter();
    return filter;
  }
  
  /**
   * A file filter that filters based off a properties file.
   */
  private class Filter implements FileFilter {
    
    private Set includedExtensions;
    private Set includedFiles;
    private Set excludedDirectories;
    
    /**
     * Create a new <code>FileFilter</code>.
     */
    public Filter() {
      includedExtensions = new HashSet();
      includedFiles = new HashSet();
      excludedDirectories = new HashSet();
      
      try {
        Properties props = loadProperties();
        copyPropertyIntoSet( props, "include-extensions", includedExtensions );
        copyPropertyIntoSet( props, "include-files", includedFiles );
        copyPropertyIntoSet( props, "exclude-dirs", excludedDirectories );
        
      } catch ( IOException e ) {
        // TODO: Log.
      }
    }
    
    /**
     * Accept files based of properties.
     */
    public boolean accept( File file ) {
      if ( file.isFile() ) {
        if ( includedFiles.contains( file.getName() ) ) return true;
        if ( includedExtensions.contains( getFileExtension( file ) ) ) return true;
      } else {
        if ( !excludedDirectories.contains( file.getName() ) ) return true;
      }
      return false;
    }
    
    /**
     * Returns the file's extension.
     */
    private String getFileExtension( File file ) {
      String fileName = file.getName();
      int dotIndex = fileName.lastIndexOf( '.' );
      if ( dotIndex == -1 || dotIndex == fileName.length() - 1 )
        return null;
      return fileName.substring( dotIndex + 1 );
    }

    /**
     * Load the specified list property to the specified set.
     */
    private void copyPropertyIntoSet( Properties props, String propertyName, Set set ) {
      String list = props.getProperty( propertyName );
      if ( list == null ) return;
      
      StringTokenizer strtok = new StringTokenizer( list );
      while ( strtok.hasMoreTokens() )
        set.add( strtok.nextToken() );
    }
    
    /**
     * Load filter properties.
     */
    private Properties loadProperties() throws IOException {
      Properties props = new Properties();
      InputStream in = null;
      try {
        in = ProjectPlugin.getResourceAsStream( "import.properties" );
        props.load( in );
        return props;
      } finally {
        if ( in != null ) in.close();
      }
    }
     
  }
   
}
