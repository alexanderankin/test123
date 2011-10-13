/*
 * Project.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2010 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gatchan.phpparser.project;

import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.project.itemfinder.QuickAccessItemFinder;
import gatchan.phpparser.sidekick.PHPSideKickParser;
import net.sourceforge.phpdt.internal.compiler.ast.ClassHeader;
import net.sourceforge.phpdt.internal.compiler.ast.MethodHeader;
import net.sourceforge.phpdt.internal.compiler.ast.InterfaceDeclaration;
import net.sourceforge.phpdt.internal.compiler.ast.MethodDeclaration;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.*;

import java.io.*;
import java.util.*;

/**
 * A Project.
 *
 * @author Matthieu Casanova
 * @author $Id$
 */
public class Project
{
	/**
	 * The file where the project will be saved.
	 */
	private final File file;

	/**
	 * Tells that the project needs to be saved.
	 */
	private boolean needSave;

	/**
	 * The directory containing the datas of the project.
	 */
	private File dataDirectory;

	/**
	 * The file where the classes will be serialized.
	 */
	private File classFile;

	/**
	 * The file where the interfaces will be serialized.
	 */
	private File interfaceFile;

	/**
	 * The file where the methods will be serialized.
	 */
	private File methodFile;
	private File fileFile;

	/**
	 * The properties
	 */
	private final Properties properties = new Properties();

	/**
	 * This table will contains class names (lowercase) as key and {@link ClassHeader} as values.
	 */
	private Hashtable<String, Object> classes;

	/**
	 * This table will contains interfaces names (lowercase) as key and {@link InterfaceDeclaration} as values.
	 */
	private Hashtable<String, Object> interfaces;

	/**
	 * This table will contains class names (lowercase) as key and {@link MethodHeader} as values.
	 */
	private Hashtable<String, Object> methods;

	/**
	 * This table will contains as key the file path, and as value a {@link List} containing {@link PHPItem}
	 */
	private Hashtable<String, List<PHPItem>> files;

	/**
	 * The quick access item finder.
	 */
	private QuickAccessItemFinder quickAccess;

	/**
	 * The list of excluded folders. It contains {@link String}
	 */
	private List<String> excludedFolders;

	/**
	 * Create a new empty project.
	 *
	 * @param name    the name of the project
	 * @param version the version of the project
	 */
	public Project(String name, String version)
	{
		properties.setProperty("name", name);
		properties.setProperty("version", version);
		reset();
		file = getValidFileName(ProjectManager.projectDirectory + File.separator + name);
		init();
		needSave = true;
	}

	/**
	 * Open an existing project.
	 *
	 * @param file the project file
	 * @throws FileNotFoundException exception if the file doesn't exists
	 * @throws InvalidProjectPropertiesException
	 *                               exception if the project is in an old or invalid format
	 */
	public Project(File file) throws FileNotFoundException, InvalidProjectPropertiesException
	{
		this.file = file;
		quickAccess = new QuickAccessItemFinder();
		FileInputStream inStream = null;
		try
		{
			inStream = new FileInputStream(file);
			properties.load(inStream);
			init();
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			try
			{
				if (inStream != null)
				{
					inStream.close();
				}
			}
			catch (IOException e)
			{
				Log.log(Log.ERROR, this, e);
			}
		}
		checkProperties();
	}

	/**
	 * Reset the project. It should be used before reparsing all files
	 */
	private void reset()
	{
		classes = new Hashtable<String, Object>();
		interfaces = new Hashtable<String, Object>();
		methods = new Hashtable<String, Object>();
		files = new Hashtable<String, List<PHPItem>>();
		quickAccess = new QuickAccessItemFinder();
	}

