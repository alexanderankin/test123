/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
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
package projectviewer.persist;

import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.io.*;
import java.util.*;

import projectviewer.vpt.VPTFile;

/** 
 *	This class will achive the project files to a zip/jar file. 
 *	Handy to have this in there so files can be transferred elsewhere
 *
 *	@author		Matthew Payne
 *	@version	$Id$
 */
public class ProjectZipper {
		
	private String[] files;
	private String manifest;
	private File manifestFile;
	private boolean includeManifest = true;
	private boolean loadManifest = false;
	private boolean aborted = false;
	private int writtenBytes;
	private int totalFileSize;
	
	private final int BUFFERSIZE = 32768;
	private final String FILESEPARATOR = System.getProperty("file.separator");
	
	/** 
	 * constructs an empty ProjectZipper class
	 */
	public ProjectZipper() {
		manifest = "";
	}	
	
// -------------------------------------- Begin Method Section ----------------------------------------


	/**
	 * sets the aborted variable
	 *
	 * @param true if the creation process should be stopped
	 */
	public void setAborted(boolean b) {
		aborted = b;
	}
	
	
	/** 
	 * sets the manifest include option
	 *
	 * @param true for including manifest, false for non-including manifest
	 */
	public void setIncludeManifest(boolean b) {
		includeManifest = b;
	}
	
	
	/** 
	 * returns the manifest include option
	 *
	 * @return manifest include option
	 */
	public boolean getIncludeManifest() {
		return includeManifest;
	}
	
	/** 
	 * sets the manifest load option
	 *
	 * @param true for loading manifest, false for non-loading manifest
	 */
	public void setLoadManifest(boolean b) {
		loadManifest = b;
	}
	
	
	/** 
	 * returns the manifest load option
	 *
	 * @return manifest include option
	 */
	public boolean getLoadManifest() {
		return loadManifest;
	}
	
		
	/** 
	 * sets the files for the jar file
	 *
	 * @param files for the jar file
	 */
	public void setFiles(String[] f) {
		files = f;
	}
	
	
	/** 
	 * returns the files of the jar file
	 *
	 * @return files of the jar file
	 */
	public String[] getFiles() {
		return files;
	}
	
	
	/** 
	 * returns the file at the given index of the jar file
	 *
	 * @param index
	 * @return the file at the given index of the jar file
	 */
	public String getFile(int i) {
		return files[i];
	}
	
	
	/** 
	 * sets the manifest content for the jar file
	 *
	 * @param manfest content for the jar file
	 */
	public void setManifest(String m) {
		manifest = m;
	}
	
	
	/** 
	 * returns the manifest content of the jar file
	 *
	 * @return manifest content of the jar file
	 */
	public String getManifest() {
		return manifest;
	}
	
	
	/** 
	 * sets the manifest file for the jar file
	 *
	 * @param manfest file for the jar file
	 */
	public void setManifestFile(File f) {
		manifestFile = f;
	}
	
	
	/** 
	 * returns the manifest file of the jar file
	 *
	 * @return manifest file of the jar file
	 */
	public File getManifestFile() {
		return manifestFile;
	}
	
	
	
	/** 
	 * returns the number of files of the jar file
	 *
	 * @return number of files of the jar file
	 */
	public int getLength() {
		return files.length;
	}
	
	
	/** 
	 * writes the manifest file
	 *
	 * @return the manifest file
	 */
	private File writeManifest() {
		File fManifest = new File("Manifest.tmp");
		try {
			BufferedWriter bout = new BufferedWriter(new FileWriter(fManifest));
		
			if (!manifest.equals("")) {
				bout.write("Manifest-Version: 1.0");
				bout.newLine();
				bout.write("Created-By: JEdit 4.1");
				bout.newLine();
				bout.write(manifest);
				bout.newLine();
				bout.newLine();
			}
			
			bout.close();
		} catch (Exception e) {
			System.out.println("[writeManifest(), JarWriter] ERROR\n" + e);
		}
		
		return fManifest;	
	}
	
	
	/**
	 * sets the message during the creation process
	 *
	 * @param value to set
	 */
	private void setMessage(int a) {
		String s = new String(new Integer(a).toString());
		int l = s.length();
			
		if (l > 3 && l < 6) s = s.substring(0, l - 3) + "'" + s.substring(l - 3, l);
		if (l == 6) s = s.substring(0, l - 3) + "'" + s.substring(l - 3, l);
		if (l > 6) s = s.substring(0, l - 6) + "'" + s.substring(l - 6, l - 3) + "'" + s.substring(l - 3, l);
		
		final String s2 = s;
		
		/*SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JarBuilder.lMessage.setText("Compressing " + s2 + " KB - please wait...");
			}
		});*/
	}

