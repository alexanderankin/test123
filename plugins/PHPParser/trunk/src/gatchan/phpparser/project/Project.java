package gatchan.phpparser.project;

import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.project.itemfinder.QuickAccessItemFinder;
import gatchan.phpparser.sidekick.PHPSideKickParser;
import net.sourceforge.phpdt.internal.compiler.ast.ClassHeader;
import net.sourceforge.phpdt.internal.compiler.ast.MethodHeader;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.WorkRequest;

import java.io.*;
import java.util.*;

/**
 * A Project.
 *
 * @author Matthieu Casanova
 */
public final class Project {
  /** The file where the project will be saved. */
  private final File file;

  /** Tells that the project needs to be saved. */
  private boolean needSave;

  /** The directory containing the datas of the project. */
  private File dataDirectory;

  /** The file where the classes will be serialized. */
  private File classFile;

  /** The file where the methods will be serialized. */
  private File methodFile;
  private File fileFile;

  /** The properties */
  private final Properties properties = new Properties();

  /** This table will contains class names (lowercase) as key and {@link ClassHeader} as values. */
  private Hashtable classes;

  /** This table will contains class names (lowercase) as key and {@link MethodHeader} as values. */
  private Hashtable methods;

  /** This table will contains as key the file path, and as value a {@link List} */
  private Hashtable files;
  private QuickAccessItemFinder quickAccess;

  public Project(String name, String version) {
    properties.setProperty("name", name);
    properties.setProperty("version", version);
    reset();
    file = getValidFileName(ProjectManager.projectDirectory + File.separator + name);
    init();
    needSave = true;
  }

  public Project(File file) throws FileNotFoundException, InvalidProjectPropertiesException {
    this.file = file;
    quickAccess = new QuickAccessItemFinder();
    FileInputStream inStream = null;
    try {
      inStream = new FileInputStream(file);
      properties.load(inStream);
      init();
    } catch (FileNotFoundException e) {
      throw e;
    } catch (IOException e) {
      Log.log(Log.ERROR, this, e);
    } finally {
      try {
        if (inStream != null) {
          inStream.close();
        }
      } catch (IOException e) {
        Log.log(Log.ERROR, this, e);
      }
    }
    checkProperties();
  }

  private void reset() {
    classes = new Hashtable();
    methods = new Hashtable();
    files = new Hashtable();
    quickAccess = new QuickAccessItemFinder();
  }

  private void init() {
    dataDirectory = new File(file.getParent(), file.getName().substring(0, file.getName().length() - 14) + "_datas");
    //todo : check the return of mkdir
    dataDirectory.mkdir();
    classFile = new File(dataDirectory, "classes.ser");
    methodFile = new File(dataDirectory, "methods.ser");
    fileFile = new File(dataDirectory, "files.ser");
  }

  /** Load the project. */
  public void load() {
    final long start = System.currentTimeMillis();

    final long end;
    try {
      classes = readObjects(classFile);
      Collection collection = classes.values();
      for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
        final ClassHeader classHeader = (ClassHeader) iterator.next();
        quickAccess.addToIndex(classHeader);
        final List methods = classHeader.getMethodsHeaders();
        for (int i = 0; i < methods.size(); i++) {
          quickAccess.addToIndex((MethodHeader) methods.get(i));
        }
      }

      methods = readObjects(methodFile);
      collection = methods.values();
      for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
        final MethodHeader methodHeader = (MethodHeader) iterator.next();
        quickAccess.addToIndex(methodHeader);
      }