	private void init()
	{
		dataDirectory = new File(file.getParent(), file.getName().substring(0, file.getName().length() - 14) + "_datas");
		//todo : check the return of mkdir
		dataDirectory.mkdir();
		classFile = new File(dataDirectory, "classes.ser");
		methodFile = new File(dataDirectory, "methods.ser");
		fileFile = new File(dataDirectory, "files.ser");
		interfaceFile = new File(dataDirectory, "interfaces.ser");
		excludedFolders = new ArrayList<String>();
	}

	/**
	 * Load the project.
	 */
	public void load()
	{
		long start = System.currentTimeMillis();
		String excludedString = properties.getProperty("excluded");
		if (excludedString != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(excludedString, "\n");
			while (tokenizer.hasMoreTokens())
			{
				excludedFolders.add(tokenizer.nextToken());
			}
		}

		try
		{
			classes = readObjects(classFile);
			Collection<Object> collection = classes.values();
			Iterator<Object> iterator = collection.iterator();
			while (iterator.hasNext())
			{
				Object o = iterator.next();
				if (o instanceof ClassHeader)
				{
					loadClassHeader((ClassHeader) o);
				}
				else
				{
					Iterable<ClassHeader> list = (Iterable<ClassHeader>) o;
					for (ClassHeader classHeader : list)
					{
						loadClassHeader(classHeader);
					}
				}
			}

			interfaces = readObjects(interfaceFile);
			collection = interfaces.values();
			iterator = collection.iterator();
			while (iterator.hasNext())
			{
				Object o = iterator.next();
				if (o instanceof InterfaceDeclaration)
				{
					loadInterface((InterfaceDeclaration) o);
				}
				else
				{
					Iterable<InterfaceDeclaration> list = (Iterable<InterfaceDeclaration>) o;
					for (InterfaceDeclaration interfaceDeclaration : list)
					{
						loadInterface(interfaceDeclaration);
					}
				}
			}

			methods = readObjects(methodFile);
			collection = methods.values();
			iterator = collection.iterator();

			while (iterator.hasNext())
			{
				Object item = iterator.next();
				if (item instanceof MethodHeader)
				{
					quickAccess.addToIndex((PHPItem) item);
				}
				else
				{
					Iterable<PHPItem> list = (Iterable<PHPItem>) item;
					for (PHPItem aList : list)
					{
						quickAccess.addToIndex(aList);
					}
				}
			}

			files = readObjects(fileFile);
		}
		catch (FileNotFoundException e)
		{
			Log.log(Log.ERROR, this, e.getMessage());
			GUIUtilities.error(jEdit.getActiveView(),
				"gatchan-phpparser.errordialog.unabletoreadproject",
				new String[]{e.getMessage()});
			reset();
		}
		catch (InvalidClassException e)
		{
			Log.log(Log.WARNING,
				this,
				"A class is invalid probably because the project used an old plugin " + e.getMessage());
			GUIUtilities.error(jEdit.getActiveView(), "gatchan-phpparser.errordialog.invalidprojectformat", null);
			reset();
		}
		catch (ClassNotFoundException e)
		{
			//should never happen
			Log.log(Log.ERROR, this, e);
			GUIUtilities.error(jEdit.getActiveView(), "gatchan-phpparser.errordialog.unexpectederror", null);
			reset();
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
			GUIUtilities.error(jEdit.getActiveView(),
				"gatchan-phpparser.errordialog.unabletoreadproject",
				new String[]{e.getMessage()});
			reset();
		}
		long end = System.currentTimeMillis();
		Log.log(Log.DEBUG, this, "Project loaded in " + (end - start) + "ms");
	}

	private void loadInterface(InterfaceDeclaration interfaceDeclaration)
	{
		quickAccess.addToIndex(interfaceDeclaration);
		for (int i = 0; i < interfaceDeclaration.size(); i++)
		{
			quickAccess.addToIndex(((MethodDeclaration) interfaceDeclaration.get(i)).getMethodHeader());
		}
	}

