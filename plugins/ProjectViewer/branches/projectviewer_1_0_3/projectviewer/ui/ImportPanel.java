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
package projectviewer.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.gjt.sp.util.Log;
import projectviewer.*;


/**
 * A panel for specifying the files to import.
 */
public class ImportPanel extends JPanel
         implements ActionListener
{

   final static String PROPS_FILE = "import.properties";

   private JCheckBox enableImport;
   private JTextArea includedExt, includedFiles, excludedDirs;

   /**
    * Create a new <code>ImportPanel</code>.
    */
   public ImportPanel()
   {
      initComponents();
      actionPerformed(null);
      loadDefaults();
   }

   /**
    * Returns <code>true</code> if importing is enabled.
    */
   public boolean isImportEnabled()
   {
      return enableImport.isSelected();
   }

   /**
    * Returns a set of included extensions.
    */
   public Set getIncludedExtensions()
   {
      return toSet(includedExt.getText());
   }

   /**
    * Returns a set of included files.
    */
   public Set getIncludedFiles()
   {
      return toSet(includedFiles.getText());
   }

   /**
    * Returns a set of excluded directories.
    */
   public Set getExcludedDirectories()
   {
      return toSet(excludedDirs.getText());
   }

   /**
    * Build a file list.
    */
   public List getFiles(File root)
   {
      List files = new ArrayList();
      buildFileList(root, files);
      return files;
   }

   /**
    * Load the default include/exclude values.
    */
   public void loadDefaults()
   {
      try {
         Properties props = loadProperties();
         includedExt.setText(props.getProperty("include-extensions"));
         includedFiles.setText(props.getProperty("include-files"));
         excludedDirs.setText(props.getProperty("exclude-dirs"));
      } catch (IOException e) {
         Log.log(Log.ERROR, this, e);
      }
   }

   /**
    * Handle the enable import check box.
    */
   public void actionPerformed(ActionEvent evt)
   {
      includedExt.setEnabled(enableImport.isSelected());
      includedFiles.setEnabled(enableImport.isSelected());
      excludedDirs.setEnabled(enableImport.isSelected());
   }

   /**
    * Build the file list.
    */
   private void buildFileList( File directory, List files )
   {
      File[] fileArray = directory.listFiles( new Filter() );

      for ( int i = 0; i < fileArray.length; ++i ) {
         if ( fileArray[i].isDirectory() ) {
            buildFileList( fileArray[i], files );
         } else {
            files.add(fileArray[i]);
         }
      }
   }

   /**
    * Load the specified list property to the specified set.
    */
   private static Set toSet(String value)
   {
      Set set = new HashSet();
      StringTokenizer strtok = new StringTokenizer( value );
      while ( strtok.hasMoreTokens() )
         set.add( strtok.nextToken() );
      return set;
   }

   /**
    * Load filter properties.
    */
   private Properties loadProperties() throws IOException
   {
      checkImportProperties();
      Properties props = new Properties();
      InputStream in = null;
      try {
         in = ProjectPlugin.getResourceAsStream( PROPS_FILE );
         props.load( in );
         return props;

      }
      finally {
         ProjectPlugin.close(in);
      }
   }

   /**
    * Perform a check for the import properties file.  If it isn't there, create one.
    */
   private void checkImportProperties()
   {
      File importFile = new File( ProjectPlugin.getResourcePath(PROPS_FILE) );
      if ( importFile.exists() )
         return ;
      OutputStream out = null;
      InputStream in = null;
      try {
         out = new FileOutputStream(importFile);
         in = getClass().getResourceAsStream("import-sample.properties");
         Pipe.pipe(in, out);

      } catch (IOException e) {
         ProjectPlugin.error(e);

      } finally {
         ProjectPlugin.close(in);
         ProjectPlugin.close(out);
      }
   }

   /**
    * Initialize components.
    */
   private void initComponents()
   {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();

      enableImport = new JCheckBox("Import file(s)");
      enableImport.addActionListener(this);
      enableImport.setSelected(true);
      gbc.anchor = gbc.WEST;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.insets = new Insets(0, 0, 11, 0);
      gbc.weightx = 1;
      gbc.weighty = .001;
      add(enableImport, gbc);

      JLabel incExtLabel = new JLabel("Include Extensions:");
      gbc.gridy = 1;
      gbc.insets = new Insets(0, 0, 2, 0);
      add(incExtLabel, gbc);

      includedExt = new JTextArea(4, 40);
      includedExt.setLineWrap(true);
      includedExt.setWrapStyleWord(true);
      gbc.fill = gbc.BOTH;
      gbc.gridy = 2;
      gbc.insets = new Insets(0, 0, 11, 0);
      gbc.weighty = .22;
      add(new JScrollPane(includedExt), gbc);

      JLabel incFilesLabel = new JLabel("Include Files:");
      gbc.gridy = 3;
      gbc.weighty = .001;
      gbc.insets = new Insets(0, 0, 2, 0);
      add(incExtLabel, gbc);

      includedFiles = new JTextArea(4, 40);
      includedFiles.setLineWrap(true);
      includedFiles.setWrapStyleWord(true);
      gbc.fill = gbc.BOTH;
      gbc.gridy = 4;
      gbc.insets = new Insets(0, 0, 11, 0);
      gbc.weighty = .22;
      add(new JScrollPane(includedFiles), gbc);

      JLabel excDirsLabel = new JLabel("Excluded Directories:");
      gbc.gridy = 5;
      gbc.weighty = .001;
      gbc.insets = new Insets(0, 0, 2, 0);
      add(excDirsLabel, gbc);

      excludedDirs = new JTextArea(4, 40);
      excludedDirs.setLineWrap(true);
      excludedDirs.setWrapStyleWord(true);
      gbc.fill = gbc.BOTH;
      gbc.gridy = 6;
      gbc.insets = new Insets(0, 0, 11, 0);
      gbc.weighty = .22;
      add(new JScrollPane(excludedDirs), gbc);

      gbc.weighty = .33;
      add(Box.createVerticalStrut(1), gbc);
   }

   /**
    * A file filter that filters based off a properties file.
    */
   private class Filter implements FileFilter
   {

      private Set includedExtensions;
      private Set includedFiles;
      private Set excludedDirectories;

      /**
       * Create a new <code>FileFilter</code>.
       */
      public Filter()
      {
         includedExtensions = getIncludedExtensions();
         includedFiles = getIncludedFiles();
         excludedDirectories = getExcludedDirectories();
      }

      /**
       * Accept files based of properties.
       */
      public boolean accept( File file )
      {
         if ( file.isFile() ) {
            if ( includedFiles.contains( file.getName() ) )
               return true;
            if ( includedExtensions.contains( getFileExtension( file ) ) )
               return true;
            return false;
         }
         return !excludedDirectories.contains( file.getName() ) ;
      }

      /**
       * Returns the file's extension.
       */
      private String getFileExtension( File file )
      {
         String fileName = file.getName();
         int dotIndex = fileName.lastIndexOf( '.' );
         if ( dotIndex == -1 || dotIndex == fileName.length() - 1 )
            return null;
         return fileName.substring( dotIndex + 1 );
      }

   }

}