      files = readObjects(fileFile);
    } catch (FileNotFoundException e) {
      Log.log(Log.ERROR, this, e.getMessage());
      GUIUtilities.error(jEdit.getActiveView(),
                         "gatchan-phpparser.errordialog.unabletoreadproject",
                         new String[]{e.getMessage()});
      reset();
    } catch (InvalidClassException e) {
      Log.log(Log.WARNING,
              this,
              "A class is invalid probably because the project used an old plugin " + e.getMessage());
      GUIUtilities.error(jEdit.getActiveView(), "gatchan-phpparser.errordialog.invalidprojectformat", null);
      reset();
    } catch (ClassNotFoundException e) {
      //should never happen
      Log.log(Log.ERROR, this, e);
      GUIUtilities.error(jEdit.getActiveView(), "gatchan-phpparser.errordialog.unexpectederror", null);
      reset();
    } catch (IOException e) {
      Log.log(Log.ERROR, this, e);
      GUIUtilities.error(jEdit.getActiveView(),
                         "gatchan-phpparser.errordialog.unabletoreadproject",
                         new String[]{e.getMessage()});
      reset();
    }
    end = System.currentTimeMillis();
    Log.log(Log.DEBUG, this, "Project loaded in " + (end - start) + "ms");
  }

  private Hashtable readObjects(File target) throws FileNotFoundException,
                                                    ClassNotFoundException, IOException {
    ObjectInputStream objIn = null;
    try {
      objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(target)));
      final Object object = objIn.readObject();
      return (Hashtable) object;
    } finally {
      if (objIn != null)
        try {
          objIn.close();
        } catch (IOException e) {
          Log.log(Log.WARNING, this, e);
        }
    }
  }

  private static File getValidFileName(String name) {
    File file = new File(name + ".project.props");
    while (file.exists()) {
      file = new File(file.getAbsolutePath() + ".project.props");
    }

    return file;
  }

  private void checkProperties() throws InvalidProjectPropertiesException {
    if (properties.getProperty("name") == null) throw new InvalidProjectPropertiesException("Missing project name");
    if (properties.getProperty("version") == null)
      throw new InvalidProjectPropertiesException("Missing project version");
  }

  public void setRoot(String root) {
    properties.setProperty("root", root);
  }

  public String getRoot() {
    return properties.getProperty("root");
  }

  /** This method is called to save the project. */
  public void save() {
    final long start = System.currentTimeMillis();
    Log.log(Log.DEBUG, this, "Saving the project");
    final File directory = classFile.getParentFile();
    if (!directory.exists()) {
      // todo : do better
      directory.mkdirs();
    }

    BufferedOutputStream outStream = null;
    try {
      outStream = new BufferedOutputStream(new FileOutputStream(file));
      properties.store(outStream, "");

      writeObjects(classFile, classes);
      writeObjects(methodFile, methods);
      writeObjects(fileFile, files);

    } catch (FileNotFoundException e) {
      Log.log(Log.ERROR, this, e);
    } catch (IOException e) {
      Log.log(Log.ERROR, this, e);
    } finally {
      if (outStream != null) {
        try {
          outStream.close();
        } catch (IOException e) {
          Log.log(Log.ERROR, this, e);
        }
      }
    }
    final long end = System.currentTimeMillis();
    Log.log(Log.DEBUG, this, "Project saved in " + (end - start) + "ms");
  }

  /**
   * Save an object in a file.
   *
   * @param target           the file target
   * @param serializableFile the object to save
   * @throws IOException
   */
  private static void writeObjects(File target, Object serializableFile) throws IOException {
    ObjectOutputStream objOut = null;
    try {
      objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
      objOut.writeObject(serializableFile);
    } finally {
      if (objOut != null) {
        try {
          objOut.close();
        } catch (IOException e) {
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
  public void addClass(ClassHeader classHeader) {
    needSave = true;
    if (!classes.containsValue(classHeader)) {
      insertItem(classes, classHeader);

      final List methods = classHeader.getMethodsHeaders();
      for (int i = 0; i < methods.size(); i++) {
        quickAccess.addToIndex((PHPItem) methods.get(i));
      }
    }
  }

  /**
   * Add a method in the project.
   *
   * @param methodHeader the header of the method
   */
  public void addMethod(MethodHeader methodHeader) {
    needSave = true;
    if (!methods.containsValue(methodHeader)) {
      insertItem(methods, methodHeader);
    }
  }

  /**
   * Tell if the project needs to be saved.
   *
   * @return true if the project needs to be saved
   */
  public boolean needSave() {
    return needSave;
  }

  /** Delete a project. */
  public void delete() {
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
  public boolean acceptFile(String filePath) {
    final String root = getRoot();
    return root != null && filePath.startsWith(root);
  }

  /** Reparse all files from the project. */
  public void rebuildProject() {
    final String root = getRoot();
    if (root == null || root.length() == 0) {
      Log.log(Log.MESSAGE, this, "No root file for that project");
    } else {
      Log.log(Log.MESSAGE, this, "Rebuilding project");
      classes.clear();
      methods.clear();
      files.clear();
      quickAccess = new QuickAccessItemFinder();
      VFSManager.runInWorkThread(new Rebuilder(this, root));
    }
  }

  /**
   * Returns the name of the project.
   *
   * @return the name of the project
   */
  public String getName() {
    return properties.getProperty("name");
  }

  public String toString() {
    return getName();
  }

  /**
   * Insert an item in a map.
   *
   * @param targetMap the map inside wich we want to insert the item. it's methods or classes
   * @param phpItem   the item to insert
   */
  private void insertItem(Map targetMap, PHPItem phpItem) {
    quickAccess.addToIndex(phpItem);
    targetMap.put(phpItem.getName().toLowerCase(), phpItem);
    final String path = phpItem.getPath();

    List fileList = (List) files.get(path);
    if (fileList == null) {
      fileList = new ArrayList();
      files.put(path, fileList);
    }
    fileList.add(phpItem);
  }

  /**
   * Remove the elements of a file. (Used before updating a file)
   *
   * @param path the filepath
   */
  public void clearSourceFile(String path) {
    final List fileTable = (List) files.get(path);
    if (fileTable != null) {
      fileTable.clear();
    }
    clearSourceFileFromMap(path, classes);
    clearSourceFileFromMap(path, methods);
    quickAccess.purgePath(path);
  }

  private static void clearSourceFileFromMap(String path, Hashtable table) {
    final Enumeration keys = table.keys();
    while (keys.hasMoreElements()) {
      final Object key = keys.nextElement();
      final PHPItem phpItem = (PHPItem) table.get(key);
      if (path.equals(phpItem.getPath())) {
        table.remove(key);
      }
    }
  }

  public Map getClasses() {
    return classes;
  }

  public Map getMethods() {
    return methods;
  }

  /**
   * Return a classHeader by it's name.
   *
   * @param name the name of the class
   * @return a {@link ClassHeader} or null
   */
  public ClassHeader getClass(String name) {
    return (ClassHeader) classes.get(name.toLowerCase());
  }

  /**
   * Returns the version of the file project.
   *
   * @return the project version
   */
  public String getVersion() {
    return properties.getProperty("version");
  }

  /**
   * Returns the number of classes in the project.
   *
   * @return how many classes are in the project
   */
  public int getClassCount() {
    return classes.size();
  }

  /**
   * Returns the number of methods in the project.
   *
   * @return how many methods are in the project
   */
  public int getMethodCount() {
    return methods.size();
  }

  /**
   * Returns the number of methods in the project.
   *
   * @return how many methods are in the project
   */
  public int getFileCount() {
    return files.size();
  }

  public QuickAccessItemFinder getQuickAccess() {
    return quickAccess;
  }

  /**
   * The rebuilder will reparse all php files of the project.
   *
   * @author Matthieu Casanova
   */
  private static final class Rebuilder extends WorkRequest {
    private final String path;

    private int current;

    private int parsedFileCount;

    private static final Mode mode = jEdit.getMode("php");

    private final Project project;

    private Rebuilder(Project project, String path) {
      this.path = path;
      this.project = project;
      setAbortable(true);
    }

    public void run() {
      final long start = System.currentTimeMillis();
      final VFS vfs = VFSManager.getVFSForPath(path);
      try {
        setStatus("Listing files");
        final Object vfsSession = vfs.createVFSSession(path, null);
        final String[] files = vfs._listDirectory(vfsSession, path, "*", true, null);
        setStatus("Parsing");
        setProgressMaximum(files.length);
        final PHPSideKickParser phpParser = new PHPSideKickParser("rebuilder");
        for (int i = 0; i < files.length; i++) {
          final String file = files[i];
          if (mode.accept(file, "")) {
            parseFile(phpParser, VFSManager.getVFSForPath(file), file, vfsSession);
          }
          setProgressValue(++current);
        }
      } catch (IOException e) {
        Log.log(Log.WARNING, this, e);
      }
      final long end = System.currentTimeMillis();
      Log.log(Log.MESSAGE, this, "Project rebuild in " + (end - start) + "ms, " + parsedFileCount + " files parsed");
      EditBus.send(new PHPProjectChangedMessage(this, project, PHPProjectChangedMessage.SELECTED));
    }

    /**
     * Parse a file.
     *
     * @param phpParser  the php parser.
     * @param f          the file to parse. If it is a directory, it will be parsed recursively
     * @param vfsSession
     */
    private void parseFile(PHPSideKickParser phpParser, VFS f, String path, Object vfsSession) {
      Reader reader = null;
      try {
        try {
          final InputStream inputStream = f._createInputStream(vfsSession, path, false, null);
          reader = new BufferedReader(new InputStreamReader(inputStream));
          parsedFileCount++;
          try {
            phpParser.parse(path, reader);
          } catch (Exception e) {
            Log.log(Log.ERROR, this, "Error while parsing file " + path);
            Log.log(Log.ERROR, this, e);
          }
        } catch (IOException e) {
          Log.log(Log.WARNING, this, e.getMessage());
        }
      } finally {
        try {
          if (reader != null) reader.close();
        } catch (IOException e) {
          Log.log(Log.WARNING, this, "Unable to close reader " + e.getMessage());
        }
      }
    }
  }
}