	/**
	 * Load a classHeader.
	 *
	 * @param classHeader the classHeader to load
	 */
	private void loadClassHeader(ClassHeader classHeader)
	{
		quickAccess.addToIndex(classHeader);
		List<MethodHeader> methods = classHeader.getMethodsHeaders();
		for (MethodHeader method : methods)
		{
			quickAccess.addToIndex(method);
		}
	}

	/**
	 * Unload the data of the project.
	 */
	public void unload()
	{
		classes = null;
		interfaces = null;
		methods = null;
		files = null;
		excludedFolders.clear();
	}

	private Hashtable readObjects(File target) throws FileNotFoundException,
		ClassNotFoundException, IOException
	{
		ObjectInputStream objIn = null;
		try
		{
			objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(target)));
			Object object = objIn.readObject();
			return (Hashtable) object;
		}
		finally
		{
			if (objIn != null)
				try
				{
					objIn.close();
				}
				catch (IOException e)
				{
					Log.log(Log.WARNING, this, e);
				}
		}
	}

	/**
	 * Get a valid name for the project. It will check if the file is exists. If it already exists it will add a suffix
	 *
	 * @param name the name
	 * @return a file that doesn't already exists
	 */
	private static File getValidFileName(String name)
	{
		File file = new File(name + ".project.props");
		while (file.exists())
		{
			file = new File(file.getAbsolutePath() + ".project.props");
		}

		return file;
	}

	private void checkProperties() throws InvalidProjectPropertiesException
	{
		if (properties.getProperty("name") == null)
			throw new InvalidProjectPropertiesException("Missing project name");
		if (properties.getProperty("version") == null)
			throw new InvalidProjectPropertiesException("Missing project version");
	}

	public void setRoot(String root)
	{
		properties.setProperty("root", root);
	}

	/**
	 * Returns the project file.
	 *
	 * @return the project file. It's a property file
	 */
	public File getFile()
	{
		return file;
	}

	public String getRoot()
	{
		return properties.getProperty("root");
	}

	/**
	 * This method is called to save the project.
	 */
	public void save()
	{
		long start = System.currentTimeMillis();
		Log.log(Log.DEBUG, this, "Saving the project");
		File directory = classFile.getParentFile();
		if (!directory.exists())
		{
			// todo : do better
			directory.mkdirs();
		}
		StringBuilder buff = new StringBuilder(1000);
		for (String excludedFolder : excludedFolders)
		{
			buff.append(excludedFolder).append('\n');
		}
		properties.setProperty("excluded", buff.toString());
		BufferedOutputStream outStream = null;
		try
		{
			outStream = new BufferedOutputStream(new FileOutputStream(file));
			properties.store(outStream, "");

			writeObjects(classFile, classes);
			writeObjects(interfaceFile, interfaces);
			writeObjects(methodFile, methods);
			writeObjects(fileFile, files);

		}
		catch (FileNotFoundException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, e);
		}
		finally
		{
			if (outStream != null)
			{
				try
				{
					outStream.close();
				}
				catch (IOException e)
				{
					Log.log(Log.ERROR, this, e);
				}
			}
		}
		long end = System.currentTimeMillis();
		Log.log(Log.DEBUG, this, "Project saved in " + (end - start) + "ms");
	}

	/**
	 * Save an object in a file.
	 *
	 * @param target	   the file target
	 * @param serializableFile the object to save
	 * @throws IOException
	 */
	private static void writeObjects(File target, Object serializableFile) throws IOException
	{
		ObjectOutputStream objOut = null;
		try
		{
			objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
			objOut.writeObject(serializableFile);
		}
		finally
		{
			if (objOut != null)
			{
				try
				{
					objOut.close();
				}
				catch (IOException e)
				{
					Log.log(Log.WARNING, Project.class, e);
				}
			}
		}
	}

	/**
	 * Add a class in the project.
	 *
	 * @param classHeader the header of the class
	 */
	public void addClass(ClassHeader classHeader)
	{
		needSave = true;
		if (!classes.containsValue(classHeader))
		{
			insertItem(classes, classHeader);

			List<MethodHeader> methods = classHeader.getMethodsHeaders();
			for (MethodHeader method : methods)
			{
				quickAccess.addToIndex(method);
			}
		}
	}

	public void addInterface(InterfaceDeclaration interfaceDeclaration)
	{
		needSave = true;
		if (!interfaces.containsValue(interfaceDeclaration))
		{
			insertItem(interfaces, interfaceDeclaration);
			for (int i = 0; i < interfaceDeclaration.size(); i++)
			{
				quickAccess.addToIndex(((MethodDeclaration) interfaceDeclaration.get(i)).getMethodHeader());
			}
		}
	}

	/**
	 * Add a method in the project.
	 *
	 * @param methodHeader the header of the method
	 */
	public void addMethod(PHPItem methodHeader)
	{
		needSave = true;
		insertItem(methods, methodHeader);
	}

	/**
	 * Tell if the project needs to be saved.
	 *
	 * @return true if the project needs to be saved
	 */
	public boolean needSave()
	{
		return needSave;
	}

	/**
	 * Delete a project.
	 */
	public void delete()
	{
		Log.log(Log.DEBUG, this, "delete");
		file.delete();
		classFile.delete();
		methodFile.delete();
		dataDirectory.delete();
	}

	/**
	 * Tell if the file is in the path.
	 *
	 * @param filePath the file path
	 * @return a boolean
	 */
	public boolean acceptFile(String filePath)
	{
		String root = getRoot();
		return root != null && filePath.substring(1).startsWith(root.substring(1)) && !isExcluded(filePath);
	}

	/**
	 * Tells if a path is excluded from the project.
	 *
	 * @param filePath the path to be checked
	 * @return true if this path is excluded
	 */
	public boolean isExcluded(String filePath)
	{
		for (String excludedPath : excludedFolders)
		{
			if (filePath.substring(1).startsWith(excludedPath.substring(1)))
				return true;
		}
		return false;
	}

	public Object[] getExcludedFolders()
	{
		return excludedFolders.toArray();
	}

	/**
	 * Reparse all files from the project.
	 */
	public void rebuildProject()
	{
		String root = getRoot();
		if (root == null || root.length() == 0)
		{
			Log.log(Log.MESSAGE, this, "No root file for that project");
		}
		else
		{
			Log.log(Log.MESSAGE, this, "Rebuilding project");
			classes.clear();
			interfaces.clear();
			methods.clear();
			files.clear();
			quickAccess = new QuickAccessItemFinder();
			Rebuilder run = new Rebuilder(this, root);
			ThreadUtilities.runInBackground(run);
		}
	}

	/**
	 * Returns the name of the project.
	 *
	 * @return the name of the project
	 */
	public String getName()
	{
		return properties.getProperty("name");
	}

	public String toString()
	{
		return getName();
	}

	/**
	 * Insert an item in a map.
	 *
	 * @param targetMap the map inside wich we want to insert the item. it's methods or classes
	 * @param phpItem   the item to insert
	 */
	private void insertItem(Map<String, Object> targetMap, PHPItem phpItem)
	{
		quickAccess.addToIndex(phpItem);
		Object item = targetMap.get(phpItem.getNameLowerCase());
		if (item == null)
		{
			targetMap.put(phpItem.getNameLowerCase(), phpItem);
		}
		else if (item instanceof List)
		{
			((Collection<PHPItem>) item).add(phpItem);
		}
		else
		{
			// The item is a PHPItem
			Collection<PHPItem> list = new ArrayList<PHPItem>();
			list.add((PHPItem) item);
			list.add(phpItem);
			targetMap.put(phpItem.getNameLowerCase(), list);
		}

		String path = phpItem.getPath();

		List<PHPItem> fileList = files.get(path);
		if (fileList == null)
		{
			fileList = new ArrayList<PHPItem>();
			files.put(path, fileList);
		}
		fileList.add(phpItem);
	}

	/**
	 * Remove the elements of a file. (Used before updating a file)
	 *
	 * @param path the filepath
	 */
	public void clearSourceFile(String path)
	{
		List<PHPItem> fileTable = files.get(path);
		if (fileTable != null)
		{
			fileTable.clear();
		}
		clearSourceFileFromMap(path, classes);
		clearSourceFileFromMap(path, methods);
		clearSourceFileFromMap(path, interfaces);
		quickAccess.purgePath(path);
	}

	private static void clearSourceFileFromMap(String path, Hashtable<String, Object> table)
	{
		Enumeration<String> keys = table.keys();
		while (keys.hasMoreElements())
		{
			String key = keys.nextElement();
			Object item = table.get(key);
			if (item instanceof PHPItem)
			{
				PHPItem phpItem = (PHPItem) item;
				if (path.equals(phpItem.getPath()))
				{
					table.remove(key);
				}
			}
			else
			{
				//it should be a list
				List<PHPItem> list = (List<PHPItem>) item;
				ListIterator<PHPItem> iterator = list.listIterator();
				while (iterator.hasNext())
				{
					PHPItem phpItem = iterator.next();
					if (path.equals(phpItem.getPath()))
					{
						iterator.remove();
					}
				}
				if (list.isEmpty())
				{
					table.remove(key);
				}
				else if (list.size() == 1)
				{
					table.put(key, list.get(0));
				}
			}
		}
	}

	public Map<String, Object> getClasses()
	{
		return classes;
	}

	public Map<String, Object> getMethods()
	{
		return methods;
	}

	/**
	 * Return a classHeader by it's name.
	 *
	 * @param name the name of the class
	 * @return a {@link ClassHeader} or null
	 */
	public ClassHeader getClass(String name)
	{
		Object o = classes.get(name.toLowerCase());
		if (o instanceof ClassHeader)
			return (ClassHeader) o;
		if (o == null)
			return null;
		return ((List<ClassHeader>) o).get(0);
	}

	/**
	 * Return an interface by it's name.
	 *
	 * @param name the name of the interface
	 * @return a {@link InterfaceDeclaration} or null
	 */
	public InterfaceDeclaration getInterface(String name)
	{
		Object o = interfaces.get(name.toLowerCase());
		if (o instanceof InterfaceDeclaration)
			return (InterfaceDeclaration) o;
		if (o == null)
			return null;
		return ((List<InterfaceDeclaration>) o).get(0);
	}

	/**
	 * Returns the version of the file project.
	 *
	 * @return the project version
	 */
	public String getVersion()
	{
		return properties.getProperty("version");
	}

	/**
	 * Returns the number of classes in the project.
	 *
	 * @return how many classes are in the project
	 */
	public int getClassCount()
	{
		return classes.size();
	}

	/**
	 * Returns the number of methods in the project.
	 *
	 * @return how many methods are in the project
	 */
	public int getMethodCount()
	{
		return methods.size();
	}

	/**
	 * Returns the number of methods in the project.
	 *
	 * @return how many methods are in the project
	 */
	public int getFileCount()
	{
		return files.size();
	}

	public QuickAccessItemFinder getQuickAccess()
	{
		return quickAccess;
	}

	/**
	 * Add an excluded folder.
	 *
	 * @param excludedFolder the path to the excluded folder
	 * @return true if it wasn't already in the list
	 */
	public boolean addExcludedFolder(String excludedFolder)
	{
		if (excludedFolders.contains(excludedFolder))
			return false;

		Log.log(Log.DEBUG, this, "Excluding folder " + excludedFolder);
		checkDatas(classes);
		checkDatas(interfaces);
		checkDatas(methods);

		return excludedFolders.add(excludedFolder);
	}

	private void checkDatas(Map<String, Object> map)
	{
		Collection<Object> values = map.values();
		Iterator<Object> iterator = values.iterator();
		while (iterator.hasNext())
		{
			Object o = iterator.next();
			if (o instanceof PHPItem)
			{
				PHPItem item = (PHPItem) o;
				if (isExcluded(item.getPath()))
				{
					Log.log(Log.DEBUG, this, item.getName() + " excluded");
					iterator.remove();
				}
			}
			else
			{
				List<PHPItem> l = (List<PHPItem>) o;
				Iterator<PHPItem> iterator1 = l.iterator();
				while (iterator1.hasNext())
				{
					PHPItem item = iterator1.next();
					if (isExcluded(item.getPath()))
					{
						Log.log(Log.DEBUG, this, item.getName() + " excluded");
						iterator1.remove();
					}
				}
				if (l.isEmpty())
					iterator.remove();
			}

		}
	}

	public boolean removeExcludedFolder(String excludedFolder)
	{
		return excludedFolders.remove(excludedFolder);
	}

	/**
	 * The rebuilder will reparse all php files of the project.
	 *
	 * @author Matthieu Casanova
	 */
	private class Rebuilder extends Task
	{
		private final String path;

		private long current;

		private int parsedFileCount;

		private final Project project;

		Rebuilder(Project project, String path)
		{
			this.path = path;
			this.project = project;
			setCancellable(true);
		}

		public void _run()
		{
			long start = System.currentTimeMillis();
			VFS vfs = VFSManager.getVFSForPath(path);
			setStatus("Listing files");
			Object vfsSession = vfs.createVFSSession(path, null);
			Mode mode = jEdit.getMode("php");
			String glob = "*";
			if (mode != null)
			{
				glob = (String) mode.getProperty("filenameGlob");
				if (glob == null || glob.length() == 0)
				{
					glob = "*";
				}
			}
			try
			{
				String[] files = vfs._listDirectory(vfsSession, path, glob, true, null);
				setStatus("Parsing");
				setMaximum(files.length);
				PHPSideKickParser phpParser = new PHPSideKickParser("rebuilder");
				for (String file : files)
				{
					if (!isExcluded(file))
					{
						parseFile(phpParser, VFSManager.getVFSForPath(file), file, vfsSession);
					}
					setValue((int) ++current);
				}
			}
			catch (IOException e)
			{
				Log.log(Log.WARNING, this, e);
			}
			finally
			{
				try
				{
					vfs._endVFSSession(vfsSession, jEdit.getActiveView());
				}
				catch (IOException e)
				{
					Log.log(Log.ERROR, this, e);
				}
			}
			long end = System.currentTimeMillis();
			Log.log(Log.MESSAGE, this, "Project rebuild in " + (end - start) + "ms, " + parsedFileCount + " files parsed");
			EditBus.send(new PHPProjectChangedMessage(this, project, PHPProjectChangedMessage.SELECTED));
		}

		/**
		 * Parse a file.
		 *
		 * @param phpParser  the php parser.
		 * @param f	  the file to parse. If it is a directory, it will be parsed recursively
		 * @param vfsSession
		 */
		private void parseFile(PHPSideKickParser phpParser, VFS f, String path, Object vfsSession)
		{
			Reader reader = null;
			try
			{
				try
				{
					InputStream inputStream = f._createInputStream(vfsSession, path, false, null);
					reader = new BufferedReader(new InputStreamReader(inputStream));
					parsedFileCount++;
					try
					{
						phpParser.parse(path, reader);
					}
					catch (Exception e)
					{
						Log.log(Log.ERROR, this, "Error while parsing file " + path);
						Log.log(Log.ERROR, this, e);
					}
				}
				catch (IOException e)
				{
					Log.log(Log.WARNING, this, e.getMessage());
				}
			}
			finally
			{
				IOUtilities.closeQuietly(reader);
			}
		}
	}
}