	/**
	 * calculates the size of one file (kb)
	 *
	 * @param file
	 */
	private int getFileSize(File f) {
		File[] dContent;
		int dSize;
		
		if (f.isDirectory() ==  false) {
			return (int)(f.length() / 1024f);
		}
		else {
			dContent = f.listFiles();
			dSize = 0;
			for (int a = 0; a < dContent.length; a++) {
				dSize += getFileSize(dContent[a]);	
			}
			return dSize;
		}
	}

	
	/**
	 * calculates the size of all files (kb)
	 *
	 * @return the total file size
	 */
	private int getTotalFileSize() {
		File f;
		int totalSize = 0;
		
		for (int i = 0; i < files.length; i++) {
			f = new File(files[i]);
			totalSize += getFileSize(f);		
		}
		return totalSize;
	}
	 
	/** 
	 * writes the manifest entry in the jar file
	 *
	 * @param file to write
	 * @param current JarOutputStream
	 */
	private void writeManifestEntry(File f, JarOutputStream out) {
		// buffer
		byte[] buffer = new byte[BUFFERSIZE];
			
		// read bytes
		int bytes_read;
		
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(f), BUFFERSIZE);
			String en = "META-INF" + "/" + "MANIFEST.MF";
			out.putNextEntry(new ZipEntry(en));
			while ((bytes_read = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes_read);
			}
				
