package gatchan.phpparser;

import errorlist.ErrorList;
import errorlist.ErrorSource;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.ParseException;
import gnu.regexp.RE;
import gnu.regexp.REException;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;

/**
 * The PHP Parser plugin.
 * The parser was taken from phpeclipse <a href="http://www.phpeclipse.de" target="_blank">http://www.phpeclipse.de</a>
 *
 * @author Matthieu Casanova
 */
public final class PHPParserPlugin extends EBPlugin {

  private ErrorList errorList;

  private PHPErrorSource errorSource;

  /**
   * The instance of the plugin for a static access.
   */
  private static PHPParserPlugin instance;

  private boolean parseOnLoad;
  private boolean parseOnSave;

  /**
   * The regexp defining files that need to be parsed.
   */
  private RE filesToParseRE;


  /**
   * Initialize the plugin.
   * When starting this plugin will add an Highlighter on each text area
   */
  public void start() {
    instance = this;
    errorSource = new PHPErrorSource();
    ErrorSource.registerErrorSource(errorSource);
    propertiesChanged();
  }


  /**
   * uninitialize the plugin.
   * we will remove the Highlighter on each text area
   */
  public void stop() {
    ErrorSource.unregisterErrorSource(errorSource);
    instance = null;
    errorSource = null;
    errorList = null;
    EditBus.removeFromBus(this);
  }

  /**
   * Get the instance of the parser. This method is called in the actions.xml
   *
   * @return the instance of the parser
   */
  public static PHPParserPlugin getInstance() {
    return instance;
  }

  public void handleMessage(final EBMessage message) {
    if (message instanceof BufferUpdate) {
      handleBufferUpdateMessage((BufferUpdate) message);
    } else if (message instanceof PropertiesChanged) {
      propertiesChanged();
      
    }
  }

  private void handleBufferUpdateMessage(final BufferUpdate message) {
    final Object what = message.getWhat();
    final Buffer buffer = message.getBuffer();
    final String path = buffer.getPath();
    if (parseOnLoad && what == BufferUpdate.LOADED) {
      parseIfPaternMatch(path, buffer, message.getView());
    } else if (parseOnSave && what == BufferUpdate.SAVING) {
      parseIfPaternMatch(path, buffer, message.getView());
    } else if (what == BufferUpdate.CLOSED) {
      Log.log(Log.DEBUG,PHPParserPlugin.class,"Buffer closed : " + path);
      errorSource.removeFileErrors(path);
    }
  }

  /**
   * parse a buffer if the path match the patern of files to parse
   *
   * @param path   the path of the file to parse
   * @param buffer the buffer
   * @param view   the jEdit's view
   */
  private void parseIfPaternMatch(final String path, final Buffer buffer, final View view) {
    if (filesToParseRE.isMatch(path)) {
      Log.log(Log.DEBUG,PHPParserPlugin.class,"Parsing launched by load or save on : " + path);
      final String text = buffer.getText(0, buffer.getLength());
      parse(path, text, view);
    }
  }

  /**
   * Parse a php string.
   *
   * @param path the path of the file
   * @param text the text to parse
   * @param view the jEdit's view
   */
  private void parse(final String path, final String text, final View view) {
    try {
      Log.log(Log.DEBUG,PHPParserPlugin.class,"Parsing "+path);
      final PHPParser parser = new PHPParser();
      parser.setPath(path);
      errorSource.removeFileErrors(path);
      parser.addParserListener(errorSource);
      parser.parseInfo(null, text);
      if (errorSource.getErrorCount() != 0 && errorList == null) {
        errorList = new ErrorList(view);
      }
    } catch (ParseException e) {
      Log.log(Log.ERROR, this, e);
      errorSource.addError(ErrorSource.ERROR,
                           path,
                           e.currentToken.beginLine - 1,
                           e.currentToken.beginColumn,
                           e.currentToken.endColumn,
                           "Unhandled error please report the bug (with the trace in the activity log");
    }
  }

  /**
   * This is the entry point for the actions.xml that will parse the buffer.
   *
   * @param view   the view
   * @param buffer the buffer
   */
  public void parseBuffer(final View view, final Buffer buffer) {
    final String path = buffer.getPath();
    Log.log(Log.DEBUG,PHPParserPlugin.class,"Parsing launched by user request : " + path);
    final String text = buffer.getText(0, buffer.getLength());
    parse(path, text, view);
  }

  /**
   * This method is called in {@link #start()} and when the properties change,
   * it will reinitialize the options.
   */
  private void propertiesChanged() {
    parseOnLoad = jEdit.getBooleanProperty("gatchan.phpparser.parseOnLoad");
    parseOnSave = jEdit.getBooleanProperty("gatchan.phpparser.parseOnSave");
    final String filesToParseGlob = jEdit.getProperty("gatchan.phpparser.files.glob");
    try {
      filesToParseRE = new RE(MiscUtilities.globToRE(filesToParseGlob));
    } catch (REException e) {
      Log.log(Log.ERROR, PHPParserPlugin.this, "Unable to read this glob " + filesToParseGlob);
      try {
        filesToParseRE = new RE("*.php");
      } catch (REException e1) {
        Log.log(Log.ERROR, PHPParserPlugin.this, "Unable to read this glob " + filesToParseGlob);
      }
    }

    //Remove the errors from the files that aren't accepted by the glob
    final Buffer[] buffers = jEdit.getBuffers();
    for (int i = 0; i < buffers.length; i++) {
      final String path = buffers[i].getPath();
      if (!filesToParseRE.isMatch(path)) {
        Log.log(Log.DEBUG, PHPParserPlugin.this, "Removing errors from " + path);
        errorSource.removeFileErrors(path);
      }
    }
  }
}
