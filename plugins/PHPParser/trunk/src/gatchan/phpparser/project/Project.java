package gatchan.phpparser.project;

import gatchan.phpparser.project.itemfinder.QuickAccessItemFinder;
import gatchan.phpparser.sidekick.PHPSideKickParser;
import net.sourceforge.phpdt.internal.compiler.ast.ClassHeader;
import net.sourceforge.phpdt.internal.compiler.ast.MethodHeader;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.WorkRequest;

import java.io.*;
import java.util.*;

/**
 * A Project.
 *
 * @author Matthieu Casanova
 */
public final class Project extends AbstractProject {

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

  public Project(String name, String version) {
    super(name, version);
    file = getValidFileName(ProjectManager.projectDirectory + File.separator + name);
    init();
    needSave = true;
  }

  public Project(File file) throws FileNotFoundException, InvalidProjectPropertiesException {
    this.file = file;
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

  /** Load the project. */
  public void load() {
    final long start = System.currentTimeMillis();
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
    final long end = System.currentTimeMillis();
    Log.log(Log.DEBUG, this, "Project loaded in " + (end - start) + "ms");
  }

  private void init() {
    dataDirectory = new File(file.getParent(), file.getName().substring(0, file.getName().length() - 14) + "_datas");
    //todo : check the return of mkdir
    dataDirectory.mkdir();
    classFile = new File(dataDirectory, "classes.ser");
    methodFile = new File(dataDirectory, "methods.ser");
    fileFile = new File(dataDirectory, "files.ser");
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

  public void setRoot(String root) { properties.setProperty("root", root); }

  public String getRoot() { return properties.getProperty("root"); }

  /** This method is called to save the project. */
  public void save() {
    final long start = System.currentTimeMillis();
    Log.log(Log.DEBUG, this, "Saving the project");
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
   *
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

  private Map readObjects(File target) {
    ObjectInputStream objIn = null;
    try {
      objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(target)));
      final Object object = objIn.readObject();
      return (Map) object;
    } catch (FileNotFoundException e) {
      Log.log(Log.ERROR, this, "The file " + target.getAbsolutePath() + " was not found");
    } catch (InvalidClassException e) {
      Log.log(Log.WARNING,
              this,
              "A class is invalid probably because the project used an old plugin " + e.getMessage());
    } catch (ClassNotFoundException e) {
      //should never happen
      Log.log(Log.ERROR, this, e);
    } catch (IOException e) {
      Log.log(Log.ERROR, this, e);
    } finally {
      if (objIn != null)
        try {
          objIn.close();
        } catch (IOException e) {
          Log.log(Log.WARNING, this, e);
        }
    }
    return new HashMap();
  }

  /**
   * Add a class in the project.
   *
   * @param classHeader the header of the class
   */
  public void addClass(ClassHeader classHeader) {
    needSave = true;
    super.addClass(classHeader);
  }

  /**
   * Add a method in the project.
   *
   * @param methodHeader the header of the method
   */
  public void addMethod(MethodHeader methodHeader) {
    needSave = true;
    super.addMethod(methodHeader);
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
   *
   * @return a boolean
   */
  public boolean acceptFile(String filePath) {
    final String root = getRoot();
    return root != null && filePath.startsWith(root);
  }

  /** Reparse all files from the project. */
  public void rebuildProject() {
    Log.log(Log.MESSAGE, this, "Rebuilding project");
    classes.clear();
    methods.clear();
    files.clear();
    // todo : maybe clear it ?
    quickAccess = new QuickAccessItemFinder();
    final String root = getRoot();
    if (root == null) {
      Log.log(Log.MESSAGE, this, "No root file for that project");
    } else {
      final File path = new File(root);

      VFSManager.runInWorkThread(new Rebuilder(this, path));
    }
  }

  /**
   * The rebuilder will reparse all php files of the project.
   *
   * @author Matthieu Casanova
   */
  private static final class Rebuilder extends WorkRequest {

    private final File path;

    private int max;
    private int current;

    private int parsedFileCount;

    private static final Mode mode = jEdit.getMode("php");

    private final Project project;

    private Rebuilder(Project project, File path) {
      this.path = path;
      this.project = project;
      setAbortable(true);
    }

    public void run() {
      final long start = System.currentTimeMillis();
      parseFile(new PHPSideKickParser("rebuilder"), path);
      final long end = System.currentTimeMillis();
      Log.log(Log.MESSAGE, this, "Project rebuild in " + (end - start) + "ms, " + parsedFileCount + " files parsed");
      EditBus.send(new PHPProjectChangedMessage(this, project));

    }

    /**
     * Parse a file.
     *
     * @param phpParser the php parser.
     * @param f         the file to parse. If it is a directory, it will be parsed recursively
     */
    private void parseFile(PHPSideKickParser phpParser, File f) {
      if (f.isDirectory()) {
        final File[] files = f.listFiles();
        max += files.length;
        setProgressMaximum(max);
        for (int i = 0; i < files.length; i++) {
          parseFile(phpParser, files[i]);
        }
      } else {
        BufferedReader reader = null;
        try {
          Log.log(Log.DEBUG, this, "Parsing file " + f.getAbsolutePath());
          if (mode.accept(f.getAbsolutePath(), "")) {
            reader = new BufferedReader(new FileReader(f));
            parsedFileCount++;
            phpParser.parse(f.getAbsolutePath(), reader);
          }
          setProgressValue(++current);
        } catch (FileNotFoundException e) {
          Log.log(Log.WARNING, this, e.getMessage());
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
}