			in.close();
			out.closeEntry();
		} catch (Exception e) {
			System.out.println("[writeManifestEntry(), JarWriter] ERROR\n" + e);
		}
	}


	/** 
	 * creates a jar/zip file including the files and the manifest file
	 *
	 * @param the jar/zip file
	 * @param fileIterator interator of the Project Files
	 * @param RootPath root path of the project, used to determine what directory level files will be put 
	 * 	  inside the archive.
	 * @return true if the file was successfully built, false if there was an error during the building process
	 */
	public boolean createProjectAchive(File archiveFile, Iterator fileIterator, String RootPath) {
		File f;
		boolean written;
		
		try {
			// target
			JarOutputStream out = new JarOutputStream(new FileOutputStream(archiveFile));
			out.setComment("This file was created by jEdit Project Viewer\nCheck http://jedit.org !");
			// compression
			out.setLevel(9);
			
		while (fileIterator.hasNext()) {
			VPTFile pf = (VPTFile) fileIterator.next();
			String RelativePath;
			
			if (pf.getNodePath().startsWith(RootPath)) {
				RelativePath = pf.getNodePath().substring(RootPath.length());
				RelativePath = RelativePath.substring(0, (RelativePath.length() - pf.getName().length()) - 1);
			} else {
				// TODO: check this out!
				// dont't add files not under the project root?!?!?
				continue;
			}
		
			System.out.println(RelativePath);
			f = new File(pf.getNodePath());	
			written = writeEntry(f, out, RelativePath);
				if (!written) {
					out.close();
					archiveFile.delete();
					return false;
				}
			
		 }
		
			out.close();
			return true;
			
		} catch (Exception e) {
			System.out.println("[createJar(), JarWriter] ERROR\n" + e);
			return false;
		}
	
	
	
	}
	
	/** 
	 * creates a jar file including the files and the manifest file
	 *
	 * @param the jar file
	 * @param true to compress, false to not compress
	 * @return true if the file was successfully built, false if there was an error during the building process
	 */
	public boolean createJar(File fj, boolean compress) {
		File f;
		boolean written;
		
		try {
			// target
			JarOutputStream out = new JarOutputStream(new FileOutputStream(fj));
			out.setComment("This file was created by JarBuilder\nCheck http://fulgur.ch.vu !");
			
			// compression
			if (compress) out.setLevel(9);
			else out.setLevel(0);
			
			// preparations
			totalFileSize = getTotalFileSize();
			//setBarMax(totalFileSize);
			setMessage(totalFileSize);
			//setBar(0, "compressing " + files[0]);
			aborted = false;
			writtenBytes = 0;

		
			// add files
			for (int i = 0; i < files.length; i++) {
				f = new File(files[i]);
				written = writeEntry(f, out, 1);
				if (!written) {
					out.close();
					fj.delete();
					return false;
				}
			}
			
			
			// add manifest
			if (includeManifest) {
				if (loadManifest == false) {
					f = writeManifest();
					writeManifestEntry(f, out);
					f.delete();
				}
				else {
					writeManifestEntry(manifestFile, out);
				}
			}
			
			out.close();
			return true;
			
		} catch (Exception e) {
			System.out.println("[createJar(), JarWriter] ERROR\n" + e);
			return false;
		}
	}
	
	/** 
	 * writes entries in the jar file
	 *
	 * @param file to write
	 * @param current JarOutputStream
	 * @param RelativePath: path of the file within the archive directory structure
	 * @return true if the file was successfully written, false if there was an error during the writing
	 */
	private boolean writeEntry(File f, JarOutputStream out, String RelativePath) {
		String en = "";
		File[] dContent;
		int i;
		String fPath;
		
		// buffer
		byte[] buffer = new byte[BUFFERSIZE];
			
		// read bytes
		int bytes_read;
		
		try {
			if (f.isDirectory() == false) {	
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(f), BUFFERSIZE);
				
				fPath = f.getPath().substring(f.getPath().lastIndexOf(FILESEPARATOR));
				en = RelativePath + fPath;
				out.putNextEntry(new ZipEntry(en));
				
				while ((bytes_read = in.read(buffer)) != -1) {
					// do the work
					out.write(buffer, 0, bytes_read);
					
					// check if the user has aborted the creation process
					if (aborted) {
						in.close();
						out.closeEntry();
						return false;
					}
					
					// update progress bar
					writtenBytes += bytes_read;
					// setBar((int)(writtenBytes / 1024), "compressing " + f.getPath());
				}

				in.close();
				out.closeEntry();
				return true;
			}
			else {
				dContent = f.listFiles();
				for (int a = 0; a < dContent.length; a++) {
					writeEntry(dContent[a], out, RelativePath);
					// check if the user has aborted the creation process
					if (aborted) {
						return false;
					}
				}
			}	
		} catch (Exception e) {
			System.out.println("[writeEntry(), JarWriter] ERROR\n" + e);
			return false;
		}
		return true;
	}
	
	
	 /** 
	 * writes entries in the jar file
	 *
	 * @param file to write
	 * @param current JarOutputStream
	 * @param depth of the directory structure
	 * @return true if the file was successfully written, false if there was an error during the writing
	 */
	private boolean writeEntry(File f, JarOutputStream out, int depth) {
		String en = "";
		File[] dContent;
		int i;
		String fPath;
		
		// buffer
		byte[] buffer = new byte[BUFFERSIZE];
			
		// read bytes
		int bytes_read;
		
		try {
			if (f.isDirectory() == false) {	
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(f), BUFFERSIZE);
				
				i = f.getPath().length();
				fPath = f.getPath();
				for (int a = 0; a <= depth; a++) {
					i = fPath.lastIndexOf(FILESEPARATOR, i) - 1;
				}
				
				
				en = fPath.substring(i + 2, fPath.length());
				out.putNextEntry(new ZipEntry(en));
				
				while ((bytes_read = in.read(buffer)) != -1) {
					// do the work
					out.write(buffer, 0, bytes_read);
					
					// check if the user has aborted the creation process
					if (aborted) {
						in.close();
						out.closeEntry();
						return false;
					}
					
					// update progress bar
					writtenBytes += bytes_read;
					// setBar((int)(writtenBytes / 1024), "compressing " + f.getPath());
				}

				in.close();
				out.closeEntry();
				return true;
			}
			else {
				dContent = f.listFiles();
				for (int a = 0; a < dContent.length; a++) {
					writeEntry(dContent[a], out, depth + 1);
					// check if the user has aborted the creation process
					if (aborted) {
						return false;
					}
				}
			}	
		} catch (Exception e) {
			System.out.println("[writeEntry(), JarWriter] ERROR\n" + e);
			return false;
		}
		return true;
	}	
	
}

